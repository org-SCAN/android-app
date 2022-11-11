package netw4ppl.ines.utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import netw4ppl.ines.MainActivity;
import netw4ppl.ines.ManagePersonsActivity;

public class Person extends JSONObject {

    public String bestDescriptiveValueKey;
    public String[] descriptiveFieldsKeys;

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
        bestDescriptiveValueKey = MainActivity.mConfiguration.getBestDescriptiveKey();
        descriptiveFieldsKeys = MainActivity.mConfiguration.getDescriptiveKeys();
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
        bestDescriptiveValueKey = MainActivity.mConfiguration.getBestDescriptiveKey();
        descriptiveFieldsKeys = MainActivity.mConfiguration.getDescriptiveKeys();
    }

    /**
     * Get all the fields of the Person
     * @return an ArrayList of Field objects
     */
    public static ArrayList<Field> getFields() {
        return MainActivity.mConfiguration.getArrayFields();
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
     * Returns the value of the key having this person in the hashmap of persons
     */
    public String getKey() {
        String key = "";
        for (String key_temp : ManagePersonsActivity.hashmap_persons.keySet()) {
            if (Objects.equals(ManagePersonsActivity.hashmap_persons.get(key_temp), this)) {
                key = key_temp;
                break;
            }
        }
        return key;
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
     * Overrides the toString method
     */
    @Override
    public String toString() {
        return this.getInfoByKey(this.bestDescriptiveValueKey);
    }
}
