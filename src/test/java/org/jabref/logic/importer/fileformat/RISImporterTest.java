package org.jabref.logic.importer.fileformat;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import org.jabref.logic.util.StandardFileType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class RISImporterTest {

    private RisImporter importer;

    @BeforeEach
    public void setUp() {
        importer = new RisImporter();
    }

    @Test
    public void testGetFormatName() {
        assertEquals("RIS", importer.getName());
    }

    @Test
    public void testGetCLIId() {
        assertEquals("ris", importer.getId());
    }

    /**
     * CS304 (manually written) issue link: https://github.com/JabRef/jabref/issues/7737
     * Test if could get entries correctly
     */
    @Test
    public void testGetEntries() {
        assertNotNull(importer.getEntries());
    }

    @Test
    public void testsGetExtensions() {
        assertEquals(StandardFileType.RIS, importer.getFileType());
    }

    @Test
    public void testGetDescription() {
        assertEquals("Imports a Biblioscape Tag File.", importer.getDescription());
    }

    @Test
    public void testIfNotRecognizedFormat() throws IOException, URISyntaxException {
        Path file = Path.of(RISImporterTest.class.getResource("RisImporterCorrupted.ris").toURI());
        assertFalse(importer.isRecognizedFormat(file, StandardCharsets.UTF_8));
    }

    /**
     * CS304 (manually written) issue link: https://github.com/JabRef/jabref/issues/7737
     * Test if split the lines remove "ER  -" correctly
     */
    @Test
    public void testIfSplitCorrect() {
        String[] entries = importer.getEntries();
        for (String entry : entries) {
            assertFalse(entry.equals("ER  - "));
        }
    }

}
