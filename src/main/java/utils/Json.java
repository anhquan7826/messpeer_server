package utils;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class Json {
    public static String toJson(HashMap<String, String> map) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.putAll(map);
        return jsonObject.toJSONString();
    }

    public static HashMap<String, String> toHashMap(String jsonString) {
        return (HashMap<String, String>) JSONValue.parse(jsonString);
    }
}


