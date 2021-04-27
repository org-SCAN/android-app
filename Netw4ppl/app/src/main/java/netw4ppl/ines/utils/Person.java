package netw4ppl.ines.utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class Person {

    private JSONObject infos_person;

    public Person(){
        this.infos_person = new JSONObject();
    }

    public Person(String data_refugee) throws JSONException {
        this.infos_person = new JSONObject(data_refugee);
    }

    public Person(JSONObject data_refugee) throws JSONException {
        this.infos_person = new JSONObject(data_refugee.toString());
    }

    public String getInfoByKey(String key) {
        String value;
        try {
            value = infos_person.getString(key);
        }
        catch (JSONException e) {
            value = "";
        }
        return value;
    }

    public boolean samePerson(Person new_person) {
        Iterator<String> iterator = infos_person.keys();
        String key;
        while (iterator.hasNext()) {
            key = iterator.next();
            if (!getInfoByKey(key).equals(new_person.getInfoByKey(key)))  {
                return false;
            }
        }
        return true;
    }

    public String getElementSearched(String query) throws JSONException {
        Iterator<String> iterator = infos_person.keys();
        while (iterator.hasNext()) {
            String key = iterator.next();
            if (infos_person.getString(key).toLowerCase().contains(query.toLowerCase()))
                return this.infos_person.toString();
        }
        return null;
    }
}
