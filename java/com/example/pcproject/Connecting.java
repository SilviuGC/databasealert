//coae EEEEE
package com.example.pcproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
public class Connecting extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connecting);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, Login.class));
        finish();
    }
}
