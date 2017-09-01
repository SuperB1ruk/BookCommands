package ru.bircode;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
public class Internet {
    
    public static String fetchIP() {
        return getURLContent("http://checkip.amazonaws.com").replace(" ", "").replace("\n", "");
    }
    
    public static String buildParameters(boolean que, String... parameters){
        String params = que ? "?" : "";
        for(String param : parameters){
            params = params+param+"&";
        }
        params = params.substring(0, params.length()-1);
        return params;
    }

    public static String getAvailable(String... urls) {
        for (String url : urls) {
            if (isAvailable(url)) return url;
        }
        return null;
    }
    
    public static String callURL(String url, String... params){
        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", "Mozilla/5.0");
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            String urlParameters = buildParameters(false, params);
            con.setDoOutput(true);
            try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
                wr.writeBytes(urlParameters);
                wr.flush();
            }
            StringBuilder response;
            try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                String inputLine;
                response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
            }
            return response.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static String getURLContent(String surl) {
        try {
            URL url = new URL(surl);
            URLConnection conn = url.openConnection();
            InputStream stream = conn.getInputStream();
            return getContent(stream);
        } catch (Exception e) { }
        return null;
    }


    private static boolean isAvailable(String url) {
        return getURLContent(url) != null;
    }

    public static String getContent(InputStream inputStream) {
        StringBuilder document = new StringBuilder();
        try (Scanner scanner = new Scanner(inputStream)) {
            scanner.useDelimiter("\\Z");
            while (scanner.hasNext())
                document.append(scanner.next());
        }
        return document.toString();
    }
    
    public static class Downloader {
    
        private URLConnection con;

        public byte[] download(String downloadLink){
            ByteArrayOutputStream baos = null;
            try {
                URL url = new URL(downloadLink);
                con = url.openConnection();
                BufferedInputStream bis = new BufferedInputStream(con.getInputStream());
                baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int count = 0;
                while((count = bis.read(buffer,0,1024)) != -1)
                {
                    baos.write(buffer, 0, count);
                }
                baos.close();
                bis.close();
            } catch (MalformedURLException ex) {
                Logger.getLogger(Downloader.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Downloader.class.getName()).log(Level.SEVERE, null, ex);
            }
            return baos.toByteArray();
        }

        public URLConnection getConnection(){
            return con;
        }

    }

    
}
