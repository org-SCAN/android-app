package netw4ppl.ines.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Iterator;

import netw4ppl.ines.MainActivity;
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

    /**
     * A basic ViewHolder to put informations like the fullname, the unique ID, the sex and the age of a person.
     */
    private static class ViewHolder {
        TextView mFullName;
        TextView mUniqueID;
        TextView mSex;
        TextView mAge;
    }

    /**
     * The class constructor
     * @param context the application context
     * @param resource the id of the layout to use to pt the informations
     * @param objects an ArrayList of Person objects
     */
    public PersonListAdapter(Context context, int resource, ArrayList<Person> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        mObjects = objects;
        mObjects_tmp = objects;
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
                    int i=0;
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
                            if (f!=null){
                                if (!f.getLinkedList().equals("")) {
                                    key_table = f.getLinkedList();
                                    value_field = MainActivity.mConfiguration.getElementFromTable(key_table, value_field);
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
                notifyDataSetChanged();
            }
        };
    }

    /**
     * Notify the adapter if the content of the adapter changed. It is the case during the filtering.
     */
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

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
            holder.mAge = (TextView) convertView.findViewById(R.id.age_person);
            holder.mFullName = (TextView) convertView.findViewById(R.id.full_name_person);
            holder.mSex = (TextView) convertView.findViewById(R.id.sex_person);
            holder.mUniqueID = (TextView) convertView.findViewById(R.id.id_person);

            result = convertView;
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        this.lastPosition = position;

        holder.mUniqueID.setText(person.getInfoByKey("unique_id"));
        holder.mSex.setText(person.getInfoByKey("gender"));
        holder.mFullName.setText(person.getInfoByKey("full_name"));
        holder.mAge.setText(person.getInfoByKey("age"));

        return convertView;
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
