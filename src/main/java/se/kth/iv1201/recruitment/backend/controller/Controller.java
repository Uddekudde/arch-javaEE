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
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.faces.bean.SessionScoped;
import javax.transaction.Transactional;
import org.json.JSONException;
import org.json.JSONObject;
import se.kth.iv1201.recruitment.backend.integration.RecruitmentDAO;
import se.kth.iv1201.recruitment.backend.json.LoginCredentials;
import se.kth.iv1201.recruitment.backend.json.RegistrationInfo;
import se.kth.iv1201.recruitment.backend.controller.ErrorCodes;
import se.kth.iv1201.recruitment.backend.controller.Roles;


/**
 *The application's controller.
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
    
    /**
     * Authenticates the given credentials against the information in the database.
     * 
     * @param credentials
     * @return JSON containing a valid token or an error message.
     */
    @Transactional(value = Transactional.TxType.REQUIRES_NEW, rollbackOn = {SQLException.class}, dontRollbackOn = {SQLWarning.class})
    public String authenticate(LoginCredentials credentials) {
        JSONObject response = new JSONObject();
        
        try {
            if (recruitmentDAO.authenticateUser(credentials)) {
                String role = recruitmentDAO.getUserRole(credentials.getUsername());
                response.put("token", generateToken(credentials.getUsername(), role));
                return response.toString();
            } else {
                response.put("error", ErrorCodes.INVALID_USER);
                return response.toString();
            }
        } catch (Exception ex) {
            try {
                response.put("error", ErrorCodes.INVALID_USER);
            } catch (JSONException ex1) {
                Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex1);
            }
            return response.toString();
        }
    }
    
    /**
     * Registers the given credentials in the database.
     * 
     * @param credentials
     * @return JSON containing a valid token or an error message.
     */
    @Transactional(value = Transactional.TxType.REQUIRES_NEW, rollbackOn = {SQLException.class}, dontRollbackOn = {SQLWarning.class})
    public String registerPerson(RegistrationInfo credentials) {
        JSONObject response = new JSONObject();
        try {
            recruitmentDAO.createPerson(credentials);
            response.put("token", generateToken(credentials.getUsername(),credentials.getRole()));
        } catch (Exception ex) {
            try {
                response.put("error", ErrorCodes.USERNAME_UNAVAILABLE);
            } catch (JSONException ex1) {
                Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex1);
            }
            return response.toString();
        }
        return response.toString();
    }

    /**
     * Generate a JWT token - Should be moved to a seperate class later
     * 
     * @param username
     * @param role
     * @return a token String.
     * @throws IllegalArgumentException
     * @throws UnsupportedEncodingException 
     */
    private String generateToken(String username, String role) throws IllegalArgumentException, UnsupportedEncodingException {
        Date today = new Date();
        int oneDay = 1000 * 60 * 60 * 24;
        String token = Jwts.builder()
                .setSubject("auth")
                .setExpiration(new Date(today.getTime() + oneDay))
                .claim("username", username)
                .claim("role", role)
                .signWith(
                        SignatureAlgorithm.HS256,
                        "secret".getBytes("UTF-8")
                )
                .compact();
        return token;
    }

    /**
     * Decode a JWT token - Should be moved to a separate class later
     * 
     * @param token
     * @return A HashMap containing the username and role extracted from the token.
     * @throws UnsupportedEncodingException
     * @throws SignatureException
     * @throws Exception 
     */
    private HashMap<String, String> decodeToken(String token) throws UnsupportedEncodingException, SignatureException, Exception {
        Jws<Claims> claims = Jwts.parser()
                .setSigningKey("secret".getBytes("UTF-8"))
                .parseClaimsJws(token);
        if (claims.getBody().getSubject().equals("auth")) {
            HashMap<String, String> result = new HashMap<>();
            result.put("username",(String) claims.getBody().get("username"));
            result.put("role",(String) claims.getBody().get("role"));
            return result;
        } else {
            throw new Exception(ErrorCodes.INVALID_TOKEN);
        }
    }
    
    /**
     * Authorize a given token and give access if the role is Recruiter.
     * 
     * @param token
     * @return 
     */
    public String authorize(String token){
        JSONObject response = new JSONObject();
        try {
            HashMap<String, String> result = decodeToken(token);
            String username = result.get("username");
            String role = result.get("role");
            if(role.equals(Roles.RECRUITER)){
                response.put("token", "ACCESS_GRANTED");
                return response.toString();
            } else {
                response.put("error", "ACCESS_DENIED");
                return response.toString();
            }
        } catch (Exception ex) {
            try {
                response.put("error", "AUTHORIZATION_FAILED");
                return response.toString();
            } catch (JSONException ex1) {
                Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
        try {
            response.put("error", "AUTHORIZATION_FAILED");
        } catch (JSONException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
        return response.toString();
    }

}
