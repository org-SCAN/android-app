package netw4ppl.ines.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Iterator;

import netw4ppl.ines.R;

public class RelationListAdapter extends ArrayAdapter<Relation> {

    private Context mContext;
    private int mResource;
    private int lastPosition = -1;
    private ArrayList<Relation> mObjects;
    private ArrayList<Relation> mObjects_tmp;

    private static class ViewHolder {
        TextView mFullName1;
        TextView mFullName2;
        TextView mUniqueID1;
        TextView mUniqueID2;
        TextView mRelationType;
    }

    public RelationListAdapter(Context context, int resource, ArrayList<Relation> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        mObjects = objects;
        mObjects_tmp = objects;
    }

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
                    String key;
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
                        String relation_fullname = r.getRelationTypeFull();
                        if (r.getRelationType().toLowerCase().contains(query) || relation_fullname.toLowerCase().contains(query))
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
            holder.mFullName1 = (TextView) convertView.findViewById(R.id.full_name_person_1);
            holder.mFullName2 = (TextView) convertView.findViewById(R.id.full_name_person_2);
            holder.mUniqueID1 = (TextView) convertView.findViewById(R.id.id_person_1);
            holder.mUniqueID2 = (TextView) convertView.findViewById(R.id.id_person_2);
            holder.mRelationType = (TextView) convertView.findViewById(R.id.relation_type);

            result = convertView;
            convertView.setTag(holder);
        }
        else {
            holder = (RelationListAdapter.ViewHolder) convertView.getTag();
            result = convertView;
        }

        this.lastPosition = position;

        holder.mFullName1.setText(relation.getFromFullname());
        holder.mFullName2.setText(relation.getToFullname());
        holder.mUniqueID1.setText(relation.getFromID());
        holder.mUniqueID2.setText(relation.getToID());
        holder.mRelationType.setText(relation.getRelationType());

        return convertView;
    }

    @Override
    public int getCount() {
        return mObjects_tmp.size();
    }

    @Override
    public Relation getItem(int position) {
        return mObjects_tmp.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
