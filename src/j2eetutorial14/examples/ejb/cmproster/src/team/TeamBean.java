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
import util.PlayerDetails;


public abstract class TeamBean implements EntityBean {
    private EntityContext context;

    // Access methods for persistent fields
    public abstract String getTeamId();

    public abstract void setTeamId(String id);

    public abstract String getName();

    public abstract void setName(String name);

    public abstract String getCity();

    public abstract void setCity(String city);

    // Access methods for relationship fields
    public abstract Collection getPlayers();

    public abstract void setPlayers(Collection players);

    public abstract LocalLeague getLeague();

    public abstract void setLeague(LocalLeague league);

    // Business methods
    public ArrayList getCopyOfPlayers() {
        Debug.print("TeamBean getCopyOfPlayers");

        ArrayList playerList = new ArrayList();
        Collection players = getPlayers();

        Iterator i = players.iterator();

        while (i.hasNext()) {
            LocalPlayer player = (LocalPlayer) i.next();
            PlayerDetails details =
                new PlayerDetails(player.getPlayerId(), player.getName(),
                    player.getPosition(), 0.00);

            playerList.add(details);
        }

        return playerList;
    }

    public void addPlayer(LocalPlayer player) {
        Debug.print("TeamBean addPlayer");

        try {
            Collection players = getPlayers();

            players.add(player);
        } catch (Exception ex) {
            throw new EJBException(ex.getMessage());
        }
    }

    public void dropPlayer(LocalPlayer player) {
        Debug.print("TeamBean dropPlayer");

        try {
            Collection players = getPlayers();

            players.remove(player);
        } catch (Exception ex) {
            throw new EJBException(ex.getMessage());
        }
    }

    // EntityBean  methods
    public String ejbCreate(String id, String name, String city)
        throws CreateException {
        Debug.print("TeamBean ejbCreate");
        setTeamId(id);
        setName(name);
        setCity(city);

        return null;
    }

    public void ejbPostCreate(String id, String name, String city)
        throws CreateException {
    }

    public void setEntityContext(EntityContext ctx) {
        context = ctx;
    }

    public void unsetEntityContext() {
        context = null;
    }

    public void ejbRemove() {
        Debug.print("TeamBean ejbRemove");
    }

    public void ejbLoad() {
        Debug.print("TeamBean ejbLoad");
    }

    public void ejbStore() {
        Debug.print("TeamBean ejbStore");
    }

    public void ejbPassivate() {
    }

    public void ejbActivate() {
    }
} // TeamBean class
