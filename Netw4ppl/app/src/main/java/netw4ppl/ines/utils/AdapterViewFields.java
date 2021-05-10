package netw4ppl.ines.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;

import java.util.ArrayList;

import netw4ppl.ines.AddPersonActivity;
import netw4ppl.ines.MainActivity;
import netw4ppl.ines.R;

public class AdapterViewFields extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Field> mFields;
    private Person person;
    private boolean new_person;

    class ViewHolderUniqueID extends RecyclerView.ViewHolder {
        TextView mTitleLetters;
        TextView mTitleFigures;
        EditText mValueLetters;
        EditText mValueFigures;
        CustomUniqueIDTextListener customUniqueIDTextListener;

        public ViewHolderUniqueID(@NonNull View itemView, CustomUniqueIDTextListener myCustomTextListener) {
            super(itemView);
            customUniqueIDTextListener = myCustomTextListener;
            mTitleLetters = itemView.findViewById(R.id.unique_id_title_letters);
            mTitleFigures = itemView.findViewById(R.id.unique_id_title_figures);
            mValueLetters = itemView.findViewById(R.id.unique_id_text_letters);
            mValueFigures = itemView.findViewById(R.id.unique_id_text_figures);
            mValueLetters.addTextChangedListener(customUniqueIDTextListener);
        }
    }

    class ViewHolderAutoComplete  extends RecyclerView.ViewHolder {
        TextView mTitle;
        AutoCompleteTextView mAutoComplete;

        public ViewHolderAutoComplete(@NonNull View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.autocomplete_title);
            mAutoComplete = itemView.findViewById(R.id.autocomplete_object);
        }
    }

    class ViewHolderSpinner  extends RecyclerView.ViewHolder {
        TextView mTitle;
        Spinner mSpinner;

        public ViewHolderSpinner(@NonNull View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.spinner_title);
            mSpinner = itemView.findViewById(R.id.spinner_object);
        }
    }

    class ViewHolderCalendarView  extends RecyclerView.ViewHolder {
        TextView mTitle;
        DatePicker mDatePicker;

        public ViewHolderCalendarView(@NonNull View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.date_picker_title);
            mDatePicker = itemView.findViewById(R.id.date_picker_object);
        }
    }

    class ViewHolderEditText extends RecyclerView.ViewHolder {
        TextView mTitle;
        EditText mText;
        MyCustomEditTextListener myCustomEditTextListener;

        public ViewHolderEditText(@NonNull View itemView, MyCustomEditTextListener customEditTextListener) {
            super(itemView);
            this.myCustomEditTextListener = customEditTextListener;
            mTitle = itemView.findViewById(R.id.textview_title);
            mText = itemView.findViewById(R.id.textview_object);
            mText.addTextChangedListener(this.myCustomEditTextListener);

            // configure everything no multiline allowed etc
        }
    }

    public AdapterViewFields(Context context, ArrayList<Field> fields) {
        this.mFields = fields;
        this.person = new Person();
        this.new_person = true;
    }

    public AdapterViewFields(Context context, ArrayList<Field> fields, Person person) {
        this.mFields = fields;
        this.person = person;
        this.new_person = false;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater mInflater = LayoutInflater.from(parent.getContext());

        View view;
        switch (viewType) {
            case -1:
                view = mInflater.inflate(R.layout.view_holder_unique_id, parent, false);
                return new ViewHolderUniqueID(view, new CustomUniqueIDTextListener());
            case 0:
                view = mInflater.inflate(R.layout.view_holder_textview, parent, false);
                return new ViewHolderEditText(view, new MyCustomEditTextListener());
            case 1:
                view = mInflater.inflate(R.layout.view_holder_spinner, parent, false);
                return new ViewHolderSpinner(view);
            case 2:
                view = mInflater.inflate(R.layout.view_holder_autocomplete, parent, false);
                return new ViewHolderAutoComplete(view);
            case 3:
                view = mInflater.inflate(R.layout.view_holder_calendar, parent, false);
                return new ViewHolderCalendarView(view);
        }
        return null;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        // position va de 0 à 19, car actuellement on 20 champs à saisir

        Field field = mFields.get(position);

        switch (field.getViewType()) {
            case -1:
                ((ViewHolderUniqueID) holder).mTitleFigures.setText(field.getTitle());
                ((ViewHolderUniqueID) holder).mTitleLetters.setText(field.getTitle());
                ((ViewHolderUniqueID) holder).customUniqueIDTextListener.updatePosition(holder.getAdapterPosition());

                // split the unique_id value in two parts
                String[] unique_id = person.getInfoByKey(field.getKey()).split("-");
                if (unique_id.length == 2) {
                    ((ViewHolderUniqueID) holder).mValueLetters.setText(unique_id[0]);
                    ((ViewHolderUniqueID) holder).mValueFigures.setText(unique_id[1]);
                }
                else {
                    String default_key = AddPersonActivity.getDefaultKey();
                    ((ViewHolderUniqueID) holder).mValueLetters.setText(default_key);
                    ((ViewHolderUniqueID) holder).mValueFigures.setText(String.format("%06d", AddPersonActivity.getNextId(default_key)));
                }
                break;
            case 0:
                ((ViewHolderEditText) holder).mTitle.setText(field.getTitle());
                ((ViewHolderEditText) holder).myCustomEditTextListener.updatePosition(holder.getAdapterPosition());
                ((ViewHolderEditText) holder).mText.setText(person.getInfoByKey(field.getKey()));
                break;
            case 1:
                ((ViewHolderSpinner) holder).mTitle.setText(field.getTitle());
                try {
                    ((ViewHolderSpinner) holder).mSpinner.setAdapter(MainActivity.mConfiguration.getArrayAdapter(field.getString("linked_list")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case 2:
                ((ViewHolderAutoComplete) holder).mTitle.setHint(field.getTitle());
                try {
                    ((ViewHolderAutoComplete) holder).mAutoComplete.setAdapter(MainActivity.mConfiguration.getArrayAdapter(field.getString("linked_list")));
                    ((ViewHolderAutoComplete) holder).mAutoComplete.setText(person.getInfoByKey(field.getKey()));
                    ((ViewHolderAutoComplete) holder).mAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            DataElement data_element = (DataElement) ((ViewHolderAutoComplete) holder).mAutoComplete.getAdapter().getItem(position);
                            person.putInfo(field.getKey(), data_element.getKey());
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case 3:
                ((ViewHolderCalendarView) holder).mTitle.setHint(field.getTitle());
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mFields.get(position).getViewType();
    }

    @Override
    public int getItemCount() {
        return mFields.size();
    }

    // we make TextWatcher to be aware of the position it currently works with
    // this way, once a new item is attached in onBindViewHolder, it will
    // update current position MyCustomEditTextListener, reference to which is kept by ViewHolder
    private class CustomUniqueIDTextListener implements TextWatcher {
        private int position;

        public void updatePosition(int position) {
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            // no op
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            try {
                String field_key = mFields.get(position).getKey();
                // il faut récupérer les deux champs associés problème, on a pas la view
                // TODO c'est la merde là

                person.put(field_key, charSequence.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
            // no op
        }
    }

    // we make TextWatcher to be aware of the position it currently works with
    // this way, once a new item is attached in onBindViewHolder, it will
    // update current position MyCustomEditTextListener, reference to which is kept by ViewHolder
    private class MyCustomEditTextListener implements TextWatcher {
        private int position;

        public void updatePosition(int position) {
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            // no op
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            try {
                String field_key = mFields.get(position).getKey();
                person.put(field_key, charSequence.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
            // no op
        }
    }
}
