package netw4ppl.ines.utils;

import android.annotation.SuppressLint;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;
import java.util.TimeZone;
import java.util.UUID;

import netw4ppl.ines.MainActivity;
import netw4ppl.ines.ManagePersonsActivity;

public class Relation extends JSONObject {

    private String from_best_descriptive_value;
    private String to_best_descriptive_value;

    /**
     * Default constructor of Relation
     * Sets from_person_name_ and to_person_name to empty Strings
     */
    public Relation(){
        super();

        from_best_descriptive_value = "";
        to_best_descriptive_value = "";
    }

    /**
     * Constructor of Relation
     * Sets the informations of a Relation
     * @param infos_relat String containing the informations of a relation in a JSON syntax
     * @throws JSONException
     */
    public Relation(String infos_relat) throws JSONException {
        super(infos_relat);

        from_best_descriptive_value = "";
        to_best_descriptive_value = "";
    }

    /**
     * Constructor of relation
     * Sets the relation using a JSONObject
     *
     * @param data_relation JSONObject containing the data of a relation
     * @throws JSONException
     */
    public Relation(JSONObject data_relation) throws JSONException {
        super(data_relation.toString());

        from_best_descriptive_value = "";
        to_best_descriptive_value = "";
    }

    /**
     * Sets the fullname of the From person
     * @param full_name String of the From person's fullname
     */
    public void setFromFullname(String full_name) {
        from_best_descriptive_value = full_name;
    }

    /**
     * Sets the fullname of the To person
     * @param full_name String of the To person's fullname
     */
    public void setToFullname(String full_name) {
        to_best_descriptive_value = full_name;
    }

    /**
     * Sets the relation type using the key of a relation type (e.g SE for service)
     * @param key_relation String of the key of a relation type
     */
    public void setRelationType(String key_relation) {
        this.putInfo("relation", key_relation);
    }

    /**
     * Sets the To informations of a relation using a Person
     * @param person To Person used to set the relation
     */
    public void setPersonTo(Person person) {
        this.putInfo("to", person.getKey());
        to_best_descriptive_value = person.getInfoByKey(person.bestDescriptiveValueKey);
    }

    /**
     * Sets the To informations of a relation using a String
     * @param unique_id String containing the unique_id of the To Person
     */
    public void setPersonTo(String unique_id) {
        this.putInfo("to_unique_id", unique_id);
    }

    /**
     * Sets the From informations of a relation using a Person
     * @param person From Person used to set the relation
     */
    public void setPersonFrom(Person person) {
        this.putInfo("from", person.getKey());
        from_best_descriptive_value = person.getInfoByKey(person.bestDescriptiveValueKey);
    }

    /**
     * Sets the From informations of a relation using a String
     * @param unique_id String containing the unique_id of the From Person
     */
    public void setPersonFrom(String unique_id) {
        this.putInfo("from_unique_id", unique_id);
    }

    /**
     * Sets the details of a Relations using a String
     * @param details details to be added to the Relation
     */
    public void setDetails(String details) {
        this.putInfo("detail", details);
    }

    /**
     * Sets the Unique ID of a Relation
     */
    public void setUUIDRelation() {
        String uuid = UUID.randomUUID().toString();
        this.putInfo("relation_application_id", uuid);
    }

