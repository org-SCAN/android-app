package netw4ppl.ines.utils;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.preference.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class SubmitData {

    /**
     * Manages the sending of the files. Reads the settings options and determine whether
     * the selected option is by email or to the server.
     * Verify the validity of the settings
     *
     * @param context context of the activity
     * @param filePath path to the file
     */
    public static void manageSend(Context context, String filePath) throws IOException, InterruptedException, JSONException {

        String dir_name = context.getString(R.string.directory_files);
        String file_name_relations = context.getString(R.string.filename_relations);
        String file_name_persons = context.getString(R.string.filename_persons);

        // gets the sending option selected by the user in the parameters
        String sending_option = PreferenceManager.getDefaultSharedPreferences(context).getString(context.getResources().getString(R.string.settings_sending_option_key), null);

        if (sending_option != null) {

            if (sending_option.equals("send_by_email")) {
                // REMARK : it is impossible to know whether the mail was successfully sent or not as it depends of the used mailing application
                sendByEmail(context, new File(context.getFilesDir(), filePath).getPath());
            } else {
                // TODO si on supprime la valeur ça ne marche pas
                // if the server informations are empty, redirect to the settings activity
                String ip_port = PreferenceManager.getDefaultSharedPreferences(context).getString(context.getResources().getString(R.string.settings_server_ip_port_key), "");
                if (ip_port.equals("")) {
                    // If no address was given => displays a pop-up and proposes to go to the settings activity
                    new AlertDialog.Builder(context)
                            .setTitle(R.string.main_submit_ip_port_fail_title)
                            .setMessage(R.string.main_submit_ip_port_fail_msg)
                            .setCancelable(true)
                            .setPositiveButton(R.string.change_settings, (a, b) -> {
                                Intent intent = new Intent(context, SettingsActivity.class);
                                context.startActivity(intent);
                            })
                            .create()
                            .show();
                    return;
                }

                String token_server = PreferenceManager.getDefaultSharedPreferences(context).getString(context.getResources().getString(R.string.settings_server_token_key), "");
                if (token_server.equals("")) {
                    new AlertDialog.Builder(context)
                            .setTitle(R.string.main_submit_token_fail_title)
                            .setMessage(R.string.main_submit_token_fail_msg)
                            .setCancelable(true)
                            .setPositiveButton(R.string.change_settings, (a, b) -> {
                                Intent intent = new Intent(context, SettingsActivity.class);
                                context.startActivity(intent);
                            })
                            .create()
                            .show();
                    return;
                }

                //Creation of an http client
                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(1, TimeUnit.SECONDS)
                        .build();

                //Submissions
                boolean submit_persons = sendToServer(context, context.getFilesDir().getPath() + dir_name + file_name_persons, ip_port, token_server, client, "manage_refugees");
                boolean submit_relations = sendToServer(context, context.getFilesDir().getPath() + dir_name + file_name_relations, ip_port, token_server, client, "links");
                boolean submit_result = (submit_persons && submit_relations);

                //Downloads
                boolean download_config = PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getResources().getString(R.string.settings_server_maj_auto_key), false);
                boolean get_success = false;

                if (download_config) {
                    get_success = getFromServer(context, ip_port, token_server, client);
                }
                // Log.d("Fichier", new File(context.getFilesDir(), filePath).getPath()+"HHH");
                showSubmitResultDialog(context, submit_result, get_success);

            }
        }else{
            new AlertDialog.Builder(context)
                    .setTitle(R.string.no_submit_option_title)
                    .setMessage(R.string.no_submit_option_msg)
                    .setCancelable(true)
                    .setPositiveButton(R.string.change_settings, (a, b) -> {
                        Intent intent = new Intent(context, SettingsActivity.class);
                        context.startActivity(intent);
                    })
                    .create()
                    .show();
            return;
        }
    }

    /**
     * Manages the downloading of files.
     *
     * @param context context of the activity
     * @throws IOException
     * @throws InterruptedException
     */
  public static void manageGet(Context context) throws IOException, InterruptedException {
      //gets and creates all the http objects needed
        String token_server = PreferenceManager.getDefaultSharedPreferences(context).getString(context.getResources().getString(R.string.settings_server_token_key), "");
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(100, TimeUnit.MILLISECONDS)
                .build();
        String ip_port = PreferenceManager.getDefaultSharedPreferences(context).getString(context.getResources().getString(R.string.settings_server_ip_port_key), "");

        String toast_text = "";

        if (token_server.equals("")){
            toast_text += context.getString(R.string.main_submit_token_fail_msg) + "\n";
        }else if (ip_port.equals("")){
            toast_text += context.getString(R.string.main_submit_ip_port_fail_msg);
        }

        if (toast_text.equals("")){
            boolean get_result = getFromServer(context,ip_port,token_server,client);
            toast_text = get_result ? context.getString(R.string.main_get_success_msg) : context.getString(R.string.main_get_fail_msg);
            Toast toast = Toast.makeText(context, toast_text, Toast.LENGTH_LONG);
            toast.show();
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
    public static boolean sendToServer(Context context, String filePath, String server_url, String token_server, OkHttpClient http_client, String target) throws InterruptedException, JSONException, IOException {

        String unique_app_id = MainActivity.mConfiguration.getApplicationId();

        String data_to_send = FileUtils.readFile(filePath);

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, data_to_send);

        final boolean[] http_success = {false};
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try  {
                    Request request = new Request.Builder()
                            .url(server_url+"/api/"+target)
                            .method("POST", body)
                            .addHeader("Application-id", unique_app_id)
                            .addHeader("Accept", "application/json")
                            .addHeader("Content-Type", "application/json")
                            .addHeader("Authorization", "Bearer "+token_server)
                            .build();
                    Response response = http_client.newCall(request).execute();
                    String response_string = response.body().string();
                    Log.d("Code de réception", String.valueOf(response.code()));
                    Log.d("GetResponse", response_string);

                    if (response.code()==201) {
                        http_success[0] = true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
        thread.join();

        return http_success[0];
    }

    /**
     * Receive a String object from the server containing new configuration files (the config Json
     * for exemple)
     * A Json file is created with the received String and put at the filePath
     *
     * @param context context of the activity
     * @param server_url ip address of the server
     * @param token_server individual token of the user
     * @param http_client http client of OkHttp
     * @return boolean which tells us if the reception was a success or not
     */

    public static boolean getFromServer(Context context, String server_url, String token_server, OkHttpClient http_client) throws IOException, InterruptedException {
        String data_path = context.getFilesDir().getPath();
        String dir_path = context.getString(R.string.config_files);
        String file_fields = context.getString(R.string.filename_fields);

        String unique_app_id = MainActivity.mConfiguration.getApplicationId();
        final boolean[] http_success = {false, false};
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try  {
                    Request request = new Request.Builder()
                            .url(server_url+"/api/fields")
                            .method("GET", null)
                            .addHeader("Accept", "application/json")
                            .addHeader("Content-Type", "application/json")
                            .addHeader("Application-id",unique_app_id)
                            .addHeader("Authorization", "Bearer "+token_server)
                            .build();
                    Response response = http_client.newCall(request).execute();
                    Log.d("Code de réception", String.valueOf(response.code()));
                    String response_string = response.body().string();
                    Log.d("GetResponse", response_string);

                    if (response.code()==200) {
                        http_success[0] = true;

                        // write the new configuration file
                        System.out.println(data_path+dir_path+file_fields);
                        http_success[1] = FileUtils.writeFile(data_path+dir_path+file_fields, response_string);

                        // read the new configuration file and set all the objects
                        JSONObject config_content = FileUtils.loadConfigFromFile(context);
                        MainActivity.mConfiguration = new Configuration(context, config_content);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
        thread.join();

        return http_success[0] && http_success[1];
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
     * @param submit_result boolean of the submit status
     * @param get_result boolean of the get status
     */
    public static void showSubmitResultDialog(Context context, boolean submit_result, boolean get_result) {
        String message_get = get_result ? context.getString(R.string.main_get_success_msg) : context.getString(R.string.main_get_fail_msg);
        String message_send =  submit_result ? context.getString(R.string.main_submit_success_msg) : context.getString(R.string.main_submit_fail_msg);
        new AlertDialog.Builder(context)
                .setTitle(submit_result ? R.string.main_submit_success_title : R.string.main_submit_fail_title)
                .setMessage(message_send + "\n" + message_get)
                .setCancelable(true)
                .setPositiveButton(android.R.string.ok, null)
                .create()
                .show();
    }
}
