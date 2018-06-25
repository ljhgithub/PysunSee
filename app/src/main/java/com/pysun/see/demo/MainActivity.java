package com.pysun.see.demo;

import android.Manifest;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.pysun.see.entity.Album;
import com.pysun.see.entity.Media;
import com.pysun.see.model.AlbumDocker;
import com.pysun.see.model.AlbumMediaDocker;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.List;

import io.reactivex.disposables.Disposable;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AlbumDocker.AlbumCallback, AlbumMediaDocker.AlbumMediaCallback {

    AlbumDocker albumDocker;
    AlbumMediaDocker albumMediaLoader;
    EditText editText;

    List<Album> albums;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.hello).setOnClickListener(this);
        editText = findViewById(R.id.edit_query);
        albumDocker = new AlbumDocker(MainActivity.this, this);
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Album album = albums.get(Integer.valueOf(editText.getText().toString()));
                albumMediaLoader.restart(album, true);

            }
        });
        albumMediaLoader = new AlbumMediaDocker(this, this);
        albumDocker.loadAlbums();
    }

    @Override
    public void onClick(View v) {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new io.reactivex.Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {

                        if (aBoolean) {


                            albumDocker.restart();

                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    @Override
    public void onAlbumLoad(List<Album> albums) {
        this.albums = albums;
        editText.setText((albums.size() - 1) + "");
        Log.d("tag", "albums " + albums.size());
    }

    @Override
    public void onAlbumReset() {

    }

    @Override
    public void onAlbumMediaLoad(List<Media> images) {
        Log.d("tag", "images " + images.size());
    }

    @Override
    public void onAlbumMediaReset() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        albumDocker.onDestroy();
    }
}
