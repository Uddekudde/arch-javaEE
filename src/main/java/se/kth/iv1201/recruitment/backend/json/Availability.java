/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.iv1201.recruitment.backend.json;

/**
 *
 * @author udde
 */
public class Availability {

    String token;
    int fromDate;
    int toDate;

    public void setToken(String token) {
        this.token = token;
    }

    public void setFromDate(int startDate) {
        this.fromDate = startDate;
    }

    public void setToDate(int stopDate) {
        this.toDate = stopDate;
    }

    public String getToken() {
        return token;
    }

    public int getFromDate() {
        return fromDate;
    }

    public int getToDate() {
        return toDate;
    }

}
