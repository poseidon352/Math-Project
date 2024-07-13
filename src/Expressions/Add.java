package Expressions;

public class Add extends Operator {
    
    public Add(Expression lhs, Expression rhs) {
        super(lhs, rhs);
    }

    @Override
    public Expression simplify() {
        // Simplify the lhs and rhs as much as possible
        lhs = lhs.simplify();
        rhs = rhs.simplify();
        // If the lhs and rhs are both constants then add them to produce a new constant
        if (this.lhs instanceof Constant && this.rhs instanceof Constant) {
            double sum = ((Constant) this.lhs).getValue() + ((Constant) this.rhs).getValue();
            return new Constant(sum);
        // If the lhs and rhs are both the same Variable then add them to produce a new Mul
        } else if (this.lhs instanceof Variable && this.rhs instanceof Variable) {
            // If they are the same variable
            if (((Variable) this.lhs).equals((Variable) this.rhs)) {
                return new Mul(new Constant(2), rhs);
            }
        // If the lhs is a Constant and the rhs is an Operator
        } else if (this.lhs instanceof Constant && this.rhs instanceof Operator) {
            return lhsConstantRhsOperator();
        // If the lhs is an Operator and the rhs is a Constant
        } else if (this.lhs instanceof Operator && this.rhs instanceof Constant) {
            return lhsOperatorRhsConstant();
        }
        // Otherwise return the simplified Add
        return this;
    }

    private Expression lhsConstantRhsOperator() {
        if (this.rhs instanceof Add) {
            return lhsConstantRhsAdd();
        } else if (this.rhs instanceof Sub) {
            return lhsConstantRhsSub();
        }
        return this;
    }

    private Expression lhsConstantRhsAdd() {
        Constant lhs = (Constant) this.lhs;
        Add rhs = (Add) this.rhs;
        if (rhs.getLHS() instanceof Constant) {
            Expression newLHS = new Constant (lhs.getValue() + ((Constant) rhs.getLHS()).getValue());
            return (new Add(newLHS, rhs.getRHS())).simplify();
        } else if (rhs.getRHS() instanceof Constant) {
            Expression newLHS = new Constant (lhs.getValue() + ((Constant) rhs.getRHS()).getValue());
            return (new Add(newLHS, rhs.getLHS())).simplify();
        }
        return this;
    }

    private Expression lhsConstantRhsSub() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'lhsConstantRhsSub'");
    }

    private Expression lhsOperatorRhsConstant() {
        if (this.lhs instanceof Add) {
            return lhsAddRhsConstant();
        } else if (this.lhs instanceof Sub) {
            return lhsSubRhsConstant();
        }
        return this;
    }

    private Expression lhsAddRhsConstant() {
        Constant rhs = (Constant) this.rhs;
        Add lhs = (Add) this.lhs;
        if (lhs.getRHS() instanceof Constant) {
            Expression newRHS = new Constant (rhs.getValue() + ((Constant) lhs.getRHS()).getValue());
            return (new Add(lhs.getLHS(), newRHS)).simplify();
        } else if (lhs.getLHS() instanceof Constant) {
            Expression newRHS = new Constant (rhs.getValue() + ((Constant) lhs.getLHS()).getValue());
            return (new Add(lhs.getRHS(), newRHS)).simplify();
        }
        return this;
    }

    private Expression lhsSubRhsConstant() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'lhsConstantRhsSub'");
    }

    @Override
    public String toString() {
        return lhs.toString() + " + " + rhs.toString();
    }
}
