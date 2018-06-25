package com.pysun.see.loader;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.pysun.see.entity.Album;
import com.pysun.see.entity.Media;
import com.pysun.see.entity.MimeType;

public class AlbumMediaLoader extends CursorLoader {

    private boolean mPlaceholderCamera;
    private static final String SORT_ORDER = MediaStore.Files.FileColumns.DATE_MODIFIED + " DESC ";

    private static final String[] COLUMNS = {
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.DISPLAY_NAME,
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns.SIZE,
            MediaStore.Files.FileColumns.MIME_TYPE
    };


    private AlbumMediaLoader(Context context, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder, boolean placeholderCamera) {
        super(context, uri, projection, selection, selectionArgs, sortOrder);
        this.mPlaceholderCamera = placeholderCamera;
    }


    public static AlbumMediaLoader newInstance(Context context, Album album, int type, String sortOrder, boolean placeholderCamera) {

        SelectionBuilder builder = new SelectionBuilder();

        if (TextUtils.isEmpty(sortOrder)) {
            sortOrder = SORT_ORDER;
        }
        if (MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE == type) {
            builder.table(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    MediaStore.Images.ImageColumns._ID,
                    MediaStore.Images.ImageColumns.DISPLAY_NAME,
                    MediaStore.Images.ImageColumns.DATA,
                    MediaStore.Images.ImageColumns.SIZE,
                    MediaStore.Images.ImageColumns.MIME_TYPE)

                    .and(MediaStore.Images.ImageColumns.SIZE + ">0")
                    .and("(" + MediaStore.MediaColumns.MIME_TYPE + "=?")
                    .or(MediaStore.MediaColumns.MIME_TYPE + "=?")
                    .or(MediaStore.MediaColumns.MIME_TYPE + "=?)");


            builder.where(MimeType.JPEG.toString(), MimeType.PNG.toString(), MimeType.WEBP.toString());


        } else if (MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO == type) {
            builder.table(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    MediaStore.Video.VideoColumns._ID,
                    MediaStore.Video.VideoColumns.DISPLAY_NAME,
                    MediaStore.Video.VideoColumns.DATA,
                    MediaStore.Video.VideoColumns.SIZE)
                    .and(MediaStore.Video.VideoColumns.SIZE + ">0");


        } else {
            builder.table(MediaStore.Files.getContentUri("external"),
                    MediaStore.Files.FileColumns._ID,
                    MediaStore.Images.ImageColumns.DISPLAY_NAME,
                    MediaStore.Files.FileColumns.DATA,
                    MediaStore.Files.FileColumns.SIZE,
                    MediaStore.Files.FileColumns.MIME_TYPE)
                    .and(MediaStore.Images.ImageColumns.SIZE + ">0")
                    .and("(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?")
                    .or(MediaStore.Files.FileColumns.MEDIA_TYPE + "=?");

            builder.where(String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO), String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE));


        }
        if (!album.isAll()) {//只查询本目录下的文件
            builder.and(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME + "=?");
            builder.where(album.getDisplayName());
        }
        return new AlbumMediaLoader(context, builder.getUri(), builder.getProjection(), builder.getSelection(), builder.getSelectionArgs(), sortOrder, placeholderCamera);

    }

    public static AlbumMediaLoader newInstance(Context context, Album album, int type, boolean placeholderCamera) {
        return newInstance(context, album, type, SORT_ORDER, placeholderCamera);
    }


    @Override
    protected Cursor onLoadInBackground() {
        Cursor medias = super.onLoadInBackground();
        if (!mPlaceholderCamera) {
            return medias;
        }
        MatrixCursor allMedias = new MatrixCursor(COLUMNS);

        allMedias.addRow(new Object[]{Media.ID_CAMERA, Media.DISPLAY_CAMERA, Media.PATH_CAMERA, 0,
                ""});
        return new MergeCursor(new Cursor[]{allMedias, medias});
    }

}
