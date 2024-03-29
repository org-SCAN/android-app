package netw4ppl.ines;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ShareActionProvider;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONException;

import java.lang.reflect.Array;
import java.security.interfaces.RSAKey;
import java.util.ArrayList;

import netw4ppl.ines.utils.FileUtils;
import netw4ppl.ines.utils.Person;
import netw4ppl.ines.utils.PersonDetailsListAdapter;
import netw4ppl.ines.utils.PersonDetailsRelationFromAdapter;
import netw4ppl.ines.utils.PersonDetailsRelationToAdapter;
import netw4ppl.ines.utils.Relation;

/**
 * Class used to display the details about a Person. For the moment it is displaying the relations where this
 * person if the "From" (on the right of the screen) and the relations where this person if the "To" (on the left of the screen).
 * n the middle of the screen are displayed the details like the name, nationality etc.
 */
public class DisplayDetailsPersonActivity extends AppCompatActivity {

    TextView mTextViewFullnameTitle;
    Button mButtonAddRelationFrom;
    Button mButtonEditPerson;
    Button mButtonDeletePerson;
    Button mButtonAddRelationTo;
    Button mButtonShowRelations;

    LinearLayout mLayoutFrom;
    LinearLayout mLayoutTo;

    PersonDetailsListAdapter adapter_details_person;
    PersonDetailsRelationFromAdapter adapter_relations_from;
    PersonDetailsRelationToAdapter adapter_relations_to;

    ListView mListRelationsFrom;
    ListView mListDetailsPerson;
    ListView mListRelationsTo;

