/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.iv1201.recruitment.backend.model;

import se.kth.iv1201.recruitment.backend.json.Competence;
import se.kth.iv1201.recruitment.backend.json.Availability;
import java.util.ArrayList;
import java.util.Calendar;
import se.kth.iv1201.recruitment.backend.json.RegistrationInfo;

/**
 *
 * @author udde
 */
public class Application {

    RegistrationInfo ownerInfo;
    ArrayList<Competence> competences;
    ArrayList<Availability> availabilities;

    public Application(RegistrationInfo owner, ArrayList<Competence> competences, ArrayList<Availability> availabilities) {
        this.ownerInfo = owner;
        this.competences = competences;
        this.availabilities = availabilities;
    }

    public RegistrationInfo getOwnerInfo() {
        return ownerInfo;
    }

    public ArrayList<Competence> getCompetences() {
        return competences;
    }

    public ArrayList<Availability> getAvailabilities() {
        return availabilities;
    }
    
    
    
    
}
