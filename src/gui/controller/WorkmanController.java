package gui.controller;

import com.jfoenix.controls.JFXListView;
import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.event.UIEventType;
import com.lynden.gmapsfx.javascript.object.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import model.ControlPosition;
import model.Position;
import model.User;
import model.Watch;
import netscape.javascript.JSObject;
import service.RadarService;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;


public class WorkmanController  implements Initializable, MapComponentInitializedListener {

    @FXML
    JFXListView<HBox> listView;

    List<User> users;
    ObservableList<HBox> data;

    private HBox hBoxBack;

    @FXML
    private GoogleMapView mapView;

    private GoogleMap map;

    private List<Watch> watches;

    private List<ControlPosition> controlList;

    private List<Marker> markers;
    private List<MarkerOptions> markersOptions;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        mapView.addMapInializedListener(this);

        controlList = RadarService.getInstance().getAllControlActive();

        users = RadarService.getInstance().getAllUser();
        if(users == null) {
            System.out.println("No users");
            users = new ArrayList<>();
        }

        try {
            loadListView();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void loadListView() throws FileNotFoundException {

        int i = 0;
        data = FXCollections.observableArrayList();
        for (User user: users) {

            while (i < 1){
                hBoxBack  = new HBox();
                Label backButton = new Label();
                backButton.setGraphic(new ImageView(new Image(new FileInputStream("src/img/arrows-Back-icon16.png"))));
                hBoxBack.getChildren().add(backButton);
                data.add(hBoxBack);
                i++;
            }

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

            data.addAll(hBox);
       }

        listView.setItems(data);
        listView.setExpanded(true);
        listView.setVerticalGap(2.0);
        listView.depthProperty().set(1);
        listView.setOnMouseClicked(event -> {
            if (listView.getSelectionModel().getSelectedIndex() == 0) {
                try {
                    StartApp sa = new StartApp();
                    sa.start(StartApp.stage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                addMarkers();

                User user = users.get(listView.getSelectionModel().getSelectedIndex()-1);
                watches =  RadarService.getInstance().getAllUserWatches(user.getId());
                if (!watches.isEmpty()) {
                    addMarkersRoute(watches.get(0));
                }

            }
        });
    }

    public void addMarkersRoute(Watch watch) {
        for (Position position:
                RadarService.getInstance().findAllPositionsByWatch(watch)) {

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
            } else {
                System.err.println("The control position didn't has match");
            }
        }
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
                System.out.println("You clicked the line at LatLong: lat: " +
                        control.getLatitude() + " lng: " + control.getLongitude());
                System.out.println(control.getPlaceName());
            });
        }

        centerMap();
    }

    public void centerMap() {
        LatLongBounds latLongBounds = new LatLongBounds();
        for (ControlPosition control: controlList) {
            LatLong latLong = new LatLong(control.getLatitude(), control.getLongitude());
            latLongBounds.extend(latLong);
        }
        map.fitBounds(latLongBounds);
    }

}
