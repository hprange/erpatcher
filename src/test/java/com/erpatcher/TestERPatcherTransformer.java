package com.erpatcher;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

import javassist.ClassPool;
import javassist.CtClass;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

/**
 * @author <a href="mailto:hprange@gmail.com.br">Henrique Prange</a>
 */
public class TestERPatcherTransformer {
    private static final byte[] ORIGINAL_BYTES = "ERPatcher rocks!".getBytes();
    private Map<String, String> classMapping;
    private URLClassLoader loader;
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    private ClassFileTransformer transformer;

    @Test
    public void returnAlternativeByteCodeWhenClassMappedForTransformation() throws Exception {
        classMapping.put("com/erpatcher/stub/Foo", "library-two");

        byte[] result = transformer.transform(loader, "com/erpatcher/stub/Foo", null, getClass().getProtectionDomain(), ORIGINAL_BYTES);

        verifyMethodExistance(result, "methodTwo");
    }

    @Test
    public void returnByteCodeWhenClassMappedForTransformation() throws Exception {
        classMapping.put("com/erpatcher/stub/Foo", "library-one");

        byte[] result = transformer.transform(loader, "com/erpatcher/stub/Foo", null, getClass().getProtectionDomain(), ORIGINAL_BYTES);

        verifyMethodExistance(result, "methodOne");
    }

    @Test
    public void returnSameByteCodeWhenNoClassMappedForTransformation() throws Exception {
        byte[] result = transformer.transform(loader, "com/erpatcher/stub/Foo", null, getClass().getProtectionDomain(), ORIGINAL_BYTES);

        assertThat(result, is(ORIGINAL_BYTES));
    }

    @Before
    public void setup() {
        URL[] libraries = new URL[] { getClass().getResource("/library-one.jar"), getClass().getResource("/library-two.jar") };

        loader = URLClassLoader.newInstance(libraries, getClass().getClassLoader());

        classMapping = new HashMap<String, String>();

        transformer = new ERPatcherTransformer(classMapping);
    }

    @Test
    public void throwErrorWhenMappedJarIsNotFound() throws Exception {
        classMapping.put("com/erpatcher/stub/Foo", "library-not-available");

        thrown.expect(ERPatcherError.class);
        thrown.expectMessage(is("ERPatcher is unable to find a library (jar file) named library-not-available containing the com/erpatcher/stub/Foo class. Aborting..."));

        transformer.transform(loader, "com/erpatcher/stub/Foo", null, getClass().getProtectionDomain(), ORIGINAL_BYTES);
    }

    @Test
    public void throwWrappedErrorWhenIOException() throws Exception {
        classMapping.put("com/erpatcher/stub/Foo", "library-one");

        loader = mock(URLClassLoader.class);

        IOException cause = new IOException("Error");

        doThrow(cause).when(loader).findResources(Mockito.anyString());

        thrown.expect(ERPatcherError.class);
        thrown.expectMessage(is("Something wrong happened while trying to find resources. Aborting..."));
        thrown.expectCause(is(cause));

        transformer.transform(loader, "com/erpatcher/stub/Foo", null, getClass().getProtectionDomain(), ORIGINAL_BYTES);
    }

    private void verifyMethodExistance(byte[] byteCode, String methodName) throws Exception {
        ClassPool pool = ClassPool.getDefault();

        CtClass clazz = pool.makeClass(new ByteArrayInputStream(byteCode));

        assertThat(clazz.getDeclaredMethod(methodName), notNullValue());
    }
}
