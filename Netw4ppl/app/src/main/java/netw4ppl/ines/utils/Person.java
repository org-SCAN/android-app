package netw4ppl.ines.utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class Person extends JSONObject {

    /**
     * Default constructor
     * Creates an empty Person
     */
    public Person(){
        super();
    }

    /**
     * Constructor of Person
     * Sets the Person using a String
     *
     * @param data_person String containing the data of a person in a Json syntax
     * @throws JSONException
     */
    public Person(String data_person) throws JSONException {
        super(data_person);
    }

    /**
     * Constructor of Person
     * Sets the Person using a JSONObject
     *
     * @param data_person JSONObject containing the data of a person
     * @throws JSONException
     */
    public Person(JSONObject data_person) throws JSONException {
        super(data_person.toString());
    }

    /**
     * Returns the value of a Person associated to a key
     *
     * @param key key of the query
     * @return a String corresponding to the value associated to the key
     */
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

    /**
     * Adds an information with the key:value format to the Person
     *
     * @param key key of the information
     * @param value value of the information
     */
    public void putInfo(String key, String value) {
        try {
            this.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Tests if two persons are the same comparing their unique_id
     *
     * @param individu Person to be compared with this Person
     * @return a boolean telling wether or not the Person is the same, true if yes, false if not
     * @throws JSONException
     */
    public boolean isSamePerson(Person individu) throws JSONException {
        return this.getInfoByKey("unique_id").equals(individu.getInfoByKey("unique_id"));
    }

    /**
     * Override of the toString method
     *
     * Returns a String containing the unique_id and the full_name of a person
     * @return a String of the form "unique_id - full_name" of the person
     */
    @Override
    public String toString(){
        String res = null;
        res = this.getInfoByKey("unique_id") + " - " + this.getInfoByKey("full_name");
        return res;
    }

}
