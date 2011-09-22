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


import javax.xml.registry.*;
import javax.xml.registry.infomodel.*;
import java.net.*;
import java.security.*;
import java.util.*;


/**
 * The JAXRSaveClassificationScheme class consists of a main method, a
 * makeConnection method, and an executePublish method.
 * It creates a classification scheme and publishes it to a registry.
 */
public class JAXRSaveClassificationScheme {
    Connection connection = null;

    public JAXRSaveClassificationScheme() {
    }

    public static void main(String[] args) {
        ResourceBundle bundle = ResourceBundle.getBundle("JAXRExamples");

        String queryURL = bundle.getString("query.url");
        String publishURL = bundle.getString("publish.url");

        // Edit to provide your own username and password
        // Defaults for Registry Server are testuser/testuser
        String username = bundle.getString("registry.username");
        String password = bundle.getString("registry.password");

        JAXRSaveClassificationScheme jscs = new JAXRSaveClassificationScheme();

        jscs.makeConnection(queryURL, publishURL);

        jscs.executePublish(username, password);
    }

    /**
     * Establishes a connection to a registry.
     *
     * @param queryUrl        the URL of the query registry
     * @param publishUrl        the URL of the publish registry
     */
    public void makeConnection(String queryUrl, String publishUrl) {
        /*
         * Specify proxy information in case you
         *  are going beyond your firewall.
         */
        ResourceBundle bundle = ResourceBundle.getBundle("JAXRExamples");
        String httpProxyHost = bundle.getString("http.proxyHost");
        String httpProxyPort = bundle.getString("http.proxyPort");
        String httpsProxyHost = bundle.getString("https.proxyHost");
        String httpsProxyPort = bundle.getString("https.proxyPort");

        /*
         * Define connection configuration properties.
         * To delete, you need both the query URL and the
         * publish URL.
         */
        Properties props = new Properties();
        props.setProperty("javax.xml.registry.queryManagerURL", queryUrl);
        props.setProperty("javax.xml.registry.lifeCycleManagerURL", publishUrl);
        props.setProperty("com.sun.xml.registry.http.proxyHost", httpProxyHost);
        props.setProperty("com.sun.xml.registry.http.proxyPort", httpProxyPort);
        props.setProperty("com.sun.xml.registry.https.proxyHost", httpsProxyHost);
        props.setProperty("com.sun.xml.registry.https.proxyPort", httpsProxyPort);

        try {
            // Create the connection, passing it the 
            // configuration properties
            ConnectionFactory factory = ConnectionFactory.newInstance();
            factory.setProperties(props);
            connection = factory.createConnection();
            System.out.println("Created connection to registry");
        } catch (Exception e) {
            e.printStackTrace();

            if (connection != null) {
                try {
                    connection.close();
                } catch (JAXRException je) {
                }
            }
        }
    }

    /**
     * Creates a classification scheme and saves it to the
     * registry.
     *
     * @param username  the username for the registry
     * @param password  the password for the registry
     */
    public void executePublish(String username, String password) {
        RegistryService rs = null;
        BusinessLifeCycleManager blcm = null;
        BusinessQueryManager bqm = null;

        try {
            rs = connection.getRegistryService();
            blcm = rs.getBusinessLifeCycleManager();
            bqm = rs.getBusinessQueryManager();
            System.out.println("Got registry service, query " +
                "manager, and life cycle manager");

            // Get authorization from the registry
            PasswordAuthentication passwdAuth =
                new PasswordAuthentication(username, password.toCharArray());

            Set creds = new HashSet();
            creds.add(passwdAuth);
            connection.setCredentials(creds);
            System.out.println("Established security credentials");

            ResourceBundle bundle = ResourceBundle.getBundle("JAXRExamples");

            // Create classification scheme
            InternationalString sn =
                blcm.createInternationalString(bundle.getString(
                        "postal.scheme.name"));
            InternationalString sd =
                blcm.createInternationalString(bundle.getString(
                        "postal.scheme.description"));
            ClassificationScheme postalScheme =
                blcm.createClassificationScheme(sn, sd);

            /*
             * Find the uddi-org:types classification scheme defined
             * by the UDDI specification, using well-known key id.
             */
            String uuid_types = "UUID:C1ACF26D-9672-4404-9D70-39B756E62AB4";
            ClassificationScheme uddiOrgTypes =
                (ClassificationScheme) bqm.getRegistryObject(uuid_types,
                    LifeCycleManager.CLASSIFICATION_SCHEME);

            if (uddiOrgTypes != null) {
                InternationalString cn =
                    blcm.createInternationalString(bundle.getString(
                            "postal.classification.name"));
                Classification classification =
                    blcm.createClassification(uddiOrgTypes, cn,
                        bundle.getString("postal.classification.value"));

                postalScheme.addClassification(classification);

                /*
                 * Set link to location of postal scheme (fictitious)
                 * so others can look it up. If the URI were valid, we
                 * could use the createExternalLink method.
                 */
                ExternalLink externalLink =
                    (ExternalLink) blcm.createObject(LifeCycleManager.EXTERNAL_LINK);
                externalLink.setValidateURI(false);
                externalLink.setExternalURI(bundle.getString(
                        "postal.scheme.link"));

                InternationalString is =
                    blcm.createInternationalString(bundle.getString(
                            "postal.scheme.linkdesc"));
                externalLink.setDescription(is);
                postalScheme.addExternalLink(externalLink);

                // Add scheme and save it to registry
                // Retrieve key if successful
                Collection schemes = new ArrayList();
                schemes.add(postalScheme);

                BulkResponse br = blcm.saveClassificationSchemes(schemes);

                if (br.getStatus() == JAXRResponse.STATUS_SUCCESS) {
                    System.out.println("Saved PostalAddress " +
                        "ClassificationScheme");

                    Collection schemeKeys = br.getCollection();
                    Iterator keysIter = schemeKeys.iterator();

                    while (keysIter.hasNext()) {
                        javax.xml.registry.infomodel.Key key =
                            (javax.xml.registry.infomodel.Key) keysIter.next();
                        System.out.println("The postalScheme key is " +
                            key.getId());
                        System.out.println("Use this key as the scheme uuid " +
                            "in the postalconcepts.xml file\n  and as the " +
                            "argument to run-publish-postal and " +
                            "run-query-postal");
                    }
                } else {
                    Collection exceptions = br.getExceptions();
                    Iterator excIter = exceptions.iterator();
                    Exception exception = null;

                    while (excIter.hasNext()) {
                        exception = (Exception) excIter.next();
                        System.err.println("Exception on save: " +
                            exception.toString());
                    }
                }
            } else {
                System.out.println("uddi-org:types not found. Unable to " +
                    "save PostalAddress scheme.");
            }
        } catch (JAXRException jaxe) {
            jaxe.printStackTrace();
        } finally {
            // At end, close connection to registry
            if (connection != null) {
                try {
                    connection.close();
                } catch (JAXRException je) {
                }
            }
        }
    }
}
