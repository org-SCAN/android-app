package netw4ppl.ines.utils;

import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

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

    /**
     * Create a file and initialize its content
     *
     * @param path path of the file
     * @param content content to be written in the file
     * @return a boolean to determine whether the file was created successfully or not
     */
    public static boolean createFileWithContent(String path, String content) {
        File file = new File(path);

        if (file.exists())
            file.delete();
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // ecriture du content dans le fichier
        writeFile(path, content);
        return file.exists();
    }

    /**
     * Write content in file
     *
     * @param path path to file
     * @param content the content to be written in the file, must be of type String
     * @return return a boolean true if teh writing was a success, false if not
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

}
