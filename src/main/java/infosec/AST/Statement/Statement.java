package infosec.AST.Statement;

public abstract class Statement {
    private int startingLine;
    private int endingLine;

    public abstract String toString();

    public void setStartingLine(int startingLine) {
        this.startingLine = startingLine;
    }

    public void setEndingLine(int endingLine) {
        this.endingLine = endingLine;
    }

    public void setLineNumbers(int startingLine, int endingLine) {
        this.startingLine = startingLine;
        this.endingLine = endingLine;
    }

    public int getStartingLine() {
        return this.startingLine;
    }

    public int getEndingLine() {
        return this.endingLine;
    }
}
