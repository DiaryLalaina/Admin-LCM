package com.work.cashier.controller.page;

import animatefx.animation.SlideInRight;
import animatefx.animation.ZoomInUp;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import com.work.cashier.Application;
import com.work.cashier.api.ApiClient;
import com.work.cashier.constants.VARIABLE_STATIC;
import com.work.cashier.controller.infoTable.CashOutInfo;
import com.work.cashier.data_transfert_object.payment.PaymentDTO;
import com.work.cashier.print.Print;
import com.work.cashier.service.ControlsOption;
import com.work.cashier.service.NodeAnimation;
import com.work.cashier.service.PageNumberEvent;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

public class CashOut implements Initializable {

    @FXML
    private JFXDatePicker startDate,endDate;

    @FXML
    private VBox containerCashOut,containerTicketing;

    @FXML
    private TextField mga_20000,mga_10000,mga_5000,mga_2000,mga_1000,mga_500,mga_200,mga_100;

    @FXML
    private Label total_20000_mga,total_10000_mga,total_5000_mga,total_2000_mga,total_1000_mga,
            total_500_mga,total_200_mga,total_100_mga;

    @FXML
    private Label paid,remain,sum_amount,sum_paid,sum_expense,sum_remain;

    @FXML
    private HBox contentAmount,contentRemain;

    @FXML
    private JFXButton jfxButton,printBtn;

