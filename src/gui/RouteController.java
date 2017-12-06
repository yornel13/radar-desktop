package gui;

import com.jfoenix.controls.*;
import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.MapReadyListener;
import com.lynden.gmapsfx.javascript.event.UIEventType;
import com.lynden.gmapsfx.javascript.object.*;
import gui.async.PrintReportRouteWatchsTask;
import io.datafx.controller.ViewController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import model.*;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import netscape.javascript.JSObject;
import org.joda.time.DateTime;
import util.Const;
import util.RadarDate;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Date;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@ViewController("view/route.fxml")
public class RouteController extends BaseController implements MapComponentInitializedListener,MapReadyListener, PrintReportRouteWatchsTask.PrintTask {

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private JFXTextField filterField;

    @FXML
    private JFXDrawer drawer;

    @FXML
    private JFXDrawer watchDrawer;

    @FXML
    private VBox drawerBox;

    @FXML
    private HBox headHBox;

    @FXML
    private HBox watchHeadHBox;

    @FXML
    private JFXTextField drawerFilterField;

    @FXML
    private VBox watchDrawerBox;

    private Route selectedRoute;

    private Watch selectedWatch;

    private Label drawerNameLabel;

    private Label drawerNumberLabel;

    private Label nameWatchLabel;

    private Label dateWatchLabel;

    @FXML
    private JFXButton printReport;

    @FXML
    private JFXButton searchButton;

    @FXML
    private JFXDatePicker fromPicker;

    @FXML
    private JFXDatePicker toPicker;

    ///////////// Route ////////////////////
    @FXML
    private JFXListView<HBox> routeListView;

    private ObservableList<HBox> routeData;

    private List<Route> routeList;

    /////////////(Drawer) ////////////
    @FXML
    private JFXListView<HBox> drawerListView;

    private ObservableList<HBox> drawerData;

    private List<Watch> drawerList;

    /////////////(Drawer Watch points) ////////////
    @FXML
    private JFXListView<HBox> pointsListView;

    private ObservableList<HBox> drawerPointsData;

    private List<Position> drawerPointsList;


    ///////////// Map ////////////////////
    @FXML
    private GoogleMapView mapView;

    private List<Marker> markers;

    private List<MarkerOptions> markersOptions;

    private GoogleMap map;

    private MapOptions mapOptions;

    public boolean isMapReady = false;

    private boolean drawerFirstShow = true;

    private boolean drawerWatchFirstShow = true;

    private boolean isDateReady = true;

    private Date from;
    private Date to;

