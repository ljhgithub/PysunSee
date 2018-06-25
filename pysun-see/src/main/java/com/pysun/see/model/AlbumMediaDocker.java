package com.pysun.see.model;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.pysun.see.entity.Album;
import com.pysun.see.entity.Media;
import com.pysun.see.loader.AlbumMediaLoader;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class AlbumMediaDocker implements LoaderManager.LoaderCallbacks<Cursor> {
    private final static String ARGS_ALBUM = "args_album";
    private final static String ARGS_PLACEHOLDER_CAMERA = "args_placeholder_camera";
    private static final int LOADER_ID = 2;
    private WeakReference<Context> mContext;
    private AlbumMediaCallback mAlbumMediaCallback;
    private LoaderManager mLoaderManager;

    public AlbumMediaDocker(AppCompatActivity appCompatActivity, AlbumMediaCallback albumCallback) {
        this.mContext = new WeakReference<Context>(appCompatActivity);
        this.mAlbumMediaCallback = albumCallback;
        this.mLoaderManager = appCompatActivity.getLoaderManager();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Context context = mContext.get();
        if (context == null) {
            return null;
        }
        Album album = args.getParcelable(ARGS_ALBUM);
        if (album == null) {
            return null;
        }
        return AlbumMediaLoader.newInstance(context, album, MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE, args.getBoolean(ARGS_PLACEHOLDER_CAMERA));

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        List<Media> images = new ArrayList<>(data.getCount());
        Media image;
        Cursor cursor = data;
        while (cursor.moveToNext()) {
            String id = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns._ID));
            String name = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME));
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA));
            long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns.SIZE));
            image = new Media(id, name, path, size);
            images.add(image);
            Log.d("tag", image.toString());
        }
        Context context = mContext.get();
        if (context == null) {
            return;
        }
        mAlbumMediaCallback.onAlbumMediaLoad(images);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Context context = mContext.get();
        if (context == null) {
            return;
        }
        mAlbumMediaCallback.onAlbumMediaReset();

    }


    public void onDestroy() {
        if (mLoaderManager != null) {
            mLoaderManager.destroyLoader(LOADER_ID);
        }
        mAlbumMediaCallback = null;
    }


    public void loadAlbumMedias(@Nullable Album album) {
        loadAlbumMedias(album, false);
    }

    public void loadAlbumMedias(@Nullable Album album, boolean placeholderCamera) {
        Bundle args = new Bundle();
        args.putParcelable(ARGS_ALBUM, album);
        args.putBoolean(ARGS_PLACEHOLDER_CAMERA, placeholderCamera);
        mLoaderManager.initLoader(LOADER_ID, args, this);
    }

    public void restart(@Nullable Album album, boolean placeholderCamera){
        Bundle args = new Bundle();
        args.putParcelable(ARGS_ALBUM, album);
        args.putBoolean(ARGS_PLACEHOLDER_CAMERA, placeholderCamera);
        mLoaderManager.restartLoader(LOADER_ID, args, this);
    }

    public interface AlbumMediaCallback {
        void onAlbumMediaLoad(List<Media> images);

        void onAlbumMediaReset();
    }
}
