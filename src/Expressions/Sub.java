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
        } else if (this.lhs instanceof Constant && this.rhs instanceof Operator) {
            return lhsConstantRhsOperator().simplify();
        // If the lhs is an Operator and the rhs is a Constant
        } else if (this.lhs instanceof Operator && this.rhs instanceof Constant) {
            return lhsOperatorRhsConstant().simplify();
        }
        return doubleNegativeSimplification();
    }

    private Expression doubleNegativeSimplification() {
        if (rhs instanceof Constant) {
            Constant rhs = (Constant) this.rhs;
            if (rhs.getValue() < 0) {
                Double newRHSValue = rhs.getValue() * (-1);
                return new Add(lhs, new Constant(newRHSValue));
            }
        }
        return this;
    }

    private Expression lhsConstantRhsOperator() {
        if (this.rhs instanceof Add) {
            return lhsConstantRhsAdd().simplify();
        } else if (this.rhs instanceof Sub) {

        }
        return this;
    }

    private Expression lhsConstantRhsAdd() {
        
        return this;
    }

    private Expression lhsOperatorRhsConstant() {
        return this;
    }


    @Override
    public String toString() {
        return lhs.toString() + " - " + rhs.toString();
    }
}