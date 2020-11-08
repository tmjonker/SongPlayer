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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.net.URL;

public class DownloadActivity extends AppCompatActivity implements Downloader.Callback {
    private static final String myid = DownloadActivity.class.getName();
    public static final String key_file = "file";
    public static final String key_url = "url";
    public static final String key_status = "status";
    public static final String key_message = "message";

    File file;
    URL url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        file = (File) getIntent().getExtras().get(key_file);
        url = (URL) getIntent().getExtras().get(key_url);
        setContentView(R.layout.activity_download);
        new Downloader(this, file, url).execute();
    }


    @Override
    public void onDownloadCompleted(File file, boolean success, String message) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(key_file, file);
        resultIntent.putExtra(key_status, success);
        resultIntent.putExtra(key_message, message);
        if (success)
            setResult(Activity.RESULT_OK, resultIntent);
        else
            setResult(Activity.RESULT_CANCELED, resultIntent);
        finish();
    }
}
