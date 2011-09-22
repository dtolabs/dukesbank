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


package eb;

import java.io.*;
import java.util.*;
import java.util.logging.*;
import javax.ejb.*;
import javax.naming.*;
import javax.jms.*;


/**
 * The SetupOfficeBean class implements the business methods of
 * the entity bean.  Because the bean uses version 2.0 of
 * container-managed persistence, the bean class and the
 * accessor methods for fields to be persisted are all declared
 * abstract.
 */
public abstract class SetupOfficeBean implements EntityBean {
    static final Logger logger = Logger.getLogger("SetupOfficeBean");

    /*
     * There should be a list of replies for each message being
     * joined.  This example is joining the work of separate
     * departments on the same original request, so it is all
     * right to have only one reply destination.  In theory, this
     * should be a set of destinations, with one reply for each
     * unique destination.
     *
     * Because a Destination is not a data type that can be
     * persisted, the persisted field is a byte array,
     * serializedReplyDestination, that is created and accessed
     * with the setReplyDestination and getReplyDestination
     * methods.
     */
    transient private Destination replyDestination;
    transient private Connection connection;
    private EntityContext context;

    abstract public String getEmployeeId();

    abstract public void setEmployeeId(String id);

    abstract public String getEmployeeName();

    abstract public void setEmployeeName(String name);

    abstract public int getOfficeNumber();

    abstract public void setOfficeNumber(int officeNum);

    abstract public String getEquipmentList();

    abstract public void setEquipmentList(String equip);

    abstract public byte[] getSerializedReplyDestination();

    abstract public void setSerializedReplyDestination(byte[] byteArray);

    abstract public String getReplyCorrelationMsgId();

    abstract public void setReplyCorrelationMsgId(String msgId);

    /**
     * The getReplyDestination method extracts the
     * replyDestination from the serialized version that is
     * persisted, using a ByteArrayInputStream and
     * ObjectInputStream to read the object and casting it to a
     * Destination object.
     *
     * @return    the reply destination
     */
    private Destination getReplyDestination() {
        ByteArrayInputStream bais = null;
        ObjectInputStream ois = null;
        byte[] srd = null;

        srd = getSerializedReplyDestination();

        if ((replyDestination == null) && (srd != null)) {
            try {
                bais = new ByteArrayInputStream(srd);
                ois = new ObjectInputStream(bais);
                replyDestination = (Destination) ois.readObject();
                ois.close();
            } catch (IOException io) {
            } catch (ClassNotFoundException cnfe) {
            }
        }

        return replyDestination;
    }

    /**
     * The setReplyDestination method serializes the reply
     * destination so that it can be persisted.  It uses a
     * ByteArrayOutputStream and an ObjectOutputStream.
     *
     * @param replyDestination    the reply destination
     */
    private void setReplyDestination(Destination replyDestination) {
        ByteArrayOutputStream baos = null;
        ObjectOutputStream oos = null;

        this.replyDestination = replyDestination;

        try {
            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(replyDestination);
            oos.close();
            setSerializedReplyDestination(baos.toByteArray());
        } catch (IOException io) {
        }
    }

    /**
     * The doEquipmentList method stores the assigned equipment
     * in the database and retrieves the reply destination, then
     * determines if setup is complete.
     *
     * @param list    assigned equipment
     * @param msg     the message received
     */
    public void doEquipmentList(String list, Message msg)
        throws JMSException {
        setEquipmentList(list);
        setReplyDestination(msg.getJMSReplyTo());
        setReplyCorrelationMsgId(msg.getJMSMessageID());
        logger.info("SetupOfficeBean.doEquipmentList: Equipment for " +
            "employeeId " + getEmployeeId() + " is " + getEquipmentList() +
            " (office number " + getOfficeNumber() + ")");
        checkIfSetupComplete();
    }

    /**
     * The doOfficeNumber method stores the assigned office
     * number in the database and retrieves the reply
     * destination, then determines if setup is complete.
     *
     * @param officeNum    assigned office
     * @param msg          the message received
     */
    public void doOfficeNumber(int officeNum, Message msg)
        throws JMSException {
        setOfficeNumber(officeNum);
        setReplyDestination(msg.getJMSReplyTo());
        setReplyCorrelationMsgId(msg.getJMSMessageID());
        logger.info("SetupOfficeBean.doOfficeNumber: Office number for " +
            "employeeId " + getEmployeeId() + " is " + getOfficeNumber() +
            " (equipment " + getEquipmentList() + ")");
        checkIfSetupComplete();
    }

