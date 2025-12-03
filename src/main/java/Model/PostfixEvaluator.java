/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

import java.util.*;

import static Model.Token.Type.*;
import static java.lang.Double.parseDouble;

/**
 * @author Alex
 */
public class PostfixEvaluator {
    /**
     * Turns an expression string into an arraylist of tokens
     *
     * @param exprStr the string expression
     * @return the converted arraylist
     */
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
                    boolean needMult = isNeedMult(prevToken, token);

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

    /**
     * Checks if two tokens are implicitly multiplied (the multiplication sign is not written, but implied)
     *
     * @param prevToken the first token
     * @param token     the second token
     * @return whether to add a multiplication operator or not
     */
    private static boolean isNeedMult(Token prevToken, Token token) {
        boolean prevIsVar = prevToken.type == VARIABLE;
        boolean prevIsConst = prevToken.type == CONSTANT;
        boolean prevIsNum = prevIsConst || prevToken.type == NUMBER;
        boolean prevIsCloseParen = prevToken.type == PARENTHESIS && prevToken.value.equals(")");
        boolean prevIsFunc = prevToken.type == FUNCTION;

        boolean currIsVar = token.type == VARIABLE;
        boolean currIsConst = token.type == CONSTANT;
        boolean currIsNum = token.type == NUMBER || currIsConst;
        boolean currIsOpenParen = token.type == PARENTHESIS && token.value.equals("(");
        boolean currIsFunc = token.type == FUNCTION;

        boolean needMult =
                // 3x, 3pi, 3(x), 3sin(x)
                (prevIsNum && (currIsVar || currIsOpenParen || currIsFunc)) ||

                        // x3, xpi, x(x), x sin(x)
                        (prevIsVar && (currIsOpenParen || currIsFunc || currIsNum)) ||

                        // pi3, 3e, epi
                        ((prevIsConst && currIsConst) || (prevIsNum && currIsConst) || (prevIsConst && currIsNum)) ||

                        // )(, )x, )3, )pi, )sin
                        (prevIsCloseParen && (currIsOpenParen || currIsVar || currIsNum || currIsFunc)) ||

                        // f(x)x
                        (prevIsFunc && (currIsVar || currIsNum));

        return needMult;
    }

    /**
     * Splits an expression into different, tokenize-able pieces
     *
     * @param expr the expression to split
     * @return the arraylist of split strings
     */
    private ArrayList<String> splitIntoPieces(String expr) {
        if (expr == null || expr.isEmpty()) {
            return null;
        }

        ArrayList<String> pieces = new ArrayList<>();
        String current = "";
        for (Character c : expr.toCharArray()) {
            if (c == ' ') {
                continue;
            }
            if (Character.isDigit(c) || c == '.') {
                if (!current.isEmpty() && Character.isLetter(current.charAt(0))) {
                    // case 5sin
                    pieces.add(current);
                    current = "";
                }
                current += c; // build a number
            } else if (Character.isLetter(c)) {
                if (current.equals("e") || current.equals("pi")) {
                    // case epi, pix
                    pieces.add(current);
                    current = "";
                } else if (!current.isEmpty() && Character.isDigit(current.charAt(0))) {

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
                } else {
                    throw new IllegalArgumentException();
                }
            }
        }

        if (!current.isEmpty()) {
            pieces.add(current);
        }

        return pieces;
    }

    /**
     * Converts an infix expression into postfix array of tokens and returns it
     *
     * @param exprStr the input infix expression
     * @return the postfix tokenized array
     */
    public Token[] convertInfixToPost(String exprStr) {
        if (exprStr == null || exprStr.isEmpty()) {
            return null;
        }
        ArrayList<Token> tokens = tokenize(exprStr);

        ArrayList<Token> output = new ArrayList<>();
        Stack<Token> operatorStack = new Stack<>();

        for (Token token : tokens) {
            if (token.type == NUMBER ||
                    token.type == VARIABLE ||
                    token.type == CONSTANT) {
                output.add(token);
            } else if (token.value.equals("(")) {
                operatorStack.push(token);
            } else if (token.type == FUNCTION) {
                operatorStack.push(token);
            } else if (token.value.equals(")")) {
                // if closing bracket, pop everything until "("
                while (!operatorStack.isEmpty() && !operatorStack.peek().value.equals("(")) {
                    output.add(operatorStack.pop());
                }
                operatorStack.pop();

                // if there is a function on top, pop it too
                if (!operatorStack.isEmpty() && operatorStack.peek().type == FUNCTION) {
                    output.add(operatorStack.pop());
                }
            } else if (token.type == OPERATOR) {
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

    /**
     * Evaluates the value of a postfix array of tokens (no variable)
     *
     * @param postfix the input array of tokens in postfix notation
     * @return the result of the evaluation
     */
    public double evaluatePostfix(Token[] postfix) {
        Stack<Double> stack = new Stack<>();

        for (Token token : postfix) {
            if (token == null) {
                continue;
            }

            if (token.type == NUMBER
                    || token.type == CONSTANT) {
                stack.push(parseDouble(token.value));
            } else if (token.type == OPERATOR || token.type == FUNCTION) {
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

    /**
     * Evaluates the value of a postfix array of tokens (with variable)
     *
     * @param postfix the input array of tokens in postfix notation
     * @param xVal    the value of x to replace it with in the postfix array
     * @return the result of the evaluation
     */
    public double evaluatePostfix(Token[] postfix, double xVal) {
        Stack<Double> stack = new Stack<>();

        for (Token token : postfix) {
            if (token == null) {
                continue;
            }

            if (token.type == NUMBER
                    || token.type == CONSTANT) {
                stack.push(parseDouble(token.value));
            } else if (token.type == VARIABLE) {
                stack.push(xVal);
            } else if (token.type == OPERATOR || token.type == FUNCTION) {
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
