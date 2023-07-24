package cse340.finalproject;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * The activity for Browse Sound page, including playing each sound with all the information
 */
public class BrowseSoundActivity extends AbstractActivity {

    /**
     * Callback that is called when the activity is first created.
     * @param savedInstanceState contains the activity's previously saved state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browse_sound);
        setHomeListeners();

        LinearLayout linearLayout = findViewById(R.id.browse_all_ll);

        // get all the files from internal storage
        // and add corresponding view to linear layout
        List<File> soundFiles = getSoundFiles();
        for (File soundFile : soundFiles) {
            View audioView = getLayoutInflater().inflate(R.layout.audio_player, null);

            ImageButton player = audioView.findViewById(R.id.player_btn);
            AudioPlayer audioPlayer = new AudioPlayer();

            player.setOnClickListener(v -> {
                if (audioPlayer.isPlaying()) {
                    audioPlayer.stopPlaying();
                    // Cite: https://stackoverflow.com/questions/38212831/how-to-set-imagebutton-
                    // property-of-appsrccompat-drawable-pic-programmaticall
                    player.setImageResource(R.drawable.play_arrow);
                    player.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                    Toast.makeText(this, getString(R.string.t_play_stop),
                            Toast.LENGTH_SHORT).show();
                } else {
                    audioPlayer.startPlaying(soundFile.getPath());
                    player.setImageResource(R.drawable.pause);
                    player.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    Toast.makeText(this, getString(R.string.t_play_start),
                            Toast.LENGTH_SHORT).show();
                }
            });

            audioPlayer.setOnCompletionListener(mp -> {
                // Called when the audio playback is completed
                player.setImageResource(R.drawable.play_arrow);
                player.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            });

            // set the information text for each sound
            String fullName = soundFile.getName();
            // https://stackoverflow.com/questions/2911005/how-to-cut-string-with-two-regular-
            // expression-and
            String[] fullNameList = fullName.split("_");
            Button category = audioView.findViewById(R.id.category_txt);
            TextView dateView = audioView.findViewById(R.id.date_txt);

            category.setText(fullNameList[0]);
            // exclude ".mp3" which are the last four characters in the string
            dateView.setText(fullNameList[1].substring(0,fullNameList[1].length() - 4));
            linearLayout.addView(audioView);
        }
    }

    /**
     * Get the sound files from internal storage
     */
    // https://www.tutorialspoint.com/android/android_internal_storage.htm
    private List<File> getSoundFiles() {
        List<File> soundFiles = new ArrayList<>();
        File directory = new File(getFilesDir().getPath());
        File[] files = directory.listFiles();

        // Iterate through the files and add sound files to the list
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".mp3") &&
                        !file.getName().contains("testFile")) {
                    soundFiles.add(file);
                }
            }
        }
        return soundFiles;
    }
}
