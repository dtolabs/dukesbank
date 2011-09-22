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


import javax.naming.*;
import javax.jms.*;


public class SampleUtilities {
    public static final String CONFAC = "jms/ConnectionFactory";
    public static final String QUEUECONFAC = "jms/QueueConnectionFactory";
    public static final String TOPICCONFAC = "jms/TopicConnectionFactory";
    private static Context jndiContext = null;

    /**
     * Returns a ConnectionFactory object.
     *
     * @return    a ConnectionFactory object
     * @throws    javax.naming.NamingException (or other
     *            exception) if name cannot be found
     */
    public static ConnectionFactory getConnectionFactory()
        throws Exception {
        return (ConnectionFactory) jndiLookup(CONFAC);
    }

    /**
     * Returns the ConnectionFactory object with the specified
     * name.
     *
     * @param name  String specifying connection factory
     *              name
     * @return      a ConnectionFactory object
     * @throws      javax.naming.NamingException (or other
     *              exception) if name cannot be found
     */
    public static ConnectionFactory getConnectionFactory(String name)
        throws Exception {
        return (ConnectionFactory) jndiLookup(name);
    }

    /**
     * Returns a QueueConnectionFactory object.
     *
     * @return    a QueueConnectionFactory object
     * @throws    javax.naming.NamingException (or other
     *            exception) if name cannot be found
     */
    public static QueueConnectionFactory getQueueConnectionFactory()
        throws Exception {
        return (QueueConnectionFactory) jndiLookup(QUEUECONFAC);
    }

    /**
     * Returns a TopicConnectionFactory object.
     *
     * @return    a TopicConnectionFactory object
     * @throws    javax.naming.NamingException (or other
     *            exception) if name cannot be found
     */
    public static TopicConnectionFactory getTopicConnectionFactory()
        throws Exception {
        return (TopicConnectionFactory) jndiLookup(TOPICCONFAC);
    }

    /**
     * Returns a Queue object.
     *
     * @param name       String specifying queue name
     * @param session    a Session object
     *
     * @return           a Queue object
     * @throws           javax.naming.NamingException (or other
     *                   exception) if name cannot be found
     */
    public static Queue getQueue(String name, Session session)
        throws Exception {
        return (Queue) jndiLookup(name);
    }

    /**
     * Returns a Topic object.
     *
     * @param name       String specifying topic name
     * @param session    a Session object
     *
     * @return           a Topic object
     * @throws           javax.naming.NamingException (or other
     *                   exception) if name cannot be found
     */
    public static Topic getTopic(String name, Session session)
        throws Exception {
        return (Topic) jndiLookup(name);
    }

    /**
     * Creates a JNDI API InitialContext object if none exists
     * yet. Then looks up the string argument and returns the
     * associated object.
     *
     * @param name    the name of the object to be looked up
     *
     * @return        the object bound to name
     * @throws        javax.naming.NamingException (or other
     *                exception) if name cannot be found
     */
    public static Object jndiLookup(String name) throws NamingException {
        Object obj = null;

        if (jndiContext == null) {
            try {
                jndiContext = new InitialContext();
            } catch (NamingException e) {
                System.err.println("Could not create JNDI API " + "context: " +
                    e.toString());
                throw e;
            }
        }

        try {
            obj = jndiContext.lookup(name);
        } catch (NamingException e) {
            System.err.println("JNDI API lookup failed: " + e.toString());
            throw e;
        }

        return obj;
    }

    /**
     * Waits for 'count' messages on controlQueue before
     * continuing.  Called by a publisher to make sure that
     * subscribers have started before it begins publishing
     * messages.
     *
     * If controlQueue does not exist, the method throws an
     * exception.
     *
     * @param prefix    prefix (publisher or subscriber) to be
     *                  displayed
     * @param controlQueueName  name of control queue
     * @param count     number of messages to receive
     */
    public static void receiveSynchronizeMessages(String prefix,
        String controlQueueName, int count) throws Exception {
        ConnectionFactory connectionFactory = null;
        Connection connection = null;
        Session session = null;
        Queue controlQueue = null;
        MessageConsumer receiver = null;

        try {
            connectionFactory = SampleUtilities.getConnectionFactory();
            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            controlQueue = getQueue(controlQueueName, session);
            connection.start();
        } catch (Exception e) {
            System.err.println("Connection problem: " + e.toString());

            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException ee) {
                }
            }

            throw e;
        }

        try {
            System.out.println(prefix + "Receiving synchronize messages from " +
                controlQueueName + "; count = " + count);
            receiver = session.createConsumer(controlQueue);

            while (count > 0) {
                receiver.receive();
                count--;
                System.out.println(prefix + "Received synchronize message; " +
                    " expect " + count + " more");
            }
        } catch (JMSException e) {
            System.err.println("Exception occurred: " + e.toString());
            throw e;
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException e) {
                }
            }
        }
    }

    /**
     * Sends a message to controlQueue.  Called by a subscriber
     * to notify a publisher that it is ready to receive
     * messages.
     * <p>
     * If controlQueue doesn't exist, the method throws an
     * exception.
     *
     * @param prefix    prefix (publisher or subscriber) to be
     *                  displayed
     * @param controlQueueName  name of control queue
     */
    public static void sendSynchronizeMessage(String prefix,
        String controlQueueName) throws Exception {
        ConnectionFactory connectionFactory = null;
        Connection connection = null;
        Session session = null;
        Queue controlQueue = null;
        MessageProducer producer = null;
        TextMessage message = null;

        try {
            connectionFactory = SampleUtilities.getConnectionFactory();
            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            controlQueue = getQueue(controlQueueName, session);
        } catch (Exception e) {
            System.err.println("Connection problem: " + e.toString());

            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException ee) {
                }
            }

            throw e;
        }

        try {
            producer = session.createProducer(controlQueue);
            message = session.createTextMessage();
            message.setText("synchronize");
            System.out.println(prefix + "Sending synchronize message to " +
                controlQueueName);
            producer.send(message);
        } catch (JMSException e) {
            System.err.println("Exception occurred: " + e.toString());
            throw e;
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException e) {
                }
            }
        }
    }

    /**
     * Monitor class for asynchronous examples.  Producer signals
     * end of message stream; listener calls allDone() to notify
     * consumer that the signal has arrived, while consumer calls
     * waitTillDone() to wait for this notification.
     */
    static public class DoneLatch {
        boolean done = false;

        /**
         * Waits until done is set to true.
         */
        public void waitTillDone() {
            synchronized (this) {
                while (!done) {
                    try {
                        this.wait();
                    } catch (InterruptedException ie) {
                    }
                }
            }
        }

        /**
         * Sets done to true.
         */
        public void allDone() {
            synchronized (this) {
                done = true;
                this.notify();
            }
        }
    }
}
