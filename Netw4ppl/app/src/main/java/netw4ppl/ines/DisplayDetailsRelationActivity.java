package netw4ppl.ines;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONException;

import netw4ppl.ines.utils.DataElement;
import netw4ppl.ines.utils.FileUtils;
import netw4ppl.ines.utils.Person;
import netw4ppl.ines.utils.PersonDetailsListAdapter;
import netw4ppl.ines.utils.Relation;

/**
 * Class used to display the details about a relation like the ids and names of the person associated,
 * the relation type and finally the comments about this relation if there are any.
 */
public class DisplayDetailsRelationActivity extends AppCompatActivity {

    TextView mTextViewFullnameTitle;
    TextView mTextViewFromFullname;
    TextView mTextViewRelationType;
    TextView mTextViewToFullname;
    TextView mTextViewComments;

    Button mButtonEditRelation;
    Button mButtonDeleteRelation;

    private int index_relation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_details_relation);

        /* Récupère ton bundle connard */
        Bundle extra_parameters = getIntent().getExtras();
        if (extra_parameters != null) {
            index_relation = extra_parameters.getInt("index_relation");
            Log.d("general-display", "Relation index: " + index_relation);
        }
        else {
            index_relation = 0;
        }

        mTextViewFromFullname = (TextView) findViewById(R.id.relation_from_fullname);
        mTextViewRelationType = (TextView) findViewById(R.id.relation_type);
        mTextViewToFullname = (TextView) findViewById(R.id.relation_to_fullname);
        mTextViewComments = (TextView) findViewById(R.id.relation_comment);

        mButtonEditRelation = (Button) findViewById(R.id.display_relation_edit);
        mButtonDeleteRelation = (Button) findViewById(R.id.display_relation_delete);

        Relation relation = ManageRelationsActivity.array_relations.get(index_relation);

        setViews(relation);

        mButtonEditRelation.setOnClickListener(v -> {
            Intent intent = new Intent(DisplayDetailsRelationActivity.this, AddRelationActivity.class);

            Bundle b = new Bundle();
            b.putInt("index_relation", index_relation); // your index relation
            b.putSerializable("relation", new Gson().toJson(ManageRelationsActivity.array_relations.get(index_relation)));
            b.putBoolean("new_relation", false);
            intent.putExtras(b); //Put your id to your next Intent

            startActivity(intent);
        });

        mButtonDeleteRelation.setOnClickListener(v-> {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.delete_relation_title)
                    .setMessage(R.string.delete_relation_message)
                    .setCancelable(true)
                    .setPositiveButton(R.string.yes, (a,b) -> {
                        // supprime la relation de l'array
                        ManageRelationsActivity.array_relations.remove(index_relation);
                        // sauvegarde le fichier
                        boolean save_relations = false;
                        try {
                            save_relations = FileUtils.saveRelationsToFile(this, ManageRelationsActivity.formatterJsonFile());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        // quitte l'activité
                        if (save_relations) {
                            // maj de la listview de l'activité ManagePersonActivity
                            // TODO pas forcément necessaire car effectué dans le onResume() de l'activité ManagePersonActivity
                            ManageRelationsActivity.updateAdapter();
                            finish();
                        }
                    })
                    .setNegativeButton(R.string.button_relation_cancel_title, (a,b) -> {
                        // no op
                    })
                    .create()
                    .show();

        });

    }

    /**
     * Function to set the views based on a Relation object given in parameters.
     *
     * @param relation a Relation object
     */
    private void setViews(Relation relation){
        mTextViewFromFullname.setText(relation.getFromBestDescriptiveValue());

        // associate the relation key with its string for a better visualisation
        String relation_key = relation.getInfoByKey("relation");
        ArrayAdapter relations_adapter = MainActivity.mConfiguration.getArrayAdapter("ListRelations");
        int pos_in_adapter = AddRelationActivity.getPositionInAdapter(relations_adapter, relation_key);
        String text_relation = relations_adapter.getItem(pos_in_adapter).toString();

        mTextViewRelationType.setText(text_relation); // relation.getInfoByKey("relation")
        mTextViewToFullname.setText(relation.getToBestDescriptiveValue());
        mTextViewComments.setText(relation.getDetails());
    }

    @Override
    protected void onResume(){
        super.onResume();
        setViews(ManageRelationsActivity.array_relations.get(index_relation));
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        // function used when the screen is rotated
        super.onSaveInstanceState(outState);

        outState.putInt("index_relation", index_relation);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        index_relation = savedInstanceState.getInt("index_relation");
    }
}