package com.documentlocker.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> home() {
        String html = """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>AI Document Locker - Backend API</title>
                <style>
                    body {
                        font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;
                        background: #f8fafc;
                        color: #1e293b;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        min-height: 100vh;
                        margin: 0;
                        padding: 20px;
                        box-sizing: border-box;
                    }
                    .card {
                        background: #ffffff;
                        border: 1px solid #e2e8f0;
                        border-radius: 16px;
                        max-width: 620px;
                        width: 100%;
                        padding: 36px;
                        box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.05);
                        text-align: center;
                    }
                    .badge {
                        display: inline-flex;
                        align-items: center;
                        gap: 6px;
                        background: #ecfdf5;
                        color: #047857;
                        border: 1px solid #a7f3d0;
                        padding: 6px 14px;
                        border-radius: 9999px;
                        font-size: 13px;
                        font-weight: 600;
                        margin-bottom: 20px;
                    }
                    .pulse {
                        width: 8px;
                        height: 8px;
                        background: #10b981;
                        border-radius: 50%;
                    }
                    h1 {
                        font-size: 24px;
                        font-weight: 800;
                        color: #0f172a;
                        margin: 0 0 12px 0;
                    }
                    p {
                        color: #64748b;
                        font-size: 14px;
                        line-height: 1.6;
                        margin: 0 0 24px 0;
                    }
                    .btn-primary {
                        display: inline-block;
                        background: #4f46e5;
                        color: #ffffff;
                        text-decoration: none;
                        font-weight: 600;
                        font-size: 14px;
                        padding: 12px 24px;
                        border-radius: 10px;
                        transition: background 0.2s;
                        box-shadow: 0 4px 6px -1px rgba(79, 70, 229, 0.2);
                    }
                    .btn-primary:hover {
                        background: #4338ca;
                    }
                    .links {
                        margin-top: 28px;
                        padding-top: 24px;
                        border-top: 1px solid #f1f5f9;
                        display: flex;
                        justify-content: center;
                        gap: 16px;
                        flex-wrap: wrap;
                    }
                    .links a {
                        color: #6366f1;
                        text-decoration: none;
                        font-size: 13px;
                        font-weight: 600;
                    }
                    .links a:hover {
                        text-decoration: underline;
                    }
                    .note {
                        background: #f1f5f9;
                        border-radius: 10px;
                        padding: 14px;
                        font-size: 13px;
                        color: #475569;
                        margin-top: 20px;
                        text-align: left;
                    }
                    .note code {
                        background: #e2e8f0;
                        padding: 2px 6px;
                        border-radius: 4px;
                        font-family: monospace;
                        font-size: 12px;
                    }
                </style>
            </head>
            <body>
                <div class="card">
                    <div class="badge">
                        <span class="pulse"></span>
                        Spring Boot 3.4 REST API Online
                    </div>
                    <h1>AI-Powered Digital Document Locker</h1>
                    <p>
                        The backend REST API server is running smoothly on port <code>8080</code>.<br>
                        To access the full web application dashboard, visit the React frontend.
                    </p>
                    <a href="http://localhost:5173" class="btn-primary">
                        👉 Open Web Application Dashboard (localhost:5173)
                    </a>
                    <div class="note">
                        <strong>📌 How the application works:</strong><br>
                        • <strong>Port 8080</strong> (here): Spring Boot REST API (data & AI processing).<br>
                        • <strong>Port 5173</strong>: React User Interface (Login, Upload, Search, and AI Viewer).<br>
                        If port 5173 is not open yet, run: <code>cd frontend</code> then <code>npm run dev</code>.
                    </div>
                    <div class="links">
                        <a href="/api/health" target="_blank">Health Check API (/api/health)</a>
                        <a href="/h2-console" target="_blank">H2 Database Console (/h2-console)</a>
                    </div>
                </div>
            </body>
            </html>
            """;
        return ResponseEntity.ok(html);
    }
}
