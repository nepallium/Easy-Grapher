/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package PostfixEvaluator;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import Model.PostfixEvaluator;
import Model.Token;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Alex
 */
public class PostfixEvaluator_ConvertInfix_Test {
    
    private PostfixEvaluator pfEval;
    
    @Before
    public void setUp() {
        pfEval = new PostfixEvaluator();
    }

    @Test
    public void testTokenize() {
        String arr1 = "(42 + 1) -- 3";
        String[] result1 = Token.toStrArray(pfEval.tokenize(arr1));
        String[] expected1 = {"(", "42", "+", "1", ")", "-", "~", "3"};
        assertArrayEquals(expected1, result1);

        String arr2 = "(42.35 /-1.86) -- 3";
        String[] result2 = Token.toStrArray(pfEval.tokenize(arr2));
        String[] expected2 = {"(", "42.35", "/", "~","1.86", ")", "-", "~", "3"};
        assertArrayEquals(expected2, result2);

//        unary + neglected
        String arr3 = "+(x+x)";
        String[] result3 = Token.toStrArray(pfEval.tokenize(arr3));
        String[] expected3 = {"(", "x", "+", "x", ")"};
        assertArrayEquals(expected3, result3);
    }

    @Test
    public void testConvertInfix_ToPost_regular() {
        String normal1 = "((3+4)*2)/7";
        String[] result1 = Token.toStrArray(pfEval.convertInfixToPost(normal1));
        String[] expected1 =  {"3", "4", "+", "2", "*", "7", "/"};
        assertArrayEquals(expected1, result1);

        String normal2 = "(4+2)+(3*(5-1))";
        String[] result2 = Token.toStrArray(pfEval.convertInfixToPost(normal2));
        String[] expected2 = {"4", "2", "+", "3", "5", "1", "-", "*", "+"};
        assertArrayEquals(expected2, result2);
    }

    @Test
    public void testConvertInfix_ToPost_unary_negation() {
        String normal1 = "-3*---4";
        String[] result1 = Token.toStrArray(pfEval.convertInfixToPost(normal1));
        String[] expected1 = {"3", "~", "4", "~", "~", "~", "*"};
        assertArrayEquals(expected1, result1);

        String normal2 = "7*(4*-3)/(5+1)";
        String[] result2 = Token.toStrArray(pfEval.convertInfixToPost(normal2));
        String[] expected2 = {"7", "4", "3", "~", "*", "*", "5", "1", "+", "/"};
        assertArrayEquals(expected2, result2);

        String normal3 = "-(x+x)";
        String[] result3 = Token.toStrArray(pfEval.convertInfixToPost(normal3));
        String[] expected3 = {"x", "x", "+", "~"};
        assertArrayEquals(expected3, result3);

        String normal4 = "4*-x";
        String[] result4 = Token.toStrArray(pfEval.convertInfixToPost(normal4));
        String[] expected4 = {"4", "x", "~", "*"};
        assertArrayEquals(expected4, result4);

        String normal5 = "2*(-x-x)";
        String[] result5 = Token.toStrArray(pfEval.convertInfixToPost(normal5));
        String[] expected5 = {"2", "x", "~", "x", "-", "*"};
        assertArrayEquals(expected5, result5);

        String normal6 = "(4*x)-x";
        String[] result6 = Token.toStrArray(pfEval.convertInfixToPost(normal6));
        String[] expected6 = {"4", "x", "*", "x", "-"};
        assertArrayEquals(expected6, result6);
    }

    @Test
    public void testConvertInfix_ToPost_emptySpace() {
        String normal1 = "(  ( 3+4   )*2      )/7";
        String[] result1 = Token.toStrArray(pfEval.convertInfixToPost(normal1));
        String[] expected1 =  {"3", "4", "+", "2", "*", "7", "/"};
        assertArrayEquals(expected1, result1);

        String normal2 = "(4  + 2) +(3*(   5- 1))";
        String[] result2 = Token.toStrArray(pfEval.convertInfixToPost(normal2));
        String[] expected2 = {"4", "2", "+", "3", "5", "1", "-", "*", "+"};
        assertArrayEquals(expected2, result2);
    }
}
