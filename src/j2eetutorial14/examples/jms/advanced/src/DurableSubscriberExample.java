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


public class DurableSubscriberExample {
    static int startindex = 0;
    Context jndiContext = null;
    String conFacName1 = null;
    String conFacName2 = null;
    String topicName = null;

    /**
     * Instantiates the subscriber and publisher classes.
     * Starts the subscriber; the publisher publishes some
     *   messages.
     * Closes the subscriber; while it is closed, the publisher
     *   publishes some more messages.
     * Restarts the subscriber and fetches the messages.
     * Finally, closes the connections.
     */
    public void run_program() {
        DurableSubscriber durableSubscriber = new DurableSubscriber();
        MultiplePublisher multiplePublisher = new MultiplePublisher();

        durableSubscriber.startSubscriber();
        multiplePublisher.publishMessages();
        durableSubscriber.closeSubscriber();
        multiplePublisher.publishMessages();
        durableSubscriber.startSubscriber();
        durableSubscriber.closeSubscriber();
        multiplePublisher.finish();
        durableSubscriber.finish();
    }

    /**
     * Reads the topic name from the command line, then calls the
     * run_program method.
     *
     * @param args    the topic used by the example
     */
    public static void main(String[] args) {
        DurableSubscriberExample dse = new DurableSubscriberExample();

        if (args.length != 0) {
            System.out.println("Program takes no arguments.");
            System.exit(1);
        }

        dse.conFacName1 = new String("jms/ConnectionFactory");
        System.out.println("Connection factory without client ID is " +
            dse.conFacName1);
        dse.conFacName2 = new String("jms/DurableConnectionFactory");
        System.out.println("Connection factory with client ID is " +
            dse.conFacName2);
        dse.topicName = new String("jms/Topic");
        System.out.println("Topic name is " + dse.topicName);

        dse.run_program();
        System.exit(0);
    }

    /**
     * The DurableSubscriber class contains a constructor, a
     * startSubscriber method, a closeSubscriber method, and a
     * finish method.
     *
     * The class fetches messages asynchronously, using a message
     * listener, TextListener.
     */
    public class DurableSubscriber {
        ConnectionFactory connectionFactory = null;
        Connection connection = null;
        Session session = null;
        Topic topic = null;
        TopicSubscriber subscriber = null;
        TextListener listener = null;

        /**
         * Constructor: looks up a connection factory and topic
         * and creates a connection and session.
         */
        public DurableSubscriber() {
            /*
             * Create a JNDI API InitialContext object if none
             * exists yet.
             */
            try {
                jndiContext = new InitialContext();
            } catch (NamingException e) {
                System.err.println("Could not create JNDI API " + "context: " +
                    e.toString());
                System.exit(1);
            }

            /*
             * Look up connection factory with client ID and
             * topic.  If either does not exist, exit.
             */
            try {
                connectionFactory = (ConnectionFactory) jndiContext.lookup(conFacName2);
                topic = (Topic) jndiContext.lookup(topicName);
            } catch (Exception e) {
                System.err.println("JNDI API lookup failed: " + e.toString());
                System.exit(1);
            }

            /*
             * Create connection and session.
             */
            try {
                connection = connectionFactory.createConnection();
                session = connection.createSession(false,
                        Session.AUTO_ACKNOWLEDGE);
            } catch (Exception e) {
                System.err.println("Connection problem: " + e.toString());

                if (connection != null) {
                    try {
                        connection.close();
                    } catch (JMSException ee) {
                    }
                }

                System.exit(1);
            }
        }

        /**
         * Stops connection, then creates durable subscriber,
         * registers message listener (TextListener), and starts
         * message delivery; listener displays the messages
         * obtained.
         */
        public void startSubscriber() {
            try {
                System.out.println("Starting subscriber");
                connection.stop();
                subscriber = session.createDurableSubscriber(topic, "MakeItLast");
                listener = new TextListener();
                subscriber.setMessageListener(listener);
                connection.start();
            } catch (JMSException e) {
                System.err.println("startSubscriber: Exception occurred: " +
                    e.toString());
            }
        }

