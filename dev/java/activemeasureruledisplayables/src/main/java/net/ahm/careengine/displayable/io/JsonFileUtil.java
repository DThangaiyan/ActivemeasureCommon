package net.ahm.careengine.displayable.io;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.log4j.Logger;

public class JsonFileUtil {
    private static final Logger LOGGER = Logger.getLogger(JsonFileUtil.class);

    public static void saveAsFile(String jsonString,
            String fileNameWithoutExtension, String destinationDirectory)
            throws IOException {
        String baseDir = System.getProperty("user.dir");
        Path destDirPath = Paths.get(baseDir, destinationDirectory);
        if (Files.exists(destDirPath)) {
            Path destinationFilePath = Paths.get(destDirPath.toString(),
                    fileNameWithoutExtension + ".json");
            Files.createDirectories(destinationFilePath.getParent());
            Files.write(destinationFilePath, jsonString.getBytes(), CREATE,
                    TRUNCATE_EXISTING, WRITE);
        } else {
            LOGGER.error("Not able to find " + destDirPath
                    + ", not going to create the JSON file");
        }
    }
}
