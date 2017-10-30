package br.ufrn.imd.behere;

/**
 * Created by claudio on 30/10/17.
 */

public class UserLink {

    private String name;
    private String description;

    public UserLink(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
