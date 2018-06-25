package com.pysun.see.model;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.pysun.see.entity.Album;
import com.pysun.see.loader.AlbumLoader;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class AlbumDocker implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int LOADER_ID = 1;
    private WeakReference<Context> mContext;
    private AlbumCallback mAlbumCallback;
    private LoaderManager mLoaderManager;

    public AlbumDocker(AppCompatActivity appCompatActivity, AlbumCallback albumCallback) {
        this.mContext = new WeakReference<Context>(appCompatActivity);
        this.mAlbumCallback = albumCallback;
        this.mLoaderManager = appCompatActivity.getLoaderManager();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Context context = mContext.get();
        if (context == null) {
            return null;
        }
        Log.d("tag", "album onCreateLoader");
        return AlbumLoader.newInstance(context, MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE, MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        List<Album> albums = new ArrayList<>(data.getCount());
        Album album;
        Cursor cursor = data;
        while (cursor.moveToNext()) {
            String id = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_ID));
            String name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME));
            int count = cursor.getInt(cursor.getColumnIndex(AlbumLoader.COLUMN_COUNT));
            String coverPath = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA));
            long size =cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns.SIZE));
            album = new Album(id, name, size,count, coverPath);
            albums.add(album);
//            Log.d("tag", album.toString());
        }
        Context context = mContext.get();
        if (context == null) {
            return;
        }
        mAlbumCallback.onAlbumLoad(albums);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Context context = mContext.get();
        if (context == null) {
            return;
        }
        mAlbumCallback.onAlbumReset();
        Log.d("tag", "album onLoaderReset");

    }


    public void onDestroy() {
        if (mLoaderManager != null) {
            mLoaderManager.destroyLoader(LOADER_ID);
        }
        mAlbumCallback = null;
    }

    public void loadAlbums() {
        mLoaderManager.initLoader(LOADER_ID, null, this);
    }
    public void restart() {
        mLoaderManager.restartLoader(LOADER_ID, null, this);
    }
    public interface AlbumCallback {
        void onAlbumLoad(List<Album> albums);

        void onAlbumReset();
    }
}
