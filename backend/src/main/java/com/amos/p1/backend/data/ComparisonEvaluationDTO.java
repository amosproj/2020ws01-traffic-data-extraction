package com.amos.p1.backend.data;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class ComparisonEvaluationDTO {

    private int tomTomIncidentsAmount;
    private int hereIncidentsAmount;
    private int sameIncidentAmount;
    private Date date;

    public void setTomTomIncidentsAmount(int tomTomIncidentsAmount) {
        this.tomTomIncidentsAmount = tomTomIncidentsAmount;
    }

    public void setHereIncidentsAmount(int hereIncidentsAmount) {
        this.hereIncidentsAmount = hereIncidentsAmount;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setSameIncidentAmount(int sameIncidentAmount) {
        this.sameIncidentAmount = sameIncidentAmount;
    }

    @Override
    public String toString() {
        return "ComparisonEvaluationDTO{" +
                "tomTomIncidentsAmount=" + tomTomIncidentsAmount +
                ", hereIncidentsAmount=" + hereIncidentsAmount +
                ", sameIncidentAmount=" + sameIncidentAmount +
                ", date=" + date +
                '}';
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone="Europe/Berlin")
    public Date getDate(){
        return this.date;
    }

    public int getTomTomIncidentsAmount(){
        return this.tomTomIncidentsAmount;
    }

    public int getHereIncidentsAmount(){
        return this.hereIncidentsAmount;
    }

    public int getSameIncidentAmount(){
        return this.sameIncidentAmount;
    }


}
