/**
 * validation.js — Client-Side Form Validation
 *
 * Demonstrates:
 *  - Regular expressions for pattern matching
 *  - DOM event listeners (blur, input, submit)
 *  - Real-time error display
 *
 * Module coverage: Module 2 — Client-Side Scripting
 */

'use strict';

// ── Validation Rules ─────────────────────────────────────────────────────────
// Each key matches a form field id (without the page-specific prefix).
const RULES = {
    title: {
        required: true,
        minLen:   5,
        maxLen:   255,
        // Allow letters, numbers, spaces and common punctuation
        pattern:  /^[\w\s.,!?'"():;@#\-&]+$/,
        patternMsg: 'Title contains unsupported characters.'
    },
    body: {
        required: true,
        minLen:   10,
        maxLen:   5000
    },
    username: {
        required:   true,
        minLen:     3,
        maxLen:     50,
        // Only letters, digits, underscores, hyphens
        pattern:    /^[a-zA-Z0-9_\-]+$/,
        patternMsg: 'Username may only contain letters, numbers, _ and -.'
    },
    email: {
        required:   true,
        // Standard email regex
        pattern:    /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
        patternMsg: 'Please enter a valid email address.'
    },
    reply: {
        required: true,
        minLen:   5,
        maxLen:   2000
    }
};

// ── Core Validation Function ──────────────────────────────────────────────────
/**
 * Validates a single value against the rules for `ruleKey`.
 * @param {string} ruleKey  - key in RULES (e.g. 'title')
 * @param {string} value    - current field value
 * @returns {string|null}   - error message, or null if valid
 */
function validateValue(ruleKey, value) {
    const rule = RULES[ruleKey];
    if (!rule) return null;

    const trimmed = (value || '').trim();

    if (rule.required && trimmed.length === 0) {
        return 'This field is required.';
    }
    if (trimmed.length === 0) return null;  // optional field is empty — OK

    if (rule.minLen && trimmed.length < rule.minLen) {
        return `Minimum ${rule.minLen} characters required (currently ${trimmed.length}).`;
    }
    if (rule.maxLen && trimmed.length > rule.maxLen) {
        return `Maximum ${rule.maxLen} characters allowed.`;
    }
    if (rule.pattern && !rule.pattern.test(trimmed)) {
        return rule.patternMsg || 'Invalid format.';
    }
    return null;  // all checks passed
}

// ── DOM Helpers ───────────────────────────────────────────────────────────────
/**
 * Displays (or clears) an error message beneath a field.
 * @param {HTMLElement} input   - the input/textarea element
 * @param {string|null} message - error string, or null to clear
 */
function setFieldError(input, message) {
    if (!input) return;

    // Find sibling error span — convention: <span class="error-msg" data-for="fieldId">
    const errEl = input.parentElement.querySelector('.error-msg');

    input.classList.toggle('invalid', !!message);
    if (errEl) errEl.textContent = message || '';
}

/**
 * Validates all fields in a form and shows any errors.
 * @param {HTMLFormElement} form
 * @param {Object} fieldMap  - { ruleKey: inputElement }
 * @returns {boolean} true if all fields are valid
 */
function validateForm(form, fieldMap) {
    let allValid = true;

    for (const [ruleKey, input] of Object.entries(fieldMap)) {
        const error = validateValue(ruleKey, input.value);
        setFieldError(input, error);
        if (error) allValid = false;
    }

    return allValid;
}

// ── Wire Up a Form ────────────────────────────────────────────────────────────
/**
 * Attaches real-time and on-submit validation to a form.
 *
 * @param {string}   formId    - id of the <form> element
 * @param {Object}   fieldMap  - { ruleKey: 'inputElementId' }
 * @param {Function} onValid   - callback(formData) when the form is valid on submit
 *
 * Usage:
 *   attachValidation('thread-form', { title: 'thread-title', body: 'thread-body' }, handleSubmit);
 */
function attachValidation(formId, fieldMap, onValid) {
    const form = document.getElementById(formId);
    if (!form) return;

    // Resolve element ids to actual DOM elements
    const resolved = {};
    for (const [ruleKey, elId] of Object.entries(fieldMap)) {
        const el = document.getElementById(elId);
        if (el) resolved[ruleKey] = el;
    }

    // Real-time: validate on blur (when field loses focus)
    for (const [ruleKey, input] of Object.entries(resolved)) {
        input.addEventListener('blur', () => {
            setFieldError(input, validateValue(ruleKey, input.value));
        });

        // Re-validate on input only if the field is already showing an error
        input.addEventListener('input', () => {
            if (input.classList.contains('invalid')) {
                setFieldError(input, validateValue(ruleKey, input.value));
            }
        });
    }

    // On submit: validate all fields, then call onValid if clean
    form.addEventListener('submit', e => {
        e.preventDefault();

        if (validateForm(form, resolved)) {
            // Collect values into a plain object for the caller
            const data = {};
            for (const [ruleKey, input] of Object.entries(resolved)) {
                data[ruleKey] = input.value.trim();
            }
            onValid(data, form);
        }
    });
}

// ── Standalone Helpers (used by dom.js) ───────────────────────────────────────
/**
 * Checks if a string matches a simple email pattern.
 * @returns {boolean}
 */
function isValidEmail(str) {
    return RULES.email.pattern.test((str || '').trim());
}

/**
 * Strips all HTML tags from a string (basic XSS prevention on display).
 * Note: the server should also sanitise inputs.
 * @returns {string}
 */
function escapeHtml(str) {
    const div = document.createElement('div');
    div.appendChild(document.createTextNode(str || ''));
    return div.innerHTML;
}

// ── Character Counter ─────────────────────────────────────────────────────────
/**
 * Attaches a live character counter to a textarea.
 * @param {string} textareaId
 * @param {number} maxLen
 * @param {string} counterId  - id of the element to display count in
 */
function attachCharCounter(textareaId, maxLen, counterId) {
    const ta      = document.getElementById(textareaId);
    const counter = document.getElementById(counterId);
    if (!ta || !counter) return;

    function update() {
        const remaining = maxLen - ta.value.length;
        counter.textContent = `${ta.value.length} / ${maxLen}`;
        counter.style.color = remaining < 50 ? 'var(--clr-error)' : 'var(--clr-muted)';
    }

    ta.addEventListener('input', update);
    update();  // initialise on load
}

// Export to global scope (no bundler in this project)
window.ForumValidation = {
    validateValue,
    setFieldError,
    validateForm,
    attachValidation,
    isValidEmail,
    escapeHtml,
    attachCharCounter
};
