package gui;

import com.jfoenix.controls.*;
import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.MapReadyListener;
import com.lynden.gmapsfx.javascript.event.UIEventType;
import com.lynden.gmapsfx.javascript.object.*;
import gui.async.PrintReportGlobalDetailRoute;
import gui.async.PrintReportRouteDetailsTask;
import gui.async.PrintReportWatchsTask;
import io.datafx.controller.ViewController;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import netscape.javascript.JSObject;
import org.joda.time.DateTime;
import service.RadarService;
import util.Const;
import util.RadarDate;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@ViewController("view/marker.fxml")
public class MarkerController extends BaseController implements MapComponentInitializedListener,
        EventHandler<MouseEvent>,MapReadyListener, RadarService.ErrorCases,
        PrintReportRouteDetailsTask.PrintTask, PrintReportWatchsTask.PrintTask {

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private JFXTextField filterField;

    @FXML
    private JFXTextField drawerFilterField;

    @FXML
    public TextField nameField;

    @FXML
    public JFXButton editButton;

    @FXML
    public JFXButton disableButton;

    private JFXButton floatingButton;

    @FXML
    private JFXTextField nameRouteField;

    @FXML
    private Pane bar;

    @FXML
    private Pane addPane;

    @FXML
    private Pane barEditRoute;

    @FXML
    private JFXTabPane tabPane;

    @FXML
    private Label editLabel;

    @FXML
    private Label addLabel;

    @FXML
    private JFXDrawer drawer;

    @FXML
    private VBox drawerBox;

    @FXML
    private HBox headHBox;



    ///////////// Markers ////////////////////
    @FXML
    private JFXListView<HBox> markerListView;

    private ObservableList<HBox> markerData;

    private ControlPosition selectedMarker;

    private List<ControlPosition> markerList;

    ///////////// Routes ////////////////////
    @FXML
    private JFXListView<HBox> routeListView;

    private ObservableList<HBox> routeData;

    private List<Route> routeList;

    private Route selectedRoute;

    ///////////// Route positions (Drawer) ////////////
    @FXML
    private JFXListView<HBox> drawerListView;

    private ObservableList<HBox> drawerData;

    private List<RoutePosition> drawerList;

    ///////////// Control add ////////////////////
    @FXML
    private JFXListView<ControlPosition> controlListView;

    private ObservableList<ControlPosition> controlData;

    private List<ControlPosition> controlList;

    ///////////// Map ////////////////////
    @FXML
    private GoogleMapView mapView;

    private List<Marker> markers;

    private List<MarkerOptions> markersOptions;

    private GoogleMap map;

    private MapOptions mapOptions;

    public boolean isMapReady = false;

    private Label drawerNameLabel;

    private boolean drawerFirstShow = true;

    private static Boolean editing;

    @PostConstruct
    public void init() throws FileNotFoundException {

        setTitle("Marcadores y Rutas");
        setBackButtonImageBlack();

        bar.setVisible(false);

        setDrawer();

        createTabPane();

        loadListView();

        mapView.addMapInializedListener(this);
        mapView.addMapReadyListener(this);
    }

    public void setDrawer() {

        headHBox.setPadding(new Insets(20, -10,20,-20));
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
    }

    public void loadListView() {
        try {
            loadRouteListView();
            loadMarkerListView();
            filterMarkerData();
            filterRouteData();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private JFXButton printRouteReport;
    public void createTabPane() {

        markerListView = new JFXListView<>();
        markerListView.setPrefWidth(275);
        markerListView.setExpanded(true);
        markerListView.setVerticalGap(1.0);
        markerListView.depthProperty().set(1);

        Tab tabM = new Tab();
        tabM.setText("Marcadores");
        tabM.setContent(markerListView);
        tabM.setUserData(1);
        tabPane.getTabs().add(tabM);

        routeListView = new JFXListView<>();
        routeListView.setPrefWidth(275);
        routeListView.setExpanded(true);
        routeListView.setVerticalGap(1.0);
        routeListView.depthProperty().set(1);

        Tab tabR = new Tab();
        tabR.setText("Rutas");
        tabR.setContent(routeListView);
        tabR.setUserData(0);
        tabPane.getTabs().add(tabR);

        floatingButton = new JFXButton("+");
        floatingButton.setButtonType(JFXButton.ButtonType.RAISED);
        floatingButton.getStyleClass().addAll("floatingButton");
        floatingButton.setLayoutX(210);
        floatingButton.setLayoutY(525);
        anchorPane.getChildren().add(floatingButton);
        floatingButton.setVisible(false);

        tabPane.getSelectionModel().selectedItemProperty().addListener((ov, t, t1) -> {
            if ((int) t1.getUserData() == 1) {
                hideFloatingButton();
                addMarkers();
            } else if ((int) t1.getUserData() == 0) {
                floatingButton.setGraphic(null);
                floatingButton.setText("+");
                floatingButton.setTooltip(
                        new Tooltip("Agregar ruta")
                );
                openFloatingButton();
                printRouteReport.setGraphic(new ImageView(new Image(getClass().getResource("img/printer.png").toExternalForm())));
                printRouteReport.setOnAction(event -> showDialogSelectReport());
            }
            if (addPane.isVisible())
                hideAddPane();
            if (bar.isVisible())
                hideMarkerBar(null);
            if (drawer.isShown())
                drawer.close();
            if (barEditRoute.isVisible())
                closeEditRoute();

            centerMap();
            filterField.clear();
        });

        floatingButton.setOnAction(eventAction -> {
            if (drawer.isShown()) {
                loadControlListView();
                drawerFilterField.clear();
                filterControlData();
                hideFloatingButton();
            } else {
                addLabel.setVisible(true);
                openAddPane();
            }
        });
    }

    private Integer getMeters(ControlPosition position1, ControlPosition position2) {

        double radioEarth = 6371000;
        double dLat = Math.toRadians(position1.getLatitude()
                - position2.getLatitude());
        double dLng = Math.toRadians(position1.getLongitude()
                - position2.getLongitude());
        double sLat = Math.sin(dLat / 2);
        double sLng = Math.sin(dLng / 2);
        double va1 = Math.pow(sLat, 2) + Math.pow(sLng, 2)
                * Math.cos(Math.toRadians(position2.getLatitude()))
                * Math.cos(Math.toRadians(position1.getLatitude()));
        double va2 = 2 * Math.atan2(Math.sqrt(va1), Math.sqrt(1 - va1));
        Double distance = radioEarth * va2;

        return distance.intValue();
    }


    public void loadPrintBasic(File file){
        dialogLoadingPrint();

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("route_name", selectedRoute.getName());
        parameters.put("created_date", RadarDate.getDateWithMonthAndTime(selectedRoute.getCreateDate()));
        parameters.put("route_id", selectedRoute.getId().toString());
        parameters.put("point_num", selectedRoute.getRoutePositions().size());

        List<RouteReport> routeReportList = new ArrayList<>();

        int count = 0;
        ControlPosition positionBefore = null;
        for(HBox hBoxDrawerData: drawerData) {

            RoutePosition routePosition = (RoutePosition) hBoxDrawerData.getUserData();
            RouteReport routeReport = new RouteReport();

            routeReport.setPoint_marker(routePosition.getControlPosition().getPlaceName());
            if (count == 0) {
                routeReport.setDistance("Punto inicial");
            } else {
                int meters = getMeters(routePosition.getControlPosition(), positionBefore);
                routeReport.setDistance("a "+meters+" metros de "+positionBefore.getPlaceName());
            }
            positionBefore = routePosition.getControlPosition();
            count++;
            routeReportList.add(routeReport);
        }

        ExecutorService executor = Executors.newFixedThreadPool(1);
        Runnable worker = new PrintReportRouteDetailsTask(this,
                new JRBeanCollectionDataSource(routeReportList),file,
                "Ruta_"+selectedRoute.getId().toString(), parameters);
        executor.execute(worker);
        executor.shutdown();
    }

    public void loadPrintFull(File file){
        dialogLoadingPrint();

        Map<String, Object> parameters = new HashMap<>();

        List <Group> groupList = service.getAllGroupByRoute(selectedRoute.getId());

        List<GroupMasterReport> groupMasterReports = new ArrayList<>();

        for (Group group: groupList) {
            GroupMasterReport groupMasterReport = new GroupMasterReport();
            groupMasterReport.setId(group.getId().toString());
            groupMasterReport.setGroup_name(group.getName());
            List<User> users = service.findUserByGroupId(group.getId());
            groupMasterReport.setEmp_num(String.valueOf(users.size()));
            List<GroupReport> groupReportList = new ArrayList<>();
            for (User user: users) {
                GroupReport groupReport = new GroupReport();
                groupReport.setDni(user.getDni());
                groupReport.setUser_name(user.getFullName());
                groupReport.setCompany_name(user.getCompany().getName());
                groupReportList.add(groupReport);
            }
            groupMasterReport.setGroupReportList(groupReportList);
            groupMasterReports.add(groupMasterReport);
        }

        parameters.put("route_name", selectedRoute.getName());
        parameters.put("created_date", RadarDate.getDateWithMonthAndTime(selectedRoute.getCreateDate()));
        parameters.put("route_id", selectedRoute.getId().toString());
        parameters.put("point_num", selectedRoute.getRoutePositions().size());

        List<RouteReport> routeReportList = new ArrayList<>();

        int count = 0;
        ControlPosition positionBefore = null;
        for(HBox hBoxDrawerData: drawerData) {

            RoutePosition routePosition = (RoutePosition) hBoxDrawerData.getUserData();
            RouteReport routeReport = new RouteReport();

            routeReport.setPoint_marker(routePosition.getControlPosition().getPlaceName());
            if (count == 0) {
                routeReport.setDistance("Punto inicial");
            } else {
                int meters = getMeters(routePosition.getControlPosition(), positionBefore);
                routeReport.setDistance("a "+meters+" metros de "+positionBefore.getPlaceName());
            }
            positionBefore = routePosition.getControlPosition();
            count++;
            routeReportList.add(routeReport);
        }

        parameters.put("total", String.valueOf(groupList.size()));

        ExecutorService executor = Executors.newFixedThreadPool(1);
        Runnable worker = new PrintReportGlobalDetailRoute(this,
                new JRBeanCollectionDataSource(groupMasterReports), new JRBeanCollectionDataSource(routeReportList), parameters, file,
                "Ruta_grupos_"+selectedRoute.getId().toString());
        executor.execute(worker);
        executor.shutdown();
    }

    public void printReport() {
        dialogType = Const.DIALOG_PRINT_BASIC;
        showDialogPrint("Debe seleccionar una ruta para guardar el reporte");
    }

    public void printReportFull() {
        dialogType = Const.DIALOG_PRINT_FULL;
        showDialogPrint("Debe seleccionar una ruta para guardar el reporte");
    }


    void closeEditRoute() {
        barEditRoute.setVisible(false);
        drawerListView.setVisible(true);
        controlListView.setVisible(false);
        if ((int) tabPane.getSelectionModel().getSelectedItem().getUserData() == 0) {
            loadDrawerListView();
            drawerFilterField.clear();
            filterDrawerData();
            openFloatingButton();
        }
    }

    void closeOpenDrawer() {
        drawer.close();
        floatingButton.setGraphic(null);
        floatingButton.setText("+");
        floatingButton.setTooltip(
                new Tooltip("Agregar ruta")
        );
        openFloatingButton();
    }

    void closeCreateRoute() {
        hideAddPane();
    }

    @FXML
    public void cancelAction(ActionEvent event) {
        if (barEditRoute.isVisible()) {
            closeEditRoute();
        } else {
            closeCreateRoute();
        }
    }

    @FXML
    public void saveAction(ActionEvent event) throws IOException {
        if (barEditRoute.isVisible()) {
            saveRoutePositions();
            closeEditRoute();
        } else {
            if (nameRouteField.getText().isEmpty()) {
                showSnackBar("El nombre no puede estar vacio");
            } else {
                Route route = new Route();
                route.setName(nameRouteField.getText());
                route.setCreateDate(new DateTime().getMillis());
                route.setLastUpdate(new DateTime().getMillis());
                route.setActive(true);
                service.saveRoute(route);
                hideAddPane();
                loadListView();
                showSnackBar("Ruta agregada con exito.");
            }

        }
    }

    private void saveRoutePositions() {
        service.deleteAllRPByRouteId(selectedRoute);
        for (ControlPosition control : controlData) {
            if (control.isSelected()) {
                RoutePosition rp = new RoutePosition();
                rp.setControlPosition(control);
                rp.setCreateDate(new DateTime().getMillis());
                rp.setRoute(selectedRoute);
                service.saveRoutePosition(rp);
            }
        }
        showSnackBar("Modificacion de ubicaciones de ruta completada");
    }

    public void openAddPane() {
        addPane.setVisible(true);
        nameRouteField.clear();
        ScaleTransition fadeTransition
                = new ScaleTransition(Duration.millis(300), addPane);
        fadeTransition.setFromX(0.0);
        fadeTransition.setFromY(0.0);
        fadeTransition.setToX(1.0);
        fadeTransition.setToY(1.0);
        fadeTransition.play();
        if (floatingButton.isVisible())
            hideFloatingButton();
    }

    public void hideAddPane() {
        ScaleTransition fadeTransition
                = new ScaleTransition(Duration.millis(300), addPane);
        fadeTransition.setFromX(1.0);
        fadeTransition.setFromY(1.0);
        fadeTransition.setToX(0.0);
        fadeTransition.setToY(0.0);
        fadeTransition.play();
        fadeTransition.setOnFinished(event -> addPane.setVisible(false));
        if ((int) tabPane.getSelectionModel().getSelectedItem().getUserData() == 0
                && !floatingButton.isVisible()) {
            openFloatingButton();
        }
    }

    public void openFloatingButton() {
        floatingButton.setVisible(true);
        ScaleTransition fadeTransition
                = new ScaleTransition(Duration.millis(300), floatingButton);
        fadeTransition.setFromX(0.0);
        fadeTransition.setFromY(0.0);
        fadeTransition.setToX(1.0);
        fadeTransition.setToY(1.0);
        fadeTransition.play();
    }

    public void hideFloatingButton() {
        ScaleTransition fadeTransition
                = new ScaleTransition(Duration.millis(300), floatingButton);
        fadeTransition.setFromX(1.0);
        fadeTransition.setFromY(1.0);
        fadeTransition.setToX(0.0);
        fadeTransition.setToY(0.0);
        fadeTransition.play();
        fadeTransition.setOnFinished(event -> floatingButton.setVisible(false));
    }

    public void openMarkerBar(ControlPosition control) {
        if ((int) tabPane.getSelectionModel().getSelectedItem().getUserData() == 1) {
            bar.setVisible(true);
            TranslateTransition fadeTransition
                    = new TranslateTransition(Duration.millis(500), bar);
            fadeTransition.setFromY(100);
            fadeTransition.setToY(0);
            fadeTransition.play();
            nameField.setText(control.getPlaceName());
            nameField.setDisable(true);
            editButton.setText("Editar");
            disableButton.setText("Desactivar");
            editing = false;

        } else {
            showSnackBar(control.getPlaceName());
        }
    }

    public void hideMarkerBar(ActionEvent actionEvent) {
        TranslateTransition fadeTransition
                = new TranslateTransition(Duration.millis(500), bar);
        fadeTransition.setFromY(0);
        fadeTransition.setToY(100);
        fadeTransition.play();
        fadeTransition.setOnFinished(event -> bar.setVisible(false));
    }

    public void disableControl(ActionEvent actionEvent) {
        if (editing) {
            nameField.setText(selectedMarker.getPlaceName());
            nameField.setDisable(true);
            editButton.setText("Editar");
            disableButton.setText("Desactivar");
            editing = false;
        } else {
            dialogType = Const.DIALOG_DISABLE;
            showDialog("Confirmacion",
                    "¿Estas seguro que deseas desactivar este punto de control?");
        }
    }

    public void editControl(ActionEvent actionEvent) {
        if (!editing) {
            nameField.setDisable(false);
            editButton.setText("Guardar");
            disableButton.setText("Cancelar");
            editing = true;
        } else {
            dialogType = Const.DIALOG_SAVE_EDIT;
            showDialog("Confirmacion",
                    "¿Estas seguro que deseas modificar el nombre de esta ubicacion?");
        }
    }

    public void enableControl() {
        dialogType = Const.DIALOG_ENABLE;
        showDialog("Confirmacion",
                "¿Estas seguro que deseas activar nuevamente este punto de control?");
    }

    public void deleteControl() {
        dialogType = Const.DIALOG_DELETE;
        showDialog("Confirmacion",
                "¿Estas seguro que deseas borrar este punto de control?");
    }

    private void deleteRoute() {
        dialogType = Const.DIALOG_DELETE;
        showDialog("Confirmacion",
                "¿Estas seguro que deseas borrar la ruta: "+selectedRoute.getName()+"?");
    }

    public void loadMarkerListView() throws IOException {

        markerList = service.getAllControl();

        markerData = FXCollections.observableArrayList();

        for (ControlPosition control: markerList) {

            HBox hBox = new HBox();
            HBox imageHBox = new HBox();
            VBox labelsVBox = new VBox();
            // ListCells
            Label nameLabel = new Label("   "+control.getPlaceName());
            nameLabel.setFont(new Font(null, 16));
            Label activeLabel  = new Label("   "+(control.getActive()?"Activo":"Desactivo"));
            activeLabel.setFont( new Font(null, 14));
            activeLabel.setTextFill(Color.valueOf("#aaaaaa"));
            ImageView guardImg = new ImageView(new Image(getClass()
                    .getResource("img/point_marker_64.png").toExternalForm()));
            if (!control.getActive()) {
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

            hBox.setUserData(control);
            markerData.addAll(hBox);

            if (!control.getActive()) {

                FXMLLoader loader = new FXMLLoader(getClass().getResource("view/popup_dual.fxml"));
                InputController inputController = new InputController(this);
                loader.setController(inputController);
                JFXPopup popup = new JFXPopup(loader.load());
                inputController.setPopup(popup);

                hBox.setOnMouseClicked(event -> {
                    if (event.getButton() == MouseButton.SECONDARY) {
                        hideMarkerBar(null);
                        popup.show(hBox, JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.LEFT);
                        selectedMarker = control;
                    }
                });
            }
        }
        markerListView.setItems(markerData);
        markerListView.setOnMouseClicked(this);
    }

    public void showDialogSelectReport() {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setResizable(false);
        dialogStage.setTitle("");
        dialogStage.getIcons().add(new Image(getClass().getResource("img/radar_splash.png").toExternalForm()));
        Button buttonReport = new Button("REPORTE RUTA");
        Button buttonReportFull = new Button("REPORTE COMPLETO");
        HBox hBox = HBoxBuilder.create()
                .spacing(10.0) //In case you are using HBoxBuilder
                .padding(new Insets(5, 5, 5, 5))
                .alignment(Pos.CENTER)
                .children(buttonReport, buttonReportFull)
                .build();
        hBox.maxWidth(120);
        dialogStage.setScene(new Scene(VBoxBuilder.create().spacing(15).
                children(new Text("Selecione el reporte a imprimir"), hBox).
                alignment(Pos.CENTER).padding(new Insets(20)).build()));

        buttonReport.setStyle("-fx-background-color: #039BE5; "
                + "-fx-text-fill: white;");
        buttonReport.setMinHeight(55);
        buttonReport.setMaxWidth(170);
        buttonReport.setOnAction((ActionEvent e) -> {
            dialogStage.close();
            printReport();
        });
        buttonReport.setOnMouseEntered((MouseEvent t) -> {
            buttonReport.setStyle("-fx-background-color: #E0E0E0; "
                    + "-fx-text-fill: white;");
        });
        buttonReport.setOnMouseExited((MouseEvent t) -> {
            buttonReport.setStyle("-fx-background-color: #039BE5; "
                    + "-fx-text-fill: white;");
        });

        buttonReportFull.setStyle("-fx-background-color: #039BE5; "
                + "-fx-text-fill: white;");
        buttonReportFull.setMinHeight(55);
        buttonReportFull.setMaxWidth(170);
        buttonReportFull.setOnAction((ActionEvent e) -> {
            dialogStage.close();
            printReportFull();
        });
        buttonReportFull.setOnMouseEntered((MouseEvent t) -> {
            buttonReportFull.setStyle("-fx-background-color: #E0E0E0; "
                    + "-fx-text-fill: white;");
        });
        buttonReportFull.setOnMouseExited((MouseEvent t) -> {
            buttonReportFull.setStyle("-fx-background-color: #039BE5; "
                    + "-fx-text-fill: white;");
        });
        dialogStage.show();
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

            FXMLLoader loader = new FXMLLoader(getClass().getResource("view/popup.fxml"));
            InputController inputController = new InputController(this);
            loader.setController(inputController);
            JFXPopup popup = new JFXPopup(loader.load());
            inputController.setPopup(popup);
            inputController.setText("Borrar");

            hBox.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.SECONDARY) {
                    popup.show(hBox, JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.LEFT);
                    selectedRoute = route;
                }
            });
        }

        routeListView.setItems(routeData);
        routeListView.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && !addPane.isVisible()
                    && routeListView.getSelectionModel().getSelectedItem() != null) {
                selectedRoute = (Route) routeListView
                        .getSelectionModel().getSelectedItem().getUserData();
                openedDrawer();
                drawerFilterField.clear();
                filterDrawerData();
            }
        });
    }

    public void loadControlListView() {

        controlList = service.getAllControlActive();

        controlData = FXCollections.observableArrayList();

        for (ControlPosition control : controlList) {
            control.setSelected(false);
            for (RoutePosition position : drawerList) {
                if (control.getId().equals(position.getControlPosition().getId())) {
                    control.setSelected(true);
                }
            }
        }

        controlData.addAll(controlList);

        controlListView.setItems(controlData);

        controlListView.setCellFactory(lv -> {
            ListCell<ControlPosition> cell = new ListCell<ControlPosition>() {
                @Override
                protected void updateItem(ControlPosition control, boolean empty) {
                    super.updateItem(control, empty);
                    if (empty) {
                        setGraphic(null);
                        setStyle(null);
                    } else {
                        Label label = createLabelForCell(control);
                        setGraphic(label);
                        if (control.isSelected()) {
                            getGraphic().setStyle("-fx-text-fill: white");
                            setStyle("-fx-background-color: #03A9F4;");
                        } else {
                            setStyle("-fx-background-color: white;");
                            getGraphic().setStyle("-fx-text-fill: black");
                        }
                    }
                }
            };

            cell.setOnMouseClicked(e -> {
                if (cell.getItem() != null) {
                    /*if (cell.getItem().isSelected()) {
                        cell.setStyle("-fx-background-color: white;");
                        cell.getGraphic().setStyle("-fx-text-fill: black");
                        cell.getItem().setSelected(false);
                    } else {
                        cell.getGraphic().setStyle("-fx-text-fill: white");
                        cell.setStyle("-fx-background-color: #03A9F4;");
                        cell.getItem().setSelected(true);
                    }*/
                    onClickMarker(controlData.indexOf(cell.getItem()));
                }
            });
            return cell ;
        });
        controlListView.setVisible(true);
        drawerListView.setVisible(false);
        showSnackBar("Marcar/desmarcar las ubicaciones para agregar/quitar de la ruta");
        barEditRoute.setVisible(true);
        addMarkers(controlList, null);
    }

    Label createLabelForCell(ControlPosition control) {

        Label nameLabel = new Label("   "+control.getPlaceName());
        nameLabel.setFont(new Font(null, 14));
        ImageView iconImage = new ImageView(new Image(getClass()
                .getResource("img/point_marker_64.png").toExternalForm()));
        iconImage.setFitHeight(30);
        iconImage.setFitWidth(30);

        nameLabel.setGraphic(iconImage);
        nameLabel.setPadding(new Insets(4));
        nameLabel.setUserData(control);
        return nameLabel;
    }

    public void openedDrawer() {

        drawer.open();
        drawer.setVisible(true);

        Route route = (Route) routeListView.getSelectionModel().getSelectedItem().getUserData();

        if(drawerFirstShow)
            createDrawer();

        drawerNameLabel.setText("   "+route.getName());

        loadDrawerListView();

        drawerListView.setExpanded(true);
        drawerListView.setVerticalGap(1.0);
        drawerListView.depthProperty().set(1);

        ImageView editImage = new ImageView(new Image(getClass()
                .getResource("img/pencil_edit_16.png").toExternalForm()));
        floatingButton.setText("");
        floatingButton.setGraphic(editImage);
        floatingButton.setTooltip(
                new Tooltip("Editar Ruta")
        );
        openFloatingButton();

        drawerListView.setOnMouseClicked(event -> {
            RoutePosition control = (RoutePosition)
                    drawerListView.getSelectionModel().getSelectedItem().getUserData();
            centerMap(control.getControlPosition());

        });
    }

    void loadDrawerListView() {

        drawerData = FXCollections.observableArrayList();

        drawerList = service.findAllRPByRouteId(selectedRoute);

        for (RoutePosition routePosition: drawerList) {

            HBox wDetail = new HBox();
            Label positionLabel = new Label();
            ImageView iconImage = new ImageView(new Image(getClass()
                    .getResource("img/circle_marker_64.png").toExternalForm()));
            iconImage.setFitHeight(30);
            iconImage.setFitWidth(30);
            positionLabel.setGraphic(iconImage);
            positionLabel.setText("   "+routePosition.getControlPosition().getPlaceName());
            wDetail.getChildren().add(positionLabel);
            wDetail.setUserData(routePosition);
            drawerData.add(wDetail);
        }

        drawerListView.setItems(drawerData);

        addMarkers(drawerList);
    }

    public void createDrawer() {
        drawerNameLabel = new Label();

        ImageView iconImage = new ImageView(new Image(getClass()
                .getResource("img/icon_multiple_marker_64.png").toExternalForm()));
        iconImage.setFitHeight(40);
        iconImage.setFitWidth(40);
        drawerNameLabel.setGraphic(iconImage);
        drawerNameLabel.setFont(new Font(null, 16));
        VBox watchLabelsVBox = new VBox();
        watchLabelsVBox.getChildren().addAll(drawerNameLabel);
        headHBox.getChildren().addAll(watchLabelsVBox);
        drawerFirstShow = false;
    }

    @Override
    public void handle(MouseEvent event) {

        if (event.getButton() == MouseButton.PRIMARY
                && markerListView.getSelectionModel().getSelectedItem() != null) {

            ControlPosition control = (ControlPosition)
                    markerListView.getSelectionModel().getSelectedItem().getUserData();
            if (control.getActive()) {
                selectedMarker = control;
                openMarkerBar(control);
                centerMap(control);
            } else {
                hideMarkerBar(null);
                centerMap(control);
            }

        } else if (bar.isVisible()) {
            hideMarkerBar(null);
        }
    }

    @Override
    protected void onBackController() {
        if (barEditRoute.isVisible()) {
            closeEditRoute();
        } else if (addPane.isVisible()) {
            hideAddPane();
        } else if (drawer.isShown()) {
            closeOpenDrawer();
        } else {
            super.onBackController();
        }
    }

    /*@FXML
    public void onBackPress(ActionEvent actionEvent) {
        if (barEditRoute.isVisible()) {
            closeEditRoute();
        } else if (addPane.isVisible()) {
            hideAddPane();
        } else if (drawer.isShown()) {
            closeOpenDrawer();
        } else {
            onBackController();
        }
    }*/

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
                hideMarkerBar(null);
            }
        });

    }

    public void addMarkers() {

        if (isMapReady) {

            map.clearMarkers();

            markers = new ArrayList<>();
            markersOptions = new ArrayList<>();

            for (ControlPosition control : markerList) {

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
                        openMarkerBar(control);
                        selectedMarker = control;
                    });
                }
            }
        }

        centerMap();
    }

    public void addMarkers(List<RoutePosition> routePositions) {

        if (isMapReady) {

            map.clearMarkers();

            markers = new ArrayList<>();
            markersOptions = new ArrayList<>();

            for (RoutePosition position : routePositions) {

                LatLong latLong = new LatLong(position.getControlPosition().getLatitude(), position.getControlPosition().getLongitude());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLong);
                markerOptions.animation(Animation.DROP);
                markerOptions.icon("red_marker_32.png");
                Marker marker = new Marker(markerOptions);
                marker.setTitle(position.getControlPosition().getPlaceName());
                map.addMarker(marker);
                markers.add(marker);
                markersOptions.add(markerOptions);

                map.addUIEventHandler(marker, UIEventType.click, (JSObject obj) -> {
                    openMarkerBar(position.getControlPosition());
                });
            }
        }
        centerMap(routePositions);
    }

    public void addMarkers(List<ControlPosition> list, String text) {

        if (isMapReady) {

            map.clearMarkers();

            markers = new ArrayList<>();
            markersOptions = new ArrayList<>();

            for (ControlPosition controlPosition : list) {

                LatLong latLong = new LatLong(controlPosition.getLatitude(),
                        controlPosition.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLong);
                markerOptions.animation(Animation.DROP);
                if (controlPosition.isSelected())
                    markerOptions.icon("blue_marker_32.png");
                else
                    markerOptions.icon("red_marker_32.png");
                Marker marker = new Marker(markerOptions);
                marker.setTitle(controlPosition.getPlaceName());
                marker.getJSObject().setMember("marker", list.indexOf(controlPosition));
                map.addMarker(marker);
                markers.add(marker);
                markersOptions.add(markerOptions);

                map.addUIEventHandler(marker, UIEventType.click, (JSObject obj) -> {
                    openMarkerBar(controlPosition);
                    onClickMarker((int) marker.getJSObject().getMember("marker"));

                });
            }
        }
        centerMap(list, null);
    }

    public void onClickMarker(Integer index) {

        ControlPosition controlPosition = controlData.get(index);
        controlPosition.setSelected(!controlPosition.isSelected());
        LatLong latLong = new LatLong(controlPosition.getLatitude(),
                controlPosition.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLong);
        markerOptions.animation(Animation.DROP);
        if (controlPosition.isSelected())
            markerOptions.icon("blue_marker_32.png");
        else
            markerOptions.icon("red_marker_32.png");
        Marker marker = new Marker(markerOptions);
        marker.setTitle(controlPosition.getPlaceName());
        marker.getJSObject().setMember("marker", index);
        map.removeMarker(markers.get(index));
        map.addMarker(marker);
        markers.set(index, marker);
        markersOptions.set(index, markerOptions);

        map.addUIEventHandler(marker, UIEventType.click, (JSObject obj) -> {
            openMarkerBar(controlPosition);
            onClickMarker((int) marker.getJSObject().getMember("marker"));

        });

        controlListView.refresh();
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

    private void centerMap(List<ControlPosition> list, String text) {
        if (isMapReady) {
            if (list.isEmpty())
                return;

            LatLongBounds latLongBounds = new LatLongBounds();
            for (ControlPosition control : list) {
                LatLong latLong = new LatLong(control.getLatitude(),
                        control.getLongitude());
                latLongBounds.extend(latLong);
            }
            map.fitBounds(latLongBounds);
        }
    }

    public void centerMap() {
        if (isMapReady) {
            if (markerList.isEmpty())
                return;

            LatLongBounds latLongBounds = new LatLongBounds();
            for (ControlPosition control : markerList) {
                if (control.getActive()) {
                    LatLong latLong = new LatLong(control.getLatitude(), control.getLongitude());
                    latLongBounds.extend(latLong);
                }
            }
            map.fitBounds(latLongBounds);
        }
    }

    public void centerMap(LatLong latLong) {
        if (isMapReady) {
            LatLongBounds latLongBounds = new LatLongBounds();
            latLongBounds.extend(latLong);
            map.fitBounds(latLongBounds);
        }
    }

    public void centerMap(ControlPosition control) {
        if (isMapReady) {
            LatLong latLong = new LatLong(control.getLatitude(), control.getLongitude());
            LatLongBounds latLongBounds = new LatLongBounds();
            latLongBounds.extend(latLong);
            map.fitBounds(latLongBounds);
        }
    }

    public void centerMapEcuador() {
        if (isMapReady) {

            mapView.setCenter(-2.0000000,
                    -77.5000000);
            mapView.setZoom(14);
        }
    }

    public void onDialogAccept(ActionEvent actionEvent) {
        super.onDialogAccept(actionEvent);

        service.setListener(null);

        ControlPosition control;
        Route route;

        boolean reset = true;

        switch (dialogType) {
            case Const.DIALOG_PRINT_BASIC: {
                File file = selectDirectory();
                if (file != null)
                    loadPrintBasic(file);
                reset = false;
            }  break;
            case Const.DIALOG_PRINT_FULL: {
                File file = selectDirectory();
                if (file != null)
                    loadPrintFull(file);
                reset = false;
            }   break;
            case Const.DIALOG_SAVE_EDIT:
                control = service.findCPById(selectedMarker.getId());
                control.setPlaceName(nameField.getText());
                service.doEdit();
                break;
            case Const.DIALOG_DISABLE:
                control = service.findCPById(selectedMarker.getId());
                control.setActive(false);
                service.doEdit();
                break;
            case Const.DIALOG_ENABLE:
                control = service.findCPById(selectedMarker.getId());
                control.setActive(true);
                service.doEdit();
                break;
            case Const.DIALOG_DELETE:
                service.setListener(this);
                if ((int) tabPane.getSelectionModel().getSelectedItem().getUserData() == 1) {
                    control = service.findCPById(selectedMarker.getId());
                    service.deleteControl(control);
                } else if ((int) tabPane.getSelectionModel().getSelectedItem().getUserData() == 0) {
                    route = service.findRouteById(selectedRoute.getId());
                    service.deleteRoute(route);
                }
                break;
        }

        if (reset) {
            selectedMarker = null;
            selectedRoute = null;
            hideMarkerBar(null);
            loadListView();
            addMarkers();
        }
    }

    private void filterMarkerData() {
        FilteredList<HBox> filteredData = new FilteredList<>(markerData, p -> true);
        filterField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(hBox -> {
                // If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                ControlPosition position = (ControlPosition) hBox.getUserData();
                if (position == null)
                    return false;
                // Compare first name and last name of every person with filter text.
                String lowerCaseFilter = filterField.getText().toLowerCase();

                if (position.getPlaceName().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches first name.
                }
                return false; // Does not match.
            });
        });

        SortedList<HBox> sortedData = new SortedList<>(filteredData);
        markerListView.setItems(sortedData);
        checkFilterMarker(filteredData);
    }

    void checkFilterMarker(FilteredList<HBox> filteredData) {
        filteredData.setPredicate(hBox -> {
            // If filter text is empty, display all persons.
            if (filterField.getText() == null || filterField.getText().isEmpty()) {
                return true;
            }

            ControlPosition position = (ControlPosition) hBox.getUserData();
            if (position == null)
                return false;
            // Compare first name and last name of every person with filter text.
            String lowerCaseFilter = filterField.getText().toLowerCase();

            if (position.getPlaceName().toLowerCase().contains(lowerCaseFilter)) {
                return true; // Filter matches first name.
            }
            return false; // Does not match.
        });
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

                ControlPosition position = ((RoutePosition) hBox.getUserData())
                        .getControlPosition();
                if (position == null)
                    return false;
                // Compare first name and last name of every person with filter text.
                String lowerCaseFilter = drawerFilterField.getText().toLowerCase();

                if (position.getPlaceName().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches first name.
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

            ControlPosition position = ((RoutePosition) hBox.getUserData())
                    .getControlPosition();
            if (position == null)
                return false;
            // Compare first name and last name of every person with filter text.
            String lowerCaseFilter = drawerFilterField.getText().toLowerCase();

            if (position.getPlaceName().toLowerCase().contains(lowerCaseFilter)) {
                return true; // Filter matches first name.
            }
            return false; // Does not match.
        });
    }

    private void filterControlData() {
        FilteredList<ControlPosition> filteredData = new FilteredList<>(controlData, p -> true);
        drawerFilterField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(control -> {
                // If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                if (control == null)
                    return false;
                // Compare first name and last name of every person with filter text.
                String lowerCaseFilter = drawerFilterField.getText().toLowerCase();

                if (control.getPlaceName().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches first name.
                }
                return false; // Does not match.
            });
        });

        SortedList<ControlPosition> sortedData = new SortedList<>(filteredData);
        controlListView.setItems(sortedData);
        checkFilterControl(filteredData);
    }

    void checkFilterControl(FilteredList<ControlPosition> filteredData) {
        filteredData.setPredicate(control -> {
            // If filter text is empty, display all persons.
            if (drawerFilterField.getText() == null || drawerFilterField.getText().isEmpty()) {
                return true;
            }

            if (control == null)
                return false;
            // Compare first name and last name of every person with filter text.
            String lowerCaseFilter = drawerFilterField.getText().toLowerCase();

            if (control.getPlaceName().toLowerCase().contains(lowerCaseFilter)) {
                return true; // Filter matches first name.
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
    public void onError(String error) {
        new Timer().schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        cancel();
                        Platform.runLater(() -> {
                            showDialogNotification("Error", error);
                        });
                    }
                }, 500, 500
        );
    }

    @Override
    public void onSuccess(String message) {
        new Timer().schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        cancel();
                        Platform.runLater(() -> {
                            showSnackBar(message);
                        });
                    }
                }, 500, 500
        );
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

    public class InputController {

        @FXML
        private JFXListView<?> toolbarPopupList;

        @FXML
        private Label popupLabel;

        @FXML
        private Label popupLabel_delete;

        private MarkerController principal;

        private JFXPopup popup;

        public InputController(MarkerController principal) {
            this.principal = principal;
        }

        // close application
        @FXML
        private void submit() {
            popup.hide();
            if (toolbarPopupList.getSelectionModel().getSelectedIndex() == 0) {
                if (popupLabel.getText().equals("Borrar"))
                    principal.deleteRoute();
                else
                    principal.enableControl();
            } else if (toolbarPopupList.getSelectionModel().getSelectedIndex() == 1) {
                principal.deleteControl();
            }
        }

        public void setPopup(JFXPopup popup) {
            this.popup = popup;
        }

        public void setText(String content) {
            popupLabel.setText(content);
        }
    }
}

