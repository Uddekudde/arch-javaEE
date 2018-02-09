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
import se.kth.iv1201.recruitment.backend.json.FrontendRegInfo;
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

    @Path("registration")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String register(FrontendRegInfo regInfo) {
        RegistrationInfo info = new RegistrationInfo();
        info.setName(regInfo.getFirstname());
        info.setEmail(regInfo.getEmail());
        info.setPassword(regInfo.getPassword());
        info.setSsn("NOT_IMPLEMENTED");
        info.setSurname(regInfo.getLastname());
        info.setUsername(regInfo.getUsername());
        return controller.registerPerson(info);
    }

    @Path("authenticate")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String login(LoginCredentials login) {
        return controller.authenticate(login);
    }
    
    @Path("authenticate")
    @GET
    public String login() {
        LoginCredentials login = new LoginCredentials();
        login.setPassword("memepass");
        login.setUsername("memeusername");
        return controller.authenticate(login);
    }
    
    @Path("registration")
    @GET
    public String register() {
        RegistrationInfo login = new RegistrationInfo();
        login.setEmail("meme");
        login.setName("memename");
        login.setPassword("memepass");
        login.setSsn("12345");
        login.setSurname("memesurname");
        login.setUsername("memeusernamenumerodos");
        return controller.registerPerson(login);
    }
}
