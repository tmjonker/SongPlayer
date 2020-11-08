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

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

public class Album {
    private static final String myid = Album.class.getName();

    // Online site
    static String Site = "https://drive.google.com/uc?export=download&confirm=no_antivirus&id=";
    // Local storage path
    static File Path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
    // FilelistId
    static String FileListId ="1jw5HEC1aj9_GkPWFX9yBCZ7m8c45nva7";
    // FilelistName
    static File FileList = new File(Path, "filelist.json");
    public static final String Label = "song";


    // JSON format of the FileList
    private static final String JSON_Songs = "Songs";
    private static final String JSON_Title = "Title";
    private static final String JSON_Artist = "Artist";
    private static final String JSON_Format = "Format";
    private static final String JSON_fileId = "fileId";
    ArrayList<String> songs = new ArrayList<>(); // default empty list
    public static final String delimiter = "\t";

    public Album() {
    }

    public static boolean fileListExists() {
        return FileList.exists();
    }
    public static URL getFileListURL() {
        try {
            return new URL(Site + FileListId);
        } catch (Exception e) {
            Log.e(myid, e.getMessage());
            return null;
        }
    }
    public boolean setup(Context context) {
        try {
            Log.i(myid, "setup");
            BufferedReader reader = new BufferedReader(new FileReader(FileList));
            StringBuilder builder = new StringBuilder();
            String line;

            while ((line=reader.readLine())!=null)
                builder.append(line);

            String data = builder.toString();
            Log.i(myid, data);

            JSONObject jsonObject = new JSONObject(data);
            JSONArray jsonArray = jsonObject.getJSONArray(JSON_Songs);
            songs = new ArrayList<>();

            for (int i=0; i<jsonArray.length(); i++) {
                JSONObject jsonItem = jsonArray.getJSONObject(i);

                String song;

                if (jsonItem.getString(JSON_Artist).isEmpty()) {
                    song = jsonItem.getString(JSON_Title)
                            + delimiter + "unknown"
                            + delimiter + jsonItem.getString(JSON_Format)
                            + delimiter + jsonItem.getString(JSON_fileId);
                    songs.add(song);
                } else {
                    song = jsonItem.getString(JSON_Title)
                            + delimiter + jsonItem.getString(JSON_Artist)
                            + delimiter + jsonItem.getString(JSON_Format)
                            + delimiter + jsonItem.getString(JSON_fileId);
                    songs.add(song);
                }
            }

            return true;
        } catch (Exception e) {
            Log.e(myid, "error " + (e.getMessage()==null?"no message":e.getMessage()));
            return false;
        }
    }

    public ArrayList<String> getSongs() {
        return songs;
    }

    public URL getURL(int i) {
        try {
            URL url = new URL(Site + getSongFileId(i));
            return url;
        } catch (Exception e) {
            Log.e(myid, e.getMessage());
            return null;
        }
    }

    public String[] getSongFields(int i) {
        String song = songs.get(i);
        String[] fields = song.split(delimiter);
        return fields;
    }
    public String getSongTitle(int i) {
        String fields[] = getSongFields(i);
        return fields[0];
    }
    public String getSongArtist(int i) {
        String fields[] = getSongFields(i);
        return fields[1];
    }
    public String getSongFormat(int i) {
        String fields[] = getSongFields(i);
        return fields[2];
    }
    public String getSongFileId(int i) {
        String fields[] = getSongFields(i);
        return fields[3];
    }

    public File getFile(int i) {
        File songFile = new File(Path, Label+i+"."+getSongFormat(i));
        return songFile;
    }
}
