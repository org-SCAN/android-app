package netw4ppl.ines;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import netw4ppl.ines.utils.Configuration;
import netw4ppl.ines.utils.DataElement;
import netw4ppl.ines.utils.Field;
import netw4ppl.ines.utils.FileUtils;
import netw4ppl.ines.utils.SubmitData;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import static netw4ppl.ines.ManagePersonsActivity.readPersonsFile;
import static netw4ppl.ines.ManageRelationsActivity.readRelationsFile;

public class MainActivity extends AppCompatActivity{

    Button mManagePersonsBtn;
    Button mManageRelationsBtn;
    Button mSettingsBtn;
    Button mSendDataBtn;

    public static String device_language;
    public static boolean mApplicationLaunch = true;
    public static Configuration mConfiguration;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        device_language=Locale.getDefault().getISO3Language();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("life-cycle", "Main onCreate()");

        // get the button views from the xml file
        mManagePersonsBtn = findViewById(R.id.main_activity_manage_persons_btn);
        mManageRelationsBtn = findViewById(R.id.main_activity_manage_relations_btn);
        mSettingsBtn = findViewById(R.id.main_activity_settings_btn);
        mSendDataBtn = findViewById(R.id.main_activity_send_data_btn);

        // add listeners to every buttons
        mManagePersonsBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ManagePersonsActivity.class);
            startActivity(intent);
        });
        mManageRelationsBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ManageRelationsActivity.class);
            startActivity(intent);
        });
        mSendDataBtn.setOnClickListener(v -> {
            Log.d("Send Data","ON EST LA");
            try {
                SubmitData.manageSend(this, getString(R.string.directory_files));
            } catch (IOException | InterruptedException | JSONException e) {
                e.printStackTrace();
            }
        });
        mSettingsBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

        // si c'est le lancement de l'application, chargement en mémoire des json de configuration
        if (mApplicationLaunch) {
            loadConfiguration(this);

            boolean download_config = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(this.getResources().getString(R.string.settings_server_maj_auto_key),false);
            if (download_config){
                try {
                    SubmitData.manageGet(this);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            mApplicationLaunch = false;
        }
    }

    @Override
    protected void onStart () {
        super.onStart();
        Log.d("life-cycle","Main onStart()");
    }

    @Override
    protected void onStop () {
        super.onStop();
        Log.d("life-cycle", "Main onStop()");
    }

    @Override
    protected void onResume () {
        super.onResume();
        Log.d("life-cycle", "Main onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("life-cycle", "Main onPause()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("life-cycle", "Main onDestroy()");
    }

    /**
     * Function to load the configuration and read all the jsons files.
     *
     * @param context the application context
     */
    public static void loadConfiguration(Context context) {
        // lecture de tous les json
        try {
            JSONObject config_content = FileUtils.loadConfigFromFile(context);

            mConfiguration = new Configuration(context, config_content);

            Log.d("general-display", "Hashmap database content: " + mConfiguration.getHashMapDatabase().toString());

            /*
            String ids = FileUtils.loadIdsFromFile(context);
            if (ids.equals(""))
                AddPersonActivity.json_ids = new JSONObject();
            else
                AddPersonActivity.json_ids = new JSONObject(ids);

             */
            readPersonsFile(context);
            readRelationsFile(context);

            //Log.d("test-read-ids", AddPersonActivity.json_ids.toString(2));

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }
}
