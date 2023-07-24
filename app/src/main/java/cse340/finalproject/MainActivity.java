package cse340.finalproject;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * The activity for the home page, including add new sound, and go the browse all the sound
 */
// cite: https://gitlab.cs.washington.edu/cse340-23sp-students/as3-accessibility-jzhang59/-
// /blob/main/app/src/main/java/cse340/askforhelp/SettingsActivity.java
public class MainActivity extends AbstractActivity implements AdapterView.OnItemSelectedListener {
    public static final String TAG = "AddSoundActivity";

    /** track permission for location */

    // Cite: https://dolby.io/blog/recording-audio-on-android-with-examples/

    /** Keep track of media recorder */
    private MediaRecorder mediaRecorder;

    /** Keep track of media player */
    private MediaPlayer mediaPlayer;


    /** buttons for add sound related activity */
    private Button recordMediaRecorder;
    private Button playMediaPlayer;
    private Button saveRecordingButton;

    // cite: https://developer.android.com/develop/ui/views/components/spinner
    private Spinner categorySpinner;


    /** keep track of the recording and playing status */
    private boolean isRecordingMedia = false;
    private boolean isPlayingMedia = false;
    private boolean isRecordingSaved = false;

    /** keep track of the sound file name */
    private String fileNameMedia = null;

    /** keep track of the category chosen */
    private String category = "";

    /** A list of the categories provided */
    private String[] categoriesArray = {"Human", "Rain", "Bird", "Traffic", "Water Flow", "Airport",
            "Traffic", "Other"};


    /**
     * Callback that is called when the activity is first created.
     * @param savedInstanceState contains the activity's previously saved state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // find views
        playMediaPlayer = findViewById(R.id.btnPlayRecording);
        recordMediaRecorder = findViewById(R.id.btnRecording);
        categorySpinner = findViewById(R.id.category_spinner);
        saveRecordingButton = findViewById(R.id.btnSaveRecording);

        // cite :
        // https://developer.android.com/reference/android/widget/ArrayAdapter
        // https://www.geeksforgeeks.org/arrayadapter-in-android-with-example/

        // set category content
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                categoriesArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);
        categorySpinner.setOnItemSelectedListener(this);

        // create a temparary file that keep track of the sound content
        fileNameMedia = getFilesDir().getPath() + "/testFile" + ".mp3";
        File fileMedia = new File(fileNameMedia);
        if (!fileMedia.exists()) {
            try {
                fileMedia.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // check the permission for record audio
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO}, 200);
        }


        // set onclick listener for info button, click to switch to info Activity
        findViewById(R.id.info_button).setOnClickListener(
                view -> {
                    switchActivity(this, InfoActivity.class);
                    Toast.makeText(this, getString(R.string.t_switch_page,
                                    "Home", "Info"),
                            Toast.LENGTH_LONG).show();
                });


        // set onclick listener for browse all button, click to switch to BrowseSoundActivity
        findViewById(R.id.browse_all_button).setOnClickListener(
            view -> {
                switchActivity(this, BrowseSoundActivity.class);
                Toast.makeText(this, getString(R.string.t_switch_page,
                                "Home", "Sound Gallery"),
                        Toast.LENGTH_LONG).show();
            });


        // set listeners for recording and playing activity
        setListeners();

    }

    /**
     * Request permission from users
     * @param requestCode The request code passed in {@link #requestPermissions(String[], int)}.
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *     which is either {@link android.content.pm.PackageManager#PERMISSION_GRANTED}
     *     or {@link android.content.pm.PackageManager#PERMISSION_DENIED}. Never null.
     *
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 200) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, getString(R.string.audio_permission_granted),
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, getString(R.string.audio_permission_denied),
                        Toast.LENGTH_LONG).show();
                playMediaPlayer.setEnabled(false);
                recordMediaRecorder.setEnabled(false);
            }
        }
    }


    /**
     * set listeners for recording, playing, and save activity
     */
    private void setListeners() {
        recordMediaRecorder.setOnClickListener(v -> {
            if (!isRecordingMedia) {
                startRecording();
            } else {
                stopRecording();
            }
            isRecordingMedia = !isRecordingMedia;
            setButtonText();
        });


        playMediaPlayer.setOnClickListener(v -> {
            if (!isPlayingMedia) {
                startPlaying();
            } else {
                stopPlaying();
            }
            isPlayingMedia = !isPlayingMedia;
            setButtonText();
        });


        saveRecordingButton.setOnClickListener(v -> {
            if (isRecordingSaved) {
                Toast.makeText(this, getString(R.string.t_save),
                        Toast.LENGTH_SHORT).show();
            } else {
                saveRecording();
            }
        });

    }

