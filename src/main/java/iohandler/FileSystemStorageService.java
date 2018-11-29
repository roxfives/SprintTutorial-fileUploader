package iohandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

/**
 * This class is responsible for the IO operations and management of the files being uploaded/downloaded
 */
@Service
public class FileSystemStorageService implements StorageService {

    private final Path rootLocation;

    /**
    * Constructor which gets the path to the file to be handled
     *
     * @param properties object with information about the location to store the file
    */
    @Autowired //Annotations to warn the framework to inject StorageProperties
    public FileSystemStorageService(StorageProperties properties) {
        this.rootLocation = Paths.get(properties.getLocation());
    }

    /**
    * This method stores the file in local system of the server
     *
     * @param file the file to be stored
    */
    @Override
    public void store(MultipartFile file) {
        String filename = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file " + filename);
            }
            if (filename.contains("..")) {
                //  Avoids saving above the directory the files are supposed to be saved
                throw new StorageException("Cannot store file with relative path outside current directory " + filename);
            }

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, this.rootLocation.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
            }
        }
        catch (IOException e) {
            throw new StorageException("Failed to store file " + filename, e);
        }
    }

    /**
     * This method loads all the files that have been uploaded (it just looks under one level of the download directory)
     *
     * @return a stream of Path objects
     */
    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.rootLocation, 1)
                    .filter(path -> !path.equals(this.rootLocation))
                    .map(this.rootLocation::relativize);
        }
        catch (IOException e) {
            throw new StorageException("Failed to read stored files", e);
        }

    }

    /**
     * This method returns the path to the filename given by concatenating the filename with this path
     *
     * @param filename the name of the file to the concatenated to this path
     * */
    @Override
    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    /**
     * This method returns the given file as a Resource object
     *
     * @param filename the name of the file to be returned as a resource. The String should contain
     *                 only the base name of the file (not the path to it).
     * */
    @Override
    public Resource loadAsResource(String filename) {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            }
            else {
                throw new StorageFileNotFoundException(
                        "Could not read file: " + filename);

            }
        }
        catch (MalformedURLException e) {
            throw new StorageFileNotFoundException("Could not read file: " + filename, e);
        }
    }

    /**
     * Deletes the given file. For directories, deletes everything under it as well.
     * */
    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }

    /**
     * Initializes the directory structure for the server
     * */
    @Override
    public void init() {
        try {
            Files.createDirectories(rootLocation);
        }
        catch (IOException e) {
            throw new StorageException("Could not initialize storage", e);
        }
    }
}
