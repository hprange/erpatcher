package com.erpatcher;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.FileNotFoundException;
import java.net.URL;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

/**
 * @author <a href="mailto:hprange@gmail.com.br">Henrique Prange</a>
 */
public class TestERToolsJarLoader {
    private ERToolsJarFinder finder;
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    private String javaHome;
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void returnClassesJarUrlWhenClassesDirectoryAvailable()
            throws Exception {
        String newJavaHome = folder.newFolder("Home").getAbsolutePath();

        folder.newFolder("Classes");
        folder.newFile("Classes/classes.jar");

        System.setProperty("java.home", newJavaHome);

        URL result = finder.find();

        assertThat(result, notNullValue());
        assertThat(result.toString(), is("file:" + folder.getRoot().getCanonicalPath() + "/Classes/classes.jar"));
    }

    @Test
    public void returnToolsJarUrlWhenLibDirectoryAvailable() throws Exception {
        folder.newFolder("lib");
        folder.newFile("lib/tools.jar");

        String newJavaHome = folder.getRoot().getAbsolutePath();

        System.setProperty("java.home", newJavaHome);

        URL result = finder.find();

        assertThat(result, notNullValue());
        assertThat(result.toString(), is("file:" + newJavaHome + "/lib/tools.jar"));
    }

    @Test
    public void returnToolsJarUrlWhenRunningFromJRE() throws Exception {
        folder.newFolder("lib");
        folder.newFile("lib/tools.jar");

        String newJavaHome = folder.newFolder("jre").getAbsolutePath();

        System.setProperty("java.home", newJavaHome);

        URL result = finder.find();

        assertThat(result, notNullValue());
        assertThat(result.toString(), is("file:" + folder.getRoot().getCanonicalPath() + "/lib/tools.jar"));
    }

    @Before
    public void setup() {
        javaHome = System.getProperty("java.home");

        finder = new ERToolsJarFinder();
    }

    @After
    public void tearDown() {
        System.setProperty("java.home", javaHome);
    }

    @Test
    public void throwExceptionWhenJavaHomeIsNotSet() throws Exception {
        System.clearProperty("java.home");

        thrown.expect(IllegalStateException.class);
        thrown.expectMessage(is("Cannot find the tools.jar file because the java home property is not set."));

        finder.find();
    }

    @Test
    public void throwExceptionWhenToolsJarDoesNotExist() throws Exception {
        String newJavaHome = folder.getRoot().getAbsolutePath();

        System.setProperty("java.home", newJavaHome);

        thrown.expect(FileNotFoundException.class);
        thrown.expectMessage(is("Cannot find the tools.jar file. ERPatcher requires a JDK in order to work correctly."));

        finder.find();
    }
}
