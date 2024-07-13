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
      // TODO: Check that this is true for * and /
      static boolean hasLeftAssociativity(char ch) {
        if (ch == '+' || ch == '-' || ch == '/' || ch == '*') {
            return true;
        } else {
            return false;
        }
    }
  
    // Method converts  given infix to reverse Polish Noation
    static List<String> infixToRpn(String expression) {
        //Removes any whitespace in the string
        expression = expression.replaceAll("\\s+","");
        Stack<Character> stack = new Stack<>();
        List<String> output = new ArrayList<>();
        

        // Iterating over tokens in the expression string
        for (int i = 0; i < expression.length(); ++i) {
            // Finding character at 'i'th index
            char c = expression.charAt(i);
            // If the token is a letter (a variable), then add it to the output
            if (isLetter(c)) {
                output.add(Character.toString(c));
            }

            // If the token is a digit
            else if (isDigit(c)) {
                // While tokens remain and the next token is still a digit or a decimal point (max one)
                // add the digits to a string that will be added to the List all at once.
                // The increment will update the index in the string with the length of the number
                // TODO: Check that there is at most one decimal point
                String number = "";
                int increment = 0;
                while (i + increment < expression.length() 
                    && (isDigit(expression.charAt(i + increment)) 
                        || (Character.toString((expression.charAt(i + increment))).equals(".")))) {
                    number += Character.toString(expression.charAt(i + increment));
                    increment++;
                }
                output.add(number);
                i += increment - 1;
            }

            // If the scanned Token is an '(', push it to the stack
            else if (c == '(') {
                stack.push(c);
            }

            // If the scanned Token is an ')' pop and add it to output from 
            // the stack until an '(' is encountered
            else if (c == ')') {
                while (!stack.isEmpty() && stack.peek() != '(') {
                    output.add(Character.toString(stack.pop()));
                }
                // Remove the '(' from the stack
                stack.pop();
            }

            // If an operator is encountered then further action is taken
            // based on the precedence of the operator
            else {
                while (!stack.isEmpty()
                        && getPrecedence(c) <= getPrecedence(stack.peek()) 
                        && hasLeftAssociativity(c)) {
                    output.add(Character.toString(stack.pop()));
                }
                stack.push(c);
            }
        }

        // pop all the remaining operators from the stack and add them to output
        while (!stack.isEmpty()) {
            // TODO: Throw an error if this happens
            // if (stack.peek() == '(') {
            //     return "This expression is invalid";
            // }
            output.add(Character.toString(stack.pop()));
        }
        return output;
    }

    // Checks if a string is a number (can be a decimal number)
    public static boolean isNumeric(String str) {
        return str.matches("\\d*(.\\d+)?");
    }

    // Checks if the string contains a single letter
    public static boolean isLetter(String str) {
        return str.matches("[a-z]");
    }

    // Check if the string contains a single operator or parenthesis
    public static boolean isOperator(String str) {
        return str.matches("[+-/*///^()]");
    }

    // Converts the algebraic expresion from RPN in a list to an Expression
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
        String expression = "1+6";
        List<String> rpn = infixToRpn(expression);
        Expression expression2 = rpnToExpression(rpn);
        for (String token : rpn) {
            System.out.print(token + " ");
        }
        System.out.println();
        System.out.println(expression2.toString());
        System.out.println(expression2.simplify().toString());
    }
}