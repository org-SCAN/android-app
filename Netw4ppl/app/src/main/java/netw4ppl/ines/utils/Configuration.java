package netw4ppl.ines.utils;

import android.content.Context;
import android.widget.ArrayAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class Configuration {
    private ArrayList<Field> array_fields = new ArrayList<>();
    private HashMap<String, ArrayList<DataElement>> hashMap_database = new HashMap<>();
    private HashMap<String, ArrayAdapter> hashMap_adapters = new HashMap<>();

    public Configuration(Context context, JSONObject config_content) {
        createHashMaps(context, config_content);
    }

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
                    while (iterator_elements.hasNext()) {
                        String key_element = iterator_elements.next();
                        JSONObject element = table.getJSONObject(key_element);
                        data_array.add(new DataElement(key_element, element.toString()));
                    }
                    // then add everything to the HashMap
                    hashMap_database.put(key_table, data_array);

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

    public void createArrayFields(JSONObject fields) {
        Iterator<String> iterator = fields.keys();
        while (iterator.hasNext()) {
            String key = iterator.next();
            try {
                array_fields.add(new Field(key, fields.getJSONObject(key).toString()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public ArrayList<Field> getArrayFields() {
        return this.array_fields;
    }

    public ArrayAdapter getArrayAdapter(String key) {
        return this.hashMap_adapters.get(key);
    }

    public ArrayList<DataElement> getTableDataElements(String key) {
        return this.hashMap_database.get(key);
    }

    public HashMap<String, ArrayList<DataElement>> getHashMapDatabase() {
        return this.hashMap_database;
    }

    public HashMap<String, ArrayAdapter> getHashMapAdapters() {
        return this.hashMap_adapters;
    }
}
