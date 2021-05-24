package netw4ppl.ines;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;

import netw4ppl.ines.utils.DataElement;
import netw4ppl.ines.utils.FileUtils;
import netw4ppl.ines.utils.Person;
import netw4ppl.ines.utils.PersonListAdapter;
import netw4ppl.ines.utils.Relation;

public class AddRelationActivity extends AppCompatActivity {

    private static final String TAG = "AddRelationActivity";

    private Person from_person;
    private Person to_person;

    private Relation relation;
    private int index_relation;
    private boolean new_relation;
    private boolean to_already_set;
    private boolean from_already_set;
    private boolean rotation_screen;

    AutoCompleteTextView mAutoTextViewRelationFrom;
    AutoCompleteTextView mAutoTextViewRelationTo;
    Spinner mSpinnerRelationType;
    EditText mEditTextRelationComments;
    Button mButtonRelationSave;
    Button mButtonRelationCancel;

    ArrayAdapter<DataElement> spinner_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_relation);

        new_relation = true;
        relation = new Relation();
        rotation_screen = false;
        index_relation = 0;

        from_person = new Person();
        to_person = new Person();

        from_already_set = false;
        to_already_set = false;

        // get the parameters from the bundle (cas du lancement de l'activité pour la "1ere" fois
        Bundle extra_parameters = getIntent().getExtras();
        if (extra_parameters != null) {
            if (extra_parameters.containsKey("relation")){
                String string_relation = (String) extra_parameters.getSerializable("relation");
                relation = new Gson().fromJson(string_relation, Relation.class);
            }
            if (extra_parameters.containsKey("index_relation"))
                index_relation = extra_parameters.getInt("index_relation");
            if (extra_parameters.containsKey("new_relation"))
                new_relation = extra_parameters.getBoolean("new_relation");
            if (extra_parameters.containsKey("to_person")) {
                String string_to_person = (String) extra_parameters.getSerializable("to_person");
                to_person = new Gson().fromJson(string_to_person, Person.class);
                relation.setPersonTo(to_person);
            }
            if (extra_parameters.containsKey("from_person")) {
                String string_from_person = (String) extra_parameters.getSerializable("from_person");
                from_person = new Gson().fromJson(string_from_person, Person.class);
                relation.setPersonFrom(from_person);
            }
            if (extra_parameters.containsKey("from_already_set"))
                from_already_set = extra_parameters.getBoolean("from_already_set");
            if (extra_parameters.containsKey("to_already_set"))
                to_already_set = extra_parameters.getBoolean("to_already_set");
        }

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("relation")){
                String string_relation = (String) savedInstanceState.getSerializable("relation");
                relation = new Gson().fromJson(string_relation, Relation.class);
            }
            if (savedInstanceState.containsKey("index_relation"))
                index_relation = savedInstanceState.getInt("index_relation");
            if (savedInstanceState.containsKey("new_relation"))
                new_relation = savedInstanceState.getBoolean("new_relation");
            if (savedInstanceState.containsKey("to_person")) {
                String string_to_person = (String) savedInstanceState.getSerializable("to_person");
                to_person = new Gson().fromJson(string_to_person, Person.class);
                relation.setPersonTo(to_person);
            }
            if (savedInstanceState.containsKey("from_person")) {
                String string_from_person = (String) savedInstanceState.getSerializable("from_person");
                from_person = new Gson().fromJson(string_from_person, Person.class);
                relation.setPersonFrom(from_person);
            }
            if (savedInstanceState.containsKey("from_already_set"))
                from_already_set = savedInstanceState.getBoolean("from_already_set");
            if (savedInstanceState.containsKey("to_already_set"))
                to_already_set = savedInstanceState.getBoolean("to_already_set");
        }

        // get the button views from the layout
        mButtonRelationSave = findViewById(R.id.display_add_relation_save);
        mButtonRelationCancel = findViewById(R.id.display_add_relation_cancel);
        mAutoTextViewRelationFrom = findViewById(R.id.add_relation_from);
        mAutoTextViewRelationTo = findViewById(R.id.add_relation_to);
        mSpinnerRelationType = findViewById(R.id.add_relation_type);
        mEditTextRelationComments = findViewById(R.id.add_relation_comments);

        /*
        * Associer au spinner de type de relations les objets de types de relations
        * Associer aux AutoCompleteTextView la liste des personnes présentent dans l'application
        * */
        setAdapters();

        Log.d("general", "onCreate() -> set the views");

        // complete the views with the edit relation informations
        if (!new_relation){
            setEditInformation(relation);
        }
        else {
            if (to_already_set)
                setToPersonView();
            if (from_already_set)
                setFromPersonView();
        }

        /*
        * Set les listeners pour les différents objets
        * Ajout de deux onClickListeners pour les AutoCompleteTextView
        * Ajout d'un TextWatcher pour le TextView (pour les commentaires sur la relation)
        * Ajout d'un onSelectedItemListener pour le spinner du type de relations
        * */
        setListeners();

        mButtonRelationSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get the informations about the relation in the views
                boolean valid_relation = testValidRelation(relation);
                boolean already_exists = testExistingRelation(relation);
                boolean success_write = false;

                try {
                    Log.d("save-relation-button", relation.toString(2));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // si la relation est valide on passe à l'étape suivante
                if (valid_relation) {
                    if (new_relation) {
                        // dans le cas d'un ajout, si la relation existe, adios mec
                        if (!already_exists) {
                            // ajout du champ application id
                            relation.setApplicationID(MainActivity.mConfiguration.getApplicationId());

                            // ajout du champ date
                            relation.setCreationDate();

                            ManageRelationsActivity.array_relations.add(relation);
                            success_write = FileUtils.saveRelationsToFile(getApplicationContext(), ManageRelationsActivity.formatterJsonFile());
                        }
                        else {
                            // display du message d'erreur
                            String toast_text = AddRelationActivity.this.getString(R.string.toast_already_existing_relation);
                            Toast toast = Toast.makeText(AddRelationActivity.this, toast_text, Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                    // si c'est une edition, on remove puis on ajoute
                    else {

                        // ajout du champ date_update
                        relation.setUpdateDate();
                        ManageRelationsActivity.array_relations.remove(index_relation);
                        ManageRelationsActivity.array_relations.add(index_relation, relation);
                        success_write = FileUtils.saveRelationsToFile(getApplicationContext(), ManageRelationsActivity.formatterJsonFile());
                    }
                }

                if (success_write) {
                    resetObjects();
                    finish();
                }
            }
        });

        mButtonRelationCancel.setOnClickListener(v -> {
            this.finish();
        });
    }
    
    /**
     * Reset the static objects present in the class.
     * TODO change this function, having static object is a source of errors and unpredictable behaviours
     */
    public void resetObjects() {
        from_person = null;
        to_person = null;
        new_relation = true;
    }

    /**
     * Function to associate specific information with a person saved in the application.
     * Example of string_p: "AAA-000001 - John DOE"
     *
     * @param unique_id a String containing a unique_id (ex: AAA-000001)
     * @return int
     */
    public int associateInfosWithPerson(String unique_id) {
        // aller chercher dans la liste ManagePersonActivity.array_persons la personne associée à ces deux éléments
        boolean found = false;
        int i= 0;
        int index_p = -1;
        while (i < ManagePersonsActivity.array_persons.size() && !found) {
            Person p = ManagePersonsActivity.array_persons.get(i);

            if (p.getInfoByKey("unique_id").equals(unique_id)) {
                index_p = i;
                found = true;
            }

            i++;
        }
        return index_p;
    }

    /**
     * Sets the listeners of the autocompleteTextView
     * onItemClick methods initialize from_person and to_person with the Persons clicked in the
     * AutoCompletTextView dropdown
     */
    private void setListeners() {
        mAutoTextViewRelationFrom.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                from_person = (Person) parent.getAdapter().getItem(position);
                relation.setPersonFrom(from_person);
            }
        });

        mAutoTextViewRelationTo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                to_person = (Person) parent.getAdapter().getItem(position);
                relation.setPersonTo(to_person);
            }
        });

        mSpinnerRelationType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                DataElement data_element = (DataElement) parent.getItemAtPosition(position);
                if (!data_element.getKey().equals("NA")) {
                    relation.setRelationType(data_element.getKey());
                }
                else {
                    relation.remove("relation");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // ne rien faire
            }
        });

        mEditTextRelationComments.addTextChangedListener(new TextListenerDetailsRelation());
    }

    /**
     * A function to set the adapters for the different objects on the Add Relation Activity.
     * We basically have to set the adapters for the two AutocompleteTextViews and one for the spinner.
     * For the definition of Adapter, please check the Android Studio documentation.
     */
    private void setAdapters() {
        ArrayAdapter<Person> autocomplete_adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, ManagePersonsActivity.array_persons);
        mAutoTextViewRelationFrom.setAdapter(autocomplete_adapter);
        mAutoTextViewRelationTo.setAdapter(autocomplete_adapter);

        spinner_adapter = MainActivity.mConfiguration.getArrayAdapter("Relations");
        mSpinnerRelationType.setAdapter(spinner_adapter);
    }

    /**
     * Checks if the two Persons given for the relation are different or not
     *
     * @param relation a Relation object
     *
     * @return a boolean corresponding to the result of the test
     * Warning, it returns the opposite of the equal test
     * True if p1 and p2 are different
     */
    private boolean samePersonRelation(Relation relation){
        boolean test = (relation.getInfoByKey("from_unique_id").equals(relation.getInfoByKey("to_unique_id")));
        String toast_text = this.getString(R.string.toast_same_person_relation);

        if (test){
            Toast toast = Toast.makeText(this, toast_text, Toast.LENGTH_SHORT);
            toast.show();
        }
        return test;
    }

    /**
     * Checks if the relation that is being created does not already exist
     * Considers if the relation is being edited or is a new one
     *
     * @param relation Relation to be compared to the already existing ones
     * @return Boolean true if the Relation does not exist, else false
     */
    private boolean testExistingRelation(Relation relation){
        for(int i=0 ; i<ManageRelationsActivity.array_relations.size();i++){
            if (relation.isSameRelation(ManageRelationsActivity.array_relations.get(i))){
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the given relation_type is a valid one
     * @param relation a relation
     * @return a boolean corresponding to the validity of the relation_type
     */
    private boolean testRelationType(Relation relation) {
        String relation_type = relation.getRelationType();

        String toast_text = this.getString(R.string.toast_relation_type_non_selected);
        boolean test_relation_type = relation_type.equals("");
        if (test_relation_type){
            Toast toast = Toast.makeText(this, toast_text, Toast.LENGTH_SHORT);
            toast.show();
        }
        return (!test_relation_type);
    }

    private boolean isPersonToValid(Relation relation) {
        boolean is_empty = relation.getInfoByKey("to_unique_id").equals("");
        String toast_text = this.getString(R.string.toast_person_not_selected);
        if (is_empty) {
            Toast toast = Toast.makeText(this, toast_text, Toast.LENGTH_SHORT);
            toast.show();
        }
        return !is_empty;
    }

    private boolean isPersonFromValid(Relation relation) {
        boolean is_empty = relation.getInfoByKey("from_unique_id").equals("");
        String toast_text = this.getString(R.string.toast_person_not_selected);
        if (is_empty) {
            Toast toast = Toast.makeText(this, toast_text, Toast.LENGTH_SHORT);
            toast.show();
        }
        return !is_empty;
    }

    /**
     * Aggregates all the test to generate a relation
     * @return a boolean to know if the relation can be written
     */
    private boolean testValidRelation(Relation relation){
        return (testRelationType(relation) && isPersonFromValid(relation) && isPersonToValid(relation) && !samePersonRelation(relation));
    }

    private void setToPersonView() {
        // informations contenues dans to_person
        mAutoTextViewRelationTo.setText(to_person.toString(), false);
        mAutoTextViewRelationTo.setEnabled(false);

        // TODO à changer car c'est pas très propre
        // empêcher le dropdown de pop
        mAutoTextViewRelationTo.setAdapter((ArrayAdapter<Person>) null);
    }

    private void setFromPersonView() {
        mAutoTextViewRelationFrom.setText(from_person.toString(), false);
        mAutoTextViewRelationFrom.setEnabled(false);

        // TODO à changer car c'est pas très propre
        // empêcher le dropdown de pop
        mAutoTextViewRelationFrom.setAdapter((ArrayAdapter<Person>) null);
    }

    private void setPersonsViews(Relation relation) {
        // associate the ids and full_names with the Person objects and set the variables
        int index_from = associateInfosWithPerson(relation.getFromID());
        int index_to = associateInfosWithPerson(relation.getToID());

        from_person = ManagePersonsActivity.array_persons.get(index_from);
        to_person = ManagePersonsActivity.array_persons.get(index_to);

        setFromPersonView();
        setToPersonView();
    }

    /**
     * Gets the informations of the relation that is being edited
     * Sets the Views of the activity so they contain the informations of the edited relation
     */
    private void setEditInformation(Relation relation){
        setPersonsViews(relation);

        String relation_key = relation.getRelationType();
        int pos_in_adapter = getPositionInAdapter(spinner_adapter, relation_key);
        mSpinnerRelationType.setSelection(pos_in_adapter);

        mEditTextRelationComments.setText(relation.getDetails());
    }

    /**
     * Returns the index of an item identified by a key in an adapter
     * @param adapter
     * @param key_val_pers
     * @return an int corresponding to the index of the key in the adapter
     */
    public static int getPositionInAdapter(Adapter adapter, String key_val_pers) {
        DataElement data_element;
        for (int i=0; i<adapter.getCount(); i++) {
            data_element = (DataElement) adapter.getItem(i);
            if (data_element.getKey().equals(key_val_pers))
                return i;
        }
        return 0;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        resetObjects();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        // ajouter les objets à sauvegarder
        Gson gson = new Gson();
        outState.putSerializable("from_person", gson.toJson(from_person));
        outState.putSerializable("to_person", gson.toJson(to_person));
        outState.putSerializable("relation", gson.toJson(relation));

        outState.putBoolean("new_relation", new_relation);
        outState.putBoolean("rotation_screen", true);
        outState.putBoolean("from_already_set", from_already_set);
        outState.putBoolean("to_already_set", to_already_set);
    }

    // we make TextWatcher to be aware of the position it currently works with
    // this way, once a new item is attached in onBindViewHolder, it will
    // update current position MyCustomEditTextListener, reference to which is kept by ViewHolder
    private class TextListenerDetailsRelation implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            // no op
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            // récupérer le texte saisi
            String comments = editable.toString();

            if (!comments.equals(""))
                relation.putInfo("detail", comments);
            else {
                relation.remove("detail");
            }

        }
    }
}