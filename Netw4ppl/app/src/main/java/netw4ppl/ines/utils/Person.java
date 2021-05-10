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

    public boolean isSamePerson(Person individu) throws JSONException {
        return this.getInfoByKey("unique_id").equals(individu.getInfoByKey("unique_id"));
    }

    public String toString(){
        String res = null;
        try {
            res = this.getInfoByKey("full_name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return res;
    }

}
