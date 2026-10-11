package com.documentlocker;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

public class GeneratePdfDemoFiles {
    public static void main(String[] args) throws Exception {
        File demoDir = new File("d:/au/Academic/TT/ai-document-locker/demo-documents");
        File[] textFiles = demoDir.listFiles((dir, name) -> name.endsWith(".txt"));

        if (textFiles == null) return;

        for (File txtFile : textFiles) {
            String pdfName = txtFile.getName().replace(".txt", ".pdf");
            File pdfFile = new File(demoDir, pdfName);

            List<String> lines = Files.readAllLines(txtFile.toPath());

            try (PDDocument doc = new PDDocument()) {
                PDPage page = new PDPage();
                doc.addPage(page);

                try (PDPageContentStream stream = new PDPageContentStream(doc, page)) {
                    stream.beginText();
                    stream.setFont(new PDType1Font(Standard14Fonts.FontName.COURIER), 8.5f);
                    stream.setLeading(12.0f);
                    stream.newLineAtOffset(40, 750);

                    int count = 0;
                    for (String line : lines) {
                        if (count++ > 55) break; // keep to single page for demo
                        String sanitized = line.replaceAll("[^\\x00-\\x7F]", " ");
                        stream.showText(sanitized);
                        stream.newLine();
                    }
                    stream.endText();
                }

                doc.save(pdfFile);
                System.out.println("Generated: " + pdfFile.getName());
            }
        }
    }
}
