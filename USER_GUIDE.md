# 📖 AI Document Locker: Complete User & Demo Guide

Welcome to the **AI-Powered Digital Document Locker**! This comprehensive manual provides step-by-step instructions to test every feature on the website, explains how files and data are stored under the hood, and provides ready-to-use demo certificates and files.

---

## 📑 Table of Contents
1. [Quick Launch & Verification](#1-quick-launch--verification)
2. [Step-by-Step Website Walkthrough](#2-step-by-step-website-walkthrough)
   - [Step 1: Sign In & Authentication](#step-1-sign-in--authentication)
   - [Step 2: Uploading Certificates & Documents](#step-2-uploading-certificates--documents)
   - [Step 3: Inspecting AI Insights & Extracted Data](#step-3-inspecting-ai-insights--extracted-data)
   - [Step 4: Natural Language AI Search](#step-4-natural-language-ai-search)
   - [Step 5: Category Filtering & Storage Metrics](#step-5-category-filtering--storage-metrics)
   - [Step 6: Creating Secure Expiring Share Links](#step-6-creating-secure-expiring-share-links)
   - [Step 7: Testing Public Recipient Access](#step-7-testing-public-recipient-access)
   - [Step 8: File Download & Deletion](#step-8-file-download--deletion)
3. [How Files and Metadata are Stored (Architecture)](#3-how-files-and-metadata-are-stored)
4. [Demo Test Files Catalog (`demo-documents/`)](#4-demo-test-files-catalog)
5. [Connecting Cloud Services (Aiven & Gemini AI)](#5-connecting-cloud-services)
6. [Troubleshooting & Mentor Presentation Tips](#6-troubleshooting--mentor-presentation-tips)

---

## 1. Quick Launch & Verification

To run the application manually from your terminal:

### Terminal 1: Spring Boot Backend (Port 8080)
```powershell
cd d:\au\Academic\TT\ai-document-locker\backend
mvn spring-boot:run
```
- Status Page: [http://localhost:8080](http://localhost:8080)
- Health Endpoint: [http://localhost:8080/api/health](http://localhost:8080/api/health)
- H2 Console: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)

### Terminal 2: React Frontend (Port 5173)
```powershell
cd d:\au\Academic\TT\ai-document-locker\frontend
npm run dev
```
- Web Application UI: [http://localhost:5173](http://localhost:5173)

---

## 2. Step-by-Step Website Walkthrough

### Step 1: Sign In & Authentication
1. Open your browser and navigate to **[http://localhost:5173](http://localhost:5173)**.
2. You will see the secure authentication screen.
3. Click the yellow button: **"✨ Fill Demo Credentials (Alice)"**.
   - *Email:* `alice@example.com`
   - *Password:* `password123`
4. Click **Sign In to Locker** (or switch to the "Create Account" tab to register a new user).
5. You are redirected to your private, encrypted document dashboard.

---

### Step 2: Uploading Certificates & Documents
1. In the top right corner of the dashboard, click **"Upload Document"**.
2. A modal will pop up with a drag-and-drop zone.
3. Open your file explorer to `d:\au\Academic\TT\ai-document-locker\demo-documents` and drag one of the demo certificates into the box:
   - Example: [`AWS_Certified_Solutions_Architect.pdf`](file:///d:/au/Academic/TT/ai-document-locker/demo-documents/AWS_Certified_Solutions_Architect.pdf)
4. Notice how the title is automatically formatted from the file name.
5. In the **Category** dropdown, you can leave it on **"✨ Auto-Detect via AI"** (or pick a manual category like `Academic Transcript / Diploma` or `Other`).
6. Optionally enter tags (e.g. `aws, cloud, certification, 2026`).
7. Click **"Upload & Analyze"**.
8. The file is uploaded, stored, extracted with Apache PDFBox, and summarized. A new card will appear instantly in your dashboard!

---

### Step 3: Inspecting AI Insights & Extracted Data
1. On the document card in your dashboard, click **"Insights"** (or click the document title).
2. The **Document Intelligence Drawer** will open showing 3 tabs:
   - **AI Insights & Entities Tab**:
     - **AI Executive Summary**: A concise summary of the document.
     - **Dates Found**: Automatically detected issue dates and expiry dates (e.g., `October 11, 2026`).
     - **Monetary Figures**: Currency values found if the file is an invoice.
     - **Identified Parties**: Certifying organizations (e.g., `Amazon Web Services`, `Google Cloud`).
     - **"Refresh AI" Button**: Allows re-analyzing the file on demand.
   - **Extracted Text Tab**:
     - View the complete raw text parsed directly out of the PDF.
   - **File Metadata Tab**:
     - View original filename, MIME type (`application/pdf`), and exact upload timestamp.

---

### Step 4: Natural Language AI Search
1. In the search bar at the top of the dashboard, click the **"AI Mode"** toggle button (it will glow yellow with a sparkle icon).
2. Type a natural-language question in plain English, such as:
   - *"What score was received on the AWS exam?"*
   - *"When does the Google Cloud certificate expire?"*
   - *"What is the total due on the Acme invoice?"*
3. Press **Enter** or click search.
4. The AI Document Assistant will analyze your documents and synthesize a direct generative answer answering your question and citing the relevant file!

*(Note: In Standard Search mode, typing keywords will instantly filter your documents by title, tags, or content in real time).*

---

### Step 5: Category Filtering & Storage Metrics
1. Look at the top metrics cards:
   - **Total Documents**: Total private files in your locker.
   - **AI Analyzed**: Documents processed by the AI engine.
   - **Storage Consumed**: Exact disk space used (e.g. `42.5 KB`).
   - **Active Shares**: Number of live shared links.
2. Click the category pills below the search bar:
   - Click **Academic** &rarr; displays only degrees and certifications.
   - Click **Invoices** &rarr; displays only billing documents.
   - Click **Resumes** &rarr; displays only CVs.
   - Click **All Documents** &rarr; resets the filter.

---

### Step 6: Creating Secure Expiring Share Links
1. Find any document card and click the **Share icon** (`Share2`).
2. The **Share Securely** modal opens.
3. Configure sharing permissions:
   - **Recipient Email (Optional)**: E.g., `mentor@university.edu` (leave empty for public token).
   - **Permission**: Choose **"👀 View Only"** or **"📥 Allow Download"**.
   - **Expiration**: Select **1 Day**, **7 Days**, **30 Days**, or **Never Expires**.
4. Click **"Create Secure Share Link"**.
5. An encrypted token is generated.
6. Click the **Copy Link** icon next to the token to copy the share URL to your clipboard.

---

### Step 7: Testing Public Recipient Access
1. Open an **Incognito / Private browser window** (or a different browser where you are NOT logged in).
2. Paste the copied link (e.g. `http://localhost:5173/?share=TOKEN_HERE`) and press **Enter**.
3. Notice that:
   - **No login is required** for the recipient.
   - The recipient sees the document title, category badge, and AI executive summary.
   - If you chose **View Only**, the download button is locked.
   - If you chose **Allow Download**, the recipient can click **"Download File"** to receive the original PDF.
   - Once a link expires or is revoked in your dashboard, access is immediately blocked with an *Access Restricted* warning.

---

### Step 8: File Download & Deletion
- **Download**: Click the download icon on any document card to stream and save the original file to your computer.
- **Delete**: Click the trash icon. A confirmation prompt will appear. Deleting a document:
  1. Deletes the physical file from the server's storage disk.
  2. Revokes all active share tokens associated with the file.
  3. Deletes the database row and updates your storage metrics in real time.

---

## 3. How Files and Metadata are Stored

### Storage Architecture Diagram

```
User uploads "AWS_Certified_Solutions_Architect.pdf"
                       │
                       ▼
             [ Spring Boot Backend ]
                       │
        ┌──────────────┴──────────────┐
        ▼                             ▼
┌──────────────────────────┐   ┌───────────────────────────┐
│   PHYSICAL DISK STORAGE   │   │     DATABASE METADATA     │
│ (Isolated File Locker)   │   │  (Aiven Cloud / H2 MySQL) │
├──────────────────────────┤   ├───────────────────────────┤
│ Path:                    │   │ Table: `documents`        │
│ backend/storage/         │   │ - id: 1                   │
│   documents/             │   │ - user_id: 1 (Owner)      │
│                          │   │ - title: "AWS Certificate"│
│ Stored File:             │   │ - file_name: "AWS...pdf"  │
│ u1_a83f9e...pdf          │   │ - file_type: "app/pdf"    │
│                          │   │ - file_size: 1388 bytes   │
│ * Sanitized UUID name    │   │ - category: "ACADEMIC"    │
│ * Not stored in SQL blob │   │ - summary: "AI Summary..."│
│ * Cannot be path-traversed│  │ - key_entities_json: "{}" │
└──────────────────────────┘   └───────────────────────────┘
```

### Why Files are NOT Stored in Database Columns:
1. **Performance & Scalability**: Storing binary blobs (`BLOB`) in relational databases bloats database backups, slows down indexing, and degrades MySQL memory cache performance.
2. **Streaming Efficiency**: Physical files are streamed directly via HTTP `Resource` streams, allowing browsers to render PDFs inline without holding the entire file in RAM.
3. **Security & Path Sanitization**:
   - Files are saved with a unique prefix format: `u{userId}_{UUID}.{ext}` (e.g. `u1_8d31a5f2-901c-4b53.pdf`).
   - Any malicious characters (such as `../`) are stripped to prevent directory traversal vulnerabilities.
4. **Tenant Privacy Enforcement**:
   - Every file request verifies that `document.owner.id == currentAuthenticatedUser.id`.
   - Even if User B guesses the document ID of User A, the backend aborts the request with `403 Forbidden`.

---

## 4. Demo Test Files Catalog

Inside the [`demo-documents/`](file:///d:/au/Academic/TT/ai-document-locker/demo-documents/) folder, you will find 5 realistic test documents available in both **PDF** and **TXT** formats:

| File Name | Format | Target Category | What it Tests |
| :--- | :---: | :---: | :--- |
| **`AWS_Certified_Solutions_Architect.pdf`** | PDF | `ACADEMIC` / `OTHER` | Certifications, Candidate ID (`AWS04829184`), Exam score (`890/1000`), Expiry (`Oct 11, 2029`). |
| **`Google_Cloud_Professional_Architect_Certificate.pdf`** | PDF | `ACADEMIC` / `OTHER` | Cloud credentials, Google Cloud Platform competencies, Credential URL. |
| **`University_Degree_Computer_Science.pdf`** | PDF | `ACADEMIC` | Graduation Convocation, B.Tech honors, CGPA (`9.24/10`), Dean signatures. |
| **`Acme_Enterprise_Cloud_Invoice.pdf`** | PDF | `INVOICE` | Subtotal (`$18,000`), Tax (`$1,800`), Total Due (`$19,800`), Due Date (`Nov 10, 2026`). |
| **`Alex_Morgan_Senior_Cloud_Architect_Resume.pdf`** | PDF | `RESUME` | Resume parsing, Experience history, Skills (Spring Boot, AWS, Docker). |

> **Tip for manual testing:** Try dragging each of these 5 files into the **Upload Document** modal and watch the AI classify them into different categories automatically!

---

## 5. Connecting Cloud Services

The application includes an embedded database and local AI fallback so it works right out of the box. To connect your cloud services:

### 1. Connecting Aiven Cloud MySQL
1. In your `.env` or environment variables, set:
   ```env
   SPRING_PROFILES_ACTIVE=aiven
   AIVEN_MYSQL_URL=jdbc:mysql://<your-aiven-host>:<port>/defaultdb?sslMode=REQUIRED
   AIVEN_MYSQL_USER=avnadmin
   AIVEN_MYSQL_PASSWORD=<your-aiven-password>
   ```
2. Restart the Spring Boot backend (`mvn spring-boot:run`). It will automatically connect over SSL and create all tables in Aiven Cloud!

### 2. Connecting Google Gemini AI
1. Obtain a free API key from [Google AI Studio](https://aistudio.google.com).
2. Add your key to `.env`:
   ```env
   GEMINI_API_KEY=AIzaSy...
   ```
3. Restart the backend. The top navbar badge will change to **"Gemini 2.0 AI Live"**, and document uploads will use live Gemini generation!

---

## 6. Troubleshooting & Mentor Presentation Tips

### Common Questions & Solutions:
- **"Port 8080 already in use"**:
  Check running tasks or stop any old Java process:
  ```powershell
  Get-Process java | Stop-Process -Force
  ```
- **"Browser shows blank on port 8080"**:
  Remember: **`http://localhost:5173`** is the interactive React application. Opening `http://localhost:8080` displays the Backend API Status Dashboard.
- **"How can my mentor test without creating an account?"**:
  1. Log into your dashboard at `http://localhost:5173`.
  2. Upload one of the demo certificates from `demo-documents/`.
  3. Click **Share** &rarr; Create a link &rarr; Copy the link.
  4. Send the link to your mentor: they can open it directly in any browser to inspect the certificate, read the AI summary, and download the file.
