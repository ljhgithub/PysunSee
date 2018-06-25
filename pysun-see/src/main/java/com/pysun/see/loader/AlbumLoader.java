package com.pysun.see.loader;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;

import com.pysun.see.entity.Album;
import com.pysun.see.entity.MimeType;

import org.json.JSONArray;
import org.json.JSONException;


public class AlbumLoader extends CursorLoader {
    public static final String COLUMN_COUNT = "count";
    private static final String SORT_ORDER = MediaStore.Files.FileColumns.DATE_MODIFIED + " DESC ";

    private static final String[] COLUMNS = {
            MediaStore.Images.ImageColumns.BUCKET_ID,
            MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns.SIZE,
            COLUMN_COUNT};


    private AlbumLoader(Context context, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        super(context, uri, projection, selection, selectionArgs, sortOrder);
    }


    public static AlbumLoader newInstance(Context context, int type, String sortOrder) {
        SelectionBuilder builder = new SelectionBuilder();


        String group = MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME;//请同步修改AlbumMediaLoader中相关的值

        if (TextUtils.isEmpty(sortOrder)) {
            sortOrder = SORT_ORDER;
        }
        if (MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE == type) {
            builder.table(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    MediaStore.Images.ImageColumns.BUCKET_ID,
                    MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                    MediaStore.Images.ImageColumns.DATA,
                    MediaStore.Images.ImageColumns.SIZE,
                    "COUNT(*) AS " + COLUMN_COUNT)

                    .and(MediaStore.Images.ImageColumns.SIZE + ">0")
                    .and("(" + MediaStore.MediaColumns.MIME_TYPE + "=?")
                    .or(MediaStore.MediaColumns.MIME_TYPE + "=?")
                    .or(MediaStore.MediaColumns.MIME_TYPE + "=?)");

            builder.where(MimeType.JPEG.toString(), MimeType.PNG.toString(), MimeType.WEBP.toString());
            builder.groupBy(group);

        } else if (MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO == type) {
            builder.table(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    MediaStore.Images.ImageColumns.BUCKET_ID,
                    MediaStore.Video.VideoColumns.BUCKET_DISPLAY_NAME,
                    MediaStore.Video.VideoColumns.DATA,
                    MediaStore.Video.VideoColumns.SIZE,
                    "COUNT(*) AS " + COLUMN_COUNT)

                    .and(MediaStore.Video.VideoColumns.SIZE + ">0");
            builder.groupBy(group);

        } else {
            builder.table(MediaStore.Files.getContentUri("external"),
                    MediaStore.Images.ImageColumns.BUCKET_ID,
                    MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                    MediaStore.Files.FileColumns.DATA,
                    MediaStore.Files.FileColumns.SIZE,
                    "COUNT(*) AS " + COLUMN_COUNT)

                    .and(MediaStore.Images.ImageColumns.SIZE + ">0")
                    .and("(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?")
                    .or(MediaStore.Files.FileColumns.MEDIA_TYPE + "=?");

            builder.where(String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO), String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE));
            builder.groupBy(group);

        }
        return new AlbumLoader(context, builder.getUri(), builder.getProjection(), builder.getSelection(), builder.getSelectionArgs(), sortOrder);

    }


    public static AlbumLoader newInstance(Context context, int type) {
        return newInstance(context, type, SORT_ORDER);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected Cursor onLoadInBackground() {
        Cursor albums = super.onLoadInBackground();
        MatrixCursor allAlbum = new MatrixCursor(COLUMNS);

        int totalCount = 0;
        long totalSize = 0;
        String allAlbumCoverPath = "";
        if (albums != null) {
            while (albums.moveToNext()) {
                totalCount += albums.getInt(albums.getColumnIndex(COLUMN_COUNT));
                totalSize += albums.getLong(albums.getColumnIndex(MediaStore.Files.FileColumns.SIZE));

            }
            if (albums.moveToFirst()) {
                allAlbumCoverPath = albums.getString(albums.getColumnIndex(MediaStore.MediaColumns.DATA));
            }
        }
        allAlbum.addRow(new String[]{
                Album.ALL_ID,
                Album.ALL_NAME,
                allAlbumCoverPath,
                String.valueOf(totalSize),
                String.valueOf(totalCount)});
        return new MergeCursor(new Cursor[]{allAlbum, albums});
    }


}