    private final ControlsOption controlsOption = new ControlsOption();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        controlsOption.jfxButtonOption(printBtn,"fa-print",Color.BLACK);
        controlsOption.jfxButtonOption(jfxButton,"fa-list-ul", Color.WHITE);
        startDate.setValue(LocalDate.now());
        endDate.setValue(LocalDate.now());
        hideContent();
        Platform.runLater(this::showCashOut);
    }

    @FXML
    void onKeyPressed() {

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
    void showCashOut() {
        if(startDate.getValue().isBefore(endDate.getValue()) ||
                startDate.getValue().isEqual(endDate.getValue())) {
            setCashData();
            animateTicketing();
            fillTable();
        }
    }

    @FXML
    void printCashOut(){
        toPDF();
        new Print().printPDF("cashout");
    }

    private void hideContent(){
        HBox[] hBoxes = new HBox[]{contentAmount,contentRemain};
        for(HBox hBox : hBoxes){
            hBox.setVisible(false);
            hBox.setManaged(false);
        }
    }

    private void fillTable(){
        VARIABLE_STATIC.cashOutInfoList.clear();
        VARIABLE_STATIC.sum_amount = 0;VARIABLE_STATIC.sum_paid = 0;
        VARIABLE_STATIC.sum_expense = 0;VARIABLE_STATIC.sum_remain = 0;
        long remainAmount = 0;

        String url = "http://192.168.7.2:8080/payment/getListCashOut?startDate="+ startDate.getValue()+"&endDate="+endDate.getValue();
        containerCashOut.getChildren().clear();
        List<PaymentDTO> list = ApiClient.getAll(url, PaymentDTO.class);

        list.sort(Comparator
                .comparing((PaymentDTO p) -> extractDate(p.getTicket()))
                .thenComparing(p -> extractNumber(p.getTicket()))
        );
        double delay = 0.0;

        for(PaymentDTO dto : list){
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(Application.class.getResource("info_table/cashoutInfo.fxml"));
            try {
                HBox hbox = fxmlLoader.load();
                CashOutInfo info = fxmlLoader.getController();
                info.setStartDate(startDate.getValue());
                info.setEndDate(endDate.getValue());
                info.setWindow("paymentView");
                info.setPaymentDTO(dto);
                info.setData();
                containerCashOut.getChildren().add(hbox);

                new NodeAnimation().animate(hbox,delay,new ZoomInUp());

                delay += 0.1;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        ControlsOption controlsOption = new ControlsOption();
        remain.setText(controlsOption.thousandSeparator(remainAmount)+" Ar");

        // AFFICHAGE TOTAL DES PAIEMENTS
        sum_amount.setText(controlsOption.thousandSeparator(VARIABLE_STATIC.sum_amount));
        sum_paid.setText(controlsOption.thousandSeparator(VARIABLE_STATIC.sum_paid));
        sum_expense.setText(controlsOption.thousandSeparator(VARIABLE_STATIC.sum_expense));
        sum_remain.setText(controlsOption.thousandSeparator(VARIABLE_STATIC.sum_remain));
    }

    private void animateTicketing(){
        List<Node> nodes = containerTicketing.getChildren().stream().toList();
        double delay = 0.0;
        for(Node node : nodes){
            new NodeAnimation().animate(node,delay,new SlideInRight());
            delay += 0.1;
        }
    }

    private void setCashData(){
        int sum = 0;
        ControlsOption controlsOption = new ControlsOption();
        PaymentDTO dto = ApiClient.getOneEntity("http://192.168.7.2:8080/payment/billSummaryBetweenDates?"+
                "startDate="+startDate.getValue()+"&endDate="+endDate.getValue(), PaymentDTO.class);
        if(dto != null) {
            int[] data = {dto.getTwentyThousand(), dto.getTenThousand(), dto.getFiveThousand(), dto.getTwoThousand(),
                    dto.getOneThousand(), dto.getFiveHundred(), dto.getTwoHundred(), dto.getOneHundred()};
            Label[] labels = {total_20000_mga, total_10000_mga, total_5000_mga, total_2000_mga, total_1000_mga,
                    total_500_mga, total_200_mga, total_100_mga};
            TextField[] textFields = {mga_20000, mga_10000, mga_5000, mga_2000, mga_1000, mga_500, mga_200, mga_100};
            for (int i = 0; i < data.length; i++) {
                textFields[i].setText(String.valueOf(data[i]));
                int countTicket = Integer.parseInt(textFields[i].getText());
                int ticket = Integer.parseInt(labels[i].getId().split("_")[1]);
                labels[i].setText(" = " + controlsOption.thousandSeparator(countTicket * ticket) + " Ar");
                sum += (countTicket * ticket);
            }
        }
        paid.setText(controlsOption.thousandSeparator(sum)+" Ar");
    }

    private static Integer extractDate(String ticket) {
        try {
            String[] parts = ticket.split("-");
            return Integer.parseInt(parts[0].trim()); // format yyyy-MM-dd
        } catch (Exception e) {
            return 0;
        }
    }

    private static Integer extractNumber(String ticket) {
        try {
            String[] parts = ticket.split(" / ");
            return Integer.parseInt(parts[1].trim());
        } catch (Exception e) {
            return 0;
        }
    }

    public void toPDF() {

        try (FileOutputStream fos = new FileOutputStream("C:/Users/Public/cashout.pdf")) {

            Document document = new Document(PageSize.A4, 36, 36, 36, 60);
            PdfWriter writer = PdfWriter.getInstance(document, fos);
            document.open();

            document.add(createTitle());
            document.add(createSubtitle());
            document.add(createMainTable());
            document.add(createBlocTicketing());

            writer.setPageEvent(new PageNumberEvent());
            document.close();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Paragraph createTitle() {
        Font boldFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);

        String startDateParse = startDate.getValue().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT));
        String endDateParse = endDate.getValue().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT));
        String date = startDate.getValue().isEqual(endDate.getValue()) ?
                startDateParse : startDateParse +"  AU  "+endDateParse;

        Paragraph title = new Paragraph("LES ENCAISSEMENTS DU    "+ date+"\n",boldFont);
        title.setAlignment(Element.ALIGN_CENTER);
        return title;
    }

    private Paragraph createSubtitle() {
        String periode = "EDITEE LE "
                + LocalDate.now().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM));

        Paragraph subtitle = new Paragraph(periode + "\n\n");
        subtitle.setAlignment(Element.ALIGN_CENTER);
        return subtitle;
    }

    private PdfPTable createMainTable() throws DocumentException {

        PdfPTable table = new PdfPTable(8);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{2.3f, 0.7f, 2.5f, 2.8f, 1.9f, 1.9f, 1.8f, 1.7f});

        String[] headers = {
                "DATE VENTE", "N°", "TICKET", "CLIENT",
                "MONTANT", "PAIEMENT", "DEPENSE", "RESTE"
        };

        addRow(table, BaseColor.BLACK, Font.BOLD, headers);

        for (CashOutInfo c : VARIABLE_STATIC.cashOutInfoList) {
            addRow(table,BaseColor.BLACK,Font.BOLD,
                    c.getDateSaleData(),
                    c.getNbPaymentData(),
                    c.getTicketData(),
                    c.getCustomerData(),
                    c.getAmountData(),
                    c.getPaidData(),
                    c.getExpenseData(),
                    c.getRemainData()
            );
        }

        ControlsOption co = new ControlsOption();

        addRow(table,BaseColor.BLACK,Font.BOLD,
                "", "", "",
                VARIABLE_STATIC.cashOutInfoList.size() + " paiements",
                co.thousandSeparator(VARIABLE_STATIC.sum_amount),
                co.thousandSeparator(VARIABLE_STATIC.sum_paid),
                co.thousandSeparator(VARIABLE_STATIC.sum_expense),
                co.thousandSeparator(VARIABLE_STATIC.sum_remain)
        );

        return table;
    }

    private PdfPTable createBlocTicketing() throws DocumentException {
        PdfPTable container = new PdfPTable(1);
        container.setWidthPercentage(100);
        container.setKeepTogether(true); // 🔥 empêche la coupure

        PdfPCell titleCell = new PdfPCell();
        titleCell.setBorder(Rectangle.NO_BORDER);
        titleCell.addElement(createCashTitle());

        PdfPCell tableCell = new PdfPCell();
        tableCell.setBorder(Rectangle.NO_BORDER);
        tableCell.addElement(createCashTable());

        container.addCell(titleCell);
        container.addCell(tableCell);

        return container;
    }

    private Paragraph createCashTitle() {
        Font boldFont = new Font(Font.FontFamily.HELVETICA, 13, Font.BOLD);
        Paragraph p = new Paragraph("\nDETAIL DES BILLETS DU    "+
                startDate.getValue().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT))
                        +"    AU    "+endDate.getValue().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT))+"\n\n",boldFont);
        p.setAlignment(Element.ALIGN_CENTER);
        return p;
    }

    private PdfPTable createCashTable() throws DocumentException {

        ControlsOption co = new ControlsOption();

        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(50);
        table.setWidths(new float[]{2f, 1f, 3f});

        int[] billets = {20000, 10000, 5000, 2000, 1000, 500, 200, 100};
        TextField[] fields = {
                mga_20000, mga_10000, mga_5000, mga_2000,
                mga_1000, mga_500, mga_200, mga_100
        };

        int total = 0;

        for (int i = 0; i < billets.length; i++) {

            int count = parseInt(fields[i].getText());
            int montant = count * billets[i];
            total += montant;

            addRow(table,BaseColor.WHITE,Font.BOLD,
                    billets[i] + " Ar",
                    "x " + count,
                    "=  " + co.thousandSeparator(montant) + " Ar"
            );
        }

        PdfPCell totalCell = new PdfPCell(
                new Phrase("TOTAL = " + co.thousandSeparator(total) + " Ar\n"+
                        "( "+co.thousandSeparator(total*5)+" FMG )")
        );

        totalCell.setColspan(3);
        totalCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        totalCell.setPadding(8);

        table.addCell(totalCell);

        PdfPTable wrapper = new PdfPTable(1);
        wrapper.setWidthPercentage(50);
        wrapper.setHorizontalAlignment(Element.ALIGN_CENTER);

        PdfPCell wrapperCell = new PdfPCell(table);

        wrapperCell.setBorder(Rectangle.BOX);
        wrapperCell.setBorderWidth(1f);

        wrapperCell.setPadding(5);

        wrapper.addCell(wrapperCell);


        return wrapper;
    }

    private void addRow(PdfPTable table,BaseColor color,int bold, String... values) {
        for (String val : values) {
            table.addCell(createCenteredCell(val,color,bold));
        }
    }

    private PdfPCell createCenteredCell(String text,BaseColor color,int bold) {
        Font font = new Font(Font.FontFamily.HELVETICA, 9,bold);

        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(5);
        cell.setBorderColor(color);

        return cell;
    }

    private int parseInt(String text) {
        return (text == null || text.isEmpty()) ? 0 : Integer.parseInt(text);
    }
}
