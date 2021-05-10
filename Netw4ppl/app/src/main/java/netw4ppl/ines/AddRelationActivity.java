package netw4ppl.ines;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONException;

import netw4ppl.ines.utils.Person;
import netw4ppl.ines.utils.PersonListAdapter;
import netw4ppl.ines.utils.Relation;

public class AddRelationActivity extends AppCompatActivity {

    private static final String TAG = "AddRelationActivity";
    public static Relation single_relation;

    TextView mTextViewRelationFrom;
    TextView mTextViewRelationType;
    TextView mTextViewRelationTo;
    TextView mTextViewRelationComment;

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

        mButtonRelationSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // récupère les infos saisies sur la personne
                getRelationInputsFromViews();

                ManageRelationsActivity.array_relations.add(single_relation);
                /*boolean save_result = FileUtils.saveJsonToFile(ManageRefugeesActivity.refugees, getApplicationContext().getFilesDir().toString()+"/cases/refugees.json");
                single_refugee = null;

                if (save_result) {
                    Log.d("general-display", "sauvegarde effectuée, retour vers l'écran de management");
                    finish();
                }*/
            }
        });
    }

    private void generateViews() {
        mAutoTextViewRelationFrom = findViewById(R.id.add_relation_from);
        ArrayAdapter<Person> adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, ManagePersonsActivity.array_persons);
        mAutoTextViewRelationFrom.setAdapter(adapter);
    }

    private void getRelationInputsFromViews() {

    }
}