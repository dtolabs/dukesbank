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


package team;

import java.util.*;
import javax.ejb.*;
import javax.naming.*;
import util.Debug;


public abstract class PlayerBean implements EntityBean {
    private EntityContext context;

    // Access methods for persistent fields
    public abstract String getPlayerId();

    public abstract void setPlayerId(String id);

    public abstract String getName();

    public abstract void setName(String name);

    public abstract String getPosition();

    public abstract void setPosition(String position);

    public abstract double getSalary();

    public abstract void setSalary(double salary);

    // Access methods for relationship fields
    public abstract Collection getTeams();

    public abstract void setTeams(Collection teams);

    // Select methods
    public abstract Collection ejbSelectLeagues(LocalPlayer player)
        throws FinderException;

    public abstract Collection ejbSelectSports(LocalPlayer player)
        throws FinderException;

    // Business methods
    public Collection getLeagues() throws FinderException {
        LocalPlayer player = (team.LocalPlayer) context.getEJBLocalObject();

        return ejbSelectLeagues(player);
    }

    public Collection getSports() throws FinderException {
        LocalPlayer player = (team.LocalPlayer) context.getEJBLocalObject();

        return ejbSelectSports(player);
    }

    // EntityBean  methods
    public String ejbCreate(String id, String name, String position,
        double salary) throws CreateException {
        Debug.print("PlayerBean ejbCreate");
        setPlayerId(id);
        setName(name);
        setPosition(position);
        setSalary(salary);

        return null;
    }

    public void ejbPostCreate(String id, String name, String position,
        double salary) throws CreateException {
    }

    public void setEntityContext(EntityContext ctx) {
        context = ctx;
    }

    public void unsetEntityContext() {
        context = null;
    }

    public void ejbRemove() {
        Debug.print("PlayerBean ejbRemove");
    }

    public void ejbLoad() {
        Debug.print("PlayerBean ejbLoad");
    }

    public void ejbStore() {
        Debug.print("PlayerBean ejbStore");
    }

    public void ejbPassivate() {
    }

    public void ejbActivate() {
    }
} // PlayerBean class
