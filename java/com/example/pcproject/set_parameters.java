package com.example.pcproject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class set_parameters extends AppCompatActivity {

    TextView current_value_string, warn_text_parameter;
    EditText new_value_edittext;
    Button set_new_value_button, log_out_button;

    Handler handler;

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
        warn_text_parameter = findViewById(R.id.warn);

        Intent intent = getIntent();
        current_value_string.setText(intent.getStringExtra(Home.EXTRA_TEXT));

        set_new_value_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dismissKeyboard(set_parameters.this);

                if(check_fields() == true)
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

    private boolean check_fields() {
        if (new_value_edittext.getText().toString().trim().length() == 0) {
            warn_blink();
            warn_text_parameter.setText("Blank fields detected!");
            return false;
        }

        return true;
    }

    private void warn_blink()
    {
        if(warn_text_parameter.length()!=0) {
            handler = new Handler();

            warn_text_parameter.setVisibility(View.INVISIBLE);

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    warn_text_parameter.setVisibility(View.VISIBLE);
                }
            }, 200);
        }
    }

    public void dismissKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (null != activity.getCurrentFocus())
            imm.hideSoftInputFromWindow(activity.getCurrentFocus()
                    .getApplicationWindowToken(), 0);
    }
}