    /**
     * The checkIfSetupComplete method determines whether
     * both the office and the equipment have been assigned.  If
     * so, it sends a message to the reply queue with the
     * information about the assignments, then removes the
     * entity bean.
     */
    private void checkIfSetupComplete() {
        Connection connection = null;
        Session session = null;
        MessageProducer producer = null;
        MapMessage replyMsg = null;

        if ((getEquipmentList() != null) && (getOfficeNumber() != -1)) {
            logger.info("SetupOfficeBean.checkIfSetupComplete: SCHEDULE" +
                " employeeId=" + getEmployeeId() + ", Name=" +
                getEmployeeName() + " to be set up in office #" +
                getOfficeNumber() + " with " + getEquipmentList());

            try {
                connection = getConnection();
            } catch (Exception ex) {
                throw new EJBException("Unable to connect to " +
                    "JMS provider: " + ex.toString());
            }

            try {
                /*
                 * Send reply to messages aggregated by this
                 * composite entity bean.  Call createReplyMsg
                 * to construct the reply.
                 */
                session = connection.createSession(true, 0);
                producer = session.createProducer(getReplyDestination());
                replyMsg = createReplyMsg(session);
                producer.send(replyMsg);
                logger.info("SetupOfficeBean.checkIfSetupComplete: " +
                    "Sent reply message for employeeId " + getEmployeeId());
            } catch (JMSException je) {
                logger.severe("SetupOfficeBean.checkIfSetupComplete: " +
                    "JMSException: " + je.toString());
            }

            /*
             * Processing is complete, so remove the entity bean.
             */
            try {
                ejbRemove();
            } catch (Exception x) {
                logger.severe("SetupOfficeBean.checkIfSetupComplete: " +
                    "Failed to remove the bean");
            }
        }
    }

    /**
     * The createReplyMsg method composes the reply message
     * with the new hire information.
     *
     * @param session   the Session object for the message
     *                  producer
     *
     * @return  a MapMessage containing the reply message
     */
    private MapMessage createReplyMsg(Session session)
        throws JMSException {
        MapMessage replyMsg = session.createMapMessage();
        replyMsg.setString("employeeId", getEmployeeId());
        replyMsg.setString("employeeName", getEmployeeName());
        replyMsg.setString("equipmentList", getEquipmentList());
        replyMsg.setInt("officeNumber", getOfficeNumber());
        replyMsg.setJMSCorrelationID(getReplyCorrelationMsgId());

        return replyMsg;
    }

    /**
     * ejbCreateLocal method, declared as public (but not final
     * or static).  Stores the available information about the
     * new hire in the database.
     *
     * @param newhireID   ID assigned to the new hire
     * @param name        name of the new hire
     *
     * @return            null (required for CMP 2.0)
     */
    public String ejbCreateLocal(String newhireID, String name)
        throws CreateException {
        setEmployeeId(newhireID);
        setEmployeeName(name);
        setEquipmentList(null);
        setOfficeNumber(-1);

        this.connection = null;

        return null;
    }

    public void ejbRemove() {
        closeConnection();
        logger.info("SetupOfficeBean.ejbRemove: " +
            "REMOVING SetupOffice bean employeeId=" + getEmployeeId() +
            ", Name=" + getEmployeeName());
    }

    public void setEntityContext(EntityContext context) {
        this.context = context;
    }

    public void unsetEntityContext() {
        this.context = null;
    }

    public void ejbActivate() {
        setEmployeeId((String) context.getPrimaryKey());
    }

    public void ejbPassivate() {
        setEmployeeId(null);
        closeConnection();
    }

    public void ejbLoad() {
    }

    public void ejbStore() {
    }

    public void ejbPostCreateLocal(String newhireID, String name) {
    }

    /**
     * The getConnection method, called by the
     * checkIfSetupComplete method, looks up a connection
     * factory and creates a connection.
     *
     * @return   a Connection object
     */
    private Connection getConnection() throws NamingException, JMSException {
        if (connection == null) {
            InitialContext ic = new InitialContext();

            ConnectionFactory connectionFactory =
                (ConnectionFactory) ic.lookup(
                    "java:comp/env/jms/MyConnectionFactory");
            connection = connectionFactory.createConnection();
        }

        return connection;
    }

    /**
     * The closeConnection method, called by the ejbRemove and
     * ejbPassivate methods, closes the connection that was
     * created by the getConnection method.
     */
    private void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (JMSException je) {
                logger.severe("SetupOfficeBean.closeConnection: " +
                    "JMSException: " + je.toString());
            }

            connection = null;
        }
    }
}
