package com.example.phototaker;

import android.os.Bundle;
import android.Manifest;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.lifecycle.LifecycleOwner;

import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Date;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private PreviewView viewFinder;
    private ImageCapture imageCapture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Get the PreviewView and the take photo button from the layout
        viewFinder = findViewById(R.id.viewFinder);

        Button takePhotoButton = findViewById(R.id.btn_take_photo);
        takePhotoButton.setOnClickListener(v -> {
            // Create a file to hold the image
            File photoFile = new File(getExternalFilesDir(null), new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US).format(new Date()) + ".jpg");

            // Setup image capture metadata
            ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(photoFile).build();

            // Setup image capture listener
            imageCapture.takePicture(outputFileOptions, ContextCompat.getMainExecutor(this), new ImageCapture.OnImageSavedCallback() {
                @Override
                public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                    // Create a new fragment and show it
                    PictureDialogFragment dialogFragment = new PictureDialogFragment();
                    // Pass the file path to PictureDialogFragment
                    Bundle args = new Bundle();
                    args.putString("imageFilePath", outputFileResults.getSavedUri().getPath());
                    dialogFragment.setArguments(args);

                    dialogFragment.show(getSupportFragmentManager(), "PictureDialogFragment");
                }

                @Override
                public void onError(@NonNull ImageCaptureException exception) {
                    // Handle the error
                    // Log the error
                    Log.e("MainActivity", "Photo capture failed: " + exception.getMessage(), exception);
                }
            });
        });

        // Check for camera permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // If not granted, request the permission
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, 0);
        } else {
            // If permission is already granted, start the camera
            startCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Check if the permission request was granted
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // If granted, start the camera
                startCamera();
            } else {
                // Permission request was denied.
                Log.e("MainActivity", "Permission request was denied for camera.");
            }
        }
    }

    private void startCamera() {
        // Get the camera provider
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                // Build the preview
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(viewFinder.getSurfaceProvider());

                // Build the image capture
                imageCapture = new ImageCapture.Builder().build();

                // Select the camera
                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build();

                // Unbind all use cases before rebinding
                cameraProvider.unbindAll();
                // Bind use cases to the camera
                Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector, preview, imageCapture);

            } catch (ExecutionException | InterruptedException e) {
                // Handle any errors
                Log.e("MainActivity", "Error starting camera: " + e.getMessage(), e);
            }
        }, ContextCompat.getMainExecutor(this));
    }
}