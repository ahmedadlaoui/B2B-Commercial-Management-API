# Login API - Usage Guide

## 🔐 Authentication Endpoints

### Base URL
```
http://localhost:8080/api/auth
```

---

## 📍 Endpoints

### 1. **Login** - `POST /api/auth/login`

Login with username and password. Returns session ID that Postman will automatically remember.

**Request:**
```
POST http://localhost:8080/api/auth/login
Content-Type: application/x-www-form-urlencoded

username=admin
password=admin123
```

**Success Response (200 OK):**
```json
{
  "message": "Login successful",
  "sessionId": "A1B2C3D4E5F6G7H8I9J0",
  "user": {
    "id": 1,
    "username": "admin",
    "role": "ADMIN"
  }
}
```

**Error Responses:**

**400 Bad Request** - Missing fields:
```json
{
  "status": 400,
  "message": "Username is required",
  "path": "/api/auth/login"
}
```

**401 Unauthorized** - Invalid credentials:
```json
{
  "status": 401,
  "message": "Invalid username or password",
  "path": "/api/auth/login"
}
```

---

### 2. **Get Current User** - `GET /api/auth/me`

Get currently logged-in user information from session.

**Request:**
```
GET http://localhost:8080/api/auth/me
```

**Success Response (200 OK):**
```json
{
  "userId": 1,
  "username": "admin",
  "role": "ADMIN",
  "sessionId": "A1B2C3D4E5F6G7H8I9J0"
}
```

**Error Response (400 Bad Request):**
```json
{
  "status": 400,
  "message": "No active session found. Please login first",
  "path": "/api/auth/me"
}
```

---

### 3. **Logout** - `POST /api/auth/logout`

Logout and invalidate session.

**Request:**
```
POST http://localhost:8080/api/auth/logout
```

**Success Response (200 OK):**
```json
{
  "message": "Logout successful"
}
```

---

## 🧪 Postman Setup

### Configure Postman to Remember Session:

1. **Cookie Management (Automatic)**
   - Postman automatically saves cookies/sessions
   - No additional setup needed!

2. **Test the Flow:**

   **Step 1:** Login
   ```
   POST http://localhost:8080/api/auth/login
   Body (x-www-form-urlencoded):
     username: admin
     password: admin123
   ```

   **Step 2:** Verify Session (No need to pass anything!)
   ```
   GET http://localhost:8080/api/auth/me
   ```

   **Step 3:** Logout
   ```
   POST http://localhost:8080/api/auth/logout
   ```

   **Step 4:** Try accessing protected endpoint (should fail)
   ```
   GET http://localhost:8080/api/auth/me
   ```

---

## 🔑 Session Behavior

- **Session Cookie Name:** `JSESSIONID`
- **Session Storage:** In-memory (server-side)
- **Session Duration:** 30 minutes of inactivity (default)
- **Auto-Renewal:** Session extends on each request
- **Persistence:** Lost on server restart (in-memory)

---

## 🎯 Error Handling

All errors return consistent format:

```json
{
  "status": <HTTP_STATUS_CODE>,
  "message": "<DESCRIPTIVE_ERROR_MESSAGE>",
  "path": "<REQUEST_PATH>"
}
```

### HTTP Status Codes:
- `200` - Success
- `400` - Bad Request (validation, business logic)
- `401` - Unauthorized (invalid credentials)
- `403` - Forbidden (insufficient permissions)
- `404` - Not Found
- `409` - Conflict (duplicate resource)
- `500` - Internal Server Error

---

## 🗄️ Insert Admin User

Run this SQL to create an admin user:

1. **Start your Spring Boot application once** to generate the password hash
2. **Check console output** for the SQL query with hashed password
3. **Run the SQL** in your PostgreSQL database

Example:
```sql
INSERT INTO users (username, password, role) 
VALUES ('admin', 'GENERATED_HASH_FROM_CONSOLE', 'ADMIN');
```

---

## 💡 Best Practices

1. **Always check session before protected operations**
   - Use `/api/auth/me` to verify active session

2. **Handle 401 errors in frontend**
   - Redirect to login page
   - Clear local state

3. **Use HTTPS in production**
   - Secure cookie transmission
   - Prevent session hijacking

4. **Set proper CORS configuration**
   - Allow credentials
   - Whitelist frontend domains

---

## 🔄 Session Flow Diagram

```
Client                          Server
  |                               |
  |---POST /login---------------->|
  |   (username, password)        |
  |                               |
  |<--200 OK + Set-Cookie---------|
  |   JSESSIONID=abc123           |
  |                               |
  |---GET /me-------------------->|
  |   Cookie: JSESSIONID=abc123   |
  |                               |
  |<--200 OK + User Data----------|
  |                               |
  |---POST /logout--------------->|
  |   Cookie: JSESSIONID=abc123   |
  |                               |
  |<--200 OK---------------------|
  |   (Session invalidated)       |
```

---

## ✅ Testing Checklist

- [ ] Login with valid credentials
- [ ] Login with invalid username (401)
- [ ] Login with invalid password (401)
- [ ] Login with empty username (400)
- [ ] Login with empty password (400)
- [ ] Access /me after login (200)
- [ ] Access /me without login (400)
- [ ] Logout after login (200)
- [ ] Access /me after logout (400)
- [ ] Multiple requests with same session (should work)

---

**Note:** Postman automatically handles session cookies. You don't need to manually copy/paste the sessionId!
