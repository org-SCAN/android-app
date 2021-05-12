package netw4ppl.ines.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Build;
import android.text.Editable;
import android.text.InputType;
import android.text.Layout;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
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
import java.util.Calendar;

import netw4ppl.ines.AddPersonActivity;
import netw4ppl.ines.MainActivity;
import netw4ppl.ines.R;

public class AdapterViewFields extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Field> mFields;
    LayoutInflater mInflater;
    private Context mContext;

    class ViewHolderUniqueID extends RecyclerView.ViewHolder {
        TextView mTitleLetters;
        TextView mTitleFigures;
        EditText mValueLetters;
        EditText mValueFigures;
        CustomUniqueIDTextListener customUniqueIDTextListener;

        public ViewHolderUniqueID(@NonNull View itemView) {
            super(itemView);
            mTitleLetters = itemView.findViewById(R.id.unique_id_title_letters);
            mTitleFigures = itemView.findViewById(R.id.unique_id_title_figures);
            mValueLetters = itemView.findViewById(R.id.unique_id_text_letters);
            mValueFigures = itemView.findViewById(R.id.unique_id_text_figures);
        }

        public void setTextListener(CustomUniqueIDTextListener customUniqueIDTextListener) {
            this.customUniqueIDTextListener = customUniqueIDTextListener;
            mValueLetters.addTextChangedListener(this.customUniqueIDTextListener);
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

    class ViewHolderSpinner  extends RecyclerView.ViewHolder implements AdapterView.OnItemSelectedListener {
        TextView mTitle;
        Spinner mSpinner;
        String key_field;
        int spinner_position;

        public ViewHolderSpinner(@NonNull View itemView) {
            super(itemView);

            mTitle = itemView.findViewById(R.id.spinner_title);
            mSpinner = itemView.findViewById(R.id.spinner_object);
            mSpinner.setOnItemSelectedListener(this);
            this.spinner_position = 0;
        }

        public int getPositionElement() {
            return this.spinner_position;
        }

        public void setKey(String key) {
            this.key_field = key;
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            DataElement data_element = (DataElement) parent.getItemAtPosition(position);
            if (!data_element.getKey().equals("NA")) {
                AddPersonActivity.person.putInfo(this.key_field, data_element.getKey());
                this.spinner_position = position;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            // don't do anything
        }
    }

    class ViewHolderCalendarView  extends RecyclerView.ViewHolder {
        TextView mTitle;
        DatePickerDialog mDatePicker;
        EditText mDateText;
        MyCustomEditTextListener myCustomEditTextListener;
        String mKey;

        public ViewHolderCalendarView(@NonNull View itemView, MyCustomEditTextListener customEditTextListener) {
            super(itemView);
            this.myCustomEditTextListener = customEditTextListener;
            mTitle = itemView.findViewById(R.id.date_picker_title);
            mDateText = itemView.findViewById(R.id.date_picker_edittext_object);
            mDateText.addTextChangedListener(this.myCustomEditTextListener);

            mDateText.setInputType(InputType.TYPE_NULL);
            mDateText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // si mDateText est vide
                    int day, month, year;
                    if (mDateText.getText().toString().equals("")) {
                        final Calendar cldr = Calendar.getInstance();
                        day = cldr.get(Calendar.DAY_OF_MONTH);
                        month = cldr.get(Calendar.MONTH);
                        year = cldr.get(Calendar.YEAR);
                    }
                    else {
                        String[] date = mDateText.getText().toString().split("-");
                        year = Integer.parseInt(date[0]);
                        month = Integer.parseInt(date[1]);
                        day = Integer.parseInt(date[2]);
                    }

                    // date picker dialog
                    mDatePicker = new DatePickerDialog(mContext,
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                    mDateText.setText(year + "-" + String.format("%02d", (monthOfYear + 1)) + "-" + String.format("%02d", dayOfMonth));
                                }
                            }, year, month, day);
                    mDatePicker.show();
                }
            });
        }

        public void setKey(String key) {
            this.mKey = key;
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
        }
    }

    public AdapterViewFields(Context context, ArrayList<Field> fields) {
        this.mContext = context;
        this.mFields = fields;
        this.mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case -1:
                view = mInflater.inflate(R.layout.view_holder_unique_id, parent, false);
                ViewHolderUniqueID vUniqueId = new ViewHolderUniqueID(view);
                vUniqueId.setTextListener(new CustomUniqueIDTextListener(vUniqueId));
                return vUniqueId;
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
                return new ViewHolderCalendarView(view, new MyCustomEditTextListener());
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
                /* Section UNIQUE ID */
                ((ViewHolderUniqueID) holder).mTitleFigures.setText(field.getTitle());
                ((ViewHolderUniqueID) holder).mTitleLetters.setText(field.getTitle());
                ((ViewHolderUniqueID) holder).customUniqueIDTextListener.updatePosition(holder.getAdapterPosition());

                // split the unique_id value in two parts
                String[] unique_id = AddPersonActivity.person.getInfoByKey(field.getKey()).split("-");
                // si cette personne a un unique id
                if (unique_id.length == 2) {
                    ((ViewHolderUniqueID) holder).mValueLetters.setText(unique_id[0]);
                    ((ViewHolderUniqueID) holder).mValueFigures.setText(unique_id[1]);
                }
                //sinon lui mettre l'ID par défaut
                else {
                    String default_key = AddPersonActivity.getDefaultKey();
                    ((ViewHolderUniqueID) holder).mValueLetters.setText(default_key);
                    ((ViewHolderUniqueID) holder).mValueFigures.setText(String.format("%06d", AddPersonActivity.getNextId(default_key)));
                }
                break;
            case 0:
                /* Section EditText basique */
                ((ViewHolderEditText) holder).mTitle.setText(field.getTitle());
                ((ViewHolderEditText) holder).myCustomEditTextListener.updatePosition(holder.getAdapterPosition());
                ((ViewHolderEditText) holder).myCustomEditTextListener.setKey(field.getKey());
                ((ViewHolderEditText) holder).mText.setText(AddPersonActivity.person.getInfoByKey(field.getKey()));
                break;
            case 1:
                ((ViewHolderSpinner) holder).mTitle.setText(field.getTitle());
                try {
                    // set l'adapter associé au Spinner
                    ArrayAdapter adapter = MainActivity.mConfiguration.getArrayAdapter(field.getString("linked_list"));
                    ((ViewHolderSpinner) holder).mSpinner.setAdapter(adapter);

                    // set le spinner a une valeur donnée
                    ((ViewHolderSpinner) holder).setKey(field.getKey());
                    String key_val_pers = AddPersonActivity.person.getInfoByKey(field.getKey());
                    int pos_in_adapt = getPositionInAdapter(adapter, key_val_pers);
                    ((ViewHolderSpinner) holder).mSpinner.setSelection(pos_in_adapt);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case 2:
                ((ViewHolderAutoComplete) holder).mTitle.setHint(field.getTitle());
                try {
                    ((ViewHolderAutoComplete) holder).mAutoComplete.setAdapter(MainActivity.mConfiguration.getArrayAdapter(field.getString("linked_list")));
                    ((ViewHolderAutoComplete) holder).mAutoComplete.setText(AddPersonActivity.person.getInfoByKey(field.getKey()));
                    ((ViewHolderAutoComplete) holder).mAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            DataElement data_element = (DataElement) ((ViewHolderAutoComplete) holder).mAutoComplete.getAdapter().getItem(position);
                            AddPersonActivity.person.putInfo(field.getKey(), data_element.getKey());
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case 3:
                ((ViewHolderCalendarView) holder).mTitle.setHint(field.getTitle());
                ((ViewHolderCalendarView) holder).myCustomEditTextListener.setKey(field.getKey());
                ((ViewHolderCalendarView) holder).mDateText.setText(AddPersonActivity.person.getInfoByKey(field.getKey()));
                break;
            default:
                break;
        }
    }

    public int getPositionInAdapter(Adapter adapter, String key_val_pers) {
        DataElement data_element;
        for (int i=0; i<adapter.getCount(); i++) {
            data_element = (DataElement) adapter.getItem(i);
            if (data_element.getKey().equals(key_val_pers))
                return i;
        }
        return 0;
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
        ViewHolderUniqueID mView;
        String previous_value;

        public CustomUniqueIDTextListener(ViewHolderUniqueID v) {
            super();
            this.mView = v;
            this.previous_value = null;
        }

        public void updatePosition(int position) {
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            // no op
            this.previous_value = charSequence.toString() + "-" + mView.mValueFigures.getText();
        }

        @SuppressLint("DefaultLocale")
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            // TODO C'est ici que l'ID fout la merde
            try {
                // get the field key (even if for this case we know it's "unique_id")
                String field_key = mFields.get(position).getKey();
                int id_figures_int;
                String id_figures;

                // convert the CharSequence in String
                String id_letters = charSequence.toString();

                // with the 3-letters code, get the next numerical id
                id_figures_int = AddPersonActivity.getNextId(id_letters);
                id_figures = String.format("%06d", id_figures_int);

                String new_value = id_letters+"-"+id_figures;

                if (!new_value.equals(this.previous_value)) {
                    mView.mValueFigures.setText(id_figures);
                    // save the id written
                    AddPersonActivity.person.put(field_key, new_value);
                }

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
        private String key_field;

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
//                String field_key = mFields.get(position).getKey();
                if (!charSequence.toString().equals(""))
                    AddPersonActivity.person.put(this.key_field, charSequence.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
            // no op
        }

        public void setKey(String key) {
            this.key_field = key;
        }
    }
}
