package netw4ppl.ines.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONException;

import java.util.ArrayList;

import netw4ppl.ines.R;

public class RelationListAdapter extends ArrayAdapter<Relation> {

    private Context mContext;
    private int mResource;
    private int lastPosition = -1;

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

        holder.mFullName1.setText(relation.getInfoByKey("from_full_name"));
        holder.mFullName2.setText(relation.getInfoByKey("to_full_name"));
        holder.mUniqueID1.setText(relation.getInfoByKey("from_unique_id"));
        holder.mUniqueID2.setText(relation.getInfoByKey("to_unique_id"));
        holder.mRelationType.setText(relation.getInfoByKey("relation"));

        return convertView;
    }
}
