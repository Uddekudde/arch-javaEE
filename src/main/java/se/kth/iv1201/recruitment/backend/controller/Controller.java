/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.iv1201.recruitment.backend.controller;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.faces.bean.SessionScoped;
import org.json.JSONException;
import org.json.JSONObject;
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

    public String authenticate(LoginCredentials credentials) {
        JSONObject response = new JSONObject();
        try {
            if (recruitmentDAO.authenticateUser(credentials)) {
                response.put("token", generateToken(credentials.getUsername()));
                return response.toString();
            } else {
                response.put("error", "Login failed.");
                return response.toString();
            }
        } catch (Exception ex) {
            try {
                response.put("error", ex.getMessage());
            } catch (JSONException ex1) {
                Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex1);
            }
            return response.toString();
        }
    }

    public String registerPerson(RegistrationInfo credentials) {
        JSONObject response = new JSONObject();
        try {
            recruitmentDAO.createPerson(credentials);
            response.put("token", generateToken(credentials.getUsername()));
        } catch (Exception ex) {
            try {
                response.put("error", ex.getMessage());
            } catch (JSONException ex1) {
                Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex1);
            }
            return response.toString();
        }
        return response.toString();
    }

    /* Generate a JWT token - Should be moved to a seperate class later */
    private String generateToken(String username) throws IllegalArgumentException, UnsupportedEncodingException {
        Date today = new Date();
        int oneDay = 1000 * 60 * 60 * 24;
        String token = Jwts.builder()
                .setSubject("auth")
                .setExpiration(new Date(today.getTime() + oneDay))
                .claim("username", username)
                .signWith(
                        SignatureAlgorithm.HS256,
                        "secret".getBytes("UTF-8")
                )
                .compact();
        return token;
    }

    /* Decode a JWT token - Should be moved to a seperate class later */
    private String decodeToken(String token) throws UnsupportedEncodingException, SignatureException, Exception {
        Jws<Claims> claims = Jwts.parser()
                .setSigningKey("secret".getBytes("UTF-8"))
                .parseClaimsJws(token);
        if (claims.getBody().getSubject().equals("auth")) {
            String username = (String) claims.getBody().get("username");
            return username;
        } else {
            throw new Exception("Invalid token");
        }
    }

}
