package cse340.finalproject;
import android.os.Bundle;
import android.widget.Toast;

/**
 * The activity for the info page
 */
public class InfoActivity extends AbstractActivity {

    /**
     * Callback that is called when the activity is first created.
     * @param savedInstanceState contains the activity's previously saved state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info);
        setHomeListeners();
    }
}
