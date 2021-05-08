package netw4ppl.ines;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import netw4ppl.ines.utils.Relation;
import netw4ppl.ines.utils.RelationListAdapter;

public class ManageRelationsActivity extends AppCompatActivity {

    FloatingActionButton mButtonAdd;
    ListView mListView;
    SearchView mSearchBar;
    public static ArrayList<Relation> array_relations = new ArrayList<Relation>();

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

        // lire le fichier files/cases/relations.json et initialiser array_relations
        try {
            readRelationsFile(this);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        // faire l'affichage
        RelationListAdapter adapter = new RelationListAdapter(this, R.layout.adapter_nutshell_relation_layout, ManageRelationsActivity.array_relations);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
                DisplayDetailsRelationActivity.index_relation = position;
                Intent intent = new Intent(ManageRelationsActivity.this, DisplayDetailsRelationActivity.class);
                startActivity(intent);
            }
        });
    }

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
                array_relations.add(new Relation(json_relation.toString()));
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

    public static String formatterJsonFile() throws JSONException {
        JSONArray json_array = new JSONArray();
        for (int i=0; i<array_relations.size(); i++) {
            json_array.put(array_relations.get(i));
        }
        return json_array.toString(2);
    }
}