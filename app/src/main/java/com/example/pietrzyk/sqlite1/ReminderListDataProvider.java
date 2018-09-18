package com.example.pietrzyk.sqlite1;

/**
 * Created by pietrzyk on 2017-03-09.
 */

public class ReminderListDataProvider {

    private int notifiaction_id, repeat_cycle, repeat_kmcounter, id_row;
    private boolean repeat;
    private String category, title, date, hour;

    public ReminderListDataProvider (int id_row, String category, String title, String date, String hour,
                                     boolean repeat, int repeat_cycle, int repeat_kmcounter,
                                     int notifiaction_id) {
        this.id_row = id_row;
        this.category = category;
        this.title = title;
        this.date = date;
        this.hour = hour;
        this.repeat = repeat;
        this.repeat_cycle = repeat_cycle;
        this.repeat_kmcounter = repeat_kmcounter;
        this.notifiaction_id = notifiaction_id;
    }

    public int getId_row() {
        return id_row;
    }

    public void setId_row(int id_row) {
        this.id_row = id_row;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public int getNotifiaction_id() {
        return notifiaction_id;
    }

    public void setNotifiaction_id(int notifiaction_id) {
        this.notifiaction_id = notifiaction_id;
    }

    public boolean isRepeat() {
        return repeat;
    }

    public void setRepeat(boolean repeat) {
        this.repeat = repeat;
    }

    public int getRepeat_cycle() {
        return repeat_cycle;
    }

    public void setRepeat_cycle(int repeat_cycle) {
        this.repeat_cycle = repeat_cycle;
    }

    public int getRepeat_kmcounter() {
        return repeat_kmcounter;
    }

    public void setRepeat_kmcounter(int repeat_kmcounter) {
        this.repeat_kmcounter = repeat_kmcounter;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
