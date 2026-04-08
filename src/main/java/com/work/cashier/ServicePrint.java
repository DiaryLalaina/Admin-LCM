package com.work.cashier;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.work.cashier.api.ApiClient;
import com.work.cashier.data_transfert_object.payment.PaymentDTO;
import com.work.cashier.service.ControlsOption;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.List;

public class ServicePrint {

    public void toPDF() {

        Document document = new Document();

        try {
            PdfWriter.getInstance(document,new FileOutputStream("C:/Users/Public/test.pdf"));
            document.open();

            PdfPTable table = new PdfPTable(4);

            table.addCell("DATE_VENTE");
            table.addCell("Ticket");
            table.addCell("MONTANT");
            table.addCell("DEPENSE");

            String url = "http://192.168.7.2:8080/payment/getListCashOut?startDate="+ LocalDate.now()
                    +"&endDate="+ LocalDate.now();
            List<PaymentDTO> list = ApiClient.getAll(url, PaymentDTO.class);

            ControlsOption controlsOption = new ControlsOption();
            for(PaymentDTO paymentDTO : list){
                table.addCell(paymentDTO.getCreatedAt());
                table.addCell(paymentDTO.getTicket());
                table.addCell(controlsOption.thousandSeparator(paymentDTO.getAmount()));
                table.addCell(controlsOption.thousandSeparator(paymentDTO.getExpense()));
            }
            document.add(table);
        } catch (DocumentException | FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        document.close();
    }

}
