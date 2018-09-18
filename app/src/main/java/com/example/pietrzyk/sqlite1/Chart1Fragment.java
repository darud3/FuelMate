package com.example.pietrzyk.sqlite1;

//import android.app.Fragment;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;


public class Chart1Fragment extends Fragment {

    Context context = getActivity();
    DBHelper dbHelper;
    LineChart lineChart;
    Conventers conventer = new Conventers();
    String displayed_fuel_usage_units; //= ((ChartsAcivity)context).displayed_fuel_usage_units;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.chart1_fragment, container, false);

        /*
        if (car_id != null) {
            lineChart = (LineChart) getActivity().findViewById(R.id.chart1_line_fuel_usage);
            Toast.makeText(getActivity(), "ID = " + car_id, Toast.LENGTH_LONG).show();
            setData(String.valueOf(1));
        }
        else Toast.makeText(getActivity(), "ID = null", Toast.LENGTH_LONG).show();
        */

        //context = getActivity();
        //String test = ((ChartsAcivity)context).spinnerValue;
        //Toast.makeText(getActivity(), "test = " + test, Toast.LENGTH_LONG).show();

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        lineChart = (LineChart) getActivity().findViewById(R.id.chart1);

        // PRZERZUCAM TUTAJ WARTOSC Z ACTIVITY
        context = getActivity();
        int car_id = ((ChartsAcivity)context).selected_car_id;
        displayed_fuel_usage_units = ((ChartsAcivity)context).displayed_fuel_usage_units;

        setDataLineChart1(String.valueOf(car_id));

        //Toast.makeText(getActivity(), "car_id = " + car_id, Toast.LENGTH_SHORT).show();

    }


    public void setDataLineChart1(String car_id){

        dbHelper = new DBHelper(getActivity());

        Cursor curChartFuelUsage = dbHelper.getChartFuelUsage(car_id);
        curChartFuelUsage.moveToFirst();

        int count = curChartFuelUsage.getCount();
        //Float[] x_refuel_data = new Float[count];
        final String[] x_refuel_data = new String[count];
        final String[] x2_refuel_data = new String[count];
        Float[] y_fuel_usage = new Float[count];
        float min_y = 0, max_y = 0, tmp_max_min=0;

        lineChart.setNoDataText("Brak danych dla tego wykresu. Dodaj więcej tankowań.");

        if (count >= 2){

            if (curChartFuelUsage.moveToFirst()){
                //do {

                tmp_max_min = curChartFuelUsage.getFloat(1);
                min_y = tmp_max_min;
                max_y = tmp_max_min;

                for (int i = 0 ; i < count ; i++){

                    //x
                    x_refuel_data[i] = curChartFuelUsage.getString(0);
                    x2_refuel_data[i] = x_refuel_data[i].replace("-", "/");

                    //y
                    if (displayed_fuel_usage_units.equals("l/100km")) {y_fuel_usage[i] = curChartFuelUsage.getFloat(1); }
                    if (displayed_fuel_usage_units.equals("mpg(uk)")) {
                        double tmp;
                        y_fuel_usage[i] = curChartFuelUsage.getFloat(1);
                        tmp = conventer.lp100ToMpgUK(y_fuel_usage[i]);
                        y_fuel_usage[i] = (float) tmp;
                    }

                    if (displayed_fuel_usage_units.equals("mpg(us)")) {
                        double tmp;
                        y_fuel_usage[i] = curChartFuelUsage.getFloat(1);
                        tmp = conventer.lp100ToMpgUS(y_fuel_usage[i]);
                        y_fuel_usage[i] = (float) tmp;
                    }

                    if (y_fuel_usage[i] > max_y) max_y = y_fuel_usage[i];
                    if (y_fuel_usage[i] < min_y) min_y = y_fuel_usage[i];

                    curChartFuelUsage.moveToNext();
                }


                ArrayList<Entry> values = new ArrayList<Entry>();

                for (int i = 0 ; i < count ; i++){
                    values.add(new Entry(i, y_fuel_usage[i]));
                }

                LineDataSet set1 = new LineDataSet(values,"Zużycie paliwa");
                set1.setAxisDependency(YAxis.AxisDependency.LEFT);
                set1.setColors(new int[]{R.color.chartRed}, getActivity());
                set1.setDrawCircles(false); // wylacza punkty z wartosciami na wykresie

                ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
                dataSets.add(set1);

                LineData lineData = new LineData(dataSets);
                lineChart.setData(lineData);



                ////@@@@@@@
                lineChart. setScaleEnabled(true);
                ////@@@@@@@


                lineChart.setTouchEnabled(true);
                // wyswietlanie wartosci po nacisnieciu
                MyMakerView mv = new MyMakerView(context, R.layout.chart_highlight);
                mv.setChartView(lineChart);
                lineChart.setMarker(mv);

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

                // podpis
                Description description = new Description();
                description.setTextColor(ColorTemplate.VORDIPLOM_COLORS[2]);
                description.setText("");
                lineChart.setDescription(description);

                // X-Axis
                lineChart.getXAxis().setValueFormatter(formatter);
                lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

                //podpisy pod osia X dokladnie pod kazdym wpisem z osi Y
                lineChart.getXAxis().setGranularity(1);

                //Y-Axis
                // usuwa puste miejsce pomiedzy poczatkiem osi y a pierwsza wartoscia

                //lineChart.getAxisLeft().setAxisMinimum(0f);
                //lineChart.getAxisRight().setAxisMinimum(0f);

                lineChart.getAxisLeft().setAxisMinimum(min_y-1);
                lineChart.getAxisRight().setAxisMinimum(max_y+1);

                // linia sredniego spalania
                Cursor curAvgFuelUsage = dbHelper.getAvgFuelUsage(car_id);
                curAvgFuelUsage.moveToFirst();

                if (curAvgFuelUsage.moveToFirst()) {

                    YAxis leftAxis = lineChart.getAxisLeft();
                    LimitLine ll = new LimitLine(curAvgFuelUsage.getFloat(0), "Śr. spalanie");
                    ll.setLineColor(Color.BLUE);
                    ll.setLineWidth(0.3f);
                    ll.setTextColor(Color.BLACK);
                    ll.setTextSize(6f);
                    leftAxis.addLimitLine(ll);
                }


                lineChart.invalidate();


            }
            else Toast.makeText(context, "Brak Danych dla wskazanego pojazdu. Najdpierw dodaj pełne tankowania.", Toast.LENGTH_LONG).show();
        }

    }


}
