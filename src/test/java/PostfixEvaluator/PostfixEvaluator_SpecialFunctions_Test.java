package PostfixEvaluator;

import Model.PostfixEvaluator;
import Model.Token;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * @author Alex
 */
public class PostfixEvaluator_SpecialFunctions_Test {

    private PostfixEvaluator pfEval;

    @Before
    public void setUp() {
        pfEval = new PostfixEvaluator();
    }

    @Test
    public void testImplicitMultiplication() {
        String expr1 = "5(3-5)";
        Token[] postfix1 = pfEval.convertInfixToPost(expr1);
        double result1 = pfEval.evaluatePostfix(postfix1);
        assertEquals(expr1, -10.0, result1, 0.001);

        String expr2 = "5sin(pi/2)";
        Token[] postfix2 = pfEval.convertInfixToPost(expr2);
        double result2 = pfEval.evaluatePostfix(postfix2);
        assertEquals(expr2, 5.0, result2, 0.001);


        String expr3 = "(3-5)(2-10)";
        Token[] postfix3 = pfEval.convertInfixToPost(expr3);
        double result3 = pfEval.evaluatePostfix(postfix3);
        assertEquals(expr3, 16.0, result3, 0.001);

        String expr4 = "7(5+2)/5^(5-3)";
        Token[] postfix4 = pfEval.convertInfixToPost(expr4);
        double result4 = pfEval.evaluatePostfix(postfix4);
        assertEquals(expr4, 1.96, result4, 0.001);
    }

    @Test
    public void testExponentiation() {
        String expr1 = "4^5";
        Token[] postfix1 = pfEval.convertInfixToPost(expr1);
        double result1 = pfEval.evaluatePostfix(postfix1);
        assertEquals(expr1, 1024.0, result1, 0.001);

        String expr2 = "5^(5-3)";
        Token[] postfix2 = pfEval.convertInfixToPost(expr2);
        double result2 = pfEval.evaluatePostfix(postfix2);
        assertEquals(expr2, 25.0, result2, 0.001);

        String expr3 = "5^(2-4)";
        Token[] postfix3 = pfEval.convertInfixToPost(expr3);
        double result3 = pfEval.evaluatePostfix(postfix3);
        assertEquals(expr3, 0.04, result3, 0.001);

        String expr4 = "7*(5+2)/5^(5-3)";
        Token[] postfix4 = pfEval.convertInfixToPost(expr4);
        double result4 = pfEval.evaluatePostfix(postfix4);
        assertEquals(expr4, 1.96, result4, 0.001);
    }

    @Test
    public void testSin() {
        String expr1 = "sin(0)";
        assertEquals(expr1, 0.0,
                pfEval.evaluatePostfix(pfEval.convertInfixToPost(expr1)), 0.001);

        String expr2 = "sin(pi/2)";
        assertEquals(expr2, 1.0,
                pfEval.evaluatePostfix(pfEval.convertInfixToPost(expr2)), 0.001);

        String expr3 = "sin(3*pi/4)^2";
        assertEquals(expr3, 0.5,
                pfEval.evaluatePostfix(pfEval.convertInfixToPost(expr3)), 0.001);

        String expr4 = "2*sin(x) + sin(2*x)";
        double val4 = pfEval.evaluatePostfix(pfEval.convertInfixToPost(expr4.replace("x", "1.2")));
        assertEquals(expr4, 2 * Math.sin(1.2) + Math.sin(2 * 1.2), val4, 0.001);
    }

    @Test
    public void testCos() {
        String expr1 = "cos(0)";
        assertEquals(expr1, 1.0,
                pfEval.evaluatePostfix(pfEval.convertInfixToPost(expr1)), 0.001);

        String expr2 = "cos(pi)";
        assertEquals(expr2, -1.0,
                pfEval.evaluatePostfix(pfEval.convertInfixToPost(expr2)), 0.001);

        String expr3 = "cos(pi/3)^3";
        assertEquals(expr3, Math.pow(0.5, 3),
                pfEval.evaluatePostfix(pfEval.convertInfixToPost(expr3)), 0.001);

        String expr4 = "3*cos(x) - cos(3*x)";
        double val4 = pfEval.evaluatePostfix(pfEval.convertInfixToPost(expr4.replace("x", "0.8")));
        assertEquals(expr4, 3 * Math.cos(0.8) - Math.cos(2.4), val4, 0.001);
    }

