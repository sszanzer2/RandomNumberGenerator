package com.mintedtech.randomnumbergenerator.activities;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

import static com.mintedtech.randomnumbergenerator.lib.Utils.showInfoDialog;

import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.preference.PreferenceManager;
import android.view.View;

import androidx.core.view.WindowCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.gson.Gson;
import com.mintedtech.randomnumbergenerator.R;
import com.mintedtech.randomnumbergenerator.databinding.ActivityMainBinding;
import com.mintedtech.randomnumbergenerator.lib.Utils;
import com.mintedtech.randomnumbergenerator.model.RandomNumber;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private RandomNumber mRandomNumber;
    private ArrayList<Integer> mNumberHistory;

    private EditText from;
    private EditText to;
    private TextView result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setUpContentView();
        // Initialize mRandomNumber using its no-arg constructor
        mRandomNumber = new RandomNumber();
        // Initialize mNumberHistory by calling the initializeHistoryList method
        initializeHistoryList(savedInstanceState, "history_key");
        setSupportActionBar(binding.toolbar);
        from=findViewById(R.id.et_prompt1);
        to=findViewById(R.id.et_prompt2);
        result= findViewById(R.id.tv_result);

        setUpFAB();
    }

    private void initializeHistoryList (Bundle savedInstanceState, String key)
    {
        if (savedInstanceState != null) {
            mNumberHistory = savedInstanceState.getIntegerArrayList (key);
        }
        else {
            String history = getDefaultSharedPreferences (this).getString (key, null);
            mNumberHistory = history == null ?
                    new ArrayList<> () : Utils.getNumberListFromJSONString (history);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save the mNumberHistory list to the bundle
        outState.putIntegerArrayList("history_key", mNumberHistory);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Convert mNumberHistory list to JSON string
        Gson gson = new Gson();
        String historyString = gson.toJson(mNumberHistory);

        // Save the JSON string to SharedPreferences
        PreferenceManager.getDefaultSharedPreferences(this)
                .edit()
                .putString("history_key", historyString)
                .apply();
    }

    private void setUpFAB() {
        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fromText = from.getText().toString();
                String toText = to.getText().toString();

                if (fromText.isEmpty() || toText.isEmpty()) {
                    // Display Toast message indicating error
                    Toast.makeText(MainActivity.this, "Please enter both numbers", Toast.LENGTH_SHORT).show();
                } else {
                    // Set the "from" and "to" numbers in the model
                    mRandomNumber.setFromTo(Integer.parseInt(from.getText().toString()), Integer.parseInt(to.getText().toString()));

                    // Get the current random number from the model
                    int randomNumber = mRandomNumber.getCurrentRandomNumber();

                    // Add the random number to the history list
                    mNumberHistory.add(randomNumber);

                    // Update the destination TextView with the random number
                    result.setText(String.valueOf(randomNumber));

                    // Display a Snackbar message
                    Snackbar.make(view, "Random number generated and added to history", Snackbar.LENGTH_LONG)
                            .setAnchorView(R.id.fab)
                            .setAction("Action", null).show();
                }
            }
        });
    }

    private void setUpContentView() {
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int itemId = item.getItemId();

        if (itemId == R.id.menu_show_history) {
            Utils.showInfoDialog(MainActivity.this, "History", "Numbers Generated: " + mNumberHistory.toString());
            return true;
        } else if (itemId == R.id.menu_clear_history) {
            mNumberHistory.clear();
            showInfoDialog(MainActivity.this, "History Cleared", "Your generated numbers have been cleared.");
            return true;
        } else if (itemId == R.id.menu_about) {
            showAbout();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }

    }
    private void showAbout() {
        String aboutText = getString(R.string.about_text);
        Snackbar.make(findViewById(android.R.id.content), aboutText, Snackbar.LENGTH_LONG).show();
    }
}