    @PostConstruct
    public void init() throws FileNotFoundException {

        setTitleToCompany("Control de Guardias (Por ruta)");
        setBackButtonImageBlack();

        setDrawer();

        loadListView();

        mapView.addMapInializedListener(this);
        mapView.addMapReadyListener(this);

        ImageView reportImage = new ImageView(new Image(getClass()
                .getResource("img/printer.png").toExternalForm()));

        printReport.setOnAction(event ->  printReport());
        printReport.setGraphic(reportImage);

        searchButton.setGraphic(new ImageView(new Image(getClass()
                .getResource("img/search_green_16.png").toExternalForm())));

        searchButton.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
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
        });

        fromPicker.valueProperty().addListener((observable, oldValue, newValue) ->
                isDateReady = false);
        toPicker.valueProperty().addListener((observable, oldValue, newValue) -> isDateReady = false);
    }

    private void printReport() {
        dialogType = Const.DIALOG_PRINT_FULL;
        showDialogPrint("Debe seleccionar una ruta para guardar el reporte");
    }

    public void onDialogAccept(ActionEvent actionEvent) {
        super.onDialogAccept(actionEvent);
        switch (dialogType) {
            case Const.DIALOG_PRINT_FULL: {
                File file = selectDirectory();
                if (file != null)
                    loadPrintFull(file);
            }
            break;
        }
    }

    public void loadPrintFull(File file){

        dialogLoadingPrint();

        Map<String, Object> parameters = new HashMap<>();

        List<Watch> watchList = new ArrayList<>();
        List<Position> positionList = new ArrayList<>();
        for (HBox hBox: drawerListView.getItems()) {
            watchList.add((Watch) hBox.getUserData());
        }
        for (Watch watch: watchList) {
            positionList.addAll(service
                    .findAllPositionsByWatchUpdateTime(watch));
        }
        Collections.sort(positionList, Comparator.comparing(Position::getTime));

        Position positionLast = null;
        List<WatchRouteMasterReport> watchRouteMasterReports = new ArrayList<>();
        WatchRouteMasterReport watchRouteMasterReport;
        List<WatchSubReport> watchReportList = new ArrayList<>();

        System.out.println(positionList.size());

        for (Position position: positionList) {

            WatchSubReport watchSubReport = new WatchSubReport();

            watchSubReport.setDni(position.getWatch().getUser().getDni());
            watchSubReport.setUser(position.getWatch().getUser().getFullName());
            watchSubReport.setMarker(position.getControlPosition().getPlaceName());
            watchSubReport.setDate(RadarDate.getDateShort(position.getTime()));
            watchSubReport.setTime(RadarDate.getHours(position.getTime()));
            watchSubReport.setDiff(position.getDifferent());

            DateTime dateTime = new DateTime(position.getTime());
            DateTime dateTimeLast = null;

            if (positionList.indexOf(position) > 0)
                dateTimeLast = new DateTime(positionLast.getTime());

            if (positionList.indexOf(position) == 0 ||
                    (dateTime.getYear() == dateTimeLast.getYear()
                    && dateTime.getMonthOfYear() == dateTimeLast.getMonthOfYear()
                    && dateTime.getDayOfMonth() == dateTimeLast.getDayOfMonth())) {
                watchReportList.add(watchSubReport);

                if (positionList.indexOf(position) == positionList.size()-1) {
                    watchRouteMasterReport = new WatchRouteMasterReport();
                    watchRouteMasterReport.setDate(RadarDate.getDateShort(position.getTime()));
                    watchRouteMasterReport.setWatchsReportList(watchReportList);
                    watchRouteMasterReports.add(watchRouteMasterReport);
                }
            } else {

                watchRouteMasterReport = new WatchRouteMasterReport();
                watchRouteMasterReport.setDate(RadarDate.getDateShort(positionLast.getTime()));
                watchRouteMasterReport.setWatchsReportList(watchReportList);
                watchRouteMasterReports.add(watchRouteMasterReport);

                watchReportList = new ArrayList<>();
                watchReportList.add(watchSubReport);
            }
            positionLast = position;
        }

        System.out.println(watchRouteMasterReports.size());

        parameters.put("route_name", selectedRoute.getName());
        parameters.put("route_id", selectedRoute.getId().toString());

        ExecutorService executor = Executors.newFixedThreadPool(1);
        if (watchRouteMasterReports.isEmpty()) {
            Runnable worker = new PrintReportRouteWatchsTask(this,
                    new JREmptyDataSource(), parameters, file,
                    "guardia_ruta_"+new DateTime().getMillis());
            executor.execute(worker);
            executor.shutdown();
        } else {
            Runnable worker = new PrintReportRouteWatchsTask(this,
                    new JRBeanCollectionDataSource(watchRouteMasterReports), parameters, file,
                    "guardia_ruta_" + new DateTime().getMillis());
            executor.execute(worker);
            executor.shutdown();
        }
    }

    public void loadListView() {
        try {
            loadRouteListView();
            filterRouteData();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setDrawer() {

        headHBox.setPadding(new Insets(20, 20,20,20));
        drawer.setSidePane(drawerBox);
        drawer.setOnDrawerClosed(event  ->  {
            drawer.setVisible(false);
            drawerFilterField.setVisible(false);
            filterField.setVisible(true);
        });
        drawer.setOnDrawerOpened(event -> {
            drawerFilterField.setVisible(true);
            drawerFilterField.clear();
            filterField.setVisible(false);
        });
        //////////////////////////////////////////////////////////////////////////////////////
        watchHeadHBox.setStyle("-fx-background-color: #ffffff");
        watchHeadHBox.setPrefWidth(275);
        watchHeadHBox.setPadding(new Insets(6, 6,6,15));

        watchDrawer.setSidePane(watchDrawerBox);
        watchDrawer.setVisible(false);
        watchDrawer.setOnDrawerClosed(event  ->  {
            watchDrawer.setVisible(false);
            //setWatchFilterField();
        });
        watchDrawer.setOnDrawerOpened(event -> {
            //setMarkerFilterField();
        });
    }

    public void loadRouteListView() throws IOException {

        routeList = service.getAllRoute();

        routeData = FXCollections.observableArrayList();

        for (Route route: routeList) {

            HBox hBox = new HBox();
            HBox imageHBox = new HBox();
            VBox labelsVBox = new VBox();
            // ListCells
            Label nameLabel = new Label("   "+route.getName());
            nameLabel.setFont(new Font(null, 16));
            Label activeLabel  = new Label("   "+(route.getActive()?"Activo":"Desactivo"));
            activeLabel.setFont( new Font(null, 14));
            activeLabel.setTextFill(Color.valueOf("#aaaaaa"));
            ImageView guardImg = new ImageView(new Image(getClass().getResource("img/route_marker_64.png").toExternalForm()));
            if (!route.getActive()) {
                ColorAdjust desaturate = new ColorAdjust();
                desaturate.setSaturation(-1);
                guardImg.setEffect(desaturate);
            }
            guardImg.setFitHeight(40);
            guardImg.setFitWidth(40);

            imageHBox.getChildren().addAll(guardImg, nameLabel);
            imageHBox.setPrefHeight(4);
            labelsVBox.getChildren().addAll(nameLabel, activeLabel);
            labelsVBox.setPadding(new Insets(-1,3,-1,3));
            hBox.getChildren().addAll(imageHBox, labelsVBox);
            hBox.setUserData(route);
            routeData.add(hBox);

        }

        routeListView.setItems(routeData);
        routeListView.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY &&
                    routeListView.getSelectionModel().getSelectedItem() != null) {
                if (isDateReady) {
                    selectedRoute = (Route) routeListView
                            .getSelectionModel().getSelectedItem().getUserData();
                    openedDrawer();
                    drawerFilterField.clear();
                    filterDrawerData();
                } else {
                    showSnackBar("Seleccionar los rangos correctamente y luego presione buscar.");
                }
            }
        });
    }

    public void openedDrawer() {

        drawer.open();
        drawer.setVisible(true);

        List<RoutePosition> routePositions = service.findAllRPByRouteId(selectedRoute);
        centerMap(routePositions);

        if(drawerFirstShow)
            createDrawer();

        drawerNameLabel.setText("   "+selectedRoute.getName());
        if (routePositions.isEmpty()) {
            drawerNumberLabel.setText("    "+"Sin puntos de control");
        } else if (routePositions.size() == 1) {
            drawerNumberLabel.setText("    "+"1 punto de control");
        } else {
            drawerNumberLabel.setText("    "+routePositions.size()+" puntos de control");
        }

        loadDrawerListView();

        drawerListView.setExpanded(true);
        drawerListView.setVerticalGap(1.0);
        drawerListView.depthProperty().set(1);


        drawerListView.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY &&
                    drawerListView.getSelectionModel().getSelectedItem() != null) {
                selectedWatch = (Watch)
                        drawerListView.getSelectionModel().getSelectedItem().getUserData();
                openedDrawerWatch();
            }

        });
    }

    private void loadDrawerListView() {

        drawerData = FXCollections.observableArrayList();

        if (fromPicker.getValue() == null
                && toPicker.getValue() == null) {
            drawerList = service.findAllWatchByRoute(selectedRoute);
        } else {
            drawerList = service.findAllWatchByRouteBetween(selectedRoute, from, to);
        }

        for (Watch watch: drawerList) {

            HBox wDetail = new HBox();
            ImageView iconImage = new ImageView(new Image(getClass()
                    .getResource("img/policeman_64.png").toExternalForm()));
            iconImage.setFitHeight(30);
            iconImage.setFitWidth(30);


            Label nameLabel = new Label();
            nameLabel.setText("   "+watch.getUser().getFullName());

            Label dateLabel = new Label();
            dateLabel.setFont( new Font(null, 12));
            dateLabel.setTextFill(Color.valueOf("#aaaaaa"));
            dateLabel.setText("    el "+RadarDate.getDateWithMonth(watch.getStartTime()));

            VBox watchLabelsVBox = new VBox();
            watchLabelsVBox.getChildren().addAll(nameLabel, dateLabel);

            wDetail.getChildren().addAll(iconImage, watchLabelsVBox);
            wDetail.setUserData(watch);
            drawerData.add(wDetail);
        }

        drawerListView.setItems(drawerData);

    }

    public void createDrawer() {
        Label iconHeader = new Label();

        ImageView iconImage = new ImageView(new Image(getClass()
                .getResource("img/icon_multiple_marker_64.png").toExternalForm()));
        iconImage.setFitHeight(40);
        iconImage.setFitWidth(40);
        iconHeader.setGraphic(iconImage);

        drawerNameLabel = new Label();
        drawerNameLabel.setFont(new Font(null, 16));

        drawerNumberLabel = new Label();
        drawerNumberLabel.setFont( new Font(null, 12));
        drawerNumberLabel.setTextFill(Color.valueOf("#aaaaaa"));

        VBox watchLabelsVBox = new VBox();
        watchLabelsVBox.getChildren().addAll(drawerNameLabel, drawerNumberLabel);
        headHBox.getChildren().addAll(iconHeader, watchLabelsVBox);
        drawerFirstShow = false;
    }

    public void openedDrawerWatch() {

        watchDrawer.open();
        watchDrawer.setVisible(true);

        if(drawerWatchFirstShow)
            createDrawerWatch();

        nameWatchLabel.setText("   "+selectedWatch.getUser().getFullName());
        dateWatchLabel.setText("    el "+RadarDate.getDateWithMonth(selectedWatch.getStartTime()));

        loadDrawerPointsListView();

        pointsListView.setExpanded(true);
        pointsListView.setVerticalGap(1.0);
        pointsListView.depthProperty().set(1);
    }

    private void loadDrawerPointsListView() {

        drawerPointsData = FXCollections.observableArrayList();

        drawerPointsList = service.findAllPositionsByWatchUpdateTime(selectedWatch);

        for (Position position: drawerPointsList) {

            HBox hBox = new HBox();
            VBox labelsVBox = new VBox();
            Label placeLabel = new Label("   "+ position.getControlPosition().getPlaceName());
            placeLabel.setFont(new Font(null, 14));
            Label timeLabel  = new Label("   "+ RadarDate.getHours(position.getTime()));
            timeLabel.setFont( new Font(null, 12));
            timeLabel.setTextFill(Color.valueOf("#aaaaaa"));
            Label updateLabel  = new Label("   "+ position.getDifferent());
            updateLabel.setFont( new Font(null, 10));
            updateLabel.setTextFill(Color.valueOf("#4B919F"));
            ImageView iconImage = new ImageView(new Image(getClass().getResource("img/marker_in_map_64.png").toExternalForm()));
            iconImage.setFitHeight(40);
            iconImage.setFitWidth(40);
            labelsVBox.getChildren().addAll(placeLabel, timeLabel, updateLabel);
            hBox.getChildren().addAll(iconImage, labelsVBox);

            hBox.setUserData(position);
            drawerPointsData.add(hBox);
        }
        if (isMapReady) {
            addMarkersRoute();
            centerMap(service.findAllRPByRouteId(selectedRoute));
        }

        pointsListView.setItems(drawerPointsData);

        drawerFilterField.setDisable(true);
        printReport.setVisible(false);
    }

    public void createDrawerWatch() {
        ImageView iconImage = new ImageView(new Image(getClass()
                .getResource("img/policeman_64.png").toExternalForm()));
        iconImage.setFitHeight(30);
        iconImage.setFitWidth(30);

        nameWatchLabel = new Label();

        dateWatchLabel = new Label();
        dateWatchLabel.setFont( new Font(null, 12));
        dateWatchLabel.setTextFill(Color.valueOf("#aaaaaa"));

        VBox watchLabelsVBox = new VBox();
        watchLabelsVBox.getChildren().addAll(nameWatchLabel, dateWatchLabel);
        watchHeadHBox.getChildren().addAll(iconImage, watchLabelsVBox);

        drawerWatchFirstShow = false;
    }


    @Override
    protected void onBackController() {
        if (watchDrawer.isShown()) {
            watchDrawer.close();
            addMarkers();
            centerMap(service.findAllRPByRouteId(selectedRoute));
            drawerFilterField.setDisable(false);
            printReport.setVisible(true);
        } else if (drawer.isShown()) {
            drawer.close();
            centerMap();
        } else {
            super.onBackController();
        }
    }

    @Override
    public void mapInitialized() {

        //Set the initial properties of the map.
        mapOptions = new MapOptions();

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

        mapView.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                //hideMarkerBar(null);
            }
        });

    }

    public void addMarkers() {

        if (isMapReady) {

            map.clearMarkers();

            markers = new ArrayList<>();
            markersOptions = new ArrayList<>();

            for (ControlPosition control : service.getAllControlActive()) {

                if (control.getActive()) {
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
                        //openMarkerBar(control);
                    });
                }
            }
        }
        centerMap();
    }

    private void centerMap() {
        List<ControlPosition> controlList = service.getAllControlActive();
        if (isMapReady) {
            if (controlList.isEmpty())
                return;

            LatLongBounds latLongBounds = new LatLongBounds();
            for (ControlPosition control : controlList) {
                LatLong latLong = new LatLong(control.getLatitude(),
                        control.getLongitude());
                latLongBounds.extend(latLong);
            }
            map.fitBounds(latLongBounds);
        }
    }

    private void centerMap(List<RoutePosition> routePositions) {
        if (isMapReady) {
            if (routePositions.isEmpty())
                return;

            LatLongBounds latLongBounds = new LatLongBounds();
            for (RoutePosition control : routePositions) {
                LatLong latLong = new LatLong(control.getControlPosition().getLatitude(),
                        control.getControlPosition().getLongitude());
                latLongBounds.extend(latLong);
            }
            map.fitBounds(latLongBounds);
        }
    }

    private void centerMap(List<Position> positions, String none) {
        if (isMapReady) {
            if (positions.isEmpty())
                return;

            LatLongBounds latLongBounds = new LatLongBounds();
            for (Position control : positions) {
                LatLong latLong = new LatLong(control.getControlPosition().getLatitude(),
                        control.getControlPosition().getLongitude());
                latLongBounds.extend(latLong);
            }
            map.fitBounds(latLongBounds);
        }
    }

    public void addMarkersRoute() {
        for (Position position: drawerPointsList) {

            LatLong latLong = new LatLong(position.getLatitude(), position.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLong);
            markerOptions.animation(Animation.DROP);
            markerOptions.icon("flag_blue_32.png");
            Marker marker = new Marker(markerOptions);
            map.addMarker(marker);
            map.addUIEventHandler(marker, UIEventType.click, (JSObject obj) -> {
                showSnackBar(RadarDate.getHours(position.getTime()));
                System.out.println("You clicked the line at LatLong: lat: " +
                        position.getLatitude() + " lng: " + position.getLongitude());
            });
        }
        //centerMap(drawerPointsList, null);
    }

    private void filterRouteData() {
        FilteredList<HBox> filteredData = new FilteredList<>(routeData, p -> true);
        filterField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(hBox -> {
                // If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                Route route = (Route) hBox.getUserData();
                if (route == null)
                    return false;
                // Compare first name and last name of every person with filter text.
                String lowerCaseFilter = filterField.getText().toLowerCase();

                if (route.getName().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches first name.
                }
                return false; // Does not match.
            });
        });

        SortedList<HBox> sortedData = new SortedList<>(filteredData);
        routeListView.setItems(sortedData);
        checkFilterRoute(filteredData);
    }

    void checkFilterRoute(FilteredList<HBox> filteredData) {
        filteredData.setPredicate(hBox -> {
            // If filter text is empty, display all persons.
            if (filterField.getText() == null || filterField.getText().isEmpty()) {
                return true;
            }

            Route route = (Route) hBox.getUserData();
            if (route == null)
                return false;
            // Compare first name and last name of every person with filter text.
            String lowerCaseFilter = filterField.getText().toLowerCase();

            if (route.getName().toLowerCase().contains(lowerCaseFilter)) {
                return true; // Filter matches first name.
            }
            return false; // Does not match.
        });
    }

    private void filterDrawerData() {
        FilteredList<HBox> filteredData = new FilteredList<>(drawerData, p -> true);
        drawerFilterField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(hBox -> {
                // If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                Watch watch = (Watch) hBox.getUserData();
                User user = watch.getUser();
                String date = RadarDate.getDateWithMonth(watch.getStartTime());
                if (watch == null)
                    return false;
                // Compare first name and last name of every person with filter text.
                String lowerCaseFilter = drawerFilterField.getText().toLowerCase();

                String fullName = user.getLastname()+" "+user.getName();
                if (user.getName().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches first name.
                } else if (user.getLastname().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches last name.
                } else if (user.getDni().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches last name.
                } else if (fullName.toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches last name.
                } else if (date.toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches last name.
                }
                return false; // Does not match.
            });
        });

        SortedList<HBox> sortedData = new SortedList<>(filteredData);
        drawerListView.setItems(sortedData);
        checkFilterDrawer(filteredData);
    }

    void checkFilterDrawer(FilteredList<HBox> filteredData) {
        filteredData.setPredicate(hBox -> {
            // If filter text is empty, display all persons.
            if (drawerFilterField.getText() == null || drawerFilterField.getText().isEmpty()) {
                return true;
            }

            Watch watch = (Watch) hBox.getUserData();
            User user = watch.getUser();
            String date = RadarDate.getDateWithMonth(watch.getStartTime());
            if (watch == null)
                return false;
            // Compare first name and last name of every person with filter text.
            String lowerCaseFilter = drawerFilterField.getText().toLowerCase();

            String fullName = user.getLastname()+" "+user.getName();
            if (user.getName().toLowerCase().contains(lowerCaseFilter)) {
                return true; // Filter matches first name.
            } else if (user.getLastname().toLowerCase().contains(lowerCaseFilter)) {
                return true; // Filter matches last name.
            } else if (user.getDni().toLowerCase().contains(lowerCaseFilter)) {
                return true; // Filter matches last name.
            } else if (fullName.toLowerCase().contains(lowerCaseFilter)) {
                return true; // Filter matches last name.
            } else if (date.toLowerCase().contains(lowerCaseFilter)) {
                return true; // Filter matches last name.
            }
            return false; // Does not match.
        });
    }

    @Override
    public void mapReady() {
        isMapReady = true;
        addMarkers();
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

