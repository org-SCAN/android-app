package netw4ppl.ines;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import java.net.URL;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceGroup;
import androidx.preference.SwitchPreference;

import netw4ppl.ines.utils.FileUtils;
import netw4ppl.ines.utils.SubmitData;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Locale;
import java.util.regex.Pattern;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        Log.d("life-cycle", "Settings onCreate()");

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("life-cycle", "Settings onStart()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("life-cycle", "Settings onStop()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("life-cycle", "Settings onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("life-cycle", "Settings onPause()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("life-cycle", "Settings onDestroy()");
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            // get all the preferences in the file
            final Resources res = getResources();

            // make that every EditTextPreference is a single line and not a multilines field
            Log.d("auto-single-line", getPreferenceManager().getPreferenceScreen().getPreferenceCount() + "");
            EditTextPreference.OnBindEditTextListener noMultilineAllowed = el -> {
                el.setSingleLine(true);
                el.setSelection(el.getText().length());
            };
            // do it recursively to avoid to have to do it by hand for every PreferenceGroup
            recursiveSetSingleLine(getPreferenceScreen(), noMultilineAllowed);

            // change some parameters about the token field
            EditTextPreference token_edit = findPreference(res.getString(R.string.settings_server_token_key));
            token_edit.setOnBindEditTextListener(el -> {
                // useless to show the previous token so just write an empty string
                el.setText("");
            });
            token_edit.setOnPreferenceChangeListener((e, v) -> {
                if (v == null)
                    return false;
                // if the user did not change the value then don't update the value in memory
                if (v.toString().equals(""))
                    return false;

                return true;
            });

            EditTextPreference input_ip_port = findPreference(res.getString(R.string.settings_server_ip_port_key));
            input_ip_port.setOnPreferenceChangeListener((e,v) -> {
                if (v == null) return false;
                // check if the address is an URL
                Boolean isURL = isValid(v.toString());
                if (!isURL) {
                    displayToast(this.getContext(), res.getString(R.string.url_not_valid));
                    return false;
                }
                return true;
            });

            SwitchPreference switch_auto_update = findPreference(res.getString(R.string.settings_server_maj_auto_key));
            switch_auto_update.setOnPreferenceChangeListener((e,v) -> {
                return true;
            });


//            // perform some magic with the password fields
//            EditTextPreference password_email = findPreference(res.getString(R.string.settings_email_password_key));
//            password_email.setOnBindEditTextListener(el -> {
//                // set the input type to a password field
//                el.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
//                // set the cursor at the end of the field
//                el.setSelection(el.getText().length());
//                // because the password is hashed, it is impossible to modify it by hand so clear the field
//                el.setText("");
//            });
//            password_email.setOnPreferenceChangeListener((e, v) -> {
//                if (v == null) return false;
//                // hash the password written
//                new PasswordHashingAsyncTask(SettingsFragment.this.getContext()).execute();
//                return false;
//            });

            // add a listener to the delete file
            findPreference(res.getString(R.string.settings_delete_button_key)).setOnPreferenceClickListener(e -> {
                new AlertDialog.Builder(SettingsFragment.this.getContext())
                        .setTitle(R.string.delete_files_title)
                        .setMessage(R.string.delete_files_message)
                        .setCancelable(true)
                        .setPositiveButton(R.string.yes, (a,b) -> {
                            // delete the files
                            displayToast(this.getContext(), res.getString(R.string.toast_delete_processing));
                            // delete the files
                            boolean result_deletion = FileUtils.clearDirectory(SettingsFragment.this.getContext().getFilesDir() + "/cases/");
                            // create the new empty ones
                            String[] filenames = {"persons.json", "relations.json"};
                            boolean result_creation = FileUtils.createFiles(SettingsFragment.this.getContext().getFilesDir().toString()+"/cases/", filenames);
                            ManagePersonsActivity.hashmap_persons.clear();
                            ManagePersonsActivity.updateAdapter();
                            ManageRelationsActivity.array_relations.clear();
                            ManageRelationsActivity.updateAdapter();
                            displayToast(this.getContext(), result_deletion ? res.getString(R.string.toast_delete_success) : res.getString(R.string.toast_delete_fail));
                        })
                        .setNegativeButton(R.string.button_relation_cancel_title, (a,b) -> {
                            // no op
                        })
                        .create()
                        .show();
                return true;
            });

            findPreference(res.getString(R.string.settings_get_button_key)).setOnPreferenceClickListener(e -> {
                displayToast(this.getContext(), res.getString(R.string.toast_get_processing));
                MainActivity.initiateData(this.getContext());
                return true;
            });
        }
    }

    /**
     * Recursive function to set all the inputs on a single line.
     *
     * @param screen a PreferenceGroup object
     * @param noMultilineAllowed
     */
    public static void recursiveSetSingleLine(PreferenceGroup screen, EditTextPreference.OnBindEditTextListener noMultilineAllowed) {
        // for every Preference in the PreferenceGroup
        for (int i = 0; i < screen.getPreferenceCount(); i++) {
            Preference preference = screen.getPreference(i);

            // if it is a PreferenceCategory, call the function again
            if (preference instanceof PreferenceCategory)
                recursiveSetSingleLine((PreferenceCategory) preference, noMultilineAllowed);
                // if it is of type EditTextPreference, just don't allow the multiline
            else if (preference instanceof EditTextPreference)
                ((EditTextPreference) preference).setOnBindEditTextListener(noMultilineAllowed);
        }
    }

    /**
     * Function to display a toast based on a context and a text
     *
     * @param contextA the application context
     * @param textToast the text to be displayed in the toast
     */
    public static void displayToast(Context contextA, CharSequence textToast) {
        Context context = contextA;
        CharSequence text = textToast;
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    /**
     * Check if an String url is valid
     * @param url
     * @return a boolean
     */
    public static boolean isValid(String url)
    {
        try {
            new URL(url).toURI();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}