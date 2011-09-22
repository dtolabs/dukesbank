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


package roster;

import java.util.*;
import javax.ejb.EJBObject;
import java.rmi.RemoteException;
import util.*;


public interface Roster extends EJBObject {
    // Players
    public void createPlayer(PlayerDetails details) throws RemoteException;

    public void addPlayer(String playerId, String teamId)
        throws RemoteException;

    public void removePlayer(String playerId) throws RemoteException;

    public void dropPlayer(String playerId, String teamId)
        throws RemoteException;

    public PlayerDetails getPlayer(String playerId) throws RemoteException;

    public ArrayList getPlayersOfTeam(String teamId) throws RemoteException;

    public ArrayList getPlayersOfTeamCopy(String teamId)
        throws RemoteException;

    public ArrayList getPlayersByPosition(String position)
        throws RemoteException;

    public ArrayList getPlayersByHigherSalary(String name)
        throws RemoteException;

    public ArrayList getPlayersBySalaryRange(double low, double high)
        throws RemoteException;

    public ArrayList getPlayersByLeagueId(String leagueId)
        throws RemoteException;

    public ArrayList getPlayersBySport(String sport) throws RemoteException;

    public ArrayList getPlayersByCity(String city) throws RemoteException;

    public ArrayList getAllPlayers() throws RemoteException;

    public ArrayList getPlayersNotOnTeam() throws RemoteException;

    public ArrayList getPlayersByPositionAndName(String position, String name)
        throws RemoteException;

    public ArrayList getLeaguesOfPlayer(String playerId)
        throws RemoteException;

    public ArrayList getSportsOfPlayer(String playerId)
        throws RemoteException;

    // Teams
    public ArrayList getTeamsOfLeague(String leagueId)
        throws RemoteException;

    public void createTeamInLeague(TeamDetails details, String leagueId)
        throws RemoteException;

    public void removeTeam(String teamId) throws RemoteException;

    public TeamDetails getTeam(String teamId) throws RemoteException;

    // Leagues
    public void createLeague(LeagueDetails details) throws RemoteException;

    public void removeLeague(String leagueId) throws RemoteException;

    public LeagueDetails getLeague(String leagueId) throws RemoteException;

    // Test
    public ArrayList testFinder(String parm1, String parm2, String parm3)
        throws RemoteException;
}
