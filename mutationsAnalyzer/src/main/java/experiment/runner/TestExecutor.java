package experiment.runner;

import java.lang.reflect.Method;
import java.util.List;

public class TestExecutor {
    public static void runTests(List<String> testMethods, Object instance) {
        for (String test : testMethods) {

            try {
                String className = extractClassName(test);
                String methodName = extractMethodName(test);

                Class<?> testClass = Class.forName(className);
                Object testInstance = testClass.getDeclaredConstructor().newInstance();

                Method method = testClass.getMethod(methodName);

                // inject instance if needed later (for now assume static scenario)
                method.invoke(testInstance);

            } catch (Exception e) {
                System.out.println("Error executing test: " + test);
                e.printStackTrace();
            }
        }
    }

    private static String extractClassName(String pitFormat) {
        return pitFormat.split("\\.\\[")[0];
    }

    private static String extractMethodName(String pitFormat) {
        int start = pitFormat.indexOf("method:") + 7;
        int end = pitFormat.indexOf("()");
        return pitFormat.substring(start, end);
    }
}
