# 🏙️ SMART MUNICIPALITY – AI-POWERED CIVIC ISSUE MANAGEMENT AND PRIORITIZATION SYSTEM

> **College-Level Round 2 Software Project**  
> Complete, functional, demo-ready civic tech platform for automated municipal triage, geospatial hotspot detection, duplicate complaint clustering, and end-to-end citizen status tracking.

---

## 📌 1. Project Overview

Citizens report civic problems such as **broken streetlights, potholes, overflowing garbage, water leakage, and damaged infrastructure**.  
The platform applies an AI multimodal analysis engine to examine the uploaded photo and description, classifies the category, estimates severity, recommends the responsible municipal department, calculates confidence, clusters duplicate complaints, detects civic infrastructure hotspots, scores urgency, and empowers citizens to track repairs until final resolution.

---

## 🛠️ 2. Technology Stack

- **Frontend**: Responsive HTML5, Modern CSS3 (Civic Design System), Vanilla JavaScript (No React dependency, clean and modular).
- **Backend**: Java 17, Spring Boot 3.2.4, REST APIs, Spring Data JPA / Hibernate.
- **Database**:
  - **Out-of-the-Box Mode (Default)**: Embedded persistent database in MySQL compatibility mode (zero setup, instant college demo).
  - **Production MySQL Mode**: Seamless toggle via Spring profile (`application-mysql.properties`).
- **AI Engine**:
  - **Multimodal AI**: Multimodal Google Gemini API client (`GeminiAiClient`) for vision + text analysis.
  - **Civic Rule Engine Fallback (`MockAiService`)**: Intelligent domain rule engine analyzing visual tokens, category keywords, and urgency triggers when an external API key is not present. UI clearly displays engine source as required.
- **Security**: BCrypt password hashing for both citizens and officers, role-based access guards.

---

## 👥 3. User Roles & Seed Demo Credentials

| Role | Name | Email | Password | Scope |
|---|---|---|---|---|
| **Citizen** | Rajesh Kumar | `citizen@municipality.gov` | `citizen123` | Submit issues, photo evidence, track status timeline |
| **Citizen** | Ananya Verma | `ananya@gmail.com` | `citizen123` | Submit issues, photo evidence, track status timeline |
| **Municipal Officer** | Inspector Priya Sharma | `officer@municipality.gov` | `officer123` | Triage table, assign department, change status, resolve, monitor hotspots |

*(Quick one-click demo login buttons are provided on both the Home and Login pages).*

---

## 🚀 4. How to Run the Application

### Prerequisites:
- Java 17 (`java -version`)
- Maven 3.9+ (`mvn -version`)

### Quick Start (Zero-Setup Default):
1. Navigate to the project root directory:
   ```bash
   cd "C:\Users\kamur\.gemini\antigravity\scratch\smart-municipality"
   ```
2. Run the Spring Boot application:
   ```bash
   mvn spring-boot:run
   ```
3. Open your browser and go to:
   ```
   http://localhost:8080
   ```

### Switch to Local MySQL Server:
1. Ensure your local MySQL server is running.
2. In `src/main/resources/application.properties`, uncomment:
   ```properties
   spring.profiles.active=mysql
   ```
   *(Or run: `mvn spring-boot:run -Dspring-boot.run.profiles=mysql`)*
3. Check credentials in `src/main/resources/application-mysql.properties`.

---

## 🎬 5. College Round 2 Live Demonstration Script

Follow this exact walkthrough to demonstrate all required capabilities:

### Step 1: Citizen Login & Dashboard
1. Open `http://localhost:8080` in your web browser.
2. Click **"Launch Citizen Demo (Rajesh Kumar)"** (or login via `citizen@municipality.gov` / `citizen123`).
3. View the **Citizen Dashboard**: notice stats for total complaints, pending, in-progress, and resolved issues.

### Step 2: Lodge Complaint & AI Analysis
1. Click **"➕ Report New Civic Issue"**.
2. Click the top button: **"⚡ Quick Fill Demo Scenario (Broken Streetlight)"**.
   - Photo: Loads `broken_streetlight.jpg`
   - Description: *"Streetlight is not working at night."*
   - Location: *"XYZ Road"* (Coordinates: `12.9716, 77.5946`)
3. Click **"🚀 Submit Complaint & Run AI Analysis"**.
4. Observe the instant output:
   - **AI Analysis Box**:
     - Category: `Streetlight`
     - Severity: `HIGH`
     - Recommended Department: `Electrical Department`
     - Confidence: `95%`
     - Engine: Displays `Civic Rule Engine Fallback` (or `Gemini AI` if key provided)
   - **⚠️ Possible Related Complaints Detected**:
     - Identifies open duplicate reports at XYZ Road (`#1`, `#2`, `#3`, `#4`) with similarity scores (up to 89%).
   - **🔥 CIVIC HOTSPOT DETECTED**:
     - Aggregates recurring complaints at XYZ Road.
     - Severity: `HIGH`, Priority: `URGENT`, Responsible Department: `Electrical Department`.
