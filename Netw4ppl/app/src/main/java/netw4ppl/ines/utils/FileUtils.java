package netw4ppl.ines.utils;

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

}
