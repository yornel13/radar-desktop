package gui;

import com.jfoenix.controls.*;
import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.event.UIEventType;
import com.lynden.gmapsfx.javascript.object.*;
import gui.async.PrintReportPointsTask;
import io.datafx.controller.ViewController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import model.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import netscape.javascript.JSObject;
import org.joda.time.DateTime;
import service.RadarService;
import util.Const;
import util.HibernateSessionFactory;
import util.RadarDate;
import util.RadarFilters;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@ViewController("view/control.fxml")
public class ControlController  extends BaseController implements MapComponentInitializedListener,
        EventHandler<MouseEvent>,PrintReportPointsTask.PrintTask {

    /*************USERS****************/
    @FXML
    private JFXListView<HBox> userListView;

    private ObservableList<HBox> userData;

    private List<User> users;

    /*************WATCHES****************/
    @FXML
    private JFXTextField watchFilterField;
    @FXML
    private JFXDrawer watchDrawer;
    @FXML
    private JFXListView<HBox> watchListView;
    @FXML
    private VBox watchDrawerBox;
    @FXML
    private HBox watchHeadHBox;

    private ObservableList<HBox> watchData;

    private List<Position> positionsUser;

    private Label watchNameLabel;

    private Label watchDniLabel;

    /*************MARKERS****************/
    @FXML
    private JFXTextField markerFilterField;
    @FXML
    private JFXDrawer markerDrawer;
    @FXML
    private HBox markerHeadHBox;
    @FXML
    private VBox markerDrawerBox;

    private ObservableList<HBox> markerData;

    private List<Position> positionWatch;

    private List<Marker> markers;

    private List<MarkerOptions> markersOptions;

    private Label markerTimeLabel;

    /*************CONTROL MARKERS****************/
    @FXML
    private JFXListView<Label> controlListView;

    private ObservableList<Label> controlPositionsData;

    private List<ControlPosition> controlPositions;

    @FXML
    private JFXButton buttonChangeListView;

    /*************MAP****************/
    @FXML
    private GoogleMapView mapView;

    private GoogleMap map;

    private List<ControlPosition> controlList;

    /*************OTHERS****************/

    @FXML
    private JFXButton searchButton;

    @FXML
    private JFXDatePicker fromPicker;

    @FXML
    private JFXDatePicker toPicker;

    @FXML
    private JFXButton printReport;

    @FXML
    private Pane paneDate;

    @FXML
    private Pane paneDuration;

    @FXML
    private JFXButton buttonDate;

    @FXML
    private JFXButton buttonDuration;

    @FXML
    private TextField filterMin;

    @FXML
    private TextField filterMax;

    private boolean drawerWatchFirstShow = true;
    private boolean drawerMarkerFirstShow = true;

    public boolean isMapReady = false;

    private boolean isDateReady = false;
    private boolean isDurationReady = false;
    private Date from;
    private Date to;
    private int min;
    private int max;

    private ControlPosition selectedControl;

    public void updateMap(ActionEvent event) {
        mapView.addMapInializedListener(this);
    }

    @PostConstruct
    public void init() throws FileNotFoundException {

        setTitleToCompany("Control de Guardias (Por ubicacion)");
        setBackButtonImageBlack();

        mapView.addMapInializedListener(this);

        controlList = service.getAllControlActive();

        users = service.getAllUserActive();

        if(users == null) {
            System.err.println("No users");
            users = new ArrayList<>();
        }

        try {
            loadListView();
            setDrawer();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        showWatchesDetail();

        createDateFilter();

        ImageView reportImage = new ImageView(new Image(getClass()
                .getResource("img/printer.png").toExternalForm()));

        printReport.setOnAction(event ->  printReport());
        printReport.setGraphic(reportImage);

        buttonDate.setOnAction(event -> {
            if (paneDate.isVisible()) {
                toPicker.getEditor().clear();
                toPicker.setValue(null);
                fromPicker.getEditor().clear();
                fromPicker.setValue(null);
                paneDate.setVisible(false);
                setPanesSize();
            } else {
                paneDate.setVisible(true);
                setPanesSize();
            }
        });
        buttonDuration.setOnAction(event -> {
            if (paneDuration.isVisible()) {
                filterMax.clear();
                filterMin.clear();
                paneDuration.setVisible(false);
                setPanesSize();
            } else {
                paneDuration.setVisible(true);
                setPanesSize();
            }
        });

        filterMin.addEventFilter(KeyEvent.KEY_TYPED, RadarFilters.numberFilter());
        filterMax.addEventFilter(KeyEvent.KEY_TYPED, RadarFilters.numberFilter());

        fromPicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            isDateReady = false;
        });
        toPicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            isDateReady = false;
        });
        filterMin.textProperty().addListener((observable, oldValue, newValue) -> {
            isDurationReady = false;
        });
        filterMax.textProperty().addListener((observable, oldValue, newValue) -> {
            isDurationReady = false;
        });
    }

    private void setPanesSize() {
        if (paneDate.isVisible() && paneDuration.isVisible()) {
            paneDate.setPrefHeight(30);
            paneDuration.setPrefHeight(30);
            controlListView.setPrefHeight(510-30-30);
            searchButton.setVisible(true);
        } else if (paneDate.isVisible()) {
            paneDate.setPrefHeight(30);
            paneDuration.setPrefHeight(0);
            controlListView.setPrefHeight(510-30);
            searchButton.setVisible(true);
        } else if (paneDuration.isVisible()) {
            paneDuration.setPrefHeight(30);
            paneDate.setPrefHeight(0);
            controlListView.setPrefHeight(510-30);
            searchButton.setVisible(true);
        } else {
            paneDuration.setPrefHeight(0);
            paneDate.setPrefHeight(0);
            controlListView.setPrefHeight(510);
            searchButton.setVisible(false);
        }
    }

    private void createDateFilter() {

        searchButton.setGraphic(new ImageView(new Image(getClass()
                .getResource("img/search_green_16.png").toExternalForm())));

        searchButton.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                isDateReady = false;
                isDurationReady = false;
                if (paneDate.isVisible()) {
                    try {
                        from = Date.valueOf(fromPicker.getValue());
                        to = Date.valueOf(toPicker.getValue());
                        if (to.before(from)) {
                            showDialogNotification("Error", "Rango de fechas incorrecto");
                            return;
                        }
                        if (isMapReady)
                            addMarkers();
                        isDateReady = true;
                    } catch (NullPointerException e) {
                        //e.printStackTrace();
                        showDialogNotification("Error", "Rango de fechas incorrecto");
                    }
                }
                if (paneDuration.isVisible()) {
                    try {
                        min = Integer.valueOf(filterMin.getText());
                        max = Integer.valueOf(filterMax.getText());
                        if (max < min) {
                            showDialogNotification("Error", "Rango de minutos incorrecto");
                            return;
                        }
                        if (isMapReady)
                            addMarkers();
                        isDurationReady = true;
                    } catch (Exception e) {
                        //e.printStackTrace();
                        showDialogNotification("Error", "Rango de minutos incorrecto");
                    }
                }
            }
        });
    }

    public void loadListView() throws FileNotFoundException {

        controlPositions = service.getAllControlActive();

        controlPositionsData = FXCollections.observableArrayList();

        for (ControlPosition control : controlPositions) {

            // ListCells
            Label nameLabel = new Label("   " + control.getPlaceName());
            nameLabel.setFont(new Font(null, 15));
            ImageView guardImg = new ImageView(new Image(getClass()
                    .getResource("img/point_marker_64.png").toExternalForm()));

            if (!control.getActive()) {
                ColorAdjust desaturate = new ColorAdjust();
                desaturate.setSaturation(-1);
                guardImg.setEffect(desaturate);
            }
            guardImg.setFitHeight(40);
            guardImg.setFitWidth(40);

            nameLabel.setGraphic(guardImg);

            nameLabel.setUserData(control);
            controlPositionsData.addAll(nameLabel);
        }
        controlListView.setItems(controlPositionsData);
        controlListView.setExpanded(true);
        controlListView.setVerticalGap(2.0);
        controlListView.depthProperty().set(1);
        controlListView.setOnMouseClicked(this);

        filterMarker();
    }

    public void setDrawer() {

        //Drawer
        watchHeadHBox.setStyle("-fx-background-color: #ffffff");
        watchHeadHBox.setPrefWidth(275);
        watchHeadHBox.setPadding(new Insets(20));

        watchDrawer.setSidePane(watchDrawerBox);
        watchDrawer.setVisible(false);
        watchDrawer.setOnDrawerClosed(event  ->  {
            watchDrawer.setVisible(false);
            setMarkerFilterField();
        });
        watchDrawer.setOnDrawerOpened(event -> {
            setWatchFilterField();
        });
    }


    public void setWatchFilterField() {
        watchFilterField.setVisible(true);
        markerFilterField.setVisible(false);

        watchFilterField.clear();
    }

    public void setMarkerFilterField() {
        watchFilterField.setVisible(false);
        markerFilterField.setVisible(true);
    }

    @Override
    public void handle(MouseEvent event) {

        if (event.getButton() == MouseButton.PRIMARY
                && controlListView.getSelectionModel().getSelectedItem() != null
                && controlListView.getSelectionModel().getSelectedItem().getUserData() != null) {
            if (!isMapReady) {
                showSnackBar("Espere que se cargue el mapa");
                return;
            }
            openedWatchDrawer();
        }
    }

    @Override
    protected void onBackController() {
        if (watchDrawer.isShown()) {
            watchDrawer.close();
            addMarkers();
        } else {
            super.onBackToSync();
        }
    }

    public void createWatchDrawer() {
        Label iconHeader = new Label();

        ImageView iconImage = new ImageView(
                new Image(getClass().getResource("img/point_marker_64.png").toExternalForm()));
        iconImage.setFitHeight(40);
        iconImage.setFitWidth(40);
        iconHeader.setGraphic(iconImage);

        watchNameLabel = new Label();
        watchNameLabel.setFont(new Font(null, 16));
        watchDniLabel = new Label();
        watchDniLabel.setFont( new Font(null, 12));
        watchDniLabel.setTextFill(Color.valueOf("#aaaaaa"));
        VBox watchLabelsVBox = new VBox();
        watchLabelsVBox.getChildren().addAll(watchNameLabel, watchDniLabel);
        watchHeadHBox.getChildren().addAll(iconHeader, watchLabelsVBox);
        drawerWatchFirstShow = false;
    }

    public void openedWatchDrawer() {

        watchDrawer.open();
        watchDrawer.setVisible(true);

        selectedControl = (ControlPosition) controlListView.getSelectionModel().getSelectedItem().getUserData();

        if(drawerWatchFirstShow)
            createWatchDrawer();

        watchNameLabel.setText("   "+selectedControl.getPlaceName());
        watchDniLabel.setText("     "+(selectedControl.getActive()?"Activo":"Desactivo"));

        watchData = FXCollections.observableArrayList();

        if (paneDate.isVisible() && paneDuration.isVisible()) {
            if (isDateReady
                    && fromPicker.getValue() != null
                    && toPicker.getValue() != null
                    && isDurationReady
                    && filterMin.getText() != null && !filterMin.getText().isEmpty()
                    && filterMax.getText() != null && !filterMax.getText().isEmpty()) {
                positionsUser = service.findAllPositionsByControlAndCompany(selectedControl, getCompany(), from, to, min, max);
            } else {
                watchDrawer.close();
                showSnackBar("Seleccionar lo rangos correctamente y luego presione buscar.");
                return;
            }
        } else if (paneDate.isVisible()) {
            if (isDateReady && fromPicker.getValue() != null && toPicker.getValue() != null) {
                positionsUser = service.findAllPositionsByControlAndCompany(selectedControl, getCompany(), from, to);
            } else {
                watchDrawer.close();
                showSnackBar("Seleccionar lo rangos correctamente y luego presione buscar.");
                return;
            }
        } else if (paneDuration.isVisible()) {
            if (isDurationReady
                    && filterMin.getText() != null && !filterMin.getText().isEmpty()
                    && filterMax.getText() != null && !filterMax.getText().isEmpty()) {
                positionsUser = service.findAllPositionsByControlAndCompany(selectedControl, getCompany(), min, max);
            } else {
                watchDrawer.close();
                showSnackBar("Seleccionar lo rangos correctamente y luego presione buscar.");
                return;
            }
        } else {
            positionsUser = service.findAllPositionsByControlAndCompany(selectedControl, getCompany());
        }

        addMarker(selectedControl);

        for (Position position: positionsUser) {

            HBox hBox = new HBox();
            VBox labelsVBox = new VBox();
            Label nameLabel = new Label("   "+position.getWatch().getUser().getLastname()
                    +" "+position.getWatch().getUser().getName());
            nameLabel.setFont(new Font(null, 14));
            Label timeLabel  = new Label("   "+ RadarDate
                    .getDateWithMonthAndTime(position.getTime()));
            timeLabel.setFont( new Font(null, 12));
            timeLabel.setTextFill(Color.valueOf("#aaaaaa"));
            Label watchLabel  = new Label("   Guardia Nro: "+position.getWatch().getId());
            watchLabel.setFont( new Font(null, 10));
            watchLabel.setTextFill(Color.valueOf("#4B919F"));
            watchLabel.setAlignment(Pos.TOP_RIGHT);
            watchLabel.setPrefWidth(200);
            Label updateLabel  = new Label("    Duración "+RadarDate.secondsToMinutesBest(
                    Math.abs(position.getUpdateTime().intValue())));
            updateLabel.setFont( new Font(null, 10));
            ImageView iconImage = new ImageView(
                    new Image(getClass().getResource("img/icon_multiple_marker_64.png").toExternalForm()));
            iconImage.setFitHeight(45);
            iconImage.setFitWidth(45);
            labelsVBox.getChildren().addAll(nameLabel, timeLabel, updateLabel, watchLabel);
            hBox.getChildren().addAll(iconImage, labelsVBox);

            hBox.setUserData(position);
            watchData.add(hBox);
        }

        watchListView.setItems(watchData);
        watchListView.setExpanded(true);
        watchListView.setVerticalGap(2.0);
        watchListView.depthProperty().set(1);

        filterWatch();
    }

    public void loadPrint(File file) {

        dialogLoadingPrint();

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("company", getCompany().getName());
        parameters.put("place_name", selectedControl.getPlaceName());
        if (paneDate.isVisible()) {
            parameters.put("date_start", "Rango de fechas: Del "+RadarDate.getDateWithMonth(from.getTime())+" al "+RadarDate.getDateWithMonth(to.getTime()));
        } else {
            parameters.put("date_start", "Rango de fechas: Cualquier fecha.");
        }
        if (paneDuration.isVisible()) {
            parameters.put("date_finish", "Duración: de "+min+"m a "+max+"m");
        } else {
            parameters.put("date_finish", "Duración: Cualquier duración.");
        }

        String routeName = "Ruta...";
        List<PointReport> pointReportList = new ArrayList<>();
        for (HBox hBox : watchListView.getItems()) {
            Position position = (Position) hBox.getUserData();
            PointReport pointReport = new PointReport();
            pointReport.setId(position.getWatch().getId().toString());
            pointReport.setUser(position.getWatch().getUser().getFullName());
            pointReport.setDuration(RadarDate
                    .secondsToMinutesBest(Math.abs(position.getUpdateTime().intValue())));
            pointReport.setDistanceMeters(getMeters(position));
            pointReport.setTime(RadarDate.getDayMonthHour(position.getTime()));
            pointReportList.add(pointReport);


            List<RoutePosition> routePositions = service.findAllRPByControlId(position.getControlPosition());
            if (!routePositions.isEmpty()) {
                routeName = routePositions.get(0).getRoute().getName();
            }
        }

        ////////////////// invert array //////////////////////////
        List<PointReport> pointReportListInvert = new ArrayList<>();
        {
            if (!pointReportList.isEmpty())
                for (int i = pointReportList.size(); i > 0; i--) {
                    pointReportListInvert.add(pointReportList.get(i-1));
                }
        }
        //////////////////////////////////////////////////////////////////////////

        parameters.put("route_name", routeName);

        ExecutorService executor = Executors.newFixedThreadPool(1);
        Runnable worker = new PrintReportPointsTask(this,
                new JRBeanCollectionDataSource(pointReportListInvert), parameters, file,
                "points_"+new DateTime().getMillis());
        executor.execute(worker);
        executor.shutdown();
    }

    public void printReport() {
        dialogType = Const.DIALOG_PRINT_BASIC;
        showDialogPrint("Debe seleccionar una ruta para guardar el reporte");
    }

    private void showWatchesDetail() {
        watchListView.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY
                    && watchListView.getSelectionModel()
                    .getSelectedItem().getUserData() != null) {
                if (!isMapReady) {
                    showSnackBar("Espere que se cargue el mapa");
                    return;
                }
                //openedMarkersDrawer();
                addFlag((Position) watchListView.getSelectionModel()
                        .getSelectedItem().getUserData());
            }
        });
    }

    public void createMarkerDrawer() {
        markerTimeLabel = new Label();
        markerTimeLabel.setFont(new Font(null, 12));
        markerHeadHBox.getChildren().addAll(markerTimeLabel);
        drawerMarkerFirstShow = false;
    }

    @Override
    public void mapInitialized() {

        //Set the initial properties of the map.
        MapOptions mapOptions = new MapOptions();

        mapOptions.mapMaker(true)
                .mapType(MapTypeIdEnum.ROADMAP)
                .overviewMapControl(false)
                .panControl(false)
                .rotateControl(false)
                .scaleControl(false)
                .streetViewControl(false)
                .zoomControl(false)
                .zoom(12);

        map = mapView.createMap(mapOptions);

        isMapReady = true;

        addMarkers();

    }

    public void addFlag(Position position) {

        markers = new ArrayList<>();
        markersOptions = new ArrayList<>();

        LatLong latLong = new LatLong(position.getLatitude(), position.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLong);
        markerOptions.animation(Animation.DROP);
        markerOptions.icon("flag_blue_32.png");
        Marker marker = new Marker(markerOptions);
        marker.setTitle(position.getControlPosition().getPlaceName());
        map.addMarker(marker);
        markers.add(marker);
        markersOptions.add(markerOptions);

        map.addUIEventHandler(marker, UIEventType.click, (JSObject obj) -> {
            showSnackBar(position.getWatch().getUser().getLastname()
                    +" "+position.getWatch().getUser().getName()+"\n"+ RadarDate
                    .getDateWithMonthAndTime(position.getTime()));
            System.out.println("You clicked the line at LatLong: lat: " +
                    position.getLatitude() + " lng: " + position.getLongitude());
        });
    }

    public void addMarkers() {

        map.clearMarkers();

        markers = new ArrayList<>();
        markersOptions = new ArrayList<>();

        for (ControlPosition control: controlList) {
            LatLong latLong = new LatLong(control.getLatitude(), control.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLong);
            markerOptions.animation(Animation.DROP);
            markerOptions.icon("red_marker_32.png");
            Marker marker = new Marker(markerOptions);
            marker.setTitle(control.getPlaceName());
            map.addMarker(marker);
            markers.add(marker);
            markersOptions.add(markerOptions);

            map.addUIEventHandler(marker, UIEventType.click, (JSObject obj) -> {
                showSnackBar(control.getPlaceName());
                System.out.println("You clicked the line at LatLong: lat: " +
                        control.getLatitude() + " lng: " + control.getLongitude());
            });
        }

        centerMap();
    }

    public void addMarker(ControlPosition control) {

        map.clearMarkers();

        markers = new ArrayList<>();
        markersOptions = new ArrayList<>();

        LatLong latLong = new LatLong(control.getLatitude(), control.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLong);
        markerOptions.animation(Animation.DROP);
        markerOptions.icon("red_marker_32.png");
        Marker marker = new Marker(markerOptions);
        marker.setTitle(control.getPlaceName());
        map.addMarker(marker);
        markers.add(marker);
        markersOptions.add(markerOptions);

        map.addUIEventHandler(marker, UIEventType.click, (JSObject obj) -> {
            showSnackBar(control.getPlaceName());
            System.out.println("You clicked the line at LatLong: lat: " +
                    control.getLatitude() + " lng: " + control.getLongitude());
        });

        centerMap(new LatLong(control.getLatitude(), control.getLongitude()));
    }

    public void addMarkersRoute() {
        for (Position position: positionWatch) {

            LatLong latLong = new LatLong(position.getLatitude(), position.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLong);
            markerOptions.animation(Animation.DROP);
            markerOptions.icon("flag_blue_32.png");
            Marker marker = new Marker(markerOptions);
            //marker.setTitle(position.getPlaceName());
            map.addMarker(marker);
            map.addUIEventHandler(marker, UIEventType.click, (JSObject obj) -> {
                showSnackBar(RadarDate.getHours(position.getTime()));
                System.out.println("You clicked the line at LatLong: lat: " +
                        position.getLatitude() + " lng: " + position.getLongitude());
            });
        }
        centerMap(positionWatch);
    }

    public void centerMap() {
        LatLongBounds latLongBounds = new LatLongBounds();
        for (ControlPosition control: controlList) {
            LatLong latLong = new LatLong(control.getLatitude(), control.getLongitude());
            latLongBounds.extend(latLong);
        }
        map.fitBounds(latLongBounds);
    }

    public void centerMap(LatLong latLong) {
        LatLongBounds latLongBounds = new LatLongBounds();
        latLongBounds.extend(latLong);
        map.fitBounds(latLongBounds);
    }

    private void centerMap(List<Position> positions) {
        if (positions.isEmpty())
            return;

        LatLongBounds latLongBounds = new LatLongBounds();
        for (Position position : positions) {
            LatLong latLong = new LatLong(position.getLatitude(), position.getLongitude());
            latLongBounds.extend(latLong);
        }
        map.fitBounds(latLongBounds);
    }

    private Integer getMeters(Position position) {

        double radioEarth = 6371000;
        double dLat = Math.toRadians(position.getLatitude()
                - position.getControlPosition().getLatitude());
        double dLng = Math.toRadians(position.getLongitude()
                - position.getControlPosition().getLongitude());
        double sLat = Math.sin(dLat / 2);
        double sLng = Math.sin(dLng / 2);
        double va1 = Math.pow(sLat, 2) + Math.pow(sLng, 2)
                * Math.cos(Math.toRadians(position.getControlPosition().getLatitude()))
                * Math.cos(Math.toRadians(position.getLatitude()));
        double va2 = 2 * Math.atan2(Math.sqrt(va1), Math.sqrt(1 - va1));
        Double distance = radioEarth * va2;

        return distance.intValue();
    }

    private void filterWatch() {
        FilteredList<HBox> filteredData = new FilteredList<>(watchData, p -> true);
        watchFilterField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(hBox -> {
                // If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                Position position = (Position) hBox.getUserData();
                if (position == null)
                    return false;
                // Compare first name and last name of every person with filter text.
                String lowerCaseFilter = watchFilterField.getText().toLowerCase();

                String timeString = RadarDate
                        .getDateWithMonth(new DateTime(position.getTime()));
                String fullName = position.getWatch().getUser().getLastname()
                        +" "+position.getWatch().getUser().getName();
                if (timeString.toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches first name.
                } else if (fullName.toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches first name.
                }
                return false; // Does not match.
            });
        });

        SortedList<HBox> sortedData = new SortedList<>(filteredData);
        watchListView.setItems(sortedData);
        checkWatchFilter(filteredData);
    }

    void checkWatchFilter(FilteredList<HBox> filteredData) {
        filteredData.setPredicate(hBox -> {
            // If filter text is empty, display all persons.
            if (watchFilterField.getText() == null || watchFilterField.getText().isEmpty()) {
                return true;
            }

            Position position = (Position) hBox.getUserData();
            if (position == null)
                return false;
            // Compare first name and last name of every person with filter text.
            String lowerCaseFilter = watchFilterField.getText().toLowerCase();

            String timeString = RadarDate
                    .getDateWithMonth(new DateTime(position.getTime()));
            String fullName = position.getWatch().getUser().getLastname()
                    +" "+position.getWatch().getUser().getName();
            if (timeString.toLowerCase().contains(lowerCaseFilter)) {
                return true; // Filter matches first name.
            } else if (fullName.toLowerCase().contains(lowerCaseFilter)) {
                return true; // Filter matches first name.
            }
            return false; // Does not match.
        });
    }

    private void filterMarker() {
        FilteredList<Label> filteredData = new FilteredList<>(controlPositionsData, p -> true);
        markerFilterField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(label -> {
                // If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                ControlPosition position = (ControlPosition) label.getUserData();
                if (position == null)
                    return false;
                // Compare first name and last name of every person with filter text.
                String lowerCaseFilter = markerFilterField.getText().toLowerCase();

                if (position.getPlaceName().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches first name.
                }
                return false; // Does not match.
            });
        });

        SortedList<Label> sortedData = new SortedList<>(filteredData);
        controlListView.setItems(sortedData);
        checkMarkerFilter(filteredData);
    }

    void checkMarkerFilter(FilteredList<Label> filteredData) {
        filteredData.setPredicate(label -> {
            // If filter text is empty, display all persons.
            if (markerFilterField.getText() == null || markerFilterField.getText().isEmpty()) {
                return true;
            }

            ControlPosition position = (ControlPosition) label.getUserData();
            if (position == null)
                return false;
            // Compare first name and last name of every person with filter text.
            String lowerCaseFilter = markerFilterField.getText().toLowerCase();

            if (position.getPlaceName()
                    .toLowerCase().contains(lowerCaseFilter)) {
                return true; // Filter matches first name.
            }
            return false; // Does not match.
        });
    }

    @Override
    public void onDialogAccept(ActionEvent actionEvent) {
        super.onDialogAccept(actionEvent);
        switch (dialogType) {
            case Const.DIALOG_PRINT_BASIC:
                File file = selectDirectory();
                if (file != null)
                    loadPrint(file);
                break;
        }
    }


    @Override
    public void onPrintCompleted() {
        closeDialogLoading();
        showSnackBar("Guardado completado");
    }

    @Override
    public void onPrintFailure(String message) {
        closeDialogLoading();
        showSnackBar("Guardado fallido");
    }
}

