package be.tjs.vubrestaurant.core;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.joda.time.LocalDate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

class RestaurantParser {
    @SuppressWarnings("unused")
    private static final String TAG = "TimetableParser";

    private static final String BASE_URL = "http://monte.rave.org/resto/";

    public static void parseRestaurant(Map<LocalDate, List<Menu>> menusPerDate, int resto, int lang) throws Exception {
        String url = getUrl(resto, lang);
        String jsonResponse = connect(url);
        Gson gson = new Gson();
        final JsonArray jsonArray = new JsonParser().parse(jsonResponse).getAsJsonArray();
        for(final JsonElement menusPerDay : jsonArray) {
            final JsonObject menusPerDayAsJsonObject = menusPerDay.getAsJsonObject();
            final LocalDate date = new LocalDate(menusPerDayAsJsonObject.get("date").getAsString());
            if(menusPerDate.containsKey(date) && menusPerDate.get(date).size() == 0){
                final Menu[] menus = gson.fromJson(menusPerDayAsJsonObject.get("menus"), Menu[].class);
                menusPerDate.put(date, new ArrayList<Menu>(Arrays.asList(menus)));
            }
        }
    }


    private static String getUrl(int resto, int lang) throws Exception {
        // This feels wrong?
        StringBuilder sb = new StringBuilder(BASE_URL);
        switch (resto) {
            case Constants.RESTO_ETTERBEEK:
                sb.append(Constants.ETTERBEEK.toLowerCase());
                break;
            case Constants.RESTO_JETTE:
                sb.append(Constants.JETTE.toLowerCase());
                break;
            default:
                throw new Exception("Invalid restaurant");
        }
        sb.append(".");
        switch (lang) {
            case Constants.LANG_EN:
                sb.append("en");
                break;
            case Constants.LANG_NL:
                sb.append("nl");
                break;
            default:
                throw new Exception("Invalid language");
        }
        sb.append(".json");
        return sb.toString();
    }

    private static String convertStreamToString(InputStream is) {
        /*
         * To convert the InputStream to String we use the BufferedReader.readLine()
         * method. We iterate until the BufferedReader return null which means
         * there's no more data to read. Each line will appended to a StringBuilder
         * and returned as String.
         */
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    private static String connect(String url){
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet(url);
        HttpResponse response;
        try {
            response = httpclient.execute(httpget);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream instream = entity.getContent();
                String result= convertStreamToString(instream);
                instream.close();
                return result;
            }
        } catch (ClientProtocolException e) {
        } catch (IOException e) {
        }
        return null;
    }



}


