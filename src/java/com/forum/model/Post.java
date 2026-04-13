package com.forum.model;

import java.sql.Timestamp;

/**
 * Post — POJO representing a reply inside a thread.
 *
 * Module coverage: Module 1 — MVC Model layer
 */
public class Post {

    private int       id;
    private int       threadId;
    private int       userId;
    private String    body;
    private Timestamp createdAt;

    // Denormalised — populated by DAO JOIN
    private String    author;

    public Post() {}

    // ── Getters & Setters ─────────────────────────────────────────────────
    public int       getId()              { return id; }
    public void      setId(int id)        { this.id = id; }

    public int       getThreadId()               { return threadId; }
    public void      setThreadId(int threadId)   { this.threadId = threadId; }

    public int       getUserId()               { return userId; }
    public void      setUserId(int userId)     { this.userId = userId; }

    public String    getBody()             { return body; }
    public void      setBody(String body)  { this.body = body; }

    public Timestamp getCreatedAt()             { return createdAt; }
    public void      setCreatedAt(Timestamp ts) { this.createdAt = ts; }

    public String    getAuthor()               { return author; }
    public void      setAuthor(String author)  { this.author = author; }
}
