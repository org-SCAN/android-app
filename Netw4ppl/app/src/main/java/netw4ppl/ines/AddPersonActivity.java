package netw4ppl.ines;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import netw4ppl.ines.utils.AdapterViewFields;
import netw4ppl.ines.utils.Field;
import netw4ppl.ines.utils.FileUtils;
import netw4ppl.ines.utils.Person;

public class AddPersonActivity extends AppCompatActivity {

    public static JSONObject json_ids = new JSONObject();
    public static Person person = new Person();
    public static boolean new_person = true;

    RecyclerView mRecyclerView;
    Button mButtonSave;
    Button mButtonCancel;

    private int index_person;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_person);

        mRecyclerView = findViewById(R.id.recycler_view_add_person);
        mButtonSave = findViewById(R.id.button_add_person_save);
        mButtonCancel = findViewById(R.id.button_add_person_cancel);

        Bundle extra_parameter = getIntent().getExtras();
        index_person = -1;
        if(extra_parameter != null)
            index_person = extra_parameter.getInt("index_person");

        // on regarde si on vient de DetailsPersonActivity ou pas
        if (new_person) {
            person = new Person();
        }

        // set up the RecyclerView
        AdapterViewFields adapter = new AdapterViewFields(this, MainActivity.mConfiguration.getArrayFields());
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mButtonCancel.setOnClickListener(v -> {
            this.finish();
        });

        mButtonSave.setOnClickListener(v -> {

            if (verificationInputPerson()) {

                Calendar calendar = Calendar.getInstance();
                /* Ajout d'une nouvelle personne */
                if (new_person) {

                    // ajout du champ date indispensable pour la database
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    String date_creation = dateFormat.format(calendar.getTime());
                    person.putInfo("date", date_creation);

                    // ajouter du champ d'application id
                    String application_id = MainActivity.mConfiguration.getApplicationId();
                    person.putInfo("application_id", application_id);

                    // ajout dans l'array de personnes
                    ManagePersonsActivity.array_persons.add(person);
                }
                /* Modification d'une personne existante */
                else {
                    // ajout de la date d'update
                    TimeZone tz = TimeZone.getTimeZone("UTC");
                    calendar.setTimeZone(tz);

                    @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    dateFormat.setTimeZone(tz);
                    String date_update = dateFormat.format(calendar.getTime());

                    person.putInfo("date_update", date_update);

                    // on supprime l'ancienne version avant d'ajouter la nouvelle
                    int index = index_person;
                    ManagePersonsActivity.array_persons.remove(index);
                    ManagePersonsActivity.array_persons.add(index, person);
                }

                boolean save_ids = saveIds(this, person);

                // enregistre les données dans le fichier associé
                boolean save_persons = FileUtils.savePersonsToFile(this, ManagePersonsActivity.formatterJsonFile());

                // reset some variables
                new_person = true;

                if (save_persons && save_ids)
                    finish();
                else {
                    // TODO faire apparaitre un toast pour dire que la sauvegarde a échoué
                }
            }
        });
    }

    /**
     * Get the 3-letters code by default. Set by default at "AAA"
     *
     * @return a String which gives the 3-letters code by default
     */
    public static String getDefaultKey() {
        return "AAA";
    }

    /**
     * Return the next id available for a specific key given
     *
     * @param key a String of letters, usually a 3-letters code
     * @return an int of the first value available for this 3-letters code
     */
    public static int getNextId(String key) {
        int last_id = 0;
        if (json_ids.has(key))
            try {
                last_id = json_ids.getInt(key);
            } catch (JSONException e) {
                e.printStackTrace();
                last_id = 0;
            }
        return last_id+1;
    }

    public boolean verificationInputPerson() {
        boolean all_good = true;

        ArrayList<Field> array_fields = MainActivity.mConfiguration.getArrayFields();
        String fields_a_remplir = "Those fields are required:\n";

        for (int i=0; i<array_fields.size(); i++) {
            Field f = array_fields.get(i);
            if (f.getRequired() <= 1) {
                String f_key = f.getKey();
                String info_p = person.getInfoByKey(f_key);

                Log.d("general-display", f_key + " is required");

                if (info_p.equals("")) {
                    fields_a_remplir += f.getTitle() + " - ";
                    all_good = false;
                }
            }
        }

        if (!all_good) {
            // mettre le toast
            SettingsActivity.displayToast(this, fields_a_remplir);
        }

        return all_good;
    }

    /**
     * Save the file containing the ids (3-letters code + the value associated)
     * Check before saving if for the 3-letters code, the value associated is the biggest one or not.
     *
     * @param context context of the activity/application
     * @param person the person currently being added/edited
     * @return a boolean to determine whether the file was successfully saved or not
     */
    public boolean saveIds(Context context, Person person) {
        // ajout de l'id au dico des IDS et sauvegarde les toutes dans le fichier associé
        String[] id = person.getInfoByKey("unique_id").split("-");
        String letters_id = id[0];
        int figures_id = Integer.parseInt(id[1]);
        boolean save_ids = true;

        // regarder si la figures_id est > à celle contenu à la même clé dans le dict
        try {
            if (json_ids.getInt(letters_id) < figures_id) {
                json_ids.put(letters_id, figures_id);
            }
            // no need to change the value

        } catch (JSONException e) {
            // it means its the first value for this tricode so we need to save it
            try {
                json_ids.put(letters_id, figures_id);
            } catch (JSONException jsonException) {
                jsonException.printStackTrace();
            }
        }

        try {
            save_ids = FileUtils.saveIdsToFile(this, json_ids.toString(2));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return save_ids;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        new_person = true;
    }
}