package com.work.cashier.controller.page;

import animatefx.animation.AnimationFX;
import animatefx.animation.ZoomInUp;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import com.work.cashier.Application;
import com.work.cashier.alert.AlertMessage;
import com.work.cashier.api.ApiClient;
import com.work.cashier.controller.infoTable.OrderInfo;
import com.work.cashier.controller.infoTable.OrderInformation;
import com.work.cashier.controller.login.Login;
import com.work.cashier.data_transfert_object.customer.CustomerDTO;
import com.work.cashier.data_transfert_object.order.OrderDTO;
import com.work.cashier.data_transfert_object.order.OrderLineDTO;
import com.work.cashier.data_transfert_object.order.OrderStatusType;
import com.work.cashier.data_transfert_object.product.ProductDTO;
import com.work.cashier.data_transfert_object.user.UserRoleType;
import com.work.cashier.service.Chart;
import com.work.cashier.service.ControlsOption;
import com.work.cashier.service.NodeAnimation;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.chart.PieChart;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.Getter;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;

public class CustomerOrders implements Initializable {

    @FXML
    private JFXDatePicker date;

    @FXML
    private Label totalArticle,subTotalArticle;

    @FXML
    private VBox containerOrderList,contentRawMaterialsUsed;

    @FXML
    private PieChart pieChart;

    @FXML
    private JFXComboBox<String> nameProductChoice,article;

    @FXML
    private JFXTextField customer,quantity,price,total;

    @FXML
    private HBox contentFormOrders;

    @Getter
    private static VBox containerOrders;

    @Getter
    private static String dateOrders;

    private final ContextMenu suggestionsPopup = new ContextMenu();

    private final ObservableList<String> nameCustomerList = FXCollections.observableArrayList();

    private final ObservableList<String> articleList = FXCollections.observableArrayList();

