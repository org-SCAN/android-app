package netw4ppl.ines.utils;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import netw4ppl.ines.MainActivity;
import netw4ppl.ines.R;

public class AdapterViewFields extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private LayoutInflater mInflater;
    private Context mContext;
    private ArrayList<Field> mFields;

    class ViewHolderUniqueID extends RecyclerView.ViewHolder {
        com.google.android.material.textfield.TextInputLayout mTitle;
        com.google.android.material.textfield.TextInputEditText mValue;

        public ViewHolderUniqueID(@NonNull View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.key_id_title);
            mValue = itemView.findViewById(R.id.key_id_text);
        }
    }

    class ViewHolderAutoComplete  extends RecyclerView.ViewHolder {
        com.google.android.material.textfield.TextInputLayout mTitle;
        com.google.android.material.textfield.MaterialAutoCompleteTextView mAutoComplete;

        public ViewHolderAutoComplete(@NonNull View itemView, String key_table) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.autocomplete_title);
            mAutoComplete = itemView.findViewById(R.id.autocomplete_object);
            mAutoComplete.setAdapter(MainActivity.mConfiguration.getArrayAdapter(key_table));
        }
    }

    class ViewHolderSpinner  extends RecyclerView.ViewHolder {
        com.google.android.material.textfield.TextInputLayout mTitle;
        Spinner mSpinner;

        public ViewHolderSpinner(@NonNull View itemView, String key_table) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.spinner_title);
            mSpinner = itemView.findViewById(R.id.spinner_object);
            mSpinner.setAdapter(MainActivity.mConfiguration.getArrayAdapter(key_table));
        }
    }

    class ViewHolderCalendarView  extends RecyclerView.ViewHolder {
        com.google.android.material.textfield.TextInputLayout mTitle;
        DatePicker mDatePicker;

        public ViewHolderCalendarView(@NonNull View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.date_picker_title);
            mDatePicker = itemView.findViewById(R.id.date_picker_object);
        }
    }

    class ViewHolderEditText extends RecyclerView.ViewHolder {
        com.google.android.material.textfield.TextInputLayout mTitle;
        com.google.android.material.textfield.TextInputEditText mText;

        public ViewHolderEditText(@NonNull View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.textview_title);
            mText = itemView.findViewById(R.id.textview_object);
        }
    }

    public AdapterViewFields(Context context, ArrayList<Field> fields) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.mFields = fields;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case 0:
                view = mInflater.inflate(R.layout.view_holder_textview, parent, false);
                return new ViewHolderEditText(view);
            case 1:
                view = mInflater.inflate(R.layout.view_holder_spinner, parent, false);
                return new ViewHolderSpinner(view, "");
            case 2:
                break;
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        // changer le texte dans cette fonction

        // avec un peu de chance c'est la position dans l'array fields
        Field field = mFields.get(position);

        switch (field.getViewType()) {
            case 0:
                break;
            case 1:
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mFields.size();
    }
}
