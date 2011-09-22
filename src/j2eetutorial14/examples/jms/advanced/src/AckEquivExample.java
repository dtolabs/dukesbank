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


public class AckEquivExample {
    final String CONTROL_QUEUE = "jms/ControlQueue";
    final String queueName = "jms/Queue";
    final String topicName = "jms/Topic";
    final String conFacName = "jms/DurableConnectionFactory";

    /**
     * Instantiates the sender, receiver, subscriber, and
     * publisher classes and starts their threads.
     * Calls the join method to wait for the threads to die.
     */
    public void run_threads() {
        SynchSender synchSender = new SynchSender();
        SynchReceiver synchReceiver = new SynchReceiver();
        AsynchSubscriber asynchSubscriber = new AsynchSubscriber();
        MultiplePublisher multiplePublisher = new MultiplePublisher();

        synchSender.start();
        synchReceiver.start();

        try {
            synchSender.join();
            synchReceiver.join();
        } catch (InterruptedException e) {
        }

        asynchSubscriber.start();
        multiplePublisher.start();

        try {
            asynchSubscriber.join();
            multiplePublisher.join();
        } catch (InterruptedException e) {
        }
    }

    /**
     * Calls the run_threads method to execute the program
     * threads.
     *
     * @param args    the topic used by the example
     */
    public static void main(String[] args) {
        AckEquivExample aee = new AckEquivExample();

        if (args.length != 0) {
            System.out.println("Program takes no arguments.");
            System.exit(1);
        }

        System.out.println("Queue name is " + aee.CONTROL_QUEUE);
        System.out.println("Queue name is " + aee.queueName);
        System.out.println("Topic name is " + aee.topicName);
        System.out.println("Connection factory name is " + aee.conFacName);

        aee.run_threads();
        System.exit(0);
    }

    /**
     * The SynchSender class creates a session in
     * CLIENT_ACKNOWLEDGE mode and sends a message.
     */
    public class SynchSender extends Thread {
        /**
         * Runs the thread.
         */
        public void run() {
            ConnectionFactory connectionFactory = null;
            Connection connection = null;
            Session session = null;
            Queue queue = null;
            MessageProducer producer = null;
            final String MSG_TEXT =
                new String("Here is a client-acknowledge message");
            TextMessage message = null;

            try {
                connectionFactory = SampleUtilities.getConnectionFactory();
                connection = connectionFactory.createConnection();
                session = connection.createSession(false,
                        Session.CLIENT_ACKNOWLEDGE);
                queue = SampleUtilities.getQueue(queueName, session);
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

            /*
             * Create client-acknowledge sender.
             * Create and send message.
             */
            try {
                System.out.println("  SENDER: Created " +
                    "client-acknowledge session");
                producer = session.createProducer(queue);
                message = session.createTextMessage();
                message.setText(MSG_TEXT);
                System.out.println("  SENDER: Sending " + "message: " +
                    message.getText());
                producer.send(message);
            } catch (JMSException e) {
                System.err.println("Exception occurred: " + e.toString());
            } finally {
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (JMSException e) {
                    }
                }
            }
        }
    }

    /**
     * The SynchReceiver class creates a session in
     * CLIENT_ACKNOWLEDGE mode and receives the message sent by
     * the SynchSender class.
     */
    public class SynchReceiver extends Thread {
        /**
         * Runs the thread.
         */
        public void run() {
            ConnectionFactory connectionFactory = null;
            Connection connection = null;
            Session session = null;
            Queue queue = null;
            MessageConsumer receiver = null;
            TextMessage message = null;

            try {
                connectionFactory = SampleUtilities.getConnectionFactory();
                connection = connectionFactory.createConnection();
                session = connection.createSession(false,
                        Session.CLIENT_ACKNOWLEDGE);
                queue = SampleUtilities.getQueue(queueName, session);
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

            /*
             * Create client-acknowledge receiver.
             * Receive message and process it.
             * Acknowledge message.
             */
            try {
                System.out.println("  RECEIVER: Created " +
                    "client-acknowledge session");
                receiver = session.createConsumer(queue);
                connection.start();
                message = (TextMessage) receiver.receive();
                System.out.println("  RECEIVER: Processing " + "message: " +
                    message.getText());
                System.out.println("  RECEIVER: Now I'll " +
                    "acknowledge the message");
                message.acknowledge();
            } catch (JMSException e) {
                System.err.println("Exception occurred: " + e.toString());
            } finally {
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (JMSException e) {
                    }
                }
            }
        }
    }

