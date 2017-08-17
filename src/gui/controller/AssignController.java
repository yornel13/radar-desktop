package gui.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.MapReadyListener;
import com.lynden.gmapsfx.javascript.event.UIEventType;
import com.lynden.gmapsfx.javascript.object.*;
import io.datafx.controller.ViewController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import model.Group;
import model.Route;
import model.RoutePosition;
import model.User;
import netscape.javascript.JSObject;
import util.Const;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static javafx.scene.paint.Color.valueOf;

@ViewController("../view/assign.fxml")
public class AssignController extends BaseController implements MapComponentInitializedListener,
        MapReadyListener, EventHandler<MouseEvent> {

    @FXML
    private JFXButton addButton;

    @FXML
    private JFXButton editButton;

    @FXML
    private JFXButton cancelEditButton;

    @FXML
    private StackPane stackPane;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private Pane selectRoutePane;

    @FXML
    private Pane barPane;

    @FXML
    private JFXTextField filterField;

    @FXML
    private JFXTextField filterRouteField;

    @FXML
    private JFXListView<HBox> userListView;

    private ObservableList<HBox> dataUser;

    private List<User> users;

    @FXML
    private Label groupLabel;

    /****************Group*****************/
    @FXML
    private JFXListView<HBox> groupListView;

    private ObservableList<HBox> groupData;

    private List<Group> groups;

    private Group selectedGroup;

    /****************Routes*****************/
    @FXML
    private JFXListView<Label> routeListView;

    private ObservableList<Label> routeData;

    private List<Route> routeList;

    private Route selectedRoute;

    @FXML
    private Pane showRoutePane;

    @FXML
    private Label routeLabel;

    ///////////// Map ////////////////////
    @FXML
    private GoogleMapView mapView;

    private List<Marker> markers;

    private List<MarkerOptions> markersOptions;

    private GoogleMap map;

    public boolean isMapReady = false;

    @PostConstruct
    public void init() throws FileNotFoundException {

        setTitle("Asignar Rutas");
        setBackButtonImageBlack();

        loadListView();
        mapView.addMapInializedListener(this);
        mapView.addMapReadyListener(this);
    }

    @Override
    protected void onBackController() {
        barPane.setEffect(null);
        super.onBackController();
    }

    public void loadListView() {
        try {
            loadGroupListView();
            filterGroup();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadUserListView() throws IOException {

        users = service.getAllUserActive();

        dataUser = FXCollections.observableArrayList();

        for(User user: users) {
            HBox parentHBox = new HBox();
            HBox imageHBox = new HBox();
            VBox nameDniVBox = new VBox();

            ImageView iconUser = new ImageView(new Image(
                    new FileInputStream("src/img/policeman_64.png")));
            iconUser.setFitHeight(55);
            iconUser.setFitWidth(55);
            Label fullNameUser = new Label("    "+user.getLastname()+"  "+user.getName());
            Label dniUser = new Label("    "+user.getDni());
            fullNameUser.setFont(new Font(null,16));
            dniUser.setFont(new Font(null,14));
            dniUser.setTextFill(valueOf("#aaaaaa"));
            imageHBox.getChildren().add(iconUser);
            nameDniVBox.getChildren().add(fullNameUser);
            nameDniVBox.getChildren().add(dniUser);
            parentHBox.getChildren().addAll(imageHBox, nameDniVBox);

            parentHBox.setUserData(user);
            dataUser.add(parentHBox);
        }
        userListView.setItems(dataUser);
        userListView.setExpanded(true);
        userListView.setVerticalGap(2.0);
        userListView.depthProperty().set(1);
    }

    private void loadGroupListView() throws FileNotFoundException {

        groups = service.getAllGroup();
        groupData = FXCollections.observableArrayList();

        for (Group group: groups) {

            HBox parentHBox = new HBox();
            HBox imageHBox = new HBox();
            VBox groupNameHBox = new VBox();

            ImageView iconGroup = new ImageView(new Image(new FileInputStream("src/img/group1_64.png")));
            iconGroup.setFitHeight(55);
            iconGroup.setFitWidth(55);
            Label groupNameLabel = new Label("    "+group.getName());
            groupNameLabel.setFont(new Font(null,16));
            Label usersLabel = new Label();
            Integer number = service.findUserByGroupId(group.getId()).size();
            if (number == 0) {
                usersLabel.setText("     Sin empleados");
            } else if (number == 1) {
                usersLabel.setText("     1 empleado");
            } else {
                usersLabel.setText("     "+number+" empleados");
            }
            usersLabel.setFont(new Font(null,12));
            usersLabel.setTextFill(valueOf("#aaaaaa"));

            imageHBox.getChildren().add(iconGroup);
            groupNameHBox.getChildren().addAll(groupNameLabel, usersLabel);
            parentHBox.getChildren().addAll(imageHBox, groupNameHBox);
            parentHBox.setUserData(group);
            groupData.add(parentHBox);
        }
        groupListView.setItems(groupData);

        groupListView.setOnMouseClicked(this);

        addButton.setOnAction(event -> {
            addButton.setVisible(false);
            selectRoutePane.setVisible(true);

            try {
                loadRouteListView();
                filterRoute();
            } catch (IOException e) {
                e.printStackTrace();
            }

        });

        editButton.setOnAction(event -> {
            showRoutePane.setVisible(false);
            selectRoutePane.setVisible(true);
            try {
                loadRouteListView();
                filterRoute();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        cancelEditButton.setOnAction(event -> {
            handle(clickPrimaryMouseButton());
        });
    }

    @Override
    public void handle(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY
                && groupListView.getSelectionModel().getSelectedItem() != null) {
            selectedGroup = (Group) groupListView.getSelectionModel().getSelectedItem().getUserData();
            groupLabel.setText(selectedGroup.getName());

            if (selectRoutePane.isVisible())
                selectRoutePane.setVisible(false);

            if (showRoutePane.isVisible())
                showRoutePane.setVisible(false);

            if (selectedGroup.getRoute() == null) {
                addButton.setVisible(true);
            } else {
                addButton.setVisible(false);
                showRoutePane.setVisible(true);
                routeLabel.setText("Ruta: "+selectedGroup.getRoute().getName());
                addMarkers();
            }
        }
    }

    public void loadRouteListView() throws IOException {

        routeList = service.getAllRoute();

        routeData = FXCollections.observableArrayList();

        for (Route route: routeList) {

            // ListCells
            Label nameLabel = new Label("   "+route.getName());
            nameLabel.setFont(new Font(null, 16));
            ImageView guardImg = new ImageView(new Image(new FileInputStream("src/img/route_marker_64.png")));
            if (!route.getActive()) {
                ColorAdjust desaturate = new ColorAdjust();
                desaturate.setSaturation(-1);
                guardImg.setEffect(desaturate);
            }
            guardImg.setFitHeight(40);
            guardImg.setFitWidth(40);
            nameLabel.setGraphic(guardImg);
            nameLabel.setUserData(route);
            routeData.add(nameLabel);
        }
        routeListView.setItems(routeData);
        routeListView.setOnMouseClicked(event -> {

            if (event.getButton() == MouseButton.PRIMARY) {
                selectedRoute = (Route) routeListView
                        .getSelectionModel().getSelectedItem().getUserData();
                dialogType = Const.DIALOG_SAVE;
                showDialog("Confirmacion", "¿Estas seguro que deseas seleccionar esta ruta para el grupo?");
            }
        });
    }

    @Override
    public void onDialogAccept(ActionEvent actionEvent) {
        super.onDialogAccept(actionEvent);
        switch (dialogType) {
            case Const.DIALOG_SAVE:
                Group group = service.findGroupById(selectedGroup.getId());
                group.setRoute(service.findRouteById(selectedRoute.getId()));
                service.doEdit();
                showSnackBar("Ruta asignada");
                handle(clickPrimaryMouseButton());
                break;
        }
    }

    private void filterGroup() {
        FilteredList<HBox> filteredData = new FilteredList<>(groupData, p -> true);
        filterField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(hBox -> {
                // If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                Group group = (Group) hBox.getUserData();
                if (group == null)
                    return false;
                // Compare first name and last name of every person with filter text.
                String lowerCaseFilter = filterField.getText().toLowerCase();

                if (group.getName().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches first name.
                }
                return false; // Does not match.
            });
        });

        SortedList<HBox> sortedData = new SortedList<>(filteredData);
        groupListView.setItems(sortedData);
        checkFilter(filteredData);
    }

    void checkFilter(FilteredList<HBox> filteredData) {
        filteredData.setPredicate(hBox -> {
            // If filter text is empty, display all persons.
            if (filterField.getText() == null || filterField.getText().isEmpty()) {
                return true;
            }

            Group group = (Group) hBox.getUserData();
            if (group == null)
                return false;
            // Compare first name and last name of every person with filter text.
            String lowerCaseFilter = filterField.getText().toLowerCase();

            if (group.getName().toLowerCase().contains(lowerCaseFilter)) {
                return true; // Filter matches first name.
            }
            return false; // Does not match.
        });
    }

    private void filterRoute() {
        FilteredList<Label> filteredData = new FilteredList<>(routeData, p -> true);
        filterRouteField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(label -> {
                // If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                Route route = (Route) label.getUserData();
                if (route == null)
                    return false;
                // Compare first name and last name of every person with filter text.
                String lowerCaseFilter = filterRouteField.getText().toLowerCase();

                if (route.getName().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches first name.
                }
                return false; // Does not match.
            });
        });

        SortedList<Label> sortedData = new SortedList<>(filteredData);
        routeListView.setItems(sortedData);
        checkRouteFilter(filteredData);
    }

    void checkRouteFilter(FilteredList<Label> filteredData) {
        filteredData.setPredicate(label -> {
            // If filter text is empty, display all persons.
            if (filterRouteField.getText() == null || filterRouteField.getText().isEmpty()) {
                return true;
            }

            Route route = (Route) label.getUserData();
            if (route == null)
                return false;
            // Compare first name and last name of every person with filter text.
            String lowerCaseFilter = filterRouteField.getText().toLowerCase();

            if (route.getName().toLowerCase().contains(lowerCaseFilter)) {
                return true; // Filter matches first name.
            }
            return false; // Does not match.
        });
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

        addMarkers();
    }

    @Override
    public void mapReady() {
        isMapReady = true;
    }

    public void addMarkers() {

        if (isMapReady) {

            map.clearMarkers();

            markers = new ArrayList<>();
            markersOptions = new ArrayList<>();

            List<RoutePosition> routePositions = service.findAllRPByRouteId(selectedGroup.getRoute());

            for (RoutePosition position : routePositions) {
                LatLong latLong = new LatLong(position.getControlPosition().getLatitude(),
                        position.getControlPosition().getLongitude());
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
                    showSnackBar(position.getControlPosition().getPlaceName());
                });
            }

            centerMap(routePositions);
        }
    }

    private void centerMap(List<RoutePosition> routePositions) {
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
