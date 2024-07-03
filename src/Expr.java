import java.util.Arrays;
import java.util.List;

interface Expr {}

class Constant implements Expr {
    double value;
}

class Variable implements Expr {
    String name;
}

class Add implements Expr {
    List<Expr> operands;
}

class Mul implements Expr {
    List<Expr> operands;
}

class Pow implements Expr {
    Expr base;
    Expr exponent;
}

class ExpressionUtils {
    static Constant constant(double val) {
        Constant constant = new Constant();
        constant.value = val;
        return constant;
    }

    static Variable variable(String name) {
        Variable variable = new Variable();
        variable.name = name;
        return variable;
    }

    static Mul neg(Expr arg) {
        return mul(constant(-1), arg);
    }

    static Add add(Expr... ops) {
        Add add = new Add();
        add.operands = Arrays.asList(ops);
        return add;
    }

    static Add sub(Expr lhs, Expr rhs) {
        return add(lhs, neg(rhs));
    }

    static Mul mul(Expr... ops) {
        Mul mul = new Mul();
        mul.operands = Arrays.asList(ops);
        return mul;
    }

    static Mul div(Expr lhs, Expr rhs) {
        return mul(lhs, pow(rhs, constant(-1)));
    }

    static Pow pow(Expr base, Expr exponent) {
        Pow pow = new Pow();
        pow.base = base;
        pow.exponent = exponent;
        return pow;
    }
}

