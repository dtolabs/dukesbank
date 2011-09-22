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


import java.sql.*;
import javax.sql.*;
import java.util.*;
import java.math.*;
import javax.ejb.*;
import javax.naming.*;


public class SavingsAccountBean implements EntityBean {
    private final static String dbName = "java:comp/env/jdbc/SavingsAccountDB";
    private String id;
    private String firstName;
    private String lastName;
    private BigDecimal balance;
    private EntityContext context;
    private Connection con;

    public void debit(BigDecimal amount) throws InsufficientBalanceException {
        if (balance.compareTo(amount) == -1) {
            throw new InsufficientBalanceException();
        }

        balance = balance.subtract(amount);
    }

    public void credit(BigDecimal amount) {
        balance = balance.add(amount);
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void ejbHomeChargeForLowBalance(BigDecimal minimumBalance,
        BigDecimal charge) throws InsufficientBalanceException {
        try {
            SavingsAccountHome home = (SavingsAccountHome) context.getEJBHome();
            Collection c =
                home.findInRange(new BigDecimal("0.00"),
                    minimumBalance.subtract(new BigDecimal("0.01")));

            Iterator i = c.iterator();

            while (i.hasNext()) {
                SavingsAccount account = (SavingsAccount) i.next();

                if (account.getBalance()
                               .compareTo(charge) == 1) {
                    account.debit(charge);
                }
            }
        } catch (Exception ex) {
            throw new EJBException("ejbHomeChargeForLowBalance: " +
                ex.getMessage());
        }
    }

    public String ejbCreate(String id, String firstName, String lastName,
        BigDecimal balance) throws CreateException {
        if (balance.signum() == -1) {
            throw new CreateException(
                "A negative initial balance is not allowed.");
        }

        try {
            insertRow(id, firstName, lastName, balance);
        } catch (Exception ex) {
            throw new EJBException("ejbCreate: " + ex.getMessage());
        }

        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.balance = balance;

        return id;
    }

    public String ejbFindByPrimaryKey(String primaryKey)
        throws FinderException {
        boolean result;

        try {
            result = selectByPrimaryKey(primaryKey);
        } catch (Exception ex) {
            throw new EJBException("ejbFindByPrimaryKey: " + ex.getMessage());
        }

        if (result) {
            return primaryKey;
        } else {
            throw new ObjectNotFoundException("Row for id " + primaryKey +
                " not found.");
        }
    }

    public Collection ejbFindByLastName(String lastName)
        throws FinderException {
        Collection result;

        try {
            result = selectByLastName(lastName);
        } catch (Exception ex) {
            throw new EJBException("ejbFindByLastName " + ex.getMessage());
        }

        return result;
    }

    public Collection ejbFindInRange(BigDecimal low, BigDecimal high)
        throws FinderException {
        Collection result;

        try {
            result = selectInRange(low, high);
        } catch (Exception ex) {
            throw new EJBException("ejbFindInRange: " + ex.getMessage());
        }

        return result;
    }

    public void ejbRemove() {
        try {
            deleteRow(id);
        } catch (Exception ex) {
            throw new EJBException("ejbRemove: " + ex.getMessage());
        }
    }

    public void setEntityContext(EntityContext context) {
        this.context = context;
    }

    public void unsetEntityContext() {
    }

    public void ejbActivate() {
        id = (String) context.getPrimaryKey();
    }

    public void ejbPassivate() {
        id = null;
    }

    public void ejbLoad() {
        try {
            loadRow();
        } catch (Exception ex) {
            throw new EJBException("ejbLoad: " + ex.getMessage());
        }
    }

    public void ejbStore() {
        try {
            storeRow();
        } catch (Exception ex) {
            throw new EJBException("ejbStore: " + ex.getMessage());
        }
    }

    public void ejbPostCreate(String id, String firstName, String lastName,
        BigDecimal balance) {
    }

    /*********************** Database Routines *************************/
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

    private void insertRow(String id, String firstName, String lastName,
        BigDecimal balance) throws SQLException {
        makeConnection();

        String insertStatement =
            "insert into savingsaccount values ( ? , ? , ? , ? )";
        PreparedStatement prepStmt = con.prepareStatement(insertStatement);

        prepStmt.setString(1, id);
        prepStmt.setString(2, firstName);
        prepStmt.setString(3, lastName);
        prepStmt.setBigDecimal(4, balance);

        prepStmt.executeUpdate();
        prepStmt.close();
        releaseConnection();
    }

    private void deleteRow(String id) throws SQLException {
        makeConnection();

        String deleteStatement = "delete from savingsaccount where id = ? ";
        PreparedStatement prepStmt = con.prepareStatement(deleteStatement);

        prepStmt.setString(1, id);
        prepStmt.executeUpdate();
        prepStmt.close();
        releaseConnection();
    }

    private boolean selectByPrimaryKey(String primaryKey)
        throws SQLException {
        makeConnection();

        String selectStatement =
            "select id " + "from savingsaccount where id = ? ";
        PreparedStatement prepStmt = con.prepareStatement(selectStatement);

        prepStmt.setString(1, primaryKey);

        ResultSet rs = prepStmt.executeQuery();
        boolean result = rs.next();

        prepStmt.close();
        releaseConnection();

        return result;
    }

    private Collection selectByLastName(String lastName)
        throws SQLException {
        makeConnection();

        String selectStatement =
            "select id " + "from savingsaccount where lastname = ? ";
        PreparedStatement prepStmt = con.prepareStatement(selectStatement);

        prepStmt.setString(1, lastName);

        ResultSet rs = prepStmt.executeQuery();
        ArrayList a = new ArrayList();

        while (rs.next()) {
            String id = rs.getString(1);

            a.add(id);
        }

        prepStmt.close();
        releaseConnection();

        return a;
    }

    private Collection selectInRange(BigDecimal low, BigDecimal high)
        throws SQLException {
        makeConnection();

        String selectStatement =
            "select id from savingsaccount " +
            "where balance between  ? and ?";
        PreparedStatement prepStmt = con.prepareStatement(selectStatement);

        prepStmt.setBigDecimal(1, low);
        prepStmt.setBigDecimal(2, high);

        ResultSet rs = prepStmt.executeQuery();
        ArrayList a = new ArrayList();

        while (rs.next()) {
            String id = rs.getString(1);

            a.add(id);
        }

        prepStmt.close();
        releaseConnection();

        return a;
    }

    private void loadRow() throws SQLException {
        makeConnection();

        String selectStatement =
            "select firstname, lastname, balance " +
            "from savingsaccount where id = ? ";
        PreparedStatement prepStmt = con.prepareStatement(selectStatement);

        prepStmt.setString(1, this.id);

        ResultSet rs = prepStmt.executeQuery();

        if (rs.next()) {
            this.firstName = rs.getString(1);
            this.lastName = rs.getString(2);
            this.balance = rs.getBigDecimal(3);
            prepStmt.close();
        } else {
            prepStmt.close();
            throw new NoSuchEntityException("Row for id " + id +
                " not found in database.");
        }

        releaseConnection();
    }

    private void storeRow() throws SQLException {
        makeConnection();

        String updateStatement =
            "update savingsaccount set firstname =  ? ," +
            "lastname = ? , balance = ? " + "where id = ?";
        PreparedStatement prepStmt = con.prepareStatement(updateStatement);

        prepStmt.setString(1, firstName);
        prepStmt.setString(2, lastName);
        prepStmt.setBigDecimal(3, balance);
        prepStmt.setString(4, id);

        int rowCount = prepStmt.executeUpdate();

        prepStmt.close();

        if (rowCount == 0) {
            throw new EJBException("Storing row for id " + id + " failed.");
        }

        releaseConnection();
    }
} // SavingsAccountBean 
