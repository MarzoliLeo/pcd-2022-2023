package pcd.ex1;

import java.io.File;

public class StartProcessingMessage {
    private final File directory;

    public StartProcessingMessage(File directory) {
        this.directory = directory;
    }

    public File getDirectory() {
        return directory;
    }
}

