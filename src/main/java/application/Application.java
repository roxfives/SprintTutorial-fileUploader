package application;

import controller.FileUploadController;
import iohandler.StorageProperties;
import iohandler.StorageService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * The class with the entry point of the application. It launches the server
 * */
@SpringBootApplication(scanBasePackageClasses = {StorageService.class, FileUploadController.class})
@EnableConfigurationProperties(StorageProperties.class)
public class Application {
    /**
     * The main method (entry point)
     * */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    /**
     * The Bean associated with the initialization of the application
     *
     * @param storageService the object that gives access to all features and data related to the
     *                       properties, storage and management of files in the server
     *
     * @return    the method to initialize the application
     * */
    @Bean
    CommandLineRunner init(StorageService storageService) {
        return (args) -> {
            storageService.deleteAll();
            storageService.init();
        };
    }
}