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
            /* Ajout d'une nouvelle personne */
            if (new_person) {

                // ajout du champ date indispensable pour la database
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Calendar calendar = Calendar.getInstance();
                String date_creation = dateFormat.format(calendar.getTime());
                person.putInfo("date", date_creation);

                // ajout dans l'array de personnes
                ManagePersonsActivity.array_persons.add(person);

                // ajout de l'id au dico des IDS et sauvegarde les toutes dans le fichier associé
                String[] id = person.getInfoByKey("unique_id").split("-");
                try {
                    json_ids.put(id[0], Integer.valueOf(id[1]));
                    FileUtils.saveIdsToFile(this, json_ids.toString(2));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            /* Modification d'une personne existante */
            else {
                // on supprime l'ancienne version avant d'ajouter la nouvelle
                int index = DisplayDetailsPersonActivity.index_person;
                ManagePersonsActivity.array_persons.remove(index);
                ManagePersonsActivity.array_persons.add(index, person);
            }

            // enregistre les données dans le fichier associé
            boolean save_persons = FileUtils.savePersonsToFile(this, ManagePersonsActivity.formatterJsonFile());

            // reset some variables
            new_person = true;

            if (save_persons)
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

    public static boolean savePersonsFile(Context context) {
        String dir_name = context.getString(R.string.directory_files);
        String file_name = context.getString(R.string.filename_persons);
        String path_file = context.getFilesDir().getPath()+dir_name+file_name;



        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        new_person = true;
    }
}