package Expressions;

public class Sub extends Operator {
    
    public Sub(Expression lhs, Expression rhs) {
        super(lhs, rhs);
    }

    @Override
    public Expression simplify() {
        lhs = lhs.simplify();
        rhs = rhs.simplify();
        // If the lhs and rhs are both constants then subtract them to produce a new constant
        if (lhs instanceof Constant && rhs instanceof Constant) {
            double difference = ((Constant) lhs).getValue() - ((Constant) rhs).getValue();
            return new Constant(difference);
        }
        return this;
    }

    @Override
    public String toString() {
        return lhs.toString() + " - " + rhs.toString();
    }
}