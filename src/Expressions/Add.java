package Expressions;

public class Add extends Operator {
    
    public Add(Expression lhs, Expression rhs) {
        super(lhs, rhs);
    }

    @Override
    public Expression simplify() {
        // Simplify the lhs and rhs as much as possible
        lhs = this.lhs.simplify();
        rhs = this.rhs.simplify();
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
        } else if (this.lhs instanceof Variable && this.rhs instanceof Constant) {
            return plusMinusSimplification();
        } else if (constOrVar(lhs) && this.rhs instanceof Operator) {
            return rhsOperator();
        // If the lhs is an Operator and the rhs is a Constant
        } else if (this.lhs instanceof Operator && constOrVar(rhs)) {
            return lhsOperator();
        }
        // Otherwise return the simplified Add
        return this;
    }
    
    // If the rhs is a negative constant then return a Sub operation
    private Expression plusMinusSimplification() {
        Double rhsValue = ((Constant) this.rhs).getValue();
        if (rhsValue < 0) {
            return (new Sub(lhs, new Constant(rhsValue*(-1)))).simplify();
        }
        return this;
    }

    // Assuming the lhs is a Constant or Variable, then return the respective method depending
    // if the rhs is an Add, Sub or Multiply (only if lhs is a Variable)
    private Expression rhsOperator() {
        if (this.rhs instanceof Add) {
            return rhsAdd().simplify();
        } else if (this.rhs instanceof Sub) {
            return rhsSub().simplify();
        } else if (this.lhs instanceof Variable && this.rhs instanceof Mul) {
            return rhsMul().simplify();
        }
        return this;
    }

    private Expression rhsAdd() {
        Add rhs = (Add) this.rhs;
        if (lhs.getClass().equals(rhs.getLHS().getClass())) {
            Expression newLHS = new Add(lhs, rhs.getLHS());
            return new Add(newLHS, rhs.getRHS());
        } else if (lhs.getClass().equals(rhs.getRHS().getClass())) {
            Expression newRHS = new Add (lhs, rhs.getRHS());
            return new Add(rhs.getLHS(), newRHS);
        }
        return this;
    }

    private Expression rhsSub() {
        Sub rhs = (Sub) this.rhs;
        if (lhs.getClass().equals(rhs.getLHS().getClass())) {
            Expression newLHS = new Add(lhs, rhs.getLHS());
            return new Sub(newLHS, rhs.getRHS());
        } else if (lhs.getClass().equals(rhs.getRHS().getClass())) {
            Expression newRHS = new Sub(lhs, rhs.getRHS());
            return new Sub(rhs.getLHS(), newRHS);
        }
        return this;
    }

    // Assuming that LHS is a Variable and RHS is a Mul
    private Expression rhsMul() {
        Mul rhs = (Mul) this.rhs;
        if (rhs.getLHS() instanceof Constant && rhs.getRHS() instanceof Variable) {
            if (lhs.equals(rhs.getRHS())) {
                return new Mul(new Constant(((Constant) rhs.getLHS()).getValue() + 1), lhs);
            }
        } else if (rhs.getLHS() instanceof Variable && rhs.getRHS() instanceof Constant) {
            if (lhs.equals(rhs.getLHS())) {
                return new Mul(new Constant(((Constant) rhs.getRHS()).getValue() + 1), lhs);
            }
        }
        return this;
    }

    private Expression lhsOperator() {
        if (this.lhs instanceof Add) {
            return lhsAdd().simplify();
        } else if (this.lhs instanceof Sub) {
            return lhsSub().simplify();
        } else if (this.lhs instanceof Mul && this.rhs instanceof Variable) {
            return lhsMul().simplify();
        }
        return this;
    }

    private Expression lhsAdd() {
        Add lhs = (Add) this.lhs;
        if (rhs.getClass().equals(lhs.getRHS().getClass())) {
            Expression newRHS = new Add(rhs, lhs.getRHS());
            return new Add(newRHS, lhs.getLHS());
        } else if (rhs.getClass().equals(lhs.getLHS().getClass())) {
            Expression newLHS = new Add (rhs, lhs.getLHS());
            return new Add(newLHS, lhs.getRHS());
        }
        return this;
    }

    private Expression lhsSub() {
        Constant rhs = (Constant) this.rhs;
        Sub lhs = (Sub) this.lhs;
        if (lhs.getRHS() instanceof Constant) {
            Double newRHSValue = rhs.getValue() - ((Constant) lhs.getRHS()).getValue();
            if (newRHSValue.equals(0.0)) {
                return lhs.getLHS();
            } else if (newRHSValue > 0) {
                return new Add(lhs.getLHS(), new Constant(newRHSValue));
            } 
            return new Sub(lhs.getLHS(), new Constant(newRHSValue));
        }
        return this;
    }

    private Expression lhsMul() {
        Mul lhs = (Mul) this.lhs;
        if (lhs.getRHS() instanceof Constant && lhs.getLHS() instanceof Variable) {
            if (rhs.equals(lhs.getLHS())) {
                return new Mul(new Constant(((Constant) lhs.getRHS()).getValue() + 1), rhs);
            }
        } else if (lhs.getRHS() instanceof Variable && lhs.getLHS() instanceof Constant) {
            if (rhs.equals(lhs.getRHS())) {
                return new Mul(new Constant(((Constant) lhs.getLHS()).getValue() + 1), rhs);
            }
        }
        return this;
    }

    private static boolean constOrVar(Expression expr) {
        return (expr instanceof Constant || expr instanceof Variable);
    }

    @Override
    public String toString() {
        return lhs.toString() + " + " + rhs.toString();
    }
}
