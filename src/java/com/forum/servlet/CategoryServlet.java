package com.forum.servlet;

import com.forum.dao.CategoryDAO;
import com.forum.model.Category;
import com.forum.util.JsonUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CategoryServlet — handles GET /categories
 *
 * Demonstrates Module 3: returns JSON by default; returns XML when the client
 * sends Accept: application/xml (showing both data formats).
 *
 * Module coverage: Module 1 (MVC Controller), Module 3 (JSON + XML), Module 4 (Servlet)
 */
@WebServlet("/categories")
public class CategoryServlet extends HttpServlet {

    private final CategoryDAO dao = new CategoryDAO();

    /**
     * GET /categories
     * Returns all categories.
     * Respects the Accept header — JSON (default) or XML.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws IOException {

        setCorsHeaders(res);
        List<Category> categories = dao.findAll();

        String accept = req.getHeader("Accept");

        if (accept != null && accept.contains("application/xml")) {
            // ── XML response (Module 3 — XML alternative) ─────────────────
            res.setContentType("application/xml;charset=UTF-8");
            StringBuilder xml = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<categories>\n");
            for (Category c : categories) {
                xml.append("  <category>\n")
                   .append("    <id>").append(c.getId()).append("</id>\n")
                   .append("    <name>").append(escapeXml(c.getName())).append("</name>\n")
                   .append("    <description>").append(escapeXml(c.getDescription())).append("</description>\n")
                   .append("  </category>\n");
            }
            xml.append("</categories>");
            res.getWriter().write(xml.toString());

        } else {
            // ── JSON response (default) ────────────────────────────────────
            res.setContentType("application/json;charset=UTF-8");
            Map<String, Object> body = new HashMap<>();
            body.put("categories", categories);
            res.getWriter().write(JsonUtil.toJson(body));
        }
    }

    // ── CORS pre-flight ───────────────────────────────────────────────────
    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse res) {
        setCorsHeaders(res);
        res.setStatus(HttpServletResponse.SC_OK);
    }

    // ── Helpers ───────────────────────────────────────────────────────────
    private void setCorsHeaders(HttpServletResponse res) {
        res.setHeader("Access-Control-Allow-Origin",  "*");
        res.setHeader("Access-Control-Allow-Methods", "GET, OPTIONS");
        res.setHeader("Access-Control-Allow-Headers", "Content-Type, Accept");
    }

    /** Escapes the five predefined XML entities. */
    private String escapeXml(String s) {
        if (s == null) return "";
        return s.replace("&",  "&amp;")
                .replace("<",  "&lt;")
                .replace(">",  "&gt;")
                .replace("\"", "&quot;")
                .replace("'",  "&apos;");
    }
}
