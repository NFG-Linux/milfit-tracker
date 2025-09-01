package com.example.milfittracker.forecasting;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.fragment.app.Fragment;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.google.common.util.concurrent.ListenableFuture;
import com.example.milfittracker.R;

public class PhotosFragment extends Fragment {
    private static final int REQ = 10;
    private ImageButton takePic;
    private RecyclerView recyclerView;
    private PhotoAdapter adapter;
    private File photoDir;
    private PreviewView pView;
    private ImageCapture imageCapture;
    private ExecutorService camExec;
    private ActivityResultLauncher<String> requestPermissionLauncher;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photos, container, false);

        takePic = view.findViewById(R.id.takePic);
        recyclerView = view.findViewById(R.id.photo_recycler);

        photoDir = new File(requireContext().getFilesDir(), "photos");
        if (!photoDir.exists()) photoDir.mkdirs();

        pView = view.findViewById(R.id.pView);

        adapter = new PhotoAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        loadPhotos();

        camExec = Executors.newSingleThreadExecutor();

        takePic.setOnClickListener(v -> checkCameraPermission());

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestPermissionLauncher =
                registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                    if (isGranted) {
                        openCamera();
                    } else {
                        Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void openCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext());

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                Preview pview = new Preview.Builder().build();
                pview.setSurfaceProvider(pView.getSurfaceProvider());

                imageCapture = new ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .build();

                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(this, cameraSelector, pview, imageCapture);

                takePic.setOnClickListener(v -> takePhoto());

            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(requireContext()));
    }

    private void takePhoto() {
        if (imageCapture == null) return;

        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                .format(new Date());
        File photoFile = new File(photoDir, "IMG_" + timestamp + ".jpg");

        ImageCapture.OutputFileOptions outputOptions =
                new ImageCapture.OutputFileOptions.Builder(photoFile).build();

        imageCapture.takePicture(outputOptions, camExec, new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        requireActivity().runOnUiThread(() -> {
                            Toast.makeText(requireContext(), "Photo saved", Toast.LENGTH_SHORT).show();
                            loadPhotos();
                        });
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        exception.printStackTrace();
                    }
                });
    }

    private void loadPhotos() {
        File[] files = photoDir.listFiles();
        if (files == null) return;

        Map<String, List<File>> grouped = new TreeMap<>(Collections.reverseOrder());
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());

        for (File f : files) {
            long lastModified = f.lastModified();
            String key = sdf.format(new Date(lastModified));
            grouped.computeIfAbsent(key, k -> new ArrayList<>()).add(f);
        }

        adapter.setData(grouped);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (camExec != null) {
            camExec.shutdown();
        }
    }
}
