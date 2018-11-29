package iohandler;

/**
 * This exception is thrown when the file resource that should be stored is not found (404)
 * */
public class StorageFileNotFoundException extends StorageException {
    public StorageFileNotFoundException(String message) {
        super(message);
    }

    public StorageFileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
