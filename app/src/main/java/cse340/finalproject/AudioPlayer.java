package cse340.finalproject;

import android.media.MediaPlayer;
import java.io.IOException;
public class AudioPlayer extends MediaPlayer {
    private MediaPlayer mediaPlayer;
    private MediaPlayer.OnCompletionListener completionListener;

    public void startPlaying(String fileName) {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(fileName);
                mediaPlayer.prepare();
                mediaPlayer.start();
                mediaPlayer.setOnCompletionListener(completionListener);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopPlaying() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public void setOnCompletionListener(MediaPlayer.OnCompletionListener listener) {
        completionListener = listener;
    }

    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }
}
