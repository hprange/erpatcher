package com.erpatcher;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author <a href="mailto:hprange@gmail.com.br">Henrique Prange</a>
 */
class ERJavaAgentLoader {
    /**
     *
     */
    static void loadAgent() {
        String nameOfRunningVM = ManagementFactory.getRuntimeMXBean().getName();

        int separatorIndex = nameOfRunningVM.indexOf('@');

        String pid = nameOfRunningVM.substring(0, separatorIndex);

        try {
            Class<?> vmClass = loadClass("com.sun.tools.attach.VirtualMachine");

            Object vm = vmClass.getMethod("attach", String.class).invoke(null, pid);

            URL location = ERJavaAgentLoader.class.getResource('/' + ERJavaAgentLoader.class.getName().replace(".", "/") + ".class");

            String path = location.getPath();

            String resource = path.substring("file:".length(), path.lastIndexOf("!"));

            vmClass.getMethod("loadAgent", String.class).invoke(vm, resource);
            vmClass.getMethod("detach").invoke(vm);
        } catch (Exception exception) {
            throw new ERPatcherError("An error has occurred while loading the ERPatcher agent. Aborting...", exception);
        }
    }

    private static Class<?> loadClass(String className) throws IOException, ClassNotFoundException {
        ERToolsJarFinder finder = new ERToolsJarFinder();

        URL toolsJarUrl = finder.find();

        ClassLoader loader = URLClassLoader.newInstance(new URL[] { toolsJarUrl }, ERJavaAgentLoader.class.getClassLoader());

        return Class.forName(className, true, loader);
    }
}
