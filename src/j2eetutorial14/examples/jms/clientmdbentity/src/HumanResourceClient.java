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


package client;

import javax.jms.*;
import javax.naming.*;
import java.util.*;


/**
 * The HumanResourceClient class is the client program for this
 * J2EE application. It publishes a message describing a new
 * hire business event that other departments can act upon. It
 * also listens for a message reporting the completion of the
 * other departments' actions and displays the results.
 */
public class HumanResourceClient {
    static Object waitUntilDone = new Object();
    static SortedSet outstandingRequests =
        Collections.synchronizedSortedSet(new TreeSet());

    public static void main(String[] args) {
        InitialContext ic = null;
        ConnectionFactory connectionFactory = null;
        Connection connection = null;
        Session session = null;
        Topic pubTopic = null;
        MessageProducer producer = null;
        MapMessage message = null;
        javax.jms.Queue replyQueue = null;
        MessageConsumer consumer = null;

        /*
         * Create a JNDI API InitialContext object.
         */
        try {
            ic = new InitialContext();
        } catch (NamingException e) {
            System.err.println("HumanResourceClient: " +
                "Could not create JNDI API context: " + e.toString());
            System.exit(1);
        }

        /*
         * Look up connection factories and topic.  If any do not
         * exist, exit.
         */
        try {
            connectionFactory = (ConnectionFactory) ic.lookup(
                    "java:comp/env/jms/MyConnectionFactory");
            pubTopic = (Topic) ic.lookup("java:comp/env/jms/NewHireTopic");
        } catch (NamingException e) {
            System.err.println("HumanResourceClient: " +
                "JNDI API lookup failed: " + e.toString());
            System.exit(1);
        }

        /*
         * Create connection.
         * Create session from connection; false means session
         *   is not transacted.
         * Create temporary queue and consumer, set message
         *   listener, and start connection.
         * Create producer and MapMessage.
         * Publish new hire business events.
         * Wait for all messages to be processed.
         * Finally, close connection.
         */
        try {
            Random rand = new Random();
            int nextHireID = rand.nextInt(100);

            String[] positions =
                { "Programmer", "Senior Programmer", "Manager", "Director" };
            String[] firstNames =
                {
                    "Fred", "Robert", "Tom", "Steve", "Alfred", "Joe", "Jack",
                    "Harry", "Bill", "Gertrude", "Jenny", "Polly", "Ethel",
                    "Mary", "Betsy", "Carol", "Edna", "Gwen"
                };
            String[] lastNames =
                {
                    "Astaire", "Preston", "Tudor", "Stuart", "Drake", "Jones",
                    "Windsor", "Hapsburg", "Robinson", "Lawrence", "Wren",
                    "Parrott", "Waters", "Martin", "Blair", "Bourbon", "Merman",
                    "Verdon"
                };

            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            replyQueue = session.createTemporaryQueue();
            consumer = session.createConsumer(replyQueue);
            consumer.setMessageListener(new HRListener());
            connection.start();

            producer = session.createProducer(pubTopic);

            message = session.createMapMessage();
            message.setJMSReplyTo(replyQueue);

            for (int i = 0; i < 5; i++) {
                int currentHireID = nextHireID++;
                message.setString("HireID", String.valueOf(currentHireID));
                message.setString("Name",
                    firstNames[rand.nextInt(firstNames.length)] + " " +
                    lastNames[rand.nextInt(lastNames.length)]);
                message.setString("Position",
                    positions[rand.nextInt(positions.length)]);
                System.out.println("PUBLISHER: Setting hire " + "ID to " +
                    message.getString("HireID") + ", name " +
                    message.getString("Name") + ", position " +
                    message.getString("Position"));
                producer.send(message);
                outstandingRequests.add(new Integer(currentHireID));
            }

            System.out.println("Waiting for " + outstandingRequests.size() +
                " message(s)");

            synchronized (waitUntilDone) {
                waitUntilDone.wait();
            }
        } catch (Exception e) {
            System.err.println("HumanResourceClient: " + "Exception: " +
                e.toString());
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) {
                    System.err.println("HumanResourceClient: " +
                        "Close exception: " + e.toString());
                }
            }

            System.exit(0);
        }
    }

    /**
     * The HRListener class implements the MessageListener
     * interface by defining an onMessage method.
     */
    static class HRListener implements MessageListener {
        /**
         * Displays the contents of a
         * MapMessage describing the results of processing the
         * new employee, then removes the employee ID from the
         * list of outstanding requests.
         *
         * @param message    the incoming message
         */
        public void onMessage(Message message) {
            MapMessage msg = (MapMessage) message;

            try {
                System.out.println("New hire event processed:");

                Integer id = Integer.valueOf(msg.getString("employeeId"));
                System.out.println("  Employee ID: " + id);
                System.out.println("  Name: " + msg.getString("employeeName"));
                System.out.println("  Equipment: " +
                    msg.getString("equipmentList"));
                System.out.println("  Office number: " +
                    msg.getString("officeNumber"));
                outstandingRequests.remove(id);
            } catch (JMSException je) {
                System.out.println("HRListener.onMessage(): " + "Exception: " +
                    je.toString());
            }

            if (outstandingRequests.size() == 0) {
                synchronized (waitUntilDone) {
                    waitUntilDone.notify();
                }
            } else {
                System.out.println("Waiting for " + outstandingRequests.size() +
                    " message(s)");
            }
        }
    }
}
