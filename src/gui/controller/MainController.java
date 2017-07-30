package gui.controller;

import io.datafx.controller.ViewController;
import io.datafx.controller.flow.Flow;
import io.datafx.controller.flow.FlowException;
import io.datafx.controller.flow.FlowHandler;
import io.datafx.controller.flow.action.ActionMethod;
import io.datafx.controller.flow.container.AnimatedFlowContainer;
import io.datafx.controller.flow.container.ContainerAnimations;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import io.datafx.controller.util.VetoException;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import javax.annotation.PostConstruct;


@ViewController(value = "../view/main.fxml")
public class MainController extends BaseController {

    @FXMLViewFlowContext
    private ViewFlowContext context;

    @FXML
    private StackPane centerPane;

    private FlowHandler flowHandler;

    @PostConstruct
    public void init() throws FlowException {

        Flow innerFlow = new Flow(SyncController.class);
        context = new ViewFlowContext();
        innerFlow.withGlobalLink("map", WorkmanController.class);
        innerFlow.withGlobalLink("control", ControlsController.class);
        innerFlow.withGlobalBackAction("back");

        flowHandler = innerFlow.createHandler(context);
        ContainerAnimations animations = ContainerAnimations.SWIPE_LEFT;
        Duration duration = Duration.millis(320);
        AnimatedFlowContainer animation = new AnimatedFlowContainer(duration, animations);
        centerPane.getChildren().add(flowHandler.start(animation));
    }
}
