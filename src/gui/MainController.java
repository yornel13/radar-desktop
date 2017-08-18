package gui;

import io.datafx.controller.ViewController;
import io.datafx.controller.flow.Flow;
import io.datafx.controller.flow.FlowException;
import io.datafx.controller.flow.FlowHandler;
import io.datafx.controller.flow.container.ContainerAnimations;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import util.ExtendedAnimatedFlowContainer;

import javax.annotation.PostConstruct;


@ViewController("view/main.fxml")
public class MainController {

    @FXMLViewFlowContext
    protected ViewFlowContext flowContext;

    @FXML
    private StackPane centerPane;

    private FlowHandler flowHandler;

    @PostConstruct
    public void init() throws FlowException {

        Flow innerFlow = new Flow(StartController.class);
        innerFlow.withGlobalLink("start", StartController.class);
        innerFlow.withGlobalLink("sync", SyncController.class);
        innerFlow.withGlobalLink("workman", WorkmanController.class);
        innerFlow.withGlobalLink("control", ControlController.class);
        innerFlow.withGlobalLink("marker", MarkerController.class);
        innerFlow.withGlobalLink("admin", AdminController.class);
        innerFlow.withGlobalLink("employee", UserController.class);
        innerFlow.withGlobalLink("assign", AssignController.class);
        innerFlow.withGlobalLink("company", CompanyController.class);
        innerFlow.withGlobalBackAction("back");

        flowHandler = innerFlow.createHandler(flowContext);
        ContainerAnimations animations = ContainerAnimations.SWIPE_LEFT;
        Duration duration = Duration.millis(320);
        ExtendedAnimatedFlowContainer animation = new ExtendedAnimatedFlowContainer(duration, animations);
        centerPane.getChildren().add(flowHandler.start(animation));
        flowContext.register("AnimatedFlow", animation);
        flowContext.register("ContentFlow", innerFlow);
    }
}
