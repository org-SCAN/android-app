package netw4ppl.ines.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import netw4ppl.ines.MainActivity;
import netw4ppl.ines.ManagePersonsActivity;
import netw4ppl.ines.R;

/**
 * A class to display the important informations about the persons
 */
public class PersonListAdapter extends ArrayAdapter<Person> implements Filterable {

    private Context mContext;
    private int mResource;
    private int lastPosition = -1;
    public ArrayList<Person> mObjects;
    public ArrayList<Person> mObjects_tmp;
    public static HashMap<Person, ArrayList<String>> usedFields = new HashMap<>();

    /**
     * A basic ViewHolder to put the BestDescriptiveValue of the Person
     */
    private static class ViewHolder {
        TextView mDescriptiveField2Title;
        TextView mDescriptiveField1Title;
        TextView mDescriptiveField2;
        TextView mDescriptiveField1;
        TextView mBestDescriptiveValue;
        ImageView mSyncState;
    }

    /**
     * The class constructor
     *
     * @param context  the application context
     * @param resource the id of the layout to use to pt the informations
     * @param objects  an ArrayList of Person objects
     */
    public PersonListAdapter(Context context, int resource, ArrayList<Person> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        mObjects = objects;
        mObjects_tmp = objects;
        for (Person p : mObjects) {
            //check if usedFields contains the person
            if (!usedFields.containsKey(p)) {
                usedFields.put(p, new ArrayList<String>());
            }
        }
    }

    /**
     * Function to perform the filtering of the persons when the search bar is used. If you want to change
     * the search criterias, you should modify the code of the <b>performFiltering(CharSequence constraint)</b>
     * function.
     *
     * @return a Filter object
     */
    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                ArrayList<Person> tempList = new ArrayList<Person>();

