package netw4ppl.ines;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

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
    public static ArrayList<Field> array_fields = new ArrayList<Field>();
    public static HashMap<String, ArrayList<DataElement>> hashMap_database= new HashMap<String, ArrayList<DataElement>>();

    static boolean FLAG_EMAIL_SUCCESS;

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
            } catch (IOException | InterruptedException e) {
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
                createArrayFields(config_content.getJSONObject("fields"));
                createHashMapDatabase(config_content);

                Log.d("general-display", "Hashmap database content: " + hashMap_database.toString());

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

    private void createHashMapDatabase(JSONObject config_content) {
        // récupère toutes les clés de l'objet json config_content
        Iterator<String> iterator_table = config_content.keys();
        while (iterator_table.hasNext()) {
            String key_table = iterator_table.next();
            try {
                JSONObject table = config_content.getJSONObject(key_table);
                // récupère toutes les clés de l'objet iterator_table
                Iterator<String> iterator_elements = table.keys();

                if (!key_table.equals("fields")) {
                    // create the ArrayList
                    ArrayList<DataElement> data_array = new ArrayList<DataElement>();
                    while (iterator_elements.hasNext()) {
                        String key_element = iterator_elements.next();
                        JSONObject element = table.getJSONObject(key_element);
                        data_array.add(new DataElement(key_element, element.toString()));
                    }
                    // then add everything to the HashMap
                    hashMap_database.put(key_table, data_array);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void createArrayFields(JSONObject fields) {
        Iterator<String> iterator = fields.keys();
        while (iterator.hasNext()) {
            String key = iterator.next();
            try {
                array_fields.add(new Field(key, fields.getJSONObject(key).toString()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