    @Test
    public void testTan() {
        String expr1 = "tan(0)";
        assertEquals(expr1, 0.0,
                pfEval.evaluatePostfix(pfEval.convertInfixToPost(expr1)), 0.001);

        String expr2 = "tan(pi/4)";
        assertEquals(expr2, 1.0,
                pfEval.evaluatePostfix(pfEval.convertInfixToPost(expr2)), 0.001);

        String expr3 = "tan(pi/6)^2 + tan(pi/3)";
        double expected3 = Math.pow(Math.tan(Math.PI / 6), 2) + Math.tan(Math.PI / 3);
        assertEquals(expr3, expected3,
                pfEval.evaluatePostfix(pfEval.convertInfixToPost(expr3)), 0.001);

        String expr4 = "tan(x) * tan(2*x)";
        double val4 = pfEval.evaluatePostfix(pfEval.convertInfixToPost(expr4.replace("x", "0.4")));
        assertEquals(expr4, Math.tan(0.4) * Math.tan(0.8), val4, 0.001);
    }

    @Test
    public void testLn() {
        String expr1 = "ln(1)";
        assertEquals(expr1, 0.0,
                pfEval.evaluatePostfix(pfEval.convertInfixToPost(expr1)), 0.001);

        String expr2 = "ln(e)";
        assertEquals(expr2, 1.0,
                pfEval.evaluatePostfix(pfEval.convertInfixToPost(expr2)), 0.001);

        String expr3 = "ln(3^2)";
        assertEquals(expr3, Math.log(9),
                pfEval.evaluatePostfix(pfEval.convertInfixToPost(expr3)), 0.001);

        String expr4 = "ln(5 + x)";
        double val4 = pfEval.evaluatePostfix(pfEval.convertInfixToPost(expr4.replace("x", "4")));
        assertEquals(expr4, Math.log(9), val4, 0.001);
    }

    @Test
    public void testSqrt() {
        String expr1 = "sqrt(0)";
        assertEquals(expr1, 0.0,
                pfEval.evaluatePostfix(pfEval.convertInfixToPost(expr1)), 0.001);

        String expr2 = "sqrt(49)";
        assertEquals(expr2, 7.0,
                pfEval.evaluatePostfix(pfEval.convertInfixToPost(expr2)), 0.001);

        String expr3 = "sqrt(4 + 5*5)";
        assertEquals(expr3, Math.sqrt(4 + 25),
                pfEval.evaluatePostfix(pfEval.convertInfixToPost(expr3)), 0.001);

        String expr4 = "3*sqrt(x) + sqrt(2*x)";
        double xVal = 16.0;
        double val4 = pfEval.evaluatePostfix(pfEval.convertInfixToPost(expr4.replace("x", "16")));
        assertEquals(expr4, 3 * Math.sqrt(xVal) + Math.sqrt(2 * xVal), val4, 0.001);
    }

    @Test
    public void testAbs() {
        String expr1 = "abs(-5)";
        assertEquals(expr1, 5.0,
                pfEval.evaluatePostfix(pfEval.convertInfixToPost(expr1)), 0.001);

        String expr2 = "abs(3 - 10)";
        assertEquals(expr2, 7.0,
                pfEval.evaluatePostfix(pfEval.convertInfixToPost(expr2)), 0.001);

        String expr3 = "abs(-3)^3";
        assertEquals(expr3, Math.pow(3, 3),
                pfEval.evaluatePostfix(pfEval.convertInfixToPost(expr3)), 0.001);

        String expr4 = "abs(x) + abs(2 - x)";
        double xVal = -3.0;

        Token[] postfix = pfEval.convertInfixToPost(expr4);
        double val4 = pfEval.evaluatePostfix(postfix, xVal);

        assertEquals(expr4, Math.abs(xVal) + Math.abs(2 - xVal), val4, 0.001);
    }
}
