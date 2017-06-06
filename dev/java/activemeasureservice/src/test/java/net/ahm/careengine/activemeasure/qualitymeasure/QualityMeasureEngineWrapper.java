package net.ahm.careengine.activemeasure.qualitymeasure;

import java.util.Arrays;

import net.ahm.careengine.activemeasure.BaseActiveMeasureEngine;

public class QualityMeasureEngineWrapper extends BaseActiveMeasureEngine {

    public void downloadFiles(boolean usePackageFiles, String baseFolder) {
        if (usePackageFiles) {
            downloadLatestPackageResources(getURL(), baseFolder, getUsername(),
                    getPassword());
        } else {
            downloadPackageSourceFilesResources(getURL(), baseFolder,
                    getUsername(), getPassword());
        }
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            throw new IllegalArgumentException("Usage: baseFolder not:"
                    + Arrays.toString(args));
        }

        QualityMeasureEngineWrapper engine = new QualityMeasureEngineWrapper();
        String resourceTypeString = engine.getResourceTypeString();
        engine.downloadFiles(QualityMeasureEngineWrapper.ALL_LATEST
                .equalsIgnoreCase(resourceTypeString), args[0]);
    }
}
