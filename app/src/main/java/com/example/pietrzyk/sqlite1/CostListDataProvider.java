package com.example.pietrzyk.sqlite1;

public class CostListDataProvider {

    private int cost_id;
    private float cost_cash_spend;
    private String cost_name, cost_date, cost_note, cost_type, cost_car, displayed_currency;

    public CostListDataProvider(Integer cost_id, float cost_cash_spend, String cost_name, String cost_date, String cost_note, String cost_type, String cost_car, String displayed_currency){

        this.cost_id = cost_id;
        this.cost_cash_spend = cost_cash_spend;
        this.cost_name = cost_name;
        this.cost_date = cost_date;
        this.cost_note = cost_note;
        this.cost_type = cost_type;
        this.cost_car = cost_car;
        this.displayed_currency = displayed_currency;
    }

    public String getCost_car() {
        return cost_car;
    }

    public void setCost_car(String cost_car) {
        this.cost_car = cost_car;
    }

    public float getCost_cash_spend() {
        return cost_cash_spend;
    }

    public void setCost_cash_spend(float cost_cash_spend) {
        this.cost_cash_spend = cost_cash_spend;
    }

    public String getCost_date() {
        return cost_date;
    }

    public void setCost_date(String cost_date) {
        this.cost_date = cost_date;
    }

    public int getCost_id() {
        return cost_id;
    }

    public void setCost_id(int cost_id) {
        this.cost_id = cost_id;
    }

    public String getCost_name() {
        return cost_name;
    }

    public void setCost_name(String cost_name) {
        this.cost_name = cost_name;
    }

    public String getCost_note() {
        return cost_note;
    }

    public void setCost_note(String cost_note) {
        this.cost_note = cost_note;
    }

    public String getCost_type() {
        return cost_type;
    }

    public void setCost_type(String cost_type) {
        this.cost_type = cost_type;
    }

    public String getDisplayed_currency() {
        return displayed_currency;
    }

    public void setDisplayed_currency(String displayed_currency) {
        this.displayed_currency = displayed_currency;
    }
}
