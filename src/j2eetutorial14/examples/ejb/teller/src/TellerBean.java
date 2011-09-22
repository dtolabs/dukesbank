/*
 * Copyright (c) 2006 Sun Microsystems, Inc.  All rights reserved.  U.S.
 * Government Rights - Commercial software.  Government users are subject
 * to the Sun Microsystems, Inc. standard license agreement and
 * applicable provisions of the FAR and its supplements.  Use is subject
 * to license terms.
 *
 * This distribution may include materials developed by third parties.
 * Sun, Sun Microsystems, the Sun logo, Java and J2EE are trademarks
 * or registered trademarks of Sun Microsystems, Inc. in the U.S. and
 * other countries.
 *
 * Copyright (c) 2006 Sun Microsystems, Inc. Tous droits reserves.
 *
 * Droits du gouvernement americain, utilisateurs gouvernementaux - logiciel
 * commercial. Les utilisateurs gouvernementaux sont soumis au contrat de
 * licence standard de Sun Microsystems, Inc., ainsi qu'aux dispositions
 * en vigueur de la FAR (Federal Acquisition Regulations) et des
 * supplements a celles-ci.  Distribue par des licences qui en
 * restreignent l'utilisation.
 *
 * Cette distribution peut comprendre des composants developpes par des
 * tierces parties. Sun, Sun Microsystems, le logo Sun, Java et J2EE
 * sont des marques de fabrique ou des marques deposees de Sun
 * Microsystems, Inc. aux Etats-Unis et dans d'autres pays.
 */


import java.util.*;
import javax.ejb.*;
import java.sql.*;
import javax.sql.*;
import javax.naming.*;
import javax.transaction.*;


public class TellerBean implements SessionBean {
    private static final String dbName = "java:comp/env/jdbc/TellerDB";
    private String customerId;
    private double machineBalance;
    private SessionContext context;
    private Connection con;

    public TellerBean() {
    }

    public void withdrawCash(double amount) {
        UserTransaction ut = context.getUserTransaction();

        try {
            ut.begin();
            updateChecking(amount);
            machineBalance -= amount;
            insertMachine(machineBalance);
            ut.commit();
        } catch (Exception ex) {
            try {
                ut.rollback();
            } catch (SystemException syex) {
                throw new EJBException("Rollback failed: " + syex.getMessage());
            }

            throw new EJBException("Transaction failed: " + ex.getMessage());
        }
    }

    public double getCheckingBalance() {
        try {
            return selectChecking();
        } catch (SQLException ex) {
            throw new EJBException("Unable to get balance: " + ex.getMessage());
        }
    }

    public void ejbCreate(String id) throws CreateException {
        customerId = id;

        try {
            machineBalance = selectMachine();
        } catch (Exception ex) {
            throw new CreateException(ex.getMessage());
        }
    }

    public void ejbRemove() {
    }

    public void ejbActivate() {
    }

    public void ejbPassivate() {
    }

    public void setSessionContext(SessionContext context) {
        this.context = context;
    }

    /************************** Database Routines **********************/
    private void makeConnection() {
        try {
            InitialContext ic = new InitialContext();
            DataSource ds = (DataSource) ic.lookup(dbName);

            con = ds.getConnection();
        } catch (Exception ex) {
            throw new EJBException("Unable to connect to database. " +
                ex.getMessage());
        }
    }

    private void releaseConnection() {
        try {
            con.close();
        } catch (SQLException ex) {
            throw new EJBException("releaseConnection: " + ex.getMessage());
        }
    }

    private void updateChecking(double amount) throws SQLException {
        makeConnection();

        String updateStatement =
            "update checking set balance =  balance - ? " + "where id = ?";

        PreparedStatement prepStmt = con.prepareStatement(updateStatement);

        prepStmt.setDouble(1, amount);
        prepStmt.setString(2, customerId);
        prepStmt.executeUpdate();
        prepStmt.close();
        releaseConnection();
    }

    private void insertMachine(double amount) throws SQLException {
        makeConnection();

        String insertStatement =
            "insert into cash_in_machine values " + "( ? , current_date )";

        PreparedStatement prepStmt = con.prepareStatement(insertStatement);

        prepStmt.setDouble(1, amount);
        prepStmt.executeUpdate();
        prepStmt.close();
        releaseConnection();
    }

    private double selectMachine() throws SQLException {
        makeConnection();

        String selectStatement =
            "select amount " + "from cash_in_machine " + "where time_stamp = " +
            "(select max(time_stamp) from cash_in_machine)";
        PreparedStatement prepStmt = con.prepareStatement(selectStatement);

        ResultSet rs = prepStmt.executeQuery();

        if (rs.next()) {
            double result = rs.getDouble(1);

            prepStmt.close();
            releaseConnection();

            return result;
        } else {
            prepStmt.close();
            releaseConnection();
            throw new EJBException("Row for id " + customerId + " not found.");
        }
    }

    private double selectChecking() throws SQLException {
        makeConnection();

        String selectStatement =
            "select balance " + "from checking " + "where id = ?";
        PreparedStatement prepStmt = con.prepareStatement(selectStatement);

        prepStmt.setString(1, customerId);

        ResultSet rs = prepStmt.executeQuery();

        if (rs.next()) {
            double result = rs.getDouble(1);

            prepStmt.close();
            releaseConnection();

            return result;
        } else {
            prepStmt.close();
            releaseConnection();
            throw new EJBException("Row for id " + customerId + " not found.");
        }
    }
} // TellerBean
