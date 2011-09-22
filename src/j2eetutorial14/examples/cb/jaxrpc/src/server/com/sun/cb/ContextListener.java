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


package com.sun.cb;

import javax.servlet.*;
import javax.xml.registry.*;
import java.util.ResourceBundle;
import java.util.logging.*;
import java.io.*;
import javax.naming.*;
import java.net.*;
import java.util.Properties;


public final class ContextListener implements ServletContextListener {
    static final Logger logger =
        Logger.getLogger("jaxrpc.server.com.sun.cb.ContextListener");
    ConnectionFactory factory = null;

    private Connection makeConnection(String queryURL, String publishURL) {
        Context context = null;
        Connection connection = null;

        ResourceBundle registryBundle =
            ResourceBundle.getBundle("com.sun.cb.CoffeeBreak");

        String httpProxyHost = registryBundle.getString("http.proxyHost");
        String httpProxyPort = registryBundle.getString("http.proxyPort");
        String httpsProxyHost = registryBundle.getString("https.proxyHost");
        String httpsProxyPort = registryBundle.getString("https.proxyPort");

        /*
         * Define connection configuration properties.
         * To delete, you need both the query URL and the
         * publish URL.
         */
        Properties props = new Properties();
        props.setProperty("javax.xml.registry.queryManagerURL", queryURL);
        props.setProperty("javax.xml.registry.lifeCycleManagerURL", publishURL);
        props.setProperty("com.sun.xml.registry.http.proxyHost", httpProxyHost);
        props.setProperty("com.sun.xml.registry.http.proxyPort", httpProxyPort);
        props.setProperty("com.sun.xml.registry.https.proxyHost", httpsProxyHost);
        props.setProperty("com.sun.xml.registry.https.proxyPort", httpsProxyPort);

        try {
            // Create the connection, passing it the 
            // configuration properties
            if (factory == null) {
                context = new InitialContext();
                factory = (ConnectionFactory) context.lookup(
                        "java:comp/env/eis/JAXR");
                factory.setProperties(props);
            }

            connection = factory.createConnection();
            logger.info("Created connection to registry");

            return connection;
        } catch (Exception e) {
            logger.severe("makeConnection: Exception: " + e.toString());

            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception je) {
                }
            }

            return null;
        }
    }

    public void contextInitialized(ServletContextEvent event) {
        ResourceBundle registryBundle =
            ResourceBundle.getBundle("com.sun.cb.CoffeeBreak");

        String queryURL = registryBundle.getString("query.url");
        String publishURL = registryBundle.getString("publish.url");
        logger.info(queryURL);
        logger.info(publishURL);

        String username = registryBundle.getString("registry.username");
        String password = registryBundle.getString("registry.password");
        String keyFile = registryBundle.getString("key.file");

        JAXRPublisher publisher = new JAXRPublisher();
        ServletContext context = event.getServletContext();

        String endpointURL = URLHelper.getEndpointURL();

        Connection connection = makeConnection(queryURL, publishURL);

        if (connection != null) {
            String key =
                publisher.executePublish(connection, username, password,
                    endpointURL);

            try {
                FileWriter out = new FileWriter(keyFile);
                out.write(key);
                out.flush();
                out.close();
            } catch (IOException ex) {
                logger.severe(ex.getMessage());
            }

            try {
                connection.close();
            } catch (Exception je) {
            }
        }
    }

    public void contextDestroyed(ServletContextEvent event) {
        String keyStr = null;
        ServletContext context = event.getServletContext();

        ResourceBundle registryBundle =
            ResourceBundle.getBundle("com.sun.cb.CoffeeBreak");

        String queryURL = registryBundle.getString("query.url");
        String publishURL = registryBundle.getString("publish.url");
        String username = registryBundle.getString("registry.username");
        String password = registryBundle.getString("registry.password");
        String keyFile = registryBundle.getString("key.file");

        try {
            FileReader in = new FileReader(keyFile);
            char[] buf = new char[512];

            while (in.read(buf, 0, 512) >= 0) {
            }

            in.close();
            keyStr = new String(buf).trim();
        } catch (IOException ex) {
            logger.severe("contextDestroyed: Exception: " + ex.toString());
        }

        JAXRRemover remover = new JAXRRemover();
        Connection connection = makeConnection(queryURL, publishURL);

        if (connection != null) {
            javax.xml.registry.infomodel.Key modelKey = null;
            modelKey = remover.createOrgKey(connection, keyStr);
            remover.executeRemove(connection, modelKey, username, password);

            try {
                connection.close();
            } catch (Exception je) {
            }
        }
    }
}
