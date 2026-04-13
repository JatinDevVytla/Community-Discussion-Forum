package com.forum.dao;

import com.forum.model.Post;
import com.forum.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * PostDAO — Data Access Object for the posts table (thread replies).
 *
 * Module coverage: Module 4 — JDBC
 */
public class PostDAO {

    /**
     * Returns all posts for a given thread, oldest first (chronological order).
     */
    public List<Post> findByThread(int threadId) {
        List<Post> list = new ArrayList<>();
        String sql =
            "SELECT p.id, p.thread_id, p.user_id, p.body, p.created_at, u.username AS author " +
            "FROM   posts p " +
            "JOIN   users u ON u.id = p.user_id " +
            "WHERE  p.thread_id = ? " +
            "ORDER BY p.created_at ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, threadId);
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

    /**
     * Inserts a new reply and returns the created Post with its generated id.
     */
    public Post create(Post post) {
        String sql = "INSERT INTO posts (thread_id, user_id, body) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, post.getThreadId());
            ps.setInt(2, post.getUserId());
            ps.setString(3, post.getBody());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    post.setId(keys.getInt(1));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return post;
    }

    /** Maps a ResultSet row to a Post object. */
    private Post mapRow(ResultSet rs) throws SQLException {
        Post p = new Post();
        p.setId(rs.getInt("id"));
        p.setThreadId(rs.getInt("thread_id"));
        p.setUserId(rs.getInt("user_id"));
        p.setBody(rs.getString("body"));
        p.setCreatedAt(rs.getTimestamp("created_at"));
        p.setAuthor(rs.getString("author"));
        return p;
    }
}
