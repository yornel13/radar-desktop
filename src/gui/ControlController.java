package gui;

import com.jfoenix.controls.*;
import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.event.UIEventType;
import com.lynden.gmapsfx.javascript.object.*;
import gui.async.PrintReportPointsTask;
import io.datafx.controller.ViewController;
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
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import model.ControlPosition;
import model.PointReport;
import model.Position;
import model.User;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import netscape.javascript.JSObject;
import org.joda.time.DateTime;
import util.Const;
import util.RadarDate;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    private boolean drawerWatchFirstShow = true;
    private boolean drawerMarkerFirstShow = true;

    public boolean isMapReady = false;

    private boolean isDateReady = false;
    private Date from;
    private Date to;

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
    }

    private void createDateFilter() {

        searchButton.setGraphic(new ImageView(new Image(getClass()
                .getResource("img/search_green_16.png").toExternalForm())));

        searchButton.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                isDateReady = false;
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
                    e.printStackTrace();
                    showDialogNotification("Error", "Rango de fechas incorrecto");
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
                && controlListView.getSelectionModel()
                .getSelectedItem().getUserData() != null) {
            if (isDateReady)
                openedWatchDrawer();
            else
                showSnackBar("Seleccione el rango de fechas primero y presione buscar");
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
        addMarker(selectedControl);

        if(drawerWatchFirstShow)
            createWatchDrawer();

        watchNameLabel.setText("   "+selectedControl.getPlaceName());
        watchDniLabel.setText("     "+(selectedControl.getActive()?"Activo":"Desactivo"));

        watchData = FXCollections.observableArrayList();

        if (isDateReady && fromPicker.getValue() != null && toPicker.getValue() != null)
            positionsUser = service.findAllPositionsByControlAndCompany(selectedControl, getCompany(), from, to);
        else {
            watchDrawer.close();
            showSnackBar("Seleccionar el rango de preciosa y presione buscar.");
            return;
        }

        for (Position position: positionsUser) {

            HBox hBox = new HBox();
            VBox labelsVBox = new VBox();
            Label nameLabel = new Label("   "+position.getWatch().getUser().getLastname()
                    +" "+position.getWatch().getUser().getName());
            nameLabel.setFont(new Font(null, 14));
            Label timeLabel  = new Label("   "+ RadarDate
                    .getFechaConMesYHora(position.getTime()));
            timeLabel.setFont( new Font(null, 12));
            timeLabel.setTextFill(Color.valueOf("#aaaaaa"));
            Label watchLabel  = new Label("   Guardia Nro: "+position.getWatch().getId());
            watchLabel.setFont( new Font(null, 10));
            watchLabel.setTextFill(Color.valueOf("#4B919F"));
            watchLabel.setAlignment(Pos.TOP_RIGHT);
            watchLabel.setPrefWidth(200);
            ImageView iconImage = new ImageView(
                    new Image(getClass().getResource("img/icon_multiple_marker_64.png").toExternalForm()));
            iconImage.setFitHeight(45);
            iconImage.setFitWidth(45);
            labelsVBox.getChildren().addAll(nameLabel, timeLabel, watchLabel);
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
        parameters.put("date_start", RadarDate.getFechaConMesYHora(from.getTime()));
        parameters.put("date_finish", RadarDate.getFechaConMesYHora(to.getTime()));

        List<PointReport> pointReportList = new ArrayList<>();
        for (HBox hBox : watchListView.getItems()) {
            Position position = (Position) hBox.getUserData();
            PointReport pointReport = new PointReport();
            pointReport.setId(position.getWatch().getId().toString());
            pointReport.setUser(position.getWatch().getUser().getFullName());
            pointReport.setDistanceMeters(getMeters(position));
            pointReport.setTime(RadarDate.getDiaMesConHora(position.getTime()));
            pointReportList.add(pointReport);
        }

        ExecutorService executor = Executors.newFixedThreadPool(1);
        Runnable worker = new PrintReportPointsTask(this,
                new JRBeanCollectionDataSource(pointReportList), parameters, file,
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
                    .getFechaConMesYHora(position.getTime()));
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
                showSnackBar(RadarDate.getHora(position.getTime()));
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
                        .getFechaConMes(new DateTime(position.getTime()));
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
                    .getFechaConMes(new DateTime(position.getTime()));
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

