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
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;

import netw4ppl.ines.utils.DataElement;
import netw4ppl.ines.utils.FileUtils;
import netw4ppl.ines.utils.Person;
import netw4ppl.ines.utils.PersonListAdapter;
import netw4ppl.ines.utils.Relation;
import netw4ppl.ines.utils.RelationListAdapter;

public class ManageRelationsActivity extends AppCompatActivity {

    FloatingActionButton mButtonAdd;
    ListView mListView;
    SearchView mSearchBar;
    public static ArrayList<Relation> array_relations = new ArrayList<Relation>();
    public static RelationListAdapter mAdapter; // TODO changer le static ici car c'est une memory leak
    public static HashMap<String, String> hashmap_android_to_server_id = new HashMap<>();
    public static ArrayList<String> relation_types;
    public static ArrayList<Relation> array_relations_synced = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_relations);

        try {
            ManagePersonsActivity.readPersonsFile(this);
        } catch (IOException | JSONException e) {
            Log.d("context", String.valueOf(e));
        }

        mButtonAdd = (FloatingActionButton) findViewById(R.id.add_relation_fab);
        mButtonAdd.setOnClickListener(v -> {
            Intent intent = new Intent(ManageRelationsActivity.this, AddRelationActivity.class);
            startActivity(intent);
        });

        mListView = (ListView) findViewById(R.id.list_nutshell_relations);
        mSearchBar = (SearchView) findViewById(R.id.searchViewRelation);

        // Read the relation.json file and initializing array_relations
        try {
            readRelationsFile(this);
        } catch (IOException | JSONException e) {
            Log.d("context", String.valueOf(e));
        }

        mSearchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.length() > 0) {
                    mAdapter.getFilter().filter(query);
                    mListView.setAdapter(mAdapter);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.getFilter().filter(newText);
                mListView.setAdapter(mAdapter);
                return false;
            }
        });

        mAdapter = new RelationListAdapter(this, R.layout.adapter_nutshell_relation_layout, ManageRelationsActivity.array_relations);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
                boolean got_it = false;
                int index_reel = position;
                int i = 0;
                Relation r = (Relation) adapter.getItemAtPosition(position);

                while (i < array_relations.size() && !got_it) {
                    if (array_relations.get(i).equals(r)) {
                        index_reel = i;
                        got_it = true;
                    }
                    i++;
                }

                Intent intent = new Intent(ManageRelationsActivity.this, DisplayDetailsRelationActivity.class);

                Bundle b = new Bundle();
                b.putInt("index_relation", index_reel); // your index relation
                intent.putExtras(b); //Put your id to your next Intent

                startActivity(intent);
            }
        });
    }

    /**
     * Read the Relations File associated with this application.
     * The path is determined based on strings in the strings.xml file
     *
     * @param context the context of the current activity
     */
    public static void readRelationsFile(Context context) throws IOException, JSONException {
        String dir_name = context.getString(R.string.directory_files);
        String file_name = context.getString(R.string.filename_relations);
        String path_dir = context.getFilesDir().getPath()+dir_name;
        String path_file = context.getFilesDir().getPath()+dir_name+file_name;
        String content_file = "";
        JSONArray jsonArray_relations = null;
        array_relations = new ArrayList<Relation>();
        if (FileUtils.directoryExists(path_dir) && FileUtils.fileExists(path_file)) {
            content_file = FileUtils.readFile(path_file);

            if (content_file.equals(""))
                jsonArray_relations = new JSONArray();
            else
                jsonArray_relations = new JSONArray(content_file);
            for (int i=0; i<jsonArray_relations.length(); i++) {
                JSONObject json_relation = jsonArray_relations.getJSONObject(i);
                Relation relat = new Relation(json_relation.toString());

                Collection<Person> persons = ManagePersonsActivity.hashmap_persons.values();
                ArrayList<Person> listOfPersons = new ArrayList<>(persons);

                relat.associateIDWithNames(listOfPersons);

                array_relations.add(relat);
            }
        }
        else if (FileUtils.directoryExists(path_dir)) {
            // directory exists but file does not
            FileUtils.createFile(path_file);
        }
        else {
            // creates directory and file
            FileUtils.createDirectory(path_dir);
            FileUtils.createFile(path_file);
        }
    }

    /**
     * Convert the ArrayList of Relations in JSONArray to then convert it in a String because the server
     * is expecting such format.
     *
     * @return a String containing the Relations contained in relation, on a JSONArray format
     */
    public static String formatterJsonFile() throws JSONException {
        JSONArray json_array = new JSONArray();
        for (int i=0; i<array_relations.size(); i++) {
            json_array.put(array_relations.get(i));
        }
        try {
            return json_array.toString(2);
        } catch (JSONException e) {
            Log.d("context", String.valueOf(e));
        }
        return "";
    }

    @Override
    protected void onResume(){
        super.onResume();
        updateAdapter();
    }

    /**
     * Function to update the adapter. During an onResume of the activity, this method will be called in
     * case there have been any changes in the adapter (add, modification or deletion of a relation)
     */
    public static void updateAdapter(){
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Put the server id from the response string into the corresponding persons
     *
     * @param response_string the response from the server
     */
    public static void saveServerIds(Context context,String response_string) throws JSONException {
        if (response_string.equals("[]") || response_string.contains("error")) {
            return;
        }
        int index = 0;
        String[] id_array = response_string.split("\",\"");
        String[] clean_array = new String[id_array.length];
        for (String id : id_array) {
            id = id.replace("\"", "");
            id = id.replace("[", "");
            id = id.replace("]", "");
            clean_array[index] = id;
            index++;
        }
        index = 0;
        for (Relation relation : ManageRelationsActivity.array_relations) {
            relation.put("id", clean_array[index]);
            index++;
        }
        FileUtils.saveRelationsToFile(context, ManageRelationsActivity.formatterJsonFile());
    }

    /**
     * Function that add the pushed relations to the hashmap of synced relations
     */
    public static void syncRelations(Context context) throws JSONException {
        String dir_name = context.getString(R.string.directory_files);
        String file_name = context.getString(R.string.filename_synced_relations);
        String path_file = context.getFilesDir().getPath() + dir_name + file_name;

        array_relations_synced.clear();
        for (Relation relation : array_relations) {
            if (!array_relations_synced.contains(relation)) {
                array_relations_synced.add(relation);
            }
        }
        FileUtils.writeFile(path_file, formatterJsonFile());
    }
}