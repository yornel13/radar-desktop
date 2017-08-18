package gui.controller;

import com.jfoenix.controls.*;
import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.event.UIEventType;
import com.lynden.gmapsfx.javascript.object.*;
import io.datafx.controller.ViewController;
import io.datafx.controller.flow.FlowException;
import io.datafx.controller.util.VetoException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
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
import model.Position;
import model.User;
import model.Watch;
import netscape.javascript.JSObject;
import org.joda.time.DateTime;
import util.RadarDate;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@ViewController(value = "../view/control.fxml")
public class ControlController  extends BaseController implements MapComponentInitializedListener, EventHandler<MouseEvent> {

    /*************USERS****************/
    @FXML
    private JFXTextField userFilterField;
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
    private JFXListView<HBox> markerListView;
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

    private boolean drawerWatchFirstShow = true;
    private boolean drawerMarkerFirstShow = true;

    public boolean isMapReady = false;


    @PostConstruct
    public void init() throws FileNotFoundException {

        setTitle("Control de Guardias - Por ubicacion");
        setBackButtonImage();

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
        showMarker();
        createDateFilter();
    }

    private void createDateFilter() {
        try {
            searchButton.setGraphic(new ImageView(
                    new Image(new FileInputStream("src/img/search_green_16.png"))));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        searchButton.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                try {
                    Date from = Date.valueOf(fromPicker.getValue());
                    Date to = Date.valueOf(toPicker.getValue());
                    if (to.before(from)) {
                        showDialogNotification("Error", "Rango de fechas incorrecto");
                        return;
                    }
                    if (isMapReady)
                        addMarkers();
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
            ImageView guardImg = null;
            try {
                guardImg = new ImageView(new Image(
                        new FileInputStream("src/img/point_marker_64.png")));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
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
            setUserFilterField();
        });
        watchDrawer.setOnDrawerOpened(event -> {
            setWatchFilterField();
        });
        ///////////////////////////////////////////////////////////////////////////
        markerHeadHBox.setStyle("-fx-background-color: #ffffff");
        markerHeadHBox.setPrefWidth(275);
        markerHeadHBox.setPadding(new Insets(6));

        markerDrawer.setSidePane(markerDrawerBox);
        markerDrawer.setVisible(false);
        markerDrawer.setOnDrawerClosed(event  ->  {
            markerDrawer.setVisible(false);
            setWatchFilterField();
        });
        markerDrawer.setOnDrawerOpened(event -> {
            setMarkerFilterField();
        });
    }

    public void setUserFilterField() {
        userFilterField.setVisible(true);
        watchFilterField.setVisible(false);
        markerFilterField.setVisible(false);

        watchFilterField.clear();
        markerFilterField.clear();
    }

    public void setWatchFilterField() {
        userFilterField.setVisible(false);
        watchFilterField.setVisible(true);
        markerFilterField.setVisible(false);

        markerFilterField.clear();
    }

    public void setMarkerFilterField() {
        userFilterField.setVisible(false);
        watchFilterField.setVisible(false);
        markerFilterField.setVisible(true);
    }

    @Override
    public void handle(MouseEvent event) {

        if (event.getButton() == MouseButton.PRIMARY
                && controlListView.getSelectionModel()
                .getSelectedItem().getUserData() != null) {
            openedWatchDrawer();
        }
    }

    @Override
    protected void onBackController() {
        if (markerDrawer.isShown()) {
            markerDrawer.close();
            addMarkers();
        } else if (watchDrawer.isShown()) {
            watchDrawer.close();
            addMarkers();
        } else {
            super.onBackToMain();
        }
    }

    @FXML
    public void changeListView(ActionEvent event) throws VetoException, FlowException {
        actionHandler.handle("workman");
    }

    public void createWatchDrawer() {
        Label iconHeader = new Label();

        ImageView iconImage;
        try {
            iconImage = new ImageView(
                    new Image(new FileInputStream("src/img/point_marker_64.png")));
            iconImage.setFitHeight(40);
            iconImage.setFitWidth(40);
            iconHeader.setGraphic(iconImage);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
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

        ControlPosition control = (ControlPosition) controlListView.getSelectionModel().getSelectedItem().getUserData();
        centerMap(new LatLong(control.getLatitude(), control.getLongitude()));

        if(drawerWatchFirstShow)
            createWatchDrawer();

        watchNameLabel.setText("   "+control.getPlaceName());
        watchDniLabel.setText("     "+(control.getActive()?"Activo":"Desactivo"));

        watchData = FXCollections.observableArrayList();

        positionsUser = service.findAllPositionsByControl(control);

        for (Position position: positionsUser) {

            HBox hBox = new HBox();
            VBox labelsVBox = new VBox();
            Label nameLabel = new Label("   "+position.getWatch().getUser().getLastname()
                    +" "+position.getWatch().getUser().getName());
            nameLabel.setFont(new Font(null, 14));
            Label timeLabel  = new Label("   "+RadarDate
                    .getFechaConMesYHora(position.getTime()));
            timeLabel.setFont( new Font(null, 12));
            timeLabel.setTextFill(Color.valueOf("#aaaaaa"));
            ImageView iconImage = null;
            try {
                iconImage = new ImageView(
                        new Image(new FileInputStream("src/img/icon_multiple_marker_64.png")));
                iconImage.setFitHeight(30);
                iconImage.setFitWidth(30);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            labelsVBox.getChildren().addAll(nameLabel, timeLabel);
            hBox.getChildren().addAll(iconImage, labelsVBox);

            hBox.setUserData(position);
            watchData.add(hBox);

        }

        watchListView.setItems(watchData);
        watchListView.setExpanded(true);
        watchListView.setVerticalGap(2.0);
        watchListView.depthProperty().set(1);

        //filterWatch();*/
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
                openedMarkersDrawer();
            }
        });
    }

