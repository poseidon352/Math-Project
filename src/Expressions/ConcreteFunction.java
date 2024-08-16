package Expressions;

public class ConcreteFunction extends Thread implements AbstractFunction {
    protected AbstractFunction function;

    public ConcreteFunction(AbstractFunction function) {
        this.function = function;
    }

    @Override
    public AbstractFunction derivative() {
        return function.derivative();
    }

    @Override
    public Expression image(double x) {
        return function.image(x);
    }

    @Override
    public boolean isPolynomial() {
        return function.isPolynomial();
    }
}
