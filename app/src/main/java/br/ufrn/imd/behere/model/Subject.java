package br.ufrn.imd.behere.model;

public class Subject {

    private Integer id;
    private String schedule;
    private String name;
    private String location;

    public Subject(Integer id, String schedule, String name, String location) {
        this.id = id;
        this.schedule = schedule;
        this.name = name;
        this.location = location;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
