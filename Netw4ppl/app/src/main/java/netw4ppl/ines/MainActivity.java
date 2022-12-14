package netw4ppl.ines;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import netw4ppl.ines.utils.Configuration;
import netw4ppl.ines.utils.DataElement;
import netw4ppl.ines.utils.Field;
import netw4ppl.ines.utils.FileUtils;
import netw4ppl.ines.utils.Person;
import netw4ppl.ines.utils.PersonListAdapter;
import netw4ppl.ines.utils.Relation;
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
    ImageView mSettingsBtn;
    Button mSendDataBtn;
    ImageView mDataSyncState;
    Button mInfo;

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
        mDataSyncState = findViewById(R.id.main_activity_data_sync_state);
        mInfo = findViewById(R.id.info);

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
            try {
                SubmitData.manageSend(this, getString(R.string.directory_files));
                FileUtils.savePersonsToFile(this, ManagePersonsActivity.formatterJsonFile());
                FileUtils.saveRelationsToFile(this, ManageRelationsActivity.formatterJsonFile());
                updateSyncImage();
            } catch (IOException | InterruptedException | JSONException e) {
                Log.d("context", String.valueOf(e));
            }
        });
        mSettingsBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        });
        mInfo.setOnClickListener(v ->{
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("https://user-doc.netw4ppl.tech"));
                startActivity(intent);
        });

        if (mApplicationLaunch) {
            initiateData(this);
            mApplicationLaunch = false;
        }
        updateSyncImage();
    }

    private void updateSyncImage() {
        if (MainActivity.checkIfFullSynced()) {
            mDataSyncState.setImageResource(R.drawable.icons8_cloud_check_48);
        } else {
            mDataSyncState.setImageResource(R.drawable.icons8_cloud_48);
        }
    }

    public static boolean checkIfFullSynced(){
        PersonListAdapter.resetArrayPersons(ManagePersonsActivity.array_persons);

        Boolean relation_synced = ManageRelationsActivity.array_relations.equals(ManageRelationsActivity.array_relations_synced);
        Boolean person_synced = ManagePersonsActivity.array_persons.equals(ManagePersonsActivity.array_persons_synced);

        return relation_synced && person_synced;
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

    public static void initiateData(Context context) {
        loadConfiguration(context);

        boolean download_config = PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getResources().getString(R.string.settings_server_maj_auto_key),false);
        if (download_config){
            try {
                SubmitData.manageGet(context);
            } catch (IOException e) {
                Log.d("context", String.valueOf(e));
            } catch (InterruptedException e) {
                Log.d("context", String.valueOf(e));
            }
        }
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
            Log.d("context", String.valueOf(e));
        }
    }
}
