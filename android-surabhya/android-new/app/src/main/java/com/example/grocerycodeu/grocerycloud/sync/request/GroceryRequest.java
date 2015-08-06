package com.example.grocerycodeu.grocerycloud.sync.request;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.net.HttpURLConnection;

public class GroceryRequest{

    //The log tag for the class
    private static final String LOG_TAG = GroceryRequest.class.getSimpleName();

    //The base api url address
    public static final String BASE_URL = "http://code-u-final.appspot.com/";

    //The loader ids for the requests
    public static final int OPCODE_USER_CREATE = 0;
    public static final int OPCODE_USER_LOGIN = 1;
    public static final int OPCODE_LIST_CREATE = 2;

    /**
     * Send the request to the server to create a new user
     * @param context   the context the loader is being created from
     * @param username  the username of the new user
     * @param password  the password of the new user
     * @return          a new loader to perform the request
     */
    public static AsyncTaskLoader<HttpURLConnection> userCreate(Context context,final String username, final String password){

        return new AsyncTaskLoader<HttpURLConnection>(context) {
            @Override
            public HttpURLConnection loadInBackground() {

                //Build the url for creating the user
                Uri url = Uri.parse(BASE_URL).buildUpon()
                        .appendPath("user")
                        .appendPath("create")
                        .appendQueryParameter("username", username)
                        .appendQueryParameter("password", password)
                        .build();

                //Attempt to create the user
                Log.e("URL", url + "");
                HttpURLConnection connection = null;
                try {
                    //Make the request
                    connection = HttpRequest.get(url);
                    connection.getResponseCode();
                } catch (Exception e) {
                    Log.e(LOG_TAG, e.toString());
                }
                //Return the connection to the url
                return connection;
            }
        };
    }

    /**
     * Send the request to the server to login with the provided username and password
     * @param context   the context the loader is being created from
     * @param username  the username of the user
     * @param password  the password of the user
     * @return          a new loader to perform the request
     */
    public static AsyncTaskLoader<HttpURLConnection> userLogin(Context context,final String username, final String password){

        return new AsyncTaskLoader<HttpURLConnection>(context) {
            @Override
            public HttpURLConnection loadInBackground() {

                //Build the url for logging in as the user
                Uri url = Uri.parse(BASE_URL).buildUpon()
                        .appendPath("user")
                        .appendPath("login")
                        .appendQueryParameter("username", username)
                        .appendQueryParameter("password", password)
                        .build();

                //Attempt to login as the user
                HttpURLConnection connection = null;
                try {
                    //Make the request
                    connection = HttpRequest.get(url);
                    connection.getResponseCode();
                } catch (Exception e) {
                    Log.e(LOG_TAG, e.toString());
                }
                //Return the connection to the url
                return connection;
            }
        };
    }

    /**
     * Send the request to the server to login with the provided username and password
     * @param context   the context the loader is being created from
     * @param username  the username of the user
     * @param password  the password of the user
     * @return          a new loader to perform the request
     */
    public static AsyncTaskLoader<HttpURLConnection> listCreate(Context context,final String userKey, final String name){

        return new AsyncTaskLoader<HttpURLConnection>(context) {
            @Override
            public HttpURLConnection loadInBackground() {

                //Build the url for logging in as the user
                Uri url = Uri.parse(BASE_URL).buildUpon()
                        .appendPath("list")
                        .appendPath("create")
                        .appendQueryParameter("user_key", userKey)
                        .appendQueryParameter("name", name)
                        .build();

                //Attempt to login as the user
                HttpURLConnection connection = null;
                try {
                    //Make the request
                    connection = HttpRequest.get(url);
                    connection.getResponseCode();
                } catch (Exception e) {
                    Log.e(LOG_TAG, e.toString());
                }
                //Return the connection to the url
                return connection;
            }
        };
    }
}
