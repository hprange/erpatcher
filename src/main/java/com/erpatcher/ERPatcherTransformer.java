package com.erpatcher;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.ProtectionDomain;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:hprange@gmail.com.br">Henrique Prange</a>
 */
public class ERPatcherTransformer implements ClassFileTransformer {
    private final Map<String, String> classesMapping;

    public ERPatcherTransformer() {
        classesMapping = new HashMap<String, String>();

        classesMapping.put("com/webobjects/eoaccess/EOAttribute$Characteristic", "ERAttributeExtension");
        classesMapping.put("com/webobjects/eoaccess/EOAttribute", "ERAttributeExtension");
        classesMapping.put("com/webobjects/eocontrol/changeNotification/EOChangeNotificationOptions", "ERAttributeExtension");
        classesMapping.put("com/webobjects/eocontrol/_EOCheapCopyArray", "ERExtensions");
        classesMapping.put("com/webobjects/eocontrol/_EOCheapCopyMutableArray", "ERExtensions");
        classesMapping.put("com/webobjects/eocontrol/_EOIntegralKeyGlobalID", "ERExtensions");
        classesMapping.put("com/webobjects/appserver/WOCookie", "ERExtensions");
        classesMapping.put("com/webobjects/appserver/_private/WOHostUtilities", "ERExtensions");
        classesMapping.put("com/webobjects/foundation/NSArray", "ERExtensions");
        classesMapping.put("com/webobjects/foundation/NSArray$_AvgNumberOperator", "ERExtensions");
        classesMapping.put("com/webobjects/foundation/NSArray$_CountOperator", "ERExtensions");
        classesMapping.put("com/webobjects/foundation/NSArray$_MaxOperator", "ERExtensions");
        classesMapping.put("com/webobjects/foundation/NSArray$_MinOperator", "ERExtensions");
        classesMapping.put("com/webobjects/foundation/NSArray$_Operator", "ERExtensions");
        classesMapping.put("com/webobjects/foundation/NSArray$_SumNumberOperator", "ERExtensions");
        classesMapping.put("com/webobjects/foundation/NSArray$Operator", "ERExtensions");
        classesMapping.put("com/webobjects/foundation/NSDictionary", "ERExtensions");
        classesMapping.put("com/webobjects/foundation/NSDictionary$_JavaNSDictionaryMapEntry", "ERExtensions");
        classesMapping.put("com/webobjects/foundation/NSMutableArray", "ERExtensions");
        classesMapping.put("com/webobjects/foundation/NSMutableDictionary", "ERExtensions");
        classesMapping.put("com/webobjects/foundation/NSMutableSet", "ERExtensions");
        classesMapping.put("com/webobjects/foundation/NSSet", "ERExtensions");
    }

    public ERPatcherTransformer(Map<String, String> classMapping) {
        this.classesMapping = classMapping;
    }

    @Override
    public byte[] transform(ClassLoader classLoader, String classname, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] bytes) throws IllegalClassFormatException {
        byte[] byteCode = bytes;

        if (classesMapping.containsKey(classname)) {
            try {
                Enumeration<URL> resources = ((URLClassLoader) classLoader).findResources(classname + ".class");

                String jarFileName = classesMapping.get(classname);

                while (resources.hasMoreElements()) {
                    URL url = resources.nextElement();

                    if (url.toString().contains(jarFileName)) {
                        InputStream inputStream = url.openStream();

                        return toByteArray(inputStream);
                    }
                }

                throw new ERPatcherError("ERPatcher is unable to find a library (jar file) named " + jarFileName + " containing the " + classname + " class. Aborting...");
            } catch (IOException exception) {
                throw new ERPatcherError("Something wrong happened while trying to find resources. Aborting...", exception);
            }
        }

        return byteCode;
    }

    private byte[] toByteArray(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int n;
        byte[] data = new byte[1024];

        while ((n = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, n);
        }

        buffer.flush();

        return buffer.toByteArray();
    }
}
