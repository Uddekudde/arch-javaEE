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
public class Competence {

    String token;
    String competence;
    int yearsOfExperience;

    public void setCompetence(String competence) {
        this.competence = competence;
    }

    public void setYearsOfExperience(int yearsOfExperience) {
        this.yearsOfExperience = yearsOfExperience;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public String getCompetence() {
        return competence;
    }

    public int getYearsOfExperience() {
        return yearsOfExperience;
    }

}
