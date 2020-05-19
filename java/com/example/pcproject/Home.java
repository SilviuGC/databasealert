package com.example.pcproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Home extends AppCompatActivity {

    Button button,button2,button3;

    int notification_id_counter, group_notification_id, constant_value;
    String name, pulse, names_n_pulses;
    boolean value_alert;

    public static final String EXTRA_TEXT = "com.example.pcproject.Home.EXTRA_TEXT";
   // public static final String EXTRA_NUMBER = "com.example.pcproject.Login.EXTRA_NUMBER";

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String REMEMBER_CONSTANT_VALUE = "CONSTANT_VALUE";

    databasehelper myDb;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference patients_database_reference;

    FirebaseRecyclerOptions<Post> options;
    FirebaseRecyclerAdapter<Post, MyRecyclerViewHolder>adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        button= findViewById(R.id.visualizedata);
        button2= findViewById(R.id.visualizegraph);
        button3= findViewById(R.id.setparameters);

        myDb = new databasehelper(this);

        notification_id_counter = 1;
        group_notification_id = 0;

        give_value_to_constant();

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                startActivity(new Intent(Home.this,Datav.class));
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                startActivity(new Intent(Home.this,Grafv.class));
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openSetParametersActivity();
            }
        });

        firebaseDatabase = FirebaseDatabase.getInstance();
        patients_database_reference = firebaseDatabase.getReference("EDMT_FIREBASE");

        patients_database_reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                addData();

                value_alert = false;

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    if (Integer.parseInt(dataSnapshot1.child("puls").getValue().toString().trim()) > constant_value) {
                        name = dataSnapshot1.child("nume").getValue().toString().trim();
                        pulse = dataSnapshot1.child("puls").getValue().toString().trim();
                        popNotification(name,pulse);

                        if(names_n_pulses == null)
                            names_n_pulses = "\nName: " + name + "\nPulse: " + pulse + "\n";
                        else
                            names_n_pulses = names_n_pulses + "\nName: " + name + "\nPulse: " + pulse + "\n";

                        value_alert = true;

                       // button.setText(constant_value+" "+value_alert);
                    }
                }

                //comment this if you don't wanna get mail from notifications @@@@@@@@@@@@@@@@@@@@@@
                if(value_alert == true)
                {
                    Intent intent = getIntent();
                    sendEMAIL(intent.getStringExtra(Login.EXTRA_TEXT_EMAIL), names_n_pulses);
                    names_n_pulses="";
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        loadConstantValueData();
    }
    @Override
    protected void onStart() {
        if(adapter != null)
            adapter.stopListening();
        super.onStart();
    }

    private void popNotification(String notif_name, String notif_pulse)
    {
        //Constructorul de notificare. Aici construim notificarea si definim continutul notificarii.
        NotificationCompat.Builder notification_builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle("DatabaseAlert: Exceeded value!")
                //Line breaks are ignored in setContentText(), as it is condensed.
                .setStyle(new NotificationCompat.InboxStyle()
                        .addLine("Name: "+notif_name)
                        .addLine("Pulse: "+notif_pulse)
                )
               // .setPriority(Notification.PRIORITY_HIGH)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC) //Arata notificarile pe lockscreen.
                .setGroup("DatabaseAlert Notifications")
                .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_ALL);

        //Constructorul de notificare in care se va introduce un grup de notificari.
        NotificationCompat.Builder notification_group_builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setStyle(new NotificationCompat.InboxStyle()
                        .setSummaryText("You've got new alerts")
                )
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC) //Arata notificarile pe lockscreen.
                .setGroup("DatabaseAlert Notifications")
                .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_ALL)
                .setGroupSummary(true); //Aceasta linie da tipul "grup" unei notificari.

        //Vibration
        notification_builder.setVibrate(new long[] { 500, 500, 500, 500, 500, 500 });

        //LED
        notification_builder.setLights(0xff00ff00, 300, 300);

        //Tone
        notification_builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        //Intent pentru notificare. La apasarea notificarii, aceasta ne va duce la o activitate specificata (notificarea merge si fara intent).
        //Intent = un obiect de tip mesaj prin care putem defini o actiune catre alta componenta a aplicatiei.
        Intent notification_intent = new Intent(this, Grafv.class);
        PendingIntent notification_content_intent = PendingIntent.getActivity(this, 0, notification_intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification_builder.setContentIntent(notification_content_intent);

        //Preia serviciul sistemului, adica notificarea, si afiseaz-o.
        NotificationManager notification_manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notification_manager.notify(notification_id_counter, notification_builder.build());

        //De la a treia notificare, afiseaza notificarea de tip grup + introdu notificarile, care sunt si care vor urma, in grup.
        //Prima oara se va afisa notificarea pe ecran (automat) si dupa ce va disparea se va introduce in grup.
        if(notification_id_counter >= 3)
            notification_manager.notify(group_notification_id, notification_group_builder.build());

        ++notification_id_counter;
    }

    //adauga numele si pulsul intr-o baza de date interna
    public void addData()
    {
        patients_database_reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        name = dataSnapshot1.child("nume").getValue().toString().trim();
                        pulse = dataSnapshot1.child("puls").getValue().toString().trim();
                        myDb.insertData(pulse, name);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void sendEMAIL(String email, String names_n_pulses){
        String subject = "DatabaseAlert: Exceeded value!";
        String message = "The constant value has been exceeded!\n" + names_n_pulses  + "\n\n(This is an automated message. Please do not reply.)";

        //send email
        JavaMailAPI javaMailAPI = new JavaMailAPI(this, email, subject, message);

        javaMailAPI.execute();
    }

    private void openSetParametersActivity(){
        Intent intent = new Intent(Home.this, set_parameters.class);
        intent.putExtra(EXTRA_TEXT, String.valueOf(constant_value));
        startActivity(intent);
        value_alert=false;
    }

    private void give_value_to_constant(){
        Intent intent = getIntent();
        if(constant_value != intent.getIntExtra(set_parameters.EXTRA_NUMBER_NEW_VALUE, constant_value)) {
            constant_value = intent.getIntExtra(set_parameters.EXTRA_NUMBER_NEW_VALUE, constant_value);

            SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putInt(REMEMBER_CONSTANT_VALUE, constant_value);

            editor.apply();
        }
    }

    private void loadConstantValueData(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

        constant_value = sharedPreferences.getInt(REMEMBER_CONSTANT_VALUE, 100);
    }

}
