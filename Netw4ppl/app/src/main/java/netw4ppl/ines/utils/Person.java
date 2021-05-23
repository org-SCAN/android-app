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

    public String getInfoByKey(String key) {
        String value = "";

        if (this.has(key)) {
            try {
                value = this.getString(key);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return value;
    }

    public void putInfo(String key, String value) {
        try {
            this.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public boolean isSamePerson(Person individu) throws JSONException {
        return this.getInfoByKey("unique_id").equals(individu.getInfoByKey("unique_id"));
    }

    @Override
    public String toString(){
        String res = null;
        res = this.getInfoByKey("unique_id") + " - " + this.getInfoByKey("full_name");
        return res;
    }

}
