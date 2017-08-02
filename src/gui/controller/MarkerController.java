package gui.controller;

import com.jfoenix.controls.*;
import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.event.UIEventType;
import com.lynden.gmapsfx.javascript.object.*;
import io.datafx.controller.ViewController;
import io.datafx.controller.flow.action.ActionTrigger;
import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
import model.User;
import netscape.javascript.JSObject;
import util.Const;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@ViewController("../view/marker.fxml")
public class MarkerController extends BaseController implements MapComponentInitializedListener, EventHandler<MouseEvent> {

    @FXML
    @ActionTrigger("back")
    private JFXButton backButton;

    @FXML
    private JFXTextField filterField;

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

    private ControlPosition selectedPosition;

    private static Boolean editing;

    @PostConstruct
    public void init() throws FileNotFoundException {

        bar.setVisible(false);

        loadListView();

        mapView.addMapInializedListener(this);
        backButton.setGraphic(new ImageView(
                new Image(new FileInputStream("src/img/arrow_back_icon16.png"))));
    }

    public void loadListView() {
        try {
            loadPositionListView();
            filterData();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            nameField.setText(selectedPosition.getPlaceName());
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

    public void loadPositionListView() throws IOException {

        controlList = service.getAllControl();

        data = FXCollections.observableArrayList();

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
            hBox.setUserData(control);
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
                        selectedPosition = control;
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

        if (event.getButton() == MouseButton.PRIMARY
                && listView.getSelectionModel().getSelectedItem() != null) {

            ControlPosition control = (ControlPosition)
                    listView.getSelectionModel().getSelectedItem().getUserData();
            if (control.getActive()) {
                openMarkerBar(control);
                LatLong latLong = new LatLong(control.getLatitude(), control.getLongitude());
                centerMap(latLong);
            } else {
                closeMarkerBar(null);
                LatLong latLong = new LatLong(control.getLatitude(), control.getLongitude());
                centerMap(latLong);
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
        selectedPosition = control;
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
                .findCPById(selectedPosition.getId());

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

        loadListView();
        addMarkers();
    }

    private void filterData() {
        FilteredList<HBox> filteredData = new FilteredList<>(data, p -> true);
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
        listView.setItems(sortedData);
        checkFilter(filteredData);
    }

    void checkFilter(FilteredList<HBox> filteredData) {
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

    public class InputController {

        @FXML
        private JFXListView<?> toolbarPopupList;

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
                principal.enableControl();
            }
        }

        public void setPopup(JFXPopup popup) {
            this.popup = popup;
        }
    }
}

