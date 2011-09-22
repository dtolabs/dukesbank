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


public class BankBean implements SessionBean, SessionSynchronization {
    private static final String dbName = "java:comp/env/jdbc/BankDB";
    private String customerId;
    private double checkingBalance;
    private double savingBalance;
    private SessionContext context;
    private Connection con;

    public BankBean() {
    }

    public void transferToSaving(double amount)
        throws InsufficientBalanceException {
        checkingBalance -= amount;
        savingBalance += amount;

        try {
            updateChecking(checkingBalance);

            if (checkingBalance < 0.00) {
                context.setRollbackOnly();
                throw new InsufficientBalanceException();
            }

            updateSaving(savingBalance);
        } catch (SQLException ex) {
            throw new EJBException("Transaction failed due to SQLException: " +
                ex.getMessage());
        }
    }

    public double getCheckingBalance() {
        return checkingBalance;
    }

    public double getSavingBalance() {
        return savingBalance;
    }

    public void ejbCreate(String id) throws CreateException {
        if (id == null) {
            throw new CreateException("Null id not allowed.");
        } else {
            customerId = id;
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

    public void afterBegin() {
        System.out.println("afterBegin()");

        try {
            checkingBalance = selectChecking();
            savingBalance = selectSaving();
        } catch (SQLException ex) {
            throw new EJBException("afterBegin Exception: " + ex.getMessage());
        }
    }

    public void beforeCompletion() {
        System.out.println("beforeCompletion()");
    }

    public void afterCompletion(boolean committed) {
        System.out.println("afterCompletion: " + committed);

        if (committed == false) {
            try {
                checkingBalance = selectChecking();
                savingBalance = selectSaving();
            } catch (SQLException ex) {
                throw new EJBException("afterCompletion SQLException: " +
                    ex.getMessage());
            }
        }
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
            "update checking set balance =  ? " + "where id = ?";

        PreparedStatement prepStmt = con.prepareStatement(updateStatement);

        prepStmt.setDouble(1, amount);
        prepStmt.setString(2, customerId);
        prepStmt.executeUpdate();
        prepStmt.close();
        releaseConnection();
    }

    private void updateSaving(double amount) throws SQLException {
        makeConnection();

        String updateStatement =
            "update saving set balance =  ? " + "where id = ?";

        PreparedStatement prepStmt = con.prepareStatement(updateStatement);

        prepStmt.setDouble(1, amount);
        prepStmt.setString(2, customerId);
        prepStmt.executeUpdate();
        prepStmt.close();
        releaseConnection();
    }

    private double selectChecking() throws SQLException {
        makeConnection();

        String selectStatement =
            "select balance " + "from checking where id = ? ";
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

    private double selectSaving() throws SQLException {
        makeConnection();

        String selectStatement =
            "select balance " + "from saving where id = ? ";
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
} // BankBean
