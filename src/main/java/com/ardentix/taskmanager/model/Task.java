package com.ardentix.taskmanager.model;

import jakarta.persistence.*;

@Entity
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 2000)
    private String description;

    private String priority = "LOW";   

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;



    public Long getId() { return id; }

    public String getTitle() { return title; }

    public String getDescription() { return description; }

    public String getPriority() { return priority; }

    public User getUser() { return user; }


    public void setId(Long id) { this.id = id; }

    public void setTitle(String title) { this.title = title; }

    public void setDescription(String description) { this.description = description; }

    public void setPriority(String priority) { this.priority = priority; }

    public void setUser(User user) { this.user = user; }
}
