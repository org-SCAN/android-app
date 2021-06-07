package netw4ppl.ines.utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class DataElement extends JSONObject {

    private String key;

    /**
     * Default constructor of DataElement
     * Sets the key to Non-Attributed "NA"
     */
    public DataElement() {
        super();
        this.key = "NA";
    }

    /**
     * Constructor of DataElement
     * Sets the key and the value of a DataElement
     * @param key key of the DataElement
     * @param string_fields value of a DataElement
     * @throws JSONException
     */
    public DataElement (String key, String string_fields) throws JSONException {
        super(string_fields);
        this.key = key;
    }

    /**
     * Returns the name of a list associated to this DataElement if there is one
     * @return Name of the list associated to a DataElement
     */
    public String getLinkedList() {
        String res = "";
        if (this.has("linked_list")) {
            try {
                res = this.getString("linked_list");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return res;
    }

    /**
     * Returns the associated key of a DataElement
     * @return key
     */
    public String getKey() {
        return this.key;
    }

    /**
     * Returns the title of a DataElement in the preferential language of the device
     * @return the title of the DataElement
     */
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

    /**
     * Override of the toString class of JsonObject
     * @return the title of a DataElement using the getTitle() Class
     */
    @Override
    public String toString() {
        return getTitle();
    }
}
