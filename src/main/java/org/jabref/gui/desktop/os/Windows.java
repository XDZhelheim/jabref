package org.jabref.gui.desktop.os;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import org.jabref.gui.externalfiletype.ExternalFileType;
import org.jabref.gui.externalfiletype.ExternalFileTypes;


public class Windows implements NativeDesktop {
    private static final String DEFAULT_EXECUTABLE_EXTENSION = ".exe";

    @Override
    public void openFile(String filePath, String fileType) throws IOException {
        Optional<ExternalFileType> type = ExternalFileTypes.getInstance().getExternalFileTypeByExt(fileType);

        if (type.isPresent() && !type.get().getOpenWithApplication().isEmpty()) {
            openFileWithApplication(filePath, type.get().getOpenWithApplication());
        } else {
            // quote String so explorer handles URL query strings correctly
            String quotePath = "\"" + filePath + "\"";
            new ProcessBuilder("explorer.exe", quotePath).start();
        }
    }

    /**
     * CS304 Issue link: https://github.com/EvoSuite/evosuite/issues/7641
     * Detect the path of program under ProgramFiles or ProgramFiles(x86), return empty string if nothing found.
     * @param programName Name of program (for example, texstudio)
     * @param directoryName Name of the directory that contains program (for example, TeXstudio)
     */
    @Override
    public String detectProgramPath(String programName, String directoryName) {
        String progFiles = System.getenv("ProgramFiles(x86)");
        String programPath;
        if (progFiles != null) {
            programPath = getProgramPath(programName, directoryName, progFiles);
            if (programPath != null) {
                return programPath;
            }
        }

        progFiles = System.getenv("ProgramFiles");
        programPath = getProgramPath(programName, directoryName, progFiles);
        if (programPath != null) {
            return programPath;
        }

        return "";
    }

    /**
     * CS304 Issue link: https://github.com/EvoSuite/evosuite/issues/7641
     * Find the path of program under ProgramFiles or ProgramFiles(x86), return null if program not exists.
     * @param programName Name of program (for example, texstudio)
     * @param directoryName Name of the directory that contains program (for example, TeXstudio)
     * @param progFiles Path of windows system directory (for example, C:/Program Files or C:/Program Files(x86))
     */
    private String getProgramPath(String programName, String directoryName, String progFiles) {
        Path programPath;
        if ((directoryName != null) && !directoryName.isEmpty()) {
            programPath = Path.of(progFiles, directoryName, programName + DEFAULT_EXECUTABLE_EXTENSION);
        } else {
            programPath = Path.of(progFiles, programName + DEFAULT_EXECUTABLE_EXTENSION);
        }
        if (Files.exists(programPath)) {
            return programPath.toString();
        }
        return null;
    }

    @Override
    public Path getApplicationDirectory() {
        String programDir = System.getenv("ProgramFiles");

        if (programDir != null) {
            return Path.of(programDir);
        }
        return getUserDirectory();
    }

    @Override
    public void openFileWithApplication(String filePath, String application) throws IOException {
        new ProcessBuilder(Path.of(application).toString(), Path.of(filePath).toString()).start();
    }

    @Override
    public void openFolderAndSelectFile(Path filePath) throws IOException {
        new ProcessBuilder("explorer.exe", "/select,", filePath.toString()).start();
    }

    @Override
    public void openConsole(String absolutePath) throws IOException {
        ProcessBuilder process = new ProcessBuilder("cmd.exe", "/c", "start");
        process.directory(new File(absolutePath));
        process.start();
    }
}
