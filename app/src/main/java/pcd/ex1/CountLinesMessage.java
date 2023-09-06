package pcd.ex1;

import java.io.File;

public class CountLinesMessage {
    private final File file;
    private final ModelEController modelActor;

    public CountLinesMessage(File file, ModelEController modelActor) {
        this.file = file;
        this.modelActor = modelActor;
    }

    public File getFile() {
        return file;
    }

    public ModelEController getModelActor() {
        return modelActor;
    }
}

