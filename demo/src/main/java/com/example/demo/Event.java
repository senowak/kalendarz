package com.example.demo;

public class Event {
    private  String dzien;
    private String nazwa;

    public Event(String dzien, String nazwa) {
        this.dzien = dzien;
        this.nazwa = nazwa;
    }

    public void setDzien(String dzien) {
        this.dzien = dzien;
    }
    public void setNazwa(String nazwa) {
        this.nazwa = nazwa;
    }


    public String getDzien() {
        return dzien;
    }
    public String getNazwa() {
        return nazwa;
    }

}