    /**
     * Start Recording activity
     */
    private void startRecording() {
        if (mediaRecorder == null) {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFile(fileNameMedia);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            // https://developer.android.com/reference/android/media/MediaRecorder.AudioEncoder
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

            try {
                mediaRecorder.prepare();
            } catch (IOException e) {
                Log.e(TAG, "could not prepare MediaRecorder " + e);
                return;
            } catch (IllegalStateException e) {
                Log.e(TAG, "could not prepare MediaRecorder " + e);
                return;
            }

            mediaRecorder.start();
            Toast.makeText(this, getString(R.string.t_record_start),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Stop Recording activity
     */
    private void stopRecording() {
        if (mediaRecorder != null) {
            // stop recording and free up resources
            mediaRecorder.stop();
            mediaRecorder.reset();
            mediaRecorder.release();
            Toast.makeText(this, getString(R.string.t_record_stop),
                    Toast.LENGTH_SHORT).show();
            mediaRecorder = null;
        }
    }

    /**
     * Start Playing activity
     */
    private void startPlaying() {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();

            mediaPlayer.setOnCompletionListener(mp -> {
                mp.reset();
                mp.release();
                mediaPlayer = null;
                isPlayingMedia = false;
                setButtonText();
            });

            try {
                mediaPlayer.setDataSource(fileNameMedia);
                mediaPlayer.setAudioAttributes(new AudioAttributes.Builder().setContentType(
                        AudioAttributes.CONTENT_TYPE_SPEECH)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()); // optional step
                mediaPlayer.prepare();
                mediaPlayer.start();
                Toast.makeText(this, getString(R.string.t_play_start),
                        Toast.LENGTH_SHORT).show();
                Log.d(TAG, "playback started with MediaPlayer");
            } catch (IOException e) {
                Log.e(TAG, "error reading from file while preparing MediaPlayer" + e);
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "illegal argument given " + e);
            }
        }
    }

    /**
     * Stop Recording activity
     */
    private void stopPlaying() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
            Toast.makeText(this, getString(R.string.t_play_stop),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Save Recording activity
     */
    private void saveRecording() {
        if (fileNameMedia != null &&  !isRecordingSaved) {
            // stop the recording and playing when click the save button
            stopRecording();
            stopPlaying();

            // create new file name with the time, category, and location
            // rename the old file to new file

            // https://docs.oracle.com/javase/8/docs/api/java/text/SimpleDateFormat.html
            String timeStamp = new SimpleDateFormat(getString(R.string.time_format),
                    Locale.getDefault()).format(new Date());
            String newFileName = getFilesDir().getPath() + "/"+ category + "_" + timeStamp +
                    ".mp3";
            File newFile = new File(newFileName);
            File testFile = new File(fileNameMedia);
            if(testFile.exists()) {
                //https://stackoverflow.com/questions/12181372/rename-a-file-in-the-internal
                // -storage
                testFile.renameTo(newFile);
                Toast.makeText(this, getString(R.string.t_save),
                        Toast.LENGTH_SHORT).show();
            }

            isRecordingSaved = true;
            setButtonText();
        }
    }

    /**
     * Set the button text changed based on the activity
     */
    private void setButtonText() {
        if (isRecordingMedia) {
            recordMediaRecorder.setText(R.string.stop_recording_button);
        } else {
            recordMediaRecorder.setText(R.string.start_recording_button);
        }

        if (isPlayingMedia) {
            playMediaPlayer.setText(R.string.stop_playing_button);
        } else {
            playMediaPlayer.setText(R.string.start_playing_button);
        }
        saveRecordingButton.setEnabled(!isRecordingMedia || isRecordingSaved);
    }

    // https://developer.android.com/develop/ui/views/components/spinner
    @Override
    /**
     * When select the item for category
     */
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        category = parent.getItemAtPosition(pos).toString();
        Toast.makeText(this, getString(R.string.t_category,
                        category),
                Toast.LENGTH_LONG).show();
    }

    /**
     * When no category is selected
     */
    public void onNothingSelected(AdapterView<?> parent) {
        category = "Null";
    }
}