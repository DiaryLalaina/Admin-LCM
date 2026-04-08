package com.work.cashier.service;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

public class PageNumberEvent extends PdfPageEventHelper {

    Font font = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC);

    @Override
    public void onEndPage(PdfWriter writer, Document document) {

        PdfContentByte cb = writer.getDirectContent();

        String text = String.valueOf(writer.getPageNumber());

        float x = (document.right() + document.left()) / 2;
        float y = document.bottom() / 2; // Position dans le footer

        ColumnText.showTextAligned(cb,
                Element.ALIGN_CENTER,
                new Phrase(text, font),
                x,
                y,
                0);
    }
}