    /**
     * The AsynchSubscriber class creates a session in
     * AUTO_ACKNOWLEDGE mode and fetches several messages from a
     * topic asynchronously, using a message listener,
     * TextListener.
     *
     * Each message is acknowledged after the onMessage method
     * completes.
     */
    public class AsynchSubscriber extends Thread {
        /**
         * Runs the thread.
         */
        public void run() {
            ConnectionFactory connectionFactory = null;
            Connection connection = null;
            Session session = null;
            Topic topic = null;
            MessageConsumer subscriber = null;
            TextListener listener = null;

            /*
             * Look up connection factory and topic.  If either
             * does not exist, exit.
             */
            try {
                connectionFactory = SampleUtilities.getConnectionFactory(conFacName);
                connection = connectionFactory.createConnection();
                session = connection.createSession(false,
                        Session.AUTO_ACKNOWLEDGE);
                System.out.println("SUBSCRIBER: Created " +
                    "auto-acknowledge session");
                topic = SampleUtilities.getTopic(topicName, session);
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

            /*
             * Create auto-acknowledge subscriber.
             * Register message listener (TextListener).
             * Start message delivery.
             * Send synchronize message to publisher, then wait
             *   till all messages have arrived.
             * Listener displays the messages obtained.
             */
            try {
                subscriber = session.createDurableSubscriber(topic, "AckSub");
                listener = new TextListener();
                subscriber.setMessageListener(listener);
                connection.start();

                // Let publisher know that subscriber is ready.
                try {
                    SampleUtilities.sendSynchronizeMessage("SUBSCRIBER: ",
                        CONTROL_QUEUE);
                } catch (Exception e) {
                    System.err.println("Queue probably missing: " +
                        e.toString());

                    if (connection != null) {
                        try {
                            connection.close();
                        } catch (JMSException ee) {
                        }
                    }

                    System.exit(1);
                }

                /*
                 * Asynchronously process messages.
                 * Block until publisher issues a control message
                 * indicating end of publish stream.
                 */
                listener.monitor.waitTillDone();
                subscriber.close();
                session.unsubscribe("AckSub");
            } catch (JMSException e) {
                System.err.println("Exception occurred: " + e.toString());
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
         * The TextListener class implements the MessageListener
         * interface by defining an onMessage method for the
         * AsynchSubscriber class.
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
                            "Processing message: " + msg.getText());
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
     * The MultiplePublisher class creates a session in
     * AUTO_ACKNOWLEDGE mode and publishes three messages
     * to a topic.
     */
    public class MultiplePublisher extends Thread {
        /**
         * Runs the thread.
         */
        public void run() {
            ConnectionFactory connectionFactory = null;
            Connection connection = null;
            Session session = null;
            Topic topic = null;
            MessageProducer publisher = null;
            TextMessage message = null;
            final int NUMMSGS = 3;
            final String MSG_TEXT =
                new String("Here is an auto-acknowledge message");

            try {
                connectionFactory = SampleUtilities.getConnectionFactory();
                connection = connectionFactory.createConnection();
                session = connection.createSession(false,
                        Session.AUTO_ACKNOWLEDGE);
                System.out.println("PUBLISHER: Created " +
                    "auto-acknowledge session");
                topic = SampleUtilities.getTopic(topicName, session);
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

            /*
             * After synchronizing with subscriber, create
             *   publisher.
             * Send 3 messages, varying text slightly.
             * Send end-of-messages message.
             */
            try {
                /*
                 * Synchronize with subscriber.  Wait for message
                 * indicating that subscriber is ready to receive
                 * messages.
                 */
                try {
                    SampleUtilities.receiveSynchronizeMessages("PUBLISHER: ",
                        CONTROL_QUEUE, 1);
                } catch (Exception e) {
                    System.err.println("Queue probably " + "missing: " +
                        e.toString());

                    if (connection != null) {
                        try {
                            connection.close();
                        } catch (JMSException ee) {
                        }
                    }

                    System.exit(1);
                }

                publisher = session.createProducer(topic);
                message = session.createTextMessage();

                for (int i = 0; i < NUMMSGS; i++) {
                    message.setText(MSG_TEXT + " " + (i + 1));
                    System.out.println("PUBLISHER: Publishing " + "message: " +
                        message.getText());
                    publisher.send(message);
                }

                /*
                 * Send a non-text control message indicating
                 * end of messages.
                 */
                publisher.send(session.createMessage());
            } catch (JMSException e) {
                System.err.println("Exception occurred: " + e.toString());
            } finally {
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (JMSException e) {
                    }
                }
            }
        }
    }
}
