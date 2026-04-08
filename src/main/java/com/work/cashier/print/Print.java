package com.work.cashier.print;

import lombok.Getter;
import lombok.Setter;
import java.awt.print.PrinterJob;
import java.io.File;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.MediaSizeName;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPageable;
import org.apache.pdfbox.printing.Scaling;

@Setter
@Getter
public class Print {

    public void printPDF(String name) {

        try {
            PDDocument document = Loader.loadPDF(new File("C:/Users/Public/"+name+".pdf"));

            PrinterJob job = PrinterJob.getPrinterJob();
            job.setPageable(new PDFPageable(document));

            PrintRequestAttributeSet attr = new HashPrintRequestAttributeSet();
            attr.add(MediaSizeName.ISO_A4);

            if (job.printDialog()) {
                job.print(attr);
            }

            document.close();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
