package iohandler;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * A class that contains all the properties regarding the storage management system. Specifically,
 * in this case the only property is the location where the uploaded files are stored (the path
 * to the download directory).
 * */
@ConfigurationProperties("storage")
public class StorageProperties {
    private String location = "upload-dir"; //Directory location for storing files

    /**
     * The getter for the location string: the name of the base directory where the files are stored
     *
     * @return returns the string containing the name of the base directory where the files are stored
     * */
    public String getLocation() {
        return location;
    }

    /**
     * The setter for the location string: the name of the base directory where the files are stored
     * */
    public void setLocation(String location) {
        this.location = location;
    }

}
