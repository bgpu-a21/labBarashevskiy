import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class Integral {
    private Function<Double, Double> function;
    private double a;
    private double b;
    private double h;

    public Integral(Function<Double, Double> function, double a, double b, double h) {
        this.function = function;
        this.a = a;
        this.b = b;
        this.h = h;
    }

    public double integrate(double start, double end) {
        double result = 0.0;
        for (double x = start; x <= end; x += h) {
            double fx1 = function.apply(x);
            double fx2 = function.apply(x + h);
            result += (fx1 + fx2) * h / 2;
        }
        return result;
    }

    public double calculateInParallel(int numThreads) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        double step = (b - a) / numThreads;
        final double[] results = new double[numThreads];

        for (int i = 0; i < numThreads; i++) {
            final int index = i;
            final double start = a + i * step;
            final double end = start + step;
            executor.submit(() -> {
                results[index] = integrate(start, end);
            });
        }

        executor.shutdown();
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

        double totalResult = 0.0;
        for (double result : results) {
            totalResult += result;
        }
        return totalResult;
    }
}
