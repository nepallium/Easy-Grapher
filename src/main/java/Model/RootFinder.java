package Model;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.solvers.BrentSolver;

import java.util.ArrayList;
import java.util.Collections;

public class RootFinder {

    private static final PostfixEvaluator evaluator = new PostfixEvaluator();

    /**
     * Finds all roots of a function in [min, max] using a scanning + BrentSolver approach.
     *
     * @param f        f(x)
     * @param g        g(x)
     * @param min      minimum x value of the search interval
     * @param max      maximum x value of the search interval
     * @param step     scanning step size (smaller = more precise)
     * @param decimals how many decimals to round the roots
     * @return a list of all roots found
     */
    public static ArrayList<Double> findAllRoots(Function f, Function g, double min, double max, double step, int decimals) {
        Function hFct = subtractFcts(f, g);

        ArrayList<Double> roots = new ArrayList<>();
        UnivariateFunction h = hFct::valueAt;
        BrentSolver solver = new BrentSolver(1e-10, 1e-14);

        for (double a = min; a < max; a += step) {
            double b = Math.min(a + step, max); // ensure we don’t exceed max

            double ha = hFct.valueAt(a);
            double hb = hFct.valueAt(b);

            if (Double.isInfinite(ha) || Double.isInfinite(hb) || Double.isNaN(ha) || Double.isNaN(hb)) {
                continue; // ignore this interval
            }

            try {
                if (h.value(a) * h.value(b) <= 0) { // sign change detected
                    double root = solver.solve(1000, h, a, b);

                    // round root
                    double rootClean = Math.round(root * Math.pow(10, decimals)) / Math.pow(10, decimals);

                    double hRoot = hFct.valueAt(rootClean);
                    if (Double.isInfinite(hRoot) || Double.isNaN(hRoot) || Math.abs(hRoot) > 1e-6) {
                        continue; // ignore fake root near discontinuity
                    }

                    // add if not already in list
                    if (!roots.contains(rootClean)) {
                        roots.add(rootClean);
                    }
                }
            } catch (Exception e) {
                // ignore small intervals without proper bracketing
                // TODO display No roots found in the interval Label
                System.out.println("No roots found in the visible interval");
            }
        }

        Collections.sort(roots);
        return roots;
    }

    private static Function subtractFcts(Function f, Function g) {
        if (f == null || g == null) {
            return null;
        }

        return new Function(String.format("%s-(%s)", f.getExprStr(), g.getExprStr()), true);
    }
}
