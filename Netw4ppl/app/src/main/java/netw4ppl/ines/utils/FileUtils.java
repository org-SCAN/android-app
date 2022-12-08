package netw4ppl.ines.utils;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.UUID;

import netw4ppl.ines.R;

public class FileUtils {

    /**
     * Create an empty file
     *
     * @param path path for the two files
     * @return a boolean to determine whether the file was created successfully or not
     */
    public static boolean createFile(String path) {
        Log.d("general-display", "I need to create this file : " + path);
        File file = new File(path);
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file.exists();
    }

    /**
     * Create the two .json empty files : persons.json and links.json
     *
     * @param path path of the directory for the two files
     * @return a boolean to determine whether the files were created successfully or not
     */
    public static boolean createFiles(String path, String[] filenames) {
        boolean result_creation = true;

        // create the "file" object and makes it a directory
        File dir = new File(path);
        if (!dir.isDirectory())
            dir.mkdir();

        for (int i=0; i<filenames.length; i++) {
            result_creation = result_creation && createFile(path+"/"+filenames[i]);
        }

        return result_creation;
    }

    /**
     * Create an empty directory
     * @param path path of the directory to be created
     * @return a boolean to determine whether the directory was successfully created or not
     */
    public static boolean createDirectory(String path) {
        boolean result = true;
        File dir = new File(path);
        if (!dir.isDirectory())
            result = dir.mkdir();

        return result;
    }

    /**
     * Returns a boolean telling if a directory already exists or not
     * @param path path of the directory to be looked for
     * @return a boolean telling if the directory already exists or not
     */
    public static boolean directoryExists(String path) {
        File dir = new File(path);
        return dir.exists() && dir.isDirectory();
    }

    /**
     * Returns a boolean telling if a file already exists or not
     * @param path path of the file to be looked for
     * @return a boolean telling if the file already exists or not
     */
    public static boolean fileExists(String path) {
        File file = new File(path);
        return file.exists();
    }

    /**
     * Write content in file
     *
     * @param path path to file
     * @param content the content to be written in the file, must be of type String
     * @return return a boolean true if the writing was a success, false if not
     */
    public static boolean writeFile(String path, String content) {
        boolean writing_success = true;
        try (FileWriter file = new FileWriter(path)) {
            //We can write any JSONArray or JSONObject instance to the file
            file.write(content);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
            writing_success = false;
        }
        return writing_success;
    }

    /**
     * Read content of file
     *
     * @param path path to file
     * @return return a String of the content of the file
     */
    public static String readFile(String path) throws IOException {
        InputStream is = new FileInputStream(path);
        return readFileInputStream(is);
    }

