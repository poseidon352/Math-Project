package Expressions;

public class Mul extends Operator {
    
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
            return rhsOperator();
        } else if (rhs instanceof Constant) {
            return lhsOperator();
        } else if (lhs instanceof Operator && rhs instanceof Operator) {
            return lhsOperatorRhsOperator();
        }
        return this;
    }

    private Expression rhsOperator() {
        if (((Constant) lhs).getValue() == 0) {
            return new Constant(0);
        } else if (rhs instanceof Mul) {
            return simplifyMul(rhs, lhs).simplify();
        }
        return this;
    }

    private Expression lhsOperator() {
        if (((Constant) rhs).getValue() == 0) {
            return new Constant(0);
        } else if (lhs instanceof Mul) {
            return simplifyMul(lhs, rhs).simplify();
        }
        return this;
    }

    private Expression simplifyMul(Expression firstExpr, Expression secondExpr) {
        Mul firstMul = (Mul) firstExpr;
        if (secondExpr.getClass().equals(firstMul.getRHS().getClass())) {
            return new Mul(new Mul(secondExpr, firstMul.getRHS()), firstMul.getLHS());
        } else if (secondExpr.getClass().equals(firstMul.getLHS().getClass())) {
            return new Mul(new Mul(secondExpr, firstMul.getLHS()), firstMul.getRHS());
        }
        return this;
    }

    private Expression lhsOperatorRhsOperator() {
        if (lhs instanceof Mul && rhs instanceof Mul) {
            return lhsMulRhsMul(lhs, rhs).simplify();
        }
        return this;
    }

    private Expression lhsMulRhsMul(Expression firstExpr, Expression secondExpr) {
        Mul firstMulExpr = (Mul) firstExpr;
        Mul secondMulExpr = (Mul) secondExpr;
        int multiplicationOfSecondMul = 1;
        int powerOfSecondMul = 1;
        Pow secondExprPow = null;

        if (secondMulExpr.getLHS() instanceof Constant) {
            multiplicationOfSecondMul = (int) ((Constant) secondMulExpr.getLHS()).getValue();
            secondExprPow = (Pow) secondMulExpr.getRHS();
            powerOfSecondMul = (int) ((Constant) secondExprPow.getRHS()).getValue();
        } else if (secondMulExpr.getRHS() instanceof Constant) {
            multiplicationOfSecondMul = (int) ((Constant) secondMulExpr.getRHS()).getValue();
            secondExprPow = (Pow) secondMulExpr.getLHS();
            powerOfSecondMul = (int) ((Constant) secondExprPow.getRHS()).getValue();
        }
        if (firstMulExpr.getRHS() instanceof Constant && firstMulExpr.getLHS() instanceof Pow) {
            Pow firstMulExprLhs = (Pow) firstMulExpr.getLHS();
            if (secondExprPow.getLHS().equals(firstMulExprLhs.getLHS())) {
                return new Mul(new Constant(((Constant) firstMulExpr.getRHS()).getValue() * multiplicationOfSecondMul), 
                new Pow(secondExprPow, new Constant(((Constant) firstMulExprLhs.getRHS()).getValue() + powerOfSecondMul)));
            }
        } else if (firstMulExpr.getRHS() instanceof Pow && firstMulExpr.getLHS() instanceof Constant) {
            Pow firstMulExprRhs = (Pow) firstMulExpr.getRHS();
            if (secondExprPow.getLHS().equals(firstMulExprRhs.getLHS())) {
                return new Mul(new Constant(((Constant) firstMulExpr.getLHS()).getValue() * multiplicationOfSecondMul), 
                new Pow(secondExprPow, new Constant(((Constant) firstMulExprRhs.getRHS()).getValue() + powerOfSecondMul)));
            }
        }
        return this;
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
}
