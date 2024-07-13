package Expressions;

public class Constant implements Expression {
    private double value;

    public Constant(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    @Override
    public Expression simplify() {
        return this;
    }

    @Override
    public String toString() {
        if (value % 1 == 0) {
            return Integer.toString(((int) value));
        }
        return Double.toString(value);
    }
}
