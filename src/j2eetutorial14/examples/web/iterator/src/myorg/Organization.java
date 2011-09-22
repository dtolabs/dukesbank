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


package myorg;

import java.util.*;


public class Organization {
    private TreeMap departments = null;

    public Organization() {
        departments = new TreeMap();
        addDepartment("Sales");

        Department dept = getDepartment("Sales");
        dept.addMember("Moran, Jo", "12345", "jlm");
        dept.addMember("Dillan, Terry", "98765", "trd");
        dept.addMember("Hansen, Lee", "45678", "lah");
        dept.addMember("Dupont, Jerry", "34567", "jad");
        addDepartment("Engineering");
        dept = getDepartment("Engineering");
        dept.addMember("Corelli, Jeff", "45454", "jc");
        dept.addMember("Dan, Tina", "78787", "tad");
        dept.addMember("Harvey, Ann", "43434", "ash");
        dept.addMember("Brown, Kim", "70707", "kjb");
        addDepartment("Marketing");
        dept = getDepartment("Marketing");
        dept.addMember("Cole, Jennifer", "12121", "jac");
        dept.addMember("Kramer, Dick", "98989", "djk");
        dept.addMember("Sheridan, Dave", "23232", "dhs");
        dept.addMember("Cluney, Sue", "10101", "slc");
    }

    public TreeMap getDepartments() {
        return departments;
    }

    public Department getDepartment(String name) {
        return (Department) departments.get(name);
    }

    public Collection getDepartmentValues() {
        return departments.values();
    }

    public Collection getDepartmentNames() {
        return departments.keySet();
    }

    public void addDepartment(String name) {
        Department aDept = new Department();
        departments.put(name, aDept);
    }
}
