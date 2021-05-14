package netw4ppl.ines;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import netw4ppl.ines.utils.AdapterViewFields;
import netw4ppl.ines.utils.FileUtils;
import netw4ppl.ines.utils.Person;

public class AddPersonActivity extends AppCompatActivity {

    public static JSONObject json_ids = new JSONObject();
    public static Person person = new Person();
    public static boolean new_person = true;

    RecyclerView mRecyclerView;
    Button mButtonSave;
    Button mButtonCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_person);

        mRecyclerView = findViewById(R.id.recycler_view_add_person);
        mButtonSave = findViewById(R.id.button_add_person_save);
        mButtonCancel = findViewById(R.id.button_add_person_cancel);

        // on regarde si on vient de DetailsPersonActivity ou pas
        if (new_person) {
            person = new Person();
        }

        // set up the RecyclerView
        AdapterViewFields adapter = new AdapterViewFields(this, MainActivity.mConfiguration.getArrayFields());
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mButtonCancel.setOnClickListener(v -> {

        });

        mButtonSave.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            /* Ajout d'une nouvelle personne */
            if (new_person) {

                // ajout du champ date indispensable pour la database
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

                String date_creation = dateFormat.format(calendar.getTime());
                person.putInfo("date", date_creation);

                // TODO rajouter le champs application_id

                // ajout dans l'array de personnes
                // TODO si ajout en première position, il faut notifier l'adapter etc
                ManagePersonsActivity.array_persons.add(person);
            }
            /* Modification d'une personne existante */
            else {
                // ajout de la date d'update
                TimeZone tz = TimeZone.getTimeZone("UTC");
                calendar.setTimeZone(tz);

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                dateFormat.setTimeZone(tz);
                String date_update = dateFormat.format(calendar.getTime());

                person.putInfo("date_update", date_update);

                // on supprime l'ancienne version avant d'ajouter la nouvelle
                int index = DisplayDetailsPersonActivity.index_person;
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
        });
    }

    public static String getDefaultKey() {
        return "AAA";
    }

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