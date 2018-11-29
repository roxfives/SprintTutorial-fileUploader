package controller;

import iohandler.StorageFileNotFoundException;
import iohandler.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.stream.Collectors;

/**
 * This class is responsible to handle every request to the server, from the welcome
 * page and uploading files to errors thrown
 * */
@Controller
public class FileUploadController {
    // The object responsible for providing all features and properties related to the storage and management of the files
    private final StorageService storageService;

    /**
     * Constructor which receives the object responsible for the management of the files
     *
     * @param storageService object responsible for providing all features and properties
     *                       related to the storage and management of the files
     * */
    @Autowired
    public FileUploadController(StorageService storageService) {
        this.storageService = storageService;
    }

    /**
     * Method responsible for building the root/index page. Besides using a template to build the page, it adds
     * the list of already uploaded files to the root page (the root/index page). It is triggered on a GET
     * request to the root/index page
     *
     * @param model the map with the page attributes
     *
     * @return    string containing the name of the template used to build the http
     *            response (the root/index page)
     * */
    @GetMapping("/")
    public String listUploadedFiles(Model model) throws IOException {

        model.addAttribute("files", storageService.loadAll().map(
                path -> MvcUriComponentsBuilder.fromMethodName(FileUploadController.class,
                        "serveFile",
                        path.getFileName().toString()).build().toString()
                ).collect(Collectors.toList()));

        return "uploadForm";
    }

    /**
     * This method is responsible for handling the request for downloading a file from the server.
     * It is triggered on a GET request to /file/<filename>, where filename is the name of the
     * requested file.
     *
     * @param filename the name of the file to be sent from the server
     *
     * @return    the http response to the download request
     * */
    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        Resource file = storageService.loadAsResource(filename); // File to be sent

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    /**
     * Method responsible for uploading a file to the server. It is triggered on a POST request
     * to the root page
     *
     * @param file the file to be uploaded
     * @param redirectAttributes the attributes of the redirect object. It is used to add a
     *                           message to the page once the upload is complete
     *
     * @return    the path to which the page is redirected after uploading the file
     * */
    @PostMapping("/")
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) {

        storageService.store(file);
        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + "!");

        return "redirect:/";
    }

    /**
     * Method responsible for handling the request to download of a file that isn't in the server
     *
     * @param exc the exception object
     *
     * @return    the http response when the exception occurs
     * */
    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }
}