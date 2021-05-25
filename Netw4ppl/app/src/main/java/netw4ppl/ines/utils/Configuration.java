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

public class Configuration {
    private ArrayList<Field> array_fields = new ArrayList<>();
    private HashMap<String, ArrayList<DataElement>> hashMap_database = new HashMap<>();
    private HashMap<String, ArrayAdapter> hashMap_adapters = new HashMap<>();
    private final HashMap<String, HashMap<String, DataElement>> hashMap_datatables = new HashMap<>();
    private final String application_id;

    public Configuration(Context context, JSONObject config_content) {
        createHashMaps(context, config_content);
        this.application_id = readApplicationIDFile(context);

        Log.d("general-settings", "Application id: " + this.getApplicationId());
    }

    public String getApplicationId() {
        return this.application_id;
    }

    private String readApplicationIDFile(Context context) {
        String file_content = "";
        try {
            file_content = FileUtils.loadApplicationIDFromFile(context);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file_content;
    }

    public HashMap<String, HashMap<String, DataElement>> getHashMap_datatables() {
        return hashMap_datatables;
    }

    public boolean hasElementInTable(String key_table, String key_element) {
        boolean has_it = false;
        if (this.hashMap_datatables.containsKey(key_table)) {
            if (this.hashMap_datatables.get(key_table).containsKey(key_element))
                has_it = true;
        }
        Log.d("display", "Element: " + key_element + " in table " + key_table + " exists");
        return has_it;
    }

    public String getElementFromTable(String key_table, String key_element) {
        String res = "";
        if (hasElementInTable(key_table, key_element))
            res = this.hashMap_datatables.get(key_table).get(key_element).getTitle();
        return res;
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
                    HashMap<String, DataElement> data_table = new HashMap<>();
                    data_array.add(new DataElement());
                    while (iterator_elements.hasNext()) {
                        String key_element = iterator_elements.next();
                        JSONObject element = table.getJSONObject(key_element);
                        DataElement data_elem = new DataElement(key_element, element.toString());
                        data_array.add(data_elem);
                        data_table.put(key_element, data_elem);
                    }
                    // then add everything to the HashMap
                    hashMap_database.put(key_table, data_array);
                    hashMap_datatables.put(key_table, data_table);

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