    /**
     * Adds an information to the Relation with the format "key:value"
     *
     * @param key String containing the key of the information to be added
     * @param info String containing the value of the information to be added
     */
    public void putInfo(String key, String info) {
        try {
            this.put(key, info);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns a String corresponding to an information's value associated to a key
     * @param key String of the key of the information we look for
     * @return String containing the value associated to the key or an empty String if there was no associated value
     */
    public String getInfoByKey(String key) {
        String res = "";
        try {
            res = this.getString(key);
        } catch (JSONException e) {
            // e.printStackTrace();
        }
        return res;
    }

    /**
     * Tests the equality of the Relation we use with another one
     * @param relat Relation to be tested for equality
     * @return a boolean, true if the relations are equal, false if they are different
     */
    public boolean isSameRelation(Relation relat) {
        boolean res_uid_from = false;
        boolean res_uid_to = false;
        boolean res_type_relation = false;
        res_uid_from = this.getInfoByKey("from").equals(relat.getInfoByKey("from"));
        res_uid_to = this.getInfoByKey("to").equals(relat.getInfoByKey("to"));
        res_type_relation = this.getInfoByKey("relation").equals(relat.getInfoByKey("relation"));
        return res_uid_from && res_uid_to && res_type_relation;
    }

    /**
     * Tests if a Person is already From or To of the Relation we use
     * @param p Person to be tested
     * @return a boolean telling if the Person is already contained in the Relation, true if yes, false if not
     */
    public boolean isPersonInRelation(Person p) {
        String p_id = p.getInfoByKey("unique_id");
        String p_full_name = p.getInfoByKey("full_name");
        String person = p_id + " - " + p_full_name;

        return this.getFrom().equals(person) || this.getTo().equals(person);
    }

    /**
     *Returns the unique_id of the From person of the Relation
     * @return a String corresponding to the unique_id of the From Person of the Relation
     */
    public String getFromID() {
        return this.getInfoByKey("from");
    }

    /**
     *Returns the unique_id of the To person of the Relation
     * @return a String corresponding to the unique_id of the To Person of the Relation
     */
    public String getToID() {
        return this.getInfoByKey("to");
    }

    /**
     * Returns the fullname of the From Person of the Relation
     * @return a String corresponding to the fullname of the From Person of the Relation
     */
    public String getFromFullname() {
        return from_best_descriptive_value;
    }

    /**
     * Returns the fullname of the To Person of the Relation
     * @return a String corresponding to the fullname of the To Person of the Relation
     */
    public String getToFullname() {
        return to_best_descriptive_value;
    }

    /**
     * Returns the unique_id and the fullname of the From Person of the Relation
     * @return a String with the format "unique_id - fullname" corresponding to the From Person of the Relation
     */
    public String getFrom() {
        return getFromID() + " - " + getFromFullname();
    }

    /**
     * Returns the unique_id and the fullname of the To Person of the Relation
     * @return a String with the format "unique_id - fullname" corresponding to the TO Person of the Relation
     */
    public String getTo() {
        return getToID() + " - " + getToFullname();
    }

    /**
     * Returns the details of the Relation
     * @return a String corresponding to the details of a Relation
     */
    public String getDetails() {
        return (this.getInfoByKey("detail"));
    }

    /**
     * Returns the RelationType of the Relation
     * @return a String corresponding to the RelationType of a Relation
     * Warning : it only returns the code of the RelationType
     */
    public String getRelationType() {
        HashMap<String, DataElement> datatable_relation_types = MainActivity.mConfiguration.getHashMap_datatables().get("ListRelations");
        if (datatable_relation_types != null) {
            String relation_type = this.getInfoByKey("relation");
            if (relation_type != null) {
                DataElement relation_type_element = datatable_relation_types.get(relation_type);
                if (relation_type_element != null) {
                    return relation_type_element.toString();
                }
            }
        }
        return "";
    }

    /**
     * Returns the RelationType of the Relation
     * @return a String corresponding to the RelationType of a Relation
     * This method returns the full name of the RelationType
     */
    public String getRelationTypeFull() {
        return MainActivity.mConfiguration.getElementFromTable("Relations", getRelationType());
    }

    /**
     * Sets the date at which the Relation was created and adds it to the Relation informations
     */
    public void setCreationDate(){
        Calendar calendar = Calendar.getInstance();
        // ajout du champ date indispensable pour la database
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String date_creation = dateFormat.format(calendar.getTime());

        this.putInfo("date", date_creation);
    }

    /**
     * Sets the date at which the Relation was modified and adds it to the Relation informations
     * This date is different from the creation date
     */
    public void setUpdateDate() {
        Calendar calendar = Calendar.getInstance();

        // ajout de la date d'update
        TimeZone tz = TimeZone.getTimeZone("UTC");
        calendar.setTimeZone(tz);

        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(tz);
        String date_update = dateFormat.format(calendar.getTime());

        this.putInfo("date_update", date_update);
    }

    /**
     * Adds the application_id to the informations of a Relation for information tracking purposes
     * @param app_id String corresponding to the unique application ID of the device
     */
    public void setApplicationID(String app_id) {
        this.putInfo("application_id", app_id);
    }

    /**
     * Associates the unique_ids of the Persons of a Relation to the corresponding names
     * Sets from_person_name and to_person_name if the unique_ids correspond
     * @param array_persons ArrayList<Person> containing the list of the existing Persons
     *                      These persons are compared to the ones of the Relation in order to match names and unique_ids
     */
    public void associateIDWithNames(ArrayList<Person> array_persons) {
        // on a les ids, on a besoin des noms maintenant
        boolean from_found = false;
        boolean to_found = false;

        for (int i=0; i<array_persons.size(); i++) {
            // si la personne a l'index i à cette ID
            Person p = array_persons.get(i);
            if (p.getKey().equals(this.getInfoByKey("from"))) {
                from_best_descriptive_value = p.getInfoByKey(Person.bestDescriptiveValueKey);
                from_found = true;
            }
            if (p.getKey().equals(this.getInfoByKey("to"))) {
                to_best_descriptive_value = p.getInfoByKey(Person.bestDescriptiveValueKey);
                to_found = true;
            }

            if (from_found && to_found)
                break;
        }
    }

    public String getFromBestDescriptiveValue() {
        return ManagePersonsActivity.hashmap_persons.get(this.getFromID()).getInfoByKey(Person.bestDescriptiveValueKey);
    }

    public String getToBestDescriptiveValue() {
        return ManagePersonsActivity.hashmap_persons.get(this.getToID()).getInfoByKey(Person.bestDescriptiveValueKey);
    }

    public void setFromId(String s) {
        this.putInfo("from", s);
    }

    public void setToId(String s) {
        this.putInfo("to", s);
    }
}
