package com.georgi.shakev.OnlineVideoLearningPlatform.web;

import com.georgi.shakev.OnlineVideoLearningPlatform.Dto.UserRequestDto;
import com.georgi.shakev.OnlineVideoLearningPlatform.Dto.UserResponseDto;
import com.georgi.shakev.OnlineVideoLearningPlatform.exception.ResourceNotFoundException;
import com.georgi.shakev.OnlineVideoLearningPlatform.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Controller
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final UserService userService;
    private User principal;
    private final AuthenticationManager authenticationManager;
    @Autowired
    private HttpServletRequest context;

    @Autowired
    public UserController(UserService userService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }

    @GetMapping("/{username}")
    @PreAuthorize("principal.username == #username or hasRole('ADMIN')")
    public String getUser(Model model, @PathVariable("username") String username){
        UserResponseDto userResponse = userService.getUser(username);
        model.addAttribute("user", userResponse);
        UserRequestDto userRequest = new UserRequestDto(null, null);
        model.addAttribute("userRequest", userRequest);
        principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        model.addAttribute("username", principal.getUsername());
        log.info("User profile {} opened by {}", userResponse, principal.getUsername());
        return "user";
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String searchUsers(Model model,
                              HttpServletRequest request,
            @RequestParam(defaultValue = "0") Integer pageNo,
            @RequestParam(defaultValue = "9") Integer pageSize,
            @RequestParam(defaultValue = "username") String sortBy,
                              @RequestParam(value = "search", defaultValue = "") String username,
                              @AuthenticationPrincipal User principal) {
        if (request.getParameter("page") != null && !request.getParameter("page").isEmpty()) {
            pageNo = Integer.parseInt(request.getParameter("page"));
        }
        int allPages = userService.getAllUsers(pageNo, pageSize, sortBy, username).getTotalPages();
        if(pageNo < 0 || pageNo > allPages){
            throw new ResourceNotFoundException("Invalid page number.");
        }
        model.addAttribute("users", userService.getAllUsers(pageNo, pageSize, sortBy, username));
        model.addAttribute("page", pageNo + 1);
        model.addAttribute("search", username);
        model.addAttribute("allPagesNumber", allPages);
        log.info("User search for {}, page number {} accessed by {}", username, pageNo, principal.getUsername());
        return "users";
    }

    @PreAuthorize("principal.username == #username or hasRole('ADMIN')")
    @GetMapping("/{username}/update")
    public String updateUser(Model model, @PathVariable("username") String username){
        UserRequestDto user = new UserRequestDto(null, null);
        model.addAttribute("user", user);
        model.addAttribute("username", username);
        return "redirect:/users/" + username + "/updated";
    }

    @PreAuthorize("principal.username == #username or hasRole('ADMIN')")
    @PostMapping("/{username}/updated")
    public String update (Model model, @PathVariable("username") String username,
                          UserRequestDto user, BindingResult binding,
                          RedirectAttributes redirectAttr) {
        if(binding.hasErrors()) {
            redirectAttr.addFlashAttribute("user", user);
            redirectAttr.addFlashAttribute("org.springframework.validation.BindingResult.project", binding);
            return "redirect:/users/" + username + "?error";
        }
        principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(username.equals(principal.getUsername())) {
            UserResponseDto updatedUser = userService.updateUser(username, user);
            org.springframework.security.core.userdetails.User principal =
                    (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext()
                            .getAuthentication().getPrincipal();
            Authentication authentication = new UsernamePasswordAuthenticationToken(principal , principal.getPassword(),
                    principal.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            model.addAttribute("username", updatedUser.getUsername());
            log.info("User {} updated", updatedUser);
        }
        else{
            UserResponseDto updatedUser = userService.updateUser(username, user);
            model.addAttribute("username", updatedUser.getUsername());
            log.info("User {} updated", updatedUser);
        }

        model.addAttribute("username", principal.getUsername());

        return "redirect:/users/" + user.getUsername();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{username}/make-admin")
    public String makeAdmin (Model model, @PathVariable("username") String username,
                          UserRequestDto user) {
        principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(username.equals(principal.getUsername())) {
            UserResponseDto updatedUser = userService.makeAdmin(username);
            reLogin(context, username, principal.getPassword());
        }
        else{
            UserResponseDto updatedUser = userService.makeAdmin(username);
        }

        model.addAttribute("username", user.getUsername());
        log.info("User {} has admin rights", username);
        return "redirect:/users/" + username + "?success";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{username}/remove-admin")
    public String removeAdmin (Model model, @PathVariable("username") String username,
                             UserRequestDto user) {
        principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(username.equals(principal.getUsername())) {
            UserResponseDto updatedUser = userService.removeAdmin(username);
            reLogin(context, username, principal.getPassword());
        }
        else{
            UserResponseDto updatedUser = userService.removeAdmin(username);
        }

        log.info("User {} does not have admin rights", username);

        model.addAttribute("username", user.getUsername());

        return "redirect:/users/" + username + "?success";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{username}/make-moderator")
    public String makeModerator (Model model, @PathVariable("username") String username,
                             UserRequestDto user) {
        principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(username.equals(principal.getUsername())) {
            UserResponseDto updatedUser = userService.makeModerator(username);
            reLogin(context, username, principal.getPassword());
        }
        else{
            UserResponseDto updatedUser = userService.makeModerator(username);
        }

        model.addAttribute("username", user.getUsername());
        log.info("User {} has moderator rights", username);
        return "redirect:/users/" + username + "?success";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{username}/remove-moderator")
    public String removeModerator (Model model, @PathVariable("username") String username,
                                 UserRequestDto user) {
        principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(username.equals(principal.getUsername())) {
            UserResponseDto updatedUser = userService.removeModerator(username);
            reLogin(context, username, principal.getPassword());
        }
        else{
            UserResponseDto updatedUser = userService.removeModerator(username);
        }

        model.addAttribute("username", user.getUsername());
        log.info("User {} does not have moderator rights", username);
        return "redirect:/users/" + username + "?success";
    }

    @PreAuthorize("principal.username == #username or hasRole('ADMIN')")
    @GetMapping("/{username}/delete")
    public String deleteUser(@PathVariable(value = "username") String username,
                                 Model model, @AuthenticationPrincipal User principal) {
        userService.deleteUser(username);
        if(username.equals(principal.getUsername())){
            SecurityContextHolder.getContext().setAuthentication(null);
            log.info("User with username {} deleted", username);
            return"login";
        }
        else {
            principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            model.addAttribute("username", principal.getUsername());
            log.info("User with username {} deleted", username);
            return "redirect:/users?deleted";
        }
    }

    public void reLogin(HttpServletRequest request, String userName, String password) {
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(userName, password);

        Authentication authentication = authenticationManager.authenticate(authRequest);
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authentication);

        HttpSession session = request.getSession(true);
        session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);
    }

    @PreAuthorize("principal.username == #username or hasRole('ADMIN')")
    @GetMapping("/{username}/profile-picture")
    public ResponseEntity<?> getProfilePicture(@PathVariable String username){
        return userService.viewProfilePicture(username);
    }

    @PreAuthorize("principal.username == #username")
    @PostMapping("/{username}/profile-picture/upload")
    public String uploadProfilePicture(@PathVariable String username,
                                    @RequestParam("file") MultipartFile file) throws IOException {
        if(!file.isEmpty()) {
            userService.uploadProfilePicture(username, file);
        }
        log.info("User with username {} profile picture displayed", username);
         return "redirect:/users/" + username;
    }

    @PreAuthorize("principal.username == #username or hasRole('ADMIN')")
    @GetMapping("/{username}/remove-profile-picture")
    public String removeProfilePicture(@PathVariable String username, Model model){
         userService.removeProfilePicture(username);
        log.info("User with username {} profile picture removed", username);
        return "redirect:/users/" + username;
    }
}
