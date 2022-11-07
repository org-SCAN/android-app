package netw4ppl.ines.utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Field extends DataElement {

    private int view_type;
    public static String[] hidden_values = {"date", "application_id", "date_update"};

    /**
     * Default Constructor
     */
    public Field() {
        super();
    }

    /**
     * Constructor of Fields
     * Sets the key and value of a field
     * Sets the type of user interface of this field according to its android-type and requirement level
     * @param key key of a Field
     * @param string_fields value of a Field
     */
    public Field(String key, String string_fields) throws JSONException {
        super(key, string_fields);
        setViewType(this.getString("android_type"), this.getInt("required"));
    }

    /**
     * Returns the value associated to a key
     * @param key key of the searched value
     * @return null if the key does not exist, the element if it does
     */
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

    /**
     * Returns the view_type of this Field
     * @return the value of view_type
     */
    public int getViewType() {
        return this.view_type;
    }

    /**
     * Sets the type of user interface of a field according to its android-type and requirement level
     * Allows to create four types of UI : EditText, AutoCompleteTextView, Spinner and CalendarView
     * @param android_type type of user interface
     * @param required level of requirement
     */
    public void setViewType(String android_type, int required) {
        switch (android_type) {
            case "EditText":
                if (required == 0)
                    this.view_type = -1;
                else
                    this.view_type = 0;
                break;
            case "number":
                this.view_type = 0;
                break;
            case "Spinner":
                this.view_type = 1;
                break;
            case "AutoCompleteTextView":
                this.view_type = 2;
                break;
            case "CalendarView":
                this.view_type = 3;
                break;
            default:
                this.view_type = 4;
        }
    }

    /**
     * Returns the level of requirement of a Field
     * @return an int corresponding to the requirement level
     */
    public int getRequired() {
        int required = 0;
        try {
            required = this.getInt("required");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return required;
    }

    /**
     * Checks if the field has "best_descriptive_value" set to 1
     * @return true if the field has "best_descriptive_value" set to 1, false otherwise
     */
    public boolean isBestDescriptiveValue() {
        String is_best_descriptive_value;
        is_best_descriptive_value = this.getElementByKey("best_descriptive_value");
        return is_best_descriptive_value.equals("1");
    }

    /**
     * Checks if the field has "descriptive_value" set to 1
     * @return true if the field has "descriptive_value" set to 1, false otherwise
     */
    public boolean isDescriptiveValue() {
        String is_descriptive_value;
        is_descriptive_value = this.getElementByKey("descriptive_value");
        return is_descriptive_value.equals("1");
    }
}
