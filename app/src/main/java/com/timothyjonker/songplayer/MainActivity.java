/*
 * Copyright (c) 2020. Timothy Jonker @ NVCC
 *
 * Author: Timothy Jonker
 * Affiliation: NVCC
 *
 * Terms of Use:
 * This application is part of the term projects of the course ITP226 of Fall 2020.  It is not to released to any third party, whether with or without the permission of the author.  Any unauthorized use of this application may be subject to prosecution.
 */

package com.timothyjonker.songplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {

    private static final String myid = MainActivity.class.getName();
    // When Download finishes its job it passes information back using this CallerCode
    private static int CallerCode = 110;
    private AudioFragment fragment;
    private Album album;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        verifyStoragePermission(this);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragment = (AudioFragment) fragmentManager.findFragmentById(R.id.fragmentHolder);

        if (fragment == null) {
            fragment = new AudioFragment();
            fragmentManager.beginTransaction().add(R.id.fragmentHolder, fragment).commit();
        }
        verifyStoragePermission(this);
        setupAlbum();
    }


    public void setupAlbum() {

        album = new Album();
        if (Album.fileListExists()) {
            album.setup(this);
            setupList();
        } else {
            Intent intent = new Intent(this, DownloadActivity.class);
            intent.putExtra(DownloadActivity.key_file, Album.FileList);
            intent.putExtra(DownloadActivity.key_url, Album.getFileListURL());
            startActivityForResult(intent, CallerCode);
        }
    }


    public void setupList() {

        RecyclerView recyclerView;
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        final ListAdapter listAdapter = new ListAdapter(this, album.getSongs());
        listAdapter.setItemClickedListener(new ListAdapter.ItemClickedListener() {

            @Override
            public void onItemClicked(View view, int selected) {

                Log.i(myid, "User pressed " + selected);

                File songFile = album.getFile(selected);
                if (songFile.exists()) {
                    // TODO: pass this file to the audio
                    fragment.play(songFile);
                    Toast.makeText(MainActivity.this, "Selected file " + songFile.getName(), Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(MainActivity.this, DownloadActivity.class);
                    intent.putExtra(DownloadActivity.key_file, songFile);
                    intent.putExtra(DownloadActivity.key_url, album.getURL(selected));
                    startActivityForResult(intent, CallerCode);
                }
            }
        });
        recyclerView.setAdapter(listAdapter);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CallerCode) {
            if (resultCode == RESULT_OK) {
                File file = (File) data.getExtras().get(DownloadActivity.key_file);
                Log.i(myid, "received download file " + file.getAbsolutePath());
                if (file.getName().equals(Album.FileList.getName())) {
                    album.setup(this);
                    setupList();
                } else {
                    // TODO: pass this file to the audio
                    fragment.play(file);
                    Toast.makeText(MainActivity.this, "Selected file " + file.getName(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    public void verifyStoragePermission(Activity activity) {

        // Required for API23 and above.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int code = 100;
            int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if (permission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, code);
                Log.i(myid, "Permission requested");
            }
        }
    }
}