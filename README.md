# README: Spring Security & JWT (with Refresh Tokens)

## 1. Overview

This document summarizes how to implement **Spring Boot** authentication using **Spring Security** and **JWT** (JSON Web Tokens), along with **Refresh Tokens** for token renewal. The approach is primarily **stateless**, meaning the server does not keep session data.

---

## 2. Key Components

- **Access Tokens**  
  Short-lived tokens (e.g., 15 minutes) used to authorize requests to protected endpoints.

- **Refresh Tokens**  
  Longer-lived tokens (e.g., 2 hours, 7 days) that enable a user to obtain a fresh Access Token after it expires without re-entering credentials.

- **User Entity & Roles**  
  A user entity (e.g., `AppUser`) represents registered users, each with roles/permissions that determine allowed actions.

- **Token Validation**  
  Access and Refresh tokens are validated on each request (or token refresh) by checking:
    - Signature correctness (using a secret key).
    - Expiration time (to ensure the token is not expired).
    - Optional additional claims, such as user ID, roles, etc.

---

## 3. Authentication Flow

1. **User Registration**
    - The user provides their credentials (e.g., email, password).
    - The password is securely hashed (e.g., using BCrypt) before storage in the database.
    - A role (e.g., `"USER"`) may be assigned by default.

2. **User Login**
    - The user provides valid credentials (email, password).
    - The application verifies these credentials against the database.
    - On success, the server generates:
        - A **short-lived Access Token** containing user details (subject, roles, etc.).
        - A **Refresh Token** with a longer lifespan.
    - Both tokens are returned to the client (e.g., in a JSON response).

3. **Accessing Protected Endpoints**
    - The client includes the Access Token in each request’s `Authorization` header:
      ```
      Authorization: Bearer <ACCESS_TOKEN>
      ```
    - The server validates the Access Token. If it is valid (not expired, signature checks out), the request proceeds.
    - If invalid or expired, the request fails with a `401 Unauthorized` (or similar).

4. **Token Expiration & Refresh**
    - When the Access Token expires (e.g., after 15 minutes), the client cannot make authenticated requests.
    - Instead of requiring a full re-login, the client uses the **Refresh Token** to obtain a new Access Token:
        - It sends the Refresh Token (e.g., in the body of a `POST /api/auth/refresh` request).
        - The server verifies the Refresh Token (signature, expiration, etc.).
        - If valid, the server issues a **new Access Token** (and possibly a new Refresh Token).
        - The client updates its stored tokens and continues making requests.

5. **Refresh Token Expiration**
    - If the Refresh Token is also expired (or invalid), the server cannot issue a new Access Token.
    - The client then must prompt the user to log in again with credentials.

---

## 4. Security Considerations

1. **Token Storage**
    - **Access Tokens** are typically stored in memory or sent in an HTTP-only cookie.
    - **Refresh Tokens** may be stored in a secure place (e.g., HTTP-only cookie).
    - Storing tokens in local storage or session storage can risk exposure if an attacker gains access to the browser context.

2. **Token Revocation**
    - By default, JWTs are stateless: once issued, they remain valid until expired.
    - If you need the ability to revoke tokens (e.g., on logout or suspected compromise), you can store them in a database (or maintain a blacklist) and check their status on each request.

3. **Transport Security**
    - Always use **HTTPS** to protect tokens in transit and prevent interception.

4. **Expiration Times**
    - The **Access Token** should have a short expiration (e.g., 15 minutes) to limit its usefulness if stolen.
    - The **Refresh Token** should have a longer expiration (e.g., 2 hours, 7 days) for user convenience but not indefinite, as that poses a security risk.

---

## 5. High-Level Steps

1. **Register** or **Log In** -> Server returns `{ accessToken, refreshToken }`.
2. **Call Protected Endpoints** with `Authorization: Bearer <ACCESS_TOKEN>`.
3. On `401 Unauthorized` (token expired) -> **Refresh** the token:
    - Send `POST /api/auth/refresh` with `{ refreshToken: "<REFRESH_TOKEN>" }`.
    - Server verifies the refresh token, issues a new `accessToken` (+ optionally a new `refreshToken`).
    - Client stores the new tokens and continues.
4. If the **Refresh Token** is also invalid/expired, the user must **re-authenticate** by logging in again.

---

## 6. Summary

By combining **short-lived Access Tokens** with **long-lived Refresh Tokens**, this approach strikes a balance between security and user convenience:

- **Short-lived tokens** minimize the damage window if a token is compromised.
- **Refresh Tokens** let users obtain new Access Tokens without constant re-logins.
- Additional measures (HTTPS, token revocation, secure token storage) further enhance security.

This README outlines the core concepts and high-level flow for implementing JWT-based authentication with Refresh Tokens in a Spring Boot application secured by Spring Security.
