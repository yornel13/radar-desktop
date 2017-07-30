package gui.controller;

import com.jfoenix.controls.*;
import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.event.UIEventType;
import com.lynden.gmapsfx.javascript.object.*;
import io.datafx.controller.ViewController;
import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;
import model.ControlPosition;
import netscape.javascript.JSObject;
import service.RadarService;
import util.Const;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

@ViewController("../view/control_points.fxml")
public class ControlsController extends BaseController implements MapComponentInitializedListener, EventHandler<MouseEvent> {

    @FXML
    public TextField nameField;

    @FXML
    public JFXButton editButton;

    @FXML
    public JFXButton disableButton;

    @FXML
    private JFXListView<HBox> listView;

    @FXML
    private GoogleMapView mapView;

    @FXML
    private Pane bar;

    private ObservableList<HBox> data;

    private GoogleMap map;

    private List<ControlPosition> controlList;

    private List<Marker> markers;

    private List<MarkerOptions> markersOptions;

    private MapOptions mapOptions;

    private static Integer controlIndex;

    private static Boolean editing;

    private RadarService service;

    @PostConstruct
    public void init()  {

        service = RadarService.getInstance();

        //root.getChildren().remove(dialog);

        bar.setVisible(false);

        try {
            loadListView();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mapView.addMapInializedListener(this);
    }

    public void closeMarkerBar(ActionEvent actionEvent) {
        FadeTransition fadeTransition
                = new FadeTransition(Duration.millis(500), bar);
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.0);
        fadeTransition.play();
        fadeTransition.setOnFinished(event -> bar.setVisible(false));
    }

    public void disableControl(ActionEvent actionEvent) {
        if (editing) {
            nameField.setText(controlList.get(controlIndex).getPlaceName());
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

    public void loadListView() throws IOException {

        controlList = service.getAllControl();

        data = FXCollections.observableArrayList();

        HBox hBoxBack = new HBox();
        Label backButton = new Label();
        backButton.setGraphic(new ImageView(new Image(
                new FileInputStream("src/img/arrows-Back-icon16.png"))));
        hBoxBack.getChildren().add(backButton);
        data.add(hBoxBack);

        for (ControlPosition control: controlList) {

            HBox hBox = new HBox();
            HBox imageHBox = new HBox();
            VBox labelsVBox = new VBox();
            // ListCells
            Label nameLabel = new Label("   "+control.getPlaceName());
            nameLabel.setFont(new Font(null, 16));
            Label activeLabel  = new Label("   "+(control.getActive()?"Activo":"Desactivo"));
            activeLabel.setFont( new Font(null, 14));
            activeLabel.setTextFill(Color.valueOf("#aaaaaa"));
            FileInputStream stream = control.getActive()? new FileInputStream("src/img/red_marker_32.png"):
                    new FileInputStream("src/img/yellow_marker_32.png");
            ImageView guardImg = new ImageView(new Image(stream));
            guardImg.setFitHeight(40);
            guardImg.setFitWidth(40);

            imageHBox.getChildren().addAll(guardImg, nameLabel);
            imageHBox.setPrefHeight(4);
            labelsVBox.getChildren().addAll(nameLabel, activeLabel);
            labelsVBox.setPadding(new Insets(-1,3,-1,3));
            hBox.getChildren().addAll(imageHBox, labelsVBox);

            data.addAll(hBox);

            if (!control.getActive()) {

                FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/popup.fxml"));
                InputController inputController = new InputController(this);
                loader.setController(inputController);
                JFXPopup popup = new JFXPopup(loader.load());
                inputController.setPopup(popup);

                hBox.setOnMouseClicked(event -> {
                    if (event.getButton() == MouseButton.SECONDARY) {
                        closeMarkerBar(null);
                        popup.show(hBox, JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.LEFT);
                        controlIndex = controlList.indexOf(control);
                    }
                });
            }

        }
        listView.setItems(data);
        listView.setExpanded(true);
        listView.setVerticalGap(2.0);
        listView.depthProperty().set(1);
        listView.setOnMouseClicked(this);
    }

    @Override
    public void handle(MouseEvent event) {

        if (event.getButton() == MouseButton.PRIMARY) {

            if (listView.getSelectionModel().getSelectedIndex() == 0) {
                onBackController();
            } else {
                ControlPosition control = controlList
                        .get(listView.getSelectionModel().getSelectedIndex() - 1);
                if (control.getActive()) {
                    openMarkerBar(control);
                    LatLong latLong = new LatLong(control.getLatitude(), control.getLongitude());
                    centerMap(latLong);
                } else {
                    closeMarkerBar(null);
                    LatLong latLong = new LatLong(control.getLatitude(), control.getLongitude());
                    centerMap(latLong);
                }
            }
        } else {
            closeMarkerBar(null);
        }
    }

    public void openMarkerBar(ControlPosition control) {
        bar.setVisible(true);
        FadeTransition fadeTransition
                = new FadeTransition(Duration.millis(500), bar);
        fadeTransition.setFromValue(0.0);
        fadeTransition.setToValue(1.0);
        fadeTransition.play();
        nameField.setText(control.getPlaceName());
        nameField.setDisable(true);
        editButton.setText("Editar");
        disableButton.setText("Desactivar");
        editing = false;
        controlIndex = controlList.indexOf(control);
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
                closeMarkerBar(null);
            }
        });

        addMarkers();

    }

    public void addMarkers() {

        map.clearMarkers();

        markers = new ArrayList<>();
        markersOptions = new ArrayList<>();

        for (ControlPosition control: controlList) {

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
                });
            }
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

    public void centerMap(LatLong latLong) {
        LatLongBounds latLongBounds = new LatLongBounds();
        latLongBounds.extend(latLong);
        map.fitBounds(latLongBounds);
    }

    public void onDialogAccept(ActionEvent actionEvent) {
        super.onDialogAccept(actionEvent);
        ControlPosition control = service
                .findCPById(controlList.get(controlIndex).getId());

        switch (dialogType) {
            case Const.DIALOG_SAVE_EDIT:
                control.setPlaceName(nameField.getText());
                service.doEdit();
                break;
            case Const.DIALOG_DISABLE:
                control.setActive(false);
                service.doEdit();
                break;
            case Const.DIALOG_ENABLE:
                control.setActive(true);
                service.doEdit();
                break;
        }

        try {
            loadListView();
            addMarkers();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public class InputController {

        @FXML
        private JFXListView<?> toolbarPopupList;

        private ControlsController principal;

        private JFXPopup popup;

        public InputController(ControlsController principal) {
            this.principal = principal;
        }

        // close application
        @FXML
        private void submit() {
            popup.hide();
            if (toolbarPopupList.getSelectionModel().getSelectedIndex() == 0) {
                principal.enableControl();
            }
        }

        public void setPopup(JFXPopup popup) {
            this.popup = popup;
        }
    }
}

