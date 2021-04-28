package netw4ppl.ines.utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class Person extends JSONObject {
    public Person(){
        super();
    }

    public Person(String data_person) throws JSONException {
        super(data_person);
    }

    public Person(JSONObject data_person) throws JSONException {
        super(data_person.toString());
    }

    public String getInfoByKey(String key) throws JSONException {
        String value;

        if (this.has(key))
            value = this.getString(key);
        else
            value = "";
        return value;
    }
}
