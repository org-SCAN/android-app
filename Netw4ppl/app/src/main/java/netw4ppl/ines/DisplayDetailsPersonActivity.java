package netw4ppl.ines;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import netw4ppl.ines.utils.Person;

public class DisplayDetailsPersonActivity extends AppCompatActivity {

    TextView mTextViewFullnameTitle;
    Button mButtonAddRelationFrom;
    Button mButtonEditPerson;
    Button mButtonDeletePerson;
    Button mButtonAddRelationTo;

    ListView mListRelationsFrom;
    ListView mDetailsPerson;
    ListView mListRelationsTo;

    public static int index_person = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_details_person);

        mTextViewFullnameTitle = findViewById(R.id.details_title_full_name);

        if (index_person != -1) {
            Person person = ManagePersonsActivity.array_persons.get(index_person);

            mTextViewFullnameTitle.setText(person.getInfoByKey("full_name"));
        }

    }
}