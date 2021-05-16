package netw4ppl.ines.utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

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
        try {
            res_uid_from = this.getString("from_unique_id").equals(relat.getString("from_unique_id"));
            res_uid_to = this.getString("to_unique_id").equals(relat.getString("to_unique_id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return res_uid_from && res_uid_to;
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

    public String getRelation_type() {

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
}
