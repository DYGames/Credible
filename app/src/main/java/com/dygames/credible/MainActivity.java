package com.dygames.credible;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    CameraFragment cameraFragment = new CameraFragment();
    BrowserFragment browserFragment = new BrowserFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO},
                7);

        String[] paths = new String[]{"Credible", "Credible" + File.separator + "Photo", "Credible" + File.separator + "Video", "Credible" + File.separator + "Voice"};
        File dir;
        for (int i = 0; i < paths.length; i++){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                dir = new File(getApplicationContext().getExternalFilesDir(null), paths[i]);
            else
                dir = new File(Environment.getExternalStorageState(), paths[i]);
            dir.mkdir();
        }

        findViewById(R.id.mic_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);

                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, 7);
                } else {
                    Toast.makeText(MainActivity.this, "녹음기 앱이 설치되어 있지 않습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        findViewById(R.id.record_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraFragment.isPhoto = false;
                getSupportFragmentManager().beginTransaction().add(R.id.frameLayout, cameraFragment).addToBackStack(null).commitAllowingStateLoss();
            }
        });
        findViewById(R.id.camera_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraFragment.isPhoto = true;
                getSupportFragmentManager().beginTransaction().add(R.id.frameLayout, cameraFragment).addToBackStack(null).commitAllowingStateLoss();
            }
        });
        findViewById(R.id.browser_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction().add(R.id.frameLayout, browserFragment).addToBackStack(null).commitAllowingStateLoss();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 7) {
            String theFilePath = data.getData().toString();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 7: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                    Toast.makeText(MainActivity.this, "권한이 부족하여 앱이 정상적으로 실행되지 않습니다.", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }
}