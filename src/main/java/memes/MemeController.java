package memes;

import memes.exception.StorageException;
import memes.services.ImageManipulationService;
import memes.services.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.stream.Collectors;

@Controller
public class MemeController {

    private StorageService storageService;
    private ImageManipulationService imageService;

    @Autowired
    public MemeController(StorageService storageService, ImageManipulationService imageService) {
        this.storageService = storageService;
        this.imageService = imageService;
    }
    @GetMapping("/")
    public String listUploadedFiles(Model model) throws IOException {

        model.addAttribute("files", storageService
                .loadAll()
                .map(path ->
                        MvcUriComponentsBuilder
                                .fromMethodName(MemeController.class, "serveFile", path.getFileName().toString())
                                .build().toString())
                .collect(Collectors.toList()));

        model.addAttribute("meme", new Meme());

        return "uploadForm";
    }

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""+file.getFilename()+"\"")
                .body(file);
    }

    @GetMapping("/meme")
    public String memeForm(Model model) {
        model.addAttribute("meme", new Meme());
        return "meme_form";
    }

    @PostMapping("/meme")
    public String memeSubmit(@ModelAttribute Meme meme) {
        return "display_meme";
    }


    @PostMapping("/")
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
                                   @ModelAttribute Meme meme,
                                   RedirectAttributes redirectAttributes) {

        storageService.store(file);
        imageService.caption(file.getOriginalFilename(),
                             meme.getTopText().replaceAll("'", "").toUpperCase(),
                             meme.getBottomText().replaceAll("'", "").toUpperCase());
        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + "!");

        return "redirect:/";
    }

    @ExceptionHandler(StorageException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageException exc) {
        return ResponseEntity.notFound().build();
    }
}
