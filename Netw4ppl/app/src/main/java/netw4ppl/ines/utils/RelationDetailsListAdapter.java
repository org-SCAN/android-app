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
import netw4ppl.ines.DisplayDetailsRelationActivity;
import netw4ppl.ines.ManagePersonsActivity;
import netw4ppl.ines.ManageRelationsActivity;
import netw4ppl.ines.R;

public class RelationDetailsListAdapter extends ArrayAdapter<Field> {

    private final Context mContext;
    private final int mResource;

    private static class ViewHolder {
        com.google.android.material.textfield.TextInputLayout mTitle;
        com.google.android.material.textfield.TextInputEditText mText;
    }

    public RelationDetailsListAdapter(Context context, int resource, ArrayList<Field> fields) {
        super(context, resource, fields);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Log.d("general-display", "Beginning getView");
        // get the person informations
        Field field = getItem(position);
        //Relation mRelation = ManageRelationsActivity.array_relations.get(DisplayDetailsRelationActivity.index_relation);

        final View result;
        RelationDetailsListAdapter.ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder = new RelationDetailsListAdapter.ViewHolder();
            holder.mTitle = (com.google.android.material.textfield.TextInputLayout) convertView.findViewById(R.id.adapter_details_title);
            holder.mText = (com.google.android.material.textfield.TextInputEditText) convertView.findViewById(R.id.adapter_details_text);

            holder.mText.setEnabled(false);
            holder.mText.setInputType(InputType.TYPE_NULL);

            result = convertView;
            convertView.setTag(holder);
        }
        else {
            holder = (RelationDetailsListAdapter.ViewHolder) convertView.getTag();
            result = convertView;
        }

        holder.mTitle.setHint(field.getTitle());
        //holder.mText.setText(mRelation.getInfoByKey(field.getKey()));

        return convertView;
    }
}
