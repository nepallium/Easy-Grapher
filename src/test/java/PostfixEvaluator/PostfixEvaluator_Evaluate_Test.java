package PostfixEvaluator;

import Model.PostfixEvaluator;
import Model.Token;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Alex
 */
public class PostfixEvaluator_Evaluate_Test {

    private PostfixEvaluator pfEval;

    @Before
    public void setUp() {
        pfEval = new PostfixEvaluator();
    }

    @Test
    public void testEvaluate_Postfix_regular() {
        String[] normal1 = {"3", "4", "+", "2", "*", "7", "/"};
        double result1 = pfEval.evaluatePostfix(Token.fromStrArray(normal1));
        assertEquals(2.0, result1, 0.001);

        String[] normal2 = {"4", "2", "+", "3", "5", "1", "-", "*", "+"};
        double result2 = pfEval.evaluatePostfix(Token.fromStrArray(normal2));
        assertEquals(18.0, result2, 0.001);

        String[] normal3 = {"7", "4", "-3", "*", "1", "5", "+", "/", "*"};
        double result3 = pfEval.evaluatePostfix(Token.fromStrArray(normal3));
        assertEquals(-14.0, result3, 0.001);
    }

    @Test
    public void testEvaluate_Postfix_negativeFactor() {
        String[] normal1 = {"3", "~", "4", "~", "~", "~", "*"};
        double result1 = pfEval.evaluatePostfix(Token.fromStrArray(normal1));
        assertEquals("12", 12.0, result1, 0.001);

        String[] normal2 = {"7", "4", "3", "~", "*", "*", "5", "1", "+", "/"};
        double result2 = pfEval.evaluatePostfix(Token.fromStrArray(normal2));
        assertEquals("-14", -14.0, result2, 0.001);

        String[] normal3 = {"2", "5", "~", "7", "-", "*"};
        double result3 = pfEval.evaluatePostfix(Token.fromStrArray(normal3));
        assertEquals("-24", -24.0, result3, 0.001);

        String[] normal4 = {"4", "3", "*", "8", "-"};
        double result4 = pfEval.evaluatePostfix(Token.fromStrArray(normal4));
        assertEquals("4", 4.0, result4, 0.001);
    }
}
