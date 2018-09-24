package com.agileengine.test.xml;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class XMLComparatorServiceTest {
    private static final String ORIGINAL_FILE_PATH = "test_html/sample-0-origin.html";
    private XMLComparatorService seeker;

    @BeforeEach
    void setUp() {
        seeker = new XMLComparatorService(new XMLElementByIdSeekerServiceService(), new XMLSimilarElementSeekerService());
    }

    @Test
    void testFile1() {
        testFile(ORIGINAL_FILE_PATH, "test_html/sample-1-evil-gemini.html", "html[0]>body[0]>div[0]>div[0]>div[2]>div[0]>div[0]>div[1]>a[1]");
    }

    private void testFile(String originalFilePath, String comparedFilePath, String expectedResult) {
        InputStream originalFile = getClass().getClassLoader().getResourceAsStream(originalFilePath);
        InputStream comparedFile = getClass().getClassLoader().getResourceAsStream(comparedFilePath);
        assertNotNull(originalFile, "test file should be found");
        assertNotNull(comparedFile, "test file should be found");
        assertEquals(expectedResult, seeker.seek(originalFile, "make-everything-ok-button", comparedFile).orElse(""), "proper name should be found");
    }
    @Nested
    class WhenBasicBehaviorIsOk {
        @Test
        void testFile2() {
            testFile(ORIGINAL_FILE_PATH, "test_html/sample-2-container-and-clone.html", "html[0]>body[0]>div[0]>div[0]>div[2]>div[0]>div[0]>div[1]>div[0]>a[0]");
        }
        @Test
        void testFile3() {
            testFile(ORIGINAL_FILE_PATH, "test_html/sample-3-the-escape.html", "html[0]>body[0]>div[0]>div[0]>div[2]>div[0]>div[0]>div[2]>a[0]");
        }
        @Test
        void testFile4() {
            testFile(ORIGINAL_FILE_PATH, "test_html/sample-4-the-mash.html", "html[0]>body[0]>div[0]>div[0]>div[2]>div[0]>div[0]>div[2]>a[0]");
        }
    }
}