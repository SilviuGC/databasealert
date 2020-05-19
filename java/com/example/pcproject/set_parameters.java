package com.example.pcproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class set_parameters extends AppCompatActivity {

    TextView current_value_string;
    EditText new_value_edittext;
    Button set_new_value_button, log_out_button;

    public static final String EXTRA_NUMBER_NEW_VALUE = "com.example.pcproject.set_parameters.EXTRA_NUMBER_NEW_VALUE";
    public static final String EXTRA_BOOL_CANCEL_AUTO_SIGN_IN = "com.example.pcproject.set_parameters.EXTRA_NUMBER_NEW_VALUE.EXTRA_CANCEL_AUTO_SIGN_IN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_parameters);

        set_new_value_button = findViewById(R.id.set);
        log_out_button = findViewById(R.id.log_out);
        new_value_edittext = findViewById(R.id.edit_value);
        current_value_string = findViewById(R.id.current_value_text);

        Intent intent = getIntent();
        current_value_string.setText(intent.getStringExtra(Home.EXTRA_TEXT));

        set_new_value_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openHomeActivity();
            }
        });

        log_out_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
               openLoginActivity();
            }
        });
    }

    private void openHomeActivity(){
        Intent intent = new Intent(set_parameters.this, Home.class);
        intent.putExtra(EXTRA_NUMBER_NEW_VALUE, Integer.parseInt(new_value_edittext.getText().toString().trim()));
        startActivity(intent);
        finish();
    }

    private void openLoginActivity(){
        Intent intent = new Intent(set_parameters.this, Login.class);
        intent.putExtra(EXTRA_BOOL_CANCEL_AUTO_SIGN_IN, false);
        startActivity(intent);
        finish();
    }
}
