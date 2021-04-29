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
     * Create the two .json empty files : refugees.json and links.json
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

    public static boolean createDirectory(String path) {
        boolean result = true;
        File dir = new File(path);
        if (!dir.isDirectory())
            result = dir.mkdir();

        return result;
    }

    public static boolean directoryExists(String path) {
        boolean res = true;
        File dir = new File(path);
        return dir.exists() && dir.isDirectory();
    }

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
        InputStream is = new FileInputStream(path); // !!Modify so we can give a real argument
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

    public static JSONObject loadConfigFromFile(Context context) throws IOException {
        // check if the file "fields.json" in "config/" directory exists
        String path_dir = context.getFilesDir()+"/config";
        String filename = "/fields.json";
        File file = new File(path_dir+filename);

        String file_content;
        if (file.exists()) {
            // chargement du fichier depuis le dossier config/
            file_content = readFile(path_dir+filename);
        }
        else {
            InputStream is = context.getResources().openRawResource(context.getResources().getIdentifier("fields", "raw", context.getPackageName()));
            file_content = readFileInputStream(is);
            // créer le dossier config/
            boolean result_dir = createDirectory(path_dir);
            // ecriture du content dans le fichier config/fields.json
            boolean result_file = writeFile(path_dir+filename, file_content);
            Log.d("display", result_dir && result_file ? "Création dossier config et création fichier successful" : "Echec creation fichier fields.json");
        }

        JSONObject fields = null;
        try {
            fields = new JSONObject(file_content);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return fields;
    }

}
