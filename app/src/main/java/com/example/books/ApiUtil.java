package com.example.books;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class ApiUtil {
    private ApiUtil(){} /// this method is used to remove the constructor


    //Declare a String constant that is assigned the base URL that is not going to change
    public static final String  BASE_API_URL = "https://www.googleapis.com/books/v1/volumes";
    public static final String QUERY_PARAMETER_KEY = "q";
    public static final String KEY = "key";
    public static final String API_KEY = "AIzaSyD2YXlEaTTYRme2ZGPsHp0ZtNZWyYguypE";

    //Create a method that is going to build a URL based on the base URL and the query
    public static URL buildUrl(String title){

        //String fullUrl = BASE_API_URL +"?q=" +title; // create a full URL from teh base and teh title entered by the user

        URL url = null; // create a variable called url of the class/type URL and instantiate as null

        Uri uri = Uri.parse(BASE_API_URL).buildUpon()
                .appendQueryParameter(QUERY_PARAMETER_KEY, title)
                .appendQueryParameter(KEY,API_KEY)
                .build();

        try {
            url = new URL(uri.toString());

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;


    }

    //Create a new method to connect to the API

    public static String getJson (URL url) throws IOException {

        //estbalish connection using HttpUrlConnection class

        HttpURLConnection connection = (HttpURLConnection) url.openConnection(); //create an object of ther HttpUrl connection class



    try {
        InputStream stream  = connection.getInputStream(); //use the inputStream class to read data after establishing a connection

        Scanner scanner = new Scanner(stream);// use a scanner to convert the stream to string.... UTF16
        scanner.useDelimiter("\\A");


        boolean hasData = scanner.hasNext();

        if(hasData){
            return scanner.next();
        }

        else{
            return null;
        }
    }
    catch (Exception e){
        Log.d("Error", e.toString());
        return null;

    }
    finally {
        connection.disconnect();
    }




    }

    public static ArrayList<Book> getBooksFromJson(String json){

        final String ID = "id";
        final String TITLE = "title";
        final String SUBTITLE = "subtitles";
        final String AUTHORS = "authors";
        final String PUBLISHERS =  "publisher";
        final String PUBLISHED_DATE = "publishedDate";
        final String ITEMS = "items";
        final String VOLUME_INFO = "volumeInfo";


        ArrayList<Book> books = new ArrayList<Book>();

        try{
            JSONObject jsonBooks = new JSONObject(json);
            JSONArray arrayBooks =  jsonBooks.getJSONArray(ITEMS);
            int numberOfBooks = arrayBooks.length();

            for(int i = 0; i<numberOfBooks; i++){

                JSONObject bookJson = arrayBooks.getJSONObject(i);
                JSONObject volumeInfoJSON = bookJson.getJSONObject(VOLUME_INFO);

                    int authorNum  = volumeInfoJSON.getJSONArray(AUTHORS).length();

                String[] authors = new String[authorNum];

                for (int j = 0; j<authorNum; j++){

                    authors[j] = volumeInfoJSON.getJSONArray(AUTHORS).get(j).toString();

                }

                Book book = new Book(

                        bookJson.getString(ID),
                        volumeInfoJSON.getString(TITLE),
                        volumeInfoJSON.isNull(SUBTITLE)?"":volumeInfoJSON.getString(SUBTITLE),
                        authors,
                        volumeInfoJSON.getString(PUBLISHERS),
                        volumeInfoJSON.getString(PUBLISHED_DATE)

                );

                books.add(book);

            }


        }
        catch (JSONException e){
            e.printStackTrace();
        }



        return books;



    }






}
