import Expressions.*;

public class Tokenizer {
    private int currentPosition;
    private int maxPosition;
    private String str;
    // private Map<String, Integer> operatorMap;

    public Tokenizer(String str) {
        currentPosition = 0;
        this.str = str.replaceAll("\\s","");
        maxPosition = this.str.length();
        // operatorMap = constructOperatorMap();
    }

    public String getString() {
        return str;
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(int n) {
        currentPosition = n;
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

    public boolean hasMoreTokens() {
        return maxPosition > currentPosition;
    }

    public String peek() {
        return Character.toString(str.charAt(currentPosition));
    }

    public String peek(int n) {
        String str = "";
        for (int i = 0; i < n; i++) {
            str += Character.toString(str.charAt(currentPosition + i));
        }
        return str;
    }

    public String consume() {
        currentPosition++;
        return Character.toString(str.charAt(currentPosition - 1));
    }

    public String consume(int n) {
        currentPosition += n;
        return peek(currentPosition - n);
    }

    public String toString() {
        String str = "";
        while (hasMoreTokens()) {
            String next = consume();
            if (isNumeric(next)) {
                str += "Integer: " + next + "; ";
            } else if (isLetter(next)) {
                str += "Letter: " + next + "; ";
            } else if (isOperator(next)) {
                str += "Operator: " + next + "; ";
            }
        }
        currentPosition = 0;
        return str;
    }

    // public List<String> tokenize() {
    //     List<String> tokens = new ArrayList<>();
    //     while (hasMoreTokens()) {
    //         String next = consume();
    //         if (isNumeric(next)) {
    //             tokens.add("num");
    //         } else if (isLetter(next)) {
    //             tokens.add("var");
    //         } else if (isOperator(next)) {
    //             switch (next) {
    //                 case "+":
    //                     tokens.add("plus");
    //                     break;
    //                 case "*":
    //                     tokens.add("mult");
    //                     break;
    //                 default:
    //                     break;
    //             }
    //         }
    //     }
    //     return tokens;
    // }

    public static Expression parse(Tokenizer tokenizer) {
        String str = tokenizer.getString();
        if (isLetter(str)) {
            return new Variable(str);
        } else if (isNumeric(str)) {
            return new Constant(Double.parseDouble(str));
        } else if (str.length() != 0) {
            int firstOperator = findFirstOperator(tokenizer);
            return parseOperator(new Tokenizer(str.substring(0, firstOperator)), 
                Character.toString(str.charAt(firstOperator)), 
                new Tokenizer(str.substring(firstOperator + 1)));
        }
        return null;
    }

    private static Expression parseOperator(Tokenizer lhs, String operator, Tokenizer rhs) {
        switch (operator) {
            case "+":
                return new Add(parse(lhs), parse(rhs));
            case "-":
                return new Sub(parse(lhs), parse(rhs));
            case "*":
                return new Mul(parse(lhs), parse(rhs));
            case "/":
                return new Div(parse(lhs), parse(rhs));
            case "^":
                return new Pow(parse(lhs), parse(rhs));
            default:
                return null;
        }
    }
    private static int findFirstOperator(Tokenizer tokenizer) {
        int originalPosition = tokenizer.getCurrentPosition();
        int indexOfOperator = 0;
        while (tokenizer.hasMoreTokens()) {
            if (isOperator(tokenizer.consume())) {
                tokenizer.setCurrentPosition(originalPosition);
                return indexOfOperator;
            } else {
                indexOfOperator++;
            }
        }
        return indexOfOperator;
    }

    public static void main(String[] args) {
        Tokenizer tokenizer = new Tokenizer("1 + a * 3 / 5");
        Expression parsedExpression = parse(tokenizer);
        System.out.print(parsedExpression.toString());
    }
    /*
    Version 1:
    Look at all the tokens, if a token is an operator, add it to a list with the type of operator and position in the string,
    then sort the list according to bedmas and construct the expression tree
    
    Version 2:
    Iterate through the string until an operator is found

    1 + 2*3 /6 - 2

    To parse, use recursion, find the first operator then create the Operator object with lhs and rhs being calls
    to the same parse function
    */
}
