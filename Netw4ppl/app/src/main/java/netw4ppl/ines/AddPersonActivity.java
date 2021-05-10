package netw4ppl.ines;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import org.json.JSONException;
import org.json.JSONObject;

import netw4ppl.ines.utils.AdapterViewFields;
import netw4ppl.ines.utils.Person;

public class AddPersonActivity extends AppCompatActivity {

    public static JSONObject json_ids = new JSONObject();
    public static Person person = new Person();
    public static boolean new_person = true;

    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_person);

        mRecyclerView = findViewById(R.id.recycler_view_add_person);

        // on regarde si on vient de DetailsPersonActivity ou pas
        if (new_person) {
            person = new Person();
        }

        // set up the RecyclerView
        AdapterViewFields adapter = new AdapterViewFields(this, MainActivity.mConfiguration.getArrayFields());
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public static String getDefaultKey() {
        return "AAA";
    }

    public static int getNextId(String key) {
        int last_id = 0;
        if (json_ids.has(key))
            try {
                last_id = json_ids.getInt(key);
            } catch (JSONException e) {
                e.printStackTrace();
                last_id = 0;
            }
        return last_id+1;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        new_person = true;
    }
}