import java.util.Random;

public class SimulatedAnnealing {


    // Generate Random Solution
    public static Solution randomSolution(Random random){
        return new Solution(
                random.nextDouble(),
                random.nextDouble(),
                random.nextDouble(),
                random.nextDouble()
        );
    }

    // Simulated Annealing Algorithm
    public static Solution simulatedAnnealing(int iterations){
        Random random = new Random();
        double temperature = 1000;
        double cooling = 0.75;

        Solution currentSolution = randomSolution(random);
        Solution bestSolution = currentSolution;

        for (int i = 0; i < iterations; i++){
            // You want to introduce variation while keeping new solutions “close” to the current one.
            Solution neighbor = new Solution(
                    currentSolution.getA() + random.nextGaussian(),
                    currentSolution.getB() + random.nextGaussian(),
                    currentSolution.getC() + random.nextGaussian(),
                    currentSolution.getX() + random.nextGaussian()
            );

            double errorDifference = currentSolution.calculateError() -  neighbor.calculateError(); // If its positive then the neighbor is better, else we take the worst solution
            if(errorDifference > 0 || Math.exp(errorDifference / temperature) > random.nextDouble()){
                currentSolution = neighbor;
                if(currentSolution.calculateError() < bestSolution.calculateError()){
                    bestSolution = currentSolution;
                }
            }
            temperature *= cooling;
        }
        return  bestSolution;
    }

    public static Solution runParallel(int noOfThreads,int iterations) throws InterruptedException {
        Thread[] threadsArray = new Thread[noOfThreads];
        Solution[] resultsArray = new Solution[noOfThreads];

        for(int i = 0; i < noOfThreads; i++){
            final int index = i;
            threadsArray[i] = new Thread(() -> {
                // Store the best solution in the results array with the specified index
                resultsArray[index] = simulatedAnnealing(iterations);
            });
            // Starting the thread after storing the result in the results array
            threadsArray[i].start();
        }
        for (Thread thread : threadsArray) {
            // Joining the threads
            thread.join();
        }

        // Check for best solution
        Solution bestSolution = resultsArray[0];
        for(Solution solution : resultsArray){
            if(solution.calculateError() < bestSolution.calculateError()){
                bestSolution = solution;
            }
        }
        return bestSolution;
    }

    static void printSolution(String label, Solution solution, double time) {
        System.out.println("\n" + label + ":");
        System.out.println("a=" + String.format("%.3f", solution.getA()) +
                ", b=" + String.format("%.3f", solution.getB()) +
                ", c=" + String.format("%.3f", solution.getC()) +
                ", x=" + String.format("%.3f", solution.getX()));
        System.out.println("Result: " + String.format("%.6f", solution.calculateError()) +
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
