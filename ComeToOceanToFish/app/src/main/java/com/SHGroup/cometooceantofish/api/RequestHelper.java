package com.SHGroup.cometooceantofish.api;

import java.io.*;
import java.net.*;
import java.util.*;

public class RequestHelper {
    private final String url;

    private String encoding = "utf-8";
    private RequestHelper.RequestType requestType = RequestHelper.RequestType.GET;
    private Map<String, String> headers = new HashMap<>();

    private Map<String, String> query = new HashMap<>();

    public RequestHelper(String url){
        this.url = url;
    }

    public RequestHelper setRequestType(RequestType type){
        requestType = type;
        return this;
    }

    public RequestHelper putHeader(String key, String value){
        headers.put(key, value);
        return this;
    }

    public RequestHelper setEncoding(String encoding){
        this.encoding = encoding;
        return this;
    }

    public RequestHelper putDefaultHeader(){

        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Whale/1.0.39.16 Safari/537.36");
        headers.put( "Accept-Encoding", "gzip,deflate");
        headers.put("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7");
        headers.put("Keep-Alive", "300");
        headers.put("Connection", "keep-alive");
        headers.put("Pragma", "no-cache");
        headers.put("Cache-Control", "no-cache");

        return this;
    }

    public RequestHelper putQuery(String key, String value){
        query.put(key, value);
        return this;
    }

    public RequestHelper.Response connect() throws RequestException{
        try{
            URL url;

            if(requestType == RequestType.GET && !query.isEmpty()){
                url = new URL(this.url + "?" + buildQuery());
            }else{
                url = new URL(this.url);
            }
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setDoInput(true);
            con.setDoOutput(true);

            con.setRequestMethod(requestType.name());

            if(headers.size() > 0) {
                boolean b = true;
                for (String n : headers.keySet()) {
                    if (b) {
                        con.setRequestProperty(n, headers.get(n));
                        b = false;
                    } else {
                        con.addRequestProperty(n, headers.get(n));
                    }
                }
            }

            if(requestType != RequestType.GET && !query.isEmpty()) {
                OutputStream os = con.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, encoding));

                writer.write(buildQuery());

                writer.flush();
                writer.close();
                os.close();
            }

            con.connect();

            StringBuilder sb = new StringBuilder();


            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), encoding));
            String n;
            boolean first = true;
            while((n = br.readLine()) != null) {
                if (first) {
                    first = false;
                } else {
                    sb.append("\r\n");
                }
                sb.append(n);
            }
            br.close();

            return new Response(con.getResponseCode(), con.getHeaderFields(), sb.toString());
        }catch(Exception ex){
            throw new RequestException("Connect failed...", ex);
        }
    }

    private final String buildQuery() throws RequestException {
        StringBuilder q = new StringBuilder();
        boolean first = true;

        for (String key : query.keySet()) {
            if (first) {
                first = false;
            } else {
                q.append("&");
            }
            try {
                q.append(URLEncoder.encode(key, "UTF-8")).append("=")
                        .append(URLEncoder.encode(query.get(key), "UTF-8"));
            } catch (UnsupportedEncodingException ex) {
                throw new RequestException("Unsupport utf-8 encoding", ex);
            }
        }
        return q.toString();
    }

    public enum RequestType {
        GET, POST, HEAD, PUT, DELETE, CONNECT, OPTIONS, TRACE, PATCH;
    }

    public static final class Response {
        private final int response;
        private final Map<String, List<String>> headers;
        private final String body;

        private Response(int response, Map<String, List<String>> headers, String body){
            this.response = response;
            this.headers = headers;
            this.body = body;
        }

        public final int getResponseCode(){
            return response;
        }

        public final Map<String, List<String>> getHeaders(){
            return headers;
        }

        public final Map<String, String> getSetCookies(){
            final String sc_key = "Set-Cookie";

            Map<String, String> set_cookie = new HashMap<>();

            if(getHeaders().containsKey(sc_key)){
                for(String n : getHeaders().get(sc_key)){
                    for(String values : n.split(";")){
                        int t_ind = values.indexOf("=");
                        try{
                            String key = values.substring(0, t_ind);
                            String value = URLDecoder.decode(values.substring(t_ind+1, values.length()), "utf-8");

                            set_cookie.put(key, value);
                        }catch(UnsupportedEncodingException e){
                        }
                    }
                }
            }

            return set_cookie;
        }

        public final String getBody(){
            return body;
        }
    }
}
