package com.documentlocker.service;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class PdfExtractionService {

    private static final Logger logger = LoggerFactory.getLogger(PdfExtractionService.class);
    private static final int MAX_TEXT_LENGTH = 40000;

    public String extractText(String filePath, String originalFileName) {
        if (filePath == null) {
            return "";
        }

        File file = new File(filePath);
        if (!file.exists()) {
            return "";
        }

        String lowerName = originalFileName != null ? originalFileName.toLowerCase() : file.getName().toLowerCase();

        try {
            if (lowerName.endsWith(".pdf")) {
                return extractFromPdf(file);
            } else if (lowerName.endsWith(".txt") || lowerName.endsWith(".md") || lowerName.endsWith(".csv") || lowerName.endsWith(".json")) {
                return extractFromTextFile(file);
            } else {
                return "[Binary file: " + originalFileName + "]";
            }
        } catch (Exception ex) {
            logger.warn("Could not extract text from file {}: {}", originalFileName, ex.getMessage());
            return "";
        }
    }

    private String extractFromPdf(File file) {
        try (PDDocument document = Loader.loadPDF(file)) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            String text = stripper.getText(document);

            if (text == null || text.isBlank()) {
                return "[Scanned or image-only PDF - no selectable text]";
            }

            text = text.trim();
            if (text.length() > MAX_TEXT_LENGTH) {
                return text.substring(0, MAX_TEXT_LENGTH) + "\n...[truncated for AI processing]";
            }
            return text;
        } catch (IOException e) {
            logger.warn("Failed to parse PDF {}: {}", file.getName(), e.getMessage());
            return "";
        }
    }

    private String extractFromTextFile(File file) {
        try {
            String text = Files.readString(Path.of(file.getAbsolutePath()), StandardCharsets.UTF_8);
            if (text.length() > MAX_TEXT_LENGTH) {
                return text.substring(0, MAX_TEXT_LENGTH) + "\n...[truncated]";
            }
            return text;
        } catch (IOException e) {
            return "";
        }
    }
}
