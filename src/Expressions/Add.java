package Expressions;

public class Add extends Operator {
    
    public Add(Expression lhs, Expression rhs) {
        super(lhs, rhs);
    }

    @Override
    public Expression simplify() {
        // If the lhs and rhs are both constants then add them to produce a new constant
        if (lhs instanceof Constant && rhs instanceof Constant) {
            double sum = ((Constant) lhs).getValue() + ((Constant) rhs).getValue();
            return new Constant(sum);
        }
        return this;
    }

    @Override
    public String toString() {
        return lhs.toString() + " + " + rhs.toString();
    }
}
