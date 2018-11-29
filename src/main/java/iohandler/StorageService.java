package iohandler;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * Interface that a class should implement to serve all the storage and directory management
 * for the uploading server
 * */
public interface StorageService {
    /**
     * Initializes the directory structure for the server
     * */
    void init();

    /**
     * This method stores the file in local system of the server
     *
     * @param file the file to be stored
     */
    void store(MultipartFile file);

    /**
     * This method loads all the files that have been uploaded (it just looks under one level of the download directory)
     *
     * @return a stream of Path objects
     */
    Stream<Path> loadAll();

    /**
     * This method returns the path to the filename given by concatenating the filename with this path
     *
     * @param filename the name of the file to the concatenated to this path
     * */
    Path load(String filename);

    /**
     * This method returns the given file as a Resource object
     *
     * @param filename the name of the file to be returned as a resource. The String should contain
     *                 only the base name of the file (not the path to it).
     * */
    Resource loadAsResource(String filename);

    /**
     * Deletes the given file. For directories, deletes everything under it as well.
     * */
    void deleteAll();
}
