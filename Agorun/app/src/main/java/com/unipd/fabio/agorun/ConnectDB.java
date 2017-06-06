package com.unipd.fabio.agorun;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.ListIterator;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by riccardo on 13/05/17.
 */

public class ConnectDB extends AsyncTask<String, Void, ArrayList<String>> {

    private static String user;        /////  INSERITO DALL'UTENTE
    private DBConnection activity;

    public ConnectDB(String user) {
        this.user = user;
    }

    public String getUser() {
        return user;
    }

    public static  void setUser(String email) {
        user = email;
    }

    public ConnectDB(DBConnection activity) {
        this.activity = activity;
    }



    protected ArrayList<String> doInBackground(String... param) {

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

                    if (param.length != 6) {
                        output.add("La registrazione richiede cinque parametri");
                        return output;
                    } else {

                        user = param[4];
                        url = new URL("https://mprogramming.000webhostapp.com/register_MODIFICA.php");
                        conn = (HttpsURLConnection) url.openConnection();
                        conn.setDoOutput(true);
                        conn.setRequestProperty("charset", "UTF-8");
                        conn.setUseCaches(false);

                        OutputStream wr = conn.getOutputStream();
                        wr.write(("user=" + param[1] +
                                "&pass=" + param[2] +
                                "&sex=" + param[3] +
                                "&email=" + param[4] +
                                "&exp=" + param[5])
                                .getBytes());
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

                case "createrun":
                    if (param.length != 8) {
                        output.add ("La creazione di un'attività richiede 8 parametri");
                        return output;
                    } else {
                        url = new URL("https://mprogramming.000webhostapp.com/createrun.php?" +
                                "user="    + user     + "&" +
                                "stlat="   + param[1] + "&" +
                                "stlong="  + param[2] + "&" +
                                "endlat="  + param[3] + "&" +
                                "endlong=" + param[4] + "&" +
                                "length="  + param[5] + "&" +
                                "diff="    + param[6] + "&" +
                                "datetime="+ param[7]);
                    }
                    break;

                case "getruns":
                    if (param.length != 3) {
                        output.add("La get delle attività richiede 3 parametri");
                        return output;
                    } else {
                        url = new URL("https://mprogramming.000webhostapp.com/getruns.php?" +
                                "user=" + user + "&" +
                                "lat=" + param[1] +"&" +
                                "lng=" + param[2]);
                        output.add("getruns");
                        System.out.println("PATH = " + url.toString());
                    }
                    break;

                case "getinforun":
                    url = new URL("https://mprogramming.000webhostapp.com/getinforun.php?sid=" + param[1]);
                    output.add("getinforun");
                    break;
                ////  TODO: da togliere!!!
                case "uploadimage":
                    url = new URL("https://mprogramming.000webhostapp.com/uploadimage.php?user=" + user);
                    conn = (HttpsURLConnection) url.openConnection();
                    conn.setDoOutput(true);
                    conn.setRequestProperty("charset", "UTF-8");
                    conn.setUseCaches(false);

                    OutputStream wr = conn.getOutputStream();
                    wr.write(("encodedstring=" + param[1])
                            .getBytes());
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
                    break;

                case "getimage":
                    url = new URL("https://mprogramming.000webhostapp.com/getimage.php?user=" + user);
                    break;
            }

            conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("GET");           // VEDERE SE MODIFICARE

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String line;

            while ((line = in.readLine()) != null) {
                System.out.println("LINE: "+line);
                output.add(line);
            }
            in.close();

        } catch (IOException e) {
            output.add(e.getMessage());
        }
        return output;
    }

    protected void onPostExecute(ArrayList<String> result) {
        activity.onTaskCompleted(result);
    }

    public void print(ArrayList<String> ls) {
        ListIterator it = ls.listIterator();
        while (it.hasNext()) {
            System.out.println(it.next());
        }
    }
}
