package netw4ppl.ines.utils;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.UUID;

import netw4ppl.ines.R;

public class PersistentDatas {
    private static String appUUID;
    private static JSONArray references;

    public PersistentDatas() throws IOException, JSONException {

        appUUID=FileUtils.readFile("");
        if (appUUID.equals("")){
            appUUID = UUID.randomUUID().toString();
        }

        references=new JSONArray(FileUtils.readFile(""));
    }

    public static String getAppUUID() {
        return appUUID;
    }

    public static JSONArray getReferences() {
        return references;
    }
}
