/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

import java.util.*;

import static java.lang.Double.parseDouble;

/**
 * @author Alex
 */
public class PostfixEvaluator {
    private final static String UNARY_SUB = "~";
    private final static String ADD = "+";
    private final static String SUBTRACT = "-";
    private final static String MULTIPLY = "*";
    private final static String DIVIDE = "/";
    private final static String EXPONENT = "^";
    private final static String LN = "ln";
    private final static String SIN = "sin";
    private final static String COS = "cos";
    private final static String TAN = "tan";
    private final static String ASIN = "asin";
    private final static String ACOS = "acos";
    private final static String ATAN = "atan";
    private final static String SQRT = "sqrt";
    private final static String ABS = "abs";
    private final static List<String> operators = Arrays.asList(
            UNARY_SUB, ADD, SUBTRACT, MULTIPLY, DIVIDE,
            EXPONENT, LN,
            SIN, COS, TAN,
            ASIN, ACOS, ATAN,
            SQRT, ABS
    );

    public ArrayList<Token> tokenize(String exprStr) {
        if (exprStr == null || exprStr.isEmpty()) {
            return null;
        }

        ArrayList<String> expr = splitIntoPieces(exprStr);
        ArrayList<Token> tokens = new ArrayList<>();
        Token prevToken = null;

        for (String piece : expr) {
            Token token = Token.create(piece, prevToken);
            if (token != null) {

                if (prevToken != null) {
                    boolean prevIsNum = prevToken.type == Token.Type.NUMBER;
                    boolean needMult = isNeedMult(prevToken, token, prevIsNum);

                    if (needMult) {
                        tokens.add(Token.create("*", null));
                    }
                }

                tokens.add(token);
                prevToken = token;
            }
        }

        return tokens;
    }

    private static boolean isNeedMult(Token prevToken, Token token, boolean prevIsNum) {
        boolean prevIsVar = prevToken.type == Token.Type.VARIABLE;
        boolean prevIsCloseParen = prevToken.type == Token.Type.PARENTHESIS && prevToken.value.equals(")");
        boolean prevIsFunc = prevToken.type == Token.Type.FUNCTION;

        boolean currIsNum = token.type == Token.Type.NUMBER;
        boolean currIsVar = token.type == Token.Type.VARIABLE;
        boolean currIsOpenParen = token.type == Token.Type.PARENTHESIS && token.value.equals("(");
        boolean currIsFunc = token.type == Token.Type.FUNCTION;

        boolean needMult =
                // 3x, 3(x), 3sin(x)
                (prevIsNum && (currIsVar || currIsOpenParen || currIsFunc)) ||

                        // x3, x(x), x sin(x)
                        (prevIsVar && (currIsOpenParen || currIsFunc)) ||

                        // )(,   )x,   )3,   )sin(x)
                        (prevIsCloseParen && (currIsOpenParen || currIsVar || currIsNum || currIsFunc)) ||

                        // f(x)x
                        (prevIsFunc && currIsVar);
        return needMult;
    }

    private ArrayList<String> splitIntoPieces(String expr) {
        if (expr == null || expr.isEmpty()) {
            return null;
        }

        ArrayList<String> pieces = new ArrayList<>();
        String current = "";
        for (Character c : expr.toCharArray()) {
            if (Character.isDigit(c) || c == '.') {
                if (!current.isEmpty() && Character.isLetter(current.charAt(0))) {
                    // case 5sin
                    pieces.add(current);
                    current = "";
                }
                current += c; // build a number
            } else if (Character.isLetter(c)) {
                if (!current.isEmpty() && Character.isDigit(current.charAt(0))) {
                    // case sin5
                    pieces.add(current);
                    current = "";
                }
                current += c; // build function or variable name
            } else {
                if (!current.isEmpty()) {
                    pieces.add(current);
                    current = "";
                }
                if (Token.operators.contains("" + c) || c == '(' || c == ')') {
                    pieces.add("" + c);
                }
            }
        }

        if (!current.isEmpty()) {
            pieces.add(current);
        }

        return pieces;
    }

    public Token[] convertInfixToPost(String exprStr) {
        if (exprStr == null || exprStr.isEmpty()) {
            return null;
        }
        ArrayList<Token> tokens = tokenize(exprStr);

        ArrayList<Token> output = new ArrayList<>();
        Stack<Token> operatorStack = new Stack<>();

        for (Token token : tokens) {
            if (token.type == Token.Type.NUMBER || token.type == Token.Type.VARIABLE) {
                output.add(token);
            } else if (token.value.equals("(")) {
                operatorStack.push(token);
            } else if (token.type == Token.Type.FUNCTION) {
                operatorStack.push(token);
            } else if (token.value.equals(")")) {
                // if closing bracket, pop everything until "("
                while (!operatorStack.isEmpty() && !operatorStack.peek().value.equals("(")) {
                    output.add(operatorStack.pop());
                }
                operatorStack.pop();

                // if there is a function on top, pop it too
                if (!operatorStack.isEmpty() && operatorStack.peek().type == Token.Type.FUNCTION) {
                    output.add(operatorStack.pop());
                }
            } else if (token.type == Token.Type.OPERATOR) {
                while (!operatorStack.isEmpty() && !operatorStack.peek().value.equals("(") &&
                        // previous operator cannot be greater priority
                        (operatorStack.peek().precedence > token.precedence ||
                                // two operators of the same priority cannot stay tgt
                                (operatorStack.peek().precedence == token.precedence &&
                                        !operatorStack.peek().isRightAssociative)
                        )) {
                    output.add(operatorStack.pop());
                }
                operatorStack.push(token);
            }
        }


        // pop remaining operators
        while (!operatorStack.isEmpty()) {
            output.add(operatorStack.pop());
        }

        // catch exception and return as user input expr error

        return output.toArray(new Token[0]);
    }

    public double evaluatePostfix(Token[] postfix) {
        Stack<Double> stack = new Stack<>();

        for (Token token : postfix) {
            if (token == null) {
                continue;
            }

            if (token.type == Token.Type.NUMBER) {
                stack.push(parseDouble(token.value));
            } else if (token.type == Token.Type.OPERATOR || token.type == Token.Type.FUNCTION) {
                double[] args = new double[token.arity];
                for (int i = token.arity - 1; i >= 0; i--) {
                    args[i] = stack.pop();
                }
                try {
                    double val = Token.calculate(token, args);
                    stack.push(val);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

        if (stack.size() != 1) {
            throw new RuntimeException("Invalid postfix expression: stack size != 1");
        }

        return stack.peek();
    }

    public double evaluatePostfix(Token[] postfix, double xVal) {
        Stack<Double> stack = new Stack<>();

        for (Token token : postfix) {
            if (token == null) {
                continue;
            }

            if (token.type == Token.Type.NUMBER) {
                stack.push(parseDouble(token.value));
            } else if (token.type == Token.Type.VARIABLE) {
                stack.push(xVal);
            } else if (token.type == Token.Type.OPERATOR || token.type == Token.Type.FUNCTION) {
                double[] args = new double[token.arity];
                for (int i = token.arity - 1; i >= 0; i--) {
                    args[i] = stack.pop();
                }
                try {
                    double val = Token.calculate(token, args);
                    stack.push(val);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

        if (stack.size() != 1) {
            throw new RuntimeException("Invalid postfix expression: stack size != 1");
        }

        return stack.peek();
    }
}
