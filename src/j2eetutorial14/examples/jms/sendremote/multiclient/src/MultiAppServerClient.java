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


import javax.jms.*;
import javax.naming.*;
import java.util.*;


/**
 * The MultiAppServerClient class is the client program for
 * this J2EE application.  It sends a message to two
 * different JMS providers and waits for a reply.
 */
public class MultiAppServerClient {
    static Object waitUntilDone = new Object();
    static SortedSet outstandingRequests1 =
        Collections.synchronizedSortedSet(new TreeSet());
    static SortedSet outstandingRequests2 =
        Collections.synchronizedSortedSet(new TreeSet());

    public static void main(String[] args) {
        InitialContext ic = null;
        ConnectionFactory cf1 = null; // App Server 1
        ConnectionFactory cf2 = null; // App Server 2
        Connection con1 = null;
        Connection con2 = null;
        Session pubSession1 = null;
        Session pubSession2 = null;
        MessageProducer producer1 = null;
        MessageProducer producer2 = null;
        Topic pTopic = null;
        TemporaryTopic replyTopic1 = null;
        TemporaryTopic replyTopic2 = null;
        Session subSession1 = null;
        Session subSession2 = null;
        MessageConsumer consumer1 = null;
        MessageConsumer consumer2 = null;
        TextMessage message = null;

        /*
         * Create a JNDI API InitialContext object.
         */
        try {
            ic = new InitialContext();
        } catch (NamingException e) {
            System.err.println("Could not create JNDI API " + "context: " +
                e.toString());
            e.printStackTrace();
            System.exit(1);
        }

        /*
         * Look up connection factories and topic.  If any do not
         * exist, exit.
         */
        try {
            cf1 = (ConnectionFactory) ic.lookup(
                    "java:comp/env/jms/ConnectionFactory1");
            cf2 = (ConnectionFactory) ic.lookup(
                    "java:comp/env/jms/ConnectionFactory2");
            pTopic = (Topic) ic.lookup("java:comp/env/jms/TopicName");
        } catch (NamingException e) {
            System.err.println("JNDI API lookup failed: " + e.toString());
            e.printStackTrace();
            System.exit(1);
        }

        try {
            // Create two connections.
            con1 = cf1.createConnection();
            con2 = cf2.createConnection();

            // Create sessions for producers.
            pubSession1 = con1.createSession(false, Session.AUTO_ACKNOWLEDGE);
            pubSession2 = con2.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Create temporary topics for replies.
            replyTopic1 = pubSession1.createTemporaryTopic();
            replyTopic2 = pubSession2.createTemporaryTopic();

            // Create sessions for consumers.
            subSession1 = con1.createSession(false, Session.AUTO_ACKNOWLEDGE);
            subSession2 = con2.createSession(false, Session.AUTO_ACKNOWLEDGE);

            /*
             * Create consumers, set message listeners, and
             * start connections.
             */
            consumer1 = subSession1.createConsumer(replyTopic1);
            consumer2 = subSession2.createConsumer(replyTopic2);
            consumer1.setMessageListener(new ReplyListener(outstandingRequests1));
            consumer2.setMessageListener(new ReplyListener(outstandingRequests2));
            con1.start();
            con2.start();

            // Create producers.
            producer1 = pubSession1.createProducer(pTopic);
            producer2 = pubSession2.createProducer(pTopic);

            /*
             * Create and send two sets of messages, one set to
             * each app server, at 1.5-second intervals.  For
             * each message, set the JMSReplyTo message header to
             * a reply topic, and set an id property.  Add the
             * message ID to the list of outstanding requests for
             * the message listener.
             */
            message = pubSession1.createTextMessage();

            int id = 1;

            for (int i = 0; i < 5; i++) {
                message.setJMSReplyTo(replyTopic1);
                message.setIntProperty("id", id);
                message.setText("text: id=" + id + " to local app server");
                producer1.send(message);
                System.out.println("Sent message: " + message.getText());
                outstandingRequests1.add(message.getJMSMessageID());
                id++;
                Thread.sleep(1500);
                message.setJMSReplyTo(replyTopic2);
                message.setIntProperty("id", id);
                message.setText("text: id=" + id + " to remote app server");

                try {
                    producer2.send(message);
                    System.out.println("Sent message: " + message.getText());
                    outstandingRequests2.add(message.getJMSMessageID());
                } catch (Exception e) {
                    System.err.println("Exception: Caught " +
                        "failed send to connection factory 2");
                    e.printStackTrace();
                }

                id++;
                Thread.sleep(1500);
            }

            /*
             * Wait for replies.
             */
            System.out.println("Waiting for " + outstandingRequests1.size() +
                " message(s) " + "from local app server");
            System.out.println("Waiting for " + outstandingRequests2.size() +
                " message(s) " + "from remote app server");

            while ((outstandingRequests1.size() > 0) ||
                    (outstandingRequests2.size() > 0)) {
                synchronized (waitUntilDone) {
                    waitUntilDone.wait();
                }
            }

            System.out.println("Finished");
        } catch (Exception e) {
            System.err.println("Exception occurred: " + e.toString());
            e.printStackTrace();
        } finally {
            System.out.println("Closing connection 1");

            if (con1 != null) {
                try {
                    con1.close();
                } catch (Exception e) {
                    System.err.println("Error closing " + "connection 1: " +
                        e.toString());
                }
            }

            System.out.println("Closing connection 2");

            if (con2 != null) {
                try {
                    con2.close();
                } catch (Exception e) {
                    System.err.println("Error closing " + "connection 2: " +
                        e.toString());
                }
            }

            System.exit(0);
        }
    }

    /**
     * The ReplyListener class is instantiated with a set of
     * outstanding requests.
     */
    static class ReplyListener implements MessageListener {
        SortedSet outstandingRequests = null;

        /**
         * Constructor for ReplyListener class.
         *
         * @param outstandingRequests   set of outstanding
         *                              requests
         */
        ReplyListener(SortedSet outstandingRequests) {
            this.outstandingRequests = outstandingRequests;
        }

        /**
         * onMessage method, which displays the contents of the
         * id property and text and uses the JMSCorrelationID to
         * remove from the list of outstanding requests the
         * message to which this message is a reply.  If this is
         * the last message, it notifies the client.
         *
         * @param message     the incoming message
         */
        public void onMessage(Message message) {
            TextMessage tmsg = (TextMessage) message;
            String txt = null;
            int id = 0;
            String correlationID = null;

            try {
                id = tmsg.getIntProperty("id");
                txt = tmsg.getText();
                correlationID = tmsg.getJMSCorrelationID();
            } catch (JMSException e) {
                System.err.println("ReplyListener.onMessage: " +
                    "JMSException: " + e.toString());
            }

            System.out.println("ReplyListener: Received " + "message: id=" +
                id + ", text=" + txt);
            outstandingRequests.remove(correlationID);

            if (outstandingRequests.size() == 0) {
                synchronized (waitUntilDone) {
                    waitUntilDone.notify();
                }
            } else {
                System.out.println("ReplyListener: Waiting " + "for " +
                    outstandingRequests.size() + " message(s)");
            }
        }
    }
}
