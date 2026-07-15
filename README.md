
A citizen grievance management platform where citizens report civic issues and government departments resolve them efficiently.

Built for **Smart India Hackathon (SIH25031)** and placement portfolio.

## Features

### Citizen
- Register & login
- Submit complaints (6 categories: Road Damage, Garbage, Water Supply, Street Light, Drainage, Other)
- Track complaint status in real-time
- Verify resolved issues or reopen if unsatisfied

### Admin Officer
- Dashboard with complaint statistics
- View, filter, and manage all complaints
- Assign complaints to departments and officers

### Department Officer
- View assigned complaints
- Update status (In Progress / Resolved)
- Add remarks and proof images

## Complaint Lifecycle

```
Citizen Submits → Admin Reviews → Admin Assigns to Dept
    ↓                                      ↓
Citizen Tracks ← Status Updates ← Dept Works on Issue
    ↓
Citizen Verifies → Verified ✓  OR  Reopened → Back to Dept
```

## Tech Stack

| Layer      | Technology               |
|------------|--------------------------|
| Frontend   | HTML, CSS, JavaScript    |
| Backend    | Java 21, Spring Boot 3.2 |
| Database   | MySQL 8                  |
| Security   | Spring Security + JWT    |
| Build      | Maven                    |

## Project Structure

```
civic-connect/
├── pom.xml
├── database-setup.sql
├── src/main/
│   ├── java/com/civicconnect/
│   │   ├── CivicConnectApplication.java
│   │   ├── config/          # JWT + Security config
│   │   ├── controller/      # REST controllers (Auth, Complaint, Admin, Dept)
│   │   ├── dto/             # Request/Response objects
│   │   ├── entity/          # JPA entities (User, Complaint, Assignment, Update)
│   │   ├── repository/      # Spring Data JPA repos
│   │   └── service/         # Business logic
│   └── resources/
│       ├── application.properties
│       └── static/
│           ├── index.html              # Landing page
│           ├── login.html              # Login
│           ├── register.html           # Registration
│           ├── citizen-dashboard.html  # Citizen home
│           ├── citizen-submit.html     # Submit complaint
│           ├── citizen-track.html      # Track complaints
│           ├── complaint-detail.html   # Complaint detail + verify/reopen
│           ├── admin-dashboard.html    # Admin stats
│           ├── admin-complaints.html   # Admin manage + assign
│           ├── dept-dashboard.html     # Dept view + update
│           ├── css/style.css           # Custom stylesheet
│           └── js/app.js              # API client + auth
└── .gitignore
```

## Setup & Run

### Prerequisites
- Java 21 (JDK)
- MySQL 8
- Maven 3.9+

### Steps

1. **Clone the repo**
   ```bash
   git clone https://github.com/YOUR_USERNAME/civic-connect.git
   cd civic-connect
   ```

2. **Create MySQL database**
   ```bash
   mysql -u root -p
   ```
   ```sql
   CREATE DATABASE civic_connect_db;
   ```

3. **Update database password** in `src/main/resources/application.properties`:
   ```properties
   spring.datasource.password=your_mysql_password
   ```

4. **Run the application**
   ```bash
   mvn spring-boot:run
   ```
   Or on Windows if `mvn` isn't in PATH:
   ```bash
   .\mvnw spring-boot:run
   ```

5. **Open in browser**
   ```
   http://localhost:8080
   ```

### Quick Test Flow

1. Register as **Citizen** → Submit a complaint
2. Register as **Admin** → View complaint → Assign to department
3. Register as **Department** officer → Update status to Resolved
4. Login as **Citizen** → Verify or Reopen the complaint

## API Endpoints

### Auth
| Method | Endpoint            | Access |
|--------|---------------------|--------|
| POST   | /api/auth/register  | Public |
| POST   | /api/auth/login     | Public |

### Citizen
| Method | Endpoint                       | Access  |
|--------|--------------------------------|---------|
| POST   | /api/complaints/submit         | CITIZEN |
| GET    | /api/complaints/my             | CITIZEN |
| GET    | /api/complaints/{id}           | Auth    |
| PUT    | /api/complaints/{id}/verify    | CITIZEN |
| PUT    | /api/complaints/{id}/reopen    | CITIZEN |

### Admin
| Method | Endpoint                     | Access |
|--------|------------------------------|--------|
| GET    | /api/admin/complaints        | ADMIN  |
| GET    | /api/admin/complaints/filter | ADMIN  |
| GET    | /api/admin/stats             | ADMIN  |
| POST   | /api/admin/assign            | ADMIN  |
| GET    | /api/admin/officers          | ADMIN  |

### Department
| Method | Endpoint                              | Access     |
|--------|---------------------------------------|------------|
| GET    | /api/department/complaints            | DEPARTMENT |
| GET    | /api/department/complaints/{id}       | DEPARTMENT |
| PUT    | /api/department/complaints/{id}/update| DEPARTMENT |

## Status Codes

| Status      | Description                             |
|-------------|-----------------------------------------|
| SUBMITTED   | Citizen filed the complaint             |
| ASSIGNED    | Admin assigned to a department          |
| IN_PROGRESS | Department is working on it             |
| RESOLVED    | Department marked it as done            |
| VERIFIED    | Citizen confirmed the issue is fixed    |
| REOPENED    | Citizen was not satisfied, sent back    |

## Phase 2 Roadmap

- [ ] Google Gemini AI for auto-categorization
- [ ] File upload for images (instead of URL)
- [ ] Google Maps location picker
- [ ] Email/SMS notifications
- [ ] Analytics dashboard with charts
- [ ] Multi-language support (Tamil/Hindi/English)

## Author

**Meiyarasan** — B.E. Computer Science & Engineering, V.S.B. Engineering College, Karur

## License

This project is built for educational and hackathon purposes.
