package com.hp.epilepsy.widget.model;

import java.io.Serializable;

/**
 * Created by mahmoumo on 11/21/2015.
 */
public class CapturedImages implements Serializable {

    private static final long serialVersionUID = 4735771050859627147L;


    /** The path. */
    private String path = "";
    private String creationDate = "";

    /**
     * Gets the path.
     *
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * Sets the path.
     *
     * @param path the new path
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * Gets the creation date.
     *
     * @return the creation date
     */
    public String getCreationDate() {
        return creationDate;
    }

    /**
     * Sets the creation date.
     *
     * @param creationDate the new creation date
     */
    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

}
