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
import java.util.*;
import java.util.logging.*;
import java.net.*;
import javax.ejb.*;
import javax.naming.*;
import javax.xml.registry.*;
import javax.xml.registry.infomodel.*;


/**
 * Bean class for PubQuery enterprise bean. Defines findOrganization
 * business method as well as required methods for a stateless
 * session bean.
 */
public class PubQueryBean implements SessionBean {
    static final Logger logger = Logger.getLogger("PubQueryBean");
    SessionContext sc = null;
    Connection connection = null;
    ResourceBundle registryBundle =
        ResourceBundle.getBundle("PubQueryBeanExample");

    public PubQueryBean() {
        logger.info("In PubQueryBean() (constructor)");
    }

    /**
     * Sets the associated session context. The container calls
     * this method after the instance creation.
     */
    public void setSessionContext(SessionContext sc) {
        this.sc = sc;
    }

    /**
     * Instantiates the enterprise bean.  Creates the
     * connection and looks up the topic.
     */
    public void ejbCreate() {
        Context context = null;
        ConnectionFactory factory = null;

        logger.info("In PubQueryBean.ejbCreate()");

        try {
            String queryUrl = registryBundle.getString("query.url");
            String publishUrl = registryBundle.getString("publish.url");
            String httpProxyHost = registryBundle.getString("http.proxyHost");
            String httpProxyPort = registryBundle.getString("http.proxyPort");
            String httpsProxyHost = registryBundle.getString("https.proxyHost");
            String httpsProxyPort = registryBundle.getString("https.proxyPort");

            /*
             * Define connection configuration properties.
             * For simple queries, you need the query URL.
             */
            Properties props = new Properties();
            props.setProperty("javax.xml.registry.queryManagerURL", queryUrl);
            props.setProperty("javax.xml.registry.lifeCycleManagerURL",
                publishUrl);
            props.setProperty("com.sun.xml.registry.http.proxyHost",
                httpProxyHost);
            props.setProperty("com.sun.xml.registry.http.proxyPort",
                httpProxyPort);
            props.setProperty("com.sun.xml.registry.https.proxyHost",
                httpsProxyHost);
            props.setProperty("com.sun.xml.registry.https.proxyPort",
                httpsProxyPort);

            // Create a connection
            context = new InitialContext();
            factory = (ConnectionFactory) context.lookup(
                    "java:comp/env/eis/JAXR");
            factory.setProperties(props);
            connection = factory.createConnection();
            logger.info("Created connection to registry");
        } catch (Throwable t) {
            // JAXRException or NamingException could be thrown
            logger.severe("PubQueryBean.ejbCreate: Exception: " + t.toString());
        }
    }

