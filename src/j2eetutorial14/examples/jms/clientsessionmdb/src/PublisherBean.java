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


package sb;

import java.rmi.RemoteException;
import java.util.*;
import java.util.logging.*;
import javax.ejb.*;
import javax.naming.*;
import javax.jms.*;


/**
 * Bean class for Publisher enterprise bean. Defines publishNews
 * business method as well as required methods for a stateless
 * session bean.
 */
public class PublisherBean implements SessionBean {
    final static String[] messageTypes =
        {
            "Nation/World", "Metro/Region", "Business", "Sports", "Living/Arts",
            "Opinion"
        };
    static final Logger logger = Logger.getLogger("PublisherBean");
    SessionContext sc = null;
    Connection connection = null;
    Topic topic = null;

    public PublisherBean() {
        logger.info("In PublisherBean() (constructor)");
    }

    /**
     * Sets the associated session context. The container calls
     * this method after the instance creation.
     *
     * @param sc    the context to set
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
        ConnectionFactory connectionFactory = null;

        logger.info("In PublisherBean.ejbCreate()");

        try {
            context = new InitialContext();
            topic = (Topic) context.lookup("java:comp/env/jms/TopicName");

            // Create a connection
            connectionFactory = (ConnectionFactory) context.lookup(
                    "java:comp/env/jms/MyConnectionFactory");
            connection = connectionFactory.createConnection();
        } catch (Throwable t) {
            // JMSException or NamingException could be thrown
            logger.severe("PublisherBean.ejbCreate:" + "Exception: " +
                t.toString());
        }
    }

    /**
     * Chooses a message type by using the random number
     * generator found in java.util.  Called by publishNews().
     *
     * @return   the String representing the message type
     */
    private String chooseType() {
        int whichMsg;
        Random rgen = new Random();

        whichMsg = rgen.nextInt(messageTypes.length);

        return messageTypes[whichMsg];
    }

    /**
     * Creates session, publisher, and message.  Publishes
     * messages after setting their NewsType property and using
     * the property value as the message text. Messages are
     * received by MessageBean, a message-driven bean that uses a
     * message selector to retrieve messages whose NewsType
     * property has certain values.
     */
    public void publishNews() {
        Session session = null;
        MessageProducer publisher = null;
        TextMessage message = null;
        int numMsgs = messageTypes.length * 3;
        String messageType = null;

        try {
            session = connection.createSession(true, 0);
            publisher = session.createProducer(topic);
            message = session.createTextMessage();

            for (int i = 0; i < numMsgs; i++) {
                messageType = chooseType();
                message.setStringProperty("NewsType", messageType);
                message.setText("Item " + i + ": " + messageType);
                logger.info("PUBLISHER: Setting " + "message text to: " +
                    message.getText());
                publisher.send(message);
            }
        } catch (Throwable t) {
            // JMSException could be thrown
            logger.severe("PublisherBean.publishNews: " + "Exception: " +
                t.toString());
            sc.setRollbackOnly();
        } finally {
            if (session != null) {
                try {
                    session.close();
                } catch (JMSException e) {
                }
            }
        }
    }

    /**
     * Closes the connection.
     */
    public void ejbRemove() throws RemoteException {
        System.out.println("In PublisherBean.ejbRemove()");

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