        /**
         * Blocks until publisher issues a control message
         * indicating end of publish stream, then closes
         * subscriber.
         */
        public void closeSubscriber() {
            try {
                listener.monitor.waitTillDone();
                System.out.println("Closing subscriber");
                subscriber.close();
            } catch (JMSException e) {
                System.err.println("closeSubscriber: Exception occurred: " +
                    e.toString());
            }
        }

        /**
         * Closes the connection.
         */
        public void finish() {
            if (connection != null) {
                try {
                    System.out.println("Unsubscribing from " +
                        "durable subscription");
                    session.unsubscribe("MakeItLast");
                    connection.close();
                } catch (JMSException e) {
                }
            }
        }

        /**
         * The TextListener class implements the MessageListener
         * interface by defining an onMessage method for the
         * DurableSubscriber class.
         */
        private class TextListener implements MessageListener {
            final SampleUtilities.DoneLatch monitor =
                new SampleUtilities.DoneLatch();

            /**
             * Casts the message to a TextMessage and displays
             * its text. A non-text message is interpreted as the
             * end of the message stream, and the message
             * listener sets its monitor state to all done
             * processing messages.
             *
             * @param message    the incoming message
             */
            public void onMessage(Message message) {
                if (message instanceof TextMessage) {
                    TextMessage msg = (TextMessage) message;

                    try {
                        System.out.println("SUBSCRIBER: " +
                            "Reading message: " + msg.getText());
                    } catch (JMSException e) {
                        System.err.println("Exception in " + "onMessage(): " +
                            e.toString());
                    }
                } else {
                    monitor.allDone();
                }
            }
        }
    }

    /**
     * The MultiplePublisher class publishes several messages to
     * a topic. It contains a constructor, a publishMessages
     * method, and a finish method.
     */
    public class MultiplePublisher {
        Connection connection = null;
        Session session = null;
        Topic topic = null;
        MessageProducer producer = null;

        /**
         * Constructor: looks up a connection factory and topic
         * and creates a connection, session, and publisher.
         */
        public MultiplePublisher() {
            ConnectionFactory connectionFactory = null;

            /*
             * Look up regular connection factory and topic.
             * If either does not exist, exit.
             */
            try {
                connectionFactory = (ConnectionFactory) jndiContext.lookup(conFacName1);
                topic = (Topic) jndiContext.lookup(topicName);
            } catch (Exception e) {
                System.out.println("JNDI API lookup failed: " + e.toString());
                System.exit(1);
            }

            /*
             * Create connection, session, and publisher.
             */
            try {
                connection = connectionFactory.createConnection();
                session = connection.createSession(false,
                        Session.AUTO_ACKNOWLEDGE);
                producer = session.createProducer(topic);
            } catch (Exception e) {
                System.err.println("Connection problem: " + e.toString());

                if (connection != null) {
                    try {
                        connection.close();
                    } catch (JMSException ee) {
                    }
                }

                System.exit(1);
            }
        }

        /**
         * Creates text message.
         * Sends some messages, varying text slightly.
         * Messages must be persistent.
         */
        public void publishMessages() {
            TextMessage message = null;
            int i;
            final int NUMMSGS = 3;
            final String MSG_TEXT = new String("Here is a message");

            try {
                message = session.createTextMessage();

                for (i = startindex; i < (startindex + NUMMSGS); i++) {
                    message.setText(MSG_TEXT + " " + (i + 1));
                    System.out.println("PUBLISHER: Publishing " + "message: " +
                        message.getText());
                    producer.send(message);
                }

                /*
                 * Send a non-text control message indicating end
                 * of messages.
                 */
                producer.send(session.createMessage());
                startindex = i;
            } catch (JMSException e) {
                System.err.println("publishMessages: Exception occurred: " +
                    e.toString());
            }
        }

        /**
         * Closes the connection.
         */
        public void finish() {
            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException e) {
                }
            }
        }
    }
}
