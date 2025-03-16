package xyz.tbvns.NsiWebsite;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import xyz.tbvns.NsiWebsite.Database.User;
import xyz.tbvns.NsiWebsite.Database.UserRepository;
import xyz.tbvns.NsiWebsite.Database.UserService;
import xyz.tbvns.NsiWebsite.Objects.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@RestController
public class Controller {
    private final UserService userService;
    private final UserRepository userRepository; // Add this line

    public Controller(UserService userService, UserRepository userRepository) { // Update constructor
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @PostMapping("/api/register")
    public LoginResponseDTO handleRegister(@RequestBody RegisterDTO registerDTO) {
        try {
            userService.createUser(
                    registerDTO.getUsername(),
                    registerDTO.getEmail(),
                    registerDTO.getPassword()
            );
            return new LoginResponseDTO(true, registerDTO.getUsername(),
                    registerDTO.getEmail(), null);
        } catch (IllegalArgumentException e) {
            return new LoginResponseDTO(false, registerDTO.getUsername(),
                    registerDTO.getEmail(), e.getMessage());
        }
    }

    @PostMapping("/api/login")
    public LoginResponseDTO handleLogin(@RequestBody LoginDTO loginDTO) {
        String token = userService.verifyUser(loginDTO.getUsername(), loginDTO.getPassword());
        if (token != null) {
            User user = userRepository.findByIdentifier(loginDTO.getUsername()).get();
            return new LoginResponseDTO(true, user.getUsername(),
                    user.getEmail(), token);
        }
        return new LoginResponseDTO(false, loginDTO.getUsername(),
                null, "Invalid credentials");
    }

    @GetMapping("/")
    public ModelAndView index(HttpServletRequest request) {
        long userCount = userRepository.count();

        String clientIp = request.getRemoteAddr();
        ModelAndView modelAndView = new ModelAndView("index");
        modelAndView.addObject("ip", clientIp);
        modelAndView.addObject("users", userCount);
        return modelAndView;
    }

    @GetMapping("/app")
    public ModelAndView app() { return new ModelAndView("app"); }

    @GetMapping("/login")
    public ModelAndView login() { return new ModelAndView("login"); }

    @GetMapping("/register")
    public ModelAndView register() { return new ModelAndView("register"); }

    @GetMapping("/about")
    public ModelAndView about() { return new ModelAndView("about"); }

    @GetMapping(value = "/asset", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<Resource> image(@RequestParam String name) throws IOException {
        try {
            final ByteArrayResource inputStream = new ByteArrayResource(Controller.class.getResourceAsStream("/assets/" + name).readAllBytes());
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .contentLength(inputStream.contentLength())
                    .body(inputStream);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .contentLength(0)
                    .body(null);
        }

    }
}