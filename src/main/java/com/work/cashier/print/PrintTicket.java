package com.work.cashier.print;

import javafx.print.*;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PrintTicket {

    private VBox printPage;

    public void printVBox(Node node, String printerHint) {
        Printer printer = (printerHint == null || printerHint.isBlank())
                ? Printer.getDefaultPrinter()
                : Printer.getAllPrinters().stream()
                .filter(p -> p.getName().toLowerCase().contains(printerHint.toLowerCase()))
                .findFirst()
                .orElse(Printer.getDefaultPrinter());

        if (printer == null) {
            System.err.println("Aucune imprimante trouvée.");
            return;
        }

        PrinterJob job = PrinterJob.createPrinterJob(printer);

        double width = 80 * 2.3;
        PageLayout layout = printer.createPageLayout(
                Paper.A4,
                PageOrientation.PORTRAIT,
                Printer.MarginType.HARDWARE_MINIMUM
        );

        node.setScaleX(width / node.getBoundsInParent().getWidth());
        node.setScaleY(node.getScaleX());

        boolean success = job.printPage(layout, node);
        if (success) {
            job.endJob();
        } else {
            System.err.println("Échec impression");
        }
    }
}
