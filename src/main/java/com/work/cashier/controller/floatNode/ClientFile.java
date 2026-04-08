package com.work.cashier.controller.floatNode;

import animatefx.animation.FadeInUp;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.jfoenix.controls.JFXButton;
import com.work.cashier.Application;
import com.work.cashier.alert.AlertMessage;
import com.work.cashier.api.ApiClient;
import com.work.cashier.constants.Constants;
import com.work.cashier.constants.VARIABLE_STATIC;
import com.work.cashier.controller.infoTable.*;
import com.work.cashier.controller.login.Login;
import com.work.cashier.data_transfert_object.customer.CustomerDTO;
import com.work.cashier.data_transfert_object.customer.CustomerReductionDTO;
import com.work.cashier.data_transfert_object.order.OrderDTO;
import com.work.cashier.data_transfert_object.order.OrderLineDTO;
import com.work.cashier.data_transfert_object.payment.UnpaidDTO;
import com.work.cashier.data_transfert_object.user.UserRoleType;
import com.work.cashier.graphics.SwitchScene;
import com.work.cashier.print.Print;
import com.work.cashier.service.ControlsOption;
import com.work.cashier.service.NodeAnimation;
import com.work.cashier.service.PageNumberEvent;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;
import java.util.List;

public class ClientFile implements Initializable {

    @FXML
    private JFXButton hideBtn,reduceBtn,printBtn,unpaidBtn;

    @FXML
    private Label userInfo,unpaid;

    @FXML
    private ImageView picture;

    @FXML
    private VBox containerData,containerReduction;

    @FXML
    private DatePicker startPicker,endPicker;

    private static VBox containerReductionBox;

