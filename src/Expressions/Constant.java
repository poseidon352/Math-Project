package Expressions;

public class Constant implements Expression {
    double value;

    public Constant(double value) {
        this.value = value;
    }

    @Override
    public Expression simplify() {
        return this;
    }
}