5. Click **"🔍 Track This Complaint Timeline"** to view its live status: `SUBMITTED`.

### Step 3: Municipal Officer Triage & Dispatch
1. In the top bar or navigation, click **Logout**, then log in as **Municipal Officer** (`officer@municipality.gov` / `officer123`).
2. On the **Officer Dashboard**, review:
   - 7 Real-time statistics: Total Complaints, Pending, Assigned, In Progress, Resolved, High Priority, and Civic Hotspots.
   - Active Civic Hotspot banner for XYZ Road.
3. In the complaints registry table, find your newly submitted complaint (`#9`). Notice it is flagged with the `CRITICAL` priority badge and `🔥 HOTSPOT`.
4. Click **"⚙️ Manage"** to open the Officer Action Modal:
   - **Assign Department**: Select *Electrical Department*, add dispatch note *"Dispatched to Zone 3 rapid response unit"*, and click **Assign**.
     *(Status updates to `ASSIGNED`)*.
   - Click **"⏱️ Update Status"**: Change to `IN_PROGRESS (Work Underway)`, add comment *"Lineman deployed to XYZ Road"*, and click **Save**.
     *(Status updates to `IN_PROGRESS`)*.
   - Click **"✅ Mark Resolved"**: Enter Resolution Note *"Streetlight repaired."*, confirm Resolved By *"Electrical Department"*, and click **Mark Resolved**.
     *(Status updates to `RESOLVED`)*.

### Step 4: Citizen Resolution Verification
1. Log back in as **Citizen** (`citizen@municipality.gov`).
2. Open the complaint details (`complaint-details.html?id=9`).
3. View the completed 4-stage tracking timeline:
   - `✓ Submitted`
   - `✓ Assigned to Electrical Department`
   - `✓ Repair in Progress`
   - `✓ Resolved`
4. Notice the green **Official Resolution Banner**:
   - Resolution: *"Streetlight repaired."*
   - Resolved By: *Electrical Department*
   - Resolved Date: *Current Timestamp*

---

## 📡 6. REST API Endpoints Reference

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/auth/register` | Register new citizen |
| `POST` | `/api/auth/login` | Authenticate citizen or officer |
| `POST` | `/api/complaints` | Submit new complaint with photo & run AI triage |
| `GET` | `/api/complaints` | Query complaints with filters (category, severity, status, search) |
| `GET` | `/api/complaints/{id}` | Retrieve complaint details, timeline, and duplicates |
| `GET` | `/api/complaints/user/{userId}` | Retrieve complaints submitted by specific citizen |
| `GET` | `/api/complaints/{id}/analysis` | Retrieve AI analysis diagnosis |
| `GET` | `/api/complaints/{id}/related` | Retrieve duplicate/related complaint clusters |
| `PUT` | `/api/complaints/{id}/assign` | Assign complaint to municipal department |
| `PUT` | `/api/complaints/{id}/status` | Transition complaint workflow status |
| `POST` | `/api/complaints/{id}/resolve` | Record resolution note & mark complaint resolved |
| `GET` | `/api/hotspots` | Fetch all active civic infrastructure hotspots |
| `GET` | `/api/departments` | Fetch all 8 municipal departments |
| `GET` | `/api/dashboard/statistics` | Retrieve 7 municipal dashboard metrics |

---

## 🗄️ 7. Database Entity Schema

1. `users`: User profiles, role (`CITIZEN`, `MUNICIPAL_OFFICER`), hashed password.
2. `departments`: 8 departments (Electrical, Roads, Sanitation, Water, Drainage, Traffic, PWD, General).
3. `complaints`: Core grievance table with coordinates, category, severity, priority, status, hotspot flag.
4. `complaint_analysis`: AI diagnostic result, confidence score, source engine tag, reasoning.
5. `related_complaints`: Similarity links between complaint pairs with match percentage and criteria.
6. `hotspots`: Clustered geographic failure zones with report counts and escalated priority.
7. `status_history`: Complete chronological audit trail for the 4-step timeline.
8. `resolutions`: Final resolution statement, resolving agency, and completion timestamp.

---

## 🏆 Round 2 Evaluation Highlights
- **100% Functional**: All APIs, database persistence, image handling, and workflows operate end-to-end.
- **Intelligent Algorithmic Priority**: Computes urgency dynamically based on severity + duplicate count + hotspot state.
- **Zero-Friction Evaluation**: Complete pre-seeded realistic complaints and one-click demo fills for instant presentation.
