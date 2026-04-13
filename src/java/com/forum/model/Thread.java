package com.forum.model;

import java.sql.Timestamp;

/**
 * Thread — POJO representing a discussion thread.
 * Includes denormalised fields (author, categoryName, replyCount) so the
 * API response is self-contained without requiring client-side joins.
 *
 * Module coverage: Module 1 — MVC Model layer
 */
public class Thread {

    private int       id;
    private int       categoryId;
    private int       userId;
    private String    title;
    private String    body;
    private Timestamp createdAt;

    // Denormalised / computed — populated by DAO JOINs
    private String    author;
    private String    categoryName;
    private int       replyCount;

    public Thread() {}

    // ── Getters & Setters ─────────────────────────────────────────────────
    public int       getId()              { return id; }
    public void      setId(int id)        { this.id = id; }

    public int       getCategoryId()                 { return categoryId; }
    public void      setCategoryId(int categoryId)   { this.categoryId = categoryId; }

    public int       getUserId()               { return userId; }
    public void      setUserId(int userId)     { this.userId = userId; }

    public String    getTitle()              { return title; }
    public void      setTitle(String title)  { this.title = title; }

    public String    getBody()             { return body; }
    public void      setBody(String body)  { this.body = body; }

    public Timestamp getCreatedAt()             { return createdAt; }
    public void      setCreatedAt(Timestamp ts) { this.createdAt = ts; }

    public String    getAuthor()               { return author; }
    public void      setAuthor(String author)  { this.author = author; }

    public String    getCategoryName()                   { return categoryName; }
    public void      setCategoryName(String categoryName){ this.categoryName = categoryName; }

    public int       getReplyCount()               { return replyCount; }
    public void      setReplyCount(int replyCount) { this.replyCount = replyCount; }
}
