package netw4ppl.ines;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.google.gson.Gson;

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

    RecyclerView mRecyclerView;
    Button mButtonSave;
    Button mButtonCancel;

    private int index_person;
    public static Person person;
    private boolean new_person = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_person);

        // initialize some variables
        index_person = 0;
        new_person = true;
        person = new Person();

        // get the objects from the view
        mRecyclerView = findViewById(R.id.recycler_view_add_person);
        mButtonSave = findViewById(R.id.button_add_person_save);
        mButtonCancel = findViewById(R.id.button_add_person_cancel);

        // get the extra parameters we might have given to the activity
        Bundle extra_parameters = getIntent().getExtras();

        if(extra_parameters != null) {
            if (extra_parameters.containsKey("index_person")) {
                index_person = extra_parameters.getInt("index_person");
                try {
                    person = new Person(ManagePersonsActivity.array_persons.get(index_person).toString(2));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (extra_parameters.containsKey("new_person"))
                new_person = extra_parameters.getBoolean("new_person");
        }

        // set up the RecyclerView
        AdapterViewFields adapter = new AdapterViewFields(this, MainActivity.mConfiguration.getArrayFields(), new_person);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mButtonCancel.setOnClickListener(v -> this.finish());

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

                /*
                System.out.println("saving id");
                boolean save_ids = saveIds(this, person);
                System.out.println("saved id: " + save_ids);
                 */

                // enregistre les données dans le fichier associé
                boolean save_persons = FileUtils.savePersonsToFile(this, ManagePersonsActivity.formatterJsonFile());

                // reset some variables
                new_person = true;

                if (save_persons /*&& save_ids*/){
                    ManageRelationsActivity.updateRelations(person);
                    finish();
                }
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
        String default_val = "AAA";
        // si on déjà un default de défini, on le lit, sinon on l'ajoute
        if (json_ids.has("default")) {
            try {
                default_val = json_ids.getString("default");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else {
            setDefaultKey(default_val);
        }
        return default_val;
    }

    /**
     * Function to set the default key for the "unique ids".
     *
     * @param tricode a String composed of three letters
     */
    public static void setDefaultKey(String tricode) {
        try {
            json_ids.put("default", tricode);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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

    /**
     * Used to determine if the required fields for a person were completed or not.
     * A toast is displayed if some required fields are not completed, and the user is notified of the different
     * fields to complete.
     *
     * @return a boolean. True if all the required fields are correctly completed or false if not.
     */
    public boolean verificationInputPerson() {
        boolean all_good = true;
        ArrayList<Field> array_fields = MainActivity.mConfiguration.getArrayFields();
        // TODO passer ça dans le fichier string.xml
        String fields_a_remplir = this.getString(R.string.toast_fields_required)+"\n";

        for (int i=0; i<array_fields.size(); i++) {
            Field f = array_fields.get(i);
            if (f.getRequired() <= 1) {
                String f_key = f.getKey();
                String info_p = person.getInfoByKey(f_key);

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
        // ajout de l'id au dico des IDS et sauvegarde les toutes dans le fichier associé (not working)
        String[] id = person.getInfoByKey("unique_id").split("-");
        String letters_id = id[0];
        int figures_id = Integer.parseInt(id[1]);
        boolean save_ids = true;

        // considérer l'identifiant saisi comme étant l'ID par défaut.
        setDefaultKey(letters_id);

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

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        // function used when the screen is rotated
        super.onSaveInstanceState(outState);

        outState.putSerializable("person", new Gson().toJson(person));
        outState.putBoolean("new_person", new_person);
        outState.putInt("index_person", index_person);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        String info_person = (String) savedInstanceState.getSerializable("person");
        person = new Gson().fromJson(info_person, Person.class);

        new_person = savedInstanceState.getBoolean("new_person");
        index_person = savedInstanceState.getInt("index_person");
    }
}