package com.unipd.fabio.agorun;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.ListIterator;

public class LoginActivity extends AppCompatActivity implements DBConnection, View.OnClickListener {


    private String result = "";
    private int connections = 0;
    private Button btn_login;
    private Toast t;// Numero di connessioni provate automaticamente
    private ProgressDialog progressDialog;
    private SharedPreferences prefs;
    private EditText editlog;
    private EditText editpass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Log In");
        setSupportActionBar(toolbar);


        btn_login = (Button) findViewById(R.id.btn_login_verify);

        btn_login.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.btn_login_verify:
                onClickLogin();
                break;


        }
    }

    public void onClickLogin() {
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage("Please Wait");
        progressDialog.show();
        connections = 0;
        connect();
    }


    private void connect() {
        connections++;

        //final TextView textView = (TextView) findViewById(R.id.logresult);
        editlog = (EditText) findViewById(R.id.username_login);
        editpass = (EditText) findViewById(R.id.password_login);


        if (!editlog.getText().toString().equals("") && !editpass.getText().toString().equals("")) {

            t = Toast.makeText(this, "Insert username and password", Toast.LENGTH_SHORT);
            new ConnectDB(this).execute("login", editlog.getText().toString(),
                    editpass.getText().toString());
        } else {
            t = Toast.makeText(this, "Insert username and password", Toast.LENGTH_SHORT);
            //textView.setText("Insert username and password");
        }
    }

    public void onTaskCompleted(ArrayList<String> ls) {

        final TextView textView = (TextView) findViewById(R.id.username_login);

        if (connections >= 5) {                     // Provo la connessione 5 volte, altrimenti do errore di connessione
            t = Toast.makeText(this, result, Toast.LENGTH_SHORT);
            return;
        }

        ListIterator it = ls.listIterator();
        while (it.hasNext()) {
            result = result + (it.next());
        }


        if (!result.equals("Login avvenuto con successo") && !result.equals("Wrong Password") &&
                !result.equals("Registrati")) {
            result = "";
            t = Toast.makeText(this, result, Toast.LENGTH_SHORT);
            t.show();
            connect();
        } else if (result.equals("Login avvenuto con successo")) {


            prefs = getSharedPreferences("UserData", 0);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("username", editlog.getText().toString());
            editor.putString("password", editpass.getText().toString());
            editor.commit();
            startAccountActivity();
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

        } else {

            t = Toast.makeText(this, result, Toast.LENGTH_SHORT);
            t.show();
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

        }

        result = "";

    }

    private void startAccountActivity() {
        Intent myIntent = new Intent(LoginActivity.this, MapsActivity.class);
        //myIntent.putExtra("key", value); //Optional parameters
        LoginActivity.this.startActivity(myIntent);
    }


}
