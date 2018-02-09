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
import java.sql.Statement;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.faces.bean.ApplicationScoped;
import se.kth.iv1201.recruitment.backend.json.LoginCredentials;
import se.kth.iv1201.recruitment.backend.json.RegistrationInfo;

/**
 *
 * @author udde
 */
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
    private static final String ROLE_COLUMN = "role";
    private static final String ROLE_ID_COLUMN = "role_id";
    private static final String ROLE_TABLE = "role";
    private static final int DEFAULT_ROLE = 2;
    
    private static final String dbms = "derby";
    private static final String databaseName = "RecruitmentDatabase";
    private PreparedStatement createUserStmt;
    private PreparedStatement findUserStmt;
    private PreparedStatement findAllAccountsStmt;
    private PreparedStatement deleteUserStmt;
    private PreparedStatement changeBalanceStmt;
    private PreparedStatement createRoleStmt;
    private PreparedStatement deleteFileStmt;
    private PreparedStatement findFileStmt;
    private PreparedStatement findAllFilesStmt;
    private PreparedStatement updateFileStmt;

    public RecruitmentDAO() throws Exception {
        try {
            Connection connection = createDatasource();
            prepareStatements(connection);
        } catch (ClassNotFoundException | SQLException exception) {
            System.out.println(exception.getMessage());
            throw new Exception("Could not connect to datasource.", exception);
        }
    }

    /**
     * 
     * @param dbms
     * @param datasource
     * @return
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
     * 
     * @param connection
     * @return
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
            createUserStmt.setInt(6, DEFAULT_ROLE);
            createUserStmt.setString(7, credentials.getUsername());
            int rows = createUserStmt.executeUpdate();
            if (rows != 1) {
                throw new Exception(failureMsg);
            }
        } catch (SQLException sqle) {
            throw new Exception(failureMsg, sqle);
        }
    }
    
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
    

    private void prepareStatements(Connection connection) throws SQLException {
        createUserStmt = connection.prepareStatement("INSERT INTO "
                + PERSON_TABLE + " VALUES ( ?, ?, ?, ?, ?, ?, ?)");
        deleteUserStmt = connection.prepareStatement("DELETE FROM "
                + PERSON_TABLE
                + " WHERE " + USERNAME_COLUMN + " = ?");
        /**
        createFileStmt = connection.prepareStatement("INSERT INTO "
                + FILE_TABLE + " VALUES (?, ?, ?, ?)");
        deleteFileStmt = connection.prepareStatement("DELETE FROM "
                + FILE_TABLE
                + " WHERE " + FILENAME_COLUMN + " = ?");
        updateFileStmt = connection.prepareStatement("UPDATE "
                + FILE_TABLE + " SET " + FILENAME_COLUMN + " = ?, " + SIZE_COLUMN
                + " = ?, " + PUBLIC_COLUMN + " = ? WHERE " + FILENAME_COLUMN + " = ?");
                * **/
        findUserStmt = connection.prepareStatement("SELECT * from "
                + PERSON_TABLE + " WHERE " + USERNAME_COLUMN + " = ?");
        /**
        findFileStmt = connection.prepareStatement("SELECT * from "
                + FILE_TABLE + " WHERE " + FILENAME_COLUMN + " = ?");
        findAllFilesStmt = connection.prepareStatement("SELECT * from "
                + FILE_TABLE + " WHERE NOT " + PUBLIC_COLUMN + " = 'private' OR "
                + OWNER_COLUMN + " = ?");
                **/
    }
}
