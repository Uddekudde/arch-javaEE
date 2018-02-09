package se.kth.iv1201.recruitment.backend.resource;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

/**
 *
 * @author udde
 */
@Path("user/recruiter/{username}")
public class Recruiter {
    @Context
    private UriInfo context;
    
    @GET
    public String getHtml(@PathParam("username") String userName){
        return "{\"Recruiter\":"+userName+"}";
    }
}
