package com.rian.apprt;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;
import com.rian.apprt.Helper.CameraHelper;
import com.rian.apprt.User.MainActivity;

import java.util.List;

public class FaceDetectionActivity extends AppCompatActivity {

    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private TextureView textureView;
    private CameraHelper cameraHelper;
    private Button buttonCapture;
    private Button buttonSwitchCamera;
    private boolean isUsingFrontCamera = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.facedetection);

        textureView = findViewById(R.id.textureView);
        buttonCapture = findViewById(R.id.buttonCapture);
        buttonSwitchCamera = findViewById(R.id.buttonSwitchCamera);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION);
        } else {
            startCamera();
        }

        buttonCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = textureView.getBitmap();
                if (bitmap != null) {
                    detectFaces(bitmap);
                }
            }
        });

        buttonSwitchCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchCamera();
            }
        });
    }

    private void startCamera() {
        cameraHelper = new CameraHelper(textureView, isUsingFrontCamera);
        cameraHelper.startCamera();
    }

    private void switchCamera() {
        isUsingFrontCamera = !isUsingFrontCamera;
        cameraHelper.stopCamera();
        startCamera();
    }

    private void detectFaces(Bitmap bitmap) {
        InputImage image = InputImage.fromBitmap(bitmap, 0);

        FaceDetectorOptions options = new FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                .build();

        FaceDetector detector = FaceDetection.getClient(options);

        detector.process(image)
                .addOnSuccessListener(new OnSuccessListener<List<Face>>() {
                    @Override
                    public void onSuccess(List<Face> faces) {
                        for (Face face : faces) {
                            Rect bounds = face.getBoundingBox();
                            Log.d("Face Detection", "Face detected with bounding box: " + bounds.toString());
                        }
                        Toast.makeText(FaceDetectionActivity.this, "Face detection completed", Toast.LENGTH_SHORT).show();

                        // Start MainActivity after successful face detection
                        Intent intent = new Intent(FaceDetectionActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish(); // Optional: finish FaceDetectionActivity if not needed anymore
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Face Detection", "Failed to detect faces", e);
                        Toast.makeText(FaceDetectionActivity.this, "Failed to detect faces", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}