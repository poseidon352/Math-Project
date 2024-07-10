package Expressions;

public class Div extends Operator {
    
    public Div(Expression numerator, Expression denominator) {
        super(numerator, denominator);
    }

    @Override
    public Expression simplify() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'simplify'");
    }

    @Override
    public String toString() {
        return lhs.toString() + " / " + rhs.toString();
    }
}
