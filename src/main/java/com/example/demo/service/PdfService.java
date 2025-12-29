package com.example.demo.service;

import com.example.demo.entity.Compra;
import com.example.demo.entity.Ingreso;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PdfService {

    public byte[] generarReporteFinanzas(
            List<Ingreso> ingresos,
            List<Compra> compras,
            Double totalIngresos,
            Double totalGastos,
            Double balance,
            String periodo
    ) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Título
            Paragraph titulo = new Paragraph("Reporte Financiero")
                    .setFontSize(20)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(titulo);

            // Período
            Paragraph periodoP = new Paragraph("Período: " + periodo)
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20);
            document.add(periodoP);

            // Resumen
            document.add(new Paragraph("Resumen Financiero")
                    .setFontSize(16)
                    .setBold()
                    .setMarginBottom(10));

            Table resumenTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}))
                    .useAllAvailableWidth();

            resumenTable.addCell(createCell("Total Ingresos:", true));
            resumenTable.addCell(createCell("$" + String.format("%.2f", totalIngresos), false));
            
            resumenTable.addCell(createCell("Total Gastos:", true));
            resumenTable.addCell(createCell("$" + String.format("%.2f", totalGastos), false));
            
            resumenTable.addCell(createCell("Balance Neto:", true));
            Cell balanceCell = createCell("$" + String.format("%.2f", balance), false);
            if (balance >= 0) {
                balanceCell.setBackgroundColor(new DeviceRgb(39, 174, 96));
                balanceCell.setFontColor(ColorConstants.WHITE);
            } else {
                balanceCell.setBackgroundColor(new DeviceRgb(231, 76, 60));
                balanceCell.setFontColor(ColorConstants.WHITE);
            }
            resumenTable.addCell(balanceCell);

            document.add(resumenTable);
            document.add(new Paragraph("\n"));

            // Tabla de Ingresos
            if (!ingresos.isEmpty()) {
                document.add(new Paragraph("Ingresos")
                        .setFontSize(14)
                        .setBold()
                        .setMarginBottom(10));

                Table ingresosTable = new Table(UnitValue.createPercentArray(new float[]{1, 1, 2, 1}))
                        .useAllAvailableWidth();

                ingresosTable.addHeaderCell(createHeaderCell("Fecha"));
                ingresosTable.addHeaderCell(createHeaderCell("Tipo"));
                ingresosTable.addHeaderCell(createHeaderCell("Descripción"));
                ingresosTable.addHeaderCell(createHeaderCell("Monto"));

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                for (Ingreso ingreso : ingresos) {
                    ingresosTable.addCell(createCell(ingreso.getFecha().format(formatter), false));
                    ingresosTable.addCell(createCell(ingreso.getTipo(), false));
                    ingresosTable.addCell(createCell(ingreso.getDescripcion(), false));
                    ingresosTable.addCell(createCell("$" + String.format("%.2f", ingreso.getMonto()), false));
                }

                document.add(ingresosTable);
                document.add(new Paragraph("\n"));
            }

            // Tabla de Gastos
            if (!compras.isEmpty()) {
                document.add(new Paragraph("Gastos")
                        .setFontSize(14)
                        .setBold()
                        .setMarginBottom(10));

                Table gastosTable = new Table(UnitValue.createPercentArray(new float[]{1, 1, 2, 1}))
                        .useAllAvailableWidth();

                gastosTable.addHeaderCell(createHeaderCell("N° Factura"));
                gastosTable.addHeaderCell(createHeaderCell("Fecha"));
                gastosTable.addHeaderCell(createHeaderCell("Proveedor"));
                gastosTable.addHeaderCell(createHeaderCell("Total"));

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                for (Compra compra : compras) {
                    gastosTable.addCell(createCell(compra.getNumeroFactura(), false));
                    gastosTable.addCell(createCell(compra.getFechaCompra().format(formatter), false));
                    gastosTable.addCell(createCell(compra.getProveedor().getNombre(), false));
                    gastosTable.addCell(createCell("$" + String.format("%.2f", compra.getTotal()), false));
                }

                document.add(gastosTable);
            }

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error generando PDF: " + e.getMessage(), e);
        }
    }

    private Cell createHeaderCell(String text) {
        return new Cell()
                .add(new Paragraph(text).setBold())
                .setBackgroundColor(new DeviceRgb(44, 62, 80))
                .setFontColor(ColorConstants.WHITE)
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(8);
    }

    private Cell createCell(String text, boolean bold) {
        Paragraph p = new Paragraph(text);
        if (bold) p.setBold();
        return new Cell()
                .add(p)
                .setPadding(5);
    }
}
