package com.example.pcproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Pattern;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
public class Login extends AppCompatActivity {

    TextView txt, warn_text_login;
    EditText email_text_login, password_text_login;
    Button sign_in_button;
    CheckBox remember_me_checkbox;

    Handler handler;

    FirebaseDatabase main_database;
    DatabaseReference users_database_reference;

    boolean check_user_var;

    public static final String EXTRA_TEXT_EMAIL = "com.example.pcproject.Login.EXTRA_TEXT_EMAIL";

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String RM_EMAIL = "EMAIL";
    public static final String RM_PASSWORD = "PASSWORD";
    public static final String RM_CHECKBOX = "CHECKBOX";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sign_in_button = findViewById(R.id.signup);

        remember_me_checkbox = findViewById(R.id.remember_me_checkbox);

        main_database =FirebaseDatabase.getInstance();
        users_database_reference = main_database.getReference().child("Users");

        txt = findViewById(R.id.view);
        warn_text_login = findViewById(R.id.warn);
        email_text_login = findViewById(R.id.mail);
        password_text_login = findViewById(R.id.pass);

        remember_me_checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 checking_the_checkbox_false();
            }
        });

        txt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
            }
         });

        sign_in_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dismissKeyboard(Login.this);

                if(check_fields()==true) {

                    check_user();

                    handler = new Handler();
                    /*
                    Since Firebase is asynchronous, you will need to wait a little bit,
                     depending on your connection in order to return any value from dataSnapshot().
                     */

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (check_user_var == true) {
                                checking_the_checkbox_true();
                                openHomeActivity();
                            }
                            else {
                                warn_blink();
                                warn_text_login.setText("User not found! Check email or password.");
                            }
                        }
                    },1000);
                }
            }
        });

        loadRememberMeData();

        if(check_first_start_of_activity()==true)
            rememberMeAutoSignIn();
    }

    private boolean check_fields()
    {
        if(email_text_login.getText().toString().trim().length()==0 || password_text_login.getText().toString().trim().length()==0) {
            warn_blink();
            warn_text_login.setText("Blank fields detected!");
            return false;
        }

        else if(check_email()==false)
        {
            warn_blink();
            warn_text_login.setText("Invalid email!");
            return false;
        }

        else if(password_text_login.getText().toString().trim().length()<8)
        {
            warn_blink();
            warn_text_login.setText("Your password should be at least 8 characters long!");
            return false;
        }

        else if(password_text_login.getText().toString().trim().contains(" "))
        {
            warn_blink();
            warn_text_login.setText("Your password contains whitespaces!");
            return false;
        }

        else return true;
    }

    private boolean check_email()
    {
        String regex_mail = "^[a-zA-Z0-9_+&*-]+(?:\\." + "[a-zA-Z0-9_+&*-]+)*@" + "(?:[a-zA-Z0-9-]+\\.)+[a-z" + "A-Z]{2,7}$";
        Pattern pattern_mail = Pattern.compile(regex_mail);

        return pattern_mail.matcher(email_text_login.getText().toString().trim()).matches();
    }

    private void warn_blink()
    {
        if(warn_text_login.length()!=0) {
            handler = new Handler();

            warn_text_login.setVisibility(View.INVISIBLE);

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    warn_text_login.setVisibility(View.VISIBLE);
                }
            }, 200);
        }
    }

    private void check_user()
    {
        check_user_var=false;

        users_database_reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                {
                    if((dataSnapshot1.child("email").getValue().toString()).equalsIgnoreCase(email_text_login.getText().toString().trim())) {
                        String pass = password_text_login.getText().toString().trim();
                        if ((dataSnapshot1.child("password").getValue().toString()).equals(getCryptoHash(pass, "MD5"))) {
                            check_user_var = true;
                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }

        });
    }

    public void dismissKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (null != activity.getCurrentFocus())
            imm.hideSoftInputFromWindow(activity.getCurrentFocus()
                    .getApplicationWindowToken(), 0);
    }


    //Java program to calculate MD5,SHA-1,SHA-256 and SHA-512 hash values

    //Function to calculate the Cryptographic hash value of an input String and the provided Digest algorithm
    public String getCryptoHash(String input, String algorithm) {
        try {
            //MessageDigest classes Static getInstance method is called with MD5 hashing
            MessageDigest msgDigest = MessageDigest.getInstance(algorithm);

            //digest() method is called to calculate message digest of the input
            //digest() return array of byte.
            byte[] inputDigest = msgDigest.digest(input.getBytes());

            // Convert byte array into signum representation
            // BigInteger class is used, to convert the resultant byte array into its signum representation
            BigInteger inputDigestBigInt = new BigInteger(1, inputDigest);

            // Convert the input digest into hex value
            String hashtext = inputDigestBigInt.toString(16);

            //Add preceding 0's to pad the hashtext to make it 32 bit
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        }
        // Catch block to handle the scenarios when an unsupported message digest algorithm is provided.
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private void openHomeActivity(){
        Intent intent = new Intent(Login.this, Home.class);
        intent.putExtra(EXTRA_TEXT_EMAIL, email_text_login.getText().toString().trim());
        startActivity(intent);
        finish();
    }

    private void loadRememberMeData(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

        email_text_login.setText(sharedPreferences.getString(RM_EMAIL, ""));
        password_text_login.setText(sharedPreferences.getString(RM_PASSWORD, ""));
        remember_me_checkbox.setChecked(sharedPreferences.getBoolean(RM_CHECKBOX, false));
    }


    private void rememberMeAutoSignIn(){
       if(remember_me_checkbox.isChecked()){
               handler = new Handler();

               handler.postDelayed(new Runnable() {
                   @Override
                   public void run() {
                       openHomeActivity();
                   }
               },1000);
           }
       }

       private void checking_the_checkbox_true(){
           if(remember_me_checkbox.isChecked()==true){
               SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE); // MODE_PRIVATE = NO OTHER APP CAN CHANGE THE PREFERENCES
               SharedPreferences.Editor editor = sharedPreferences.edit();

               editor.putString(RM_EMAIL, email_text_login.getText().toString().trim());
               editor.putString(RM_PASSWORD, password_text_login.getText().toString().trim());
               editor.putBoolean(RM_CHECKBOX, remember_me_checkbox.isChecked());

               editor.apply();
           }
       }
    private void checking_the_checkbox_false(){
        if(remember_me_checkbox.isChecked()==false){
            SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE); // MODE_PRIVATE = NO OTHER APP CAN CHANGE THE PREFERENCES
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putString(RM_EMAIL, "");
            editor.putString(RM_PASSWORD, "");
            editor.putBoolean(RM_CHECKBOX, remember_me_checkbox.isChecked());

            editor.apply();
        }
    }

       private boolean check_first_start_of_activity(){
        Intent intent = getIntent();
        return intent.getBooleanExtra(set_parameters.EXTRA_BOOL_CANCEL_AUTO_SIGN_IN,true);
       }
}
