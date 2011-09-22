package org.jboss.roster;

/**
 *  A hibernate ready player class mapping onto
 *  the cmproster playerbean table
 */
public class Player {

    private String id;
    private String name;
    private String position;
    private double salary;

    public Player() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
	this.id = id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
	this.name = name;
    }


    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
	this.position = position;
    }

    public double getSalary() {
        return salary;
    }
    public void setSalary(double salary) {
	this.salary = salary;
    }
}
