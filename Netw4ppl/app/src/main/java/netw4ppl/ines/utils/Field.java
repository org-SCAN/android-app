package netw4ppl.ines.utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class Field extends JSONObject {

    private String key;

    public Field() {
        super();
    }

    public Field(String key, String string_fields) throws JSONException {
        super(string_fields);
        this.key = key;
    }

    public String getTitle() {
        String res = "";
        String language = Locale.getDefault().getISO3Language();
        try {
            if (this.getJSONObject("displayed_value").has(language))
                return this.getJSONObject("displayed_value").getString(language);
            else
                return this.getJSONObject("displayed_value").getString("eng");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return res;
    }

    public String getKey() {
        return this.key;
    }

    public String getElementByKey(String key) {
        if (this.has(key)) {
            try {
                return this.getString(key);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


}
