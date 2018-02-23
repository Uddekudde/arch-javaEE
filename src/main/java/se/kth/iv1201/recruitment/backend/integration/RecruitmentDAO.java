/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.iv1201.recruitment.backend.integration;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.faces.bean.ApplicationScoped;
import se.kth.iv1201.recruitment.backend.controller.Roles;
import se.kth.iv1201.recruitment.backend.json.LoginCredentials;
import se.kth.iv1201.recruitment.backend.json.RegistrationInfo;

/**
 * A Facade to the database
 *
 * @author udde
 */
@ApplicationScoped
@TransactionAttribute(TransactionAttributeType.MANDATORY)
@Stateless
public class RecruitmentDAO {

    private static final String PERSON_TABLE = "person";
    private static final String PASSWORD_COLUMN = "password";
    private static final String USERNAME_COLUMN = "username";
    private static final String PERSON_ID_COLUMN = "person_id";
    private static final String NAME_COLUMN = "name";
    private static final String SURNAME_COLUMN = "surname";
    private static final String SSN_COLUMN = "ssn";
    private static final String EMAIL_COLUMN = "email";
    private static final String ROLE_ID_COLUMN = "role_id";
    private static final String ROLE_TABLE = "role";
    private static final int DEFAULT_ROLE = 2;
    
    private static final String dbms = "derby";
    private static final String databaseName = "RecruitmentDatabase";
    private PreparedStatement createUserStmt;
    private PreparedStatement findUserStmt;
    private PreparedStatement deleteUserStmt;
    private PreparedStatement createRoleStmt;
    private PreparedStatement findRoleStmt;
    
    
    private Connection connection;
    private final String SAVEPOINT_NAME = "savepoint";
    private Savepoint savepoint;

    public RecruitmentDAO() throws Exception {
        try {
            Connection connection = createDatasource();
            this.connection = connection;
            prepareStatements(connection);
        } catch (ClassNotFoundException | SQLException exception) {
            System.out.println(exception.getMessage());
            throw new Exception("Could not connect to datasource.", exception);
        }
    }

