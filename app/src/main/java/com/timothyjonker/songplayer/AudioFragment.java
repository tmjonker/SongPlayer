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

import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;


public class AudioFragment extends Fragment {
    private static final String myid = AudioFragment.class.getName();
    private MediaPlayer mediaPlayer;
    private ImageButton imageButton;
    private TextView audioText;
    private File songFile = null;
    private boolean repeat = false;

    public AudioFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                updateControlStatus();
            }
        });
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_audio, container, false);

        imageButton = (ImageButton) view.findViewById(R.id.imageButton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                }
                else {
                    mediaPlayer.start();
                }
                updateControlStatus();
            }
        });
        audioText = (TextView) view.findViewById(R.id.audioText);

        updateControlStatus();
        return view;
    }

    private void updateControlStatus() {
        if (mediaPlayer.isPlaying()) {
            imageButton.setImageResource(R.drawable.ic_action_pause);
            audioText.setText("Now Playing\n" + getMetaData(songFile));
        }
        else {
            imageButton.setImageResource(R.drawable.ic_action_play);
            if (songFile==null)
                audioText.setText("No song selected.");
            else
                audioText.setText(getMetaData(songFile));
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        updateControlStatus();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
    }

    private String getMetaData(File songFile) {
        Uri uri = Uri.parse(songFile.getAbsolutePath());
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(getActivity(), uri);
        String album = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
        String title = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        String date = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE);
        String metadata = "";

        if (album!=null)
            metadata += "Album: " + album + "\n";
        if (title!=null)
            metadata += "Title: " + title + "\n";
        if (date!=null)
            metadata += "Date: " + date + "\n";

        return metadata;
    }


    public void play(File songFile) {
        this.songFile = songFile;


        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(songFile.getAbsolutePath());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
            Log.e(myid, "Error with mediaPlayer");
            e.printStackTrace();
        }
        updateControlStatus();
    }
}
