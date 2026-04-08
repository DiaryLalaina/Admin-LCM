package com.work.cashier.controller.page;

import animatefx.animation.FadeInUp;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.work.cashier.Application;
import com.work.cashier.api.ApiClient;
import com.work.cashier.constants.ActionDatabase;
import com.work.cashier.constants.Constants;
import com.work.cashier.controller.infoTable.UserInfo;
import com.work.cashier.data_transfert_object.customer.CustomerDTO;
import com.work.cashier.data_transfert_object.employee.EmployeeDTO;
import com.work.cashier.data_transfert_object.user.UserRoleType;
import com.work.cashier.graphics.SwitchScene;
import com.work.cashier.print.Print;
import com.work.cashier.service.ControlsOption;
import com.work.cashier.service.NodeAnimation;
import com.work.cashier.service.PageNumberEvent;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.Setter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class User {

    @FXML
    private FlowPane containerClients;

    @FXML
    private Label nbCustomer;

    @FXML
    private JFXButton employeeBtn,printPresenceBtn;

    @FXML
    private JFXTextField searchField;

    @Getter @Setter
    private static FlowPane containerCustomers;

    @Setter
    private String userType;

    @FXML
    void addEmployee(){
        Constants.userRole = UserRoleType.ROLE_EMPLOYEE;
        Constants.action = ActionDatabase.INSERT;
        new SwitchScene().showFloatNode("user",0);
    }

    @FXML
    void onKeyTyped(KeyEvent event){
        if(event.getCode() == KeyCode.ENTER) {
            if (userType.equals("employee")) {
                fillEmployees(searchField.getText());
            } else if (userType.equals("customer")) {
                fillCustomers(searchField.getText());
            }
        }
    }

    @FXML
    void printPresence(){
        toPDF();
        new Print().printPDF("presence");
    }

    public static void fillCustomers(String keyword) {
        String url = "http://192.168.7.2:8080/customer/getList";
        containerCustomers.getChildren().clear();
        List<CustomerDTO> list = ApiClient.getAll(url, CustomerDTO.class);

        if (keyword != null && !keyword.isBlank()) {
            String lower = keyword.toLowerCase();
            list = list.stream()
                    .filter(e ->
                            e.getFirstName().toLowerCase().contains(lower)
                    )
                    .toList();
        }

        double delay = 0.0;

        for (CustomerDTO customerDTO : list) {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(Application.class.getResource("info_table/userInfo.fxml"));
            try {
                VBox vBox = fxmlLoader.load();
                UserInfo info = fxmlLoader.getController();
                info.setCustomerDTO(customerDTO);
                info.setUserType("customer");
                info.setDataCustomer();
                containerCustomers.getChildren().add(vBox);

                new NodeAnimation().animate(vBox,delay,new FadeInUp());

                delay += 0.1;

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void fillEmployees(String keyword) {

        String url = "http://192.168.7.2:8080/employee/getList";

        List<EmployeeDTO> list = ApiClient.getAll(url, EmployeeDTO.class);
        containerCustomers.getChildren().clear();
        if (keyword != null && !keyword.isBlank()) {
            String lower = keyword.toLowerCase();
            list = list.stream()
                    .filter(e ->
                            e.getFirstName().toLowerCase().contains(lower)
                    )
                    .toList();
        }
        double delay = 0.0;

        for (EmployeeDTO employeeDTO : list) {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(Application.class.getResource("info_table/userInfo.fxml"));
            try {
                VBox vBox = fxmlLoader.load();
                UserInfo info = fxmlLoader.getController();
                info.setEmployeeDTO(employeeDTO);
                info.setUserType("employee");
                info.setDataEmployee();

                containerCustomers.getChildren().add(vBox);

                new NodeAnimation().animate(vBox,delay,new FadeInUp());

                delay += 0.1;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void initAfterUserType() {
        Platform.runLater(()->{
            setContainerCustomers(containerClients);
            if ("customer".equals(userType)) {
                employeeBtn.setVisible(false);
                printPresenceBtn.setVisible(false);
                String numbers = ApiClient.getString("http://192.168.7.2:8080/customer/getCountCustomers");
                nbCustomer.setText(numbers.isBlank() ? "0 Client" : numbers + " Clients");
                Platform.runLater(()->fillCustomers(""));
            } else if ("employee".equals(userType)) {

                new ControlsOption().jfxButtonOption(employeeBtn,"fa-plus", Color.WHITE);
                new ControlsOption().jfxButtonOption(printPresenceBtn,"fa-print", Color.DARKVIOLET);
                Platform.runLater(()->fillEmployees(""));
                nbCustomer.setText("Employées");
            }
        });
    }

    public void toPDF() {

        try (FileOutputStream fos = new FileOutputStream("C:/Users/Public/presence.pdf")) {

            Document document = new Document(PageSize.A4.rotate());

            PdfWriter writer = PdfWriter.getInstance(document, fos);
            writer.setPageEvent(new PageNumberEvent());

            document.open();
            document.add(createTitle());
            document.add(createMainTable(document));

            document.close();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Paragraph createTitle() {
        Font boldFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
        Paragraph titlePage = new Paragraph("FICHE DE TRAVAIL\n\n",boldFont);
        titlePage.setAlignment(Element.ALIGN_CENTER);
        return titlePage;
    }

    private PdfPTable createMainTable(Document document) throws DocumentException {

        PdfPTable table = new PdfPTable(8);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{2f,2f,2f,2f,2f,2f,2f,2f});

        String[] headers = {
                "EMPLOYE", "LUNDI", "MARDI", "MERCREDI", "JEUDI",
                "VENDREDI", "SAMEDI", "DIMANCHE"
        };

        addRow(table, Font.BOLD, headers);

        float pageHeight = document.getPageSize().getHeight()
                - document.topMargin()
                - document.bottomMargin();

        float rowHeight = 20f;
        int maxRows = (int) (pageHeight / rowHeight);

        int emptyRows = maxRows - 1;

        for (int i = 0; i < emptyRows; i++) {
        //for (int i = 0; i < 30; i++) {
            addRow(table, 0,
                    "-", "", "", "", "", "", "", ""
            );
        }

        return table;
    }

    private void addRow(PdfPTable table, int bold, String... values) {
        for (String val : values) {
            table.addCell(createCenteredCell(val,bold));
        }
    }

    private PdfPCell createCenteredCell(String text, int bold) {
        Font font = new Font(Font.FontFamily.HELVETICA, 7,bold);

        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(5);
        cell.setBorderColor(BaseColor.BLACK);

        return cell;
    }
}