    /**
     * Connects to the database.
     * 
     * @param dbms
     * @param datasource
     * @return A connection to the database.
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws Exception 
     */
    private Connection connectToRecruitmentDB(String dbms, String datasource)
            throws ClassNotFoundException, SQLException, Exception {
        if (dbms.equalsIgnoreCase("derby")) {
            Class.forName("org.apache.derby.jdbc.ClientXADataSource");
            return DriverManager.getConnection(
                    "jdbc:derby://localhost:1527/" + datasource + ";create=true");
        } else {
            throw new Exception("Unable to create datasource, unknown dbms.");
        }
    }
    
    
    /**
     * Checks if the Role and Person tables exist in the database.
     * 
     * @param connection
     * @return true if the tables exist in the database, else false.
     * @throws SQLException 
     */
    private boolean tablesExist(Connection connection) throws SQLException {
        int tableNameColumn = 3;
        DatabaseMetaData dbm = connection.getMetaData();
        try (ResultSet rs = dbm.getTables(null, null, null, null)) {
            for (; rs.next();) {
                if (rs.getString(tableNameColumn).equalsIgnoreCase(ROLE_TABLE)) {
                    return true;
                }
                if (rs.getString(tableNameColumn).equalsIgnoreCase(PERSON_TABLE)) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * Creates the Person and Role tables in the database.
     * 
     * @return A connection to the database.
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws Exception 
     */
    private Connection createDatasource() throws
            ClassNotFoundException, SQLException, Exception {
        Connection connection = connectToRecruitmentDB(dbms, databaseName);
        if (!tablesExist(connection)) {
            Statement statement = connection.createStatement();
            statement.executeUpdate("CREATE TABLE "+ROLE_TABLE+" ("+ROLE_ID_COLUMN+" BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL PRIMARY KEY,"+NAME_COLUMN+" VARCHAR(255))");
            statement.executeUpdate("CREATE TABLE "+PERSON_TABLE+" ("+NAME_COLUMN+" VARCHAR(255)"
                    + ","+SURNAME_COLUMN+" VARCHAR(255),"+SSN_COLUMN+" VARCHAR(255),"+EMAIL_COLUMN+" VARCHAR(255)"
                    + ","+PASSWORD_COLUMN+" VARCHAR(255),"+ROLE_ID_COLUMN+" BIGINT REFERENCES "+ROLE_TABLE+","+USERNAME_COLUMN+" VARCHAR(255) PRIMARY KEY)");
            createRoles(connection);
        }
        return connection;
    }
    
    /**
     * Insert roles into the Role table.
     * 
     * @param connection
     * @throws SQLException 
     */
    private void createRoles(Connection connection) throws SQLException{
        createRoleStmt = connection.prepareStatement("INSERT INTO "
                + ROLE_TABLE + " ("+NAME_COLUMN+") VALUES (?)");
        try {
            createRoleStmt.setString(1,"recruiter");
            createRoleStmt.executeUpdate();
            createRoleStmt.setString(1,"applicant");
            createRoleStmt.executeUpdate();
        } catch (SQLException sqle) {
            throw sqle;
        }
    }
    
    /**
     * Authenticates the given credentials against the database.
     * 
     * @param credentials
     * @return
     * @throws Exception 
     */
    public boolean authenticateUser(LoginCredentials credentials) throws Exception{
        
        String failureMsg = "No user with those credentials in database";
        ResultSet result = null;
        try {
            findUserStmt.setString(1, credentials.getUsername());
            result = findUserStmt.executeQuery();
            if (result.next()) {
                return result.getString(PASSWORD_COLUMN).equals(credentials.getPassword());
            }
        } catch (SQLException sqle) {
            throw new Exception(failureMsg, sqle);
        } finally {
            try {
                result.close();
            } catch (Exception e) {
                throw new Exception(failureMsg, e);
            }
        }
        return false;
    }
    
    /**
     * Gets the role of the given user from the database.
     * 
     * @param username
     * @return The role of the given user.
     * @throws SQLException 
     */
    public String getUserRole(String username) throws SQLException{
        String failureMsg = "No user with that username in database.";
        ResultSet result = null;
        try {
            findRoleStmt.setString(1, username);
            result = findRoleStmt.executeQuery();
            if (result.next()) {
                    return result.getString(NAME_COLUMN);
            }
        } catch (SQLException e) {
            throw new SQLException(failureMsg, e);
        } finally {
            try {
                result.close();
            } catch (Exception e) {
                throw new SQLException(failureMsg, e);
            }
        }
        throw new SQLException(failureMsg);
    }
    
    /**
     * Enters a new Person into the database.
     * 
     * @param credentials Data relating to the person.
     * @throws Exception 
     */
    public void createPerson(RegistrationInfo credentials) throws Exception {
        String failureMsg = "Could not create the account: " + credentials.getUsername();
        if(usernameInDatabase(credentials.getUsername())){
            throw new Exception(failureMsg);
        }
        try {
            createUserStmt.setString(1, credentials.getFirstname());
            createUserStmt.setString(2, credentials.getLastname());
            createUserStmt.setString(3, credentials.getSsn());
            createUserStmt.setString(4, credentials.getEmail());
            createUserStmt.setString(5, credentials.getPassword());
            int role;
            if(credentials.getRole().equals(Roles.RECRUITER)){
                role = 1;
            } else {
                role = 2;
            }
            createUserStmt.setInt(6, role);
            createUserStmt.setString(7, credentials.getUsername());
            int rows = createUserStmt.executeUpdate();
            if (rows != 1) {
                throw new Exception(failureMsg);
            }
        } catch (SQLException sqle) {
            throw new Exception(failureMsg, sqle);
        }
    }
    
    /**
     * Checks if the given username is in the database.
     * 
     * @param username
     * @return True if the usernam is in the database, else false.
     * @throws Exception 
     */
    public boolean usernameInDatabase(String username) throws Exception {
        String failureMsg = "Could not search for specified username.";
        ResultSet result = null;
        try {
            findUserStmt.setString(1, username);
            result = findUserStmt.executeQuery();
            return result.next();
        } catch (SQLException sqle) {
            throw new Exception(failureMsg, sqle);
        } finally {
            try {
                result.close();
            } catch (Exception e) {
                throw new Exception(failureMsg, e);
            }
        }
    }
    
    /**
     * Initializes PreparedStatemets.
     * 
     * @param connection
     * @throws SQLException 
     */
    private void prepareStatements(Connection connection) throws SQLException {
        createUserStmt = connection.prepareStatement("INSERT INTO "
                + PERSON_TABLE + " VALUES ( ?, ?, ?, ?, ?, ?, ?)");

        findUserStmt = connection.prepareStatement("SELECT * FROM "
                + PERSON_TABLE + " WHERE " + USERNAME_COLUMN + " = ?");
        findRoleStmt = connection.prepareStatement("SELECT "+NAME_COLUMN+" FROM "+ROLE_TABLE+" WHERE "+ROLE_ID_COLUMN+" IN (SELECT "+ROLE_ID_COLUMN+" FROM "
                + PERSON_TABLE + " WHERE " + USERNAME_COLUMN + " = ?)");
    }
}
