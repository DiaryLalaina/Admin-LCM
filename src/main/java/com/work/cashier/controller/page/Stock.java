package com.work.cashier.controller.page;

import animatefx.animation.*;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import com.work.cashier.Application;
import com.work.cashier.api.ApiClient;
import com.work.cashier.constants.VARIABLE_STATIC;
import com.work.cashier.controller.infoTable.ArticleInfo;
import com.work.cashier.controller.infoTable.IngredientInfo;
import com.work.cashier.controller.infoTable.StockTransactionInfo;
import com.work.cashier.data_transfert_object.ingredient.IngredientDTO;
import com.work.cashier.data_transfert_object.product.ProductDTO;
import com.work.cashier.data_transfert_object.stock.StockTransactionDTO;
import com.work.cashier.data_transfert_object.unitOption.Unit;
import com.work.cashier.data_transfert_object.unitOption.UnitConverter;
import com.work.cashier.print.Print;
import com.work.cashier.service.ControlsOption;
import com.work.cashier.service.NodeAnimation;
import com.work.cashier.service.PageNumberEvent;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import lombok.Getter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.ResourceBundle;

public class Stock implements Initializable {

    @FXML
    private JFXDatePicker startDate,endDate;

    @FXML
    private VBox containerRecipe,containerIngredient,containerArticle,containerListTransactions;

    @FXML
    private JFXTextField nbProduct,filter;

    @FXML
    private BarChart<String, Number> barChart;

    @FXML
    private JFXComboBox<String> ingredientChoice;

    @FXML
    private JFXButton transactionBtn,printBtn;

    @FXML
    private Label entered,sorted;

    @Getter
    private static JFXTextField nbProductStatic;

    @Getter
    private static VBox containerListIngredient,containerListArticle,containerListRecipe;

    @Getter
    private static LocalDate dateStock;

