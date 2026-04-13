# Community Discussion Forum — Full Stack Capstone

A lightweight discussion forum demonstrating HTML5, CSS3, JavaScript,
Java Servlets/JSP, JDBC, and JSON across a full MVC stack.

---

## Tech Stack

| Layer      | Technology                          |
|------------|-------------------------------------|
| Frontend   | HTML5, CSS3, Vanilla JavaScript     |
| Backend    | Java 17, Jakarta Servlets, JSP      |
| Database   | MySQL 8                             |
| Server     | Apache Tomcat 10                    |
| JSON       | Gson 2.10                           |
| Build tool | Maven 3.8+                          |

---

## Project Structure

```
forum-app/
├── pom.xml                          Maven build file
├── schema.sql                       DB schema + seed data
└── src/main/
    ├── java/com/forum/
    │   ├── model/
    │   │   ├── Category.java
    │   │   ├── User.java
    │   │   ├── Thread.java
    │   │   └── Post.java
    │   ├── dao/
    │   │   ├── CategoryDAO.java
    │   │   ├── ThreadDAO.java
    │   │   └── PostDAO.java
    │   ├── servlet/
    │   │   ├── CategoryServlet.java
    │   │   ├── ThreadServlet.java
    │   │   └── ReplyServlet.java
    │   └── util/
    │       ├── DBConnection.java
    │       └── JsonUtil.java
    └── webapp/
        ├── WEB-INF/web.xml
        ├── index.html               Main page (thread list)
        ├── thread.html              Thread detail + replies
        ├── thread-list.jsp          JSP alternative (server-rendered)
        ├── thread-detail.jsp        JSP thread detail
        ├── css/style.css
        └── js/
            ├── validation.js        Client-side validation
            ├── api.js               JSON API calls
            └── dom.js               DOM rendering utilities
```

---

## Setup & Run

### 1. Prerequisites
- Java JDK 17+
- Apache Tomcat 10.x → [tomcat.apache.org](https://tomcat.apache.org)
- MySQL 8.x
- Maven 3.8+

### 2. Create the Database

```bash
mysql -u root -p < schema.sql
```

Then create a dedicated DB user:

```sql
CREATE USER 'forum_user'@'localhost' IDENTIFIED BY 'secret';
GRANT ALL PRIVILEGES ON forum_db.* TO 'forum_user'@'localhost';
FLUSH PRIVILEGES;
```

### 3. Configure DB Credentials

Edit `src/main/java/com/forum/util/DBConnection.java`:

```java
private static final String URL  = "jdbc:mysql://localhost:3306/forum_db?useSSL=false&serverTimezone=UTC";
private static final String USER = "forum_user";   // ← your DB user
private static final String PASS = "secret";       // ← your DB password
```

### 4. Build the WAR

```bash
mvn clean package
```

This produces `target/forum-app.war`.

### 5. Deploy to Tomcat

```bash
cp target/forum-app.war $CATALINA_HOME/webapps/
$CATALINA_HOME/bin/startup.sh       # Linux/Mac
# $CATALINA_HOME\bin\startup.bat   # Windows
```

### 6. Open in Browser

```
http://localhost:8080/forum-app/index.html
```

Test the API directly:

```bash
# List all threads
curl http://localhost:8080/forum-app/threads

# List categories (JSON)
curl http://localhost:8080/forum-app/categories

# List categories (XML — demonstrates Module 3)
curl -H "Accept: application/xml" http://localhost:8080/forum-app/categories

# Create a thread
curl -X POST http://localhost:8080/forum-app/threads \
     -H "Content-Type: application/json" \
     -d '{"title":"Hello World","body":"My first thread!","categoryId":1,"userId":1}'

# Post a reply
curl -X POST http://localhost:8080/forum-app/replies \
     -H "Content-Type: application/json" \
     -d '{"threadId":1,"userId":2,"body":"Great post!"}'
```

---

## API Endpoints

| Method | URL                              | Description                        |
|--------|----------------------------------|------------------------------------|
| GET    | `/categories`                    | List all categories (JSON or XML)  |
| GET    | `/threads`                       | List all threads                   |
| GET    | `/threads?categoryId=N`          | Filter threads by category         |
| GET    | `/threads?id=N`                  | Single thread + replies            |
| POST   | `/threads`                       | Create a new thread                |
| GET    | `/replies?threadId=N`            | All replies for a thread           |
| POST   | `/replies`                       | Post a new reply                   |

---

## Module Coverage Checklist

| Course Module          | Demonstrated by                                            |
|------------------------|------------------------------------------------------------|
| 1 · Web App Design     | MVC architecture, HTML5 semantics, CSS3 responsive layout  |
| 2 · Client-Side JS     | `validation.js`, `dom.js` — events, DOM, regex validation  |
| 3 · XML & JSON         | `api.js` JSON fetch; `CategoryServlet` XML alternative     |
| 4 · Server-Side Java   | Servlets, JSP, JDBC `PreparedStatement`, ResultSet mapping |
| 5 · Frameworks         | Swap frontend layer with AngularJS `$http` or React hooks  |

---

## Adapting to AngularJS (Module 5)

Replace `api.js` + inline scripts with an Angular module:

```javascript
// app.js
angular.module('forumApp', [])
  .controller('ThreadCtrl', function($scope, $http) {
      $http.get('/forum-app/threads')
           .then(res => { $scope.threads = res.data.threads; });
  });
```

```html
<!-- index.html with AngularJS -->
<div ng-app="forumApp" ng-controller="ThreadCtrl">
  <div class="thread-card" ng-repeat="t in threads">
    <h3>{{ t.title }}</h3>
    <p class="thread-meta">by {{ t.author }}</p>
  </div>
</div>
```

## Adapting to React (Module 5)

```jsx
// ThreadList.jsx
import { useState, useEffect } from 'react';

export default function ThreadList() {
  const [threads, setThreads] = useState([]);

  useEffect(() => {
    fetch('/forum-app/threads')
      .then(r => r.json())
      .then(data => setThreads(data.threads));
  }, []);

  return (
    <div>
      {threads.map(t => (
        <div key={t.id} className="thread-card">
          <h3>{t.title}</h3>
          <p>by {t.author}</p>
        </div>
      ))}
    </div>
  );
}
```

The Servlet API layer requires **zero changes** when switching frontend frameworks.
