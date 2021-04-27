package netw4ppl.ines;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import netw4ppl.ines.utils.Person;

public class ManagePersonsActivity extends AppCompatActivity {

    FloatingActionButton mButtonAdd;
    ArrayList<Person> array_persons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_persons);

        mButtonAdd = (FloatingActionButton) findViewById(R.id.add_person_fab);

        mButtonAdd.setOnClickListener(v -> {
            Intent intent = new Intent(ManagePersonsActivity.this, AddPersonActivity.class);
            startActivity(intent);
        });

        // lire le fichier files/cases/refugees.json

        // faire l'affichage
    }
}