package com.example.milfittracker.ui.analysis;

import android.os.Bundle;
import android.Manifest;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.*;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.LifecycleCameraController;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executor;
import com.google.common.util.concurrent.ListenableFuture;
import com.example.milfittracker.R;

public class PhotosFragment extends Fragment {
    private static final String TAG = "CameraX";
    private static final int REQ = 10;
    private static final String[] PERMS = new String[]{"android.permission.CAMERA"};

    private PreviewView pView;
    private ImageCapture imageCapture;
    private ProcessCameraProvider cameraProvider;
    private LifecycleCameraController controller;

    private Executor mainExecutor;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_photos, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainExecutor = ContextCompat.getMainExecutor(requireContext());

        pView = view.findViewById(R.id.pView);
        pView.post(() ->
                Log.d("CameraX", "PreviewView size = " + pView.getWidth() + "x" + pView.getHeight()));

        ImageButton takePic = view.findViewById(R.id.takePic);

        pView.setImplementationMode((PreviewView.ImplementationMode.PERFORMANCE));
        pView.setScaleType(PreviewView.ScaleType.FILL_CENTER);

        if (allPermsGranted()) startCamera();
        else requestPermissions(new String[]{Manifest.permission.CAMERA}, REQ);

        if (takePic != null)
            takePic.setOnClickListener(v -> takePhoto());

        pView.getPreviewStreamState().observe(
                getViewLifecycleOwner(),
                state -> Log.d("CameraX", "Preview stream state: " + state));
    }

    private boolean allPermsGranted() {
        return ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> future =
                ProcessCameraProvider.getInstance(requireContext());

        future.addListener(() -> {
            try {
                cameraProvider = future.get();

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(pView.getSurfaceProvider());

                imageCapture = new ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .build();

                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(
                        getViewLifecycleOwner(),
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        imageCapture
                );

                Log.d(TAG, "Manual bind done; PreviewView attached? " + pView.isAttachedToWindow());
            } catch (Exception e) {
                Log.e(TAG, "startCamera failed", e);
                Toast.makeText(requireContext(), "Camera failed to start", Toast.LENGTH_SHORT).show();
            }
        }, mainExecutor);
    }

    @Override
    public void onRequestPermissionsResult(int code, @NonNull String[] perms, @NonNull int[] res) {
        super.onRequestPermissionsResult(code, perms, res);
        if (code == REQ && allPermsGranted()) {
            startCamera();
        }
    }

    private void takePhoto() {
        File dir = new File(requireContext().getFilesDir(), "photos");
        if (!dir.exists()) dir.mkdirs();

        String ts = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        File photoFile = new File(dir, "IMG_" + ts + ".jpg");

        ImageCapture.OutputFileOptions opts = new ImageCapture.OutputFileOptions.Builder(photoFile).build();

        imageCapture.takePicture(
                opts,
                ContextCompat.getMainExecutor(requireContext()),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults output) {
                        Log.i(TAG, "Saved: " + photoFile.getAbsolutePath());
                        Toast.makeText(requireContext(), "Saved photo", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onError(@NonNull ImageCaptureException exc) {
                        Log.e(TAG, "Capture failed: " + exc.getMessage(), exc);
                        Toast.makeText(requireContext(), "Capture failed", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (pView != null) pView.setController(null);
        controller = null;
        pView = null;
    }
}
