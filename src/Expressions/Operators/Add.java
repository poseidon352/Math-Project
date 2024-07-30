package Expressions.Operators;

import Expressions.Constant;
import Expressions.Expression;
import Expressions.Function;

public class Add extends Operator implements Function {
    
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

    // If the lhs is a Constant and the rhs is an Operator
    private Expression rhsOperator() {
        if (this.rhs instanceof Add) {
            return simplifyAdd((Add) rhs, lhs);
        // If the constant value is 0 then return rhs
        }
        return basicSimplification((Constant) lhs, rhs);
    }

    // If the lhs is an Operator and the rhs is a Constant
    private Expression lhsOperator() {
        if (this.lhs instanceof Add) {
            return simplifyAdd((Add) lhs, rhs);
        } 
        return basicSimplification((Constant) rhs, lhs);
    }

    // If the constant value is 0 then return the other side of the Add
    private Expression basicSimplification(Constant constant, Expression expression) {
        if (constant.getValue() == 0) {
            return expression;
        }
        return this;
    }

    // Will reorder the expression if needed, ex. (x+1)+2 --> x+(1+2)
    private Expression simplifyAdd(Add addExpr, Expression secondExpr) {
        if (secondExpr.getClass().equals(addExpr.getRHS().getClass())) {
            return new Add((new Add(secondExpr, addExpr.getRHS())).simplify(), addExpr.getLHS());
        } else if (secondExpr.getClass().equals(addExpr.getLHS().getClass())) {
            return new Add((new Add(secondExpr, addExpr.getLHS())).simplify(), addExpr.getRHS());
        }
        return this;
    }

    private Expression lhsMulRhsMul(Mul firstExpr, Mul secondExpr) {
        int multiplicationOfSecondMul = 1;
        Pow secondExprPow = null;

        if (secondExpr.getLHS() instanceof Constant) {
            multiplicationOfSecondMul = (int) ((Constant) secondExpr.getLHS()).getValue();
            secondExprPow = (Pow) secondExpr.getRHS();
        } else if (secondExpr.getRHS() instanceof Constant) {
            multiplicationOfSecondMul = (int) ((Constant) secondExpr.getRHS()).getValue();
            secondExprPow = (Pow) secondExpr.getLHS();
        }
        if (firstExpr.getRHS() instanceof Constant && firstExpr.getLHS() instanceof Pow) {
            if (secondExprPow.equals(firstExpr.getLHS())) {
                return new Mul(new Constant(((Constant) firstExpr.getRHS()).getValue() + multiplicationOfSecondMul), secondExprPow).simplify();
            }
        } else if (firstExpr.getRHS() instanceof Pow && firstExpr.getLHS() instanceof Constant) {
            if (secondExprPow.equals(firstExpr.getRHS())) {
                return new Mul(new Constant(((Constant) firstExpr.getLHS()).getValue() + multiplicationOfSecondMul), secondExprPow).simplify();
            }
        }
        return this;
    }


    // These methods will reorder the expression so that the above rules can be 
    // directly followed
    private Expression lhsOperatorRhsOperator() {
        if (this.lhs instanceof Add) {
            return addExprSimplification((Add) lhs, rhs).simplify();
        } else if (this.rhs instanceof Add) {
            return addExprSimplification((Add) rhs, lhs).simplify();
        } else if (this.lhs instanceof Mul && this.rhs instanceof Mul) {
            return lhsMulRhsMul((Mul) lhs, (Mul) rhs);
        }
        return this;
    }

    private Expression addExprSimplification(Add addExpr, Expression secondExpr) {
        if (addExpr.getLHS() instanceof Constant) {
            return new Add(new Add(addExpr.getRHS(), secondExpr), addExpr.getLHS());
        } else if (addExpr.getRHS() instanceof Constant) {
            return new Add(new Add(addExpr.getLHS(), secondExpr), addExpr.getRHS());
        }
        return this;
    }

    @Override
    public Expression derivative() {
        return (new Add(((Function) lhs).derivative(), ((Function) rhs).derivative())).simplify();
    }

    @Override
    public Expression image(double x) {
        return (new Add(((Function) lhs).image(x), ((Function) rhs).image(x))).simplify();
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((lhs == null) ? 0 : lhs.hashCode());
        result = prime * result + ((rhs == null) ? 0 : rhs.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Add other = (Add) obj;
        if (lhs == null || rhs == null) {
            return false;
        } else if ((!lhs.equals(other.lhs) || !rhs.equals(other.rhs)) &&
                    (!lhs.equals(other.rhs) || !rhs.equals(other.lhs))) {
            return false;
        }
        return true;
    }
}
