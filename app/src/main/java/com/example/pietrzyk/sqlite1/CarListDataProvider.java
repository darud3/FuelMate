package com.example.pietrzyk.sqlite1;

public class CarListDataProvider {

    private int car_id, car_mfg_date;
    private float car_start_counter, car_engine_cap;
    private String car_name, car_model, car_note, car_fuel_type, car_brand,
            car_fuel_units, car_dist_units, car_fuel_usage_units,car_reg_num;

    public CarListDataProvider(Integer car_id, Integer car_mfg_date, Float car_start_counter, Float car_engine_cap,
                               String car_name, String car_model, String car_note, String car_fuel_type,
                               String car_brand, String car_fuel_units, String car_dist_units, String car_fuel_usage_units,
                               String car_reg_num){

        this.car_id = car_id;
        this.car_mfg_date = car_mfg_date;
        this.car_start_counter = car_start_counter;
        this.car_engine_cap = car_engine_cap;
        this.car_name = car_name;
        this.car_model = car_model;
        this.car_note = car_note;
        this.car_fuel_type = car_fuel_type;
        this.car_brand = car_brand;
        this.car_fuel_units = car_fuel_units;
        this.car_dist_units = car_dist_units;
        this.car_fuel_usage_units = car_fuel_usage_units;
        this.car_reg_num = car_reg_num;
    }

    public String getCar_brand() {
        return car_brand;
    }

    public void setCar_brand(String car_brand) {
        this.car_brand = car_brand;
    }

    public String getCar_dist_units() {
        return car_dist_units;
    }

    public void setCar_dist_units(String car_dist_units) {
        this.car_dist_units = car_dist_units;
    }

    public float getCar_engine_cap() {
        return car_engine_cap;
    }

    public void setCar_engine_cap(float car_engine_cap) {
        this.car_engine_cap = car_engine_cap;
    }

    public String getCar_fuel_type() {
        return car_fuel_type;
    }

    public void setCar_fuel_type(String car_fuel_type) {
        this.car_fuel_type = car_fuel_type;
    }

    public String getCar_fuel_units() {
        return car_fuel_units;
    }

    public void setCar_fuel_units(String car_fuel_units) {
        this.car_fuel_units = car_fuel_units;
    }

    public String getCar_fuel_usage_units() {
        return car_fuel_usage_units;
    }

    public void setCar_fuel_usage_units(String car_fuel_usage_units) {
        this.car_fuel_usage_units = car_fuel_usage_units;
    }

    public int getCar_id() {
        return car_id;
    }

    public void setCar_id(int car_id) {
        this.car_id = car_id;
    }

    public int getCar_mfg_date() {
        return car_mfg_date;
    }

    public void setCar_mfg_date(int car_mfg_date) {
        this.car_mfg_date = car_mfg_date;
    }

    public String getCar_model() {
        return car_model;
    }

    public void setCar_model(String car_model) {
        this.car_model = car_model;
    }

    public String getCar_name() {
        return car_name;
    }

    public void setCar_name(String car_name) {
        this.car_name = car_name;
    }

    public String getCar_note() {
        return car_note;
    }

    public void setCar_note(String car_note) {
        this.car_note = car_note;
    }

    public float getCar_start_counter() {
        return car_start_counter;
    }

    public void setCar_start_counter(float car_start_counter) {
        this.car_start_counter = car_start_counter;
    }

    public String getCar_reg_num() {
        return car_reg_num;
    }

    public void setCar_reg_num(String car_reg_num) {
        this.car_reg_num = car_reg_num;
    }
}
