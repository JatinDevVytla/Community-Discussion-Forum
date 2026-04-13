/**
 * dom.js — DOM Manipulation & UI Rendering
 *
 * Responsible for:
 *  - Rendering category list, thread cards, reply cards
 *  - Showing/hiding loading and empty states
 *  - Toast notifications
 *  - Utility helpers for creating elements
 *
 * Module coverage: Module 2 — DOM manipulation, event handling
 */

'use strict';

// ── Toast Notification ────────────────────────────────────────────────────────
/**
 * Displays a brief toast message in the bottom-right corner.
 * @param {string} message
 * @param {'success'|'error'} type
 */
function showToast(message, type = 'success') {
    // Remove any existing toast
    const existing = document.querySelector('.toast');
    if (existing) existing.remove();

    const toast = document.createElement('div');
    toast.className = `toast ${type}`;
    toast.textContent = message;
    document.body.appendChild(toast);

    // Trigger CSS transition
    requestAnimationFrame(() => {
        requestAnimationFrame(() => toast.classList.add('show'));
    });

    setTimeout(() => {
        toast.classList.remove('show');
        setTimeout(() => toast.remove(), 300);
    }, 3000);
}

// ── Loading / Empty States ────────────────────────────────────────────────────
function showLoading(containerId, message = 'Loading…') {
    const el = document.getElementById(containerId);
    if (el) el.innerHTML = `<p class="loading-msg">${message}</p>`;
}

function showEmpty(containerId, message = 'Nothing here yet.') {
    const el = document.getElementById(containerId);
    if (el) el.innerHTML = `<p class="empty-msg">${message}</p>`;
}

function clearContainer(containerId) {
    const el = document.getElementById(containerId);
    if (el) el.innerHTML = '';
}

// ── Category Rendering ────────────────────────────────────────────────────────
/**
 * Renders the sidebar category list.
 * Highlights the currently active category.
 *
 * @param {Array}  categories
 * @param {number} activeCategoryId  - currently selected category (0 = All)
 * @param {Function} onSelect        - callback(categoryId)
 */
function renderCategoryList(categories, activeCategoryId, onSelect) {
    const container = document.getElementById('category-list');
    if (!container) return;

    container.innerHTML = '';

    // "All" option
    const allLi = document.createElement('li');
    allLi.innerHTML = `
        <a href="#" class="${activeCategoryId === 0 ? 'active' : ''}" data-id="0">
            <span class="cat-dot" style="background:#888"></span> All Threads
        </a>`;
    allLi.querySelector('a').addEventListener('click', e => {
        e.preventDefault();
        onSelect(0);
    });
    container.appendChild(allLi);

    // One item per category
    const colours = ['#3a5fd9', '#2a7d4f', '#b85c0d', '#7b3fb5', '#c93232'];
    categories.forEach((cat, i) => {
        const li   = document.createElement('li');
        const color = colours[i % colours.length];
        const isActive = cat.id === activeCategoryId;

        li.innerHTML = `
            <a href="#" class="${isActive ? 'active' : ''}" data-id="${cat.id}">
                <span class="cat-dot" style="background:${color}"></span>
                ${ForumValidation.escapeHtml(cat.name)}
            </a>`;

        li.querySelector('a').addEventListener('click', e => {
            e.preventDefault();
            onSelect(cat.id);
        });
        container.appendChild(li);
    });
}

// ── Thread List Rendering ─────────────────────────────────────────────────────
/**
 * Renders thread cards into #thread-list.
 * @param {Array} threads
 */
function renderThreadList(threads) {
    const container = document.getElementById('thread-list');
    if (!container) return;

    if (!threads || threads.length === 0) {
        showEmpty('thread-list', 'No threads in this category yet. Be the first to post!');
        return;
    }

    container.innerHTML = '';
    threads.forEach(thread => {
        const card = createThreadCard(thread);
        container.appendChild(card);
    });
}

/**
 * Creates a single thread card DOM element.
 * @param {Object} thread
 * @returns {HTMLElement}
 */
function createThreadCard(thread) {
    const card = document.createElement('div');
    card.className = 'thread-card';

    const date = formatDate(thread.createdAt);
    const replies = thread.replyCount || 0;

    card.innerHTML = `
        <h3>
            <a href="thread.html?id=${thread.id}">
                ${ForumValidation.escapeHtml(thread.title)}
            </a>
        </h3>
        <div class="thread-meta">
            <span>by <strong>${ForumValidation.escapeHtml(thread.author || 'Anonymous')}</strong></span>
            <span>${ForumValidation.escapeHtml(thread.categoryName || '')}</span>
            <span>${replies} ${replies === 1 ? 'reply' : 'replies'}</span>
            <span>${date}</span>
        </div>`;

    // Clicking the card body (not the link) also navigates
    card.addEventListener('click', e => {
        if (e.target.tagName !== 'A') {
            window.location.href = `thread.html?id=${thread.id}`;
        }
    });

    return card;
}

