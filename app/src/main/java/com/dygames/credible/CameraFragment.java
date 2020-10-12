package com.dygames.credible;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.DngCreator;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Semaphore;

public class CameraFragment extends Fragment {
    Preview preview;

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray(4);

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    private static String generateTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SSS", Locale.US);
        return sdf.format(new Date());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_camera, container, false);

        preview = new Preview(getContext(), (TextureView) rootView.findViewById(R.id.cameraTextureView));

        rootView.findViewById(R.id.camera_capture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (null == preview.mCameraDevice) {
                    Log.e("DD", "mCameraDevice is null, return");
                    return;
                }

                try {
                    Size[] jpegSizes = null;
                    CameraManager cameraManager = (CameraManager) getContext().getSystemService(Context.CAMERA_SERVICE);
                    CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics("0");
                    StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

                    if (map != null) {
                        jpegSizes = map.getOutputSizes(ImageFormat.JPEG);
                    }
                    int width = 640;
                    int height = 480;
                    if (jpegSizes != null && 0 < jpegSizes.length) {
                        width = jpegSizes[0].getWidth();
                        height = jpegSizes[0].getHeight();
                    }

                    ImageReader reader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1);
                    List<Surface> outputSurfaces = new ArrayList<Surface>(2);
                    outputSurfaces.add(reader.getSurface());
                    outputSurfaces.add(new Surface(preview.mTextureView.getSurfaceTexture()));

                    final CaptureRequest.Builder captureBuilder = preview.mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
                    captureBuilder.addTarget(reader.getSurface());
//            captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);

                    captureBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_START);

                    // Orientation
                    int rotation = ((Activity) getContext()).getWindowManager().getDefaultDisplay().getRotation();
                    captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));

                    Date date = new Date();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss");

                    final File file = new File(Environment.getExternalStorageDirectory() + "/Credible/Image", "pic_" + dateFormat.format(date) + ".jpg");

                    ImageReader.OnImageAvailableListener readerListener = new ImageReader.OnImageAvailableListener() {
                        @Override
                        public void onImageAvailable(ImageReader reader) {
                            Image image = null;
                            try {
                                image = reader.acquireLatestImage();
                                ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                                byte[] bytes = new byte[buffer.capacity()];
                                buffer.get(bytes);
                                save(bytes);
                                Log.d("DD", "[junsu] save()");
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } finally {
                                if (image != null) {
                                    image.close();
                                    reader.close();
                                }
                            }
                        }

                        private void save(byte[] bytes) throws IOException {
                            OutputStream output = null;
                            try {
                                output = new FileOutputStream(file);
                                output.write(bytes);
                            } finally {
                                if (null != output) {
                                    output.close();
                                }
                            }
                        }
                    };

                    HandlerThread thread = new HandlerThread("CameraPicture");
                    thread.start();
                    final Handler backgroudHandler = new Handler(thread.getLooper());
                    reader.setOnImageAvailableListener(readerListener, backgroudHandler);

                    final Handler delayPreview = new Handler();

                    final CameraCaptureSession.CaptureCallback captureListener = new CameraCaptureSession.CaptureCallback() {
                        @Override
                        public void onCaptureCompleted(CameraCaptureSession session,
                                                       CaptureRequest request, TotalCaptureResult result) {
                            super.onCaptureCompleted(session, request, result);
                            Toast.makeText(getContext(), "Saved:" + file, Toast.LENGTH_SHORT).show();
                            delayPreview.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    preview.startPreview();
                                }
                            }, 1000);
                        }

                    };

                    preview.mCameraDevice.createCaptureSession(outputSurfaces, new CameraCaptureSession.StateCallback() {
                        @Override
                        public void onConfigured(CameraCaptureSession session) {
                            try {
                                session.capture(captureBuilder.build(), captureListener, backgroudHandler);
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onConfigureFailed(CameraCaptureSession session) {

                        }
                    }, backgroudHandler);

                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }

        });

        return rootView;
    }

    public class Preview extends Thread {
        private final static String TAG = "Preview : ";

        private Size mPreviewSize;
        private Context mContext;
        private CameraDevice mCameraDevice;
        private CaptureRequest.Builder mPreviewBuilder;
        private CameraCaptureSession mPreviewSession;
        private TextureView mTextureView;

        public Preview(Context context, TextureView textureView) {
            mContext = context;
            mTextureView = textureView;
        }

        private String getBackFacingCameraId(CameraManager cManager) {
            try {
                for (final String cameraId : cManager.getCameraIdList()) {
                    CameraCharacteristics characteristics = cManager.getCameraCharacteristics(cameraId);
                    int cOrientation = characteristics.get(CameraCharacteristics.LENS_FACING);
                    if (cOrientation == CameraCharacteristics.LENS_FACING_BACK) return cameraId;
                }
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
            return null;
        }

        public void openCamera() {
            CameraManager manager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
            Log.e(TAG, "openCamera E");
            try {
                String cameraId = getBackFacingCameraId(manager);
                CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
                StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                mPreviewSize = map.getOutputSizes(SurfaceTexture.class)[0];

                int permissionCamera = ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA);
                if (permissionCamera == PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.CAMERA}, 7);
                } else {
                    manager.openCamera(cameraId, mStateCallback, null);
                }
            } catch (CameraAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Log.e(TAG, "openCamera X");
        }

        private TextureView.SurfaceTextureListener mSurfaceTextureListener = new TextureView.SurfaceTextureListener() {

            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface,
                                                  int width, int height) {
                // TODO Auto-generated method stub
                Log.e(TAG, "onSurfaceTextureAvailable, width=" + width + ",height=" + height);
                openCamera();
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface,
                                                    int width, int height) {
                // TODO Auto-generated method stub
                Log.e(TAG, "onSurfaceTextureSizeChanged");
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {
                // TODO Auto-generated method stub
            }
        };

        private CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {

            @Override
            public void onOpened(CameraDevice camera) {
                // TODO Auto-generated method stub
                Log.e(TAG, "onOpened");
                mCameraDevice = camera;
                startPreview();
            }

            @Override
            public void onDisconnected(CameraDevice camera) {
                // TODO Auto-generated method stub
                Log.e(TAG, "onDisconnected");
            }

            @Override
            public void onError(CameraDevice camera, int error) {
                // TODO Auto-generated method stub
                Log.e(TAG, "onError");
            }

        };

        protected void startPreview() {
            // TODO Auto-generated method stub
            if (null == mCameraDevice || !mTextureView.isAvailable() || null == mPreviewSize) {
                Log.e(TAG, "startPreview fail, return");
            }

            SurfaceTexture texture = mTextureView.getSurfaceTexture();
            if (null == texture) {
                Log.e(TAG, "texture is null, return");
                return;
            }

            texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
            Surface surface = new Surface(texture);

            try {
                mPreviewBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            } catch (CameraAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            mPreviewBuilder.addTarget(surface);

            try {
                mCameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {

                    @Override
                    public void onConfigured(CameraCaptureSession session) {
                        // TODO Auto-generated method stub
                        mPreviewSession = session;
                        updatePreview();
                    }

                    @Override
                    public void onConfigureFailed(CameraCaptureSession session) {
                        // TODO Auto-generated method stub
                        Toast.makeText(mContext, "onConfigureFailed", Toast.LENGTH_LONG).show();
                    }
                }, null);
            } catch (CameraAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        protected void updatePreview() {
            // TODO Auto-generated method stub
            if (null == mCameraDevice) {
                Log.e(TAG, "updatePreview error, return");
            }

            mPreviewBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
            HandlerThread thread = new HandlerThread("CameraPreview");
            thread.start();
            Handler backgroundHandler = new Handler(thread.getLooper());

            try {
                mPreviewSession.setRepeatingRequest(mPreviewBuilder.build(), null, backgroundHandler);
            } catch (CameraAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        public void setSurfaceTextureListener() {
            mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
        }

        public void onResume() {
            Log.d(TAG, "onResume");
            setSurfaceTextureListener();
        }

        private Semaphore mCameraOpenCloseLock = new Semaphore(1);

        public void onPause() {
            // TODO Auto-generated method stub
            Log.d(TAG, "onPause");
            try {
                mCameraOpenCloseLock.acquire();
                if (null != mCameraDevice) {
                    mCameraDevice.close();
                    mCameraDevice = null;
                    Log.d(TAG, "CameraDevice Close");
                }
            } catch (InterruptedException e) {
                throw new RuntimeException("Interrupted while trying to lock camera closing.");
            } finally {
                mCameraOpenCloseLock.release();
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        preview.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        preview.onPause();
    }
}
