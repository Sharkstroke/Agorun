package com.unipd.fabio.agorun;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.ListIterator;

public class Signi_In_activity extends AppCompatActivity implements DBConnection, View.OnClickListener {

    private String result = "";
    private int connections = 0;
    private Button btn_register;
    private Toast t;// Numero di connessioni provate automaticamente
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signi__in_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Sign In");
        setSupportActionBar(toolbar);

        btn_register = (Button) findViewById(R.id.btn_signin_check);

        btn_register.setOnClickListener(this);


    }


    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.btn_signin_check:
                onClickRegister();
                break;


        }
    }

    public void onClickRegister() {
        progressDialog = new ProgressDialog(Signi_In_activity.this);
        progressDialog.setMessage("Please Wait");
        progressDialog.show();
        connections = 0;
    //    t = Toast.makeText(this, "Stica", Toast.LENGTH_SHORT);
//        t.show();

        final EditText editpass = (EditText) findViewById(R.id.pwd_signin);
        final EditText editpass_ver = (EditText) findViewById(R.id.pwd_signin_ver);

        if (editpass.getText().toString().equals(editpass_ver.getText().toString())) {
            connect();
        } else {
            t = Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT);
            t.show();

        }
    }


    private void connect() {
        connections++;

        //final TextView textView = (TextView) findViewById(R.id.logresult);
        final EditText editlog = (EditText) findViewById(R.id.username_signin);
        final EditText editpass = (EditText) findViewById(R.id.pwd_signin);
        final EditText editpass_ver = (EditText) findViewById(R.id.pwd_signin_ver);
        final EditText editemail = (EditText) findViewById(R.id.email_signin);
        final Spinner spinnersex = (Spinner) findViewById(R.id.sex_signin);
        final Spinner spinnerexp = (Spinner) findViewById(R.id.exp_signin);

        ArrayAdapter<CharSequence> adapterSex = ArrayAdapter.createFromResource(this, R.array.sex, android.R.layout.simple_spinner_item);
        adapterSex.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnersex.setAdapter(adapterSex);

        ArrayAdapter<CharSequence> adapterExp = ArrayAdapter.createFromResource(this, R.array.experience, android.R.layout.simple_spinner_item);
        adapterSex.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerexp.setAdapter(adapterExp);


        if (!editlog.getText().toString().equals("") && !editpass.getText().toString().equals("")
                && !editemail.getText().toString().equals("")) {


            new ConnectDB(this).execute("register", editlog.getText().toString(),
                    editpass.getText().toString(), spinnersex.getSelectedItem().toString(), editemail.getText().toString(),
                    spinnerexp.getSelectedItemPosition()+"");
        } else {
            t = Toast.makeText(this, "Fill in the form", Toast.LENGTH_SHORT);
            t.show();
            //textView.setText("Insert username and password");
        }
    }

    public void onTaskCompleted(ArrayList<String> ls) {

        //final TextView textView = (TextView) findViewById(R.id.username_signin);

        if (connections >= 5) {                     // Provo la connessione 5 volte, altrimenti do errore di connessione
            t = Toast.makeText(this, result, Toast.LENGTH_SHORT);
            t.show();
            return;
        }

        ListIterator it = ls.listIterator();
        while (it.hasNext()) {
      //      t = Toast.makeText(this, "Iterate " + result, Toast.LENGTH_SHORT);
//            t.show();
            result = result + (it.next());
        }


        t = Toast.makeText(this, result, Toast.LENGTH_SHORT);
        t.show();
        progressDialog.dismiss();
       /* if (!result.equals("Login avvenuto con successo") && !result.equals("Wrong Password") &&
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
        }*/

        result = "";

    }

}



