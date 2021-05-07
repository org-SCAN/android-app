package netw4ppl.ines;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;

import java.util.ArrayList;

import netw4ppl.ines.utils.Person;
import netw4ppl.ines.utils.PersonDetailsListAdapter;

public class DisplayDetailsPersonActivity extends AppCompatActivity {

    TextView mTextViewFullnameTitle;
    Button mButtonAddRelationFrom;
    Button mButtonEditPerson;
    Button mButtonDeletePerson;
    Button mButtonAddRelationTo;

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

        PersonDetailsListAdapter adapter_details_person = new PersonDetailsListAdapter(this, R.layout.adapter_details_person_fields, MainActivity.mConfiguration.getArrayFields());
        mListDetailsPerson.setAdapter(adapter_details_person);

        mButtonAddRelationFrom.setOnClickListener(v -> {

        });
        mButtonEditPerson.setOnClickListener(v -> {
            AddPersonActivity.person = person;
            Intent intent = new Intent(DisplayDetailsPersonActivity.this, AddPersonActivity.class);
            startActivity(intent);
        });
        mButtonDeletePerson.setOnClickListener(v-> {

        });
        mButtonAddRelationTo.setOnClickListener(v -> {

        });
    }
}