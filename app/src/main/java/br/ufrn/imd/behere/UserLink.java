package br.ufrn.imd.behere;

/**
 * Created by claudio on 30/10/17.
 */

public class UserLink {

    private LinkType type;
    private String description;

    public UserLink(LinkType type, String description) {
        this.type = type;
        this.description = description;
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
