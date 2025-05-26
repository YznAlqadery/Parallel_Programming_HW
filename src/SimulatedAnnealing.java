import java.util.Random;

public class SimulatedAnnealing {

    static class Solution {
        double a, b, c, x;

        Solution(double a, double b, double c, double x) {
            this.a = a;
            this.b = b;
            this.c = c;
            this.x = x;
        }

        double error() {
            return Math.abs(a * x * x + b * x + c);
        }
    }

    static Solution randomSolution(Random rand) {
        return new Solution(
                rand.nextDouble() * 20 - 10,
                rand.nextDouble() * 20 - 10,
                rand.nextDouble() * 20 - 10,
                rand.nextDouble() * 20 - 10
        );
    }
    static Solution simulatedAnnealing(int iterations) {
        Random rand = new Random();
        double temperature = 1000;
        double cooling = 0.95;

        Solution currentError = randomSolution(rand);
        Solution bestError = currentError;

        for (int i = 0; i < iterations; i++) {

            Solution neighbor = new Solution(
                    currentError.a + rand.nextGaussian(),
                    currentError.b + rand.nextGaussian(),
                    currentError.c + rand.nextGaussian(),
                    currentError.x + rand.nextGaussian()
            );
            double errorDiff = currentError.error() - neighbor.error();
            if (errorDiff > 0 || Math.exp(errorDiff / temperature) > rand.nextDouble()) {
                currentError = neighbor;
                if (currentError.error() < bestError.error()) {
                    bestError = currentError;
                }
            }


            temperature *= cooling;
        }

        return bestError;
    }


    static Solution runParallel(int threads, int iterations) throws InterruptedException {
        Thread[] threadArr = new Thread[threads];
        Solution[] results = new Solution[threads];

        for (int i = 0; i < threads; i++) {
            final int index = i;
            threadArr[i] = new Thread(() -> {
                results[index] = simulatedAnnealing(iterations);
            });
            threadArr[i].start();
        }

        for (Thread t : threadArr) {
            t.join();
        }

        Solution bestSolution = results[0];
        for (Solution solution : results) {
            if (solution.error() < bestSolution.error()) {
                bestSolution = solution;
            }
        }

        return bestSolution;
    }

    static void printSolution(String label, Solution solution, double time) {
        System.out.println("\n" + label + ":");
        System.out.println("a=" + String.format("%.3f", solution.a) +
                ", b=" + String.format("%.3f", solution.b) +
                ", c=" + String.format("%.3f", solution.c) +
                ", x=" + String.format("%.3f", solution.x));
        System.out.println("Result: " + String.format("%.6f", solution.error()) +
                ", Time: " + String.format("%.2f", time) + " ms");
    }

    public static void main(String[] args) throws InterruptedException {
        int iterations = 500;

        long start = System.nanoTime();
        Solution seq = simulatedAnnealing(iterations);
        long end = System.nanoTime();
        printSolution("Sequential", seq, (end - start) / 1_000_000.0);

        start = System.nanoTime();
        Solution best4Threads = runParallel(4, iterations);
        end = System.nanoTime();
        printSolution("Parallel (4 Threads)", best4Threads, (end - start) / 1_000_000.0);


        start = System.nanoTime();
        Solution best8Threads = runParallel(8, iterations);
        end = System.nanoTime();
        printSolution("Parallel (8 Threads)", best8Threads, (end - start) / 1_000_000.0);
    }
}
