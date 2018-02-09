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
import se.kth.iv1201.recruitment.backend.json.LoginCredentials;
import se.kth.iv1201.recruitment.backend.json.RegistrationInfo;

/**
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
        login.setEmail("meme");
        login.setFirstname("memename");
        login.setPassword("memepass");
        login.setSsn("12345");
        login.setLastname("memesurname");
        login.setUsername("memeusernamenumerodos");
        return controller.registerPerson(login);
    }
}
