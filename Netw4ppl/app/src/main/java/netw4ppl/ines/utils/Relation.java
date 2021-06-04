package netw4ppl.ines.utils;

import android.annotation.SuppressLint;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.UUID;

import netw4ppl.ines.MainActivity;

public class Relation extends JSONObject {

    private String from_person_name;
    private String to_person_name;

    /**
     * Default constructor of Relation
     * Sets from_person_name_ and to_person_name to empty Strings
     */
    public Relation(){
        super();

        from_person_name = "";
        to_person_name = "";
    }

    /**
     * Constructor of Relation
     * Sets the informations of a Relation
     * @param infos_relat String containing the informations of a relation in a JSON syntax
     * @throws JSONException
     */
    public Relation(String infos_relat) throws JSONException {
        super(infos_relat);

        from_person_name = "";
        to_person_name = "";
    }

    /**
     * Sets the fullname of the from person
     * @param full_name String of the from person's fullname
     */
    public void setFromFullname(String full_name) {
        from_person_name = full_name;
    }

    /**
     * Sets the fullname of the to person
     * @param full_name String of the to person's fullname
     */
    public void setToFullname(String full_name) {
        to_person_name = full_name;
    }

    /**
     * Sets the relation type using the key of a relation type (e.g SE for service)
     * @param key_relation String of the key of a relation type
     */
    public void setRelationType(String key_relation) {
        this.putInfo("relation", key_relation);
    }

    /**
     * Sets the to informations of a relation using a Person
     * @param person to Person used to set the relation
     */
    public void setPersonTo(Person person) {
        this.putInfo("to_unique_id", person.getInfoByKey("unique_id"));
        to_person_name = person.getInfoByKey("full_name");
    }

    /**
     * Sets the to informations of a relation using a String
     * @param unique_id String containing the unique_id of the to Person
     */
    public void setPersonTo(String unique_id) {
        this.putInfo("to_unique_id", unique_id);
    }

    /**
     * Sets the from informations of a relation using a Person
     * @param person from Person used to set the relation
     */
    public void setPersonFrom(Person person) {
        this.putInfo("from_unique_id", person.getInfoByKey("unique_id"));
        from_person_name = person.getInfoByKey("full_name");
    }

    /**
     * Sets the from informations of a relation using a String
     * @param unique_id String containing the unique_id of the from Person
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
        res_uid_from = this.getInfoByKey("from_unique_id").equals(relat.getInfoByKey("from_unique_id"));
        res_uid_to = this.getInfoByKey("to_unique_id").equals(relat.getInfoByKey("to_unique_id"));
        res_type_relation = this.getInfoByKey("relation").equals(relat.getInfoByKey("relation"));
        return res_uid_from && res_uid_to && res_type_relation;
    }

    /**
     * Tests if a Person is already from or to of the Relation we use
     * @param p Person to be tested
     * @return a boolean telling if the Person is already contained in the Relation, true if yes, false if not
     */
    public boolean isPersonInRelation(Person p) {
        String p_id = p.getInfoByKey("unique_id");
        String p_full_name = p.getInfoByKey("full_name");
        String person = p_id + " - " + p_full_name;

        return this.getFrom().equals(person) || this.getTo().equals(person);
    }

    public String getFromID() {
        return this.getInfoByKey("from_unique_id");
    }

    public String getToID() {
        return this.getInfoByKey("to_unique_id");
    }

    public String getFromFullname() {
        return from_person_name;
    }

    public String getToFullname() {
        return to_person_name;
    }

    public String getFrom() {
        // aller récupérer le nom de la personne dans la liste des personnes
        return getFromID() + " - " + getFromFullname();
    }

    public String getTo() {
        return getToID() + " - " + getToFullname();
    }

    public String getDetails() {
        return (this.getInfoByKey("detail"));
    }

    public String getRelationType() {
        return (this.getInfoByKey("relation"));
    }

    public String getRelationTypeFull() {
        return MainActivity.mConfiguration.getElementFromTable("Relations", getRelationType());
    }

    public void setCreationDate(){
        Calendar calendar = Calendar.getInstance();
        // ajout du champ date indispensable pour la database
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String date_creation = dateFormat.format(calendar.getTime());

        this.putInfo("date", date_creation);
    }

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

    public void setApplicationID(String app_id) {
        this.putInfo("application_id", app_id);
    }

    public void associateIDWithNames(ArrayList<Person> array_persons) {
        // on a les ids, on a besoin des noms maintenant
        boolean from_found = false;
        boolean to_found = false;

        for (int i=0; i<array_persons.size(); i++) {
            // si la personne a l'index i à cette ID
            Person p = array_persons.get(i);
            if (p.getInfoByKey("unique_id").equals(this.getInfoByKey("from_unique_id"))) {
                from_person_name = p.getInfoByKey("full_name");
                from_found = true;
            }
            if (p.getInfoByKey("unique_id").equals(this.getInfoByKey("to_unique_id"))) {
                to_person_name = p.getInfoByKey("full_name");
                to_found = true;
            }

            if (from_found && to_found)
                break;
        }
    }
}
