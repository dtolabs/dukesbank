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
 * The JAXRPublish class consists of a main method, a
 * makeConnection method, and an executePublish method.
 * It creates an organization and publishes it to a registry.
 */
public class JAXRPublish {
    Connection connection = null;

    public JAXRPublish() {
    }

    public static void main(String[] args) {
        ResourceBundle bundle = ResourceBundle.getBundle("JAXRExamples");

        String queryURL = bundle.getString("query.url");
        String publishURL = bundle.getString("publish.url");

        String username = bundle.getString("registry.username");
        String password = bundle.getString("registry.password");

        JAXRPublish jp = new JAXRPublish();

        jp.makeConnection(queryURL, publishURL);

        jp.executePublish(username, password);
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
         * To publish, you need both the query URL and the
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
     * Creates an organization, its classification, and its
     * services, and saves it to the registry.
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

            // Create organization name and description
            InternationalString s =
                blcm.createInternationalString(bundle.getString("org.name"));
            Organization org = blcm.createOrganization(s);
            s = blcm.createInternationalString(bundle.getString(
                        "org.description"));
            org.setDescription(s);

            // Create primary contact, set name
            User primaryContact = blcm.createUser();
            PersonName pName =
                blcm.createPersonName(bundle.getString("person.name"));
            primaryContact.setPersonName(pName);

            // Set primary contact phone number
            TelephoneNumber tNum = blcm.createTelephoneNumber();
            tNum.setNumber(bundle.getString("phone.number"));

            Collection phoneNums = new ArrayList();
            phoneNums.add(tNum);
            primaryContact.setTelephoneNumbers(phoneNums);

            // Set primary contact email address
            EmailAddress emailAddress =
                blcm.createEmailAddress(bundle.getString("email.address"));
            Collection emailAddresses = new ArrayList();
            emailAddresses.add(emailAddress);
            primaryContact.setEmailAddresses(emailAddresses);

            // Set primary contact for organization
            org.setPrimaryContact(primaryContact);

            // Set classification scheme to NAICS, using
            // well-known UUID of ntis-gov:naics:1997
            String uuid_naics = "UUID:C0B9FE13-179F-413D-8A5B-5004DB8E5BB2";
            ClassificationScheme cScheme =
                (ClassificationScheme) bqm.getRegistryObject(uuid_naics,
                    LifeCycleManager.CLASSIFICATION_SCHEME);

            if (cScheme != null) {
                // Create and add classification
                InternationalString sn =
                    blcm.createInternationalString(bundle.getString(
                            "classification.name"));
                Classification classification =
                    blcm.createClassification(cScheme, sn,
                        bundle.getString("classification.value"));
                Collection classifications = new ArrayList();
                classifications.add(classification);
                org.addClassifications(classifications);
            } else {
                System.out.println("Classification scheme not found, " +
                    "not classifying organization");
            }

            // Create services and service
            Collection services = new ArrayList();
            s = blcm.createInternationalString(bundle.getString("service.name"));

            Service service = blcm.createService(s);
            s = blcm.createInternationalString(bundle.getString(
                        "service.description"));
            service.setDescription(s);

            // Create service bindings
            Collection serviceBindings = new ArrayList();
            ServiceBinding binding = blcm.createServiceBinding();
            s = blcm.createInternationalString(bundle.getString(
                        "svcbinding.description"));
            binding.setDescription(s);

            // allow us to publish a fictitious URI without an error
            binding.setValidateURI(false);
            binding.setAccessURI(bundle.getString("svcbinding.accessURI"));
            serviceBindings.add(binding);

            // Add service bindings to service
            service.addServiceBindings(serviceBindings);

            // Add service to services, then add services to organization
            services.add(service);
            org.addServices(services);

            // Add organization and submit to registry
            // Retrieve key if successful
            Collection orgs = new ArrayList();
            orgs.add(org);

            BulkResponse response = blcm.saveOrganizations(orgs);
            Collection exceptions = response.getExceptions();

            if (exceptions == null) {
                System.out.println("Organization saved");

                Collection keys = response.getCollection();
                Iterator keyIter = keys.iterator();

                if (keyIter.hasNext()) {
                    javax.xml.registry.infomodel.Key orgKey =
                        (javax.xml.registry.infomodel.Key) keyIter.next();
                    String id = orgKey.getId();
                    System.out.println("Organization key is " + id);
                }
            } else {
                Iterator excIter = exceptions.iterator();
                Exception exception = null;

                while (excIter.hasNext()) {
                    exception = (Exception) excIter.next();
                    System.err.println("Exception on save: " +
                        exception.toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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
