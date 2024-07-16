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
        // If the lhs is a Constant and the rhs is an Operator
        } else if (this.lhs instanceof Constant && this.rhs instanceof Operator) {
            return rhsOperator();
        // If the lhs is an Operator and the rhs is a Constant
        } else if (this.lhs instanceof Operator && this.rhs instanceof Constant) {
            return lhsOperator();
        } else if (this.lhs instanceof Operator && this.rhs instanceof Operator) {
            return lhsOperatorRhsOperator();
        }
        return this;
    }

    private Expression rhsOperator() {
        if (this.rhs instanceof Add) {
            return simplifyAdd(rhs, lhs).simplify();
        //TODO: Check if there is a way to abstract the below code with the other +0 code in the basicSimplification method
        } else if (((Constant) this.lhs).getValue() == 0) {
            return this.rhs;
        }
        return this;
    }

    private Expression lhsOperator() {
        if (this.lhs instanceof Add) {
            return simplifyAdd(lhs, rhs).simplify();
        } 
        return basicSimplification();
    }

    private Expression basicSimplification() {
        double rhsValue = ((Constant) this.rhs).getValue();
        /*  if (rhsValue < 0) {
             return (new Sub(lhs, new Constant(rhsValue*(-1))));
        } else */ if (rhsValue == 0) {
            return this.lhs;
        }
        return this;
    }

    private Expression simplifyAdd(Expression firstExpr, Expression secondExpr) {
        Add firstAdd = (Add) firstExpr;
        if (secondExpr.getClass().equals(firstAdd.getRHS().getClass())) {
            return new Add(new Add(secondExpr, firstAdd.getRHS()), firstAdd.getLHS());
        } else if (secondExpr.getClass().equals(firstAdd.getLHS().getClass())) {
            return new Add(new Add(secondExpr, firstAdd.getLHS()), firstAdd.getRHS());
        }
        return this;
    }

    private Expression lhsMulRhsMul(Expression firstExpr, Expression secondExpr) {
        Mul firstMulExpr = (Mul) firstExpr;
        Mul secondMulExpr = (Mul) secondExpr;
        int multiplicationOfSecondMul = 1;
        Pow secondExprPow = null;

        if (secondMulExpr.getLHS() instanceof Constant) {
            multiplicationOfSecondMul = (int) ((Constant) secondMulExpr.getLHS()).getValue();
            secondExprPow = (Pow) secondMulExpr.getRHS();
        } else if (secondMulExpr.getRHS() instanceof Constant) {
            multiplicationOfSecondMul = (int) ((Constant) secondMulExpr.getRHS()).getValue();
            secondExprPow = (Pow) secondMulExpr.getLHS();
        }
        if (firstMulExpr.getRHS() instanceof Constant && firstMulExpr.getLHS() instanceof Pow) {
            if (secondExprPow.equals(firstMulExpr.getLHS())) {
                return new Mul(new Constant(((Constant) firstMulExpr.getRHS()).getValue() + multiplicationOfSecondMul), secondExprPow).simplify();
            }
        } else if (firstMulExpr.getRHS() instanceof Pow && firstMulExpr.getLHS() instanceof Constant) {
            if (secondExprPow.equals(firstMulExpr.getRHS())) {
                return new Mul(new Constant(((Constant) firstMulExpr.getLHS()).getValue() + multiplicationOfSecondMul), secondExprPow).simplify();
            }
        }
        return this;
    }


    // These methods will reorder the expression so that the above rules can be 
    // directly followed
    private Expression lhsOperatorRhsOperator() {
        if (this.lhs instanceof Add) {
            return addExprSimplification(lhs, rhs).simplify();
        } else if (this.rhs instanceof Add) {
            return addExprSimplification(rhs, lhs).simplify();
        } else if (this.lhs instanceof Mul && this.rhs instanceof Mul) {
            return lhsMulRhsMul(lhs, rhs);
        }
        return this;
    }

    private Expression addExprSimplification(Expression firstExpr, Expression secondExpr) {
        Add addExpr = (Add) firstExpr;
        if (addExpr.getLHS() instanceof Constant) {
            return new Add(new Add(addExpr.getRHS(), secondExpr), addExpr.getLHS());
        } else if (addExpr.getRHS() instanceof Constant) {
            return new Add(new Add(addExpr.getLHS(), secondExpr), addExpr.getRHS());
        }
        return this;
    }

    @Override
    public String toString() {
        if (this.rhs instanceof Constant && ((Constant) this.rhs).getValue() < 0) {
            Constant newRhs = new Constant(((Constant) this.rhs).getValue() * (-1));
            return lhs.toString() + " - " + newRhs.toString();
        } else if (this.lhs instanceof Constant && this.rhs instanceof Mul) {
            return (new Add(rhs, lhs)).toString();
        }
        return lhs.toString() + " + " + rhs.toString();
    }
}
