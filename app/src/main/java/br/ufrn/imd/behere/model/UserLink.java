package br.ufrn.imd.behere.model;


public class UserLink {

    private int id;
    private String name;
    private LinkType type;
    private String description;

    public UserLink(int id, String name, LinkType type, String description) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public LinkType getType() {
        return type;
    }

    public void setType(LinkType type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public enum LinkType {
        STUDENT,
        PROFESSOR
    }
}
