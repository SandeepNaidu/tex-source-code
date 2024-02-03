package com.project.tex.post.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.project.tex.R;
import com.steelkiwi.cropiwa.AspectRatio;
import com.steelkiwi.cropiwa.CropIwaView;
import com.steelkiwi.cropiwa.config.CropIwaSaveConfig;
import com.steelkiwi.cropiwa.image.CropIwaResultReceiver;
import com.steelkiwi.cropiwa.shape.CropIwaOvalShape;
import com.steelkiwi.cropiwa.shape.CropIwaRectShape;

import java.io.File;

public class CropActivity extends AppCompatActivity implements CropIwaResultReceiver.Listener {

    private static final String EXTRA_URI = "uri";

    private CropIwaView cropView;
    CropIwaResultReceiver resultReceiver = null;

    @Override
    @SuppressWarnings("ConstantConditions")
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Uri imageUri = getIntent().getParcelableExtra(EXTRA_URI);
        String imageType = getIntent().getStringExtra("type");
        cropView = findViewById(R.id.crop_view);
        cropView.setImageUri(imageUri);
        if (imageType.equals("Audio")) {
            cropView.configureOverlay().setAspectRatio(new AspectRatio(1, 1))
                    .setCropShape(new CropIwaOvalShape(cropView.configureOverlay())).apply();
        } else if (imageType.equals("event")) {
            cropView.configureOverlay().setAspectRatio(new AspectRatio(16, 9))
                    .setCropShape(new CropIwaRectShape(cropView.configureOverlay())).apply();
        }

        cropView.setCropSaveCompleteListener(new CropIwaView.CropSaveCompleteListener() {
            @Override
            public void onCroppedRegionSaved(Uri bitmapUri) {
                setResult(RESULT_OK, new Intent().setData(bitmapUri));
                finish();
            }
        });
        findViewById(R.id.btn_done).setOnClickListener(v -> {
            File file = new File(getCacheDir(), "cropped_" + System.currentTimeMillis() + ".jpg");
            cropView.crop(new CropIwaSaveConfig.Builder(Uri.fromFile(file))
                    .setCompressFormat(Bitmap.CompressFormat.PNG)
                    .setQuality(80) //Hint for lossy compression formats
                    .build());
        });

        findViewById(R.id.btn_cancel).setOnClickListener(v -> finish());

    }

    @Override
    public void onCropSuccess(Uri croppedUri) {
        setResult(RESULT_OK, new Intent().setData(croppedUri));
        finish();
    }

    @Override
    public void onCropFailed(Throwable e) {
        finish();
    }

}