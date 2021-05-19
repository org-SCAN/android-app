package netw4ppl.ines.utils;

import android.annotation.SuppressLint;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class Relation extends JSONObject {

    public Relation(){
        super();
    }

    public Relation(Person from, String key_relation, Person to, String detail_input) throws JSONException {
        super();

        this.put("from_unique_id", from.getInfoByKey("unique_id"));
        this.put("from_full_name", from.getInfoByKey("full_name"));
        this.put("to_unique_id", to.getInfoByKey("unique_id"));
        this.put("to_full_name", to.getInfoByKey("full_name"));
        this.put("relation", key_relation);
        this.put("detail", detail_input);
    }

    public Relation(String infos_relat) throws JSONException {
        super(infos_relat);
    }

    public void setRelationType(String key_relation) {
        try {
            this.put("relation", key_relation);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setDetails(String details) {
        try {
            this.put("detail", details);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getInfoByKey(String key) {
        String res = "";
        try {
            res = this.getString(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return res;
    }

    public boolean isSameRelation(Relation relat) {
        boolean res_uid_from = false;
        boolean res_uid_to = false;
        boolean res_type_relation = false;
        try {
            res_uid_from = this.getString("from_unique_id").equals(relat.getString("from_unique_id"));
            res_uid_to = this.getString("to_unique_id").equals(relat.getString("to_unique_id"));
            res_type_relation = this.getString("relation").equals(relat.getString("relation"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return res_uid_from && res_uid_to && res_type_relation;
    }

    public boolean isPersonInRelation(Person p) {
        String p_id = p.getInfoByKey("unique_id");
        String p_full_name = p.getInfoByKey("full_name");
        String person = p_id + " - " + p_full_name;

        return this.getFrom().equals(person) || this.getTo().equals(person);
    }

    public String getFrom() {
        return (this.getInfoByKey("from_unique_id") + " - " + this.getInfoByKey("from_full_name"));
    }

    public String getTo() {
        return (this.getInfoByKey("to_unique_id") + " - " + this.getInfoByKey("to_full_name"));
    }

    public String getDetails() {
        return (this.getInfoByKey("detail"));
    }

    public String getRelationType() {

        return (this.getInfoByKey("relation"));
    }

    public void setCreationDate(){
        Calendar calendar = Calendar.getInstance();
        // ajout du champ date indispensable pour la database
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String date_creation = dateFormat.format(calendar.getTime());

        try {
            this.put("date", date_creation);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setUpdateDate() {
        Calendar calendar = Calendar.getInstance();

        // ajout de la date d'update
        TimeZone tz = TimeZone.getTimeZone("UTC");
        calendar.setTimeZone(tz);

        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(tz);
        String date_update = dateFormat.format(calendar.getTime());

        try {
            this.put("date_update", date_update);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setApplicationID(String app_id) {
        try {
            this.put("application_id", app_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
