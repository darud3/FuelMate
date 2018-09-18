package com.example.pietrzyk.sqlite1;

public class RefuelListDataProvider {

    private int fuel_id, km_counter, fuel_km_diff, fuel_missed;
    private float fuel_amount, fuel_price, cash_spend, fuel_usage;
    private String fuel_date, fuel_type, fuel_car,fuel_full, fuel_note, displayed_currency , displayed_dist_units, displayed_fuel_cap, displayed_fuel_usage_units;

    public RefuelListDataProvider(Integer fuel_id, Float fuel_amount, Float fuel_price, Float cash_spend, Integer km_counter, String fuel_date, String fuel_full,
                                  Integer fuel_km_diff, float fuel_usage, String fuel_note, Integer fuel_missed, String displayed_currency, String displayed_dist_units, String displayed_fuel_cap, String displayed_fuel_usage_units){

        this.fuel_id = fuel_id;
        this.fuel_amount = fuel_amount;
        this.fuel_price = fuel_price;
        this.cash_spend = cash_spend;
        this.km_counter = km_counter;
        this.fuel_date = fuel_date;
        this.fuel_full = fuel_full;
        this.fuel_km_diff = fuel_km_diff;
        this.fuel_usage = fuel_usage;
        this.fuel_note = fuel_note;
        this.fuel_missed = fuel_missed;
        this.displayed_currency = displayed_currency;
        this.displayed_dist_units = displayed_dist_units;
        this.displayed_fuel_cap = displayed_fuel_cap;
        this.displayed_fuel_usage_units = displayed_fuel_usage_units;
    }

    public int getFuel_id() {
        return fuel_id;
    }

    public void setFuel_id(int fuel_id) {
        this.fuel_id = fuel_id;
    }

    public float getCash_spend() {
        return cash_spend;
    }

    public void setCash_spend(float cash_spend) {
        this.cash_spend = cash_spend;
    }

    public float getFuel_amount() {
        return fuel_amount;
    }

    public void setFuel_amount(float fuel_amount) {
        this.fuel_amount = fuel_amount;
    }

    public String getFuel_date() {
        return fuel_date;
    }

    public void setFuel_date(String fuel_date) {
        this.fuel_date = fuel_date;
    }

    public float getFuel_price() {
        return fuel_price;
    }

    public void setFuel_price(float fuel_price) {
        this.fuel_price = fuel_price;
    }

    public int getKm_counter() {
        return (km_counter);
    }

    public void setKm_counter(int km_counter) {
        this.km_counter = km_counter;
    }

    public String getFuel_car() {
        return fuel_car;
    }

    public void setFuel_car(String fuel_car) {
        this.fuel_car = fuel_car;
    }

    public String getFuel_full() {
        return fuel_full;
    }

    public void setFuel_full(String fuel_full) {
        this.fuel_full = fuel_full;
    }

    public String getFuel_type() {
        return fuel_type;
    }

    public void setFuel_type(String fuel_type) {
        this.fuel_type = fuel_type;
    }

    public int getFuel_km_diff() {
        return fuel_km_diff;
    }

    public void setFuel_km_diff(int fuel_km_diff) {
        this.fuel_km_diff = fuel_km_diff;
    }

    public float getFuel_usage() {
        return fuel_usage;
    }

    public void setFuel_usage(float fuel_usage) {
        this.fuel_usage = fuel_usage;
    }

    public String getDisplayed_currency() {
        return displayed_currency;
    }

    public void setDisplayed_currency(String displayed_currency) {
        this.displayed_currency = displayed_currency;
    }

    public String getDisplayed_dist_units() {
        return displayed_dist_units;
    }

    public void setDisplayed_dist_units(String displayed_dist_units) {
        this.displayed_dist_units = displayed_dist_units;
    }

    public String getDisplayed_fuel_cap() {
        return displayed_fuel_cap;
    }

    public void setDisplayed_fuel_cap(String displayed_fuel_cap) {
        this.displayed_fuel_cap = displayed_fuel_cap;
    }

    public String getDisplayed_fuel_usage_units() {
        return displayed_fuel_usage_units;
    }

    public void setDisplayed_fuel_usage_units(String displayed_fuel_usage_units) {
        this.displayed_fuel_usage_units = displayed_fuel_usage_units;
    }

    public String getFuel_note() {
        return fuel_note;
    }

    public void setFuel_note(String fuel_note) {
        this.fuel_note = fuel_note;
    }

    public int getFuel_missed() {
        return fuel_missed;
    }

    public void setFuel_missed(int fuel_missed) {
        this.fuel_missed = fuel_missed;
    }
}
