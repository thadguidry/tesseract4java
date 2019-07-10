package de.vorb.tesseract.gui.work;

import de.vorb.tesseract.gui.controller.TesseractController;
import de.vorb.tesseract.gui.model.Image;
import de.vorb.tesseract.tools.preprocessing.Preprocessor;
import de.vorb.util.FileNames;

import org.checkerframework.checker.nullness.qual.NonNull;

import javax.imageio.ImageIO;
import javax.swing.SwingWorker;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

public class PreprocessingWorker extends SwingWorker<Image, Void> {
    private final TesseractController controller;
    private final Preprocessor preprocessor;
    private final Path sourceFile;
    private final Path destinationDir;

    public PreprocessingWorker(TesseractController controller,
            Preprocessor preprocessor, Path sourceFile, @NonNull Path destinationDir) {
        this.controller = controller;
        this.preprocessor = preprocessor;
        this.sourceFile = sourceFile;
        this.destinationDir = destinationDir;
    }

    @Override
    protected Image doInBackground() throws Exception {
        Files.createDirectories(destinationDir);

        final Path destFile = destinationDir.resolve(FileNames.replaceExtension(
                sourceFile, "png").getFileName());

        final BufferedImage sourceImg = ImageIO.read(sourceFile.toFile());

        final BufferedImage preprocessedImg = preprocessor.process(sourceImg);
        ImageIO.write(preprocessedImg, "PNG", destFile.toFile());

        return new Image(sourceFile, sourceImg, destFile, preprocessedImg);
    }

    @Override
    protected void done() {
        try {
            controller.setImage(get());
        } catch (InterruptedException | ExecutionException | CancellationException e) {
        } finally {
            controller.getView().getProgressBar().setIndeterminate(false);
        }
    }
}
