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


import javax.ejb.*;
import javax.naming.*;
import javax.jms.*;
import java.util.logging.*;


/**
 * The ReplyMsgBean class is a message-driven bean. It
 * implements the javax.ejb.MessageDrivenBean and
 * javax.jms.MessageListener interfaces. It is defined as public
 * (but not final or abstract).  It defines a constructor and the
 * methods ejbCreate, onMessage, setMessageDrivenContext, and
 * ejbRemove.
 */
public class ReplyMsgBean implements MessageDrivenBean, MessageListener {
    static final Logger logger = Logger.getLogger("ReplyMsgBean");
    private transient MessageDrivenContext mdc = null;
    private transient ConnectionFactory cf = null;

    /**
     * Constructor, which is public and takes no arguments.
     */
    public ReplyMsgBean() {
        logger.info("In ReplyMsgBean.ReplyMsgBean()");
    }

    /**
     * setMessageDrivenContext method, declared as public (but
     * not final or static), with a return type of void, and
     * with one argument of type javax.ejb.MessageDrivenContext.
     *
     * @param mdc    the context to set
     */
    public void setMessageDrivenContext(MessageDrivenContext mdc) {
        logger.info("In ReplyMsgBean.setMessageDrivenContext()");
        this.mdc = mdc;
    }

    /**
     * ejbCreate method, declared as public (but not final or
     * static), with a return type of void, and with no
     * arguments. It looks up the topic connection factory.
     */
    public void ejbCreate() {
        logger.info("In ReplyMsgBean.ejbCreate()");

        try {
            Context initial = new InitialContext();
            cf = (ConnectionFactory) initial.lookup(
                    "java:comp/env/jms/MyConnectionFactory");
        } catch (Exception ex) {
            logger.severe("ReplyMsgBean.ejbCreate: Exception: " +
                ex.toString());
        }
    }

    /**
     * onMessage method, declared as public (but not final or
     * static), with a return type of void, and with one argument
     * of type javax.jms.Message.
     *
     * It displays the contents of the message and creates a
     * connection, session, and producer for the reply, using
     * the JMSReplyTo field of the incoming message as the
     * destination.  It creates and sends a reply message,
     * setting the JMSCorrelationID header field to the message
     * ID of the incoming message, and the id property to that of
     * the incoming message.  It then closes the connection.
     *
     * @param inMessage        the incoming message
     */
    public void onMessage(Message inMessage) {
        TextMessage msg = null;
        Connection con = null;
        Session ses = null;
        MessageProducer producer = null;
        TextMessage replyMsg = null;

        try {
            if (inMessage instanceof TextMessage) {
                msg = (TextMessage) inMessage;
                logger.info("ReplyMsgBean: Received message: " + msg.getText());
                con = cf.createConnection();
                ses = con.createSession(true, 0);

                producer = ses.createProducer((Topic) msg.getJMSReplyTo());
                replyMsg = ses.createTextMessage("ReplyMsgBean " +
                        "processed message: " + msg.getText());
                replyMsg.setJMSCorrelationID(msg.getJMSMessageID());
                replyMsg.setIntProperty("id", msg.getIntProperty("id"));
                producer.send(replyMsg);
                con.close();
            } else {
                logger.warning("Message of wrong type: " +
                    inMessage.getClass().getName());
            }
        } catch (JMSException e) {
            logger.severe("ReplyMsgBean.onMessage: JMSException: " +
                e.toString());
        } catch (Throwable te) {
            logger.severe("ReplyMsgBean.onMessage: Exception: " +
                te.toString());
        }
    }

    /**
     * ejbRemove method, declared as public (but not final or
     * static), with a return type of void, and with no
     * arguments.
     */
    public void ejbRemove() {
        logger.info("In ReplyMsgBean.ejbRemove()");
    }
}
