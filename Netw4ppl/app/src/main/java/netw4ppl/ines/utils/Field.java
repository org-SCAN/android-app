package netw4ppl.ines.utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Field extends DataElement {

    private int view_type;

    public Field() {
        super();
    }

    public Field(String key, String string_fields) throws JSONException {
        super(key, string_fields);
        setViewType(this.getString("android_type"), this.getInt("required"));
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

    public int getViewType() {
        return this.view_type;
    }

    public void setViewType(String android_type, int required) {
        switch (android_type) {
            case "EditText":
                if (required == 0)
                    this.view_type = -1;
                else
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

    public int getRequired() {
        int required = 0;
        try {
            required = this.getInt("required");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return required;
    }

    /*
    Useful method when the field is of type CalendarView, that way we can initialise them with the right value
    using method setDate(long long_date)
     */
    public long getDateInMilliSeconds(String key) {
        long long_date = 1588433899000L;
        String string_date = this.getElementByKey(key);
        if (!isNull(string_date)) {
            // on essaie de parser la date en long
            SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date d = f.parse(string_date);
                long_date = d.getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return long_date;
    }
}