    private final ControlsOption controlsOption = new ControlsOption();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        containerReductionBox = containerReduction;
        CustomerDTO customerDTO = UserInfo.getCustomerDTOClicked();
        userInfo.setText(customerDTO.getLastName().toUpperCase()+" "+customerDTO.getFirstName()+"\n"+
                "Télephone : "+customerDTO.getPhoneNumber()+"\nAdresse : "+customerDTO.getAddress()+"\n"+
                "CIN : "+customerDTO.getCin());
        if(customerDTO.getImage() != null) {
            byte[] imageBytes = Base64.getDecoder().decode(customerDTO.getImage());
            javafx.scene.image.Image img = new Image(new ByteArrayInputStream(imageBytes));
            picture.setImage(img);
        }
        controlsOption.jfxButtonOption(hideBtn,"fa-times", Color.WHITE);
        controlsOption.jfxButtonOption(reduceBtn,"fa-minus", Color.BLACK);
        controlsOption.jfxButtonOption(printBtn,"fa-print", Color.DARKGREEN);
        controlsOption.jfxButtonOption(unpaidBtn,"fa-list-ul", Color.WHITE);
        startPicker.setValue(LocalDate.now().minusDays(3));
        endPicker.setValue(LocalDate.now());
        Platform.runLater(()->{
            refreshTable();
            filReductionInfo();
        });
    }

    @FXML
    void print(){
        toPDF();
        new Print().printPDF("situation");
    }

    @FXML
    void close() {
        new SwitchScene().closeFloatScene(1);
    }

    @FXML
    void reducePrice() {
        if(Login.getConnected().getRole() == UserRoleType.ROLE_ADMIN) {
            SwitchScene switchScene = new SwitchScene();
            switchScene.setCustomerDTO(UserInfo.getCustomerDTOClicked());
            switchScene.showFloatNode("reductionPrice", 1);
        }else{
            new AlertMessage("ACCES REFUSE").information();
        }
    }

    @FXML
    void start(){
        refreshTable();
    }

    @FXML
    void end(){
        refreshTable();
    }

    @FXML
    void onMouseEntered(MouseEvent event) {
        ScrollPane scrollPane = (ScrollPane) event.getSource();
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
    }

    @FXML
    void onMouseExited(MouseEvent event) {
        ScrollPane scrollPane = (ScrollPane) event.getSource();
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    }

    @FXML
    void showUnpaid(){
        showOrderUnpaid();
        unpaid.setText("IMPAYES : "+new ControlsOption().thousandSeparator(VARIABLE_STATIC.sum_remain)+" Ar");
    }

    private void refreshTable(){
        if(startPicker.getValue().isBefore(endPicker.getValue()) ||
                startPicker.getValue().isEqual(endPicker.getValue())){
            fillPrintInfo();
            unpaid.setText("IMPAYES : "+new ControlsOption().thousandSeparator(VARIABLE_STATIC.sum_remain)+" Ar");
        }
    }

    private void animateNode(Node node, double delay) {
        new NodeAnimation().animate(node, delay, new FadeInUp());
    }

    private void renderOrder(OrderDTO orderDTO, double delay) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Application.class.getResource("info_table/printOrderInfo.fxml"));

        try {
            HBox hBox = loader.load();

            FileUserOrderInfo info = loader.getController();
            info.setDto(orderDTO);

            setOrderLinePrintInfo(info.getContainerOrderLines(), orderDTO.getOrderLines());

            info.setData();

            containerData.getChildren().add(hBox);
            animateNode(hBox, delay);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void fillPrintInfo() {
        VARIABLE_STATIC.fileUserOrderInfoList.clear();
        VARIABLE_STATIC.sum_remain = 0;
        String url = "http://192.168.7.2:8080/order/byCustomerAndDates?customerId=" +
                UserInfo.getCustomerDTOClicked().getId() +
                "&startDate=" + startPicker.getValue() +
                "&endDate=" + endPicker.getValue();

        containerData.getChildren().clear();

        List<OrderDTO> list = ApiClient.getAll(url, OrderDTO.class);

        Collections.reverse(list);

        double delay = 0.0;
        for (OrderDTO orderDTO : list) {
            renderOrder(orderDTO, delay);
            delay += 0.1;
        }
    }

    public void showOrderUnpaid() {
        VARIABLE_STATIC.fileUserOrderInfoList.clear();
        VARIABLE_STATIC.sum_remain = 0;
        String url = "http://192.168.7.2:8080/order/getUnpaid?idCustomer=" +
                UserInfo.getCustomerDTOClicked().getId();

        containerData.getChildren().clear();

        List<UnpaidDTO> list = ApiClient.getAll(url, UnpaidDTO.class);

        Collections.reverse(list);

        double delay = 0.0;

        for (UnpaidDTO dto : list) {
            OrderDTO orderDTO = ApiClient.getOneEntity(
                    "http://192.168.7.2:8080/order/" + dto.getIdOrder(),
                    OrderDTO.class
            );

            if (orderDTO != null) {
                renderOrder(orderDTO, delay);
                delay += 0.1;
            }
        }
    }

    private void setOrderLinePrintInfo(VBox contentPrinting, List<OrderLineDTO> list){
        //int amount = 0;
        for (OrderLineDTO orderLineDTO : list) {

            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(Application.class.getResource("info_table/printOrderLineInfo.fxml"));

            try {
                String nameProduct = ApiClient.getString("http://192.168.7.2:8080/product/getName/"+orderLineDTO.getIdProduct());
                orderLineDTO.setNameProduct(nameProduct);
                HBox hBox = fxmlLoader.load();
                FileUserOrderLineInfo info = fxmlLoader.getController();
                info.setData(orderLineDTO);

                //amount += Integer.parseInt(info.getSubTotal().getText());
                contentPrinting.getChildren().add(hBox);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        //dto.setTotalPrice(amount);
    }

    public static void filReductionInfo() {
        String url = "http://192.168.7.2:8080/reduction/getListReductions?idCustomer="+
                UserInfo.getCustomerDTOClicked().getId();
        containerReductionBox.getChildren().clear();
        List<CustomerReductionDTO> list = ApiClient.getAll(url,CustomerReductionDTO.class);

        double delay = 0.0;

        for (CustomerReductionDTO dto : list) {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(Application.class.getResource("info_table/reductionInfo.fxml"));
            try {
                HBox hBox = fxmlLoader.load();

                ReductionInfo info = fxmlLoader.getController();
                info.setData(dto);

                containerReductionBox.getChildren().add(hBox);

                new NodeAnimation().animate(hBox,delay,new FadeInUp());
                delay += 0.1;

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void toPDF() {

        try (FileOutputStream fos = new FileOutputStream("C:/Users/Public/situation.pdf")) {

            Document document = new Document(PageSize.A4);

            PdfWriter writer = PdfWriter.getInstance(document, fos);
            writer.setPageEvent(new PageNumberEvent());

            document.open();

            document.add(createTitle());
            document.add(createSubtitle());
            document.add(createMainTable());

            document.close();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Paragraph createTitle() {
        Font boldFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
        Paragraph title = new Paragraph("SITUATION DE "+
                UserInfo.getCustomerDTOClicked().getFirstName().toUpperCase() +"\n",boldFont);
        title.setAlignment(Element.ALIGN_CENTER);
        return title;
    }

    private Paragraph createSubtitle() {
        String periode = "Edite le "
                + LocalDate.now().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM));

        Paragraph subtitle = new Paragraph(periode + "\n\n");
        subtitle.setAlignment(Element.ALIGN_CENTER);
        return subtitle;
    }

    private PdfPTable createMainTable() throws DocumentException{

        PdfPTable table = new PdfPTable(9);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{2f,0.8f, 1.5f, 1.5f, 2f, 2f, 2f, 2f, 1.5f});

        String[] headers = {
                "Date_sortie", "Nb", "Qté Sortie", "Prix sortie",
                "MONTANT", "TOT AVCE", "DEPENSE", "RESTE", "STATUS"
        };

        addRow(table,1,"title", headers);

        int totalPains = 0;
        int totalAmount = 0;
        int totalPaid = 0;
        int totalUnpaid = 0;
        int totalExpense = 0;
        int totalPayment = 0;

        ControlsOption controlsOption = new ControlsOption();

        for (FileUserOrderInfo d : VARIABLE_STATIC.fileUserOrderInfoList) {

            totalAmount += parseInt(d.getTotalData());
            totalPains += parseInt(d.getQty());
            totalPaid += parseInt(d.getPayedData());
            totalUnpaid += Math.max(parseInt(d.getRemainData()), 0);
            totalExpense += parseInt(d.getExpenseData());
            totalPayment += parseInt(d.getNbPaymentData());

            addRow(table,0,"body",
                    d.getDateData(),
                    d.getNbPaymentData(),
                    controlsOption.thousandSeparator(Double.parseDouble(d.getQty())),
                    d.getPrice(),
                    controlsOption.thousandSeparator(Double.parseDouble(d.getTotalData())),
                    controlsOption.thousandSeparator(Double.parseDouble(d.getPayedData())),
                    controlsOption.thousandSeparator(Double.parseDouble(d.getExpenseData())),
                    controlsOption.thousandSeparator(Double.parseDouble(d.getRemainData())),
                    getCheckBox(d.isStatus())
            );
        }
        addRow(table,1,"title",
                "TOTAL",
                controlsOption.thousandSeparator(totalPayment),
                controlsOption.thousandSeparator(totalPains),
                "",
                controlsOption.thousandSeparator(totalAmount),
                controlsOption.thousandSeparator(totalPaid),
                controlsOption.thousandSeparator(totalExpense),
                controlsOption.thousandSeparator(totalUnpaid),
                ""
        );
        return table;
    }

    private void addRow(PdfPTable table, int bold,String column, String... values)  {
        for (int i = 0; i < values.length; i++) {

            int alignment;

            if(column.equals("title")){
                alignment = Element.ALIGN_CENTER;
            }else {
                if (i == 0) {
                    alignment = Element.ALIGN_LEFT;
                } else {
                    alignment = Element.ALIGN_RIGHT;
                }
            }

            table.addCell(createCenteredCell(values[i], bold, alignment));
        }
    }

    private PdfPCell createCenteredCell(String text, int count, int align) {
        BaseFont bf;
        try {
            bf = BaseFont.createFont(
                    "C:/Windows/Fonts/DejaVuSans.ttf",
                    BaseFont.IDENTITY_H,
                    BaseFont.EMBEDDED
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Font font = new Font(bf, 9, count);

        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(align);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(5);

        return cell;
    }

    private int parseInt(String text) {
        return (text == null || text.isEmpty()) ? 0 : Integer.parseInt(text);
    }

    private String getCheckBox(boolean status) {
        return status ? "☑" : "☐";
    }
}
