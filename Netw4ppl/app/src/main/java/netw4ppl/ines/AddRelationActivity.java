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

    public static Person from_person;
    public static Person to_person;

    private Relation relation;
    private int index_relation;
    private boolean new_relation;
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

        // get the parameters from the bundle (cas du lancement de l'activité pour la "1ere" fois
        Bundle extra_parameters = getIntent().getExtras();
        if (extra_parameters != null) {
            String string_relation = (String) extra_parameters.getSerializable("relation");
            relation = new Gson().fromJson(string_relation, Relation.class);
            index_relation = extra_parameters.getInt("index_relation");
            new_relation = extra_parameters.getBoolean("new_relation");
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

        // complete the views with the edit relation informations
        if (!new_relation){
            setEditInformation();

            // désactiver les champs de saisie des personnes
            mAutoTextViewRelationFrom.setEnabled(false);
            mAutoTextViewRelationTo.setEnabled(false);

            // associate the ids and full_names with the Person objects and set the variables
            int index_from = associateInfosWithPerson(relation.getFrom());
            int index_to = associateInfosWithPerson(relation.getTo());

            from_person = ManagePersonsActivity.array_persons.get(index_from);
            to_person = ManagePersonsActivity.array_persons.get(index_to);
        }
        else {
            if (to_person != null)
                setToPersonView();
            if (from_person != null)
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
                boolean success_write = false;

                if (valid_relation) {
                    if (new_relation) {
                        // ajout du champ application id
                        relation.setApplicationID(MainActivity.mConfiguration.getApplicationId());

                        // ajout du champ date
                        relation.setCreationDate();

                        ManageRelationsActivity.array_relations.add(relation);
                        success_write = FileUtils.saveRelationsToFile(getApplicationContext(), ManageRelationsActivity.formatterJsonFile());
                    }
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
     * @param string_p a String containing some informations about a person
     * @return int
     */
    public int associateInfosWithPerson(String string_p) {
        // aller chercher dans la liste ManagePersonActivity.array_persons la personne associée à ces deux éléments
        boolean found = false;
        int i= 0;
        int index_p = -1;
        while (i < ManagePersonsActivity.array_persons.size() && !found) {
            Person p = ManagePersonsActivity.array_persons.get(i);

            if (p.toString().equals(string_p)) {
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
     * Set the person from with a defined person given in parameters
     *
     * @param from_person an object of type Person
     */
    public static void setFromPerson(Person from_person) {
        AddRelationActivity.from_person = from_person;
    }

    /**
     * Set the person to with a defined person given in parameters
     *
     * @param to_person an object of type Person
     */
    public static void setToPerson(Person to_person) {
        AddRelationActivity.to_person = to_person;
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
        String toast_text = this.getString(R.string.toast_already_existing_relation);
        for(int i=0 ; i<ManageRelationsActivity.array_relations.size();i++){
            if (relation.isSameRelation(ManageRelationsActivity.array_relations.get(i))){
                Toast toast = Toast.makeText(this, toast_text, Toast.LENGTH_SHORT);
                toast.show();
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
        if (is_empty) {
            mAutoTextViewRelationFrom.setError(this.getString(R.string.toast_person_not_selected));
        }
        return !is_empty;
    }

    private boolean isPersonFromValid(Relation relation) {
        boolean is_empty = relation.getInfoByKey("from_unique_id").equals("");
        if (is_empty) {
            mAutoTextViewRelationFrom.setError(this.getString(R.string.toast_person_not_selected));
        }
        return !is_empty;
    }

    /**
     * Aggregates all the test to generate a relation
     * @return a boolean to know if the relation can be written
     */
    private boolean testValidRelation(Relation relation){
        try {
            Log.d("general-display", relation.toString(2));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return (testRelationType(relation) && isPersonFromValid(relation) && isPersonToValid(relation) && !samePersonRelation(relation) && !testExistingRelation(relation));
    }

    private void setToPersonView() {
        // informations contenues dans to_person
        mAutoTextViewRelationTo.setText(to_person.toString(), false);
        mAutoTextViewRelationTo.setEnabled(false);
    }

    private void setFromPersonView() {
        mAutoTextViewRelationFrom.setText(from_person.toString(), false);
        mAutoTextViewRelationFrom.setEnabled(false);
    }

    /**
     * Gets the informations of the relation that is being edited
     * Sets the Views of the activity so they contain the informations of the edited relation
     */
    private void setEditInformation(){
        mAutoTextViewRelationFrom.setText(relation.getFrom(), false);
        mAutoTextViewRelationTo.setText(relation.getTo(), false);

        String relation_key = relation.getInfoByKey("relation");
        int pos_in_adapter = getPositionInAdapter(spinner_adapter, relation_key);
        mSpinnerRelationType.setSelection(pos_in_adapter);

        mEditTextRelationComments.setText(relation.getInfoByKey("detail"));
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
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        String info_person_from = (String) savedInstanceState.getSerializable("from_person");
        String info_person_to = (String) savedInstanceState.getSerializable("to_person");
        String info_single_relat = (String) savedInstanceState.getSerializable("relation");

        from_person = new Gson().fromJson(info_person_from, Person.class);
        to_person = new Gson().fromJson(info_person_to, Person.class);
        relation = new Gson().fromJson(info_single_relat, Relation.class);

        new_relation = savedInstanceState.getBoolean("new_relation");
        rotation_screen = savedInstanceState.getBoolean("rotation_screen");
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        // ajouter les objets à sauvegarder
        Gson gson = new Gson();
        outState.putSerializable("from_person", gson.toJson(from_person));
        outState.putSerializable("to_person", gson.toJson(to_person));

        outState.putBoolean("new_relation", new_relation);
        outState.putBoolean("rotation_screen", true);
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