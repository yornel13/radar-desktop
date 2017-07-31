package gui.controller;

import com.jfoenix.controls.*;
import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.event.UIEventType;
import com.lynden.gmapsfx.javascript.object.*;
import io.datafx.controller.ViewController;
import io.datafx.controller.flow.action.ActionTrigger;
import javafx.animation.Interpolator;
import javafx.animation.KeyValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Callback;
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
import java.util.ArrayList;
import java.util.List;

@ViewController(value = "../view/workman.fxml")
public class WorkmanController extends BaseController implements MapComponentInitializedListener, EventHandler<MouseEvent> {

    @FXML
    private JFXButton backButton;

    /*************USERS****************/
    @FXML
    private JFXTextField userFilterField;
    @FXML
    private JFXListView<HBox> userListView;

    private ObservableList<HBox> userData;

    private List<User> users;

    /*************WAHCTS****************/
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

    private List<Watch> watchesUser;

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

    /*************MAP****************/
    @FXML
    private GoogleMapView mapView;

    private GoogleMap map;

    private List<ControlPosition> controlList;

    /*************OTHERS****************/

    private boolean drawerWatchFirstShow = true;
    private boolean drawerMarkerFirstShow = true;


    @PostConstruct
    public void init() throws FileNotFoundException {

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
        backButton.setGraphic(new ImageView(
                new Image(new FileInputStream("src/img/arrow_back_icon16.png"))));
    }

    public void loadListView() throws FileNotFoundException {

        userData = FXCollections.observableArrayList();

        for (User user: users) {

            HBox hBox = new HBox();
            HBox imageHBox = new HBox();
            VBox labelsVBox = new VBox();

            Label nameLabel = new Label("   "+user.getLastname()+" "+user.getName());
            nameLabel.setFont(new Font(null, 16));
            Label dniLabel  = new Label("   "+user.getDni());
            dniLabel.setFont( new Font(null, 14));
            dniLabel.setTextFill(Color.valueOf("#aaaaaa"));
            ImageView guardImg = new ImageView(new Image(new FileInputStream("src/img/policeman64.png")));
            guardImg.setFitHeight(55);
            guardImg.setFitWidth(60);

            imageHBox.getChildren().addAll(guardImg, nameLabel);
            imageHBox.setPrefHeight(4);
            labelsVBox.getChildren().addAll(nameLabel, dniLabel);
            labelsVBox.setPadding(new Insets(-1,3,-1,3));
            hBox.getChildren().addAll(imageHBox, labelsVBox);
            hBox.setUserData(user);
            userData.addAll(hBox);
       }
        userListView.setItems(userData);
        userListView.setExpanded(true);
        userListView.setVerticalGap(2.0);
        userListView.depthProperty().set(1);
        userListView.setOnMouseClicked(this);

        filterUser();
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
                && userListView.getSelectionModel()
                .getSelectedItem().getUserData() != null) {
            openedWatchDrawer();
        }
    }

    @FXML
    public void onBackPress(ActionEvent actionEvent) {
        if (markerDrawer.isShown()) {
            markerDrawer.close();
            addMarkers();
        } else if (watchDrawer.isShown()) {
            watchDrawer.close();
        } else {
            onBackController();
        }
    }

    public void createWatchDrawer() {
        Label iconHeader = new Label();

        ImageView iconImage;
        try {
            iconImage = new ImageView(
                    new Image(new FileInputStream("src/img/policeman_64.png")));
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

        User user = (User) userListView.getSelectionModel().getSelectedItem().getUserData();

        if(drawerWatchFirstShow)
            createWatchDrawer();

        watchNameLabel.setText("   "+user.getLastname()+" "+user.getName());
        watchDniLabel.setText("     "+user.getDni());

        watchData = FXCollections.observableArrayList();

        watchesUser = service.getAllUserWatches(user.getId());

        for (Watch watch: watchesUser) {

            HBox wDetail = new HBox();
            Label watchLabel = new Label();
            ImageView iconImage;
            try {
                iconImage = new ImageView(
                        new Image(new FileInputStream("src/img/icon_multiple_marker_64.png")));
                iconImage.setFitHeight(30);
                iconImage.setFitWidth(30);
                watchLabel.setGraphic(iconImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            watchLabel.setText("   "+RadarDate
                    .getFechaConMes(new DateTime(watch.getStartTime())));
            wDetail.getChildren().add(watchLabel);
            wDetail.setUserData(watch);
            watchData.add(wDetail);
            
        }

        watchListView.setItems(watchData);
        watchListView.setExpanded(true);
        watchListView.setVerticalGap(2.0);
        watchListView.depthProperty().set(1);

        filterWatch();
    }

    private void showWatchesDetail() {
        watchListView.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY
                    && watchListView.getSelectionModel()
                    .getSelectedItem().getUserData() != null) {
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

            ControlPosition control = null;
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
            }
        }
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
        FilteredList<HBox> filteredData = new FilteredList<>(markerData, p -> true);
        markerFilterField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(hBox -> {
                // If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                Position position = (Position) hBox.getUserData();
                if (position == null)
                    return false;
                // Compare first name and last name of every person with filter text.
                String lowerCaseFilter = markerFilterField.getText().toLowerCase();

                if (position.getControlPosition().getPlaceName().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches first name.
                }
                return false; // Does not match.
            });
        });

        SortedList<HBox> sortedData = new SortedList<>(filteredData);
        markerListView.setItems(sortedData);
        checkMarkerFilter(filteredData);
    }

    void checkMarkerFilter(FilteredList<HBox> filteredData) {
        filteredData.setPredicate(hBox -> {
            // If filter text is empty, display all persons.
            if (markerFilterField.getText() == null || markerFilterField.getText().isEmpty()) {
                return true;
            }

            Position position = (Position) hBox.getUserData();
            if (position == null)
                return false;
            // Compare first name and last name of every person with filter text.
            String lowerCaseFilter = markerFilterField.getText().toLowerCase();

            if (position.getControlPosition().getPlaceName()
                    .toLowerCase().contains(lowerCaseFilter)) {
                return true; // Filter matches first name.
            }
            return false; // Does not match.
        });
    }
}
