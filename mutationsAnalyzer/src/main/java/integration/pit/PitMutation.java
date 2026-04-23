package integration.pit;

public class PitMutation {

    public String className;
    public String sourceFile;
    public int lineNumber;
    public String mutator;
    public String description;
    public String status;

    public PitMutation(String className, String sourceFile,
                       int lineNumber, String mutator,
                       String description, String status) {

        this.className = className;
        this.sourceFile = sourceFile;
        this.lineNumber = lineNumber;
        this.mutator = mutator;
        this.description = description;
        this.status = status;
    }

    @Override
    public String toString() {
        return className + " line " + lineNumber + " → " + description;
    }
}