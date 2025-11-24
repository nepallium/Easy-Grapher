package Model;

import lombok.Getter;

import java.util.EmptyStackException;

public class Function {

    @Getter
    private String exprStr;
    private Token[] postfix;
    private String derivativeStr;
    private Token[] derivativePostfix;

    private static final PostfixEvaluator evaluator = new PostfixEvaluator();

    public Function(String exprStr) {
        this.exprStr = exprStr;
        this.postfix = null;
    }

    // testing purposes
    public Function(String exprStr, boolean ignoreValidity) {
        this.exprStr = exprStr;
        this.postfix = evaluator.convertInfixToPost(exprStr);
    }

    public double valueAt(double x) {
        if (postfix == null) {
            return Double.NaN;
        }
        return evaluator.evaluatePostfix(this.postfix, x);
    }

    public boolean isValid() {
        if (exprStr == null || exprStr.isBlank()) {
            return false;
        }

        try {
            if (postfix == null) {
                try {
                    postfix = evaluator.convertInfixToPost(exprStr);
                }
                catch (EmptyStackException e) {
//                    System.out.println("bad parenthesis");
                    return false;
                }
                catch (IllegalArgumentException e) {
//                    System.out.println("bad symbol");
                    return false;
                }
            }

            valueAt(0);
            return true;
        } catch (ArithmeticException e) {
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Token[] getPostfix() {
        if (postfix == null) {
            isValid();
        }

        return postfix;
    }
}
