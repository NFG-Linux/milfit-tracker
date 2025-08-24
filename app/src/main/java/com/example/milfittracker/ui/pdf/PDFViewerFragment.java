package com.example.milfittracker.ui.pdf;

import android.graphics.pdf.PdfRenderer;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.io.IOException;
import java.io.File;
import java.io.InputStream;
import java.io.FileOutputStream;
import com.example.milfittracker.R;

public class PDFViewerFragment extends Fragment {
    private PdfRenderer pdfRenderer;
    private PdfRenderer.Page currentPage;
    private ParcelFileDescriptor fileDescriptor;
    private ImageView pdfImageView;
    private int currentPageIndex = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pdf_viewer, container, false);
        pdfImageView = view.findViewById(R.id.pdfImageView);
        Button prev = view.findViewById(R.id.Prev);
        Button next = view.findViewById(R.id.Next);

        try {
            File file = new File(requireContext().getCacheDir(), "NavyPRTstandards.pdf");
            if (!file.exists()) {
                InputStream asset = requireContext().getAssets().open("NavyPRTstandards.pdf");
                FileOutputStream out = new java.io.FileOutputStream(file);
                byte[] buffer = new byte[1024];
                int read;
                while ((read = asset.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }
                asset.close();
                out.close();
            }

            fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
            pdfRenderer = new PdfRenderer(fileDescriptor);
            showPage(currentPageIndex);

        } catch (IOException e) {
            e.printStackTrace();
        }

        prev.setOnClickListener(v -> {
            if (currentPageIndex > 0) {
                currentPageIndex--;
                showPage(currentPageIndex);
            }
        });

        next.setOnClickListener(v -> {
            if (pdfRenderer != null && currentPageIndex < pdfRenderer.getPageCount() - 1) {
                currentPageIndex++;
                showPage(currentPageIndex);
            }
        });

        return view;
    }

    private void showPage(int index) {
        if (pdfRenderer == null || pdfRenderer.getPageCount() <= index) return;

        if (currentPage != null) {
            currentPage.close();
        }

        currentPage = pdfRenderer.openPage(index);

        Bitmap bitmap = Bitmap.createBitmap(
                currentPage.getWidth(),
                currentPage.getHeight(),
                Bitmap.Config.ARGB_8888
        );
        currentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

        pdfImageView.setImageBitmap(bitmap);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            if (currentPage != null) currentPage.close();
            if (pdfRenderer != null) pdfRenderer.close();
            if (fileDescriptor != null) fileDescriptor.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}