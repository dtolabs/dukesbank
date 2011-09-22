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
import java.math.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.rmi.PortableRemoteObject;


public class SavingsAccountClient {
    public static void main(String[] args) {
        try {
            Context initial = new InitialContext();
            Object objref =
                initial.lookup("java:comp/env/ejb/SimpleSavingsAccount");

            SavingsAccountHome home =
                (SavingsAccountHome) PortableRemoteObject.narrow(objref,
                    SavingsAccountHome.class);

            BigDecimal zeroAmount = new BigDecimal("0.00");
            SavingsAccount duke =
                home.create("123", "Duke", "Earl", zeroAmount);

            duke.credit(new BigDecimal("88.50"));
            duke.debit(new BigDecimal("20.25"));

            BigDecimal balance = duke.getBalance();

            System.out.println("balance = " + balance);
            duke.remove();

            SavingsAccount joe = home.create("836", "Joe", "Jones", zeroAmount);

            joe.credit(new BigDecimal("34.55"));

            SavingsAccount jones = home.findByPrimaryKey("836");

            jones.debit(new BigDecimal("2.00"));
            balance = jones.getBalance();
            System.out.println("balance = " + balance);

            SavingsAccount pat = home.create("456", "Pat", "Smith", zeroAmount);

            pat.credit(new BigDecimal("44.77"));

            SavingsAccount john =
                home.create("730", "John", "Smith", zeroAmount);

            john.credit(new BigDecimal("19.54"));

            SavingsAccount mary =
                home.create("268", "Mary", "Smith", zeroAmount);

            mary.credit(new BigDecimal("100.07"));

            Collection c = home.findByLastName("Smith");
            Iterator i = c.iterator();

            while (i.hasNext()) {
                SavingsAccount account = (SavingsAccount) i.next();
                String id = (String) account.getPrimaryKey();
                BigDecimal amount = account.getBalance();

                System.out.println(id + ": " + amount);
            }

            c = home.findInRange(new BigDecimal("20.00"),
                    new BigDecimal("99.00"));
            i = c.iterator();

            while (i.hasNext()) {
                SavingsAccount account = (SavingsAccount) i.next();
                String id = (String) account.getPrimaryKey();
                BigDecimal amount = account.getBalance();

                System.out.println(id + ": " + amount);
            }

            SavingsAccount pete =
                home.create("904", "Pete", "Carlson", new BigDecimal("5.00"));
            SavingsAccount sally =
                home.create("905", "Sally", "Fortney", new BigDecimal("8.00"));

            home.chargeForLowBalance(new BigDecimal("10.00"),
                new BigDecimal("1.00"));

            BigDecimal reducedAmount = pete.getBalance();

            System.out.println(reducedAmount);
            reducedAmount = sally.getBalance();
            System.out.println(reducedAmount);

            System.exit(0);
        } catch (InsufficientBalanceException ex) {
            System.err.println("Caught an InsufficientBalanceException: " +
                ex.getMessage());
        } catch (Exception ex) {
            System.err.println("Caught an exception.");
            ex.printStackTrace();
        }
    }
}
