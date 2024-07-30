package Expressions.Operators;

import Expressions.Constant;
import Expressions.Expression;
import Expressions.Function;

public class Mul extends Operator implements Function {
    
    public Mul(Expression lhs, Expression rhs) {
        super(lhs, rhs);
    }

    @Override
    public Expression simplify() {
        lhs = lhs.simplify();
        rhs = rhs.simplify();
        // If the lhs and rhs are both constants then subtract them to produce a new constant
        if (lhs instanceof Constant && rhs instanceof Constant) {
            double product = ((Constant) lhs).getValue() * ((Constant) rhs).getValue();
            return new Constant(product);
        } else if (lhs instanceof Constant) {
            return oneSideConstant(rhs, (Constant) lhs);
        } else if (rhs instanceof Constant) {
            return oneSideConstant(lhs, (Constant) rhs);
        } else if (lhs instanceof Operator && rhs instanceof Operator) {
            return lhsOperatorRhsOperator();
        }
        return this;
    }

    // private Expression rhsOperator() {
    //     if (((Constant) lhs).getValue() == 0) {
    //         return new Constant(0);
    //     } else if (rhs instanceof Mul) {
    //         return simplifyMul((Mul) rhs, lhs).simplify();
    //     }
    //     return this;
    // }

    // private Expression lhsOperator() {
    //     if (((Constant) rhs).getValue() == 0) {
    //         return new Constant(0);
    //     } else if (lhs instanceof Mul) {
    //         return simplifyMul((Mul) lhs, rhs).simplify();
    //     }
    //     return this;
    // }

    private Expression oneSideConstant(Expression expression, Constant constant) {
        if (constant.getValue() == 0) {
            return new Constant(0);
        } else if (expression instanceof Mul) {
            return simplifyMul((Mul) expression, constant);
        }
        return this;
    }

    private Expression simplifyMul(Mul firstExpr, Expression secondExpr) {
        if (secondExpr.getClass().equals(firstExpr.getRHS().getClass())) {
            return new Mul(new Mul(secondExpr, firstExpr.getRHS()), firstExpr.getLHS()).simplify();
        } else if (secondExpr.getClass().equals(firstExpr.getLHS().getClass())) {
            return new Mul(new Mul(secondExpr, firstExpr.getLHS()), firstExpr.getRHS()).simplify();
        }
        return this;
    }

    private Expression lhsOperatorRhsOperator() {
        if (lhs instanceof Mul && rhs instanceof Mul) {
            return lhsMulRhsMul((Mul) lhs, (Mul) rhs).simplify();
        } else if (rhs instanceof Pow && rhs.baseEquals(lhs)) {
            return powLhsEqualRhs((Pow) rhs, lhs).simplify();
        } else if (lhs instanceof Pow && lhs.baseEquals(rhs)) {
            return powLhsEqualRhs((Pow) lhs, rhs).simplify();
        }
        return this;
    }

    private Expression lhsMulRhsMul(Mul firstExpr, Mul secondExpr) {
        double multiplicationOfSecondMul = 1;
        double powerOfSecondMul = 1;
        Pow secondExprPow = null;

        if (secondExpr.getLHS() instanceof Constant) {
            multiplicationOfSecondMul = ((Constant) secondExpr.getLHS()).getValue();
            secondExprPow = (Pow) secondExpr.getRHS();
            powerOfSecondMul = ((Constant) secondExprPow.getRHS()).getValue();
        } else if (secondExpr.getRHS() instanceof Constant) {
            multiplicationOfSecondMul = ((Constant) secondExpr.getRHS()).getValue();
            secondExprPow = (Pow) secondExpr.getLHS();
            powerOfSecondMul = ((Constant) secondExprPow.getRHS()).getValue();
        }
        if (firstExpr.getRHS() instanceof Constant && firstExpr.getLHS() instanceof Pow) {
            Pow firstMulExprLhs = (Pow) firstExpr.getLHS();
            if (secondExprPow.getLHS().equals(firstMulExprLhs.getLHS())) {
                return new Mul(new Constant(((Constant) firstExpr.getRHS()).getValue() * multiplicationOfSecondMul), 
                new Pow(secondExprPow.getLHS(), new Constant(((Constant) firstMulExprLhs.getRHS()).getValue() + powerOfSecondMul)));
            }
        } else if (firstExpr.getRHS() instanceof Pow && firstExpr.getLHS() instanceof Constant) {
            Pow firstMulExprRhs = (Pow) firstExpr.getRHS();
            if (secondExprPow.getLHS().equals(firstMulExprRhs.getLHS())) {
                return new Mul(new Constant(((Constant) firstExpr.getLHS()).getValue() * multiplicationOfSecondMul), 
                new Pow(secondExprPow.getLHS(), new Constant(((Constant) firstMulExprRhs.getRHS()).getValue() + powerOfSecondMul)));
            }
        }
        return this;
    }

    // If there is a Pow and both expressions are equals (excluding powers they are raised to)
    private Expression powLhsEqualRhs(Pow powExpr, Expression expression) {
        double powerOfFirstPow = ((Constant) powExpr.getRHS()).getValue();
        double powerOfSecondExpr = 1;
        double newRhsValue = powerOfFirstPow + powerOfSecondExpr;
        if (expression instanceof Pow) {
            Pow secondPow = (Pow) expression;
            powerOfSecondExpr = ((Constant) secondPow.getRHS()).getValue();
            newRhsValue = powerOfFirstPow + powerOfSecondExpr;
            return new Pow(powExpr.getLHS(), new Constant(newRhsValue));
        }
        return new Pow(powExpr.getLHS(), new Constant(newRhsValue));
    }

    @Override
    public String toString() {

        if (this.lhs instanceof Constant){
            double lhsValue = ((Constant) this.lhs).getValue();
            if (lhsValue == 1) {
                return this.rhs.toString();
            } else if (lhsValue == -1) {
                return "-" + this.rhs.toString();
            }
        } else if (this.rhs instanceof Constant){
            double rhsValue = ((Constant) this.rhs).getValue();
            if (rhsValue == 1) {
                return this.lhs.toString();
            } else if (rhsValue == -1) {
                return "-" + this.lhs.toString();
            }
        }

        String lhsString = this.lhs.toString();
        String rhsString = this.rhs.toString();

        if (lhs instanceof Add || lhs instanceof Sub) {
            lhsString = "(" + this.lhs.toString() + ")";
        }

        
        if (rhs instanceof Add || rhs instanceof Sub) {
            rhsString = "(" + this.rhs.toString() + ")";
        }
        return lhsString + rhsString;
    }

    // Product rule
    @Override
    public Expression derivative() {
        Expression lhsDerivative = ((Function) lhs).derivative();
        Expression rhsDerivative = ((Function) rhs).derivative();
        return (new Add(new Mul(lhs, rhsDerivative), new Mul(lhsDerivative, rhs))).simplify();
    }

    @Override
    public Expression image(double x) {
        return (new Mul(((Function) lhs).image(x), ((Function) rhs).image(x))).simplify();
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
        Mul other = (Mul) obj;
        if (lhs == null || rhs == null) {
            return false;
        } else if ((!lhs.equals(other.lhs) || !rhs.equals(other.rhs)) &&
                    (!lhs.equals(other.rhs) || !rhs.equals(other.lhs))) {
            return false;
        }
        return true;
    }
}
