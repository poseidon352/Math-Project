package Expressions;

public interface Expression {
    Expression simplify();
    String toString();
    boolean hasVariable();
    boolean isVariable();
    boolean baseEquals(Object obj);
    
}