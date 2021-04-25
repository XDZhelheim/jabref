package org.jabref.logic.integrity;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import org.jabref.model.entry.BibEntry;
import org.jabref.model.entry.field.StandardField;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UTF8CheckerTest {

    private final BibEntry entry = new BibEntry();

    /**
     * CS304 Issue Link: https://github.com/JabRef/jabref/issues/5850
     * <p>
     * fieldAcceptsUTF8 to check UTF8Checker's result set
     * when the entry is encoded in UTF-8 (should be empty)
     */
    @Test
    void fieldAcceptsUTF8() {
        UTF8Checker checker = new UTF8Checker();
        entry.setField(StandardField.TITLE, "Only ascii characters!'@12");
        assertEquals(Collections.emptyList(), checker.check(entry));
    }

    /**
     * CS304 Issue Link: https://github.com/JabRef/jabref/issues/5850
     * <p>
     * fieldDoesNotAcceptUmlauts to check UTF8Checker's result set
     * when the entry is encoded in Non-Utf-8 charset and the System
     * environment is Non UTF-8.
     * Finally we need to reset the environment charset.
     *
     * @throws UnsupportedEncodingException initial a String in charset GBK
     *                                      Demo: new String(StringDemo.getBytes(), "GBK");
     */
    @Test
    void fieldDoesNotAcceptUmlauts() throws UnsupportedEncodingException {
        String defaultCharset = System.getProperty("file.encoding");
        System.getProperties().put("file.encoding", "GBK");
        UTF8Checker checker = new UTF8Checker();
        String nonUTF8 = new String("你好，这条语句使用GBK字符集".getBytes(defaultCharset), Charset.forName("GBK"));
        entry.setField(StandardField.MONTH, nonUTF8);
        assertEquals(List.of(new IntegrityMessage("Non-UTF-8 encoded field found", entry, StandardField.MONTH)), checker.check(entry));
        System.getProperties().put("file.encoding", defaultCharset);
    }

    /**
     * CS304 Issue Link: https://github.com/JabRef/jabref/issues/5850
     * <p>
     * To check the UTF8Checker.UTF8EncodingChecker
     * in NonUTF8 char array (should return false)
     *
     * @throws UnsupportedEncodingException initial a String in charset GBK
     *                                      Demo: new String(StringDemo.getBytes(), "GBK");
     */
    @Test
    void nonUTF8EncodingCheckerTest() throws UnsupportedEncodingException {
        String defaultCharset = System.getProperty("file.encoding");
        String nonUTF8 = new String("你好，这条语句使用GBK字符集".getBytes(defaultCharset), "GBK");
        assertFalse(UTF8Checker.utf8EncodingChecker(nonUTF8.getBytes("GBK")));

    }

    /**
     * CS304 Issue Link: https://github.com/JabRef/jabref/issues/5850
     * <p>
     * To check the UTF8Checker.UTF8EncodingChecker
     * in UTF-8 char array (should return true)
     */
    @Test
    void utf8EncodingCheckerTest() throws UnsupportedEncodingException {
        String defaultCharset = System.getProperty("file.encoding");
        String utf8Demo = new String("你好，这条语句使用GBK字符集".getBytes(defaultCharset), StandardCharsets.UTF_8);
        assertTrue(UTF8Checker.utf8EncodingChecker(utf8Demo.getBytes(StandardCharsets.UTF_8)));
    }
}
