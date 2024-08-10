package Expressions;

public interface AbstractFunction {
    public AbstractFunction derivative();
    public Expression image(double x);
    public boolean isPolynomial();
}
