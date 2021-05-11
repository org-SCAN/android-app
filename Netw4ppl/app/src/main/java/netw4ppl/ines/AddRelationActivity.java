package netw4ppl.ines;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONException;

import netw4ppl.ines.utils.DataElement;
import netw4ppl.ines.utils.FileUtils;
import netw4ppl.ines.utils.Person;
import netw4ppl.ines.utils.PersonListAdapter;
import netw4ppl.ines.utils.Relation;

public class AddRelationActivity extends AppCompatActivity {

    private static final String TAG = "AddRelationActivity";
    public static Relation single_relation;

    private static Person from_person;
    private static Person to_person;
    private static String relation_type;
    private static String relation_details;

    AutoCompleteTextView mAutoTextViewRelationFrom;
    AutoCompleteTextView mAutoTextViewRelationTo;

    Spinner mSpinnerRelationType;

    EditText mEditTextRelationComments;

    Button mButtonRelationSave;
    Button mButtonRelationCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_relation);
        mButtonRelationSave = findViewById(R.id.display_add_relation_save);
        mButtonRelationCancel = findViewById(R.id.display_add_relation_cancel);

        generateViews();
        setListeners();

        mButtonRelationSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the details about the Relation
                relation_details = mEditTextRelationComments.getText().toString();

                relation_type = mSpinnerRelationType.getSelectedItem().toString();
                // récupère les infos saisies sur la relation
                try {
                    generateRelationFromInformations();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                ManageRelationsActivity.array_relations.add(single_relation);
                writeRelationToFile(getApplicationContext());
            }
        });
    }

    private void setListeners(){

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

    private void generateViews() {
        mAutoTextViewRelationFrom = findViewById(R.id.add_relation_from);
        mAutoTextViewRelationTo = findViewById(R.id.add_relation_to);

        ArrayAdapter<Person> autocomplete_adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, ManagePersonsActivity.array_persons);

        mAutoTextViewRelationFrom.setAdapter(autocomplete_adapter);
        mAutoTextViewRelationTo.setAdapter(autocomplete_adapter);

        mSpinnerRelationType = findViewById(R.id.add_relation_type);
        ArrayAdapter<DataElement> spinner_adapter = MainActivity.mConfiguration.getArrayAdapter("Relations");
        mSpinnerRelationType.setAdapter(spinner_adapter);


        mEditTextRelationComments = findViewById(R.id.add_relation_comments);
    }

    private void generateRelationFromInformations() throws JSONException {
        single_relation = new Relation(from_person,relation_type,to_person,String.valueOf(System.currentTimeMillis()), relation_details);
    }

    private void writeRelationToFile(Context context){
        String dir_name = context.getString(R.string.directory_files);
        String file_name = context.getString(R.string.filename_relations);
        String path_file = context.getFilesDir().getPath()+dir_name+file_name;
        boolean save_result = FileUtils.writeFile(path_file,ManageRelationsActivity.array_relations.toString());
        //single_refugee = null;

        if (save_result) {
            Log.d("general-display", "sauvegarde effectuée, retour vers l'écran de management");
            finish();
        }
    }
}