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


public class WarehouseBean implements SessionBean {
    private static final String dbName = "java:comp/env/jdbc/WarehouseDB";
    private SessionContext context;
    private Connection con;

    public WarehouseBean() {
    }

    public void ship(String productId, String orderId, int quantity) {
        try {
            makeConnection();
            con.setAutoCommit(false);
            updateOrderItem(productId, orderId);
            updateInventory(productId, quantity);
            con.commit();
        } catch (Exception ex) {
            try {
                con.rollback();
                throw new EJBException("Transaction failed: " +
                    ex.getMessage());
            } catch (SQLException sqx) {
                throw new EJBException("Rollback failed: " + sqx.getMessage());
            }
        } finally {
            releaseConnection();
        }
    }

    public String getStatus(String productId, String orderId) {
        try {
            makeConnection();

            return selectStatus(productId, orderId);
        } catch (SQLException ex) {
            throw new EJBException(
                "Unable to fetch status due to SQLException: " +
                ex.getMessage());
        } finally {
            releaseConnection();
        }
    }

    public void ejbCreate() {
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

    /********************* Database Routines ***********************/
    private String selectStatus(String productId, String orderId)
        throws SQLException {
        String result;

        String selectStatement =
            "select status " + "from order_item where product_id = ? " +
            "and order_id = ?";
        PreparedStatement prepStmt = con.prepareStatement(selectStatement);

        prepStmt.setString(1, productId);
        prepStmt.setString(2, orderId);

        ResultSet rs = prepStmt.executeQuery();

        if (rs.next()) {
            result = rs.getString(1);
        } else {
            result = "No rows found.";
        }

        prepStmt.close();

        return result;
    }

    private void updateOrderItem(String productId, String orderId)
        throws SQLException {
        String updateStatement =
            "update order_item set status =  'shipped' " +
            "where product_id = ? " + "and order_id = ?";

        PreparedStatement prepStmt = con.prepareStatement(updateStatement);

        prepStmt.setString(1, productId);
        prepStmt.setString(2, orderId);
        prepStmt.executeUpdate();
        prepStmt.close();
    }

    private void updateInventory(String productId, int quantity)
        throws SQLException {
        String updateStatement =
            "update inventory " + "set quantity = quantity - ? " +
            "where product_id = ?";

        PreparedStatement prepStmt = con.prepareStatement(updateStatement);

        prepStmt.setInt(1, quantity);
        prepStmt.setString(2, productId);
        prepStmt.executeUpdate();
        prepStmt.close();
    }

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
} // WarehouseBean 
