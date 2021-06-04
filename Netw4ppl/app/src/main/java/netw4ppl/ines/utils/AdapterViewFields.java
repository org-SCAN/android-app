package netw4ppl.ines.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Build;
import android.text.Editable;
import android.text.InputFilter;
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
import java.util.regex.Pattern;

import netw4ppl.ines.AddPersonActivity;
import netw4ppl.ines.MainActivity;
import netw4ppl.ines.R;


/**
 * AdapterViewFields is the class used to display the AddPersonActivity form. It extends the object Adapter
 * from the RecyclerView class. The adapter created will contain ViewHolders of different types. That way we
 * can enter more than one type of data (ex: dates, numbers, strings ...)
 */
public class AdapterViewFields extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Field> mFields;
    LayoutInflater mInflater;
    private final Context mContext;
    private boolean new_person;

    /**
     * ViewHolder used to enter what was originally called "Unique ID".
     */
    class ViewHolderUniqueID extends RecyclerView.ViewHolder {
        TextView mTitleLetters;
        TextView mTitleFigures;
        EditText mValueLetters;
        EditText mValueFigures;
        CustomUniqueIDTextListener customUniqueIDTextListener;

        /**
         * Class constructor.
         *
         * @param itemView a View
         * {@inheritDoc}
         */
        public ViewHolderUniqueID(@NonNull View itemView) {
            super(itemView);
            mTitleLetters = itemView.findViewById(R.id.unique_id_title_letters);
            mTitleFigures = itemView.findViewById(R.id.unique_id_title_figures);
            mValueLetters = itemView.findViewById(R.id.unique_id_text_letters);
            mValueFigures = itemView.findViewById(R.id.unique_id_text_figures);

            mValueLetters.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        }

        /**
         * Function to set the TextListener of the ViewHolder.
         *
         * @param customUniqueIDTextListener a TextListener adapted for the Unique ID format.
         */
        public void setTextListener(CustomUniqueIDTextListener customUniqueIDTextListener) {
            this.customUniqueIDTextListener = customUniqueIDTextListener;
            mValueLetters.addTextChangedListener(this.customUniqueIDTextListener);
        }
    }

    /**
     * ViewHolder used to contain an AutoCompleteTextView.
     */
    class ViewHolderAutoComplete  extends RecyclerView.ViewHolder {
        TextView mTitle;
        AutoCompleteTextView mAutoComplete;

        /**
         * Class constructor.
         *
         * @param itemView a View
         * {@inheritDoc}
         */
        public ViewHolderAutoComplete(@NonNull View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.autocomplete_title);
            mAutoComplete = itemView.findViewById(R.id.autocomplete_object);
        }
    }

    /**
     * ViewHolder used to contain an Spinner.
     */
    class ViewHolderSpinner  extends RecyclerView.ViewHolder implements AdapterView.OnItemSelectedListener {
        TextView mTitle;
        Spinner mSpinner;
        String key_field;
        int spinner_position;

        /**
         * Class constructor.
         *
         * @param itemView a View
         * {@inheritDoc}
         */
        public ViewHolderSpinner(@NonNull View itemView) {
            super(itemView);

            mTitle = itemView.findViewById(R.id.spinner_title);
            mSpinner = itemView.findViewById(R.id.spinner_object);
            mSpinner.setOnItemSelectedListener(this);
            this.spinner_position = 0;
        }

        /**
         * Get the spinner position.
         *
         * @return an int. The value of the attribute spinner_position.
         */
        public int getPositionElement() {
            return this.spinner_position;
        }

        /**
         * Set the attribute key_field with the specified value in parameters.
         *
         * @param key a String representing the key of the Field object.
         */
        public void setKey(String key) {
            this.key_field = key;
        }

        /**
         * Determine the actions done when an element is selected in the spinner.
         *
         * @param parent an AdapterView object
         * @param view a View object
         * @param position an int, representing the spinner position
         * @param id a long
         */
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            DataElement data_element = (DataElement) parent.getItemAtPosition(position);
            if (!data_element.getKey().equals("NA")) {
                AddPersonActivity.person.putInfo(this.key_field, data_element.getKey());
                this.spinner_position = position;
            }
            else {
                AddPersonActivity.person.remove(this.key_field);
            }
        }

        /**
         * Determine the actions done when nothing is selected. Here, no operation is performed.
         *
         * @param parent an AdapterView object
         */
        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            // don't do anything
        }
    }

    /**
     * ViewHolder used to contain an EditText and a DatePicker.
     */
    class ViewHolderCalendarView  extends RecyclerView.ViewHolder {
        TextView mTitle;
        DatePickerDialog mDatePicker;
        EditText mDateText;
        MyCustomEditTextListener myCustomEditTextListener;
        String mKey;

        String regex_date;

        /**
         * Class constructor
         *
         * @param itemView the view associated with this ViewHolder
         * @param customEditTextListener a TextListener to associate with the view
         */
        public ViewHolderCalendarView(@NonNull View itemView, MyCustomEditTextListener customEditTextListener) {
            super(itemView);
            this.myCustomEditTextListener = customEditTextListener;
            this.myCustomEditTextListener.setView(this);
            mTitle = itemView.findViewById(R.id.date_picker_title);
            mDateText = itemView.findViewById(R.id.date_picker_edittext_object);
            mDateText.addTextChangedListener(this.myCustomEditTextListener);

            regex_date = "([12]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01]))";
            this.myCustomEditTextListener.setRegex(regex_date);

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
                        month = Integer.parseInt(date[1]) - 1;
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

        /**
         * Set the mKey attribute wit the value given in parameter.
         *
         * @param key a String
         */
        public void setKey(String key) {
            this.mKey = key;
        }
    }

    /**
     * A ViewHolder used for the input of text in an EditText.
     */
    class ViewHolderEditText extends RecyclerView.ViewHolder {
        TextView mTitle;
        EditText mText;
        MyCustomEditTextListener myCustomEditTextListener;

        /**
         * Class Constructor
         *
         * @param itemView the view associated with this ViewHolder
         * @param customEditTextListener the text listener to associate with the EditText
         */
        public ViewHolderEditText(@NonNull View itemView, MyCustomEditTextListener customEditTextListener) {
            super(itemView);
            this.myCustomEditTextListener = customEditTextListener;
            mTitle = itemView.findViewById(R.id.textview_title);
            mText = itemView.findViewById(R.id.textview_object);
            mText.addTextChangedListener(this.myCustomEditTextListener);
        }
    }

    /**
     * Class constructor
     *
     * @param context the context of the application
     * @param fields an ArrayList of Field objects
     * @param new_pers a boolean telling if it's a new person the user is adding or not
     */
    public AdapterViewFields(Context context, ArrayList<Field> fields, boolean new_pers) {
        this.mContext = context;
        this.mFields = fields;
        this.mInflater = LayoutInflater.from(context);
        this.new_person = new_pers;
    }

    /**
     * Override of the onCreateViewHolder function. Create ViewHolders based on the viewType specified in parameters.
     *
     * @param parent a ViewGroup object
     * @param viewType an int representing the view type
     * @return a RecyclerView.ViewHolder object. The type of the object depends of the input type specified.
     */
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

    /**
     * Override of the onBindViewHolder function. Set the ViewHolder content based on what they are representing.
     *
     * @param holder a RecyclerView.ViewHolder object
     * @param position an int
     */
    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Field field = mFields.get(position);

        switch (field.getViewType()) {
            case -1:
                /* Section UNIQUE ID */
                ((ViewHolderUniqueID) holder).mTitleFigures.setText("");
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

                // s'il s'agit de la modificaiton d'une personne, désactiver ce champs
                ((ViewHolderUniqueID) holder).mValueLetters.setEnabled(this.new_person);

                break;
            case 0:
                /* Section EditText basique */
                ((ViewHolderEditText) holder).mTitle.setText(field.getTitle());
                ((ViewHolderEditText) holder).mText.setHint(field.getElementByKey("placeholder"));
                ((ViewHolderEditText) holder).myCustomEditTextListener.updatePosition(holder.getAdapterPosition());
                ((ViewHolderEditText) holder).myCustomEditTextListener.setKey(field.getKey());
                ((ViewHolderEditText) holder).mText.setText(AddPersonActivity.person.getInfoByKey(field.getKey()));

                if (field.getElementByKey("database_type").equals("integer"))
                    ((ViewHolderEditText) holder).mText.setInputType(InputType.TYPE_CLASS_NUMBER);
                else if (field.getElementByKey("database_type").equals("string"))
                    ((ViewHolderEditText) holder).mText.setInputType(InputType.TYPE_CLASS_TEXT);
                else
                    ((ViewHolderEditText) holder).mText.setInputType(InputType.TYPE_CLASS_TEXT);
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
                    String text_displayed = AddPersonActivity.person.getInfoByKey(field.getKey());
                    text_displayed = MainActivity.mConfiguration.getElementFromTable(field.getLinkedList(), text_displayed);

                    ((ViewHolderAutoComplete) holder).mAutoComplete.setText(text_displayed, false); // AddPersonActivity.person.getInfoByKey(field.getKey())
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
                ((ViewHolderCalendarView) holder).mDateText.setHint(field.getElementByKey("placeholder"));
                ((ViewHolderCalendarView) holder).mDateText.setText(AddPersonActivity.person.getInfoByKey(field.getKey()));
                break;
            default:
                break;
        }
    }

    /**
     * Function to get the position of a specified key in an adapter of DataElement objects.
     *
     * @param adapter an Adapter object
     * @param key_val_pers a String
     * @return an int giving the position of the key_val_person parameter in the adapter
     */
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

    /**
     * Class implementing TextWatcher for a Unique ID.
     */
    private class CustomUniqueIDTextListener implements TextWatcher {
        private int position;
        ViewHolderUniqueID mView;

        private final String regex_id = "([A-Z]{3})";

        /**
         * Class constructor.
         *
         * @param v a ViewHolderUniqueID object
         */
        public CustomUniqueIDTextListener(ViewHolderUniqueID v) {
            super();
            this.mView = v;
        }

        /**
         * Function to update the position of the cursor in the EditText
         *
         * @param position an int
         */
        public void updatePosition(int position) {
            this.position = position;
        }

        /**
         * Function called before the text is changed.
         *
         * @param charSequence a CharSequence object representing the text written
         * @param i an int
         * @param i2 an int
         * @param i3 an int
         */
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            // no op
        }

        /**
         * Function called when the text is changed.
         *
         * @param charSequence a CharSequence object
         * @param i an int
         * @param i2 an int
         * @param i3 an int
         */
        @SuppressLint("DefaultLocale")
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            // no op
        }

        /**
         * Function called after the text is changed.
         *
         * @param editable an Editable object
         */
        @Override
        public void afterTextChanged(Editable editable) {
            // no op
            String actual_value = editable.toString();
            // get the field key (even if for this case we know it's "unique_id")
            String field_key = mFields.get(position).getKey();

            if (Pattern.matches(regex_id, actual_value)) {
                String id_person = AddPersonActivity.person.getInfoByKey(field_key);
                String[] ids = id_person.split("-");
                String letters = ids[0];

                if (!actual_value.equals(letters)) {
                    // on va récupérer la valeur suivante de tricode
                    int id_figures_int = AddPersonActivity.getNextId(actual_value);
                    String id_figures = String.format("%06d", id_figures_int);
                    String new_value = actual_value+"-"+id_figures;

                    // on change le champs de texte pour les chiffres
                    mView.mValueFigures.setText(id_figures);
                    // on ajoute l'unique ID à la personne
                    try {
                        AddPersonActivity.person.put(field_key, new_value);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            else {
                // supprimer le champ de personne pour forcer l'erreur
                AddPersonActivity.person.remove("unique_id");
                mView.mValueLetters.setError(mContext.getResources().getString(R.string.error_unique_id));
            }
        }
    }

    // we make TextWatcher to be aware of the position it currently works with
    // this way, once a new item is attached in onBindViewHolder, it will
    // update current position MyCustomEditTextListener, reference to which is kept by ViewHolder
    /**
     * Class implementing TextWatcher for an basic EditText
     */
    private class MyCustomEditTextListener implements TextWatcher {
        private int position;
        private String key_field;
        private String regex = null;
        private ViewHolderCalendarView mView;

        /**
         * Class Constructor
         */
        public MyCustomEditTextListener() {
            super();
        }

        /**
         * Class constructor using a ViewHolderCalendarView view
         *
         * @param view a ViewHolderCalendarView object
         */
        public MyCustomEditTextListener(ViewHolderCalendarView view) {
            super();
            mView = view;
        }

        /**
         * Function to update the position of the cursor in the EditText
         *
         * @param position an int
         */
        public void updatePosition(int position) {
            this.position = position;
        }

        /**
         * Function called before the text is changed.
         *
         * @param charSequence a CharSequence object representing the text written
         * @param i an int
         * @param i2 an int
         * @param i3 an int
         */
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            // no op
        }

        /**
         * Function called when the text is changed.
         *
         * @param charSequence a CharSequence object representing the text written
         * @param i an int
         * @param i2 an int
         * @param i3 an int
         */
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
//                String field_key = mFields.get(position).getKey();
            // si mon champs respecte la regex, on l'ajoute, sinon on le supprime
            if (getRegex() != null) {
                if (Pattern.matches(getRegex(), charSequence.toString())) {
                    // enlever le message d'erreur
                    AddPersonActivity.person.putInfo(this.key_field, charSequence.toString());
                    mView.mDateText.setError(null);
                }
                else {
                    if (!charSequence.toString().equals(""))
                        mView.mDateText.setError(mContext.getString(R.string.invalid_format));
                    // set error message
                    AddPersonActivity.person.remove(this.key_field);
                }
            } else {
                if (!charSequence.toString().equals(""))
                    AddPersonActivity.person.putInfo(this.key_field, charSequence.toString());
                else {
                    AddPersonActivity.person.remove(this.key_field);
                }
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
            // no op
        }

        /**
         * Function to set the key_field attribute.
         *
         * @param key a String representing the key of the field associated with the view.
         */
        public void setKey(String key) {
            this.key_field = key;
        }

        /**
         * Associate a regex with the text listener to perform verifications when something is written.
         *
         * @param reg a String representing the regex.
         */
        public void setRegex(String reg) {
            this.regex = reg;
        }

        /**
         * Get the regex associated with the TextListener.
         *
         * @return a String if the object exists, or null if it doesn't
         */
        public String getRegex() {
            return this.regex;
        }

        /**
         * Function to set a view. 
         *
         * @param view a ViewHolderCalendarView object
         */
        public void setView(ViewHolderCalendarView view) {
            this.mView = view;
        }
    }
}
