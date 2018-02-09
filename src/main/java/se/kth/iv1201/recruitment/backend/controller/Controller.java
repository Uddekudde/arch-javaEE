/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.iv1201.recruitment.backend.controller;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.SessionScoped;
import se.kth.iv1201.recruitment.backend.integration.RecruitmentDAO;
import se.kth.iv1201.recruitment.backend.json.LoginCredentials;
import se.kth.iv1201.recruitment.backend.json.RegistrationInfo;

/**
 *
 * @author udde
 */
@SessionScoped
@Stateless
public class Controller {
    @EJB
    RecruitmentDAO recruitmentDAO;

    public Controller() {
         try {
            recruitmentDAO = new RecruitmentDAO();
        } catch (Exception ex) {
            
            System.out.println(ex.getMessage());
        }
    }
    
    public String authenticate(LoginCredentials credentials){
        try{
            if(recruitmentDAO.authenticateUser(credentials)){
                return "{\"token\":\"Login successful\"}";
            } else {
                return "{\"error\":\"Login failed.\"}";
            }
        }catch (Exception ex){
            return "{\"error\":\""+ex.getMessage()+"\"}";
        }
    }
    
    public String registerPerson(RegistrationInfo credentials){
        try {
            recruitmentDAO.createPerson(credentials);
        } catch (Exception ex) {
            return "{\"error\":\""+ex.getMessage()+"\"}";
        }
        return "{\"token\":\""+credentials.getPassword()+"\"}";
    }
    
    

}
