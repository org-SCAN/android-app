package netw4ppl.ines;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;

import java.util.ArrayList;

import netw4ppl.ines.utils.FileUtils;
import netw4ppl.ines.utils.Person;
import netw4ppl.ines.utils.PersonDetailsListAdapter;

public class DisplayDetailsPersonActivity extends AppCompatActivity {

    TextView mTextViewFullnameTitle;
    Button mButtonAddRelationFrom;
    Button mButtonEditPerson;
    Button mButtonDeletePerson;
    Button mButtonAddRelationTo;

    PersonDetailsListAdapter adapter_details_person;

    ListView mListRelationsFrom;
    ListView mListDetailsPerson;
    ListView mListRelationsTo;

    public static int index_person = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_details_person);

        mTextViewFullnameTitle = (TextView) findViewById(R.id.details_title_full_name);

        mButtonAddRelationFrom = (Button) findViewById(R.id.display_person_add_relation_from);
        mButtonAddRelationTo = (Button) findViewById(R.id.display_person_add_relation_to);
        mButtonEditPerson = (Button) findViewById(R.id.display_person_edit);
        mButtonDeletePerson = (Button) findViewById(R.id.display_person_delete);
        
        mListDetailsPerson = (ListView) findViewById(R.id.list_details_person);

        Person person = ManagePersonsActivity.array_persons.get(index_person);

        mTextViewFullnameTitle.setText(person.getInfoByKey("full_name"));

        adapter_details_person = new PersonDetailsListAdapter(this, R.layout.adapter_details_person_fields, MainActivity.mConfiguration.getArrayFields());
        mListDetailsPerson.setAdapter(adapter_details_person);

        mButtonAddRelationFrom.setOnClickListener(v -> {
            AddRelationActivity.setToPerson(person);
            AddRelationActivity.new_relation = true;
            Intent intent = new Intent(DisplayDetailsPersonActivity.this, AddRelationActivity.class);
            startActivity(intent);
        });
        mButtonEditPerson.setOnClickListener(v -> {
            AddPersonActivity.person = person;
            AddPersonActivity.new_person = false;
            Intent intent = new Intent(DisplayDetailsPersonActivity.this, AddPersonActivity.class);
            startActivity(intent);
        });
        mButtonDeletePerson.setOnClickListener(v-> {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.delete_person_title)
                    .setMessage(R.string.delete_person_message)
                    .setCancelable(true)
                    .setPositiveButton(R.string.yes, (a,b) -> {
                        // supprime les relations avant de supprimer la personne
                        boolean save_relations = this.deleteAssociatedRelations(ManagePersonsActivity.array_persons.get(index_person));

                        // supprime la personne de l'array
                        ManagePersonsActivity.array_persons.remove(index_person);

                        // sauvegarde le fichier
                        boolean save_persons = FileUtils.savePersonsToFile(this, ManagePersonsActivity.formatterJsonFile());
                        // quitte l'activité
                        if (save_relations && save_persons) {
                            // maj de la listview de l'activité ManagePersonActivity
                            // TODO pas forcément necessaire car effectué dans le onResume() de l'activité ManagePersonActivity
                            ManagePersonsActivity.updateAdapter();
                            finish();
                        }
                        SettingsActivity.displayToast(this, save_persons && save_relations ? getString(R.string.toast_delete_success) : getString(R.string.toast_delete_fail));
                    })
                    .setNegativeButton(R.string.button_relation_cancel_title, (a,b) -> {
                        // no op
                    })
                    .create()
                    .show();
        });
        mButtonAddRelationTo.setOnClickListener(v -> {
            AddRelationActivity.setFromPerson(person);
            AddRelationActivity.new_relation = true;
            Intent intent = new Intent(DisplayDetailsPersonActivity.this, AddRelationActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume () {
        super.onResume();
        adapter_details_person.notifyDataSetChanged();
    }

    public boolean deleteAssociatedRelations(Person p) {
        ArrayList<Integer> index_relations = new ArrayList<Integer>();

        // suppression dans les listes
        for (int i=0; i<ManageRelationsActivity.array_relations.size(); i++) {
            if (ManageRelationsActivity.array_relations.get(i).isPersonInRelation(p)) {
                index_relations.add(i);
            }
        }

        // pour chaque relation qui possède cette personne
        int index = 0;
        for (int i=0; i<index_relations.size(); i++) {
            ManageRelationsActivity.array_relations.remove(index_relations.get(i) + (-1*index));
            index++;
        }

        // sauvegarde le fichier
        boolean save_relations = FileUtils.saveRelationsToFile(this, ManageRelationsActivity.formatterJsonFile());

        return save_relations;
    }
}