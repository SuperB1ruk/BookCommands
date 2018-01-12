package ru.bircode;

import com.google.gson.JsonObject;
import java.util.logging.Logger;

public class BConnector {
    
    private final String IP, resourceName, resourceVersion;
    private final String connectorURL = "https://connector.bircode.ru/";
    
    public BConnector(String resourceName, String resourceVersion){
        IP = Internet.fetchIP();
        this.resourceName = resourceName;
        this.resourceVersion = resourceVersion;
        info("IP: "+IP);
        JsonObject res = doRequest("checkConnection", new JsonObject());
        if(res.get("resultCode").getAsInt() == 203){
            info("Access granted.");
        }else{
            info("Access denied. Result code: "+res.get("resultCode").getAsInt());
        }
    }
    
    public JsonObject doRequest(String action, JsonObject request){
        request.addProperty("action", action);
        request.addProperty("resourceName", resourceName);
        request.addProperty("resourceVersion", resourceVersion);
        request.addProperty("ip", IP);
        String result = Internet.callURL(connectorURL, "noCrypt", "data="+request.toString());
        System.out.println(result);
        return JSON.parse(result);
    }
    
    private void info(String message){
        Logger.getGlobal().info("[bircodeConnector] "+message);
    }
    
}