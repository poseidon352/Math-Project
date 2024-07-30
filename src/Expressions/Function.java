package Expressions;

public interface Function extends Expression{
    public Expression derivative();
    public Expression image(double x);
}
