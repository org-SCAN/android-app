package netw4ppl.ines.utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class Field extends DataElement {

    private String key;

    public Field() {
        super();
    }

    public Field(String key, String string_fields) throws JSONException {
        super(key, string_fields);
        this.key = key;
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
