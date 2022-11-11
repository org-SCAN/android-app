package netw4ppl.ines.utils;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;

public class Configuration {
    private ArrayList<Field> array_fields = new ArrayList<>();
    private HashMap<String, Field> hashMap_fields = new HashMap<>();
    private HashMap<String, ArrayList<DataElement>> hashMap_database = new HashMap<>();
    private HashMap<String, ArrayAdapter> hashMap_adapters = new HashMap<>();
    private final HashMap<String, HashMap<String, DataElement>> hashMap_datatables = new HashMap<>();
    private final String application_id;

    public Configuration(Context context, JSONObject config_content) {
        createHashMaps(context, config_content);
        this.application_id = readApplicationIDFile(context);

        Log.d("general-settings", "Application id: " + this.getApplicationId());
    }

    /**
     * Function to get the application ID from a Configuration object.
     *
     * @return a String
     */
    public String getApplicationId() {
        return this.application_id;
    }

    /**
     * Read the application id from the specified Application ID file.
     * This function is calling the function loadApplicationIDFromFile from the package FileUtils
     * defined by us.
     *
     * @param context the applicaiton context
     * @return a String containing the content of the file given for the application ID. If no content or
     * an error has occured, return an empty string.
     */
    private String readApplicationIDFile(Context context) {
        String file_content = "";
        try {
            file_content = FileUtils.loadApplicationIDFromFile(context);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file_content;
    }

    /**
     * Get the hashmaps identified by a String and containing DataElement objects.
     * It is similar to a database. For example, we can get the tables like "Relations", "country", "role" etc.
     *
     * @return a HashMap containing HashMaps identified by Strings.
     */
    public HashMap<String, HashMap<String, DataElement>> getHashMap_datatables() {
        return hashMap_datatables;
    }

    /**
     * Function to determine if given a specific table key (string) the given string for the key element
     * is in the HashMap or not.
     *
     * @param key_table a String identifying a "table". Example : "country".
     * @param key_element a String identifyin an element. Example : "FRA" standing for "France".
     *
     * @return boolean. True if the element identified by key_element is in HashMap identified by key_table.
     */
    public boolean hasElementInTable(String key_table, String key_element) {
        boolean has_it = false;
        if (this.hashMap_datatables.containsKey(key_table)) {
            if (this.hashMap_datatables.get(key_table).containsKey(key_element))
                has_it = true;
        }
        return has_it;
    }

    /**
     * Get a Field object from the HashMap of fields if the field identified by the parameter given exists.
     *
     * @param key_field a String representing
     * @return an object of type Field if it exists or null.
     */
    public Field getFieldFromHashMap(String key_field) {
        if (this.hashMap_fields.containsKey(key_field)) {
            return this.hashMap_fields.get(key_field);
        }
        return null;
    }

    /**
     * Get an element from our "internal database".
     *
     * @param key_table a String representing the key value of the HashMap in the more global HashMap (ex: "country")
     * @param key_element a String representing the key value of a DataElement object in the hasmap (ex: "FRA")
     *
     * @return a String containing the name of the element in the displayed language or an empty string if the element was not found.
     */
    public String getElementFromTable(String key_table, String key_element) {
        String res = "";
        if (hasElementInTable(key_table, key_element))
            res = this.hashMap_datatables.get(key_table).get(key_element).getTitle();
        return res;
    }

    /**
     * Function used to create not only hashmaps actually but also different objects. It used to
     * create the different ArrayList of DataElement, the associated HashMaps and the associated Adapters.
     *
     * @param context an object of type Context representing the context of the application
     * @param config_content a JSONObject containing the configuration for the application.
     */
    public void createHashMaps(Context context, JSONObject config_content) {
        // récupère toutes les clés de l'objet json config_content
        Iterator<String> iterator_table = config_content.keys();
        while (iterator_table.hasNext()) {
            String key_table = iterator_table.next();
            try {
                JSONObject table = config_content.getJSONObject(key_table);
                // récupère toutes les clés de l'objet iterator_table
                Iterator<String> iterator_elements = table.keys();

                if (!key_table.equals("fields")) {
                    // create the ArrayList
                    ArrayList<DataElement> data_array = new ArrayList<DataElement>();
                    // create a new empty HashMap (like a database table)
                    HashMap<String, DataElement> data_table = new HashMap<>();
                    data_array.add(new DataElement());
                    while (iterator_elements.hasNext()) {
                        String key_element = iterator_elements.next();
                        JSONObject element = table.getJSONObject(key_element);
                        DataElement data_elem = new DataElement(key_element, element.toString());

                        // add the element to the array
                        data_array.add(data_elem);

                        // add the element to the hashmap. it's like an insert into a table in a database.
                        data_table.put(key_element, data_elem);
                    }
                    // then add everything to the more global HashMap
                    hashMap_database.put(key_table, data_array);
                    hashMap_datatables.put(key_table, data_table);

                    // create the adapters from the array just created
                    ArrayAdapter adapter = new ArrayAdapter(context, android.R.layout.simple_spinner_item, data_array);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    hashMap_adapters.put(key_table, adapter);
                }
                else {
                    createArrayFields(table);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Function used to create the ArrayList of the fields present in the AddPersonActivity.
     *
     * @param fields a JSONObject of the fields.
     */
    public void createArrayFields(JSONObject fields) {
        // create an iterator to go through all the keys in the field object
        Iterator<String> iterator = fields.keys();

        // for each field in the JSONObject
        while (iterator.hasNext()) {
            String key = iterator.next();
            try {
                Field f = new Field(key, fields.getJSONObject(key).toString());
                array_fields.add(f);
                this.hashMap_fields.put(key, f);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Function to get the ArrayList containing all the fields.
     *
     * @return an ArrayList of Field objects.
     */
    public ArrayList<Field> getArrayFields() {
        return this.array_fields;
    }

    /**
     * Function to get an ArrayAdapter identified by a specific key.
     *
     * @param key a String representing the identifier for the ArrayAdapter
     * @return an ArrayAdapter object if the HashMap contains the key or return null.
     */
    public ArrayAdapter getArrayAdapter(String key) {
        if (this.hashMap_adapters.containsKey(key))
            return this.hashMap_adapters.get(key);
        return null;
    }

    /**
     *
     */
    public HashMap<String, ArrayList<DataElement>> getHashMapDatabase() {
        return this.hashMap_database;
    }

    /**
     * Get the key of the field that have "best_descriptive_value" set to "1" in the hashmap of fields
     */
    public String getBestDescriptiveKey() {
        String res = "";
        for (String key : this.hashMap_fields.keySet()) {
            if (Objects.requireNonNull(this.hashMap_fields.get(key)).isBestDescriptiveValue()) {
                res = key;
                break;
            }
        }
        return res;
    }

    /**
     * Get the key of the fields that have "descriptive_value" set to "1" in the hashmap of fields
     */
    public String[] getDescriptiveKeys() {
        ArrayList<String> res = new ArrayList<>();
        for (String key : this.hashMap_fields.keySet()) {
            if (Objects.requireNonNull(this.hashMap_fields.get(key)).isDescriptiveValue()) {
                res.add(key);
            }
        }
        return res.toArray(new String[0]);
    }
}
