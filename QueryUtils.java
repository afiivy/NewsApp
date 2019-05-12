package com.example.android.newsapp;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class QueryUtils {
    private static final String LOG_TAG = QueryUtils.class.getSimpleName ();

    private QueryUtils() {

    }

    public static List<News> fetchNewsData(String requestUrl) {
        URL url = createUrl ( requestUrl );
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest ( url );
        } catch (IOException e) {
            Log.e ( LOG_TAG, "Problem making the HTTP request.", e );

        }
        List<News> news = extractFeatureFromJson ( jsonResponse );
        return news;
    }

    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL ( stringUrl );
        } catch (MalformedURLException e) {
            Log.e ( LOG_TAG, "Problem building the URL ", e );
        }

        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        if (url == null) {
            return jsonResponse;

        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection ();
            urlConnection.setReadTimeout ( 10000 /* milliseconds */ );
            urlConnection.setConnectTimeout ( 15000 /* milliseconds */ );
            urlConnection.setRequestMethod ( "GET" );
            urlConnection.connect ();

            if (urlConnection.getResponseCode () == 200) {
                inputStream = urlConnection.getInputStream ();
                jsonResponse = readFromStream ( inputStream );
            } else {
                Log.e ( LOG_TAG, "Error response code: " + urlConnection.getResponseCode () );
            }
        } catch (IOException e) {
            Log.e ( LOG_TAG, "Problem retrieving the news JSON results.", e );

        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect ();
            }
            if (inputStream != null) {

                inputStream.close ();
            }
        }
        return jsonResponse;

    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder ();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader ( inputStream, Charset.forName ( "UTF-8" ) );
            BufferedReader reader = new BufferedReader ( inputStreamReader );
            String line = reader.readLine ();
            while (line != null) {
                output.append ( line );
                line = reader.readLine ();
            }

        }
        return output.toString ();

    }

    private static List<News> extractFeatureFromJson(String newsJSON) {
        List<News> newsList = new ArrayList<> ();

        if (TextUtils.isEmpty ( newsJSON )) {
            return null;
        }

        List<News> news = new ArrayList<> ();
        try {

            JSONObject jsonObject = new JSONObject ( newsJSON );
            JSONObject response = jsonObject.getJSONObject ( "response" );
            JSONArray newsArray = response.getJSONArray ( "results" );

            for (int i = 0; i < newsArray.length (); i++) {
                JSONObject currentNews = newsArray.getJSONObject ( i );

                String sectionName = currentNews.getString ( "sectionName" );
                String section = "No section available";
                if (currentNews.has ( "sectionName" )) {
                    section = currentNews.getString ( "sectionName" );
                }

                String title = currentNews.getString ( "webTitle" );
                String webTitle = "No webTitle available";
                if (currentNews.has ( "webTitle" )) {
                    webTitle = currentNews.getString ( "webTitle" );
                }

                String url = currentNews.getString ( "webUrl" );
                String date = currentNews.getString ( "webPublicationDate" );

                JSONObject fields = currentNews.getJSONObject ( "fields" );
                String imageUrl = null;
                if (fields != null) {
                    imageUrl = fields.getString ( "thumbnail" );
                }

                JSONArray tags = currentNews.getJSONArray ( "tags" );
                String authorsName = null;
                String authorsLastname = null;
                if (tags.length () > 0) {
                    JSONObject author = tags.getJSONObject ( 0 );
                    authorsName = author.getString ( "firstName" );
                    authorsLastname = author.getString ( "lastName" );

                }

                news.add ( new News ( sectionName, title, authorsName, authorsLastname, date, url, imageUrl ) );
                Log.d ( LOG_TAG, news.get ( i ).toString () );

            }
            // return news;


        } catch (JSONException e) {
            Log.e ( "QueryUtils", "Problem parsing the news JSON results", e );
        }

        return news;

    }


}


