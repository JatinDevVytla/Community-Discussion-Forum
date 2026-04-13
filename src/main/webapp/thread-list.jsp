<%-- 
    thread-list.jsp — Server-Side Rendered Thread List
    
    Demonstrates JSP as the View layer (MVC Module 1).
    This is an alternative to the JSON/fetch approach in index.html.
    The ThreadServlet would set the "threads" attribute before forwarding here.

    Module coverage: Module 1 (MVC View), Module 4 (JSP / Servlets)
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List, com.forum.model.Thread" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Forum — Thread List</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>

<header class="site-header">
    <h1>&#128172; Community Forum</h1>
    <nav>
        <a href="thread-list.jsp">Home</a>
        <a href="new-thread.jsp">New Thread</a>
    </nav>
</header>

<main class="container">
    <h2 style="margin-bottom:1rem">All Threads</h2>

    <%
        // The Servlet places the thread list in request scope before forwarding
        List<Thread> threads = (List<Thread>) request.getAttribute("threads");
    %>

    <% if (threads == null || threads.isEmpty()) { %>
        <p class="empty-msg">No threads yet. <a href="new-thread.jsp">Start one!</a></p>
    <% } else { %>
        <div id="thread-list">
        <% for (Thread t : threads) { %>
            <div class="thread-card">
                <h3>
                    <a href="thread-detail.jsp?id=<%= t.getId() %>">
                        <%= t.getTitle() %>
                    </a>
                </h3>
                <div class="thread-meta">
                    <span>by <strong><%= t.getAuthor() %></strong></span>
                    <span><%= t.getCategoryName() %></span>
                    <span><%= t.getReplyCount() %> replies</span>
                </div>
            </div>
        <% } %>
        </div>
    <% } %>
</main>

</body>
</html>