    /**
     * Creates an organization and publishes it to a registry.
     */
    public void executePublish() throws EJBException {
        RegistryService rs = null;
        BusinessLifeCycleManager blcm = null;
        BusinessQueryManager bqm = null;

        try {
            logger.info("\nPublishing an organization:");
            rs = connection.getRegistryService();
            blcm = rs.getBusinessLifeCycleManager();
            bqm = rs.getBusinessQueryManager();
            logger.info("Got registry service, query manager, and " +
                "life cycle manager");

            // Get authorization from the registry
            String username = registryBundle.getString("registry.username");
            String password = registryBundle.getString("registry.password");
            PasswordAuthentication passwdAuth =
                new PasswordAuthentication(username, password.toCharArray());

            Set creds = new HashSet();
            creds.add(passwdAuth);
            connection.setCredentials(creds);
            logger.info("Established security credentials");

            // Create organization name and description
            InternationalString s =
                blcm.createInternationalString(registryBundle.getString(
                        "org.name"));
            Organization org = blcm.createOrganization(s);
            s = blcm.createInternationalString(registryBundle.getString(
                        "org.description"));
            org.setDescription(s);

            // Create primary contact, set name
            User primaryContact = blcm.createUser();
            PersonName pName =
                blcm.createPersonName(registryBundle.getString("person.name"));
            primaryContact.setPersonName(pName);

            // Set primary contact phone number
            TelephoneNumber tNum = blcm.createTelephoneNumber();
            tNum.setNumber(registryBundle.getString("phone.number"));

            Collection phoneNums = new ArrayList();
            phoneNums.add(tNum);
            primaryContact.setTelephoneNumbers(phoneNums);

            // Set primary contact email address
            EmailAddress emailAddress =
                blcm.createEmailAddress(registryBundle.getString(
                        "email.address"));
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
                    blcm.createInternationalString(registryBundle.getString(
                            "classification.name"));
                Classification classification =
                    blcm.createClassification(cScheme, sn,
                        registryBundle.getString("classification.value"));
                Collection classifications = new ArrayList();
                classifications.add(classification);
                org.addClassifications(classifications);
            } else {
                System.out.println("Classification scheme not found, " +
                    "not classifying organization");
            }

            // Create services and service
            Collection services = new ArrayList();
            s = blcm.createInternationalString(registryBundle.getString(
                        "service.name"));

            Service service = blcm.createService(s);
            s = blcm.createInternationalString(registryBundle.getString(
                        "service.description"));
            service.setDescription(s);

            // Create service bindings
            Collection serviceBindings = new ArrayList();
            ServiceBinding binding = blcm.createServiceBinding();
            s = blcm.createInternationalString(registryBundle.getString(
                        "svcbinding.description"));
            binding.setDescription(s);

            // allow us to publish a fictitious URL without an error
            binding.setValidateURI(false);
            binding.setAccessURI(registryBundle.getString(
                    "svcbinding.accessURI"));
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
                logger.info("Organization saved");

                Collection keys = response.getCollection();
                Iterator keyIter = keys.iterator();

                if (keyIter.hasNext()) {
                    javax.xml.registry.infomodel.Key orgKey =
                        (javax.xml.registry.infomodel.Key) keyIter.next();
                    String id = orgKey.getId();
                    logger.info("Organization key is " + id);
                }
            } else {
                Iterator excIter = exceptions.iterator();
                Exception exception = null;

                while (excIter.hasNext()) {
                    exception = (Exception) excIter.next();
                    logger.severe("Exception on save: " + exception.toString());
                }
            }
        } catch (Throwable t) {
            // JAXRException could be thrown
            logger.severe("PubQueryBean.executePublish: Exception: " +
                t.toString());
            sc.setRollbackOnly();
        }
    }

    /**
     * Searches for organizations containing a string and
     * displays data about them.
     *
     * @param qString        the string argument
     */
    public void executeQuery(String qString) throws EJBException {
        RegistryService rs = null;
        BusinessQueryManager bqm = null;

        try {
            logger.info("\nQuerying for an organization:");

            // Get registry service and query manager
            rs = connection.getRegistryService();
            bqm = rs.getBusinessQueryManager();
            logger.info("Got registry service and query manager");

            // Define find qualifiers and name patterns
            Collection findQualifiers = new ArrayList();
            findQualifiers.add(FindQualifier.SORT_BY_NAME_DESC);

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
                logger.info("No organizations found");
            } else {
                while (orgIter.hasNext()) {
                    Organization org = (Organization) orgIter.next();
                    logger.info("Org name: " + getName(org));
                    logger.info("Org description: " + getDescription(org));
                    logger.info("Org key id: " + getKey(org));

                    // Display primary contact information
                    User pc = org.getPrimaryContact();

                    if (pc != null) {
                        PersonName pcName = pc.getPersonName();
                        logger.info("Contact name: " + pcName.getFullName());

                        Collection phNums = pc.getTelephoneNumbers(null);
                        Iterator phIter = phNums.iterator();

                        while (phIter.hasNext()) {
                            TelephoneNumber num =
                                (TelephoneNumber) phIter.next();
                            logger.info("Contact phone number: " +
                                num.getNumber());
                        }

                        Collection eAddrs = pc.getEmailAddresses();
                        Iterator eaIter = eAddrs.iterator();

                        while (eaIter.hasNext()) {
                            EmailAddress eAd = (EmailAddress) eaIter.next();
                            logger.info("Contact email address: " +
                                eAd.getAddress());
                        }
                    }

                    // Display service and binding information
                    Collection services = org.getServices();
                    Iterator svcIter = services.iterator();

                    while (svcIter.hasNext()) {
                        Service svc = (Service) svcIter.next();
                        logger.info("Service name: " + getName(svc));
                        logger.info("Service description: " +
                            getDescription(svc));

                        Collection serviceBindings = svc.getServiceBindings();
                        Iterator sbIter = serviceBindings.iterator();

                        while (sbIter.hasNext()) {
                            ServiceBinding sb = (ServiceBinding) sbIter.next();
                            logger.info("Service binding description: " +
                                getDescription(sb));
                            logger.info("Service binding access URI: " +
                                sb.getAccessURI());
                        }
                    }

                    // Print spacer between organizations
                    logger.info(" --- ");
                }
            }
        } catch (Throwable t) {
            // JAXRException could be thrown
            logger.severe("PubQueryBean.executeQuery: Exception: " +
                t.toString());
            sc.setRollbackOnly();
        }
    }

    /**
     * Helper method.
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
     * Helper method.
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
     * Helper method.
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

    /**
     * Closes the connection.
     */
    public void ejbRemove() throws RemoteException {
        logger.info("In PubQueryBean.ejbRemove()");

        if (connection != null) {
            try {
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void ejbActivate() {
    }

    public void ejbPassivate() {
    }
}
