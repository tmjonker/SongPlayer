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

import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;

public class Downloader extends AsyncTask<Void, Void, Void> {
    private static final String myid = Downloader.class.getName();

    File file;
    URL url;
    String message;
    boolean success;
    WeakReference<Callback> ref;

    public Downloader(Callback callback, File file, URL url) {
        this.file = file;
        this.url = url;
        ref = new WeakReference<>(callback);
        message = "";
        success = false;
    }
    @Override
    protected Void doInBackground(Void... params) {
        try {
            ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());
            if (!file.exists()) file.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(file.getAbsolutePath());

            long blocksize;
            if (Build.VERSION.SDK_INT>23)
                blocksize = Long.MAX_VALUE;
            else
                blocksize = 8*1024;
            long position = 0;
            FileChannel channel = fileOutputStream.getChannel();
            long loaded;
            while ((loaded=channel.transferFrom(readableByteChannel, position, blocksize))>0)
                position+=loaded;
            fileOutputStream.close();

            success = true;
            message = file.getAbsolutePath() + " saved.";
            Log.i(myid, "Finishing ");
        } catch (Exception e) {
            message = e.getMessage();
            e.printStackTrace();
        }
        return null;
    }
    @Override
    protected void onPostExecute(Void dummy) {
        Callback callback = ref.get();
        if (callback!=null) {
            callback.onDownloadCompleted(file, success, message);
        }
        Log.i(myid, "finished with message " + message + " success="+success);
    }


    interface Callback {
        void onDownloadCompleted(File file, boolean success, String message);
    }
}
