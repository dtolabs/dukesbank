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
 * The JAXRPublishHelloOrg class consists of a main method, a
 * makeConnection method, and an executePublish method.
 * It creates an organization and publishes it to a registry.
 * The organization has a service binding that includes a WSDL file.
 */
public class JAXRPublishHelloOrg {
    Connection connection = null;

    public JAXRPublishHelloOrg() {
    }

    public static void main(String[] args) {
        ResourceBundle bundle = ResourceBundle.getBundle("JAXRExamples");

        String queryURL = bundle.getString("query.url");
        String publishURL = bundle.getString("publish.url");

        String username = bundle.getString("registry.username");
        String password = bundle.getString("registry.password");

        if (args.length < 1) {
            System.out.println("Argument required: " + "-Duuid-string=<value>");
            System.exit(1);
        }

        String uuidString = new String(args[0]);
        System.out.println("UUID string is " + uuidString);

        JAXRPublishHelloOrg jpws = new JAXRPublishHelloOrg();

        jpws.makeConnection(queryURL, publishURL);

        jpws.executePublish(uuidString, username, password);
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
    public void executePublish(String uuidString, String username,
        String password) {
        RegistryService rs = null;
        BusinessLifeCycleManager blcm = null;
        BusinessQueryManager bqm = null;

        try {
            // Get registry service and managers
            rs = connection.getRegistryService();
            bqm = rs.getBusinessQueryManager();
            blcm = rs.getBusinessLifeCycleManager();
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
                blcm.createInternationalString(bundle.getString("wsdlorg.name"));
            Organization org = blcm.createOrganization(s);
            s = blcm.createInternationalString(bundle.getString(
                        "wsdlorg.description"));
            org.setDescription(s);

            // Create primary contact, set name
            User primaryContact = blcm.createUser();
            PersonName pName =
                blcm.createPersonName(bundle.getString("wsdlorg.person.name"));
            primaryContact.setPersonName(pName);
            s = blcm.createInternationalString(bundle.getString(
                        "wsdlorg.person.description"));
            primaryContact.setDescription(s);

            // Set primary contact phone number
            TelephoneNumber tNum = blcm.createTelephoneNumber();
            tNum.setNumber(bundle.getString("wsdlorg.phone"));

            Collection phoneNums = new ArrayList();
            phoneNums.add(tNum);
            primaryContact.setTelephoneNumbers(phoneNums);

            // Set primary contact email address
            EmailAddress emailAddress =
                blcm.createEmailAddress(bundle.getString(
                        "wsdlorg.email.address"));
            Collection emailAddresses = new ArrayList();
            emailAddresses.add(emailAddress);
            primaryContact.setEmailAddresses(emailAddresses);

            // Set primary contact for organization
            org.setPrimaryContact(primaryContact);

            // Create services and service
            Collection services = new ArrayList();
            s = blcm.createInternationalString(bundle.getString(
                        "wsdlorg.svc.name"));

            Service service = blcm.createService(s);
            s = blcm.createInternationalString(bundle.getString(
                        "wsdlorg.svc.description"));
            service.setDescription(s);

            // Create service bindings
            Collection serviceBindings = new ArrayList();
            ServiceBinding binding = blcm.createServiceBinding();
            s = blcm.createInternationalString(bundle.getString(
                        "wsdlorg.svcbnd.description"));
            binding.setDescription(s);
            binding.setAccessURI(bundle.getString("wsdlorg.svcbnd.uri"));

            /*
             * Find the uddi-org:types classification scheme defined
             * by the UDDI specification, using well-known key id.
             */
            String uuid_types = "UUID:C1ACF26D-9672-4404-9D70-39B756E62AB4";
            ClassificationScheme uddiOrgTypes =
                (ClassificationScheme) bqm.getRegistryObject(uuid_types,
                    LifeCycleManager.CLASSIFICATION_SCHEME);

            /*
             * Create a classification, specifying the scheme
             *  and the taxonomy name and value defined for WSDL
             *  documents by the UDDI specification.
             */
            Classification wsdlSpecClassification =
                blcm.createClassification(uddiOrgTypes,
                    blcm.createInternationalString("wsdlSpec"), "wsdlSpec");

            // Define classifications
            Collection classifications = new ArrayList();
            classifications.add(wsdlSpecClassification);

            // Find the concept by its UUID
            Concept specConcept =
                (Concept) bqm.getRegistryObject(uuidString,
                    LifeCycleManager.CONCEPT);

            // If we found the concept, we can save the organization
            if (specConcept != null) {
                String name = getName(specConcept);

                Collection links = specConcept.getExternalLinks();

                System.out.println("\nSpecification Concept:\n\tName: " + name +
                    "\n\tKey: " + getKey(specConcept));

                if (links.size() > 0) {
                    ExternalLink link = (ExternalLink) links.iterator()
                                                            .next();
                    System.out.println("\tURL of WSDL document: '" +
                        link.getExternalURI() + "'");
                }

                // Now set the specification link for the service binding
                SpecificationLink specLink = blcm.createSpecificationLink();
                specLink.setSpecificationObject(specConcept);

                binding.addSpecificationLink(specLink);
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
            } else {
                System.out.println("Specified concept not found, " +
                    "organization not saved");
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
