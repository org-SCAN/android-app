package netw4ppl.ines.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import netw4ppl.ines.MainActivity;
import netw4ppl.ines.ManagePersonsActivity;
import netw4ppl.ines.R;

/**
 * Class to create a ListAdapter adapted to display the details of a person.
 */
public class PersonDetailsListAdapter extends ArrayAdapter<Field> {

    private final Context mContext;
    private final int mResource;
    private final String id_person;

    /**
     * A simple ViewHolder containing a title (of a field) and the text associated to it in the person data
     */
    private static class ViewHolder {
        com.google.android.material.textfield.TextInputLayout mTitle;
        com.google.android.material.textfield.TextInputEditText mText;
    }

    /**
     * Constructor of the class
     * @param context the application context
     * @param resource
     * @param fields an ArrayList of Field objects
     * @param index an int corresponding to the index of the person in the ManagePersonsActivity.array_persons object
     */
    public PersonDetailsListAdapter(Context context, int resource, ArrayList<Field> fields, String index) {
        super(context, resource, fields);
        mContext = context;
        mResource = resource;
        id_person = index;
    }


    /**
     * Get the views to display the details of a person and set this values of the Labels.
     *
     * @param position the field index
     * @param convertView a View
     * @param parent a ViewGroup
     * @return
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Log.d("general-display", "Beginning getView");
        // get the person informations
        Field field = getItem(position);
        Person mPerson = ManagePersonsActivity.hashmap_persons.get(id_person);

        final View result;
        PersonDetailsListAdapter.ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder = new PersonDetailsListAdapter.ViewHolder();
            holder.mTitle = (com.google.android.material.textfield.TextInputLayout) convertView.findViewById(R.id.adapter_details_title);
            holder.mText = (com.google.android.material.textfield.TextInputEditText) convertView.findViewById(R.id.adapter_details_text);

            holder.mText.setEnabled(false);
            holder.mText.setInputType(InputType.TYPE_NULL);

            result = convertView;
            convertView.setTag(holder);
        }
        else {
            holder = (PersonDetailsListAdapter.ViewHolder) convertView.getTag();
            result = convertView;
        }

        holder.mTitle.setHint(field.getTitle());

        // associer la clé à la bonne valeur
        String linkedListValue = null;
        String text = mPerson.getInfoByKey(field.getKey());

        if (!field.getLinkedList().equals("")) {
            linkedListValue = field.getLinkedListValue(mPerson);
        }

        if (linkedListValue != null) {
            holder.mText.setText(linkedListValue);
        }
        else {
            holder.mText.setText(text);
        }

        holder.mText.setTextColor(0xff000000);

        return result;
    }
}
