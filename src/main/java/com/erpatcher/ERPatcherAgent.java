package com.erpatcher;

import java.lang.instrument.Instrumentation;

/**
 * @author <a href="mailto:hprange@gmail.com.br">Henrique Prange</a>
 */
public class ERPatcherAgent {
    private static Instrumentation instrumentation;

    /**
     * JVM hook to dynamically load javaagent at runtime.
     *
     * The agent class may have an agentmain method for use when the agent is
     * started after VM startup.
     *
     * @param args
     * @param inst
     * @throws Exception
     */
    public static void agentmain(String args, Instrumentation instrumentation) throws Exception {
        System.out.println("agentmain method invoked with args: " + args + " and instrumentation: " + instrumentation);

        prepareInstrumentation(args, instrumentation);
    }

    /**
     * Programmatic hook to dynamically load javaagent at runtime.
     */
    public static void initialize() {
        if (instrumentation == null) {
            ERJavaAgentLoader.loadAgent();
        }
    }

    /**
     * JVM hook to statically load the javaagent at startup.
     *
     * After the Java Virtual Machine (JVM) has initialized, the premain method
     * will be called. Then the real application main method will be called.
     *
     * @param args
     * @param instrumentation
     * @throws Exception
     */
    public static void premain(String args, Instrumentation instrumentation) throws Exception {
        System.out.println("premain method invoked with args: " + args + " and instrumentation: " + instrumentation);

        prepareInstrumentation(args, instrumentation);
    }

    private static void prepareInstrumentation(String args, Instrumentation instrumentation) {
        ERPatcherAgent.instrumentation = instrumentation;

        instrumentation.addTransformer(new ERPatcherTransformer());
    }
}
