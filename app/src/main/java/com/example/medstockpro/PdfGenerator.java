package com.example.medstockpro;

import static com.example.medstockpro.PatientList.patientDetailsAge;
import static com.example.medstockpro.PatientList.patientDetailsEmail;
import static com.example.medstockpro.PatientList.patientDetailsId;
import static com.example.medstockpro.PatientList.patientDetailsName;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.util.Map;

public class PdfGenerator {

    public static void generatePrescriptionPdf(Map<String, Integer> prescriptionData, String filePath) {
        Document document = new Document();

        try {
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            document.add(new Paragraph("Name: " + patientDetailsName));
            document.add(new Paragraph("Age: " + patientDetailsAge));
            document.add(new Paragraph("ID: " + patientDetailsId));
            document.add(new Paragraph("Email: " + patientDetailsEmail));
            document.add(new Paragraph("-----------------------------------------------------------------------------"));
            document.add(new Paragraph("Medicine Counts:"));

            for (Map.Entry<String, Integer> entry : prescriptionData.entrySet()) {
                String medicineName = entry.getKey();
                int count = entry.getValue();

                // Add medicine name and count to the PDF document
                document.add(new Paragraph(medicineName + " - " + count + " Tablets"));
            }

        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            document.close();
        }
    }
}