    public void openedMarkersDrawer() {

        markerDrawer.open();
        markerDrawer.setVisible(true);

        Watch watch = (Watch) watchListView.getSelectionModel()
                .getSelectedItem().getUserData();

        if(drawerMarkerFirstShow)
            createMarkerDrawer();

        markerTimeLabel.setText("   "+RadarDate
                .getFechaConMes(new DateTime(watch.getStartTime())));

        markerData = FXCollections.observableArrayList();

        positionWatch = service.findAllPositionsByWatch(watch);

        for (Position position: positionWatch) {

            HBox hBox = new HBox();
            VBox labelsVBox = new VBox();
            Label placeLabel = new Label("   "+position.getControlPosition().getPlaceName());
            placeLabel.setFont(new Font(null, 14));
            Label timeLabel  = new Label("   "+RadarDate
                    .getHora(position.getTime()));
            timeLabel.setFont( new Font(null, 12));
            timeLabel.setTextFill(Color.valueOf("#aaaaaa"));
            ImageView iconImage = null;
            try {
                iconImage = new ImageView(
                        new Image(new FileInputStream("src/img/marker_in_map_64.png")));
                iconImage.setFitHeight(40);
                iconImage.setFitWidth(40);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            labelsVBox.getChildren().addAll(placeLabel, timeLabel);
            hBox.getChildren().addAll(iconImage, labelsVBox);

            hBox.setUserData(position);
            markerData.add(hBox);
        }
        addMarkersRoute();

        markerListView.setItems(markerData);
        markerListView.setExpanded(true);
        markerListView.setVerticalGap(2.0);
        markerListView.depthProperty().set(1);

        filterMarker();
    }

    private void showMarker() {
        markerListView.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY
                    && markerListView.getSelectionModel()
                    .getSelectedItem().getUserData() != null) {
                Position position = (Position)
                        markerListView.getSelectionModel().getSelectedItem().getUserData();
                LatLong latLong = new LatLong(position.getControlPosition().getLatitude(),
                        position.getControlPosition().getLongitude());
                centerMap(latLong);
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

    public void addMarkersRoute() {
        for (Position position: positionWatch) {

            /*ControlPosition control = null;
            for (ControlPosition controlPosition: controlList) {
                if (position.getControlPosition().getId().equals(controlPosition.getId())) {
                    control = controlPosition;
                }
            }

            if (control != null) {
                map.removeMarker(markers.get(controlList.indexOf(control)));
                LatLong latLong = new LatLong(control.getLatitude(), control.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLong);
                markerOptions.animation(Animation.DROP);
                markerOptions.icon("flag_blue_32.png");
                Marker marker = new Marker(markerOptions);
                marker.setTitle(control.getPlaceName());
                map.addMarker(marker);
                ControlPosition finalControl = control;
                map.addUIEventHandler(marker, UIEventType.click, (JSObject obj) -> {
                    showSnackBar(finalControl.getPlaceName());
                    System.out.println("You clicked the line at LatLong: lat: " +
                            finalControl.getLatitude() + " lng: " + finalControl.getLongitude());
                });
            } else {
                System.err.println("The control position didn't has match");
            }*/

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

    private void filterUser() {
        FilteredList<HBox> filteredData = new FilteredList<>(userData, p -> true);
        userFilterField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(hBox -> {
                // If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                User user = (User) hBox.getUserData();
                if (user == null)
                    return false;
                // Compare first name and last name of every person with filter text.
                String lowerCaseFilter = userFilterField.getText().toLowerCase();

                String fullName = user.getLastname()+" "+user.getName();
                if (user.getName().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches first name.
                } else if (user.getLastname().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches last name.
                } else if (user.getDni().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches last name.
                } else if (fullName.toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches last name.
                }
                return false; // Does not match.
            });
        });

        SortedList<HBox> sortedData = new SortedList<>(filteredData);
        userListView.setItems(sortedData);
        checkUserFilter(filteredData);
    }

    void checkUserFilter(FilteredList<HBox> filteredData) {
        filteredData.setPredicate(hBox -> {
            // If filter text is empty, display all persons.
            if (userFilterField.getText() == null || userFilterField.getText().isEmpty()) {
                return true;
            }

            User user = (User) hBox.getUserData();
            // Compare first name and last name of every person with filter text.
            String lowerCaseFilter = userFilterField.getText().toLowerCase();

            String fullName = user.getLastname()+" "+user.getName();
            if (user.getName().toLowerCase().contains(lowerCaseFilter)) {
                return true; // Filter matches first name.
            } else if (user.getLastname().toLowerCase().contains(lowerCaseFilter)) {
                return true; // Filter matches last name.
            } else if (user.getDni().toLowerCase().contains(lowerCaseFilter)) {
                return true; // Filter matches last name.
            } else if (fullName.toLowerCase().contains(lowerCaseFilter)) {
                return true; // Filter matches last name.
            }
            return false; // Does not match.
        });
    }

    private void filterWatch() {
        FilteredList<HBox> filteredData = new FilteredList<>(watchData, p -> true);
        watchFilterField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(hBox -> {
                // If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                Watch watch = (Watch) hBox.getUserData();
                if (watch == null)
                    return false;
                // Compare first name and last name of every person with filter text.
                String lowerCaseFilter = watchFilterField.getText().toLowerCase();

                String timeString = RadarDate
                        .getFechaConMes(new DateTime(watch.getStartTime()));
                if (timeString.toLowerCase().contains(lowerCaseFilter)) {
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

            Watch watch = (Watch) hBox.getUserData();
            if (watch == null)
                return false;
            // Compare first name and last name of every person with filter text.
            String lowerCaseFilter = watchFilterField.getText().toLowerCase();

            String timeString = RadarDate
                    .getFechaConMes(new DateTime(watch.getStartTime()));
            if (timeString.toLowerCase().contains(lowerCaseFilter)) {
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
}
