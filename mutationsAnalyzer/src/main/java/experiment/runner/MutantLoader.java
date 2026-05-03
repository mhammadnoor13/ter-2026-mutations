package experiment.runner;

public class MutantLoader {

    public static Object load(String className) {
        try {
            Class<?> clazz = Class.forName(className);
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load mutant: " + className, e);
        }
    }
}
