package netw4ppl.ines.utils;

import android.content.Context;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONException;

import java.util.ArrayList;

import netw4ppl.ines.DisplayDetailsPersonActivity;
import netw4ppl.ines.MainActivity;
import netw4ppl.ines.ManagePersonsActivity;
import netw4ppl.ines.R;

public class PersonDetailsListAdapter extends ArrayAdapter<Field> {

    private final Context mContext;
    private final int mResource;
    private int index_person;

    /**
     * A simple ViewHolder containing a title (of a field) and the text associated to it in the person data
     *
     */
    private static class ViewHolder {
        com.google.android.material.textfield.TextInputLayout mTitle;
        com.google.android.material.textfield.TextInputEditText mText;
    }

    public PersonDetailsListAdapter(Context context, int resource, ArrayList<Field> fields, int index) {
        super(context, resource, fields);
        mContext = context;
        mResource = resource;
        index_person = index;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Log.d("general-display", "Beginning getView");
        // get the person informations
        Field field = getItem(position);
        Person mPerson = ManagePersonsActivity.array_persons.get(index_person);

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
        String key_table;
        String key_elem;
        String text = mPerson.getInfoByKey(field.getKey());

        if (!field.getLinkedList().equals("")) {
            key_table = field.getLinkedList();
            key_elem = mPerson.getInfoByKey(field.getKey());
            if (MainActivity.mConfiguration.hasElementInTable(key_table, key_elem))
                text = MainActivity.mConfiguration.getElementFromTable(key_table, key_elem);
        }

        holder.mText.setText(text);

        return convertView;
    }
}
