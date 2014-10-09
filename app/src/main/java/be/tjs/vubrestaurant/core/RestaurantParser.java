package be.tjs.vubrestaurant.core;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.joda.time.LocalDate;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

class RestaurantParser {
    @SuppressWarnings("unused")
    private static final String TAG = "TimetableParser";

    private static final String BASE_URL = "http://monte.rave.org/resto/";
    private static final String BASE_URL_2 = "http://www.tomjaspers.be/downloads/";

    private static final OkHttpClient client = new OkHttpClient();

    public static void parseRestaurant(Map<LocalDate, List<Menu>> menusPerDate, int resto, int lang) throws Exception {
        // Make the request
        String filename = getFilename(resto, lang);
        Request request = new Request.Builder()
                .url(getUrl(BASE_URL, filename))
                .url(getUrl(BASE_URL_2, filename))
                .build();
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        }

        Reader body = response.body().charStream();
        // And transform the data
        Gson gson = new Gson();
        final JsonArray jsonArray = new JsonParser().parse(body).getAsJsonArray();
        for (final JsonElement menusPerDay : jsonArray) {
            final JsonObject menusPerDayAsJsonObject = menusPerDay.getAsJsonObject();
            final LocalDate date = new LocalDate(menusPerDayAsJsonObject.get("date").getAsString());
            if (menusPerDate.containsKey(date) && menusPerDate.get(date).size() == 0) {
                final Menu[] menus = gson.fromJson(menusPerDayAsJsonObject.get("menus"), Menu[].class);
                menusPerDate.put(date, new ArrayList<Menu>(Arrays.asList(menus)));
            }
        }
    }

    private static String getUrl(String baseUrl, String filename) {
        return new StringBuilder(baseUrl).append(filename).toString();
    }

    private static String getFilename(int resto, int lang) throws Exception {
        // This feels wrong?
        StringBuilder sb = new StringBuilder();
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
}


