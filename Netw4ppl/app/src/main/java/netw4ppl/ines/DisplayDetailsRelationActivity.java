package netw4ppl.ines;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;

import netw4ppl.ines.utils.FileUtils;
import netw4ppl.ines.utils.Person;
import netw4ppl.ines.utils.PersonDetailsListAdapter;
import netw4ppl.ines.utils.Relation;

public class DisplayDetailsRelationActivity extends AppCompatActivity {

    TextView mTextViewFullnameTitle;
    TextView mTextViewFromFullname;
    TextView mTextViewRelationType;
    TextView mTextViewToFullname;
    TextView mTextViewComments;

    Button mButtonEditRelation;
    Button mButtonDeleteRelation;

    public static int index_relation = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_details_relation);

        mTextViewFullnameTitle = (TextView) findViewById(R.id.details_title_full_name);

        mTextViewFromFullname = (TextView) findViewById(R.id.relation_from_fullname);
        mTextViewRelationType = (TextView) findViewById(R.id.relation_type);
        mTextViewToFullname = (TextView) findViewById(R.id.relation_to_fullname);
        mTextViewComments = (TextView) findViewById(R.id.relation_comment);

        mButtonEditRelation = (Button) findViewById(R.id.display_relation_edit);
        mButtonDeleteRelation = (Button) findViewById(R.id.display_relation_delete);

        Relation relation = ManageRelationsActivity.array_relations.get(index_relation);

        setViews(relation);

        mButtonEditRelation.setOnClickListener(v -> {
            AddRelationActivity.single_relation = relation;
            AddRelationActivity.new_relation = false;
            Intent intent = new Intent(DisplayDetailsRelationActivity.this, AddRelationActivity.class);
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
                        boolean save_relations = FileUtils.saveRelationsToFile(this, ManageRelationsActivity.formatterJsonFile());
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

    private void setViews(Relation relation){

        mTextViewFullnameTitle.setText(relation.getInfoByKey("from_full_name")+relation.getInfoByKey("to_full_name"));
        mTextViewFromFullname.setText(relation.getInfoByKey("from_full_name"));
        mTextViewRelationType.setText(relation.getInfoByKey("relation"));
        mTextViewToFullname.setText(relation.getInfoByKey("to_full_name"));
        mTextViewComments.setText(relation.getInfoByKey("detail"));


    }

    @Override
    protected void onResume(){
        super.onResume();
        setViews(ManageRelationsActivity.array_relations.get(index_relation));
    }
}