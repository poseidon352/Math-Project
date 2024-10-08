package Expressions;

public class Variable implements Expression, AbstractFunction {
    String name;

    public Variable(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public Expression simplify() {
        return this;
    }

    @Override
    public boolean hasVariable() {
        return true;
    }

    @Override
    public boolean isVariable() {
        return true;
    }

    @Override
    public boolean baseEquals(Object obj) {
        return equals(obj);
    }

    @Override
    public AbstractFunction derivative() {
        return new Constant(1);
    }

    @Override
    public Expression image(double x) {
        return new Constant(x);
    }

    @Override
    public boolean isPolynomial() {
        return true;
    }

    @Override
    public boolean containsSameExpression(Expression expression) {
        if (expression instanceof Variable) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
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
        Variable other = (Variable) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

    
}
