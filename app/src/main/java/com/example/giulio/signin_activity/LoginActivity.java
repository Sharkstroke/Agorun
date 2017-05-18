package com.example.giulio.signin_activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.ListIterator;

class LoginActivity extends AppCompatActivity implements DBConnection, View.OnClickListener{



    private String result = "";
    private int connections = 0;
    private Button btn_login;
    private Toast t;// Numero di connessioni provate automaticamente
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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

    public void onClickLogin () {
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage("Please Wait");
        progressDialog.show();
        connections = 0;
        connect();
    }


    private void connect() {
        connections++;

        //final TextView textView = (TextView) findViewById(R.id.logresult);
        final EditText editlog = (EditText) findViewById(R.id.username);
        final EditText editpass = (EditText) findViewById(R.id.password);



        if (! editlog.getText().toString().equals("") && ! editpass.getText().toString().equals("")) {

            t = Toast.makeText(this,"Insert username and password",Toast.LENGTH_SHORT);
            new ConnectDB(this).execute("login", editlog.getText().toString(),
                    editpass.getText().toString());
        } else {
            t = Toast.makeText(this,"Insert username and password",Toast.LENGTH_SHORT);
            //textView.setText("Insert username and password");
        }
    }
    public void onTaskCompleted (ArrayList<String> ls) {

        final TextView textView = (TextView) findViewById(R.id.username);

        if (connections >= 5) {                     // Provo la connessione 5 volte, altrimenti do errore di connessione
            t = Toast.makeText(this,result,Toast.LENGTH_SHORT);
            return;
        }

        ListIterator it = ls.listIterator();
        while (it.hasNext()) {
            result = result + (it.next());
        }


        if (!result.equals("Login avvenuto con successo") && !result.equals("Wrong Password") &&
                !result.equals("Registrati")) {
            result = "";
            t = Toast.makeText(this,result,Toast.LENGTH_SHORT);
            t.show();
            connect();
        } else {
            t = Toast.makeText(this,result,Toast.LENGTH_SHORT);
            t.show();
            //textView.setText(result);
            if (progressDialog.isShowing()) { progressDialog.dismiss(); }
        }

        result = "";

    }

}
