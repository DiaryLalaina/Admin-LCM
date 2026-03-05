package com.work.cashier.burningwave;

import static org.burningwave.core.assembler.StaticComponentContainer.Modules;

public class AllModulesToAllModulesExporter {

    public static void execute() {
        try {
            Modules.exportPackageToAllUnnamed("java.base","java.lang.reflect");
            /*Modules.exportAllToAll();
            Class<?> bootClassLoaderClass = Class.forName("jdk.internal.loader.ClassLoaders$BootClassLoader");
            Constructor<? extends ClassLoader> constructor =
                    ClassLoader.getPlatformClassLoader().getClass().getDeclaredConstructor(bootClassLoaderClass);
            constructor.setAccessible(true);
            Class<?> classLoadersClass = Class.forName("jdk.internal.loader.ClassLoaders");
            Method bootClassLoaderRetriever = classLoadersClass.getDeclaredMethod("bootLoader");
            bootClassLoaderRetriever.setAccessible(true);
            ClassLoader newBuiltinclassLoader = constructor.newInstance(bootClassLoaderRetriever.invoke(classLoadersClass));
            System.out.println(newBuiltinclassLoader + " instantiated");

             */
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }


}
