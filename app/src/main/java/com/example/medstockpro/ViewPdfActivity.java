package com.example.medstockpro;

import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ViewPdfActivity extends AppCompatActivity {

    private ImageView pdfImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pdf);

        pdfImageView = findViewById(R.id.pdfImageView);

        //Copy the PDF file from assets to internal storage
        copyPdfToInternalStorage();

        //Display the PDF
        displayPdf();
    }

    private void copyPdfToInternalStorage() {
        try {
            File externalStorageDir = getExternalFilesDir(null);
            File externalFile = new File(externalStorageDir, "prescription.pdf");
            File internalFile = new File(getFilesDir(), "prescription.pdf");

            //Create input stream for external file
            InputStream inputStream = new FileInputStream(externalFile);

            FileOutputStream outputStream = new FileOutputStream(internalFile);
            byte[] buffer = new byte[1024];
            int length;

            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void displayPdf() {
        try {
            File externalStorageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File pdfFile = new File(externalStorageDir, "prescription.pdf");

            ParcelFileDescriptor fileDescriptor = ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY);
            PdfRenderer pdfRenderer = new PdfRenderer(fileDescriptor);
            PdfRenderer.Page pdfPage = pdfRenderer.openPage(0);

            //Create a bitmap and render the PDF page onto it
            Bitmap bitmap = Bitmap.createBitmap(pdfPage.getWidth(), pdfPage.getHeight(), Bitmap.Config.ARGB_8888);
            pdfPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

            //Display the bitmap in an ImageView
            pdfImageView.setImageBitmap(bitmap);

            //Close the PdfRenderer and the file descriptor
            pdfPage.close();
            pdfRenderer.close();
            fileDescriptor.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

