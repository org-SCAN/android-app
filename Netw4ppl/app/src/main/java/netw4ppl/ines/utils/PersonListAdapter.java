package netw4ppl.ines.utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.w3c.dom.Text;

import java.nio.channels.InterruptedByTimeoutException;
import java.util.ArrayList;

import netw4ppl.ines.ManagePersonsActivity;
import netw4ppl.ines.R;

public class PersonListAdapter extends ArrayAdapter<Person> {

    private Context mContext;
    private int mResource;
    private int lastPosition = -1;

    private static class ViewHolder {
        TextView mFullName;
        TextView mUniqueID;
        TextView mSex;
        TextView mAge;
    }

    public PersonListAdapter(Context context, int resource, ArrayList<Person> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // get the person informations
        Person person = getItem(position);
        String unique_id = person.getInfoByKey("unique_id");
        String fullname = person.getInfoByKey("full_name");
        String age = person.getInfoByKey("age");
        String sex = person.getInfoByKey("gender");

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
}
