package ru.bircode;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

public class JSON {
    
    private static final JsonParser PARSER = new JsonParser();
    
    public static JsonObject parse(String jsonString){
        try {
            return PARSER.parse(jsonString).getAsJsonObject();
        } catch (JsonParseException ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    public static  boolean isValidJson(String jsonString) {
        return parse(jsonString) != null;
    }
    
}