    public int index_person;
    private boolean show_relations;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_details_person);

        SharedPreferences prefs = getSharedPreferences(getString(R.string.show_relations_boolean_key), MODE_PRIVATE);
        show_relations = prefs.getBoolean(getString(R.string.show_relations_boolean_key), false);

        Bundle extra_parameter = getIntent().getExtras();
        index_person = 0;
        if(extra_parameter != null)
            index_person = extra_parameter.getInt("index_person");

        mTextViewFullnameTitle = (TextView) findViewById(R.id.details_title_full_name);

        mButtonAddRelationFrom = (Button) findViewById(R.id.display_person_add_relation_from);
        mButtonAddRelationTo = (Button) findViewById(R.id.display_person_add_relation_to);
        mButtonEditPerson = (Button) findViewById(R.id.display_person_edit);
        mButtonDeletePerson = (Button) findViewById(R.id.display_person_delete);
        mButtonShowRelations = (Button) findViewById(R.id.display_person_show_relations);

        mLayoutFrom = (LinearLayout) findViewById(R.id.linearlayout_relations_from);
        mLayoutTo = (LinearLayout) findViewById(R.id.linearlayout_relations_to);
        
        mListDetailsPerson = (ListView) findViewById(R.id.list_details_person);
        mListRelationsFrom = (ListView) findViewById(R.id.list_relations_from);
        mListRelationsTo = (ListView) findViewById(R.id.list_relations_to);

        Person person = ManagePersonsActivity.array_persons.get(index_person);

        mTextViewFullnameTitle.setText(person.getInfoByKey("full_name"));

        /* Adapters for the different list views */
        adapter_details_person = new PersonDetailsListAdapter(this, R.layout.adapter_details_person_fields, MainActivity.mConfiguration.getArrayFields(), index_person);
        mListDetailsPerson.setAdapter(adapter_details_person);

        updateAdapterFrom();
        updateAdapterTo();

        if (!show_relations) {
            mButtonShowRelations.setText(getString(R.string.button_show_relations_title));
            mLayoutFrom.setVisibility(View.GONE);
            mLayoutTo.setVisibility(View.GONE);
        }
        else {
            mButtonShowRelations.setText(getString(R.string.button_hide_relations_title));
        }

        mListRelationsFrom.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // position correspond à la position de la personne dans l'adapter
                boolean got_it = false;
                int index_reel = 0;
                int i=0;
                Relation r = (Relation) adapter_relations_from.getItem(position);
                String person_from_string = r.getFrom();

                // associer cette position à la position réelle dans l'array de base
                while (i < ManagePersonsActivity.array_persons.size() && !got_it) {
                    if (ManagePersonsActivity.array_persons.get(i).toString().equals(person_from_string)) {
                        index_reel = i;
                        got_it = true;
                    }
                    i++;
                }

                // on a désormais la bonne relation, il faut cherche l'index de la personne

                // DisplayDetailsPersonActivity.index_person = index_reel;
                Intent intent = new Intent(DisplayDetailsPersonActivity.this, DisplayDetailsPersonActivity.class);
                Bundle b = new Bundle();
                b.putInt("index_person", index_reel); //Your id
                intent.putExtras(b); //Put your id to your next Intent
                startActivity(intent);
            }
        });

        mListRelationsTo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // position correspond à la position de la personne dans l'adapter
                boolean got_it = false;
                int index_reel = 0;
                int i=0;
                Relation r = (Relation) adapter_relations_to.getItem(position);
                String person_from_string = r.getTo();

                // associer cette position à la position réelle dans l'array de base
                while (i < ManagePersonsActivity.array_persons.size() && !got_it) {
                    if (ManagePersonsActivity.array_persons.get(i).toString().equals(person_from_string)) {
                        index_reel = i;
                        got_it = true;
                    }
                    i++;
                }

                // DisplayDetailsPersonActivity.index_person = index_reel;
                Intent intent = new Intent(DisplayDetailsPersonActivity.this, DisplayDetailsPersonActivity.class);
                Bundle b = new Bundle();
                b.putInt("index_person", index_reel); //Your id
                intent.putExtras(b); //Put your id to your next Intent
                startActivity(intent);
            }
        });

        mButtonAddRelationFrom.setOnClickListener(v -> {

            // TODO changer ça
            Intent intent = new Intent(DisplayDetailsPersonActivity.this, AddRelationActivity.class);
            Bundle b = new Bundle();
            b.putBoolean("new_relation", true);
            b.putSerializable("to_person", new Gson().toJson(person));
            b.putBoolean("to_already_set", true);
            intent.putExtras(b);
            startActivity(intent);
        });
        mButtonEditPerson.setOnClickListener(v -> {
            Intent intent = new Intent(DisplayDetailsPersonActivity.this, AddPersonActivity.class);

            Bundle b = new Bundle();
            b.putInt("index_person", index_person); //Your id
            b.putBoolean("new_person", false);
            b.putSerializable("person", new Gson().toJson(person));
            intent.putExtras(b); //Put your id to your next Intent

            startActivity(intent);
        });
        mButtonShowRelations.setOnClickListener(v -> {
            show_relations = !show_relations;
            if (show_relations) {
                mLayoutTo.setVisibility(View.VISIBLE);
                mLayoutFrom.setVisibility(View.VISIBLE);
                mButtonShowRelations.setText(DisplayDetailsPersonActivity.this.getString(R.string.button_hide_relations_title));
//                mButtonShowRelations.setImageDrawable(this.getDrawable(R.drawable.baseline_visibility_24));
            }
            else {
                mLayoutFrom.setVisibility(View.GONE);
                mLayoutTo.setVisibility(View.GONE);
                mButtonShowRelations.setText(DisplayDetailsPersonActivity.this.getString(R.string.button_show_relations_title));
//                mButtonShowRelations.setImageDrawable(this.getDrawable(R.drawable.baseline_visibility_off_24));
            }
            SharedPreferences shared = getSharedPreferences(getString(R.string.show_relations_boolean_key),MODE_PRIVATE);
            SharedPreferences.Editor editor = shared.edit();
            editor.putBoolean(getString(R.string.show_relations_boolean_key),show_relations);
            editor.commit();
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
            // TODO changer ça
            Intent intent = new Intent(DisplayDetailsPersonActivity.this, AddRelationActivity.class);
            Bundle b = new Bundle();
            b.putBoolean("new_relation", true);
            b.putSerializable("from_person", new Gson().toJson(person));
            b.putBoolean("from_already_set", true);
            intent.putExtras(b);
            startActivity(intent);
        });
    }

    /**
     * Function to get all the Relation objects in the object ManageRelationsActivity.array_relations
     * where the person given in parameters is the "Person To".
     *
     * @param to_person a Person object
     * @return an ArrayList of Relation objects
     */
    private ArrayList<Relation> getRelationsFrom(Person to_person) {
        ArrayList<Relation> array_relations_from = new ArrayList<Relation>();

        for (int i=0; i<ManageRelationsActivity.array_relations.size(); i++) {
            Relation r = ManageRelationsActivity.array_relations.get(i);
            if (r.getToID().equals(to_person.getInfoByKey("unique_id"))) {
                array_relations_from.add(r);
            }
        }

        return array_relations_from;
    }

    /**
     * Function to update the adapter of "relations From" in case the content of the adapter has changed.
     * Example: A relation From was added.
     */
    private void updateAdapterFrom() {
        Person person = ManagePersonsActivity.array_persons.get(index_person);
        ArrayList<Relation> array_relations_from = getRelationsFrom(person);
        adapter_relations_from = new PersonDetailsRelationFromAdapter(this, R.layout.adapter_relation_details_person, array_relations_from);
        mListRelationsFrom.setAdapter(adapter_relations_from);
    }

    /**
     * Function to update the adapter of "relations To" in case the content of the adapter has changed.
     * Example: A relation To was added.
     */
    private void updateAdapterTo() {
        Person person = ManagePersonsActivity.array_persons.get(index_person);
        ArrayList<Relation> array_relations_to = getRelationsTo(person);
        adapter_relations_to = new PersonDetailsRelationToAdapter(this, R.layout.adapter_relation_details_person, array_relations_to);
        mListRelationsTo.setAdapter(adapter_relations_to);
    }

    /**
     * Function to get all the Relation objects in the object ManageRelationsActivity.array_relations
     * where the person given in parameters is the "Person From".
     *
     * @param from_person a Person object
     * @return an ArrayList of Relation objects
     */
    private ArrayList<Relation> getRelationsTo(Person from_person) {
        ArrayList<Relation> array_relations_to = new ArrayList<Relation>();

        for (int i=0; i<ManageRelationsActivity.array_relations.size(); i++) {
            Relation r = ManageRelationsActivity.array_relations.get(i);
            if (r.getFromID().equals(from_person.getInfoByKey("unique_id"))) {
                array_relations_to.add(r);
            }
        }

        return array_relations_to;
    }

    @Override
    protected void onResume () {
        super.onResume();
        adapter_details_person.notifyDataSetChanged();
        updateAdapterFrom();
        updateAdapterTo();
        mTextViewFullnameTitle.setText(ManagePersonsActivity.array_persons.get(index_person).getInfoByKey("full_name"));
    }

    /**
     * Function used to delete the relations where the person was present in. Used only when we delete a person.
     *
     * @param p a Person object
     * @return a boolean. true if the relations were successfully deleted and saved
     */
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

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        // sauvegarder le boolean qui décide de l'affichage ou non des ailettes du triptyque.
        outState.putBoolean("show_relations", show_relations);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState.containsKey("show_relations"))
            show_relations = savedInstanceState.getBoolean("show_relations");
    }
}
