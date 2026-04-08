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
import com.work.cashier.controller.infoTable.FollowSaleInfo;
import com.work.cashier.data_transfert_object.customer.CustomerDTO;
import com.work.cashier.data_transfert_object.order.OrderDTO;
import com.work.cashier.data_transfert_object.order.OrderLineDTO;
import com.work.cashier.data_transfert_object.payment.PaymentDTO;
import com.work.cashier.data_transfert_object.payment.UnpaidDTO;
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
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class FollowSale implements Initializable {

    @FXML
    private JFXDatePicker startDate,endDate;

    @FXML
    private JFXButton displayBtn,printBtn;

    @FXML
    private VBox containerData,containerInformation;

    @FXML
    private Label sale,bread,payment,amount,paid,remain,expense;

    private final ControlsOption controlsOption = new ControlsOption();

    private String title = "LES IMPAYES \n\n";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        controlsOption.jfxButtonOption(printBtn,"fa-print", Color.DARKVIOLET);
        controlsOption.jfxButtonOption(displayBtn,"fa-list-ul",Color.DARKGREEN);
        startDate.setValue(LocalDate.now().minusDays(2));
        endDate.setValue(LocalDate.now());
        Platform.runLater(()-> fillTable(listAllUnpaid()));
    }

    @FXML
    void displayBtn_OnAction() {
        if(startDate.getValue().isBefore(endDate.getValue()) ||
                startDate.getValue().isEqual(endDate.getValue())) {
            String startDateParse = startDate.getValue().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT));
            String endDateParse = endDate.getValue().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT));
            String date = startDate.getValue().isEqual(endDate.getValue()) ?
                    startDateParse : startDateParse + "  AU  " + endDateParse;
            System.out.println(startDate.getValue() + " --- " + endDate.getValue());
            title = "CREANCE DES CLIENTS DU  " + date + "\n\n";
            fillTable(listOrder());
            animateTicketing();
        }
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
    void print() {
        toPDF();
        new Print().printPDF("creance");
    }

    @FXML
    void unpaid(){
        title = "LES IMPAYES \n\n";
        fillTable(listAllUnpaid());
        new Print().printPDF("creance");
    }

    private void fillTable(List<OrderDTO> list){
        VARIABLE_STATIC.followSaleInfos.clear();
        containerData.getChildren().clear();

        String urlCustomer = "http://192.168.7.2:8080/customer/getList";
        List<CustomerDTO> customerDTOS = ApiClient.getAll(urlCustomer, CustomerDTO.class);


        list.sort(Comparator.comparing(
                order -> LocalDate.parse(order.getCreatedAt()),
                Comparator.nullsLast(LocalDate::compareTo)
        ));

        Map<Long, String> customerMap = customerDTOS.stream()
                .collect(Collectors.toMap(CustomerDTO::getId, CustomerDTO::getFirstName));

        for (OrderDTO order : list) {
            order.setNameCustomer(customerMap.get(order.getIdCustomer()));
        }

        list.sort(Comparator.comparing(
                OrderDTO::getNameCustomer,
                Comparator.nullsLast(String::compareToIgnoreCase)
        ));

        double delay = 0.0;

        for(OrderDTO dto : list){
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(Application.class.getResource("info_table/followSaleInfo.fxml"));
            try {
                HBox hbox = fxmlLoader.load();
                FollowSaleInfo info = fxmlLoader.getController();
                info.setDto(dto);
                info.setData();
                containerData.getChildren().add(hbox);

                new NodeAnimation().animate(hbox,delay,new ZoomInUp());

                delay += 0.1;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        setCashData();
    }

    private void setCashData(){
        List<FollowSaleInfo> followSaleInfos = VARIABLE_STATIC.followSaleInfos;
        int sum_amount = 0; int sum_paid = 0;
        int sum_expense = 0;int sum_remain = 0;
        int sum_bread = 0;int sum_payment = 0;

        for(FollowSaleInfo info : followSaleInfos){
            sum_amount += parseInt(info.getAmountData());
            sum_paid += parseInt(info.getPaidData());
            sum_expense += parseInt(info.getExpenseData());
            sum_remain += parseInt(info.getRemainData());
            sum_bread += parseInt(info.getBreadData());
            sum_payment += parseInt(info.getNbPaymentData());
        }

        // AFFICHAGE TOTAL DES PAIEMENTS
        sale.setText("VENTES : "+controlsOption.thousandSeparator(followSaleInfos.size()));
        bread.setText("PAINS : "+controlsOption.thousandSeparator(sum_bread));
        payment.setText("PAIEMENTS : "+controlsOption.thousandSeparator(sum_payment));
        amount.setText("TOTAL : "+controlsOption.thousandSeparator(sum_amount)+ " Ar");
        paid.setText("TOT AVCE : "+controlsOption.thousandSeparator(sum_paid)+ " Ar");
        remain.setText("RESTE : "+controlsOption.thousandSeparator(sum_remain)+ " Ar");
        expense.setText("DEPENSES : "+controlsOption.thousandSeparator(sum_expense)+ " Ar");
    }

    private List<OrderDTO> listOrder(){
        String url = "http://192.168.7.2:8080/order/getByDateBetween?startDate=" +
                startDate.getValue() + "&endDate=" + endDate.getValue();

        return ApiClient.getAll(url, OrderDTO.class);
    }

    private List<OrderDTO> listAllUnpaid() {
        List<OrderDTO> orderDTOList = new ArrayList<>();
        String url = "http://192.168.7.2:8080/order/getAllUnpaid";

        List<UnpaidDTO> list = ApiClient.getAll(url, UnpaidDTO.class);

        for (UnpaidDTO dto : list) {
            OrderDTO orderDTO = ApiClient.getOneEntity(
                    "http://192.168.7.2:8080/order/" + dto.getIdOrder(),
                    OrderDTO.class
            );

            assert orderDTO != null;
            OrderLineDTO orderLineDTO = orderDTO.getOrderLines().getFirst();

            double total = orderLineDTO.getQuantity() * orderLineDTO.getPrice();
            if(orderDTO.getTotalPrice() == total){
                orderDTOList.add(orderDTO);
            }else{
                if((getSumPaid(orderDTO) + orderDTO.getTotalPrice()) != total) orderDTOList.add(orderDTO);
            }

        }
        return orderDTOList;
    }

    private double getSumPaid(OrderDTO dto){
        double sum = 0;
        for(PaymentDTO paymentDTO : dto.getPayments()){
            sum += paymentDTO.getAmount() - paymentDTO.getExpense();
        }

        return sum;
    }

    private int parseInt(String data){
        int result = 0;
        if(data.length() > 2){
            data = data.replaceAll("\\.","");
            result = Integer.parseInt(data);
        }else result = Integer.parseInt(data);;
        return result;
    }

    private void animateTicketing(){
        List<Node> nodes = containerInformation.getChildren().stream().toList();
        double delay = 0.0;
        for(Node node : nodes){
            new NodeAnimation().animate(node,delay,new SlideInRight());
            delay += 0.1;
        }
    }

    public void toPDF() {

        try (FileOutputStream fos = new FileOutputStream("C:/Users/Public/creance.pdf")) {

            Document document = new Document(PageSize.A4, 36, 36, 36, 60);
            //Document document = new Document();

            PdfWriter writer = PdfWriter.getInstance(document, fos);
            writer.setPageEvent(new PageNumberEvent());

            document.open();
            document.add(createTitle());
            document.add(createMainTable());
            document.add(edited());

            document.close();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Paragraph createTitle() {
        Font boldFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
        Paragraph titlePage = new Paragraph(title,boldFont);
        titlePage.setAlignment(Element.ALIGN_CENTER);
        return titlePage;
    }

    private PdfPTable createMainTable() throws DocumentException {

        PdfPTable table = new PdfPTable(9);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{2.5f,1.8f, 1.5f, 1.2f, 2f, 0.7f, 2f, 2f, 2f});

        String[] headers = {
                "CLIENT", "DATE", "Qté", "PRIX", "MONTANT",
                "N°", "TOT AVCE", "DEPENSE", "RESTE"
        };

        addRow(table, Font.BOLD, headers);

        int sum_amount_client = 0; int sum_paid_client = 0;
        int sum_expense_client = 0; int sum_remain_client = 0;
        int sum_bread_customer = 0; int sum_nbPayment_client = 0;

        String currentCustomer = "";

        for (int i = 0; i < VARIABLE_STATIC.followSaleInfos.size(); i++) {

            FollowSaleInfo f = VARIABLE_STATIC.followSaleInfos.get(i);

            String customerName = f.getCustomerData();

            String displayCustomer = customerName.equals(currentCustomer) ? "-" : customerName;

            addRow(table, 0,
                    displayCustomer,
                    f.getDateData(),
                    f.getBreadData(),
                    f.getPriceData(),
                    f.getAmountData(),
                    f.getNbPaymentData(),
                    f.getPaidData(),
                    f.getExpenseData(),
                    f.getRemainData()
            );

            currentCustomer = customerName;

            sum_amount_client += parseInt(f.getAmountData());
            sum_paid_client += parseInt(f.getPaidData());
            sum_expense_client += parseInt(f.getExpenseData());
            sum_remain_client += parseInt(f.getRemainData());
            sum_bread_customer += parseInt(f.getBreadData());
            sum_nbPayment_client += parseInt(f.getNbPaymentData());

            boolean isLast = (i == VARIABLE_STATIC.followSaleInfos.size() - 1);
            boolean nextDifferent = !isLast &&
                    !customerName.equals(VARIABLE_STATIC.followSaleInfos.get(i + 1).getCustomerData());

            if (isLast || nextDifferent) {

                addRow(table, Font.BOLD,
                        customerName,
                        "-",
                        controlsOption.thousandSeparator(sum_bread_customer),
                        "-",
                        controlsOption.thousandSeparator(sum_amount_client),
                        controlsOption.thousandSeparator(sum_nbPayment_client),
                        controlsOption.thousandSeparator(sum_paid_client),
                        controlsOption.thousandSeparator(sum_expense_client),
                        controlsOption.thousandSeparator(sum_remain_client)
                );

                sum_amount_client = 0;
                sum_paid_client = 0;
                sum_expense_client = 0;
                sum_remain_client = 0;
                sum_bread_customer = 0;
                sum_nbPayment_client = 0;
            }

        }
        List<String> cashOutData = getCashOutData();
        addRow(table,Font.BOLD,
                "TOTAL",
                cashOutData.getFirst()+" ventes",
                cashOutData.get(1),
                "-",
                cashOutData.get(3),
                cashOutData.get(2),
                cashOutData.get(4),
                cashOutData.get(5),
                cashOutData.get(6)
                );

        return table;
    }

    private List<String> getCashOutData()  {

        List<String> list = new ArrayList<>();

        for (Node node : containerInformation.getChildren()) {

            Label label = (Label) node;
            String result = label.getText();

            if(result.contains("Ar")){
                String amount = result.split(" : ")[1];
                list.add(amount.split(" ")[0]);
            }else list.add(result.split(" : ")[1]);
        }

        return  list;
    }

    private void addRow(PdfPTable table, int bold, String... values) {
        for (String val : values) {
            table.addCell(createCenteredCell(val,bold));
        }
    }

    private PdfPCell createCenteredCell(String text,int bold) {
        Font font = new Font(Font.FontFamily.HELVETICA, 8,bold);

        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(5);
        cell.setBorderColor(BaseColor.BLACK);

        return cell;
    }

    private Paragraph edited() {
        Font font = new Font(Font.FontFamily.HELVETICA, 10);
        String date = LocalDate.now().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG));
        Paragraph titlePage = new Paragraph("Edite à Mahajanga ( LA CASA MOFO ), le "+date,font);
        titlePage.setAlignment(Element.ALIGN_RIGHT);
        return titlePage;
    }

}
