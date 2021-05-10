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

    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_person);

        mRecyclerView = findViewById(R.id.recycler_view_add_person);

        // set up the RecyclerView
        AdapterViewFields adapter = new AdapterViewFields(this, MainActivity.mConfiguration.getArrayFields(), person);
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
        person = new Person();
    }
}