package Expressions.Operators;

import Expressions.Constant;
import Expressions.Expression;
import Expressions.Variable;

public abstract class Operator implements Expression {
    protected Expression lhs;
    protected Expression rhs;

    public Operator(Expression lhs, Expression rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    public Expression getLHS() {
        return this.lhs;
    }

    public Expression getRHS() {
        return this.rhs;
    }

    @Override
    public boolean hasVariable() {
        if (lhs.hasVariable() || rhs.hasVariable()) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isVariable() {
        if (this instanceof Mul) {
            Mul object = (Mul) this;
            if (object.getLHS() instanceof Pow && object.getRHS().equals(new Constant(1))) {
                Pow objectLhs = (Pow) object.getLHS();
                if (objectLhs.getLHS() instanceof Variable && objectLhs.getRHS().equals(new Constant(1))) {
                    return true;
                }
            } else if (object.getRHS() instanceof Pow && object.getLHS().equals(new Constant(1))) {
                Pow objectRhs = (Pow) object.getRHS();
                if (objectRhs.getLHS() instanceof Variable && objectRhs.getRHS().equals(new Constant(1))) {
                    return true;
                }
            }
        } else if (this instanceof Pow) {
            Pow object = (Pow) this;
            if (object.getLHS() instanceof Variable && object.getRHS().equals(new Constant(1))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean baseEquals(Object obj) {
        if (obj instanceof Pow) {
            Expression lhs = ((Pow) obj).getLHS();
            return lhs.baseEquals(this);
        }
        return equals(obj);
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
        Operator other = (Operator) obj;
        if (lhs == null) {
            if (other.lhs != null)
                return false;
        } else if (!lhs.equals(other.lhs))
            return false;
        if (rhs == null) {
            if (other.rhs != null)
                return false;
        } else if (!rhs.equals(other.rhs))
            return false;
        return true;
    }  
}