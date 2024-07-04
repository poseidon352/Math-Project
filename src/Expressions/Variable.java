package Expressions;

public class Variable implements Expression {
    String name;

    public Variable(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public Expression simplify() {
        return this;
    }
}
