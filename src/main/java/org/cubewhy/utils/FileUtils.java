package org.cubewhy.utils;

import java.io.InputStream;

@SuppressWarnings("unused")
public class FileUtils extends org.cubewhy.launcher.utils.FileUtils {
    public static InputStream getFile(String pathToFile) {
        return FileUtils.class.getResourceAsStream("/" + pathToFile);
    }
}
