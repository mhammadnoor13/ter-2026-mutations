package experiment.execution;

import java.lang.reflect.Method;

public class ExecutionSupport {
    public static Object call(
            Object target,
            String methodName,
            Class<?>[] parameterTypes,
            Object... args
    ) {
        try {
            Method method = target.getClass().getMethod(methodName, parameterTypes);
            return method.invoke(target, args);
        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to execute method: " + methodName +
                            " on class " + target.getClass().getName(),
                    e
            );
        }
    }
}
