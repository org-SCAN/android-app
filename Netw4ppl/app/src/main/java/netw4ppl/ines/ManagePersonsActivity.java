package netw4ppl.ines;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import netw4ppl.ines.utils.FileUtils;
import netw4ppl.ines.utils.Person;
import netw4ppl.ines.utils.PersonListAdapter;

public class ManagePersonsActivity extends AppCompatActivity {

    FloatingActionButton mButtonAdd;
    ListView mListView;
    SearchView mSearchBar;
    public static ArrayList<Person> array_persons = new ArrayList<Person>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_persons);

        mButtonAdd = (FloatingActionButton) findViewById(R.id.add_person_fab);

        mButtonAdd.setOnClickListener(v -> {
            Intent intent = new Intent(ManagePersonsActivity.this, AddPersonActivity.class);
            startActivity(intent);
        });

        mListView = (ListView) findViewById(R.id.list_nutshell_persons);

        mSearchBar = (SearchView) findViewById(R.id.searchViewPerson);


        // lire le fichier files/cases/refugees.json et initialiser array_persons
        try {
            String dir_name = getString(R.string.directory_files);
            String file_name = getString(R.string.filename_persons);
            String path_dir = getFilesDir().getPath()+dir_name;
            String path_file = getFilesDir().getPath()+dir_name+file_name;
            String content_file = "";
            JSONArray jsonArray_persons = null;
            this.array_persons = new ArrayList<Person>();
            if (FileUtils.directoryExists(path_dir) && FileUtils.fileExists(path_file)) {
                content_file = FileUtils.readFile(path_file);
                jsonArray_persons = new JSONArray(content_file);
                for (int i=0; i<jsonArray_persons.length(); i++) {
                    JSONObject json_person = jsonArray_persons.getJSONObject(i);
                    this.array_persons.add(new Person(json_person.toString()));
                }
            }
            else if (FileUtils.directoryExists(path_dir)) {
                // juste le dossier existe mais pas le fichier
                FileUtils.createFile(path_file);
            }
            else {
                // créer le dossier ET le fichier
                FileUtils.createDirectory(path_dir);
                FileUtils.createFile(path_file);
            }

            Log.d("general-display", array_persons.size() != 0 ? jsonArray_persons.toString(2) : "File does not exists or is just empty");

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        // faire l'affichage
        PersonListAdapter adapter = new PersonListAdapter(this, R.layout.adapter_nutshell_person_layout, ManagePersonsActivity.array_persons);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
                DisplayDetailsPersonActivity.index_person = position;
                Intent intent = new Intent(ManagePersonsActivity.this, DisplayDetailsPersonActivity.class);
                startActivity(intent);
            }
        });

    }
}