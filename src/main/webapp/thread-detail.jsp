<%--
    thread-detail.jsp — Server-Side Rendered Thread Detail
    Displays a single thread and all its replies.

    Module coverage: Module 1 (MVC View), Module 4 (JSP)
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List, com.forum.model.Thread, com.forum.model.Post" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Thread — Community Forum</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>

<header class="site-header">
    <h1>&#128172; Community Forum</h1>
    <nav>
        <a href="thread-list.jsp">Home</a>
    </nav>
</header>

<main class="container" style="max-width:760px">

    <a href="thread-list.jsp" class="back-link">&#8592; Back to threads</a>

    <%
        Thread thread = (Thread) request.getAttribute("thread");
        List<Post> posts = (List<Post>) request.getAttribute("posts");
    %>

    <% if (thread == null) { %>
        <p class="empty-msg">Thread not found.</p>
    <% } else { %>

        <!-- Thread Detail -->
        <div class="thread-detail-header">
            <span class="badge"><%= thread.getCategoryName() %></span>
            <h1><%= thread.getTitle() %></h1>
            <div class="thread-meta" style="margin-top:.5rem">
                <span>by <strong><%= thread.getAuthor() %></strong></span>
                <span><%= thread.getReplyCount() %> replies</span>
            </div>
            <p class="thread-detail-body"><%= thread.getBody() %></p>
        </div>

        <!-- Replies -->
        <section class="section-gap">
            <h2 style="font-size:16px;font-weight:600;margin-bottom:1rem">
                Replies (<%= posts != null ? posts.size() : 0 %>)
            </h2>

            <% if (posts == null || posts.isEmpty()) { %>
                <p class="empty-msg">No replies yet.</p>
            <% } else {
                for (Post p : posts) { %>
                <div class="reply-card">
                    <div class="reply-author"><%= p.getAuthor() %></div>
                    <div class="reply-body"><%= p.getBody() %></div>
                    <div class="reply-time"><%= p.getCreatedAt() %></div>
                </div>
            <% } } %>
        </section>

        <!-- Reply Form (POST back to ReplyServlet) -->
        <div class="form-card section-gap">
            <h2>Leave a Reply</h2>
            <form method="POST" action="replies" novalidate>
                <input type="hidden" name="threadId" value="<%= thread.getId() %>">
                <input type="hidden" name="userId"   value="1">
                <div class="field">
                    <label for="reply-body">Your reply</label>
                    <textarea id="reply-body" name="body" rows="5"
                              placeholder="Share your thoughts…"></textarea>
                    <span class="error-msg" id="err-reply"></span>
                </div>
                <button type="submit" class="btn btn-primary">Post Reply</button>
            </form>
        </div>

    <% } %>

</main>

<script src="js/validation.js"></script>
<script>
// Simple inline validation for the JSP form (no api.js needed here)
document.querySelector('form').addEventListener('submit', function(e) {
    const body = document.getElementById('reply-body').value.trim();
    const err  = document.getElementById('err-reply');
    if (body.length < 5) {
        e.preventDefault();
        err.textContent = 'Reply must be at least 5 characters.';
        document.getElementById('reply-body').classList.add('invalid');
    }
});
</script>

</body>
</html>
