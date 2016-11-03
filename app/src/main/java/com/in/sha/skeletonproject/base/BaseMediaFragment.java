package com.in.sha.skeletonproject.base;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.util.ArrayMap;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.in.sha.skeletonproject.R;
import com.in.sha.skeletonproject.utils.FileUtils;
import com.in.sha.skeletonproject.utils.GeneralUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.nereo.multi_image_selector.MultiImageSelector;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * Created by sreepolavarapu on 16/03/16.
 * <p/>
 * Base class to handle Media. For now its only images
 */
public abstract class BaseMediaFragment extends BaseFragment {

    public static final int REQUEST_IMAGE = 11;
    public static final String PATH = "mediaFilePath";
    protected static final int TAKE_PHOTO = 2;
    private static final int IMAGE_DIMEN_1024 = 1024;
    private static final int IMAGE_DIMEN_768 = 768;
    private static final String TAG = BaseMediaFragment.class.getSimpleName();
    //  Make it false if the image do not require scaling
    protected boolean shouldScaleImage = true;
    PermissionListener readExternalStoragePemissionListener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            openImagePickerIntent();
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
//            Toast.makeText(getActivity(), "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
        }
    };
    //	mediaFilePath is used when taking a picture
    private String mediaFilePath;
    PermissionListener takePicturePemissionListener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            dispatchTakePictureIntent();
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
//            Toast.makeText(getActivity(), "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
        }
    };
    //For multi image picker, save the path the user originally selected as well as their scaled path.
    private ArrayMap<String, String> mediaPathScaledAndOriginMap = new ArrayMap<>();

    //Multi image picker
    PermissionListener multiImagePermissionListener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            ArrayList<String> paths = new ArrayList<>();

            //mark the path which has been selected before in the multi image picker if possible.
            //null will be returned if there is no path map in the model anymore. But null will be a place holder anyway.
            for (String path : getMediaFilePathList()) {
                if (mediaPathScaledAndOriginMap.containsKey(path)) {
                    paths.add(mediaPathScaledAndOriginMap.get(path));
                    Log.d("SiteView", "add original path" + path);
                } else {
                    Log.d("SiteView", "add path" + path);
                    paths.add(path);
                }
            }

            //issue the picker
            MultiImageSelector.create()
                    .count(10)// max select image size, 9 by default. used width #.multi()
                    .origin(paths)
                    .start(BaseMediaFragment.this, REQUEST_IMAGE);
        }

        @Override
        public void onPermissionDenied(ArrayList<String> arrayList) {

        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EasyImage.configuration(getContext())
                .setImagesFolderName(getString(R.string.app_name));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (mediaFilePath != null) {
            outState.putString(PATH, mediaFilePath);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case TAKE_PHOTO: {
                    handleTakePhoto();

                    break;
                }
                case EasyImage.REQ_PICK_PICTURE_FROM_GALLERY: {
                    handleAttachPhoto(requestCode, resultCode, data);

                    break;
                }
                case REQUEST_IMAGE: {
                    handleMultiImagePicker(data);
                    break;
                }
            }
        }
    }

    /**
     *
     * @param data
     */
    private void handleMultiImagePicker(Intent data){
        List<String> paths = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
        List<String> scaledPaths = getMediaFilePathList();
        //to-remove list, which could be de-select by user,which has been selected before.
        List<String> removedPaths = new ArrayList<>();
        removedPaths.addAll(scaledPaths);

        for (String path : paths) {
            boolean has = false;
            for (String scaledPath : scaledPaths) {
                String originPath = mediaPathScaledAndOriginMap.get(scaledPath);
                if (path!= null && originPath != null && path.equalsIgnoreCase(originPath)) {
                    has = true;
                    //if found , it is still selected,remove it from the to-remove list
                    removedPaths.remove(scaledPath);
                    break;
                }
            }
            if (!has) {
                //first time select
                //TODO, should store the path list here for all the selected path.
                mediaFilePath = path;
                handleTakePhoto();
            }
        }

        for(String removePath: removedPaths){
            postImageUnSelected(removePath);
        }
    }

    protected abstract void postImageSelected(String mediaFilePath);
    protected void postImageUnSelected(String mediaFilePath){}

    /**
     * Check for permissions and open image chooser once they are granted.
     */
    public void reqPermissionsAndopenImagePickerIntent() {

        String permissions[] = new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE};

        new TedPermission(getContext())
                .setPermissionListener(readExternalStoragePemissionListener)
                .setDeniedMessage(getString(R.string.permission_denied_message))
                .setPermissions(permissions)
                .check();
    }

    public void openImagePickerIntent() {
        EasyImage.openGallery(this, 0);
    }

    //  --- PERMISSION LISTENERS

    /**
     * Request permissions required for
     */
    public void requestPermissionsAndDispatchPictureIntent() {
        boolean isWritePermissionRequired = false;
        File dir = getContext().getFilesDir();
        if (!FileUtils.isFreeSpaceAvailable(dir)) {
            //  Free space not available in internal directory,
            //  check for write to external storage permission as well.
            isWritePermissionRequired = true;
        }

        String permissions[] = new String[]{
                Manifest.permission.CAMERA};

        if (isWritePermissionRequired) {
            permissions = new String[]{
                    Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        }

        new TedPermission(getContext())
                .setPermissionListener(takePicturePemissionListener)
                .setDeniedMessage(getString(R.string.permission_denied_message))
                .setPermissions(permissions)
                .check();
    }

    public void requestPermissionsAndDispatchMultiImagesIntent() {
        String permissions[] = new String[]{
                Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

        new TedPermission(getContext())
                .setPermissionListener(multiImagePermissionListener)
                .setDeniedMessage(getString(R.string.permission_denied_message))
                .setPermissions(permissions)
                .check();
    }

    /**
     * Dispatches the picture intent while creating image with specified name.
     */
    public void dispatchTakePictureIntent() {
        mediaFilePath = null;

        String uuid = String.valueOf(GeneralUtils.generateUUIDWithoutDashes());
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            try {

                File photoFile = FileUtils.createImageFile(getContext(), uuid);
                photoFile.createNewFile();

                mediaFilePath = photoFile.getAbsolutePath();

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));

                this.startActivityForResult(takePictureIntent, TAKE_PHOTO);
            } catch (IOException ex) {
//                Toast.makeText(getContext(), )
            }

        }

    }

    //  --- END PERMISSION LISTENERS

    //  --  HANDLE PROCESSED MEDIA

    public void handleAttachPhoto(int requestCode, int resultCode, Intent data) {

        EasyImage.handleActivityResult(requestCode, resultCode, data, getActivity(), new DefaultCallback() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                //Some error handling
                Log.e(TAG, "Error in Image picker");
            }

            @Override
            public void onImagePicked(File imageFile, EasyImage.ImageSource source, int type) {
                //Handle the image
                mediaFilePath = imageFile.getAbsolutePath();
                postImageSelected(mediaFilePath);
                //  If you have to scale image, uncomment below method.
//                scaleImage(mediaFilePath);
            }

            @Override
            public void onCanceled(EasyImage.ImageSource source, int type) {
                //Cancel handling, you might wanna remove taken photo if it was canceled
                if (source == EasyImage.ImageSource.CAMERA) {
                    File photoFile = EasyImage.lastlyTakenButCanceledPhoto(getActivity());
                    if (photoFile != null) photoFile.delete();
                }
            }
        });
    }

    public void handleTakePhoto() {
        if (mediaFilePath != null) {
//			FileUtils.addToGallery(activity, mediaFilePath);

            if (shouldScaleImage) {
                scaleImage(mediaFilePath);
            } else {
                postImageSelected(mediaFilePath);
            }
        }
    }

    /**
     * Scales the image to the dimensions {@link BaseMediaFragment#IMAGE_DIMEN_1024} &
     * {@link BaseMediaFragment#IMAGE_DIMEN_768}
     *
     * @param mediaFilePath The media path of the image
     */
    private void scaleImage(final String mediaFilePath) {
        //  Calculates the dimensions to scale
        ImageDimensions dimensions = getDimensions(mediaFilePath);

        //  Scale the image using glide.
        Glide.with(getActivity())
                .load(new File(mediaFilePath))
                .asBitmap()
                .override(dimensions.width, dimensions.height)
                .fitCenter()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {

                        saveScaledImageOnObserver(mediaFilePath, bitmap);
                    }
                });

    }

    /**
     * Helper to calculate the max width and height.
     *
     * @param mediaFilePath MEdia file path on the local storage
     * @return The constructed image dimensions.
     */
    private ImageDimensions getDimensions(String mediaFilePath) {
        boolean isHeightGrater = false;
        try {
            //  GEt the image width and height of bitmap in local storage without actually loading.
            FileInputStream inputStream = new FileInputStream(mediaFilePath);
            BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
            bitmapOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(inputStream, null, bitmapOptions);
            int imageWidth = bitmapOptions.outWidth;
            int imageHeight = bitmapOptions.outHeight;

            isHeightGrater = imageWidth < imageHeight;
            inputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //  If height is grater, use height dimension.
        if (isHeightGrater) {
            return new ImageDimensions(IMAGE_DIMEN_768, IMAGE_DIMEN_1024);
        }

        return new ImageDimensions(IMAGE_DIMEN_1024, IMAGE_DIMEN_768);
    }

    /**
     * Saves the scaled bitmap to the file system and returns the path
     *
     * @param mediaFilePath Actual image path
     * @param bitmap        Scaled bitmap
     * @return Scaled bitmap file path, in case of failure returns actual image path.
     */
    private String saveScaledImage(String mediaFilePath, Bitmap bitmap) {
        // Do something with bitmap here.
        FileOutputStream out = null;
        try {
            //                            bitmap.gets
            File photoFile = FileUtils.createImageFile(getContext(), GeneralUtils.generateUUIDWithoutDashes());


            out = new FileOutputStream(photoFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, out);

            return photoFile.getAbsolutePath();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return mediaFilePath;
    }

    /**
     * Saves the scaled bitmap on  observer
     *
     * @param mediaFilePath
     * @param bitmap
     */
    private void saveScaledImageOnObserver(final String mediaFilePath, Bitmap bitmap) {
        showProgress(true);

        Subscription getEntitesModelSubscription =
                Observable.just(saveScaledImage(mediaFilePath, bitmap))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<String>() {
                            @Override
                            public void onCompleted() {
                                hideProgress();
                            }

                            @Override
                            public void onError(Throwable e) {
                                hideProgress();
                            }

                            @Override
                            public void onNext(String imageFilePath) {
                                postImageSelected(imageFilePath);
                                mediaPathScaledAndOriginMap.put(imageFilePath, mediaFilePath);
                                hideProgress();
                            }
                        });
        mCompositeSubscription.add(getEntitesModelSubscription);
    }

    @Override
    public void onDestroy() {
        // Clear any configuration that was done!
        EasyImage.clearConfiguration(getActivity());
        super.onDestroy();
    }

    //  ---- END HANDLE PROCESSED MEDIA

    /**
     * get the file path list from the network data. Actually, they are from the path map of entity model
     * @return
     */
    protected List<String> getMediaFilePathList() {
        return new ArrayList<>();
    }

    /**
     * Class to maintain image dimensions
     */
    private class ImageDimensions {
        int width;
        int height;

        private ImageDimensions(int width, int height) {
            this.width = width;
            this.height = height;
        }
    }

}
