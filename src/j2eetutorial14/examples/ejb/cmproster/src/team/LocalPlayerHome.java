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


public interface LocalPlayerHome extends EJBLocalHome {
    public LocalPlayer create(String id, String name, String position,
        double salary) throws CreateException;

    public LocalPlayer findByPrimaryKey(String id) throws FinderException;

    public Collection findByPosition(String position) throws FinderException;

    public Collection findByHigherSalary(String name) throws FinderException;

    public Collection findBySalaryRange(double low, double high)
        throws FinderException;

    public Collection findByLeague(LocalLeague league)
        throws FinderException;

    public Collection findBySport(String sport) throws FinderException;

    public Collection findByCity(String city) throws FinderException;

    public Collection findAll() throws FinderException;

    public Collection findNotOnTeam() throws FinderException;

    public Collection findByPositionAndName(String position, String name)
        throws FinderException;

    public Collection findByTest(String parm1, String parm2, String parm3)
        throws FinderException;
}
