package com.example.pietrzyk.sqlite1;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class ChartLineActivity extends AppCompatActivity {

    Context context = this;
    int selected_car_id = 0;
    DBHelper dbHelper;
    LineChart chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chart_line_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.chart_line_activity_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Wykres Line");

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            //getSupportActionBar().setDisplayShowTitleEnabled(false); // usuwa tytuł activity z toolbara
        }

        chart = (LineChart) findViewById(R.id.line_chart_fuel_usage);
        chart.setDoubleTapToZoomEnabled(false);


    }


    public void setDataLineChart(String car_id){

        dbHelper = new DBHelper(context);

        Cursor curChartFuelUsage = dbHelper.getChartFuelUsage(car_id);
        curChartFuelUsage.moveToFirst();

        int count = curChartFuelUsage.getCount();
        //Float[] x_refuel_data = new Float[count];
        final String[] x_refuel_data = new String[count];
        final String[] x2_refuel_data = new String[count];
        Float[] y_fuel_usage = new Float[count];


        if (curChartFuelUsage.moveToFirst()){
            Log.e("CHART", "moveToFirst OK, count: " + count);
            //do {

                for (int i = 0 ; i < count ; i++){

                    //x_refuel_data[i] = curChartFuelUsage.getFloat(0);
                    x_refuel_data[i] = curChartFuelUsage.getString(0);
                    x2_refuel_data[i] = x_refuel_data[i].replace("-", "/");
                    Log.e("CHART", "New date: " + x2_refuel_data[i]);

                    y_fuel_usage[i] = curChartFuelUsage.getFloat(1);
                    curChartFuelUsage.moveToNext();
                }
            //}
            //while(curChartFuelUsage.moveToNext());

            ArrayList<Entry> values = new ArrayList<Entry>();

            for (int i = 0 ; i < count ; i++){
                //values.add(new Entry(x_refuel_data[i], y_fuel_usage[i])); //float x, float y
                values.add(new Entry(i, y_fuel_usage[i]));
            }

            LineDataSet set1 = new LineDataSet(values,"Zużycie paliwa");
            set1.setAxisDependency(YAxis.AxisDependency.LEFT);
            set1.setColors(new int[]{R.color.chartRed}, context);

            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
            dataSets.add(set1);

            LineData lineData = new LineData(dataSets);
            chart.setData(lineData);

            IAxisValueFormatter formatter = new IAxisValueFormatter() {
                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    //return x_refuel_data[(int)value];
                    return x2_refuel_data[(int)value];
                }

                @Override
                public int getDecimalDigits() {
                    return 0;
                }
            };

            Description description = new Description();
            description.setTextColor(ColorTemplate.VORDIPLOM_COLORS[2]);
            description.setText("");
            chart.setDescription(description);

            chart.getXAxis().setValueFormatter(formatter);
            chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

            //podpisy pod osia X dokladnie pod kazdym wpisem z osi Y
            chart.getXAxis().setGranularity(1);

            // usuwa puste miejsce pomiedzy poczatkiem osi y a pierwsza wartoscia
            chart.getAxisLeft().setAxisMinimum(0f);
            chart.getAxisRight().setAxisMinimum(0f);

            chart.invalidate();


        }
        else Toast.makeText(context, "Brak Danych dla wskazanego pojazdu. Najdpierw dodaj pełne tankowania.", Toast.LENGTH_LONG).show();

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chart_activity, menu);

        MenuItem carSpinner = menu.findItem(R.id.chart_activ_car_spinner);
        Spinner CarRefuelListSpinner = (Spinner) MenuItemCompat.getActionView(carSpinner);

        SQLiteDatabase db = new DBHelper(this).getWritableDatabase();

        //Inicializacja spinnera z bazy danych
        Cursor spinner_cursor = db.rawQuery("SELECT " + TableCar.NewCar.CAR_ID + " AS _id, " + TableCar.NewCar.CAR_NAME + " FROM " + TableCar.NewCar.TABLE_NAME, null);

        if(spinner_cursor.moveToFirst()) {

            String[] from = new String[]{TableCar.NewCar.CAR_NAME};
            int[] to = new int[]{android.R.id.text1};

            SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, spinner_cursor, from, to);
            simpleCursorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            CarRefuelListSpinner.setAdapter(simpleCursorAdapter);

            CarRefuelListSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
                    selected_car_id = Integer.valueOf(String.valueOf(id));

                    setDataLineChart(String.valueOf(selected_car_id));
                }

                public void onNothingSelected(AdapterView<?> parent) {
                    //cos tam
                }
            });
        }
        else {
            Log.e("SPINNER NEW CAR", "Kursor jest pusty");
            Intent myIntent = new Intent(ChartLineActivity.this, CarListActivity.class);
            startActivity(myIntent);
            Toast.makeText(context, "Nie masz jeszcze żadnego Pojazdu. Przed dodaniem tankowania musisz dodać pierwszy Pojazd!", Toast.LENGTH_LONG).show();
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //return super.onOptionsItemSelected(item);

        //tutaj deklarujemy działanie guzika back
        switch (item.getItemId()) {
            case android.R.id.home:
                // this takes the user 'back', as if they pressed the left-facing triangle icon on the main android toolbar.
                // if this doesn't work as desired, another possibility is to call `finish()` here.
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }






}
