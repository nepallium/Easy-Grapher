package Model;

import java.util.ArrayList;
import java.util.Set;

public class Token {
    public Type type;
    public String value;
    public int precedence;
    public int arity; // 1 = unary, 2 = binary
    public boolean isRightAssociative;
    public static final Set<String> functions = Set.of("sin", "cos", "tan", "sqrt", "ln", "abs", "asin", "acos", "atan", "sec", "csc", "cot");
    public static final Set<String> operators = Set.of("+", "-", "*", "/", "^", "~");

    public Token(Type type, String value, int precedence, int arity, boolean isRightAssociative) {
        this.type = type;
        this.value = value;
        this.precedence = precedence;
        this.arity = arity;
        this.isRightAssociative = isRightAssociative;
    }

    public enum Type {
        NUMBER, OPERATOR, FUNCTION, VARIABLE, PARENTHESIS, CONSTANT
    }

    public static Token create(String piece, Token prevToken) {

        if (piece.length() == 1) {
            char chr = piece.charAt(0);

            if (chr == 'x' || chr == 'y') { // TODO also check for other variables if needed
                return new Token(Type.VARIABLE, piece, 0, 0, false);
            } else if (chr == 'e') {
                return new Token(Type.CONSTANT, String.valueOf(Math.E), 0, 0, false);
            } else if (chr == '(' || chr == ')') {
                return new Token(Type.PARENTHESIS, piece, -1, 0, false);
            } else if (operators.contains(piece)) {
                // '-' is a unary negation if prevToken is null (ie its the first operation of the expression)
                // or if the prev token is '('
                // or if prev is binary operator
                // or if prev is function, similar to '('
                if (prevToken == null || operators.contains(prevToken.value) || prevToken.value.equals("(")) {
                    if (piece.equals("-")) {
                        return new Token(Type.OPERATOR, "~", 4, 1, true);
                    } else if (piece.equals("+")) {
                        return null;
                    }
                }
                return new Token(Type.OPERATOR, piece, getPrecedence(piece), getArity(piece), isRightAssociative(piece));
            } else {
                try {
                    Double.parseDouble(piece);
                    return new Token(Type.NUMBER, piece, 0, 0, false);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Could not tokenize piece: " + piece);
                }
            }
        } else {
            try {
                Double.parseDouble(piece);
                return new Token(Type.NUMBER, piece, 0, 0, false);
            } catch (NumberFormatException e) {
                if (functions.contains(piece)) {
                    return new Token(Type.FUNCTION, piece, 4, 1, true);
                } else if (piece.equals("pi")) {
                    return new Token(Type.CONSTANT, String.valueOf(Math.PI), 0, 0, false);
                } else {
                    throw new IllegalArgumentException("Could not tokenize piece: " + piece);
                }
            }
        }
    }


    /**
     * Check if operator is right-associative
     *
     * @param operator the operator str
     * @return whether the operator is right-associative
     */
    private static boolean isRightAssociative(String operator) {
        if (operator == null) {
            return false;
        }

        return switch (operator) {
            case "^", "~" -> true;
            default -> false;
        };
    }

    /**
     * Returns precedence of operators
     *
     * @param operator operator str
     * @return the precedence
     */
    private static int getPrecedence(String operator) {
        return switch (operator) {
            case "~" -> 4;
            case "^" -> 3;
            case "*", "/" -> 2;
            case "+", "-" -> 1;
            default -> -1;
        };
    }

    /**
     * Returns the arity, or num of operands a function/operation takes
     *
     * @param operator operator str
     * @return the arity, 1 or 2
     */
    private static int getArity(String operator) {
        return switch (operator) {
            case "~" -> 1;
            default -> 2;
        };
    }

    public static double calculate(Token token, double[] args) throws Exception {
        double n1 = args[0];
        double n2 = token.arity == 2 ? args[1] : 0;
        return switch (token.value) {
            case "~" -> -n1;
            case "+" -> n1 + n2;
            case "-" -> n1 - n2;
            case "*" -> n1 * n2;
            case "/" -> n1 / n2;
            case "^" -> Math.pow(n1, n2);
            case "sin" -> Math.sin(n1);
            case "cos" -> Math.cos(n1);
            case "tan" -> Math.tan(n1);
            case "asin" -> Math.asin(n1);
            case "acos" -> Math.acos(n1);
            case "atan" -> Math.atan(n1);
            case "sec" -> 1 / Math.cos(n1);
            case "csc" -> 1 / Math.sin(n1);
            case "cot" -> 1 / Math.tan(n1);
            case "ln" -> Math.log(n1);
            case "sqrt" -> Math.sqrt(n1);
            case "abs" -> Math.abs(n1);
            default -> throw new Exception("Could not calculate values");
        };
    }

    //    functions used during testing
    public static String[] toStrArray(Token[] tokens) {
        if (tokens == null) {
            return new String[0];
        }
        String[] arr = new String[tokens.length];
        for (int i = 0; i < tokens.length; i++) {
            Token t = tokens[i];
            arr[i] = t == null ? "" : t.value;
        }
        return arr;
    }

    public static String[] toStrArray(ArrayList<Token> tokenList) {
        if (tokenList == null) return new String[0];
        String[] arr = new String[tokenList.size()];
        for (int i = 0; i < tokenList.size(); i++) {
            Token t = tokenList.get(i);
            arr[i] = t == null ? "" : t.value;
        }
        return arr;
    }

    public static Token[] fromStrArray(String[] arr) {
        if (arr == null) return new Token[0];
        Token[] tokens = new Token[arr.length];
        for (int i = 0; i < arr.length; i++) {
            String s = arr[i];

            // Numbers
            try {
                Double.parseDouble(s);
                tokens[i] = new Token(Type.NUMBER, s, 0, 0, false);
                continue;
            } catch (NumberFormatException ignored) {
            }

            // Operators / functions
            if (operators.contains(s)) {
                int arity = getArity(s);
                int precedence = getPrecedence(s);
                boolean rightAssoc = isRightAssociative(s);
                Token.Type type = (arity == 1 || functions.contains(s)) ? Type.FUNCTION : Type.OPERATOR;
                tokens[i] = new Token(type, s, precedence, arity, rightAssoc);
                continue;
            }

            // Variables
            if (s.equals("x") || s.equals("y")) {
                tokens[i] = new Token(Type.VARIABLE, s, 0, 0, false);
                continue;
            }

            throw new IllegalArgumentException("Cannot create token from string: " + s);
        }
        return tokens;
    }

}
