package com.codeup.springblog.controllers;

import com.codeup.springblog.models.Post;
import com.codeup.springblog.repositories.PostRepository;
import com.codeup.springblog.models.User;
import com.codeup.springblog.repositories.UserRepository;
import com.codeup.springblog.services.EmailService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class PostController {
    private final PostRepository postDao;
    private final UserRepository userDao;
    private final EmailService emailSvc;

    public PostController(PostRepository postDao, UserRepository userDao, EmailService emailSvc) {
        this.postDao = postDao;
        this.userDao = userDao;
        this.emailSvc = emailSvc;
    }

    @GetMapping("/posts")
    public String viewPosts(Model model) {
        model.addAttribute("posts", postDao.findAll());
        return "posts/index";
    }

    @GetMapping("/posts/{id}")
    public String singlePost(@PathVariable long id, Model model) {
        Post post = postDao.findById(id);
        boolean isPostOwner = false;
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() != "anonymousUser") {
            User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            isPostOwner = currentUser.getId() == post.getUser().getId();
        }
        model.addAttribute("post", post);
        model.addAttribute("isPostOwner", isPostOwner);
        return "posts/show";
    }

    @GetMapping("/posts/{id}/edit")
    public String editForm(@PathVariable long id, Model model) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Post post = postDao.findById(id);
        if(currentUser.getId() == post.getUser().getId()) {
            model.addAttribute("post", post);
            return "posts/edit";
        }
        return "redirect:/posts/" + id;
    }

    @PostMapping("/posts/{id}/edit")
    public String editPost(@PathVariable long id,@ModelAttribute Post post) {
//        post.setUser(userDao.getById(1L));
//        postDao.save(post);
//        return "redirect:/posts/" + id;
        return createPost(post);
    }

    @PostMapping("/posts/delete/{id}")
    public String deletePost(@PathVariable long id) {
        postDao.delete(postDao.getById(id));
        return "redirect:/posts";
    }

    @GetMapping("/posts/create")
    public String showCreateForm(Model model) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(currentUser != null) {
            model.addAttribute("post", new Post());
            return "posts/create";
        }
       return "redirect:/posts";
    }

    @PostMapping("/posts/create")
    public String createPost(@ModelAttribute Post post) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        post.setUser(currentUser);
        emailSvc.prepareAndSend(post, "you created " + post.getTitle(), post.getBody());
        postDao.save(post);
        return "redirect:/posts";
    }

}
