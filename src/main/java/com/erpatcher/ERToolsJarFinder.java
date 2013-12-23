package com.erpatcher;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

/**
 * @author <a href="mailto:hprange@gmail.com.br">Henrique Prange</a>
 */
class ERToolsJarFinder {
    /**
     * @return
     * @throws IOException
     */
    URL find() throws IOException {
        String javaHome = System.getProperty("java.home");
        String separator = System.getProperty("file.separator");

        if (javaHome == null) {
            throw new IllegalStateException("Cannot find the tools.jar file because the java home property is not set.");
        }

        File toolsFile = findIntoJavaHome(javaHome, separator);

        return toolsFile.toURI().toURL();
    }

    private File findIntoJavaHome(String javaHome, String separator) throws IOException {
        File toolsFile = new File(javaHome + separator + "lib" + separator + "tools.jar");

        if (toolsFile.exists()) {
            return toolsFile;
        }

        toolsFile = new File(javaHome + separator + ".." + separator + "lib" + separator + "tools.jar").getCanonicalFile();

        if (toolsFile.exists()) {
            return toolsFile;
        }

        toolsFile = new File(javaHome + separator + ".." + separator + "Classes" + separator + "classes.jar").getCanonicalFile();

        if (toolsFile.exists()) {
            return toolsFile;
        }

        throw new FileNotFoundException("Cannot find the tools.jar file. ERPatcher requires a JDK in order to work correctly.");
    }
}