    private final ObservableList<CustomerDTO> customers = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        contentFormOrders.setVisible(Login.getConnected().getRole() == UserRoleType.ROLE_ADMIN);
        contentFormOrders.setManaged(Login.getConnected().getRole() == UserRoleType.ROLE_ADMIN);
        customers.addAll(listCustomer());
        pieChart.setLabelsVisible(false);
        date.setValue(LocalDate.now());
        dateOrders = String.valueOf(date.getValue());
        setContainerOrders(containerOrderList);
        datePickerOnAction();
        Platform.runLater(()->{

            article.getItems().addAll(products());
            article.setValue(products().getFirst());
            datePickerOnAction();
            autoCompletion(nameCustomerList,listNameCustomer(),customer);
            quantity.setOnKeyPressed(e -> {
                if (e.getCode() == KeyCode.ENTER) {
                    if(new AlertMessage("Voulez-vous insérer ?").confirmMessage()) {
                        saveOrder();
                        showOrderList();
                        clearForm();
                    }
                }
            });
        });
    }

    @FXML
    void datePickerOnAction() {
        dateOrders = String.valueOf(date.getValue());
        setTotalBread();
        showOrderList();
        disposeNameProductChoice();
    }

    @FXML
    void onChoiceProduct(){
        showPieChart();
    }

    @FXML
    void calculateTotal(){
        if(!price.getText().isBlank()){
            int unitPrice = Integer.parseInt(price.getText());
            int qty = Integer.parseInt(quantity.getText());
            int totalPrice = unitPrice * qty;
            total.setText(totalPrice+"");
        }
    }

    @FXML
    void article_OnAction(){
        CustomerDTO customerDTO = findCustomerByName(customer.getText());
        if(customerDTO != null) {
            String url = "http://192.168.7.2:8080/product/getByNameWithReduction?name=" + article.getValue()
                    + "&idCustomer=" + customerDTO.getId();
            ProductDTO productDTO = ApiClient.getOneEntity(url, ProductDTO.class);
            assert productDTO != null;
            price.setText(productDTO.getPrice()+"");
            quantity.requestFocus();
        }else new AlertMessage("Client introuvable").information();
    }

    private void setTotalBread(){
        String url = "http://192.168.7.2:8080/orderLine/getSumBread/"+dateOrders;
        String totalBread = ApiClient.getString(url);
        totalArticle.setText(totalBread.isEmpty() ? (0+" pains") :
                new ControlsOption().thousandSeparator(Double.parseDouble(totalBread))+" pains");
    }

    private void showPieChart(){
        Chart chart = new Chart();
        chart.setContainerListRecipe(contentRawMaterialsUsed);
        chart.setPieChart(pieChart);
        chart.setNameProduct(nameProductChoice.getValue());
        String quantity = ApiClient.getString("http://192.168.7.2:8080/orderLine/getSumBreadByDateAndProduct?nameProduct="+
                nameProductChoice.getValue()+"&date="+dateOrders);
        chart.setQuantity(Integer.parseInt(quantity));
        chart.configure(true);
        subTotalArticle.setText("Production : "+quantity+" "+nameProductChoice.getValue());
    }

    public static void showOrderList(){
        String url = "http://192.168.7.2:8080/order/getByDate/"+dateOrders;
        CustomerOrders.getContainerOrders().getChildren().clear();
        List<OrderDTO> orderDTOList = ApiClient.getAll(url,OrderDTO.class);

        double delay = 0.0;
        for(OrderDTO dto : orderDTOList) {
            CustomerDTO customerDTO = ApiClient.getOneEntity(
                    "http://192.168.7.2:8080/customer/" + dto.getIdCustomer(),
                    CustomerDTO.class
            );

            if (customerDTO == null || dto.getOrderLines().isEmpty()) {
                return;
            }
            dto.setNameCustomer(customerDTO.getFirstName());
        }

        orderDTOList.sort(Comparator.comparing(
                OrderDTO::getNameCustomer,
                String.CASE_INSENSITIVE_ORDER
        ));

        for (OrderDTO dto : orderDTOList) {
            addOrderInTable(dto,delay,new ZoomInUp());
            delay += 0.15;
        }
    }

    private static void addOrderInTable(OrderDTO dto,double delay ,AnimationFX animationFX){
        FXMLLoader fxmlLoader = new FXMLLoader();
        //fxmlLoader.setLocation(Application.class.getResource("info_table/orderInfo.fxml"));
        fxmlLoader.setLocation(Application.class.getResource("info_table/orderInformation.fxml"));
        try {
            HBox hBox = fxmlLoader.load();
            OrderInformation info = fxmlLoader.getController();
            //OrderInfo info = fxmlLoader.getController();
            info.setDto(dto);
            info.setData();
            CustomerOrders.getContainerOrders().getChildren().add(hBox);
            new NodeAnimation().animate(hBox, delay, animationFX);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void saveOrder() {

        CustomerDTO customerDTO = findCustomerByName(customer.getText());
        if (customerDTO == null) {
            new AlertMessage("Client introuvable").information();
            return;
        }

        ProductDTO productDTO = getProductWithReduction(customerDTO.getId());
        if (productDTO == null) return;

        if(orderIsExist()){
            OrderDTO currentOrderDTO = getOrderUpdate();

            assert currentOrderDTO != null;
            int total = currentOrderDTO.getTotalPrice();
            OrderLineDTO orderLineDTO = buildOrderLine(productDTO,currentOrderDTO);

            ApiClient.update("http://192.168.7.2:8080/orderLine/update/"+orderLineDTO.getId(),orderLineDTO);
            currentOrderDTO.setTotalPrice(orderLineDTO.getSubTotalPrice()+total);
            ApiClient.update("http://192.168.7.2:8080/order/update/"+currentOrderDTO.getId(),currentOrderDTO);
            setTotalBread();
        }else {
            OrderDTO orderDTO = buildOrder(customerDTO.getId(), productDTO);
            ApiClient.insert("http://192.168.7.2:8080/order/save", orderDTO);
            setTotalBread();
        }
        customer.requestFocus();
    }

    private ProductDTO getProductWithReduction(Long customerId) {
        String url = String.format(
                "http://192.168.7.2:8080/product/getByNameWithReduction?name=%s&idCustomer=%d",
                article.getValue(), customerId
        );
        return ApiClient.getOneEntity(url, ProductDTO.class);
    }

    private OrderDTO buildOrder(Long customerId, ProductDTO product) {

        OrderLineDTO line = new OrderLineDTO();
        line.setIdProduct(product.getId());
        line.setPrice(parseInt(price));
        line.setQuantity(parseInt(quantity));
        line.setSubTotalPrice(parseInt(total));

        OrderDTO order = new OrderDTO();
        order.setIdCustomer(customerId);
        order.setStatus(OrderStatusType.DRAFT);
        order.setCreatedAt(String.valueOf(date.getValue()));
        order.setOrderLines(List.of(line));

        return order;
    }

    private OrderLineDTO buildOrderLine(ProductDTO product,OrderDTO orderDTO) {

        OrderLineDTO currentOrderLine = orderDTO.getOrderLines().getFirst();

        OrderLineDTO line = new OrderLineDTO();
        line.setId(currentOrderLine.getId());
        line.setIdProduct(product.getId());
        line.setPrice(parseInt(price));
        line.setQuantity(parseInt(quantity)+currentOrderLine.getQuantity());
        line.setSubTotalPrice(parseInt(total)+currentOrderLine.getSubTotalPrice());

        return line;
    }

    private OrderLineDTO newOrderLine(ProductDTO product,OrderDTO orderDTO) {

        OrderLineDTO line = new OrderLineDTO();
        line.setIdProduct(product.getId());
        line.setPrice(parseInt(price));
        line.setQuantity(parseInt(quantity));
        line.setSubTotalPrice(parseInt(total));

        return line;
    }

    private int parseInt(TextField field) {
        return Integer.parseInt(field.getText());
    }

    private void disposeNameProductChoice(){
        nameProductChoice.getItems().clear();
        String urlNameProductList = "http://192.168.7.2:8080/orderLine/getProductTotalList/"+dateOrders;
        nameProductChoice.getItems().addAll(ApiClient.getListString(urlNameProductList));
    }

    private boolean orderIsExist(){
        for (OrderDTO dto : listOrders()) {
            if(dto.getNameCustomer().equals(customer.getText())){
                return true;
            }
        }
        return false;
    }

    private OrderDTO getOrderUpdate(){
        for (OrderDTO dto : listOrders()) {
            if(dto.getNameCustomer().equals(customer.getText())){
                return dto;
            }
        }
        return null;
    }

    private static List<OrderDTO> listOrders() {

        String url = "http://192.168.7.2:8080/order/getByDate/" + dateOrders;
        List<OrderDTO> orders = ApiClient.getAll(url, OrderDTO.class);

        for (OrderDTO dto : orders) {

            CustomerDTO customerDTO = ApiClient.getOneEntity(
                    "http://192.168.7.2:8080/customer/" + dto.getIdCustomer(),
                    CustomerDTO.class
            );

            if (customerDTO != null) {
                dto.setNameCustomer(customerDTO.getFirstName());
            } else {
                System.out.println("Client introuvable pour ID = " + dto.getIdCustomer());
            }
        }
        return orders;
    }

    private List<String> listNameCustomer(){
        return ApiClient.getListString("http://192.168.7.2:8080/customer/getNames");
    }

    private List<CustomerDTO> listCustomer(){
        return ApiClient.getAll("http://192.168.7.2:8080/customer/getList", CustomerDTO.class);
    }

    private List<String> products(){
        String urlProduct = "http://192.168.7.2:8080/product/getNames";
        return ApiClient.getListString(urlProduct);
    }

    private void autoCompletion(ObservableList<String> initList,
                                List<String> listData,
                                JFXTextField field){
        //nameCustomerList.addAll(listNameCustomer());
        initList.addAll(listData);
        field.textProperty().addListener((_, _, newText) -> {
            if (newText == null || newText.isEmpty()) {
                suggestionsPopup.hide();
                return;
            }

            List<String> matches = initList.stream()
                    .filter(name -> name.toLowerCase().startsWith(newText.toLowerCase()))
                    .toList();

            if (!matches.isEmpty()) {
                suggestionsPopup.requestFocus();
                suggestionsPopup.getItems().clear();
                for (String match : matches) {
                    MenuItem item = new MenuItem(match);
                    item.setOnAction(_ -> {
                        field.setText(match);
                        field.positionCaret(match.length());
                        suggestionsPopup.hide();
                    });
                    suggestionsPopup.getItems().add(item);
                    suggestionsPopup.requestFocus();
                }

                if (!suggestionsPopup.isShowing()) {
                    suggestionsPopup.requestFocus();
                    suggestionsPopup.show(field, Side.BOTTOM, 0, 0);
                } else {
                    suggestionsPopup.hide();
                    suggestionsPopup.show(field, Side.BOTTOM, 0, 0);
                }

            } else {
                suggestionsPopup.hide();
            }
        });

        field.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                if (!suggestionsPopup.getItems().isEmpty()) {
                    field.setText(suggestionsPopup.getItems().getFirst().getText());
                    switchToQuantity();
                    /*SwitchScene switchScene = new SwitchScene();
                    *switchScene.setCustomerDTO(findCustomerByName(customer.getText()));
                    switchScene.showFloatNode("order",0);
                     */
                }
                suggestionsPopup.hide();
            }
        });

        field.focusedProperty().addListener((_, _, newVal) -> {
            if (!newVal) suggestionsPopup.hide();
        });
    }

    private CustomerDTO findCustomerByName(String name) {
        return customers.stream()
                .filter(c -> c.getFirstName() != null)
                .filter(c -> c.getFirstName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    private void switchToQuantity(){
        article_OnAction();
    }

    private void clearForm(){
        customer.clear();
        quantity.setText("0");
        total.setText("0");
    }

    public static void setContainerOrders(VBox containerOrders) {
        CustomerOrders.containerOrders = containerOrders;
    }
}

