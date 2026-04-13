package com.forum.dao;

import com.forum.model.Thread;
import com.forum.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ThreadDAO — Data Access Object for the threads table.
 * Handles SELECT (all, by category, by id) and INSERT operations.
 *
 * Module coverage: Module 4 — JDBC (PreparedStatement, ResultSet, transactions)
 */
public class ThreadDAO {

    // Reusable base SELECT that JOINs author and category, counts replies
    private static final String BASE_SELECT =
        "SELECT t.id, t.category_id, t.user_id, t.title, t.body, t.created_at, " +
        "       u.username AS author, " +
        "       c.name     AS category_name, " +
        "       COUNT(p.id) AS reply_count " +
        "FROM   threads t " +
        "JOIN   users      u ON u.id = t.user_id " +
        "JOIN   categories c ON c.id = t.category_id " +
        "LEFT JOIN posts   p ON p.thread_id = t.id ";

    // ── READ ──────────────────────────────────────────────────────────────

    /**
     * Returns all threads, newest first.
     */
    public List<Thread> findAll() {
        return executeQuery(BASE_SELECT + "GROUP BY t.id ORDER BY t.created_at DESC", ps -> {});
    }

    /**
     * Returns threads belonging to a specific category.
     */
    public List<Thread> findByCategory(int categoryId) {
        return executeQuery(
            BASE_SELECT + "WHERE t.category_id = ? GROUP BY t.id ORDER BY t.created_at DESC",
            ps -> ps.setInt(1, categoryId)
        );
    }

    /**
     * Returns a single thread by id, or null if not found.
     */
    public Thread findById(int id) {
        List<Thread> results = executeQuery(
            BASE_SELECT + "WHERE t.id = ? GROUP BY t.id",
            ps -> ps.setInt(1, id)
        );
        return results.isEmpty() ? null : results.get(0);
    }

    // ── WRITE ─────────────────────────────────────────────────────────────

    /**
     * Inserts a new thread and returns the created object with its generated id.
     * Uses RETURN_GENERATED_KEYS to retrieve the auto-increment id.
     */
    public Thread create(Thread thread) {
        String sql = "INSERT INTO threads (category_id, user_id, title, body) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, thread.getCategoryId());
            ps.setInt(2, thread.getUserId());
            ps.setString(3, thread.getTitle());
            ps.setString(4, thread.getBody());
            ps.executeUpdate();

            // Retrieve the auto-generated primary key
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    thread.setId(keys.getInt(1));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Re-fetch the full thread so denormalised fields (author, categoryName) are populated
        return findById(thread.getId());
    }

    // ── Private Helpers ───────────────────────────────────────────────────

    /**
     * Generic query executor.
     * Accepts a SQL string and a lambda to bind parameters (avoids code duplication).
     */
    private List<Thread> executeQuery(String sql, StatementBinder binder) {
        List<Thread> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            binder.bind(ps);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /** Maps a ResultSet row to a Thread object. */
    private Thread mapRow(ResultSet rs) throws SQLException {
        Thread t = new Thread();
        t.setId(rs.getInt("id"));
        t.setCategoryId(rs.getInt("category_id"));
        t.setUserId(rs.getInt("user_id"));
        t.setTitle(rs.getString("title"));
        t.setBody(rs.getString("body"));
        t.setCreatedAt(rs.getTimestamp("created_at"));
        t.setAuthor(rs.getString("author"));
        t.setCategoryName(rs.getString("category_name"));
        t.setReplyCount(rs.getInt("reply_count"));
        return t;
    }

    /** Functional interface for binding PreparedStatement parameters. */
    @FunctionalInterface
    private interface StatementBinder {
        void bind(PreparedStatement ps) throws SQLException;
    }
}
