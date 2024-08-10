package Expressions;


public class Constant implements Expression, AbstractFunction {
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
    public boolean hasVariable() {
        return false;
    }

    @Override
    public boolean isVariable() {
        return false;
    }

    @Override
    public boolean baseEquals(Object obj) {
        return equals(obj);
    }

    @Override
    public AbstractFunction derivative() {
        return new Constant(0);
    }

    @Override
    public Expression image(double x) {
        return this;
    }

    @Override
    public boolean isPolynomial() {
        return true;
    }

    @Override
    public boolean containsSameExpression(Expression expression) {
        if (expression instanceof Constant) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        if (value % 1 == 0) {
            return Integer.toString(((int) value));
        }
        return Double.toString(value);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(value);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Constant other = (Constant) obj;
        if (Double.doubleToLongBits(value) != Double.doubleToLongBits(other.value))
            return false;
        return true;
    }

    
}