/**
 * Prepends a newly created thread card to the top of the list
 * (called after a successful POST so no full reload is needed).
 * @param {Object} thread
 */
function prependThreadCard(thread) {
    const container = document.getElementById('thread-list');
    if (!container) return;

    // Remove "empty" message if present
    const empty = container.querySelector('.empty-msg');
    if (empty) empty.remove();

    const card = createThreadCard(thread);
    container.insertBefore(card, container.firstChild);
}

// ── Thread Detail Rendering ───────────────────────────────────────────────────
/**
 * Renders the thread title, body and metadata on thread.html.
 * @param {Object} thread
 */
function renderThreadDetail(thread) {
    const header = document.getElementById('thread-header');
    if (!header) return;

    const date = formatDate(thread.createdAt);

    header.innerHTML = `
        <div class="thread-detail-header">
            <span class="badge">${ForumValidation.escapeHtml(thread.categoryName || '')}</span>
            <h1>${ForumValidation.escapeHtml(thread.title)}</h1>
            <div class="thread-meta" style="margin-top:.5rem">
                <span>by <strong>${ForumValidation.escapeHtml(thread.author || 'Anonymous')}</strong></span>
                <span>${date}</span>
                <span>${thread.replyCount || 0} replies</span>
            </div>
            <p class="thread-detail-body">${ForumValidation.escapeHtml(thread.body)}</p>
        </div>`;

    // Update page title
    document.title = `${thread.title} — Forum`;
}

// ── Reply Rendering ───────────────────────────────────────────────────────────
/**
 * Renders all reply cards into #reply-list.
 * @param {Array} posts
 */
function renderReplies(posts) {
    const container = document.getElementById('reply-list');
    if (!container) return;

    if (!posts || posts.length === 0) {
        container.innerHTML = '<p class="empty-msg">No replies yet. Start the conversation!</p>';
        return;
    }

    container.innerHTML = '';
    posts.forEach(post => container.appendChild(createReplyCard(post)));
}

/**
 * Creates a single reply card DOM element.
 * @param {Object} post
 * @returns {HTMLElement}
 */
function createReplyCard(post) {
    const card = document.createElement('div');
    card.className = 'reply-card';
    card.innerHTML = `
        <div class="reply-author">${ForumValidation.escapeHtml(post.author || 'Anonymous')}</div>
        <div class="reply-body">${ForumValidation.escapeHtml(post.body)}</div>
        <div class="reply-time">${formatDate(post.createdAt)}</div>`;
    return card;
}

/**
 * Appends a newly submitted reply card to #reply-list.
 * @param {Object} post
 */
function appendReplyCard(post) {
    const container = document.getElementById('reply-list');
    if (!container) return;

    const empty = container.querySelector('.empty-msg');
    if (empty) empty.remove();

    container.appendChild(createReplyCard(post));
}

// ── Category Dropdown ─────────────────────────────────────────────────────────
/**
 * Populates a <select> element with categories.
 * @param {string} selectId
 * @param {Array}  categories
 */
function populateCategorySelect(selectId, categories) {
    const select = document.getElementById(selectId);
    if (!select) return;

    select.innerHTML = '<option value="">— Select a category —</option>';
    categories.forEach(cat => {
        const opt = document.createElement('option');
        opt.value       = cat.id;
        opt.textContent = cat.name;
        select.appendChild(opt);
    });
}

// ── Utility ───────────────────────────────────────────────────────────────────
/**
 * Formats an ISO timestamp into a readable local date/time string.
 * @param {string} isoString
 * @returns {string}
 */
function formatDate(isoString) {
    if (!isoString) return '';
    try {
        return new Date(isoString).toLocaleString(undefined, {
            year:   'numeric',
            month:  'short',
            day:    'numeric',
            hour:   '2-digit',
            minute: '2-digit'
        });
    } catch {
        return isoString;
    }
}

/**
 * Reads a query-string parameter from the current URL.
 * @param {string} name
 * @returns {string|null}
 */
function getQueryParam(name) {
    return new URLSearchParams(window.location.search).get(name);
}

// ── Expose to global scope ────────────────────────────────────────────────────
window.ForumDOM = {
    showToast,
    showLoading,
    showEmpty,
    clearContainer,
    renderCategoryList,
    renderThreadList,
    prependThreadCard,
    renderThreadDetail,
    renderReplies,
    appendReplyCard,
    populateCategorySelect,
    formatDate,
    getQueryParam
};
