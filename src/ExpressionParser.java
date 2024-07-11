import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import Expressions.*;

import java.lang.Character;

public class ExpressionParser {

    private static boolean isDigit(char c) {
        return Character.isDigit(c);
    }

    private static boolean isLetter(char c) {
        return Character.isLetter(c);
    }

    // Operator having higher precedence
    // value will be returned
    static int getPrecedence(char ch) {

        if (ch == '+' || ch == '-')
            return 1;
        else if (ch == '*' || ch == '/')
            return 2;
        else if (ch == '^')
            return 3;
        else
            return -1;
    }
    
      // Operator has Left --> Right associativity
      static boolean hasLeftAssociativity(char ch) {
        if (ch == '+' || ch == '-' || ch == '/' || ch == '*') {
            return true;
        } else {
            return false;
        }
    }
  
    // Method converts  given infix to postfix expression
    static List<String> infixToRpn(String expression) {
        // Initialising an empty String
        // (for output) and an empty stack
        Stack<Character> stack = new Stack<>();

        // Initializing the Stack for the expression that can take multi digit numbers
        List<String> outputList = new ArrayList<>();
        

        // Iterating over tokens using inbuilt
        // .length() function
        for (int i = 0; i < expression.length(); ++i) {
            // Finding character at 'i'th index
            char c = expression.charAt(i);
            if (isLetter(c)) {
                outputList.add(Character.toString(c));
            }

            else if (isDigit(c)) {
                String number = "";
                int increment = 0;
                while (i + increment < expression.length() && isDigit(expression.charAt(i + increment))) {
                    number += Character.toString(expression.charAt(i + increment));
                    increment++;
                }
                outputList.add(number);
                i += increment - 1;
            }

            // If the scanned Token is an '('
            // push it to the stack
            else if (c == '(') {
                stack.push(c);
            }

            // If the scanned Token is an ')' pop and append
            // it to output from the stack until an '(' is
            // encountered
            else if (c == ')') {
                while (!stack.isEmpty() && stack.peek() != '(') {
                    outputList.add(Character.toString(stack.pop()));
                }

                stack.pop();
            }

            // If an operator is encountered then taken the
            // further action based on the precedence of the
            // operator

            else {
                while (!stack.isEmpty()
                        && getPrecedence(c) <= getPrecedence(stack.peek()) 
                        && hasLeftAssociativity(c)) {
                    outputList.add(Character.toString(stack.pop()));
                }
                stack.push(c);
            }
        }

        // pop all the remaining operators from
        // the stack and append them to output
        while (!stack.isEmpty()) {
            // if (stack.peek() == '(') {
            //     return "This expression is invalid";
            // }
            outputList.add(Character.toString(stack.pop()));
        }
        return outputList;
    }

    public static boolean isNumeric(String str) {
        return str.matches("\\d*");
    }

    public static boolean isLetter(String str) {
        return str.matches("[a-z]");
    }

    public static boolean isOperator(String str) {
        return str.matches("[+-/*///^()]");
    }

    static Expression rpnToExpression(List<String> rpn) {
    
        Stack<Expression> stack = new Stack<>();

        for (int i = 0; i < rpn.size(); ++i) {
            String c = rpn.get(i);

            if (isNumeric(c)) {
                stack.push(new Constant(Double.parseDouble(c)));
            } else if (isLetter(c)) {
                stack.push(new Variable(c));
            } else if (isOperator(c)) {
                parseOperator(stack, c);
            }
        }

        return stack.pop();
    }

    static void parseOperator(Stack<Expression> stack, String c) {
        Expression rhs = stack.pop();
        Expression lhs = stack.pop();
        switch (c) {
            case "+":
                stack.push(new Add(lhs, rhs));
                break;
            case "-":
                stack.push(new Sub(lhs, rhs));
                break;
            case "*":
                stack.push(new Mul(lhs, rhs));
                break;
            case "/":
                stack.push(new Div(lhs, rhs));
                break;
            case "^":
                stack.push(new Pow(lhs, rhs));
                break;
            default:
                // TODO: create exception
                break;
        }
    }
    public static void main(String[] args)
    {
        // Considering random infix string notation
        String expression = "1+2";
        Expression expression2 = rpnToExpression(infixToRpn(expression));
        System.out.println(expression2.toString());

    }
}