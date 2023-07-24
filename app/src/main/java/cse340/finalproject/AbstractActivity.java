package cse340.finalproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public abstract class AbstractActivity extends AppCompatActivity {

    /**
     * Callback that is called when the activity is first created.
     * @param savedInstanceState contains the activity's previously saved state.
     */
    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Set up all of the click listeners for the settings and home buttons on the screen.
     */
    protected void setHomeListeners() {
        ImageButton home = findViewById(R.id.home);
        if (home != null) {
            home.setOnClickListener((view) -> {
                switchActivity(view.getContext(),
                        MainActivity.class);
                Toast.makeText(this, getString(R.string.t_back_to_home_page),
                        Toast.LENGTH_LONG).show();
            });

        }
    }

    /**
     * Switches to the provided view's context to the provided activity class.
     * @param context The context that we are switching from.
     * @param intentClass The activity class to switch to.
     */
    protected void switchActivity(Context context, Class<?> intentClass) {
        Intent settingsIntent = new Intent(context, intentClass);
        context.startActivity(settingsIntent);
    }
}

