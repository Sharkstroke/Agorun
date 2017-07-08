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

/* Classe per la connessione al Database
 * Prende una lista di stringhe (param) in cui il primo elemento è il tipo di query
 * Ritorna una lista di stringhe contenenti tutte le righe emesse dalla query al database
 *
 * Ogni classe che voglia fare una query al DB deve implementare l'interfaccia DBConnection
 */


public class ConnectDB extends AsyncTask<String, Void, ArrayList<String>> {

    private static String user;        /////  INSERITO DALL'UTENTE
    private DBConnection activity;

    private final String urldb = "https://www.agorun.com/database/";

    public ConnectDB(String user) {
        this.user = user;
    }

    public static String getUser() {
        return user;
    }

    public static void setUser(String email) {
        user = email;
    }

    public ConnectDB(DBConnection activity) {
        this.activity = activity;
    }



    protected ArrayList<String> doInBackground(String... param) {

        ArrayList<String> output = new ArrayList<String>();

        try {

            HttpsURLConnection conn;
            URL url = new URL(urldb);

            switch (param[0]) {
                case "login":

                    if (param.length != 3) {
                        output.add("Il login richiede due parametri: username e password");
                        return output;
                    } else {

                        user = param[1];
                        url = new URL(urldb + "login.php");
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

                // TODO: Aggiustare url di register quando viene aggiustata l'attivita'

                case "register":

                    if (param.length != 6) {
                        output.add("La registrazione richiede cinque parametri");
                        return output;
                    } else {

                        user = param[4];
                        url = new URL(urldb + "register.php");
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
                        url = new URL(urldb + "createrun.php?" +
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

                case "joinrun":
                    if (param.length != 2) {
                        output.add ("La join di un'attività richiede 2 parametri");
                        return output;
                    } else {
                        url = new URL(urldb + "joinrun.php?" +
                                "user=" + user + "&" +
                                "sid=" + param[1]);
                    }
                    break;

                case "getruns":
                    if (param.length != 6) {
                        output.add("La get delle attività richiede 5 parametri");
                        return output;
                    } else {
                        url = new URL(urldb + "getruns.php?" +
                                "user=" + user + "&" +
                                "latne=" + param[1] +"&" +
                                "lngne=" + param[2] + "&" +
                                "latsw=" + param[3] + "&" +
                                "lngsw=" + param[4] + "&" +
                                "sids="  + param[5]);
                        output.add("getruns");
                        //System.out.println("PATH = " + url.toString());
                    }
                    break;

                case "getinforun":
                    url = new URL(urldb + "getinforun.php?sid=" + param[1]);
                    output.add("getinforun");
                    break;
                ////  TODO: da togliere forse!!!
                case "uploadimage":

                    if (param.length != 1) {
                        output.add("La send dell'immagine richiede 1 parametro");
                    } else {
                        url = new URL(urldb + "uploadimage.php?user=" + user);
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
                    }
                    break;

                case "getimage":
                    url = new URL(urldb + "getimage.php?user=" + user);
                    break;

                case "settrack":

                    if (param.length != 3) {
                        output.add("La set della track richiede 3 parametri");
                        return output;
                    } else {
                        output.add("settrack");
                        url = new URL(urldb + "settrack.php?sid=" + param[1]);

                        conn = (HttpsURLConnection) url.openConnection();
                        conn.setDoOutput(true);
                        conn.setRequestProperty("charset", "UTF-8");
                        conn.setUseCaches(false);

                        OutputStream wr = conn.getOutputStream();
                        wr.write(("track=" + param[2])
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

                case "getmessage":
                    if (param.length != 1) {
                        output.add("Richiedere il messaggio richiede 1 parametro");
                        return output;
                    } else {
                        output.add("getmessage");
                        url = new URL(urldb + "getmessage.php?user=" + user);
                    }
                    break;

                case "getinfouser":
                    if (param.length != 1) {
                        output.add("Richiedere info sull'utente richiede 1 parametro");
                    } else {
                        url = new URL(urldb + "getinfouser.php?user=" + user);
                    }
                    break;

                case "getnamerank":
                    if (param.length != 1) {
                        output.add("Richiedere dati per la leaderboard richiede 1 parametro");
                        return output;
                    } else {
                        url = new URL(urldb + "getnamerank.php");
                    }
                    break;
                case "increaserank":
                    if (param.length != 1) {
                        output.add("Incrementare il rank richiede 1 parametro");
                        return output;
                    } else {
                        url = new URL(urldb + "increaserank.php?user=" + user);
                        output.add("increaserank");
                    }
                    break;

            }

            conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("GET");           // VEDERE SE MODIFICARE

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String line;

            while ((line = in.readLine()) != null) {
                //System.out.println("LINE: "+line);
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
            //System.out.println(it.next());
        }
    }
}
