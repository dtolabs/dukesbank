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


import javax.naming.*;
import javax.ejb.*;
import java.net.*;
import java.io.*;


public class HTMLReaderBean implements SessionBean {
    public HTMLReaderBean() {
    }

    public StringBuffer getContents() throws HTTPResponseException {
        Context context;
        URL url;
        StringBuffer buffer;
        String line;
        int responseCode;
        HttpURLConnection connection;
        InputStream input;
        BufferedReader dataInput;

        try {
            context = new InitialContext();
            url = (URL) context.lookup("java:comp/env/url/MyURL");
            connection = (HttpURLConnection) url.openConnection();
            responseCode = connection.getResponseCode();
        } catch (Exception ex) {
            throw new EJBException(ex.getMessage());
        }

        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new HTTPResponseException("HTTP response code: " +
                String.valueOf(responseCode));
        }

        try {
            buffer = new StringBuffer();
            input = connection.getInputStream();
            dataInput = new BufferedReader(new InputStreamReader(input));

            while ((line = dataInput.readLine()) != null) {
                buffer.append(line);
                buffer.append('\n');
            }
        } catch (Exception ex) {
            throw new EJBException(ex.getMessage());
        }

        return buffer;
    } // getContents()

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
} // HTMLReaderBean
