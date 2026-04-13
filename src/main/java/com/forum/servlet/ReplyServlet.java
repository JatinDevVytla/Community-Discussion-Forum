package com.forum.servlet;

import com.forum.dao.PostDAO;
import com.forum.model.Post;
import com.forum.util.JsonUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ReplyServlet — handles GET and POST for /replies
 *
 * GET  /replies?threadId=N  → all posts for a thread
 * POST /replies              → submit a new reply
 *
 * Module coverage: Module 4 — Servlet + JDBC
 */
@WebServlet("/replies")
public class ReplyServlet extends HttpServlet {

    private final PostDAO dao = new PostDAO();

    // ── GET ───────────────────────────────────────────────────────────────
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws IOException {

        setCorsHeaders(res);
        res.setContentType("application/json;charset=UTF-8");

        String threadIdParam = req.getParameter("threadId");
        if (threadIdParam == null) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            res.getWriter().write("{\"error\":\"threadId parameter is required\"}");
            return;
        }

        int threadId = Integer.parseInt(threadIdParam);
        List<Post> posts = dao.findByThread(threadId);

        Map<String, Object> body = new HashMap<>();
        body.put("posts", posts);
        body.put("count", posts.size());
        res.getWriter().write(JsonUtil.toJson(body));
    }

    // ── POST ──────────────────────────────────────────────────────────────
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws IOException {

        setCorsHeaders(res);
        res.setContentType("application/json;charset=UTF-8");

        // Parse JSON body
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = req.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
        }
        Post post = JsonUtil.fromJson(sb.toString(), Post.class);

        // ── Validation ─────────────────────────────────────────────────────
        if (post == null || post.getBody() == null || post.getBody().trim().isEmpty()) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            res.getWriter().write("{\"error\":\"Reply body is required\"}");
            return;
        }
        if (post.getThreadId() <= 0) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            res.getWriter().write("{\"error\":\"threadId is required\"}");
            return;
        }
        if (post.getUserId() <= 0) {
            post.setUserId(1); // Default to user 1 for demo (no auth)
        }

        Post created = dao.create(post);
        res.setStatus(HttpServletResponse.SC_CREATED);
        res.getWriter().write(JsonUtil.toJson(created));
    }

    // ── CORS pre-flight ───────────────────────────────────────────────────
    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse res) {
        setCorsHeaders(res);
        res.setStatus(HttpServletResponse.SC_OK);
    }

    private void setCorsHeaders(HttpServletResponse res) {
        res.setHeader("Access-Control-Allow-Origin",  "*");
        res.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        res.setHeader("Access-Control-Allow-Headers", "Content-Type");
    }
}
