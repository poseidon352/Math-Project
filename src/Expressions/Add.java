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
        } else if (this.rhs instanceof Sub) {
            return rhsSub().simplify();
        // } else if (this.lhs instanceof Variable && this.rhs instanceof Mul) {
        //     return simplifyMul(rhs, lhs).simplify(); //TODO: remove this if no problems occur
        //TODO: Check if there is a way to abstract the below code with the other +0 code in the basicSimplification method
        } else if (this.lhs instanceof Constant && ((Constant) this.lhs).getValue() == 0) {
            return this.rhs;
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

    private Expression lhsOperator() {
        if (this.lhs instanceof Add) {
            return simplifyAdd(lhs, rhs).simplify();
        } else if (this.lhs instanceof Sub) {
            return lhsSub().simplify();
        // } else if (this.lhs instanceof Mul && this.rhs instanceof Variable) {
        //     return simplifyMul(lhs, rhs).simplify(); //TODO: remove this if no problems occur
        } else if (this.rhs instanceof Constant) {
            return basicSimplification();
        }
        return this;
    }

    private Expression basicSimplification() {
        double rhsValue = ((Constant) this.rhs).getValue();
        if (rhsValue < 0) {
            return (new Sub(lhs, new Constant(rhsValue*(-1))));
        } else if (rhsValue == 0) {
            return this.lhs;
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

    private Expression simplifyAdd(Expression firstExpr, Expression secondExpr) {
        if (firstExpr instanceof Add) {
            Add firstAdd = (Add) firstExpr;
            if (secondExpr.getClass().equals(firstAdd.getRHS().getClass())) {
                Expression newExpr = new Add(secondExpr, firstAdd.getRHS());
                return new Add(newExpr, firstAdd.getLHS());
            } else if (secondExpr.getClass().equals(firstAdd.getLHS().getClass())) {
                Expression newExpr = new Add(secondExpr, firstAdd.getLHS());
                return new Add(newExpr, firstAdd.getRHS());
            } else {
                return simplifyAddAddMul(firstExpr, secondExpr);
            }
        }
        return this;
    }

    // Assuming firstExpr is an Add
    private Expression simplifyAddAddMul(Expression firstExpr, Expression secondExpr) {
        Add firstAdd = (Add) firstExpr;
        if (firstAdd.getLHS() instanceof Constant) {
            return new Add(new Add(secondExpr, firstAdd.getRHS()), firstAdd.getLHS());
        } else if (firstAdd.getRHS() instanceof Constant) {
            return new Add(new Add(secondExpr, firstAdd.getLHS()), firstAdd.getRHS());
        }
        return this;
    }

    private Expression simplifyMul(Expression firstExpr, Expression secondExpr) {
        Mul firstMulExpr = (Mul) firstExpr;
        Mul secondMulExpr = (Mul) secondExpr;
        int multiplicationOfSecondMul = 1;
        Variable secondExprVariable = null;

        if (secondMulExpr.getLHS() instanceof Constant) {
            multiplicationOfSecondMul = (int) ((Constant) secondMulExpr.getLHS()).getValue();
            secondExprVariable = (Variable) secondMulExpr.getRHS();
        } else if (secondMulExpr.getRHS() instanceof Constant) {
            multiplicationOfSecondMul = (int) ((Constant) secondMulExpr.getRHS()).getValue();
            secondExprVariable = (Variable) secondMulExpr.getLHS();
        }
        if (firstMulExpr.getRHS() instanceof Constant && firstMulExpr.getLHS() instanceof Variable) {
            if (secondExprVariable.equals(firstMulExpr.getLHS())) {
                return new Mul(new Constant(((Constant) firstMulExpr.getRHS()).getValue() + multiplicationOfSecondMul), secondExprVariable);
            }
        } else if (firstMulExpr.getRHS() instanceof Variable && firstMulExpr.getLHS() instanceof Constant) {
            if (secondExprVariable.equals(firstMulExpr.getRHS())) {
                return new Mul(new Constant(((Constant) firstMulExpr.getLHS()).getValue() + multiplicationOfSecondMul), secondExprVariable);
            }
        }
        return this;
    }

    /****************************************************************************
    //                               LHS && RHS Operator
    *****************************************************************************/

    // These methods will reorder the expression so that the above rules can be 
    // directly followed
    private Expression lhsOperatorRhsOperator() {
        if (this.lhs instanceof Add) {
            return addExprSimplification(lhs, rhs).simplify();
        } else if (this.rhs instanceof Add) {
            return addExprSimplification(rhs, lhs).simplify();
        } else if (this.lhs instanceof Sub) {
            return lhsSubRhsOperator().simplify();
        } else if (this.rhs instanceof Sub) {
            return lhsOperatorRhsSub().simplify();
        } else if (this.lhs instanceof Mul && this.rhs instanceof Mul) {
            return simplifyMul(lhs, rhs);
        }
        return this;
    }

    private Expression addExprSimplification(Expression firstExpr, Expression secondExpr) {
        if (firstExpr instanceof Add) {
            Add addExpr = (Add) firstExpr;
            if (addExpr.getLHS() instanceof Constant) {
                return new Add(new Add(addExpr.getRHS(), secondExpr), addExpr.getLHS());
            } else if (addExpr.getRHS() instanceof Constant) {
                return new Add(new Add(addExpr.getLHS(), secondExpr), addExpr.getRHS());
            }
        }
        return this;
    }

    private Expression lhsSubRhsOperator() {
        Sub lhs = (Sub) this.lhs;
        if (lhs.getLHS() instanceof Constant && lhs.getRHS() instanceof Variable) {
            return new Add(lhs.getLHS(), new Sub(this.rhs, lhs.getRHS()));
        } else if (lhs.getLHS() instanceof Variable && lhs.getRHS() instanceof Constant) {
            return new Sub(new Add(lhs.getLHS(), this.rhs), lhs.getRHS());
        }
        return this;
    }

    private Expression lhsOperatorRhsSub() {
        Sub rhs = (Sub) this.rhs;
        if (rhs.getLHS() instanceof Constant && rhs.getRHS() instanceof Variable) {
            return new Add(rhs.getLHS(), new Sub(this.lhs, rhs.getRHS()));
        } else if (rhs.getLHS() instanceof Variable && rhs.getRHS() instanceof Constant) {
            return new Sub(new Add(this.lhs, rhs.getLHS()), rhs.getRHS());
        }
        return this;
    }


    // private static boolean constOrVar(Expression expr) {
    //     return (expr instanceof Constant || expr instanceof Variable);
    // } //TODO: remove if no problems occur

    @Override
    public String toString() {
        return lhs.toString() + " + " + rhs.toString();
    }
}
