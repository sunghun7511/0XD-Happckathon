package com.SHGroup.cometooceantofish.api;

import java.io.*;
import java.net.*;
import java.util.*;

public class RequestHelper {
    private final String url;

    private String encoding = "utf-8";

    private boolean useCaches = false;
    private boolean useDefaultCaches = false;

    private RequestHelper.RequestType requestType = RequestHelper.RequestType.GET;
    private Map<String, String> headers = new HashMap<>();

    private Map<String, String> query = new HashMap<>();
    private Map<String, Integer> query_i = new HashMap<>();
    private Map<String, InputStream> files = new HashMap<>();

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

    public RequestHelper setUseCaches(boolean useCaches){
        this.useCaches = useCaches;
        return this;
    }

    public RequestHelper setUseDefaultCaches(boolean useDefaultCaches){
        this.useDefaultCaches = useDefaultCaches;
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
    public RequestHelper putQuery(String key, int value){
        query_i.put(key, value);
        return this;
    }

    public RequestHelper putFile(String fieldName, InputStream uploadFile) {
        files.put(fieldName, uploadFile);
        return this;
    }

    private final String boundary = "===" + System.currentTimeMillis() + "===";
    private final String LINE_FEED = "\r\n";

    public RequestHelper.Response connect() throws RequestException{
        try{
            URL url;

            if(requestType == RequestType.GET && !query.isEmpty()){
                url = new URL(this.url + "?" + buildQuery());
            }else{
                url = new URL(this.url);
            }
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod(requestType.name());

            con.setUseCaches(useCaches);
            con.setDefaultUseCaches(useDefaultCaches);

            if(headers.size() > 0) {
                for (String n : headers.keySet()) {
                    con.addRequestProperty(n, headers.get(n));
                }
            }
            con.setDoInput(true);

            if(requestType != RequestType.GET && !query.isEmpty()) {
                if(files.size() != 0){
                    con.setRequestProperty("Content-Type",
                            "multipart/form-data;boundary=" + boundary);
                }
                con.setDoOutput(true);
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(con.getOutputStream(), encoding));

                if(files.size() != 0){
                    for(String n : query.keySet()){
                        writer.append("--" + boundary).append(LINE_FEED);
                        writer.append("Content-Disposition: form-data; name=\"" + URLEncoder.encode(n, encoding) + "\"")
                                .append(LINE_FEED);
                        writer.append("Content-Type: text/plain; charset=" + encoding).append(
                                LINE_FEED);
                        writer.append(LINE_FEED);
                        writer.append(URLEncoder.encode(query.get(n), encoding)).append(LINE_FEED);
                        writer.flush();
                    }

                    for(String n : files.keySet()){
                        String fileName = Integer.toHexString(new Random().nextInt()) + "_" + Integer.toHexString(new Random().nextInt()) + "_" + Integer.toHexString(new Random().nextInt()) +Integer.toHexString(new Random().nextInt()) +Integer.toHexString(new Random().nextInt()) +Integer.toHexString(new Random().nextInt());
                        writer.append("--" + boundary).append(LINE_FEED);
                        writer.append(
                                "Content-Disposition: form-data; name=\"" + n
                                        + "\"; filename=\"" + fileName + "\"")
                                .append(LINE_FEED);
                        writer.append(
                                "Content-Type: "
                                        + URLConnection.guessContentTypeFromName(fileName))
                                .append(LINE_FEED);
                        writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
                        writer.append(LINE_FEED);
                        writer.flush();

                        InputStreamReader inputStream = new InputStreamReader(files.get(n));
                        char[] buffer = new char[4096];
                        int bytesRead = -1;
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            writer.write(buffer, 0, bytesRead);
                        }
                        writer.flush();
                        inputStream.close();


                        writer.flush();
                    }
                    writer.append(LINE_FEED).flush();
                    writer.append("--" + boundary + "--").append(LINE_FEED);
                }else{
                    writer.write(buildQuery());
                }

                writer.flush();
                writer.close();
            }

            con.connect();

            StringBuilder sb = new StringBuilder();

            if(con.getResponseCode() < 300) {
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), encoding));
                String n;
                boolean first = true;
                while ((n = br.readLine()) != null) {
                    if (first) {
                        first = false;
                    } else {
                        sb.append("\r\n");
                    }
                    sb.append(n);
                }
                br.close();
            }

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
        for (String key : query_i.keySet()) {
            if (first) {
                first = false;
            } else {
                q.append("&");
            }
            try {
                q.append(URLEncoder.encode(key, "UTF-8")).append("=")
                        .append(query_i.get(key));
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
