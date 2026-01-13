package com.ardentix.taskmanager.controller;

import com.ardentix.taskmanager.model.Task;
import com.ardentix.taskmanager.model.User;
import com.ardentix.taskmanager.repository.TaskRepository;
import com.ardentix.taskmanager.repository.UserRepository;
import com.ardentix.taskmanager.security.JwtUtil;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskRepository taskRepo;
    private final UserRepository userRepo;
    private final JwtUtil jwtUtil;

    public TaskController(TaskRepository taskRepo, UserRepository userRepo, JwtUtil jwtUtil) {
        this.taskRepo = taskRepo;
        this.userRepo = userRepo;
        this.jwtUtil = jwtUtil;
    }

    private User getUserFromToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);
        String username = jwtUtil.extractUsername(token);
        return userRepo.findByUsername(username).orElseThrow();
    }

    // READ
    @GetMapping
    public List<Task> getTasks(@RequestHeader("Authorization") String auth) {
        User user = getUserFromToken(auth);
        return taskRepo.findByUser(user);
    }

    // CREATE
    @PostMapping
    public Task createTask(@RequestHeader("Authorization") String auth,
                           @RequestBody Task task) {
        User user = getUserFromToken(auth);
        task.setUser(user);
        return taskRepo.save(task);
    }

    // UPDATE
    @PutMapping("/{id}")
    public Task updateTask(@RequestHeader("Authorization") String auth,
                           @PathVariable Long id,
                           @RequestBody Task updated) {

        User user = getUserFromToken(auth);
        Task task = taskRepo.findById(id).orElseThrow();

        if (!task.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Not allowed");
        }

        // 🔥 THIS WAS MISSING
        task.setTitle(updated.getTitle());
        task.setDescription(updated.getDescription());
        task.setPriority(updated.getPriority());

        return taskRepo.save(task);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public void deleteTask(@RequestHeader("Authorization") String auth,
                           @PathVariable Long id) {

        User user = getUserFromToken(auth);
        Task task = taskRepo.findById(id).orElseThrow();

        if (!task.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Not allowed");
        }

        taskRepo.delete(task);
    }
}
