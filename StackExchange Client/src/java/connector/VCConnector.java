/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package connector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author visat
 */
public class VCConnector {
    public static final String CONTEXT_PATH = "http://localhost:8083";

    private static JSONObject request(String servletPath, byte[] query) {
        JSONObject object = new JSONObject();
        try {
            if (servletPath == null)
                return object;
            if (!servletPath.startsWith("/"))
                servletPath = "/" + servletPath;
            URLConnection connection = new URL(CONTEXT_PATH + servletPath).openConnection();
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            try (OutputStream output = connection.getOutputStream()) {
                output.write(query);
            }
            StringBuilder builder = new StringBuilder();
            BufferedReader buf = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String read;
            while ((read = buf.readLine()) != null)
                builder.append(read);
            object = (JSONObject)new JSONParser().parse(builder.toString());
        }
        catch (IOException | ParseException ex) {
            System.err.println(ex.getMessage());
        }
        return object;
    }

    public static JSONObject requestVote(String auth, String id, String type, String action) {
        JSONObject object = new JSONObject();
        try {            
            String charset = java.nio.charset.StandardCharsets.UTF_8.name();
            String query = String.format(
                    "auth=%s&id=%s&type=%s&action=%s",
                    URLEncoder.encode(auth, charset),
                    URLEncoder.encode(id, charset),
                    URLEncoder.encode(type, charset),
                    URLEncoder.encode(action, charset));
            object = request("/vote", query.getBytes());
            System.out.println(object.toString());
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
        return object;
    }
}