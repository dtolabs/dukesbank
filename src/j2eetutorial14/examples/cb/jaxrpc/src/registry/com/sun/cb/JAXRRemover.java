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
 * The JAXRRemover class consists of a createOrgKey method
 * and an executeRemove method. It finds and deletes the
 * organization that the JAXRPublisher class created.
 */
public class JAXRRemover {
    static final Logger logger =
        Logger.getLogger("jaxrpc.registry.com.sun.cb.JAXRRemover");
    RegistryService rs = null;

    public JAXRRemover() {
    }

    /**
     * Searches for the organization created by the JAXRPublisher
     * class, verifying it by checking that the key strings
     * match.
     *
     * @param keyStr        the key of the published organization
     *
     * @return        the key of the organization found
     */
    public javax.xml.registry.infomodel.Key createOrgKey(
        Connection connection,
        String keyStr) {
        BusinessLifeCycleManager blcm = null;
        javax.xml.registry.infomodel.Key orgKey = null;

        try {
            rs = connection.getRegistryService();
            blcm = rs.getBusinessLifeCycleManager();
            logger.info("Got registry service and " + "life cycle manager");

            orgKey = blcm.createKey(keyStr);
        } catch (Exception e) {
            logger.severe("createOrgKey: " + e.toString());

            if (connection != null) {
                try {
                    connection.close();
                } catch (JAXRException je) {
                    logger.severe("createOrgKey: Connection close failed");
                }
            }
        }

        return orgKey;
    }

    /**
     * Removes the organization with the specified key value.
     *
     * @param key        the Key of the organization
     */
    public void executeRemove(Connection connection,
        javax.xml.registry.infomodel.Key key, String username, String password) {
        BusinessLifeCycleManager blcm = null;

        try {
            blcm = rs.getBusinessLifeCycleManager();

            // Get authorization from the registry
            PasswordAuthentication passwdAuth =
                new PasswordAuthentication(username, password.toCharArray());

            Set creds = new HashSet();
            creds.add(passwdAuth);
            connection.setCredentials(creds);
            logger.info("Established security credentials");

            String id = key.getId();
            logger.info("Deleting organization with id " + id);

            Collection keys = new ArrayList();
            keys.add(key);

            BulkResponse response = blcm.deleteOrganizations(keys);
            Collection exceptions = response.getExceptions();

            if (exceptions == null) {
                logger.info("Organization deleted");

                Collection retKeys = response.getCollection();
                Iterator keyIter = retKeys.iterator();
                javax.xml.registry.infomodel.Key orgKey = null;

                if (keyIter.hasNext()) {
                    orgKey = (javax.xml.registry.infomodel.Key) keyIter.next();
                    id = orgKey.getId();
                    logger.info("Organization key was " + id);
                }
            } else {
                Iterator excIter = exceptions.iterator();
                Exception exception = null;

                while (excIter.hasNext()) {
                    exception = (Exception) excIter.next();
                    logger.severe("executeRemove: Exception on delete: " +
                        exception.toString());
                }
            }
        } catch (Exception e) {
            logger.severe("executeRemove: " + e.toString());
        } finally {
            // At end, close connection to registry
            if (connection != null) {
                try {
                    connection.close();
                } catch (JAXRException je) {
                    logger.severe("executeRemove: Connection close failed");
                }
            }
        }
    }
}
