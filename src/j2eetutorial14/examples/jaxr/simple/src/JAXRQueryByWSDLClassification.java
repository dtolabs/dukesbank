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
import java.util.*;


/**
 * The JAXRQueryByWSDLClassification class consists of a main
 * method, a makeConnection method, an executeQuery method, and
 * some private helper methods. It searches a registry for
 * information about organizations that offer services based
 * on technical specifications that take the form of WSDL
 * documents. Use the query-string argument to limit the search
 * to certain specification concept names.
 */
public class JAXRQueryByWSDLClassification {
    Connection connection = null;

    public JAXRQueryByWSDLClassification() {
    }

    public static void main(String[] args) {
        ResourceBundle bundle = ResourceBundle.getBundle("JAXRExamples");

        String queryURL = bundle.getString("query.url");
        String publishURL = bundle.getString("publish.url");

        if (args.length < 1) {
            System.out.println("Argument required: " +
                "-Dquery-string=<value>");
            System.exit(1);
        }

        String queryString = new String(args[0]);
        System.out.println("Query string is " + queryString);

        JAXRQueryByWSDLClassification jq = new JAXRQueryByWSDLClassification();

        jq.makeConnection(queryURL, publishURL);

        jq.executeQuery(queryString);
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
         * For simple queries, you need the query URL.
         * To use a life cycle manager, you need the publish URL.
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
     * Searches for organizations using a WSDL
     * classification and displays data about them.
     *
     * @param qString        the string argument
     */
    public void executeQuery(String qString) {
        RegistryService rs = null;
        BusinessQueryManager bqm = null;
        BusinessLifeCycleManager blcm = null;

        try {
            // Get registry service and managers
            rs = connection.getRegistryService();
            bqm = rs.getBusinessQueryManager();
            blcm = rs.getBusinessLifeCycleManager();
            System.out.println("Got registry service, query " +
                "manager, and lifecycle manager");

            ResourceBundle bundle = ResourceBundle.getBundle("JAXRExamples");

            /*
             * Find the uddi-org:types classification scheme defined
             * by the UDDI specification, using well-known key id.
             */
            String uuid_types = "UUID:C1ACF26D-9672-4404-9D70-39B756E62AB4";
            ClassificationScheme uddiOrgTypes =
                (ClassificationScheme) bqm.getRegistryObject(uuid_types,
                    LifeCycleManager.CLASSIFICATION_SCHEME);

            // Define name pattern
            Collection namePatterns = new ArrayList();
            namePatterns.add("%" + qString + "%");

            /*
             * Create a classification, specifying the scheme
             *  and the taxonomy name and value defined for WSDL
             *  documents by the UDDI specification.
             */
            Classification wsdlSpecClassification =
                blcm.createClassification(uddiOrgTypes,
                    blcm.createInternationalString("wsdlSpec"), "wsdlSpec");

            Collection classifications = new ArrayList();
            classifications.add(wsdlSpecClassification);

            // Find concepts
            BulkResponse br =
                bqm.findConcepts(null, namePatterns, classifications, null, null);
            Collection specConcepts = br.getCollection();

            // Display information about the concepts found
            Iterator iter = specConcepts.iterator();

            if (!iter.hasNext()) {
                System.out.println("No WSDL specification concepts found");
            } else {
                int count = 0;

                while (iter.hasNext()) {
                    count++;

                    Concept concept = (Concept) iter.next();

                    String name = getName(concept);

                    Collection links = concept.getExternalLinks();

                    System.out.println("\nSpecification Concept:\n\tName: " +
                        name + "\n\tKey: " + getKey(concept) +
                        "\n\tDescription: " + getDescription(concept));

                    if (links.size() > 0) {
                        ExternalLink link =
                            (ExternalLink) links.iterator()
                                                .next();
                        System.out.println("\tURL of WSDL document: '" +
                            link.getExternalURI() + "'");
                    }

                    // Find organizations that use this concept
                    Collection specConcepts1 = new ArrayList();
                    specConcepts1.add(concept);
                    br = bqm.findOrganizations(null, null, null, specConcepts1,
                            null, null);

                    Collection orgs = br.getCollection();

                    // Display information about organizations
                    Iterator orgIter = orgs.iterator();

                    if (orgIter.hasNext()) {
                        System.out.println("Organizations using the '" + name +
                            "' WSDL Specification:");
                    } else {
                        System.out.println("No Organizations using the '" +
                            name + "' WSDL Specification");
                    }

                    while (orgIter.hasNext()) {
                        Organization org = (Organization) orgIter.next();
                        System.out.println("\tName: " + getName(org) +
                            "\n\tKey: " + getKey(org) + "\n\tDescription: " +
                            getDescription(org));
                    }
                }

                System.out.println("Found " + count + " concept(s)");
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

    /**
     * Returns the name value for a registry object.
     *
     * @param ro        a RegistryObject
     * @return                the String value
     */
    private String getName(RegistryObject ro) throws JAXRException {
        try {
            return ro.getName()
                     .getValue();
        } catch (NullPointerException npe) {
            return "No Name";
        }
    }

    /**
     * Returns the description value for a registry object.
     *
     * @param ro        a RegistryObject
     * @return                the String value
     */
    private String getDescription(RegistryObject ro) throws JAXRException {
        try {
            return ro.getDescription()
                     .getValue();
        } catch (NullPointerException npe) {
            return "No Description";
        }
    }

    /**
     * Returns the key id value for a registry object.
     *
     * @param ro        a RegistryObject
     * @return                the String value
     */
    private String getKey(RegistryObject ro) throws JAXRException {
        try {
            return ro.getKey()
                     .getId();
        } catch (NullPointerException npe) {
            return "No Key";
        }
    }
}
