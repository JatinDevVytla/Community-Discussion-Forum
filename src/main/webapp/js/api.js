/**
 * api.js — API Communication Layer
 *
 * Wraps all fetch() calls to the Tomcat backend.
 * Handles JSON serialisation/deserialisation and HTTP error mapping.
 *
 * Module coverage: Module 3 — JSON data interchange, XML alternative
 */

'use strict';

// Context path — change if Tomcat is deployed under a different path
const API_BASE = '/forum-app';

// ── Generic Fetch Wrapper ─────────────────────────────────────────────────────
/**
 * Makes an HTTP request and returns the parsed JSON response.
 *
 * @param {string} path      - API path e.g. '/threads'
 * @param {Object} [options] - standard fetch() options (method, body, headers)
 * @returns {Promise<any>}
 * @throws {Error} with a descriptive message on HTTP or network failure
 */
async function apiFetch(path, options = {}) {
    const defaultHeaders = { 'Content-Type': 'application/json' };

    const response = await fetch(API_BASE + path, {
        headers: { ...defaultHeaders, ...(options.headers || {}) },
        ...options
    });

    // Parse response body regardless of status (error bodies contain {error:...})
    let data;
    try {
        data = await response.json();
    } catch {
        data = null;
    }

    if (!response.ok) {
        const message = data?.error || `HTTP ${response.status}: ${response.statusText}`;
        throw new Error(message);
    }

    return data;
}

// ── Category API ──────────────────────────────────────────────────────────────
const CategoryAPI = {
    /**
     * GET /categories → { categories: [...] }
     */
    list() {
        return apiFetch('/categories');
    },

    /**
     * GET /categories (Accept: application/xml) — demonstrates XML alternative
     * Returns raw XML string for Module 3 XML demonstration.
     */
    async listAsXml() {
        const response = await fetch(API_BASE + '/categories', {
            headers: { 'Accept': 'application/xml' }
        });
        return response.text();  // Return raw XML string
    }
};

// ── Thread API ────────────────────────────────────────────────────────────────
const ThreadAPI = {
    /**
     * GET /threads                 → { threads: [...], count: N }
     * GET /threads?categoryId=N    → filtered by category
     */
    list(categoryId = null) {
        const qs = categoryId ? `?categoryId=${categoryId}` : '';
        return apiFetch(`/threads${qs}`);
    },

    /**
     * GET /threads?id=N → { thread: {...}, posts: [...] }
     */
    get(threadId) {
        return apiFetch(`/threads?id=${threadId}`);
    },

    /**
     * POST /threads
     * @param {{ title, body, categoryId, userId }} data
     * @returns {Promise<Thread>} created thread object
     */
    create(data) {
        return apiFetch('/threads', {
            method: 'POST',
            body:   JSON.stringify(data)
        });
    }
};

// ── Reply API ─────────────────────────────────────────────────────────────────
const ReplyAPI = {
    /**
     * GET /replies?threadId=N → { posts: [...], count: N }
     */
    list(threadId) {
        return apiFetch(`/replies?threadId=${threadId}`);
    },

    /**
     * POST /replies
     * @param {{ threadId, userId, body }} data
     * @returns {Promise<Post>} created post object
     */
    create(data) {
        return apiFetch('/replies', {
            method: 'POST',
            body:   JSON.stringify(data)
        });
    }
};

// ── Expose to global scope ────────────────────────────────────────────────────
window.ForumAPI = { CategoryAPI, ThreadAPI, ReplyAPI };
