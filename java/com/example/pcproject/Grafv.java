package com.example.pcproject;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class Grafv extends AppCompatActivity implements OnChartValueSelectedListener  {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseRecyclerOptions<Post> options;
    FirebaseRecyclerAdapter<Post, MyRecyclerViewHolder>adapter;
    databasehelper myDb;
    List<String> arrayList = new ArrayList<>();
    Button clear,foo;
    private LineChart chart;
    private Spinner spinner;
    Random r = new Random();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grafv);

        myDb = new databasehelper(this);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("EDMT_FIREBASE");

        clear = findViewById(R.id.clear);
        clear.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                myDb.clearDatabase("pulse_table");
                Intent intent = new Intent(Grafv.this, Home.class);
                startActivity(intent);
                //finish();
            }
        });
        foo = findViewById(R.id.button);
        foo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                final Handler ha=new Handler();
                ha.postDelayed(new Runnable() {

                    @Override
                    public void run() {

                        databaseReference.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot ds, @Nullable String s) {
                                int low = 50;
                                int high = 121;
                                int result = r.nextInt(high-low) + low;
                                ds.child("puls").getRef().setValue(String.valueOf(result).trim());
                            }

                            @Override
                            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                            }

                            @Override
                            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                            }

                            @Override
                            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                        ha.postDelayed(this, 10000);
                    }
                }, 10000);


            }
        });


        Cursor cursor = myDb.getAllDatac();
        while(cursor.moveToNext()) arrayList.add(cursor.getString(2));
        Set<String> set = new HashSet<>(arrayList);
        arrayList.clear();
        arrayList.addAll(set);

        spinner = findViewById(R.id.spinner1);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, arrayList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String p = (String) spinner.getSelectedItem();
                setChart();
                fct(p);

                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
                    @Override
                    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
                        String persoana = (String) arg0.getSelectedItem();

                        setChart();
                        fct(persoana);
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                        // TODO Auto-generated method stub
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
        setChart();
    }

    private LineDataSet createSet(){
        LineDataSet set = new LineDataSet(null, "");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(Color.BLACK);
        set.setCircleColor(Color.BLACK);
        set.setLineWidth(2f);
        set.setCircleRadius(4f);
        set.setFillAlpha(65);
        set.setFillColor(Color.BLACK);
        set.setHighLightColor(Color.rgb(244, 117, 117));
        set.setValueTextColor(Color.BLACK);
        set.setValueTextSize(9f);
        set.setDrawValues(false);
        return set;
    }

    private void setChart(){

        chart = findViewById(R.id.chart);
        chart.setOnChartValueSelectedListener(this);                // enable description text
        chart.getDescription().setEnabled(true);

        // enable touch gestures
        chart.setTouchEnabled(true);

        // enable scaling and dragging
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setDrawGridBackground(false);

        // if disabled, scaling can be done on x- and y-axis separately
        chart.setPinchZoom(true);

        // set an alternative background color
        chart.setBackgroundColor(Color.TRANSPARENT);

        // add empty data
        chart.setData(new LineData());
        chart.getLineData().setValueTextColor(Color.BLACK);

        chart.getDescription().setText("Pulse values");
        chart.getDescription().setTextColor(Color.BLACK);

        // get the legend (only possible after setting data)
        Legend l = chart.getLegend();

        // modify the legend ...
        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(Color.BLACK);

        XAxis xl = chart.getXAxis();
        xl.setTextColor(Color.BLACK);
        xl.setDrawGridLines(false);
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(true);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setAxisMaximum(130f);
        leftAxis.setAxisMinimum(30);
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(Color.BLACK);

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);
    }

    private void addEntry(int pulse) {
        LineData data = chart.getData();

        if (data != null) {

            LineDataSet set = (LineDataSet) data.getDataSetByIndex(0);

            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }

            data.addEntry(new Entry(set.getEntryCount(), pulse), 0);
            data.notifyDataChanged();

            // let the graph know it's data has changed
            chart.notifyDataSetChanged();

            // limit the number of visible entries
            chart.setVisibleXRangeMaximum(60);

            // move to the latest entry
            chart.moveViewToX(data.getEntryCount());

        }
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }
    private void fct(String persoana){

        int x=9999;
        int y;
        Cursor cursor = myDb.getAllDatac();
        while(cursor.moveToNext())
            if((cursor.getString(2)).equals(persoana))
            {    y = Integer.parseInt(cursor.getString(1));
                    if(x!=y){
                        Grafv.this.addEntry(y);
                        x=y;
                            }
            }
    }
}
