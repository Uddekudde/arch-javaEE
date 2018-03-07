/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.iv1201.recruitment.backend.resource;

import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import se.kth.iv1201.recruitment.backend.controller.Controller;
import se.kth.iv1201.recruitment.backend.controller.Roles;
import se.kth.iv1201.recruitment.backend.json.Availability;
import se.kth.iv1201.recruitment.backend.json.Competence;
import se.kth.iv1201.recruitment.backend.json.LoginCredentials;
import se.kth.iv1201.recruitment.backend.json.RegistrationInfo;
import se.kth.iv1201.recruitment.backend.json.Token;

/**
 * Contains REST endpoints relating to registration and authentication.
 * 
 * @author udde
 */
@Path("")
public class AuthenticationResource {

    @Context
    private UriInfo context;
    
    @EJB
    private Controller controller;
    
    @Path("authentication")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String login(LoginCredentials login) {
        return controller.authenticate(login);
    }

    @Path("registration")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String register(RegistrationInfo regInfo) {
        regInfo.setRole(Roles.APPLICANT);
        return controller.registerPerson(regInfo);
    }
    
    @Path("authentication")
    @GET
    public String login() {
        LoginCredentials login = new LoginCredentials();
        login.setUsername("memeusername");
        login.setPassword("memepass");
        return controller.authenticate(login);
    }
    
    @Path("registration")
    @GET
    public String register() {
        RegistrationInfo login = new RegistrationInfo();
        login.setEmail("meme@meme.com");
        login.setFirstname("memename");
        login.setPassword("memepass");
        login.setSsn("12345");
        login.setLastname("memesurname");
        login.setUsername("memeusername7");
        login.setRole(Roles.APPLICANT);
        return controller.registerPerson(login);
    }
    
    @Path("restricted")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String registerRecruiter(RegistrationInfo regInfo) {
        regInfo.setRole(Roles.RECRUITER);
        return controller.registerPerson(regInfo);
    }
    
    @Path("restricted/resource")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String authorizeUser(Token token) {
        return controller.authorize(token.getToken());
    }
    
    @Path("competence")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String addCompetence(){
        //Competence competence = new Competence("meme browsing", 30);
        Competence competence = new Competence();
        competence.setCompetence("meme browsing");
        competence.setYearsOfExperience(20);
        competence.setToken("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhdXRoIiwiZXhwIjoxNTIwNDU0NTM4LCJ1c2VybmFtZSI6Im1lbWV1c2VybmFtZSIsInJvbGUiOiJyZWNydWl0ZXIifQ.vj2fwU29KELI1K8u_0NGss3nF01cbnQEP2a5JRxf_-c");
        return controller.addCompetence(competence);
    }
    
    @Path("competence")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String postCompetence(Competence competence){
        return controller.addCompetence(competence);
    }
    
    @Path("availability")
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String addAvailability(){
        //Availability availability = new Availability(1520369306, 1520467200);
        Availability availability = new Availability();
        availability.setFromDate(1520369306);
        availability.setToDate(1520467200);
        availability.setToken("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhdXRoIiwiZXhwIjoxNTIwNDU0NTM4LCJ1c2VybmFtZSI6Im1lbWV1c2VybmFtZSIsInJvbGUiOiJyZWNydWl0ZXIifQ.vj2fwU29KELI1K8u_0NGss3nF01cbnQEP2a5JRxf_-c");
        return controller.addAvailability(availability);
    }
    
    @Path("availability")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String postAvailability(Availability availability){
        return controller.addAvailability(availability);
    }
    
    @Path("applications")
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String getApplications(){
        return controller.getApplications("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhdXRoIiwiZXhwIjoxNTIwNDY2MDkwLCJ1c2VybmFtZSI6IlJlY3J1aXRlclRlc3QiLCJyb2xlIjoicmVjcnVpdGVyIn0.YRzDzRWGz0GvirXWEjAtczy8XLA2slXvxhshz9ZaIsU");
    }
    
    @Path("applications")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String getPostApplications(Token token){
        return controller.getApplications(token.getToken());
    }
}
