package netw4ppl.ines;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import org.json.JSONException;

import netw4ppl.ines.utils.DataElement;
import netw4ppl.ines.utils.FileUtils;
import netw4ppl.ines.utils.Person;
import netw4ppl.ines.utils.PersonListAdapter;
import netw4ppl.ines.utils.Relation;

public class AddRelationActivity extends AppCompatActivity {

    private static final String TAG = "AddRelationActivity";
    public static Relation single_relation;
    public static boolean new_relation = true;

    public static Person from_person;
    public static Person to_person;
    private static String relation_type;
    private static String relation_details;

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

        // get the button views from the layout
        mButtonRelationSave = findViewById(R.id.display_add_relation_save);
        mButtonRelationCancel = findViewById(R.id.display_add_relation_cancel);
        mAutoTextViewRelationFrom = findViewById(R.id.add_relation_from);
        mAutoTextViewRelationTo = findViewById(R.id.add_relation_to);
        mSpinnerRelationType = findViewById(R.id.add_relation_type);
        mEditTextRelationComments = findViewById(R.id.add_relation_comments);

        // set the adapters for the different fields
        setAdapters();

        // complete the views with the edit relation informations
        if (!new_relation){
            setEditInformation();

            // désactiver les champs de saisie des personnes
            mAutoTextViewRelationFrom.setEnabled(false);
            mAutoTextViewRelationTo.setEnabled(false);

            // associate the ids and full_names with the Person objects and set the variables
            int index_from = associateInfosWithPerson(single_relation.getFrom());
            int index_to = associateInfosWithPerson(single_relation.getTo());

            from_person = ManagePersonsActivity.array_persons.get(index_from);
            to_person = ManagePersonsActivity.array_persons.get(index_to);
            relation_type = single_relation.getRelationType();
            relation_details = single_relation.getDetails();
        }
        else {
            if (to_person != null)
                setToPersonView();
            if (from_person != null)
                setFromPersonView();
        }

        //
        setListeners();

        mButtonRelationSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the details about the Relation
                relation_details = mEditTextRelationComments.getText().toString();

                //Get the relation_type key
                DataElement data_element = (DataElement) mSpinnerRelationType.getSelectedItem();
                relation_type = data_element.getKey();

                //Get the informations about the relation in the views
                boolean valid_relation = generateRelationFromInformations();
                boolean success_write = false;

                if (valid_relation) {
                    if (new_relation) {
                        if (single_relation != null && testExistingRelation(single_relation)) {
                            Log.d(TAG, "onClick: ecriture de la relation");

                            // ajout du champ application id
                            single_relation.setApplicationID(MainActivity.mConfiguration.getApplicationId());

                            // ajout du champ date
                            single_relation.setCreationDate();

                            ManageRelationsActivity.array_relations.add(single_relation);
                            success_write = FileUtils.saveRelationsToFile(getApplicationContext(), ManageRelationsActivity.formatterJsonFile());
                        }
                    }
                    else {
                        if (testExistingRelation(single_relation)) {
                            // ajout du champ date_update
                            single_relation.setUpdateDate();

                            int index_relation = DisplayDetailsRelationActivity.index_relation;
                            ManageRelationsActivity.array_relations.remove(index_relation);
                            ManageRelationsActivity.array_relations.add(index_relation, single_relation);
                            success_write = FileUtils.saveRelationsToFile(getApplicationContext(), ManageRelationsActivity.formatterJsonFile());
                        }
                    }

                    if (success_write) {
                        resetObjects();
                        finish();
                    }
                }
            }
        });

        mButtonRelationCancel.setOnClickListener(v -> {
            this.finish();
        });
    }

    public void resetObjects() {
        from_person = null;
        to_person = null;
        relation_type = "NA";
        relation_details = "";
        single_relation = null;
        new_relation = true;
    }

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
            }
        });

        mAutoTextViewRelationTo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                to_person = (Person) parent.getAdapter().getItem(position);
            }
        });
    }

    public static void setFromPerson(Person from_person) {
        AddRelationActivity.from_person = from_person;
    }

    public static void setToPerson(Person to_person) {
        AddRelationActivity.to_person = to_person;
    }

    private void setAdapters() {
        ArrayAdapter<Person> autocomplete_adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, ManagePersonsActivity.array_persons);
        mAutoTextViewRelationFrom.setAdapter(autocomplete_adapter);
        mAutoTextViewRelationTo.setAdapter(autocomplete_adapter);

        spinner_adapter = MainActivity.mConfiguration.getArrayAdapter("Relations");
        mSpinnerRelationType.setAdapter(spinner_adapter);
    }

    private boolean generateRelationFromInformations() {
        boolean result_generation = testValidRelation();
        boolean res_does_not_exists = false;
        if (result_generation){
            try {
                if (new_relation)
                    single_relation = new Relation(from_person, relation_type, to_person, relation_details);
                else {
                    single_relation.setRelationType(relation_type);
                    single_relation.setDetails(relation_details);
                }
                res_does_not_exists = testExistingRelation(single_relation);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return result_generation && res_does_not_exists;
    }

    /**
     * Checks if the two Persons given for the relation are different or not
     *
     * @param p1 , first person of the test
     * @param p2, second person of the test
     *
     * @return a boolean corresponding to the result of the test
     * Warning, it returns the opposite of the equal test
     * True if p1 and p2 are different
     */
    private boolean testSamePersonRelation(Person p1, Person p2){
        boolean test = (p1.equals(p2));
        String toast_text = this.getString(R.string.toast_same_person_relation);
        if (test){
            Toast toast = Toast.makeText(this, toast_text, Toast.LENGTH_SHORT);
            toast.show();
        }
        return (!test);
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
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if the given relation_type is a valid one
     * @param type selected type of relation
     * @return a boolean corresponding to the validity of the relation_type
     */
    private boolean testRelationType(String type){
        String toast_text = this.getString(R.string.toast_relation_type_non_selected);
        boolean test_relation_type = type.equals("NA");
        if (test_relation_type){
            Toast toast = Toast.makeText(this, toast_text, Toast.LENGTH_SHORT);
            toast.show();
        }
        return (!test_relation_type);
    }

    /**
     * Checks if the given person is a valid one
     * @param tested_person person to be tested
     * @return a boolean corresponding to the validity of the person
     * if the person was not initialized, returns false
     */
    private boolean testValidPerson(Person tested_person){
        if (tested_person == null){
            String toast_text = this.getString(R.string.toast_person_not_selected);
            Toast toast = Toast.makeText(this,toast_text, Toast.LENGTH_SHORT);
            toast.show();
        }
        return (tested_person != null);
    }

    /**
     * Aggregates all the test to generate a relation
     * @return a boolean to know if the relation can be written
     */
    private boolean testValidRelation(){
        return (testRelationType(relation_type) && testValidPerson(from_person) && testValidPerson(to_person) && testSamePersonRelation(from_person,to_person));
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
        mAutoTextViewRelationFrom.setText(single_relation.getFrom(), false);
        mAutoTextViewRelationTo.setText(single_relation.getTo(), false);

        String relation_key = single_relation.getInfoByKey("relation");
        int pos_in_adapter = getPositionInAdapter(spinner_adapter, relation_key);
        mSpinnerRelationType.setSelection(pos_in_adapter);

        mEditTextRelationComments.setText(single_relation.getInfoByKey("detail"));
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
}