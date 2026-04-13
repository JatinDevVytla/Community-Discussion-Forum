package com.forum.servlet;

import com.forum.dao.PostDAO;
import com.forum.dao.ThreadDAO;
import com.forum.model.Post;
import com.forum.model.Thread;
import com.forum.util.JsonUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ThreadServlet — handles GET and POST for /threads
 *
 * GET  /threads               → list all threads (JSON)
 * GET  /threads?categoryId=N  → list threads in a category
 * GET  /threads?id=N          → single thread + its replies
 * POST /threads               → create a new thread
 *
 * Module coverage: Module 1 (MVC Controller), Module 4 (Servlet + JDBC)
 */
@WebServlet("/threads")
public class ThreadServlet extends HttpServlet {

    private final ThreadDAO threadDAO = new ThreadDAO();
    private final PostDAO   postDAO   = new PostDAO();

    // ── GET ───────────────────────────────────────────────────────────────
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws IOException {

        setCorsHeaders(res);
        res.setContentType("application/json;charset=UTF-8");

        String idParam  = req.getParameter("id");
        String catParam = req.getParameter("categoryId");

        if (idParam != null) {
            // ── Single thread + replies ────────────────────────────────────
            int threadId = Integer.parseInt(idParam);
            Thread thread = threadDAO.findById(threadId);

            if (thread == null) {
                res.setStatus(HttpServletResponse.SC_NOT_FOUND);
                res.getWriter().write("{\"error\":\"Thread not found\"}");
                return;
            }

            List<Post> posts = postDAO.findByThread(threadId);
            Map<String, Object> body = new HashMap<>();
            body.put("thread", thread);
            body.put("posts",  posts);
            res.getWriter().write(JsonUtil.toJson(body));

        } else if (catParam != null) {
            // ── Threads filtered by category ───────────────────────────────
            int categoryId = Integer.parseInt(catParam);
            List<Thread> threads = threadDAO.findByCategory(categoryId);
            writeThreadList(res, threads);

        } else {
            // ── All threads ────────────────────────────────────────────────
            List<Thread> threads = threadDAO.findAll();
            writeThreadList(res, threads);
        }
    }

    // ── POST ──────────────────────────────────────────────────────────────
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws IOException {

        setCorsHeaders(res);
        res.setContentType("application/json;charset=UTF-8");

        // Read raw JSON body from the request
        String json = readBody(req);

        // Deserialise JSON → Thread POJO
        Thread thread = JsonUtil.fromJson(json, Thread.class);

        // ── Server-side validation ─────────────────────────────────────────
        if (thread == null || isBlank(thread.getTitle())) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            res.getWriter().write("{\"error\":\"Title is required\"}");
            return;
        }
        if (isBlank(thread.getBody())) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            res.getWriter().write("{\"error\":\"Body is required\"}");
            return;
        }
        if (thread.getCategoryId() <= 0) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            res.getWriter().write("{\"error\":\"categoryId is required\"}");
            return;
        }
        if (thread.getUserId() <= 0) {
            // Default to user 1 for demo purposes (no auth)
            thread.setUserId(1);
        }

        // ── Persist and respond ────────────────────────────────────────────
        Thread created = threadDAO.create(thread);
        res.setStatus(HttpServletResponse.SC_CREATED);   // 201
        res.getWriter().write(JsonUtil.toJson(created));
    }

    // ── CORS pre-flight ───────────────────────────────────────────────────
    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse res) {
        setCorsHeaders(res);
        res.setStatus(HttpServletResponse.SC_OK);
    }

    // ── Helpers ───────────────────────────────────────────────────────────
    private void writeThreadList(HttpServletResponse res, List<Thread> threads)
            throws IOException {
        Map<String, Object> body = new HashMap<>();
        body.put("threads", threads);
        body.put("count",   threads.size());
        res.getWriter().write(JsonUtil.toJson(body));
    }

    private String readBody(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = req.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
        }
        return sb.toString();
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private void setCorsHeaders(HttpServletResponse res) {
        res.setHeader("Access-Control-Allow-Origin",  "*");
        res.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        res.setHeader("Access-Control-Allow-Headers", "Content-Type");
    }
}
