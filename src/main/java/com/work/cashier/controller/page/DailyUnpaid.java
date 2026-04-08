package com.work.cashier.controller.page;

import animatefx.animation.AnimationFX;
import animatefx.animation.ZoomInUp;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.jfoenix.controls.JFXDatePicker;
import com.work.cashier.Application;
import com.work.cashier.api.ApiClient;
import com.work.cashier.constants.VARIABLE_STATIC;
import com.work.cashier.controller.infoTable.DailyUnpaidInfo;
import com.work.cashier.data_transfert_object.customer.CustomerDTO;
import com.work.cashier.data_transfert_object.order.OrderDTO;
import com.work.cashier.data_transfert_object.payment.PaymentDTO;
import com.work.cashier.print.Print;
import com.work.cashier.service.ControlsOption;
import com.work.cashier.service.NodeAnimation;
import com.work.cashier.service.PageNumberEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

public class DailyUnpaid implements Initializable {

    @FXML
    private JFXDatePicker date;

    @FXML
    private Label totalBread;

    @FXML
    private Label unpaid;

    @FXML
    private VBox containerData,contentPrint;

    private int totalUnpaid;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        date.setValue(LocalDate.now());
        showOrderList();
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
    void picker_OnAction() {
        showOrderList();
    }

    @FXML
    void print() {
        toPDF();
        new Print().printPDF("unpaid");
    }

    private void showOrderList(){
        VARIABLE_STATIC.dailyUnpaidInfoList.clear();
        int bread = 0;
        totalUnpaid = 0;
        String url = "http://192.168.7.2:8080/order/getByDate/"+date.getValue();
        containerData.getChildren().clear();
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
            bread += dto.getOrderLines().getFirst().getQuantity();
        }
        ControlsOption controlsOption = new ControlsOption();
        totalBread.setText(controlsOption.thousandSeparator(bread));
        unpaid.setText(controlsOption.thousandSeparator(totalUnpaid)+" Ar");
    }

    private void addOrderInTable(OrderDTO dto, double delay , AnimationFX animationFX){
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(Application.class.getResource("info_table/dailyUnpaidInfo.fxml"));
        try {
            HBox hBox = fxmlLoader.load();
            DailyUnpaidInfo info = fxmlLoader.getController();
            info.setOrderDTO(dto);
            int[] dataPayment = getPayment(dto);
            int payed = dataPayment[0];
            int expense = dataPayment[1];
            int unpaid = dto.getTotalPrice() - (payed + expense);
            info.setAmountUnpaid(unpaid);
            info.setAmountPayed(payed);
            info.setAmountExpense(expense);
            info.setData();

            totalUnpaid += unpaid;
            containerData.getChildren().add(hBox);
            new NodeAnimation().animate(hBox, delay, animationFX);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private int[] getPayment(OrderDTO orderDTO){
        int sum = 0;
        int sumExpense = 0;
        for(PaymentDTO dto : orderDTO.getPayments()){
            sum += (dto.getAmount()-dto.getExpense());
            if(dto.getExpense() > 0)    sumExpense += dto.getExpense();
        }
        return new int[] {sum,sumExpense};
    }

    public void toPDF() {

        try (FileOutputStream fos = new FileOutputStream("C:/Users/Public/unpaid.pdf")) {

            Document document = new Document(PageSize.A4, 20, 20, 10, 20);

            PdfWriter writer = PdfWriter.getInstance(document, fos);
            writer.setPageEvent(new PageNumberEvent());

            document.open();

            document.add(createTitle());
            document.add(createMainTable());

            document.close();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Paragraph createTitle() {
        Font boldFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
        Paragraph title = new Paragraph("SORTIE JOURNALIER DU    "+
                date.getValue().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT))+"\n\n",boldFont);
        title.setAlignment(Element.ALIGN_CENTER);
        return title;
    }


    private PdfPTable createMainTable() throws DocumentException {

        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{3.5f, 1.5f, 1.5f, 2.5f, 2.5f, 2.5f,2.5f});

        String[] headers = {
                "NOM_CLIENT", "Qté Sortie", "Prix unit.", "MONTANT", "TOTAL AVANCE", "DEPENSE", "RESTE"
        };

        addRow(table,1,"title", headers);

        int totalPains = 0;
        int totalAmount = 0;
        int totalPaid = 0;
        int totalUnpaid = 0;
        int totalExpense = 0;

        ControlsOption controlsOption = new ControlsOption();

        for (DailyUnpaidInfo d : VARIABLE_STATIC.dailyUnpaidInfoList) {

            totalAmount += parseInt(d.getTotalData());
            totalPains += parseInt(d.getQuantityData());
            totalPaid += parseInt(d.getPayedData());
            totalUnpaid += parseInt(d.getUnpaidData());
            totalExpense += parseInt(d.getExpenseData());

            addRow(table,0,"body",
                    d.getCustomerData(),
                    d.getQuantityData(),
                    d.getPriceData(),
                    controlsOption.thousandSeparator(Double.parseDouble(d.getTotalData())),
                    controlsOption.thousandSeparator(Double.parseDouble(d.getPayedData())),
                    controlsOption.thousandSeparator(Double.parseDouble(d.getExpenseData())),
                    controlsOption.thousandSeparator(Double.parseDouble(d.getUnpaidData()))
            );
        }
        addRow(table,1,"title",
                "TOTAL",
                controlsOption.thousandSeparator(totalPains),
                "",
                controlsOption.thousandSeparator(totalAmount),
                controlsOption.thousandSeparator(totalPaid),
                controlsOption.thousandSeparator(totalExpense),
                controlsOption.thousandSeparator(totalUnpaid)
        );
        return table;
    }

    private void addRow(PdfPTable table, int bold,String column, String... values) {
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

    private PdfPCell createCenteredCell(String text,int count,int align) {
        Font font = new Font(Font.FontFamily.HELVETICA, 8,count);

        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(align);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(5);

        return cell;
    }

    private int parseInt(String text) {
        return (text == null || text.isEmpty()) ? 0 : Integer.parseInt(text);
    }
}
