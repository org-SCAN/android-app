package netw4ppl.ines;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
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
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

import netw4ppl.ines.utils.FileUtils;
import netw4ppl.ines.utils.Person;
import netw4ppl.ines.utils.PersonListAdapter;

public class ManagePersonsActivity extends AppCompatActivity {

    // TODO changer le static ici car c'est une memory leak
    public static PersonListAdapter mAdapter;
    FloatingActionButton mButtonAdd;
    ListView mListView;
    SearchView mSearchBar;
    public static HashMap<String, Person> hashmap_persons = new HashMap<>();
    public static ArrayList<Person> array_persons = new ArrayList<Person>();
    public static ArrayList<Person> array_persons_synced = new ArrayList<Person>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_persons);

        // lire le fichier files/cases/persons.json et initialiser array_persons
        try {
            readPersonsFile(this);
        } catch (IOException | JSONException e) {
            Log.d("context", String.valueOf(e));
        }

        mButtonAdd = (FloatingActionButton) findViewById(R.id.add_person_fab);

        mButtonAdd.setOnClickListener(v -> {
            Intent intent = new Intent(ManagePersonsActivity.this, AddPersonActivity.class);
            startActivity(intent);
        });

        mListView = (ListView) findViewById(R.id.list_nutshell_persons);

        mSearchBar = (SearchView) findViewById(R.id.searchViewPerson);

        array_persons = new ArrayList<>(ManagePersonsActivity.hashmap_persons.values());

        mAdapter = new PersonListAdapter(this, R.layout.adapter_nutshell_person_layout, array_persons);
        mListView.setAdapter(mAdapter);

        // attach setOnQueryTextListener
        // to search view defined above
        mSearchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // si on appuie sur le bouton de recherche
                if (query.length() > 0) {
                    mAdapter.getFilter().filter(query);
                    updateAdapter();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // dès qu'on ajoute ou enlève une lettre
                mAdapter.getFilter().filter(newText);
                updateAdapter();
                return false;
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
                Person p = (Person) adapter.getItemAtPosition(position);
                String id_person = p.getKey();
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
        mAdapter.update();
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
        JSONObject jsonObject_persons = null;
        if (FileUtils.directoryExists(path_dir) && FileUtils.fileExists(path_file)) {
        content_file = FileUtils.readFile(path_file);

            if (content_file.equals("")){
                jsonObject_persons = new JSONObject();
            }else{
                jsonObject_persons = new JSONObject(content_file);
            }

            Iterator<String> keys = jsonObject_persons.keys();
            while(keys.hasNext()) {
                String key = keys.next();
                if (jsonObject_persons.get(key) instanceof JSONObject) {
                    JSONObject json_person = jsonObject_persons.getJSONObject(key);
                    hashmap_persons.put(key,new Person(json_person.toString()));
                }
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
     * Convert the hashmap of Persons in JSONObject to then convert it in a String because the server
     * is expecting such format.
     *
     * @return a String containing the Persons contained in person, on a JSONObject format
     */
    public static String formatterJsonFile() {
        JSONObject jsonObject_persons = new JSONObject(hashmap_persons);
        return jsonObject_persons.toString();
    }

    /**
     * Put the server id from the response string into the corresponding persons
     *
     * @param response_string the response from the server
     */
    public static void saveServerIds(Context context,String response_string) throws JSONException {
        if (response_string == "[]" || response_string.contains("error")) {
            return;
        }
        String dir_name = context.getString(R.string.directory_files);
        String file_name = context.getString(R.string.filename_persons_server_id);
        String path_file = context.getFilesDir().getPath() + dir_name + file_name;
        int index = 0;
        String[] id_array = response_string.split("\",\"");
        String[] clean_array = new String[id_array.length];
        //print all values of the array
        for (String id : id_array) {
            id = id.replace("\"", "");
            id = id.replace("[", "");
            id = id.replace("]", "");
            clean_array[index] = id;
            index++;
        }
        index = 0;
        for (String key : ManagePersonsActivity.hashmap_persons.keySet()) {
            hashmap_persons.get(key).put("id", clean_array[index]);
            ManageRelationsActivity.hashmap_android_to_server_id.put(key, clean_array[index]);
            index++;
        }
        JSONObject jsonObject_server_ids = new JSONObject(ManageRelationsActivity.hashmap_android_to_server_id);
        FileUtils.writeFile(path_file, jsonObject_server_ids.toString());
    }

    /**
     * Function that add the pushed persons to the hashmap of synced persons
     */
    public static void syncPersons(Context context) {
        String dir_name = context.getString(R.string.directory_files);
        String file_name = context.getString(R.string.filename_synced_persons);
        String path_file = context.getFilesDir().getPath() + dir_name + file_name;

        String content = "";
        JSONArray json_array = new JSONArray();

        array_persons_synced.clear();
        for (Person person : array_persons) {
            json_array.put(person);
            if (!array_persons_synced.contains(person)) {
                array_persons_synced.add(person);
            }
        }

        try {
            content = json_array.toString(2);
        } catch (JSONException e) {
            Log.d("context", String.valueOf(e));
        }

        FileUtils.writeFile(path_file, content);
    }
}