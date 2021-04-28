package netw4ppl.ines.utils;

import org.json.JSONException;
import org.json.JSONObject;

public class Relation extends JSONObject {

    public Relation(Person from, String key_relation, Person to, String date_ajout, String detail_input) throws JSONException {
        super();

        this.put("from_unique_id", from.getInfoByKey("unique_id"));
        this.put("from_full_name", from.getInfoByKey("full_name"));
        this.put("to_unique_id", from.getInfoByKey("unique_id"));
        this.put("to_full_name", to.getInfoByKey("full_name"));
        this.put("relation", key_relation);
        this.put("date", date_ajout);
        this.put("detail", detail_input);
    }
}
