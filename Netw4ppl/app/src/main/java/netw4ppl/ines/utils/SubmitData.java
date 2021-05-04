package netw4ppl.ines.utils;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.preference.PreferenceManager;

import org.json.JSONException;

import netw4ppl.ines.MainActivity;
import netw4ppl.ines.ManagePersonsActivity;
import netw4ppl.ines.R;
import netw4ppl.ines.SettingsActivity;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.File;
import java.io.IOException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SubmitData {

    /**
     * Manage the sending of the files. Read the settings options and determine whether
     * the selected option is by email or to the server.
     * Verify the validity of the settings
     *
     * @param context context of the activity
     * @param filePath path to the file
     */
    public static void manageSend(Context context, String filePath) throws IOException, InterruptedException, JSONException {

        // récupère l'option d'envoi sélectionnée par l'utilisateurs dans les paramètres
        String sending_option = PreferenceManager.getDefaultSharedPreferences(context).getString(context.getResources().getString(R.string.settings_sending_option_key), null);

        if (sending_option.equals("send_by_email")) {
            // REMARQUE : on ne peut pas certifier/vérifier que l'email a bien été envoyé malheureusement
            sendByEmail(context, new File(context.getFilesDir(), filePath).getPath());
        }
        else {
            // TODO si on supprime la valeur ça ne marche pas
            // si pas les infos du serveur, rediriger vers les settings
            String ip_port = PreferenceManager.getDefaultSharedPreferences(context).getString(context.getResources().getString(R.string.settings_server_ip_port_key), "");
            if (ip_port == "") {
                // Si aucune adresse renseignée => affiche une pop-up et propose de rediriger vers les settings
                new AlertDialog.Builder(context)
                        .setTitle(R.string.main_submit_ip_port_fail_title)
                        .setMessage(R.string.main_submit_ip_port_fail_msg)
                        .setCancelable(true)
                        .setPositiveButton(R.string.change_settings, (a,b) -> {
                            Intent intent = new Intent(context, SettingsActivity.class);
                            context.startActivity(intent);
                        })
                        .create()
                        .show();
                return;
            }

            String token_server = PreferenceManager.getDefaultSharedPreferences(context).getString(context.getResources().getString(R.string.settings_server_token_key), "");
            if (token_server == "") {
                new AlertDialog.Builder(context)
                        .setTitle(R.string.main_submit_token_fail_title)
                        .setMessage(R.string.main_submit_token_fail_msg)
                        .setCancelable(true)
                        .setPositiveButton(R.string.change_settings, (a,b) -> {
                            Intent intent = new Intent(context, SettingsActivity.class);
                            context.startActivity(intent);
                        })
                        .create()
                        .show();
                return;
            }

            OkHttpClient client = new OkHttpClient();

            boolean submit_result = sendToServer(context, new File(context.getFilesDir(), filePath).getPath(), ip_port, token_server, client); //Pour l'instant il fait la lecture
            showSubmitResultDialog(context, submit_result);
        }
    }

    /**
     * Function that makes the sending to an email address via an email application
     *
     * @param context context of the activity
     * @param filePath path to the file
     */
    public static void sendByEmail(Context context, String filePath) {
        Intent emailSelectorIntent = new Intent(Intent.ACTION_SENDTO);
        emailSelectorIntent.setData(Uri.parse("mailto:"));

        String target_address = PreferenceManager.getDefaultSharedPreferences(context).getString(context.getResources().getString(R.string.settings_email_default_key), null);

        // create the intent for the mail
        final Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{target_address});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, makeSubject(context.getResources(), filePath));
        emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        emailIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        emailIntent.setSelector(emailSelectorIntent);

        // get the files in the source directory
        ArrayList<Uri> attachments = new ArrayList<Uri>();
        File directory = new File(filePath);
        Log.d("general-display", "Le dossier contenant les pièces jointes : " + directory.getAbsolutePath());
        File[] arrFiles = directory.listFiles();

        // for every file present in the directory, add it in the attachments list
        if (arrFiles != null) {
            for (File f : arrFiles){
                Uri attachment = FileProvider.getUriForFile(context, MainActivity.class.getPackage().getName() + ".fileprovider", new File(String.valueOf(f)));
                attachments.add(attachment);
            }

            emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, attachments);
        }

        context.startActivity(emailIntent);
    }

    /**
     * Sends the informations to the server in form of a String read from the filePath given in
     * parameter
     *
     * @param context context of the activity
     * @param filePath path to the file
     * @param server_url ip address of the server
     * @param token_server individual token of the user
     * @param http_client http client of OkHttp
     * @return boolean which tells us if the sending was a success or not
     */
    public static boolean sendToServer(Context context, String filePath, String server_url, String token_server, OkHttpClient http_client) throws InterruptedException, JSONException {

        String unique_app_id = PersistentDatas.getAppUUID()+"HH";
        String data_to_send = ManagePersonsActivity.formatterJsonFile();

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, data_to_send);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try  {
                    Request request = new Request.Builder()
                            .url("http://"+server_url+"/api/manage_refugees")
                            .method("POST", body)
                            .addHeader("unique_app_id", unique_app_id)
                            .addHeader("Accept", "application/json")
                            .addHeader("Content-Type", "application/json")
                            .addHeader("Authorization", "Bearer "+token_server)
                            .build();
                    Response response = http_client.newCall(request).execute();
                    Log.d("GetResponse", response.body().string());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
        thread.join();

        return true;
        // TODO envoi au serveur
        // faire les requêtes HTTP
    }

    /**
     * Receive a String object from the server containing new configuration files (the config Json
     * for exemple)
     * A Json file is created with the received String and put at the filePath
     *
     * @param context context of the activity
     * @param filePath path to the file
     * @param server_url ip address of the server
     * @param token_server individual token of the user
     * @param http_client http client of OkHttp
     * @return boolean which tells us if the reception was a success or not
     */

    public static boolean getFromServer(Context context, String filePath, String server_url, String token_server, OkHttpClient http_client) throws IOException, InterruptedException {
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try  {
                    Request request = new Request.Builder()
                            .url("http://"+server_url+"/api/fields")
                            .method("GET", null)
                            .addHeader("Accept", "application/json")
                            .addHeader("Content-Type", "application/json")
                            .addHeader("Authorization", "Bearer "+token_server)
                            .build();
                    Response response = http_client.newCall(request).execute();
                    Log.d("GetResponse", response.body().string());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
        thread.join();

        return true;
    }

    /**
     * Create the subject for the email sending option. Composed of the name of the application
     * added with a '-' and the name of the file
     *
     * @param resources resources of the application
     * @param filePath path to the file
     * @return the subject for the email
     */
    public static String makeSubject(Resources resources, String filePath) {
        // return resources.getString(R.string.app_name) + "-" + new File(filePath).getName();
        return resources.getString(R.string.app_name) + "-" + "cases";
    }

    /**
     * Create a dialog window to display a message about the submitting status depending on if it
     * was a success or a failure.
     *
     * @param context resources of the application
     * @param result path to the file
     */
    public static void showSubmitResultDialog(Context context, boolean result) {
        new AlertDialog.Builder(context)
                .setTitle(result ? R.string.main_submit_success_title : R.string.main_submit_fail_title)
                .setMessage(result ? R.string.main_submit_success_msg : R.string.main_submit_fail_msg)
                .setCancelable(true)
                .setPositiveButton(android.R.string.ok, null)
                .create()
                .show();
    }
}
