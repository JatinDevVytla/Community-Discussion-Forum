package com.forum.model;

import java.sql.Timestamp;

/**
 * User — POJO representing a forum member.
 * No password field — authentication is out of scope for this capstone.
 *
 * Module coverage: Module 1 — MVC Model layer
 */
public class User {

    private int       id;
    private String    username;
    private String    email;
    private Timestamp createdAt;

    public User() {}

    public User(int id, String username, String email, Timestamp createdAt) {
        this.id        = id;
        this.username  = username;
        this.email     = email;
        this.createdAt = createdAt;
    }

    // ── Getters & Setters ─────────────────────────────────────────────────
    public int       getId()           { return id; }
    public void      setId(int id)     { this.id = id; }

    public String    getUsername()                { return username; }
    public void      setUsername(String username) { this.username = username; }

    public String    getEmail()             { return email; }
    public void      setEmail(String email) { this.email = email; }

    public Timestamp getCreatedAt()             { return createdAt; }
    public void      setCreatedAt(Timestamp ts) { this.createdAt = ts; }
}
