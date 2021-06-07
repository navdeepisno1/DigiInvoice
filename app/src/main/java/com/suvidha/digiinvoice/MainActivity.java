package com.suvidha.digiinvoice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReferenceApp,databaseReferenceInvoice;
    String pwdApp,pwdDBMS;
    ProgressDialog progressDialog;
    Context context = this;
    EditText editText_pwd;
    Button button_login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        editText_pwd = findViewById(R.id.main_et_pwd);
        button_login = findViewById(R.id.main_btn_login);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReferenceApp = firebaseDatabase.getReference("App Pwd");
        databaseReferenceInvoice = firebaseDatabase.getReference("Invoice Pwd");
        progressDialog = ProgressDialog.show(context,"Please Wait","Loading...",true,false);
        databaseReferenceApp.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pwdApp = snapshot.getValue(String.class);
                Log.e("tag",pwdApp);
                databaseReferenceInvoice.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        pwdDBMS = snapshot.getValue(String.class);
                        Log.e("tag",pwdDBMS);
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        //startSplash();

        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!editText_pwd.getText().toString().trim().isEmpty())
                {
                    if(editText_pwd.getText().toString().trim().equals(pwdApp))
                    {
                        Intent intent = new Intent(MainActivity.this,Invoices.class);
                        intent.putExtra("pwdDBMS",pwdDBMS);
                        startActivity(intent);
                        finish();
                    }
                    else
                    {
                        editText_pwd.setText("");
                    }
                }
            }
        });

    }
    private void startSplash()
    {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(MainActivity.this,Invoices.class));
                finish();
            }
        },1000);
    }

    private boolean checkConnection()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;
    }
}