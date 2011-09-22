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

import java.rmi.RemoteException;
import javax.rmi.PortableRemoteObject;
import javax.ejb.*;
import javax.naming.*;
import javax.jms.*;
import java.util.Random;
import java.util.logging.*;


/**
 * The ReserveEquipmentMsgBean class is a message-driven bean.
 * It implements the javax.ejb.MessageDrivenBean and
 * javax.jms.MessageListener interfaces. It is defined as public
 * (but not final or abstract).  It defines a constructor and the
 * methods ejbCreate, onMessage, setMessageDrivenContext, and
 * ejbRemove.
 */
public class ReserveEquipmentMsgBean implements MessageDrivenBean,
    MessageListener {
    static final Logger logger = Logger.getLogger("ReserveEquipmentMsgBean");
    private transient MessageDrivenContext mdc = null;
    private SetupOfficeLocalHome soLocalHome = null;
    private Random processingTime = new Random();

    /**
     * Constructor, which is public and takes no arguments.
     */
    public ReserveEquipmentMsgBean() {
        logger.info("In ReserveEquipmentMsgBean.ReserveEquipmentMsgBean()");
    }

    /**
     * setMessageDrivenContext method, declared as public (but
     * not final or static), with a return type of void, and with
     * one argument of type javax.ejb.MessageDrivenContext.
     *
     * @param mdc    the context to set
     */
    public void setMessageDrivenContext(MessageDrivenContext mdc) {
        logger.info("In ReserveEquipmentMsgBean.setMessageDrivenContext()");
        this.mdc = mdc;
    }

    /**
     * ejbCreate method, declared as public (but not final or
     * static), with a return type of void, and with no
     * arguments. It looks up the entity bean and gets a handle
     * to its home interface.
     */
    public void ejbCreate() {
        logger.info("In ReserveEquipmentMsgBean.ejbCreate()");

        try {
            Context initial = new InitialContext();
            Object objref =
                initial.lookup("java:comp/env/ejb/local/SetupOffice");
            soLocalHome = (SetupOfficeLocalHome) PortableRemoteObject.narrow(objref,
                    SetupOfficeLocalHome.class);
        } catch (Exception ex) {
            logger.severe("ReserveEquipmentMsgBean.ejbCreate: Exception: " +
                ex.toString());
        }
    }

    /**
     * onMessage method, declared as public (but not final or
     * static), with a return type of void, and with one argument
     * of type javax.jms.Message.
     *
     * Casts the incoming Message to a MapMessage, retrieves its
     * contents, and assigns equipment appropriate to the new
     * hire's position.  Calls the compose method to store the
     * information in the entity bean.
     *
     * @param inMessage    the incoming message
     */
    public void onMessage(Message inMessage) {
        MapMessage msg = null;
        String key = null;
        String name = null;
        String position = null;
        String equipmentList = null;

        try {
            if (inMessage instanceof MapMessage) {
                msg = (MapMessage) inMessage;
                key = msg.getString("HireID");
                name = msg.getString("Name");
                position = msg.getString("Position");
                logger.info("ReserveEquipmentMsgBean:" +
                    " Message received for employeeId " + key);

                if (position.equals("Programmer")) {
                    equipmentList = "Desktop System";
                } else if (position.equals("Senior Programmer")) {
                    equipmentList = "Laptop";
                } else if (position.equals("Manager")) {
                    equipmentList = "Pager";
                } else if (position.equals("Director")) {
                    equipmentList = "Java Phone";
                } else {
                    equipmentList = "Baton";
                }

                /* Simulate processing time taking 1 to 10
                 * seconds.
                 */
                Thread.sleep(processingTime.nextInt(10) * 1000);
                compose(key, name, equipmentList, msg);
            } else {
                logger.warning("Message of wrong type: " +
                    inMessage.getClass().getName());
            }
        } catch (JMSException e) {
            logger.severe("ReserveEquipmentMsgBean.onMessage: JMSException: " +
                e.toString());
            mdc.setRollbackOnly();
        } catch (Throwable te) {
            logger.severe("ReserveEquipmentMsgBean.onMessage: Exception: " +
                te.toString());
        }
    }

    /**
     * compose method, helper to onMessage method.
     *
     * Locates the row of the database represented by the primary
     * key and adds the equipment allocated for the new hire.
     *
     * @param key           employee ID, primary key
     * @param name          employee name
     * @param equipmentList equipment allocated based on position
     * @param msg           the message received
     */
    void compose(String key, String name, String equipmentList, Message msg) {
        int num = 0;
        SetupOfficeLocal so = null;

        try {
            so = soLocalHome.findByPrimaryKey(key);
            logger.info("ReserveEquipmentMsgBean: " +
                "Found join entity bean for employeeId " + key);
        } catch (ObjectNotFoundException onfe) {
            logger.warning("ReserveEquipmentMsgBean: " +
                "No join entity bean found");
        } catch (FinderException fe) {
            mdc.setRollbackOnly();
        }

        // No entity bean found; create it
        if (so == null) {
            try {
                logger.info("ReserveEquipmentMsgBean:" +
                    " Creating join entity bean for " + "employeeId " + key);
                so = soLocalHome.createLocal(key, name);
            } catch (CreateException e) {
                logger.warning("ReserveEquipmentMsgBean:" +
                    " Could not create join entity bean");
                mdc.setRollbackOnly();
            }
        }

        // Entity bean found or created, so add equipment
        if (so != null) {
            try {
                so.doEquipmentList(equipmentList, msg);
            } catch (JMSException e) {
                logger.severe("ReserveEquipmentMsgBean:" +
                    " Could not get equipment");
                mdc.setRollbackOnly();
            }
        }
    }

    /**
     * ejbRemove method, declared as public (but not final or
     * static), with a return type of void, and with no
     * arguments.
     */
    public void ejbRemove() {
        logger.info("In ReserveEquipmentMsgBean.ejbRemove()");
    }
}