    /**
     * Read content of file
     *
     * @param is inputstream of the file
     * @return return a String of the content of the file
     */
    public static String readFileInputStream(InputStream is) throws IOException {
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            is.close();
        }
        return writer.toString();
    }

    /**
     * Deletes a file
     *
     * @param path path to file
     * @return true if file was deleted
     */
    public static boolean deleteFile(String path) {
        File file = new File(path);
        return file.exists() && file.delete();
    }

    /**
     * Deletes a directory content
     *
     * @param directory_path path to the directory
     * @return true if the directory content was completely deleted
     */
    public static boolean clearDirectory(String directory_path) {
        boolean result = true;

        File file = new File(directory_path);
        if (file.isDirectory()) {
            File[] arr = file.listFiles();

            // if the directory is already empty
            if (arr == null) {
                Log.d("life-cycle", "The directory is already empty");
                return false;
            }

            // for each element in the directory
            for (File f : arr) {
                // if it's a directory, call the function on itself
                if (f.isDirectory())
                    clearDirectory(f.getPath());
                    // else perform the deletion
                else {
                    Log.d("life-cycle", "I need to delete this file: " + f.getName());
                    result &= deleteFile(f.getPath());
                }
            }
        }
        else {
            deleteFile(file.getPath());
        }

        return result;
    }

    /**
     * Load the file containing the IDs
     *
     * @param context the application context
     * @return return a string containing all the ids and the last value for the tricode
     */
    public static String loadIdsFromFile(Context context) throws IOException {
        String path_dir = context.getString(R.string.config_files);
        String filename_ids = context.getString(R.string.filename_ids);
        String file_path = context.getFilesDir() + path_dir + filename_ids;

        String file_content = "";
        if (FileUtils.fileExists(file_path)) {
            file_content = readFile(file_path);
        }
        else {
            // create the file
            boolean result_file = createFile(file_path);
        }

        return file_content;
    }

    /**
     * Load the application ID from a file, creates it if it doesn't already exists.
     *
     * @param context the application context
     * @return a string containing the application id
     */
    public static String loadApplicationIDFromFile(Context context) throws IOException {
        String data_path = context.getFilesDir().getPath();
        String dir_path = context.getString(R.string.config_files);
        String file_id = context.getString(R.string.unique_id_filename);

        //Get the unique application ID
        String android_id_file_path = data_path+dir_path+file_id;
        String unique_app_id;

        if (FileUtils.fileExists(android_id_file_path)){
            unique_app_id = FileUtils.readFile(android_id_file_path);
        }
        else{
            unique_app_id = UUID.randomUUID().toString();
            FileUtils.writeFile(android_id_file_path, unique_app_id);
        }

        return unique_app_id;
    }

    /**
     * Save the ids (the tricodes and their highest value) in a file
     *
     * @param context the application context
     * @param content a String of the content to write
     * @return a boolean, true if the save was possible, else return false
     */
    public static boolean saveIdsToFile(Context context, String content) {
        String dir_name = context.getString(R.string.config_files);
        String file_name = context.getString(R.string.filename_ids);
        String path_file = context.getFilesDir().getPath()+dir_name+file_name;
        return writeFile(path_file, content);
    }

    /**
     * Save the Persons in a file
     *
     * @param context the application context
     * @param content a String of the content to write
     * @return a boolean, true if the save was possible, else return false
     */
    public static boolean savePersonsToFile(Context context, String content) {
        String dir_name = context.getString(R.string.directory_files);
        String file_name = context.getString(R.string.filename_persons);
        String path_file = context.getFilesDir().getPath()+dir_name+file_name;
        return writeFile(path_file, content);
    }

    /**
     * Save the Relations in a file
     *
     * @param context the application context
     * @param content a String of the content to write
     * @return a boolean, true if the save was possible, else return false
     */
    public static boolean saveRelationsToFile(Context context, String content) {
        String dir_name = context.getString(R.string.directory_files);
        String file_name = context.getString(R.string.filename_relations);
        String path_file = context.getFilesDir().getPath()+dir_name+file_name;
        return writeFile(path_file, content);
    }

    /**
     * Reads the Persons file
     *
     * @param context the application context
     * @return a String
     */
    public static String readPersonsFile(Context context) throws IOException {
        String dir_name = context.getString(R.string.directory_files);
        String file_name = context.getString(R.string.filename_persons);
        String path_file = context.getFilesDir().getPath()+dir_name+file_name;
        return readFile(path_file);
    }

    /**
     * Reads the Relations file
     *
     * @param context the application context
     * @return a String
     */
    public static String readRelationsFile(Context context) throws IOException {
        String dir_name = context.getString(R.string.directory_files);
        String file_name = context.getString(R.string.filename_relations);
        String path_file = context.getFilesDir().getPath()+dir_name+file_name;
        return readFile(path_file);
    }

    /**
     *Loads the fields configuration file to store it in a JsonObject
     * @param context the context of the application
     * @return a JSonObject of the configuration file
     * @throws IOException
     */
    public static JSONObject loadConfigFromFile(Context context) throws IOException {
        // check if the file "fields.json" in "config/" directory exists
        String dir_name = context.getString(R.string.config_files);
        String file_name = context.getString(R.string.filename_fields);
        String path_dir = context.getFilesDir().getPath()+dir_name;
        String path_file = context.getFilesDir().getPath()+dir_name+file_name;

        File file = new File(path_file);

        String file_content = "";
        if (file.exists()) {
            // chargement du fichier depuis le dossier config/
            file_content = readFile(path_file);
        }
        else {
            InputStream is = context.getResources().openRawResource(context.getResources().getIdentifier("fields", "raw", context.getPackageName()));
            file_content = readFileInputStream(is);
            // créer le dossier config/
            boolean result_dir = createDirectory(path_dir);
            // ecriture du content dans le fichier config/fields.json
            boolean result_file = writeFile(path_file, file_content);
            // creation du fichier ids
            Log.d("display", result_dir && result_file ? "Création dossier config et création fichier successful" : "Echec creation fichier fields.json");
        }

        JSONObject config_content = null;
        try {
            config_content = new JSONObject(file_content);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return config_content;
    }

    /**
     * Reads the content of a file and returns it as a JSONObject
     */
    public static JSONArray readJSONFile(String path) throws IOException {
        String file_content = readFile(path);
        JSONArray content = null;
        try {
            content = new JSONArray(file_content);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return content;
    }
}