                // constraints is the query
                // objects is the dataset
                if (constraint != null && mObjects != null) {
                    int length = mObjects.size();
                    int i = 0;
                    while (i < length) {
                        Person p = mObjects.get(i);

                        // do the comparison
                        Iterator<String> keys = p.keys();
                        boolean has_it = false;
                        String key_field;
                        String key_table = "";

                        // on regarde toutes les valeurs des clés en s'arrêtant seulement si :
                        // plus de clé à regarder ou si on a trouvé ce qu'on cherchait
                        while (keys.hasNext() && !has_it) {

                            key_field = keys.next();
                            String value_field = String.valueOf(p.getInfoByKey(key_field));

                            // si la linked_list associée au field est définie on va regarder dedans
                            Field f = MainActivity.mConfiguration.getFieldFromHashMap(key_field);
                            if (f != null) {
                                if (!f.getLinkedList().equals("")) {
                                    String linkedListValue = f.getLinkedListValue(p);
                                    if (linkedListValue != null) {
                                        value_field = linkedListValue;
                                    } else {
                                        value_field = "";
                                    }
                                }
                            }

                            if (value_field.toLowerCase().contains(constraint.toString().toLowerCase()))
                                has_it = true;

                        }

                        if (has_it) {
                            tempList.add(p);
                        }
                        i++;
                    }

                    filterResults.values = tempList;
                    filterResults.count = tempList.size();
                }
                cleanUsedFields();
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mObjects_tmp = (ArrayList<Person>) results.values;
                // si on fait la recherche avec une chaine vide (aucun texte saisi)
                if (constraint.toString().equals("") || constraint == null) {
                    // réinitialiser la liste de l'adapter
                    mObjects_tmp = mObjects;
                }
                update();
            }
        };
    }

    public void cleanUsedFields() {
        usedFields.clear();
        for (Person p : mObjects) {
            //check if usedFields contains the person
            if (!usedFields.containsKey(p)) {
                usedFields.put(p, new ArrayList<String>());
            }
        }
    }

    public void update() {
        resetArrayPersons(mObjects);
        notifyDataSetChanged();
    }

    public static void resetArrayPersons(ArrayList<Person> mObjects) {
        ArrayList<Person> tmp = new ArrayList<>(ManagePersonsActivity.hashmap_persons.values());
        ManagePersonsActivity.array_persons.clear();
        ManagePersonsActivity.array_persons.addAll(tmp);
        for (Person p : mObjects) {
            //check if usedFields contains the person
            if (!usedFields.containsKey(p)) {
                usedFields.put(p, new ArrayList<String>());
            }
        }
    }

    /**
     * Notify the adapter if the content of the adapter changed. It is the case during the filtering.
     */
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // get the person informations
        Person person = mObjects_tmp.get(position);

        final View result;
        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder = new ViewHolder();
            holder.mBestDescriptiveValue = (TextView) convertView.findViewById(R.id.best_descriptive_value);
            holder.mDescriptiveField1 = (TextView) convertView.findViewById(R.id.descriptive_field_1);
            holder.mDescriptiveField1Title = (TextView) convertView.findViewById(R.id.descriptive_field_1_title);
            holder.mDescriptiveField2 = (TextView) convertView.findViewById(R.id.descriptive_field_2);
            holder.mDescriptiveField2Title = (TextView) convertView.findViewById(R.id.descriptive_field_2_title);
            holder.mSyncState = (ImageView) convertView.findViewById(R.id.sync_state);

            result = convertView;
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        if (ManagePersonsActivity.array_persons_synced.contains(person)) {
            holder.mSyncState.setImageResource(R.drawable.icons8_cloud_check_48);
        } else {
            holder.mSyncState.setImageResource(R.drawable.icons8_cloud_48);
        }

        this.lastPosition = position;

        Iterator<String> keys = person.keys();
        while(keys.hasNext()) {
            String linkedListValue = null;

            String key = keys.next();
            // This ugly while loop (and if statement) are used to ignore the fields hidden values,
            // as editing the persons's field values mess up the field's orders
            while (Arrays.asList(Field.hidden_values).contains(key)) {
                if (!keys.hasNext()) {
                    break;
                }
                key = keys.next();
            }
            if (Arrays.asList(Field.hidden_values).contains(key)) {
                break;
            }
            Field field = MainActivity.mConfiguration.getFieldFromHashMap(key);
            if (field == null) {
                break;
            }
            if (usedFields.containsKey(person)) {
                if (!usedFields.get(person).contains(key)) {
                    if (!field.isBestDescriptiveValue()) {
                        if (field.isDescriptiveValue()) {
                            if (holder.mDescriptiveField1Title.getText().equals("")) {
                                holder.mDescriptiveField1Title.setText(field + " : ");
                                if (!field.getLinkedList().equals("")) {
                                    linkedListValue = field.getLinkedListValue(person);
                                }
                                if (linkedListValue != null) {
                                    holder.mDescriptiveField1.setText(linkedListValue);
                                } else {
                                    holder.mDescriptiveField1.setText(String.valueOf(person.getInfoByKey(key)));
                                }
                            } else {
                                holder.mDescriptiveField2Title.setText(field + " : ");
                                if (!field.getLinkedList().equals("")) {
                                    linkedListValue = field.getLinkedListValue(person);
                                }
                                if (linkedListValue != null) {
                                    holder.mDescriptiveField2.setText(linkedListValue);
                                } else {
                                    holder.mDescriptiveField2.setText(String.valueOf(person.getInfoByKey(key)));
                                }
                            }
                        }
                    } else if (field.isBestDescriptiveValue()) {
                        holder.mBestDescriptiveValue.setText(String.valueOf(person.getInfoByKey(key)));
                    }
                    usedFields.get(person).add(key);
                }
            }
        }
        cleanUsedFields();
        return result;
    }


    /**
     * Get the number of elements in the adapter.
     *
     * @return an int
     */
    @Override
    public int getCount() {
        return mObjects_tmp.size();
    }

    /**
     * Get the item in the dapter at the position given
     *
     * @param position an int
     * @return a Person
     */
    @Override
    public Person getItem(int position) {
        return mObjects_tmp.get(position);
    }

    /**
     * Get the item id (aka its position in the array)
     *
     * @param position an int
     * @return a long
     */
    @Override
    public long getItemId(int position) {
        return position;
    }
}
