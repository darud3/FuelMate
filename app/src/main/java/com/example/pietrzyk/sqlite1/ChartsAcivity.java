package com.example.pietrzyk.sqlite1;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class ChartsAcivity extends AppCompatActivity {

    Context context = this;
    int selected_car_id = 0;
    DBHelper dbHelper;

    private ViewPager mViewPager;

    String sett_currency,sett_dist_unit,sett_fuel_cap , sett_fuel_usage_units ;
    String displayed_currency = "PLN", displayed_fuel_cap = "l", displayed_fuel_usage_units = "l/100km", displayed_dist_units = "km";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.charts_acivity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);


        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            //getSupportActionBar().setDisplayShowTitleEnabled(false); // usuwa tytuł activity z toolbara
        }

        getSettings();

        Spinner CarRefuelListSpinner = (Spinner) findViewById(R.id.charts_activ_car_spinner);

        SQLiteDatabase db = new DBHelper(this).getWritableDatabase();

        //Inicializacja spinnera z bazy danych
        Cursor spinner_cursor = db.rawQuery("SELECT " + TableCar.NewCar.CAR_ID + " AS _id, " + TableCar.NewCar.CAR_NAME + " FROM " + TableCar.NewCar.TABLE_NAME, null);

        if(spinner_cursor.moveToFirst()) {

            String[] from = new String[]{TableCar.NewCar.CAR_NAME};
            int[] to = new int[]{android.R.id.text1};

            //SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, spinner_cursor, from, to);
            //simpleCursorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(this, R.layout.my_spinner_item, spinner_cursor, from, to);
            simpleCursorAdapter.setDropDownViewResource(R.layout.my_spinner_dropdown);
            CarRefuelListSpinner.setAdapter(simpleCursorAdapter);

            CarRefuelListSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    //((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);

                    selected_car_id = Integer.valueOf(String.valueOf(id));
                    //String s = String.valueOf(selected_car_id);
                    //getSpinnerVal(s);


                    //PRZENIESIONE Z onCreate
                    // Create the adapter that will return a fragment for each of the three
                    // primary sections of the activity.
                    SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
                    //Chart1Fragment chart1 = (Chart1Fragment) mSectionsPagerAdapter.getItem(0);
                    //chart1.setCarID(Integer.valueOf(selected_car_id));

                    // Set up the ViewPager with the sections adapter.
                    mViewPager = (ViewPager) findViewById(R.id.container);
                    mViewPager.setAdapter(mSectionsPagerAdapter);

                    TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
                    tabLayout.setupWithViewPager(mViewPager);
                    //PRZENIESIONE Z onCreate End
                }

                public void onNothingSelected(AdapterView<?> parent) {
                    //cos tam
                }
            });
        }
        else {
            Log.e("SPINNER NEW CAR", "Kursor jest pusty");
            Intent myIntent = new Intent(ChartsAcivity.this, CarListActivity.class);
            startActivity(myIntent);
            Toast.makeText(context, "Nie masz jeszcze żadnego Pojazdu. Przed dodaniem tankowania musisz dodać pierwszy Pojazd!", Toast.LENGTH_LONG).show();
        }

    }

    public void getSettings(){

        dbHelper = new DBHelper(context);
        Cursor cur_settings = dbHelper.getSettings();

        if (cur_settings.moveToFirst()){

            cur_settings.moveToFirst();

            sett_currency = cur_settings.getString(0);
            sett_dist_unit = cur_settings.getString(1);
            sett_fuel_cap = cur_settings.getString(2);
            sett_fuel_usage_units = cur_settings.getString(3);

            if (sett_currency.equals("PLN")) displayed_currency = "zł";
            if (sett_currency.equals("EUR")) displayed_currency = "€";
            if (sett_currency.equals("USD")) displayed_currency = "$";
            if (sett_currency.equals("GBP")) displayed_currency = "£";
            if (sett_currency.equals("CZK")) displayed_currency = "Kč";

            //dist units
            if (sett_dist_unit.equals("Kilometry")) displayed_dist_units = "km";
            if (sett_dist_unit.equals("Mile")) displayed_dist_units = "mi";

            //fuel cap
            if (sett_fuel_cap.equals("Litry")) displayed_fuel_cap = "l";
            if (sett_fuel_cap.equals("Galony (UK)")) displayed_fuel_cap = "gal";
            if (sett_fuel_cap.equals("Galony (US)")) displayed_fuel_cap = "gal";

            //fuel usage
            if (sett_fuel_usage_units.equals("l/100km")) displayed_fuel_usage_units = "l/100km";
            if (sett_fuel_usage_units.equals("mpg (UK)")) displayed_fuel_usage_units = "mpg(uk)";
            if (sett_fuel_usage_units.equals("mpg (US)")) displayed_fuel_usage_units = "mpg(us)";
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_charts_acivity, menu);

        //MenuItem carSpinner = menu.findItem(R.id.charts_activ_car_spinner);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

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

    //Przeniesione do chart1,2,3 activity public static class PlaceholderFragment extends Fragment

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> pages;
        private List<String> tabs_names;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);

            //Alternatywa dla Switchy
            pages = new ArrayList<Fragment>();
            pages.add(0, new Chart1Fragment());
            pages.add(1, new Chart2Fragment());
            pages.add(2, new Chart3Fragment());

            Resources res = getResources();
            tabs_names = new ArrayList<String>();
            tabs_names.add(0,res.getString(R.string.view_pager_fuel_usage_chart_tab_PL));
            tabs_names.add(1,res.getString(R.string.view_pager_monthly_costs_chart_tab_PL));
            tabs_names.add(2,res.getString(R.string.view_pager_fuel_price_chart_tab_PL));
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class in fragments).

            /*
            switch (position){
                case 0:
                    Chart1Fragment chart1 = new Chart1Fragment();
                    return chart1;

                case 1:
                    Chart2Fragment chart2 = new Chart2Fragment();
                    return chart2;

                case 2:
                    Chart3Fragment chart3 = new Chart3Fragment();
                    return chart3;

                default:
                    return null;
            } */


            return pages.get(position);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.

            return 3;
            //return pages.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            /*switch (position) {
                case 0:
                    Resources res = getResources();
                    String tab1 = res.getString(R.string.view_pager_fuel_usage_chart_tab_PL);
                    return tab1;
                case 1:
                    return "TEST 2";
                case 2:
                    return "TEST 3";
            }
            return null; */
            return tabs_names.get(position);

        }
    }
}
