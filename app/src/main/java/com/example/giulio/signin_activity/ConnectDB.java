package com.example.giulio.signin_activity;

import android.os.AsyncTask;

import java.net.*;
import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.ListIterator;

/**
 * Created by riccardo on 13/05/17.
 */

public class ConnectDB extends AsyncTask<String,Void,ArrayList<String> >{

    private static String user;        /////  INSERITO DALL'UTENTE
    private DBConnection activity;

    public ConnectDB (String user) {
        this.user = user;
    }

    public String getUser() { return user; }

    public ConnectDB(DBConnection activity) {
        this.activity = activity;
    }

    protected ArrayList<String> doInBackground (String... param) {

        ArrayList<String> output = new ArrayList<String>();

        try {

            HttpsURLConnection conn;
            URL url = new URL("https://mprogramming.000webhostapp.com/");

            switch (param[0]) {
                case "login":

                    if (param.length != 3) {
                        output.add("Il login richiede due parametri: username e password");
                        return output;
                    } else {

                        user = param[1];
                        url = new URL("https://mprogramming.000webhostapp.com/login_MODIFICA.php");
                        conn = (HttpsURLConnection) url.openConnection();
                        conn.setDoOutput(true);
                        conn.setRequestProperty("charset", "UTF-8");
                        conn.setUseCaches(false);

                        OutputStream wr = conn.getOutputStream();
                        wr.write(("user=" + user + "&pass=" + param[2]).getBytes());
                        wr.flush();
                        wr.close();

                        int responseCode = conn.getResponseCode();

                        if (responseCode == HttpURLConnection.HTTP_OK) {
                            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                            String line;

                            while ((line = in.readLine()) != null) {
                                output.add(line);
                            }
                            in.close();
                        } else {
                            output.add("Post connection failed");
                        }
                    }

                    return output;

                case "register":

                    if (param.length != 4) {
                        output.add("La registrazione richiede tre parametri: username, password e sesso");
                        return output;
                    } else {

                        user = param[1];
                        url = new URL("https://mprogramming.000webhostapp.com/register_MODIFICA.php");
                        conn = (HttpsURLConnection) url.openConnection();
                        conn.setDoOutput(true);
                        conn.setRequestProperty("charset", "UTF-8");
                        conn.setUseCaches(false);

                        OutputStream wr = conn.getOutputStream();
                        wr.write(("user=" + user + "&pass=" + param[2] + "&sex=" + param[3]).getBytes());
                        wr.flush();
                        wr.close();

                        int responseCode = conn.getResponseCode();

                        if (responseCode == HttpURLConnection.HTTP_OK) {
                            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                            String line;

                            while ((line = in.readLine()) != null) {
                                output.add(line);
                            }
                            in.close();
                        } else {
                            output.add("Post connection failed");
                        }
                    }

                    return output;
            }

            conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("GET");           // VEDERE SE MODIFICARE

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String line;

            while ((line = in.readLine()) != null) {
                output.add(line);
            }
            in.close();

        } catch (IOException e) { output.add(e.getMessage()); }
        return output;
    }

    protected void onPostExecute (ArrayList<String> result) {
        activity.onTaskCompleted(result);
    }

    public void print (ArrayList<String> ls) {
        ListIterator it = ls.listIterator();
        while (it.hasNext()) {
            System.out.println (it.next());
        }
    }
}
