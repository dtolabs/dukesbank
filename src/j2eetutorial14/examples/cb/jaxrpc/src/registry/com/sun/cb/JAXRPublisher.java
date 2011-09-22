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

import javax.xml.registry.*;
import javax.xml.registry.infomodel.*;
import java.net.*;
import java.security.*;
import java.util.*;
import java.util.logging.*;
import javax.naming.*;


/**
 * The JAXRPublisher class consists of an executePublish
 * method, which creates an organization and publishes it
 * to the registry.
 */
public class JAXRPublisher {
    static final Logger logger =
        Logger.getLogger("jaxrpc.registry.com.sun.cb.JAXRPublisher");

    public JAXRPublisher() {
    }

    /**
     * Creates an organization, its classification, and its
     * services, and saves it to the registry.
     */
    public String executePublish(Connection connection, String username,
        String password, String endpoint) {
        String id = null;
        RegistryService rs = null;
        BusinessLifeCycleManager blcm = null;
        BusinessQueryManager bqm = null;

        try {
            rs = connection.getRegistryService();
            blcm = rs.getBusinessLifeCycleManager();
            bqm = rs.getBusinessQueryManager();
            logger.info("Got registry service, query manager, " +
                "and life cycle manager");

            // Get authorization from the registry
            PasswordAuthentication passwdAuth =
                new PasswordAuthentication(username, password.toCharArray());

            Set creds = new HashSet();
            creds.add(passwdAuth);
            connection.setCredentials(creds);
            logger.info("Established security credentials");

            // Get hardcoded strings from a ResourceBundle
            ResourceBundle bundle =
                ResourceBundle.getBundle("com.sun.cb.CoffeeBreak");

            // Create organization name and description
            Organization org =
                blcm.createOrganization(bundle.getString("org.name"));

            logger.info(bundle.getString("org.name"));

            InternationalString s =
                blcm.createInternationalString(bundle.getString(
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

            // Set classification scheme to NAICS
            String schemeName = bundle.getString("classification.scheme");
            logger.info(schemeName);

            ClassificationScheme cScheme = null;

            // Set classification scheme to NAICS
            // workaround while IBM has two NAICS schemes
            String queryURL = bundle.getString("query.url");
            String uuid_naics = "UUID:C0B9FE13-179F-413D-8A5B-5004DB8E5BB2";

            if (queryURL.equals("http://uddi.ibm.com/testregistry/inquiryapi")) {
                cScheme = (ClassificationScheme) bqm.getRegistryObject(uuid_naics,
                        LifeCycleManager.CLASSIFICATION_SCHEME);
            } else {
                cScheme = bqm.findClassificationSchemeByName(null, schemeName);
            }

            // Create and add classification
            Classification classification =
                (Classification) blcm.createClassification(cScheme,
                    bundle.getString("classification.name"),
                    bundle.getString("classification.value"));
            Collection classifications = new ArrayList();
            classifications.add(classification);
            org.addClassifications(classifications);

            // Create services and service
            Collection services = new ArrayList();
            Service service =
                blcm.createService(bundle.getString("service.name"));
            InternationalString is =
                blcm.createInternationalString(bundle.getString(
                        "service.description"));
            service.setDescription(is);

            // Create service bindings
            Collection serviceBindings = new ArrayList();
            ServiceBinding binding = blcm.createServiceBinding();
            is = blcm.createInternationalString(bundle.getString(
                        "service.binding"));
            binding.setDescription(is);
            binding.setValidateURI(false);
            binding.setAccessURI(endpoint);
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
                    id = orgKey.getId();
                    logger.info("Organization key is " + id);
                }
            } else {
                Iterator excIter = exceptions.iterator();
                Exception exception = null;

                while (excIter.hasNext()) {
                    exception = (Exception) excIter.next();
                    logger.severe("executePublish: Exception on save: " +
                        exception.toString());
                }
            }
        } catch (Exception e) {
            logger.severe("executePublish: " + e.toString());

            if (connection != null) {
                try {
                    connection.close();
                } catch (JAXRException je) {
                    logger.severe("executePublish: Connection close failed");
                }
            }
        }

        return id;
    }
}
