package netw4ppl.ines;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

import netw4ppl.ines.utils.FileUtils;
import netw4ppl.ines.utils.Person;
import netw4ppl.ines.utils.PersonListAdapter;

public class ManagePersonsActivity extends AppCompatActivity {

    // TODO changer le static ici car c'est une memory leak
    private static PersonListAdapter mAdapter;
    FloatingActionButton mButtonAdd;
    ListView mListView;
    SearchView mSearchBar;
    public static HashMap<String, Person> hashmap_persons = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_persons);

        // lire le fichier files/cases/persons.json et initialiser array_persons
        try {
            readPersonsFile(this);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        mButtonAdd = (FloatingActionButton) findViewById(R.id.add_person_fab);

        mButtonAdd.setOnClickListener(v -> {
            Intent intent = new Intent(ManagePersonsActivity.this, AddPersonActivity.class);
            startActivity(intent);
        });

        mListView = (ListView) findViewById(R.id.list_nutshell_persons);

        mSearchBar = (SearchView) findViewById(R.id.searchViewPerson);

        // faire l'affichage
        Collection<Person> persons = ManagePersonsActivity.hashmap_persons.values();
        ArrayList<Person> listOfPersons = new ArrayList<>(persons);

        mAdapter = new PersonListAdapter(this, R.layout.adapter_nutshell_person_layout, listOfPersons);
        mListView.setAdapter(mAdapter);

        // attach setOnQueryTextListener
        // to search view defined above
        mSearchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // si on appuie sur le bouton de recherche
                if (query.length() > 0)
                    mAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // dès qu'on ajoute ou enlève une lettre
                mAdapter.getFilter().filter(newText);
                return false;
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
                // position correspond à la position de la personne dans l'adapter
                boolean got_it = false;
                Person p = (Person) adapter.getItemAtPosition(position);
                // associer cette position à la position réelle dans l'array de base
                String id_person = null;
                for (String key : hashmap_persons.keySet()) {
                    if (!got_it) {
                        if (hashmap_persons.get(key).equals(p)) {
                            got_it = true;
                            id_person = key;
                        }
                    }
                }

                // DisplayDetailsPersonActivity.index_person = index_reel;
                Intent intent = new Intent(ManagePersonsActivity.this, DisplayDetailsPersonActivity.class);
                intent.putExtra("id_person", id_person); //Put your id to your next Intent
                startActivity(intent);
            }
        });

    }

    /**
     * Function to update the adapter. During an onResume of the activity, this method will be called in
     * case there have been any changes in the adapter (add, modification or deletion of a person)
     */
    public static void updateAdapter() {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume () {
        super.onResume();
        mSearchBar.setQuery("", true);
        updateAdapter();
    }

    /**
     * Read the Persons File associated with this application.
     * The path is determined based on strings in the strings.xml file
     *
     * @param context the context of the current activity
     */
    public static void readPersonsFile(Context context) throws IOException, JSONException {
        String dir_name = context.getString(R.string.directory_files);
        String file_name = context.getString(R.string.filename_persons);
        String path_dir = context.getFilesDir().getPath()+dir_name;
        String path_file = context.getFilesDir().getPath()+dir_name+file_name;
        String content_file = "";
        JSONArray jsonArray_persons = null;
        hashmap_persons = new HashMap<>();
        if (FileUtils.directoryExists(path_dir) && FileUtils.fileExists(path_file)) {
            content_file = FileUtils.readFile(path_file);

            if (content_file.equals("")){
                jsonArray_persons = new JSONArray();
            }else{
                jsonArray_persons = new JSONArray(content_file);
            }

            for (int i=0; i<jsonArray_persons.length(); i++) {
                JSONObject json_person = jsonArray_persons.getJSONObject(i);
                hashmap_persons.put(UUID.randomUUID().toString(),new Person(json_person.toString()));
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
    }

    /**
     * Convert the ArrayList of Persons in JSONArray to then convert it in a String because the server
     * is expecting such format.
     *
     * @return a String containing the Persons contained in person, on a JSONArray format
     */
    public static String formatterJsonFile() {
        /*
        JSONArray json_array = new JSONArray();
        // for each key in the hashmap, get the person and add it to the json_array
        for (String key : hashmap_persons.keySet()) {
            json_array.put(hashmap_persons.get(key));
        }
        try {
            System.out.println(json_array.toString(2));
            return json_array.toString(2);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";

         */
        JSONObject jsonObject = new JSONObject(hashmap_persons);
        return jsonObject.toString();
    }
}