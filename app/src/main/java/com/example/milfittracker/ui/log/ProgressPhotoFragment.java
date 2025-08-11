package com.example.milfittracker.ui.log;

import android.Manifest;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import com.example.milfittracker.R;
import com.google.common.util.concurrent.ListenableFuture;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProgressPhotoFragment extends Fragment {

    private PreviewView pview;
    private ImageButton takePic;
    private ImageButton switchB;
    private ImageButton flash;

    private ExecutorService cameraExecutor;
    private ProcessCameraProvider cameraProvider;
    private CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;
    private ImageCapture imageCapture;
    private Camera camera;

    // Permissions
    private ActivityResultLauncher<String[]> permissionLauncher;

    public ProgressPhotoFragment() { /* required empty */ }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_progress_photo, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        pview = v.findViewById(R.id.pview);
        takePic  = v.findViewById(R.id.takePic);
        switchB   = v.findViewById(R.id.switchB);
        flash    = v.findViewById(R.id.flash);

        cameraExecutor = Executors.newSingleThreadExecutor();

        // Permission launcher
        permissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                result -> {
                    boolean allGranted = true;
                    for (Boolean granted : result.values()) {
                        allGranted &= (granted != null && granted);
                    }
                    if (allGranted) {
                        startCamera();
                    } else {
                        Toast.makeText(requireContext(),
                                "Camera permission required.", Toast.LENGTH_SHORT).show();
                    }
                });

        if (hasAllPermissions()) {
            startCamera();
        } else {
            permissionLauncher.launch(requiredPermissions());
        }

        takePic.setOnClickListener(view -> takePhoto());
        switchB.setOnClickListener(view -> {
            cameraSelector = (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA)
                    ? CameraSelector.DEFAULT_FRONT_CAMERA
                    : CameraSelector.DEFAULT_BACK_CAMERA;
            startCamera();
        });
        flash.setOnClickListener(view -> toggleFlash());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (cameraProvider != null) {
            cameraProvider.unbindAll();
        }
        if (cameraExecutor != null) {
            cameraExecutor.shutdown();
        }
    }

    /* ---------------- CameraX ---------------- */

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(requireContext());

        cameraProviderFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderFuture.get();

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(pview.getSurfaceProvider());

                imageCapture = new ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .setFlashMode(ImageCapture.FLASH_MODE_OFF)
                        .build();

                cameraProvider.unbindAll();
                camera = cameraProvider.bindToLifecycle(
                        (LifecycleOwner) getViewLifecycleOwner(),
                        cameraSelector,
                        preview,
                        imageCapture
                );

                // Update flash icon state
                updateFlashIcon();

            } catch (Exception e) {
                Toast.makeText(requireContext(),
                        "Failed to start camera: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        }, ContextCompat.getMainExecutor(requireContext()));
    }

    private void takePhoto() {
        if (imageCapture == null) return;

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, "milfit_" + timeStamp);
        values.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");

        // Save under Pictures/MilFitTracker (scoped storage friendly)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/MilFitTracker");
        }

        ImageCapture.OutputFileOptions outputOptions =
                new ImageCapture.OutputFileOptions.Builder(
                        requireContext().getContentResolver(),
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        values
                ).build();

        imageCapture.takePicture(
                outputOptions,
                cameraExecutor,
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults output) {
                        Uri uri = output.getSavedUri();
                        requireActivity().runOnUiThread(() ->
                                Toast.makeText(requireContext(),
                                        "Saved photo: " + (uri != null ? uri.toString() : "OK"),
                                        Toast.LENGTH_SHORT).show());
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exc) {
                        requireActivity().runOnUiThread(() ->
                                Toast.makeText(requireContext(),
                                        "Save failed: " + exc.getMessage(),
                                        Toast.LENGTH_SHORT).show());
                    }
                }
        );
    }

    private void toggleFlash() {
        if (imageCapture == null) return;
        int mode = imageCapture.getFlashMode();
        if (mode == ImageCapture.FLASH_MODE_OFF) {
            imageCapture.setFlashMode(ImageCapture.FLASH_MODE_ON);
        } else {
            imageCapture.setFlashMode(ImageCapture.FLASH_MODE_OFF);
        }
        updateFlashIcon();
    }

    private void updateFlashIcon() {
        if (imageCapture == null || flash == null) return;
        int mode = imageCapture.getFlashMode();
        flash.setImageResource(
                mode == ImageCapture.FLASH_MODE_ON
                        ? R.drawable.ic_flash_on
                        : R.drawable.ic_flash_off
        );
    }

    /* ---------------- Permissions helpers ---------------- */

    private boolean hasAllPermissions() {
        for (String p : requiredPermissions()) {
            if (ContextCompat.checkSelfPermission(requireContext(), p)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private String[] requiredPermissions() {
        ArrayList<String> list = new ArrayList<>();
        list.add(Manifest.permission.CAMERA);

        // Pre-Android 10 needs WRITE_EXTERNAL_STORAGE to insert into MediaStore
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            list.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        // Android 13+ often requires READ_MEDIA_IMAGES for gallery access/preview flows
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            list.add(Manifest.permission.READ_MEDIA_IMAGES);
        }
        return list.toArray(new String[0]);
    }
}