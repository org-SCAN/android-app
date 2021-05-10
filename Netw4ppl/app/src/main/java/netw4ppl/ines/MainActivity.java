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

    public static boolean mLancementApplication = true;

    public static Configuration mConfiguration;

    static boolean FLAG_EMAIL_SUCCESS;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        device_language=Locale.getDefault().getISO3Language();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FileUtils.writeFile(this.getFilesDir().getPath()+"/cases/persons.json","[\n" +
                "    {\n" +
                "    \"unique_id\" : \"ABC-000001\",\n" +
                "    \"full_name\" : \"Patrice\",\n" +
                "    \"nationality\" : \"FRA\",\n" +
                "    \"date\" : \"2021-04-12\",\n" +
                "    \"age\" : 68,\n" +
                "    \"gender\" : \"F\"\n" +
                "    },\n" +
                "    {\n" +
                "    \"unique_id\" : \"ABC-000002\",\n" +
                "    \"full_name\" : \"Maryse\",\n" +
                "    \"nationality\" : \"USA\",\n" +
                "    \"date\" : \"2021-04-12\",\n" +
                "    \"age\" : 92,\n" +
                "    \"gender\" : \"F\"\n" +
                "    }\n" +
                "]");

        //Ajout Relation Crade!!!
        FileUtils.writeFile(this.getFilesDir().getPath()+"/cases/relations.json", "[\n" +
                "  {\n" +
                "    \"date\": \"2020-04-07\",\n" +
                "    \"relation\": \"Step Brother\",\n"+
                "    \"detail\" : \"Ceci est un détail sur la relation \",\n" +
                "    \"from_unique_id\": \"UQO-311916\",\n" +
                "    \"from_full_name\": \"Dr. Gay Hane\",\n" +
                "    \"to_unique_id\": \"QFL-290254\",\n" +
                "    \"to_full_name\": \"Dr. Emile Reichert\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"date\": \"2020-04-07\",\n" +
                "    \"relation\": \"Travelled With\",\n"+
                "    \"detail\" : \"Ceci est un détail sur la relation\",\n" +
                "    \"from_unique_id\": \"UQO-311916\",\n" +
                "    \"from_full_name\": \"Dr. Gay Hane\",\n" +
                "    \"to_unique_id\": \"OOJ-893809\",\n" +
                "    \"to_full_name\": \"Mr. Emmett Schroeder MD\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"date\": \"2020-04-07\",\n" +
                "    \"relation\": \"Travelled With\",\n" +
                "    \"detail\" : \"Ceci est un détail sur la relation\",\n" +
                "    \"from_unique_id\": \"QFL-290254\",\n" +
                "    \"from_full_name\": \"Dr. Emile Reichert\",\n" +
                "    \"to_unique_id\": \"XEZ-629536\",\n" +
                "    \"to_full_name\": \"Eleanore Wisoky III\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"date\": \"2020-04-07\",\n" +
                "    \"relation\": \"Step Brother\",\n"+
                "    \"detail\" : \"Ceci est un détail sur la relation\",\n" +
                "    \"from_unique_id\": \"KUJ-610430\",\n" +
                "    \"from_full_name\": \"Ansel Pagac MD\",\n" +
                "    \"to_unique_id\": \"XTK-018974\",\n" +
                "    \"to_full_name\": \"Ms. Rosalyn Dach I\"\n" +
                "  }\n" +
                "]\n");

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
        if (mLancementApplication) {
            // lecture de tous les json
            try {
                JSONObject config_content = FileUtils.loadConfigFromFile(this);

                mConfiguration = new Configuration(MainActivity.this, config_content);

                Log.d("general-display", "Hashmap database content: " + mConfiguration.getHashMapDatabase().toString());

                String ids = FileUtils.loadIdsFromFile(this);
                if (ids.equals(""))
                    AddPersonActivity.json_ids = new JSONObject();
                else
                    AddPersonActivity.json_ids = new JSONObject(ids);
                readPersonsFile(this);
                readRelationsFile(this);

                Log.d("test-read-ids", AddPersonActivity.json_ids.toString(2));

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            mLancementApplication = false;
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
}
