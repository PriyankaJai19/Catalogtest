import java.util.*;

class ShamirSecretSharing {
    // Function to calculate the value of y
    // y = poly[0] + x*poly[1] + x^2*poly[2] + ...
    static int calculateY(int x, List<Integer> poly) {
        int y = 0;
        int temp = 1;

        // Iterating through the array
        for (int coeff : poly) {
            // Computing the value of y
            y += coeff * temp;
            temp *= x;
        }
        return y;
    }

    // Function to perform the secret sharing algorithm and encode the given secret
    static void secretSharing(int S, List<int[]> points, int N, int K) {
        // A list to store the polynomial coefficient of K-1 degree
        List<Integer> poly = new ArrayList<>(K);

        // Randomly choose K - 1 numbers but not zero and poly[0] is the secret
        poly.add(S);
        Random rand = new Random();

        for (int j = 1; j < K; ++j) {
            int p = 0;
            while (p == 0) {
                p = rand.nextInt(997); // To keep random values in range, mod with a prime number around 1000
            }
            poly.add(p);
        }

        // Generating N points from the polynomial
        for (int j = 1; j <= N; ++j) {
            int x = j;
            int y = calculateY(x, poly);

            // Points created on sharing
            points.add(new int[]{x, y});
        }
    }

    // Class for handling fractions for Lagrange Interpolation
    static class Fraction {
        int num, den;

        Fraction(int n, int d) {
            num = n;
            den = d;
            reduceFraction(this);
        }

        // Reducing fraction by dividing by GCD
        void reduceFraction(Fraction f) {
            int gcd = gcd(f.num, f.den);
            f.num /= gcd;
            f.den /= gcd;
        }

        // GCD function
        static int gcd(int a, int b) {
            if (b == 0) return a;
            return gcd(b, a % b);
        }

        // Multiplication of fractions
        Fraction multiply(Fraction f) {
            Fraction temp = new Fraction(this.num * f.num, this.den * f.den);
            reduceFraction(temp);
            return temp;
        }

        // Addition of fractions
        Fraction add(Fraction f) {
            Fraction temp = new Fraction(this.num * f.den + this.den * f.num, this.den * f.den);
            reduceFraction(temp);
            return temp;
        }
    }

    // Function to generate the secret back from the given points using Lagrange interpolation
    static int generateSecret(int[] x, int[] y, int M) {
        Fraction ans = new Fraction(0, 1);

        // Loop to iterate through the given points
        for (int i = 0; i < M; ++i) {
            Fraction l = new Fraction(y[i], 1);
            for (int j = 0; j < M; ++j) {
                if (i != j) {
                    Fraction temp = new Fraction(-x[j], x[i] - x[j]);
                    l = l.multiply(temp);
                }
            }
            ans = ans.add(l);
        }
        // Return the secret
        return ans.num;
    }

    // Function to encode and decode the given secret by using the above-defined functions
    static void operation(int S, int N, int K) {
        // List to store the points
        List<int[]> points = new ArrayList<>();

        // Sharing of secret code in N parts
        secretSharing(S, points, N, K);

        System.out.println("Secret is divided into " + N + " parts: ");
        for (int[] point : points) {
            System.out.println(point[0] + " " + point[1]);
        }

        System.out.println("We can generate Secret from any " + K + " parts");

        // Input any M points from these to get back our secret code
        int M = 2;

        if (M < K) {
            System.out.println("Points are less than threshold " + K + " points required");
        }

        int[] x = new int[M];
        int[] y = new int[M];

        // Input M points (take first M points from the generated N points)
        for (int i = 0; i < M; ++i) {
            x[i] = points.get(i)[0];
            y[i] = points.get(i)[1];
        }

        // Get back our secret again
        System.out.println("Our Secret Code is: " + generateSecret(x, y, M));
    }

    // Driver Code
    public static void main(String[] args) {
        int S = 42; // Secret to be shared
        int N = 4;  // Number of shares to be created
        int K = 1;  // Minimum shares needed to reconstruct the secret

        operation(S, N, K);
    }
}
