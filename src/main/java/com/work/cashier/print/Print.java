package com.work.cashier.print;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.work.cashier.notifications.NotificationType;
import com.work.cashier.notifications.NotificationsBuilder;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.Setter;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

@Setter
@Getter
public class Print {

    private VBox printPage;

    // PRINT TO PDF FILE
    public void toPdfFile(String namePDF) throws IOException {
        WritableImage snapshot = printPage.snapshot(new SnapshotParameters(), null);
        File imageFile = new File("capture.png");
        ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), "png", imageFile);

        String nameFile = namePDF+" "+ LocalDate.now().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM));
        PdfWriter writer = new PdfWriter("C:/Users/Public/PDF File/"+nameFile+".pdf");
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc, PageSize.A4);

        Image image = new Image(ImageDataFactory.create("capture.png"));

        float maxWidth = PageSize.A4.getWidth();
        float maxHeight = PageSize.A4.getHeight();

        image.scaleToFit(maxWidth, maxHeight);
        image.setAutoScale(true);
        image.setMarginTop(20);
        image.setMarginBottom(20);

        document.add(image);
        document.close();

        System.out.println("✅ PDF généré avec succès !");
        NotificationsBuilder.create(NotificationType.SUCCESS,"✅ PDF généré avec succès !");
    }

}
