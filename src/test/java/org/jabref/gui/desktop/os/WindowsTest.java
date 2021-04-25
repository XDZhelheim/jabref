package org.jabref.gui.desktop.os;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WindowsTest {

    Windows windows = new Windows();

    /**
     * CS304 Issue link: https://github.com/JabRef/jabref/issues/7641
     * Test method: Test on detectProgram
     */
    @Test
    void detectProgramPathUnderProgramFileTest() {
        assertEquals("C:\\Program Files\\TeXstudio\\texstudio.exe", windows.detectProgramPath("texstudio", "TeXstudio"));
    }

    /**
     * CS304 Issue link: https://github.com/JabRef/jabref/issues/7641
     * Test method: Test on detectProgram
     */
    @Test
    void detectProgramPathNotUnderProgramFileTest() {
        assertEquals("C:\\Program Files\\TeXstudio\\texstudio.exe", windows.detectProgramPath("texstudio", "teXstudio"));
    }
}
