import java.util.function.Function;

public class Main {

    public static void main(String[] args) throws InterruptedException {
      
        Function<Double, Double> function = Math::sin; 
        double a = 0;
        double b = Math.PI;
        double h = Math.pow(10, -6);

        Integral integral = new Integral(function, a, b, h);

        for (int numThreads = 1; numThreads <= 20; numThreads++) {
            long startTime = System.nanoTime();
            double result = integral.calculateInParallel(numThreads);
            long endTime = System.nanoTime();
            double durationMs = (endTime - startTime) / 1_000_000.0;
            System.out.printf("%d поток(ов) - %.2f мс, результат интегрирования: %.6f%n", numThreads, durationMs, result);
        }
    }
}
