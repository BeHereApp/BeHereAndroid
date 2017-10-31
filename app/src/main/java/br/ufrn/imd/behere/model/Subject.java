package br.ufrn.imd.behere.model;

/**
 * Created by thaislins on 31/10/17.
 */

public class Subject {

    private String schedule;
    private String name;
    private String location;

    public Subject(String schedule, String name, String location) {
        this.schedule = schedule;
        this.name = name;
        this.location = location;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

}