    private final ControlsOption controlsOption = new ControlsOption();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        controlsOption.jfxButtonOption(printBtn,"fa-print",Color.BLACK);
        controlsOption.jfxButtonOption(transactionBtn,"fa-list-ul", Color.DARKGREEN);
        ingredientChoice.getItems().addAll("AMELIORANT","HUILE","FARINE","SEL","CARBURANT","LEVURE");
        ingredientChoice.setValue("FARINE");
        startDate.setValue(LocalDate.now().minusDays(3));
        endDate.setValue(LocalDate.now());
        setNbProductStatic(nbProduct);
        setContainerListIngredient(containerIngredient);
        setContainerListArticle(containerArticle);
        setContainerListRecipe(containerRecipe);
        Platform.runLater(()->{
            fillTableIngredient();
            fillTableArticle();
            displayTransactions();
        });
    }

    @FXML
    void onMouseClicked(){
        nbProduct.setLabelFloat(true);
        String nb = nbProduct.getText().split(" ")[0];
        nbProduct.setText(nb);
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
    void print(){
        toPDF();
        new Print().printPDF("transaction");
    }

    @FXML
    void displayTransactions(){
        fillTableTransaction(ingredientChoice.getValue());
    }

    public static void fillTableIngredient(){
        String url = "http://192.168.7.2:8080/ingredient/getList";
        containerListIngredient.getChildren().clear();
        List<IngredientDTO> list = ApiClient.getAll(url, IngredientDTO.class);

        double delay = 0.0;

        for(IngredientDTO ingredientDTO : list){
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(Application.class.getResource("info_table/ingredientInfo.fxml"));
            try {
                HBox hbox = fxmlLoader.load();
                IngredientInfo info = fxmlLoader.getController();
                info.setIngredientDTO(ingredientDTO);
                info.setData();
                containerListIngredient.getChildren().add(hbox);


                new NodeAnimation().animate(hbox,delay,new ZoomInLeft());

                delay += 0.1;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void fillTableTransaction(String ingredient){
        VARIABLE_STATIC.stockTransactionInfos.clear();
        int sum_entered = 0;
        int sum_sorted = 0;
        String unit = "";
        String url = "http://192.168.7.2:8080/stockTransaction/getListByDate?startDate="+startDate.getValue()
                +"&endDate="+endDate.getValue();
        containerListTransactions.getChildren().clear();
        List<StockTransactionDTO> list = ApiClient.getAll(url, StockTransactionDTO.class);

        double delay = 0.0;

        list = list.stream()
                .filter(t -> t.getIngredientName().equalsIgnoreCase(ingredient))
                .toList();

        if(!filter.getText().isEmpty() || filter.getText() != null){
            list = list.stream()
                    .filter(t -> t.getReason().contains(filter.getText()))
                    .toList();
        }

        for(StockTransactionDTO dto : list){
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(Application.class.getResource("info_table/stockTransactionInfo.fxml"));
            try {
                HBox hbox = fxmlLoader.load();
                StockTransactionInfo info = fxmlLoader.getController();
                info.setDto(dto);
                info.setData();
                containerListTransactions.getChildren().add(hbox);
                double used = dto.getQuantityAfter() - dto.getQuantityBefore();
                if(used < 0){
                    sum_sorted += dto.getQuantityUsed();
                }else sum_entered += dto.getQuantityUsed();

                new NodeAnimation().animate(hbox,delay,new Pulse());

                delay += 0.1;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            unit = dto.getUnitType();
        }
        entered.setText("Entree:"+controlsOption.thousandSeparator(sum_entered)
                +" "+UnitConverter.abbreviate(unit)+"("+converted(sum_entered)+")");
        sorted.setText("Sortie:"+controlsOption.thousandSeparator(sum_sorted)
                +" "+UnitConverter.abbreviate(unit)+"("+converted(sum_sorted)+")");
    }

    private String converted(double sum){
        switch (ingredientChoice.getValue()) {
            case "FARINE" -> {
                return String.format("%.2f", UnitConverter.
                        convert(sum, Unit.KILOGRAM, Unit.SAC_50Kg)) + " " +
                        UnitConverter.abbreviate(String.valueOf(Unit.SAC_50Kg));
            }
            case "AMELIORANT", "LEVURE" -> {
                return String.format("%.2f", UnitConverter.
                        convert(sum, Unit.GRAM, Unit.CARTON_20Pcs)) + " " +
                        UnitConverter.abbreviate(String.valueOf(Unit.CARTON_20Pcs));
            }
            case "SEL" -> {
                return String.format("%.2f", UnitConverter.
                        convert(sum, Unit.GRAM, Unit.SACHET)) + " " +
                        UnitConverter.abbreviate(String.valueOf(Unit.SACHET));
            }
            case "CARBURANT" -> {
                return String.format("%.2f", UnitConverter.
                        convert(sum, Unit.LITER, Unit.BIDON)) + " " +
                        UnitConverter.abbreviate(String.valueOf(Unit.BIDON));
            }
            default -> {
                return "";
            }
        }
    }

    public static void fillTableArticle(){
        String url = "http://192.168.7.2:8080/product/getList";
        containerListArticle.getChildren().clear();
        List<ProductDTO> list = ApiClient.getAll(url, ProductDTO.class);

        double delay = 0.0;

        for(ProductDTO productDTO : list){
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(Application.class.getResource("info_table/articleInfo.fxml"));
            try {
                HBox hbox = fxmlLoader.load();
                ArticleInfo info = fxmlLoader.getController();
                info.setProductDTO(productDTO);
                info.setData();
                containerListArticle.getChildren().add(hbox);

                new NodeAnimation().animate(hbox,delay,new ZoomInUp());

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void toPDF() {

        try (FileOutputStream fos = new FileOutputStream("C:/Users/Public/transaction.pdf")) {

            Document document = new Document();
            PdfWriter writer = PdfWriter.getInstance(document, fos);
            document.open();

            document.add(createTitle());
            document.add(createSubtitle());
            document.add(createLegend());
            document.add(createMainTable());

            writer.setPageEvent(new PageNumberEvent());
            document.close();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Paragraph createTitle() {
        Font boldFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
        Paragraph title = new Paragraph("TRANSACTIONS du "+
                startDate.getValue().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT))
                + " au "+
                endDate.getValue().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT))+"\n",boldFont);
        title.setAlignment(Element.ALIGN_CENTER);
        return title;
    }

    private Paragraph createSubtitle() {
        String ingredient = "Ingredient : "+ingredientChoice.getValue().toUpperCase();
        String filterTransaction = (filter.getText().isEmpty() || filter.getText() == null) ?
                "" : "     Filtrage : "+filter.getText();

        Paragraph subtitle = new Paragraph(ingredient +filterTransaction+ "\n\n");
        subtitle.setAlignment(Element.ALIGN_CENTER);
        return subtitle;
    }

    private PdfPTable createLegend() throws DocumentException {

        PdfPTable table = new PdfPTable(14);
        table.setWidthPercentage(100);
        table.getDefaultCell().setBorder(Rectangle.NO_BORDER);

        table.setWidths(new float[]{0.5f, 1.5f,0.2f, 0.5f, 1.5f, 0.2f,0.5f, 1.5f, 0.2f,0.5f, 1.5f, 0.2f,0.5f, 2.5f});

        table.addCell(createStatusCell("ACHAT",Rectangle.NO_BORDER));
        table.addCell(createCenteredCell(":ACHAT"));
        table.addCell(createCenteredCell(""));
        table.addCell(createStatusCell("EMPRUNT",Rectangle.NO_BORDER));
        table.addCell(createCenteredCell(":EMPRUNT"));
        table.addCell(createCenteredCell(""));
        table.addCell(createStatusCell("MAPOESA",Rectangle.NO_BORDER));
        table.addCell(createCenteredCell(":MAPOESA"));
        table.addCell(createCenteredCell(""));
        table.addCell(createStatusCell("PRET",Rectangle.NO_BORDER));
        table.addCell(createCenteredCell(":PRET"));
        table.addCell(createCenteredCell(""));
        table.addCell(createStatusCell("REMBOURSEMENT",Rectangle.NO_BORDER));
        table.addCell(createCenteredCell(":REMBOURSEMENT"));

        return table;
    }

    private PdfPTable createMainTable() throws DocumentException {

        PdfPTable table = new PdfPTable(8);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1.8f, 1.5f, 3.5f, 1.8f, 1.8f, 1.8f, 1.8f, 1f});

        String[] units = getUnit();

        String[] headers = {
                "DATE", "HEURE", "LIBELLE", "ENTREE",
                "SORTIE", "Stock ("+units[0]+")", "Stock ("+units[1]+")",
                ""
        };

        double entered = 0;
        double sorted = 0;

        addRow(table,1, headers);

        for (StockTransactionInfo s : VARIABLE_STATIC.stockTransactionInfos) {

            addRow(table,0,
                    s.getDateData(),
                    s.getTimeData(),
                    s.getReasonData(),
                    s.getEnteredData(),
                    s.getSortedData(),
                    s.getStockData(),
                    s.getConversionData()
            );
            table.addCell(createStatusCell(s.getReasonData(),Rectangle.BOX));

            entered += s.getEnteredData().isEmpty() ? 0 : Double.parseDouble(s.getEnteredData().split("\\.")[0]);
            sorted += s.getSortedData().isEmpty() ? 0 : Double.parseDouble(s.getSortedData().split("\\.")[0]);
        }

        String[] footers = {"","","TOTAL",controlsOption.thousandSeparator(entered),
                controlsOption.thousandSeparator(sorted),"","",""};

        addRow(table,1, footers);

        return table;
    }

    private void addRow(PdfPTable table, int bold, String... values) {
        for (String val : values) {
            table.addCell(createCenteredCell(val,bold));
        }
    }

    private PdfPCell createCenteredCell(String text, int count) {
        Font font = new Font(Font.FontFamily.HELVETICA, 9,count);

        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBorder(Rectangle.BOX);
        cell.setPadding(5);

        return cell;
    }

    private PdfPCell createCenteredCell(String text) {
        Font font = new Font(Font.FontFamily.HELVETICA, 9);

        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(Rectangle.LEFT);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(5);

        return cell;
    }

    private String[] getUnit(){
        String[] units = new String[0];

        switch (ingredientChoice.getValue()) {
            case "FARINE" -> units = new String[]{"KG","SAC"};
            case "AMELIORANT", "LEVURE" -> units = new String[]{"G","CTN"};
            case "SEL" -> units = new String[]{"G","SCS"};
            case "CARBURANT" -> units = new String[]{"L","BDN"};
            default -> {
            }
        }
        return units;
    }

    private PdfPCell createStatusCell(String reason,int border) {

        Font font = new Font(Font.FontFamily.HELVETICA, 1);

        PdfPCell cell = new PdfPCell(new Phrase(" ", font));
        cell.setFixedHeight(18);
        cell.setPadding(2);

        PdfPTable inner = new PdfPTable(1);
        PdfPCell square = new PdfPCell();
        square.setFixedHeight(10);
        square.setBorder(Rectangle.NO_BORDER);

        if (reason.contains("ACHAT")) {
            square.setBackgroundColor(BaseColor.GREEN);
        } else if (reason.contains("EMPRUNT")) {
            square.setBackgroundColor(BaseColor.YELLOW);
        } else if (reason.contains("PRET")) {
            square.setBackgroundColor(BaseColor.RED);
        } else if(reason.contains("MAPOESA")){
            square.setBackgroundColor(BaseColor.BLUE);
        } else if(reason.contains("REMBOURSEMENT")){
            square.setBackgroundColor(BaseColor.LIGHT_GRAY);
        }else cell.setBackgroundColor(BaseColor.WHITE);

        inner.addCell(square);

        cell.addElement(inner);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBorder(border);

        return cell;
    }

    public static void setContainerListIngredient(VBox containerListIngredient) {
        Stock.containerListIngredient = containerListIngredient;
    }

    public static void setContainerListArticle(VBox containerListArticle) {
        Stock.containerListArticle = containerListArticle;
    }

    public static void setContainerListRecipe(VBox containerListRecipe) {
        Stock.containerListRecipe = containerListRecipe;
    }

    public static void setNbProductStatic(JFXTextField nbProductStatic) {
        Stock.nbProductStatic = nbProductStatic;
    }
}
