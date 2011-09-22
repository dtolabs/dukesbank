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


import java.rmi.RemoteException;
import java.io.Serializable;
import java.io.IOException;
import java.util.*;
import java.text.*;
import javax.ejb.*;
import javax.mail.*;
import javax.activation.*;
import javax.mail.internet.*;
import javax.naming.*;


public class ConfirmerBean implements SessionBean {
    private static final String mailer = "JavaMailer";
    SessionContext sc;

    public ConfirmerBean() {
    }

    public void sendNotice(String recipient) {
        try {
            Context initial = new InitialContext();
            Session session =
                (Session) initial.lookup("java:comp/env/mail/TheMailSession");

            System.out.println("Found mail session " + session);

            Message msg = new MimeMessage(session);

            msg.setFrom();

            msg.setRecipients(Message.RecipientType.TO,
                InternetAddress.parse(recipient, false));

            msg.setSubject("Test Message from ConfirmerBean");

            DateFormat dateFormatter =
                DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT);

            Date timeStamp = new Date();

            String messageText =
                "Thank you for your order." + '\n' +
                "We received your order on " + dateFormatter.format(timeStamp) +
                ".";

            msg.setText(messageText);

            msg.setHeader("X-Mailer", mailer);
            msg.setSentDate(timeStamp);

            Transport.send(msg);

            System.out.println("Mail sent");
        } catch (Exception e) {
            throw new EJBException(e.getMessage());
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

    public void setSessionContext(SessionContext sc) {
    }
} // ConfirmerBean
