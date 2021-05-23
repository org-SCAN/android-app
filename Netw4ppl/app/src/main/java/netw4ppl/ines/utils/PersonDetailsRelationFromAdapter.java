package netw4ppl.ines.utils;

import android.content.Context;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import netw4ppl.ines.DisplayDetailsPersonActivity;
import netw4ppl.ines.ManagePersonsActivity;
import netw4ppl.ines.R;

public class PersonDetailsRelationFromAdapter extends ArrayAdapter<Relation> {

    private final Context mContext;
    private final int mResource;

    private static class ViewHolder {
        TextView name_person;
        TextView relation_type;
    }

    public PersonDetailsRelationFromAdapter(Context context, int resource, ArrayList<Relation> fields) {
        super(context, resource, fields);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Relation relation = getItem(position);

        final View result;
        PersonDetailsRelationFromAdapter.ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder = new PersonDetailsRelationFromAdapter.ViewHolder();
            holder.name_person = (TextView) convertView.findViewById(R.id.details_person_relation_person);
            holder.relation_type = (TextView) convertView.findViewById(R.id.details_person_relation_type);

            result = convertView;
            convertView.setTag(holder);
        }
        else {
            holder = (PersonDetailsRelationFromAdapter.ViewHolder) convertView.getTag();
            result = convertView;
        }

        holder.name_person.setText(relation.getFrom());

        // associates the relation type with the full name
        String relation_key = relation.getRelationType();
        holder.relation_type.setText(relation_key);

        return convertView;
    }

    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }


}
