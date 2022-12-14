package netw4ppl.ines.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Iterator;

import netw4ppl.ines.ManagePersonsActivity;
import netw4ppl.ines.ManageRelationsActivity;
import netw4ppl.ines.R;

/**
 * A class to display the important informations about a relation.
 */
public class RelationListAdapter extends ArrayAdapter<Relation> {

    private Context mContext;
    private int mResource;
    private int lastPosition = -1;
    private ArrayList<Relation> mObjects;
    private ArrayList<Relation> mObjects_tmp;

    /**
     * A basic ViewHolder to put informations like the two fullnames and Unique IDs and the relation type
     */
    private static class ViewHolder {
        TextView mBestDescriptiveValueFrom;
        TextView mBestDescriptiveValueTo;
        TextView mRelationType;
        ImageView mSyncState;
    }

    /**
     * The class constructor.
     *
     * @param context the application context
     * @param resource the layout id
     * @param objects a ArrayList of Relation objects
     */
    public RelationListAdapter(Context context, int resource, ArrayList<Relation> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        mObjects = objects;
        mObjects_tmp = objects;
    }

    /**
     * Function to perform the filtering of the relations when the search bar is used. If you want to change
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
                ArrayList<Relation> tempList = new ArrayList<Relation>();

                if (constraint != null && mObjects != null) {
                    int length = mObjects.size();
                    int i=0;
                    boolean has_it;
                    String query = constraint.toString().toLowerCase();

                    while (i<length) {
                        Relation r = mObjects.get(i);
                        has_it = false;

                        // regarder unique id from
                        if (r.getFromID().toLowerCase().contains(query))
                            has_it = true;

                        // regarder le full_name from
                        if (r.getFromFullname().toLowerCase().contains(query))
                            has_it = true;

                        // regarder le full_name to
                        if (r.getToID().toLowerCase().contains(query))
                            has_it = true;

                        // regarder unique_id to
                        if (r.getToFullname().toLowerCase().contains(query))
                            has_it = true;

                        // regarder le type de relation
                        String relation_fullname = r.getRelation();
                        if (r.getRelation().toLowerCase().contains(query) || relation_fullname.toLowerCase().contains(query))
                            has_it = true;

                        if (has_it)
                            tempList.add(r);
                        i++;
                    }

                    filterResults.values = tempList;
                    filterResults.count = tempList.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mObjects_tmp = (ArrayList<Relation>) results.values;

                if (constraint.toString().equals("") || constraint == null) {
                    mObjects_tmp = mObjects;
                }
                notifyDataSetChanged();
            }
        };
    }

    /**
     * Notify the adapter if the content of the adapter changed. It is the case during the filtering.
     */
    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // get the relation informations
        Relation relation = getItem(position);

        final View result;
        RelationListAdapter.ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder = new RelationListAdapter.ViewHolder();
            holder.mBestDescriptiveValueFrom = (TextView) convertView.findViewById(R.id.best_descriptive_value_1);
            holder.mBestDescriptiveValueTo = (TextView) convertView.findViewById(R.id.best_descriptive_value_2);
            holder.mRelationType = (TextView) convertView.findViewById(R.id.relation_type);
            holder.mSyncState = (ImageView) convertView.findViewById(R.id.relation_sync_state);


            result = convertView;
            convertView.setTag(holder);
        }
        else {
            holder = (RelationListAdapter.ViewHolder) convertView.getTag();
            result = convertView;
        }

        if (lowContains(ManageRelationsActivity.array_relations_synced,relation)) {
            holder.mSyncState.setImageResource(R.drawable.icons8_cloud_check_48);
        } else {
            holder.mSyncState.setImageResource(R.drawable.icons8_cloud_48);
        }

        this.lastPosition = position;

        holder.mBestDescriptiveValueFrom.setText(relation.getFromBestDescriptiveValue());
        holder.mBestDescriptiveValueTo.setText(relation.getToBestDescriptiveValue());
        holder.mRelationType.setText(relation.getRelation());

        return convertView;
    }

    private boolean lowContains(ArrayList<Relation> array_relations, Relation relation) {
        //check in array_relations, if the from_id, the to_id and the relation type are the same
        for (Relation r : array_relations) {
            if (r.getFromID().equals(relation.getFromID()) && r.getToID().equals(relation.getToID()) && r.getRelation().equals(relation.getRelation())) {
                return true;
            }
        }
        return false;
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
    public Relation getItem(int position) {
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
