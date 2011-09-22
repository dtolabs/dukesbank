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
 * The JAXRQuery class consists of a main method, a
 * makeConnection method, an executeQuery method, and some
 * private helper methods. It searches a registry for
 * information about organizations whose names contain a
 * user-supplied string.
 */
public class JAXRQuery {
    Connection connection = null;

    public JAXRQuery() {
    }

    public static void main(String[] args) {
        ResourceBundle registryBundle =
            ResourceBundle.getBundle("JAXRExamples");

        String queryURL = registryBundle.getString("query.url");
        String publishURL = registryBundle.getString("publish.url");

        if (args.length < 1) {
            System.out.println("Argument required: " +
                "-Dquery-string=<value>");
            System.exit(1);
        }

        String queryString = new String(args[0]);
        System.out.println("Query string is " + queryString);

        JAXRQuery jq = new JAXRQuery();

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
        ResourceBundle registryBundle =
            ResourceBundle.getBundle("JAXRExamples");
        String httpProxyHost = registryBundle.getString("http.proxyHost");
        String httpProxyPort = registryBundle.getString("http.proxyPort");

        /*
         * Define connection configuration properties.
         * For simple queries, you need the query URL.
         */
        Properties props = new Properties();
        props.setProperty("javax.xml.registry.queryManagerURL", queryUrl);
        props.setProperty("com.sun.xml.registry.http.proxyHost", httpProxyHost);
        props.setProperty("com.sun.xml.registry.http.proxyPort", httpProxyPort);

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
     * Searches for organizations containing a string and
     * displays data about them.
     *
     * @param qString        the string argument
     */
    public void executeQuery(String qString) {
        RegistryService rs = null;
        BusinessQueryManager bqm = null;

        try {
            // Get registry service and query manager
            rs = connection.getRegistryService();
            bqm = rs.getBusinessQueryManager();
            System.out.println("Got registry service and " + "query manager");

            // Define find qualifiers and name patterns
            Collection findQualifiers = new ArrayList();
            findQualifiers.add(FindQualifier.SORT_BY_NAME_ASC);

            Collection namePatterns = new ArrayList();
            namePatterns.add("%" + qString + "%");

            // Find using the name
            BulkResponse response =
                bqm.findOrganizations(findQualifiers, namePatterns, null, null,
                    null, null);
            Collection orgs = response.getCollection();

            // Display information about the organizations found
            Iterator orgIter = orgs.iterator();

            if (!(orgIter.hasNext())) {
                System.out.println("No organizations found");
            } else {
                int count = 0;

                while (orgIter.hasNext()) {
                    count++;

                    Organization org = (Organization) orgIter.next();
                    System.out.println("Org name: " + getName(org));
                    System.out.println("Org description: " +
                        getDescription(org));
                    System.out.println("Org key id: " + getKey(org));

                    // Display primary contact information
                    User pc = org.getPrimaryContact();

                    if (pc != null) {
                        PersonName pcName = pc.getPersonName();
                        System.out.println(" Contact name: " +
                            pcName.getFullName());

                        Collection phNums = pc.getTelephoneNumbers(null);
                        Iterator phIter = phNums.iterator();

                        while (phIter.hasNext()) {
                            TelephoneNumber num =
                                (TelephoneNumber) phIter.next();
                            System.out.println("  Phone number: " +
                                num.getNumber());
                        }

                        Collection eAddrs = pc.getEmailAddresses();
                        Iterator eaIter = eAddrs.iterator();

                        while (eaIter.hasNext()) {
                            EmailAddress eAd = (EmailAddress) eaIter.next();
                            System.out.println("  Email address: " +
                                eAd.getAddress());
                        }
                    }

                    // Display service and binding information
                    Collection services = org.getServices();
                    Iterator svcIter = services.iterator();

                    while (svcIter.hasNext()) {
                        Service svc = (Service) svcIter.next();
                        System.out.println(" Service name: " + getName(svc));
                        System.out.println(" Service description: " +
                            getDescription(svc));

                        Collection serviceBindings = svc.getServiceBindings();
                        Iterator sbIter = serviceBindings.iterator();

                        while (sbIter.hasNext()) {
                            ServiceBinding sb = (ServiceBinding) sbIter.next();
                            System.out.println("  Binding " + "description: " +
                                getDescription(sb));
                            System.out.println("  Access URI: " +
                                sb.getAccessURI());
                        }
                    }

                    // Print spacer between organizations
                    System.out.println(" --- ");
                }

                System.out.println("Found " + count + " organization(s)");
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
