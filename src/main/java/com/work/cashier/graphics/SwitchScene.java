package com.work.cashier.graphics;

import animatefx.animation.*;
import com.work.cashier.Launch;
import com.work.cashier.api.ApiClient;
import com.work.cashier.constants.Constants;
import com.work.cashier.controller.floatNode.IngredientProportionForProduction;
import com.work.cashier.controller.floatNode.OrderCustomerForm;
import com.work.cashier.controller.floatNode.ReductionPrice;
import com.work.cashier.controller.floatNode.TicketPrint;
import com.work.cashier.controller.page.User;
import com.work.cashier.data_transfert_object.customer.CustomerDTO;
import com.work.cashier.data_transfert_object.order.OrderDTO;
import com.work.cashier.data_transfert_object.payment.PaymentDTO;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import lombok.Setter;

import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SwitchScene {

    private final Map<String, Object> loadedControllers = new HashMap<>();
    @Setter
    private OrderDTO orderDTO;
    @Setter
    private PaymentDTO paymentDTO;
    @Setter
    private String userType;
    @Setter
    private CustomerDTO customerDTO;

    private final Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
    private final int height = (int)dimension.getHeight();
    private final int width  = (int)dimension.getWidth();


    public void navigateWindow(HBox hBox, String page_fxml){
        try {
            hBox.getChildren().clear();
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(Launch.class.getResource(page_fxml));
            VBox newNode = fxmlLoader.load();

            newNode.setPrefWidth(width);

            Object controller = fxmlLoader.getController();

            loadedControllers.put(page_fxml, controller);

            if (controller instanceof User) {
                ((User) controller).setUserType(userType);
                Platform.runLater(((User) controller)::initAfterUserType);
            }

            hBox.getChildren().add(newNode);
            new ZoomIn(newNode).play();

        } catch (IOException e) {
            Logger.getLogger(SwitchScene.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void closeFloatScene(int layer){
        Node nodeClosed = Constants.stackPane.getChildren().get(layer);
        BounceOutUp animation = new BounceOutUp(nodeClosed);
        animation.setOnFinished(_ -> {
            Constants.stackPane.getChildren().get(layer-1).setMouseTransparent(false);
            Constants.stackPane.getChildren().remove(layer);
            Constants.stackPane.getChildren().get(layer-1).setEffect(null);
        });
        animation.setSpeed(1.5);
        animation.play();
    }

    public void showFloatNode(String window,int layer){
        StackPane stackPane = Constants.stackPane;
        Node windowDisplay = stackPane.getChildren().get(layer);
        windowDisplay.setMouseTransparent(true);
        windowDisplay.setEffect(new GaussianBlur());
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(Launch.class.getResource("float_window/"+window+".fxml"));
            VBox new_scene = fxmlLoader.load();

            Object controller = fxmlLoader.getController();

            loadedControllers.put(window, controller);

            if (controller instanceof OrderCustomerForm) {
                ((OrderCustomerForm) controller).setOrderDTOSelected(orderDTO);
            } else if (controller instanceof IngredientProportionForProduction) {
                ((IngredientProportionForProduction) controller).setOrderDTOSelected(orderDTO);
            } else if (controller instanceof TicketPrint) {
                ((TicketPrint) controller).setPaymentDTO(paymentDTO);
                OrderDTO orderDTO1 = ApiClient.getOneEntity("http://192.168.7.2:8080/order/"+paymentDTO.getIdOrder(),
                        OrderDTO.class);
                ((TicketPrint) controller).setOrderDTO(orderDTO1);
            } else if (controller instanceof ReductionPrice) {
                ((ReductionPrice) controller).setCustomerDTO(customerDTO);
            }
            stackPane.getChildren().add(new_scene);

            new BounceInDown(new_scene).play();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T getController(String window, Class<T> controllerClass) {
        Object controller = loadedControllers.get(window);
        if (controllerClass.isInstance(controller)) {
            return controllerClass.cast(controller);
        }
        return null;
    }

}
