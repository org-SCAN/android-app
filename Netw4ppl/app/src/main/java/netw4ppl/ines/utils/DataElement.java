package netw4ppl.ines.utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class DataElement extends JSONObject {

    private String key;

    public DataElement() {
        super();
        this.key = "NA";
    }

    public DataElement (String key, String string_fields) throws JSONException {
        super(string_fields);
        this.key = key;
    }

    public String getKey() {
        return this.key;
    }

    public String getTitle() {
        String res = "";
        String language = Locale.getDefault().getISO3Language();
        try {
            if (this.has("displayed_value")) {
                if (this.getJSONObject("displayed_value").has(language))
                    return this.getJSONObject("displayed_value").getString(language);
                else
                    return this.getJSONObject("displayed_value").getString("eng");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return res;
    }

    @Override
    public String toString() {
        return getKey() + " - " + getTitle();
    }
}
