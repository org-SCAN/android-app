package netw4ppl.ines;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class DisplayDetailsPersonActivity extends AppCompatActivity {

    TextView mTextViewFullnameTitle;
    Button mButtonAddRelationFrom;
    Button mButtonEditPerson;
    Button mButtonDeletePerson;
    Button mButtonAddRelationTo;

    ListView mListRelationsFrom;
    ListView mDetailsPerson;
    ListView mListRelationsTo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_details_person);



    }
}