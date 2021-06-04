package netw4ppl.ines.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import netw4ppl.ines.R;

/**
 * Class to create an ArrayAdapter of Relation objects. This class is currently used in the DisplayDetailsPersonActivity
 * to show the "relations to".
 */
public class PersonDetailsRelationToAdapter  extends ArrayAdapter<Relation> {
    private final Context mContext;
    private final int mResource;

    /**
     * A basic viewholder to put a person's name and a relation type
     */
    private static class ViewHolder {
        TextView name_person;
        TextView relation_type;
    }

    /**
     * The class constructor
     * @param context the application context
     * @param resource an int representing the id of the layout to put the elements inside
     * @param relations an ArrayList of Relation objects
     */
    public PersonDetailsRelationToAdapter(Context context, int resource, ArrayList<Relation> relations) {
        super(context, resource, relations);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Relation relation = getItem(position);

        final View result;
        PersonDetailsRelationToAdapter.ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder = new PersonDetailsRelationToAdapter.ViewHolder();
            holder.name_person = (TextView) convertView.findViewById(R.id.details_person_relation_person);
            holder.relation_type = (TextView) convertView.findViewById(R.id.details_person_relation_type);

            result = convertView;
            convertView.setTag(holder);
        }
        else {
            holder = (PersonDetailsRelationToAdapter.ViewHolder) convertView.getTag();
            result = convertView;
        }

        holder.name_person.setText(relation.getTo());

        // associates the relation type with the full name
        String relation_key = relation.getRelationType();
        holder.relation_type.setText(relation_key);

        return convertView;
    }

    /**
     * Function to notify the adapter if elements were added, deleted or modified.
     */
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
}
