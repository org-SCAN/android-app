package netw4ppl.ines;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import netw4ppl.ines.utils.FileUtils;
import netw4ppl.ines.utils.SubmitData;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

public class MainActivity extends AppCompatActivity{

    Button mManagePersonsBtn;
    Button mManageRelationsBtn;
    Button mSettingsBtn;
    Button mSendDataBtn;

    public static String device_language;

    public static boolean mLancementApplication = true;

    static boolean FLAG_EMAIL_SUCCESS;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        device_language=Locale.getDefault().getISO3Language();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("life-cycle", "Main onCreate()");

        // get the button views from the xml file
        mManagePersonsBtn = findViewById(R.id.main_activity_manage_persons_btn);
        mManageRelationsBtn = findViewById(R.id.main_activity_manage_relations_btn);
        mSettingsBtn = findViewById(R.id.main_activity_settings_btn);
        mSendDataBtn = findViewById(R.id.main_activity_send_data_btn);

        // add listeners to every buttons
        mManagePersonsBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ManagePersonsActivity.class);
            startActivity(intent);
        });
        mManageRelationsBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ManageRelationsActivity.class);
            startActivity(intent);
        });
        mSendDataBtn.setOnClickListener(v -> {
            Log.d("Send Data","ON EST LA");
            try {
                SubmitData.manageSend(this, "/cases/");
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
        mSettingsBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

        // si c'est le lancement de l'application, chargement en mémoire des json de configuration
        if (mLancementApplication) {
            // lecture du json de config
            try {
                JSONObject fields = FileUtils.loadConfigFromFile(this);
            } catch (IOException e) {
                e.printStackTrace();
            }

            mLancementApplication = false;
        }
    }

    @Override
    protected void onStart () {
        super.onStart();
        Log.d("life-cycle","Main onStart()");
    }

    @Override
    protected void onStop () {
        super.onStop();
        Log.d("life-cycle", "Main onStop()");
    }

    @Override
    protected void onResume () {
        super.onResume();
        Log.d("life-cycle", "Main onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("life-cycle", "Main onPause()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("life-cycle", "Main onDestroy()");
    }
}
