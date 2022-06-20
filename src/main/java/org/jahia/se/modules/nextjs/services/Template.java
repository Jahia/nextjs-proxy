package org.jahia.se.modules.nextjs.services;

/**
 * Template
 * <pre>
 * {
 *     "name": "about",
 *     "displayName": "About"
 * }
 * </pre>
 *
 * @author hduchesne
 */
public class Template {
    private String name;
    private String displayName;

    public Template(){}

    public void setName(String name) {
        this.name = name;
    }
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getName() {
        return name;
    }
    public String getDisplayName() {
        return displayName;
    }
}
