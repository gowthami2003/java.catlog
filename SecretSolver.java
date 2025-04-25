import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileReader;
import java.math.BigInteger;
import java.util.*;

public class SecretSolver {

    public static void main(String[] args) throws Exception {
        // Load JSON file
        JSONObject root = new JSONObject(new JSONTokener(new FileReader("input.json")));

        // Process both test cases
        BigInteger secret1 = solveTestCase(root.getJSONObject("testcase1"));
        BigInteger secret2 = solveTestCase(root.getJSONObject("testcase2"));

        System.out.println("Secret for Test Case 1: " + secret1);
        System.out.println("Secret for Test Case 2: " + secret2);
    }

    static BigInteger solveTestCase(JSONObject testCase) {
        int k = testCase.getJSONObject("keys").getInt("k");

        // Decode points
        List<Point> points = new ArrayList<>();
        for (String key : testCase.keySet()) {
            if (key.equals("keys")) continue;

            int x = Integer.parseInt(key);
            JSONObject val = testCase.getJSONObject(key);
            int base = Integer.parseInt(val.getString("base"));
            String encodedValue = val.getString("value");

            BigInteger y = new BigInteger(encodedValue, base);
            points.add(new Point(x, y));
        }

        // Sort and take first k points
        points.sort(Comparator.comparingInt(p -> p.x));
        return lagrangeInterpolationAtZero(points.subList(0, k));
    }

    static BigInteger lagrangeInterpolationAtZero(List<Point> points) {
        BigInteger result = BigInteger.ZERO;
        int k = points.size();

        for (int i = 0; i < k; i++) {
            BigInteger xi = BigInteger.valueOf(points.get(i).x);
            BigInteger yi = points.get(i).y;

            BigInteger numerator = BigInteger.ONE;
            BigInteger denominator = BigInteger.ONE;

            for (int j = 0; j < k; j++) {
                if (i == j) continue;
                BigInteger xj = BigInteger.valueOf(points.get(j).x);
                numerator = numerator.multiply(xj.negate());
                denominator = denominator.multiply(xi.subtract(xj));
            }

            BigInteger term = yi.multiply(numerator).divide(denominator);
            result = result.add(term);
        }

        return result;
    }

    static class Point {
        int x;
        BigInteger y;

        Point(int x, BigInteger y) {
            this.x = x;
            this.y = y;
        }
    }
}
