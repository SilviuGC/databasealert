package com.example.pcproject;

import androidx.annotation.NonNull;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Pattern;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
public class Register extends AppCompatActivity {

    Button sign_up_btn;
    EditText first_name_text, last_name_text, email_text_register, password_text_register, confirm_password_text;
    TextView warn_text_register;

    Handler handler;

    FirebaseDatabase users_database;
    DatabaseReference database_reference;

    User user;

    boolean check_mail_var;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        sign_up_btn = findViewById(R.id.signup);
        first_name_text = findViewById(R.id.firstname);
        last_name_text = findViewById(R.id.lastname);
        email_text_register = findViewById(R.id.email);
        password_text_register = findViewById(R.id.pass);
        confirm_password_text = findViewById(R.id.confirmpass);
        warn_text_register = findViewById(R.id.warn);

        user = new User();

        users_database = FirebaseDatabase.getInstance();
        database_reference = users_database.getReference().child("Users");

        sign_up_btn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                dismissKeyboard(Register.this);

                if(check_fields()==true) {

                    check_existing_mail();

                    handler = new Handler();
                    /*
                    Since Firebase is asynchronous, you will need to wait a little bit,
                     depending on your connection, in order to return any value from dataSnapshot().
                     */

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            if(check_mail_var==false) {
                                String fname = first_name_text.getText().toString().trim();
                                String lname = last_name_text.getText().toString().trim();
                                String email = email_text_register.getText().toString().trim();
                                String pass = password_text_register.getText().toString().trim();

                                String password = getCryptoHash(pass, "MD5");


                                user.setFirst_name(fname);
                                user.setLast_name(lname);
                                user.setEmail(email);
                                user.setPassword(password);

                                database_reference.push().setValue(user); //users_database.child("User1").setValue(user);

                                sendEMAIL(email, pass, fname, lname);

                                Intent intent_activity = new Intent(Register.this, Home.class);
                                startActivity(intent_activity);
                                finish();
                            }
                            else{
                                warn_blink();
                                warn_text_register.setText("E-mail is already in use!");
                            }
                        }
                    },1000);
                }
            }
        });
    }

    private boolean check_fields()
    {

        if(first_name_text.getText().toString().trim().length()==0 || last_name_text.getText().toString().trim().length()==0 || email_text_register.getText().toString().trim().length()==0 || password_text_register.getText().toString().trim().length()==0 || confirm_password_text.getText().toString().trim().length()==0) {
            warn_blink();
            warn_text_register.setText("Blank fields detected!");
            return false;
        }

        else if(check_email()==false)
        {
            warn_blink();
            warn_text_register.setText("Invalid email!");
            return false;
        }

        else if(password_text_register.getText().toString().trim().length()<8)
        {
            warn_blink();
            warn_text_register.setText("Your password should be at least 8 characters long!");
            return false;
        }

        else if(password_text_register.getText().toString().trim().contains(" "))
        {
            warn_blink();
            warn_text_register.setText("Your password contains whitespaces!");
            return false;
        }

        else if(!password_text_register.getText().toString().trim().equals(confirm_password_text.getText().toString().trim()))
        {
            warn_blink();
            warn_text_register.setText("Passwords do not match!");
            return false;
        }

        else return true;
    }

    private boolean check_email()
    {
        String regex_mail = "^[a-zA-Z0-9_+&*-]+(?:\\." + "[a-zA-Z0-9_+&*-]+)*@" + "(?:[a-zA-Z0-9-]+\\.)+[a-z" + "A-Z]{2,7}$";
        Pattern pattern_mail = Pattern.compile(regex_mail);

        return pattern_mail.matcher(email_text_register.getText().toString().trim()).matches();
    }

    private void warn_blink()
    {
        if(warn_text_register.length()!=0) {
            handler = new Handler();

            warn_text_register.setVisibility(View.INVISIBLE);

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    warn_text_register.setVisibility(View.VISIBLE);
                }
            }, 200);
        }
    }

    private void check_existing_mail()
    {
        check_mail_var = false;
        /*
        Refresh, ca sa nu ramana check_mail_var = true.
        Din oarecare motiv, daca dadeam valoarea false, inainte de utilizarea functiei, nu mergea.
        Ma gandesc ca e din cauza faptului ca firebase e asincron.
        Daca ii dau valoarea false in functie, merge, desi ar trebui sa fie cam acelasi lucru, dar nu e.
         */

        database_reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                {
                    if((dataSnapshot1.child("email").getValue().toString()).equalsIgnoreCase(email_text_register.getText().toString().trim())) {
                        check_mail_var = true;
                        break;
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

            //System.out.println("HashCode Generated by MD5 is: " + getCryptoHash(inputText, "MD5"));
            //System.out.println("HashCode Generated by SHA-1 is: " + getCryptoHash(inputText, "SHA-1"));
            //System.out.println("HashCode Generated by SHA-256 is: " + getCryptoHash(inputText, "SHA-256"));
            //System.out.println("HashCode Generated by SHA-512 is: " + getCryptoHash(inputText, "SHA-512"));

    private void sendEMAIL(String email, String password, String receiver_first_name, String receiver_last_name){
        String subject = "New DatabaseAlert account.";
        String message = "Dear " + receiver_first_name + " " + receiver_last_name + "," + "\n\nYour DatabaseAlert account has been successfully created!\n\nE-mail: " + email + "\nPassword: " + password + "\n\n\n(This is an automated message. Please do not reply.)";

        //send email
        JavaMailAPI javaMailAPI = new JavaMailAPI(this, email, subject, message);

        javaMailAPI.execute();
    }

}
