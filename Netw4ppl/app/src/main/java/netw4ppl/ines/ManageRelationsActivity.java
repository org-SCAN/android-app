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
    public static RelationListAdapter mAdapter;

    public static void updateRelations(Person person) {
        // parcourir toutes les relations
        for (int i=0; i<ManageRelationsActivity.array_relations.size(); i++) {
            // si le unique id de la personne correspond à un unique_id dans la relation (from ou to)
            if (person.getInfoByKey("unique_id").equals(ManageRelationsActivity.array_relations.get(i).getFromID())) {
                Log.d("display", "Je m'appelais " + ManageRelationsActivity.array_relations.get(i).getFromFullname() + "et maintenant je m'appelle " + person.getInfoByKey("full_name"));
                ManageRelationsActivity.array_relations.get(i).setFromFullname(person.getInfoByKey("full_name"));
            }
            if (person.getInfoByKey("unique_id").equals(ManageRelationsActivity.array_relations.get(i).getToID())) {
                Log.d("display", "Je m'appelais " + ManageRelationsActivity.array_relations.get(i).getToFullname() + "et maintenant je m'appelle " + person.getInfoByKey("full_name"));
                ManageRelationsActivity.array_relations.get(i).setToFullname(person.getInfoByKey("full_name"));
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_relations);

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
            e.printStackTrace();
        }

        mSearchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.length() > 0)
                    mAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.getFilter().filter(newText);
                return false;
            }
        });

        mAdapter = new RelationListAdapter(this, R.layout.adapter_nutshell_relation_layout, ManageRelationsActivity.array_relations);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
                Intent intent = new Intent(ManageRelationsActivity.this, DisplayDetailsRelationActivity.class);

                Bundle b = new Bundle();
                b.putInt("index_relation", position); // your index relation
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
            jsonArray_relations = new JSONArray(content_file);
            for (int i=0; i<jsonArray_relations.length(); i++) {
                JSONObject json_relation = jsonArray_relations.getJSONObject(i);
                Relation relat = new Relation(json_relation.toString());

                relat.associateIDWithNames(ManagePersonsActivity.array_persons);

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
    public static String formatterJsonFile() {
        JSONArray json_array = new JSONArray();
        for (int i=0; i<array_relations.size(); i++) {
            json_array.put(array_relations.get(i));
        }
        try {
            return json_array.toString(2);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    protected void onResume(){
        super.onResume();
        updateAdapter();
    }

    public static void updateAdapter(){
        mAdapter.notifyDataSetChanged();
    }
}