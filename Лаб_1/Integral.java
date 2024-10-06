import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Integral {
    private final double a; // Начальная граница интегрирования
    private final double b; // Конечная граница интегрирования
    private final double h; // Шаг интегрирования
    private final java.util.function.Function<Double, Double> function; // Функция для интегрирования

    // Конструктор класса Integral
    public Integral(double a, double b, double h, java.util.function.Function<Double, Double> function) {
        this.a = a;
        this.b = b;
        this.h = h;
        this.function = function;
    }

    // Метод для вычисления интеграла
    public double calculate(int numThreads) throws Exception {
        // Количество трапеций
        double n = (b - a) / h; 
        double result = 0.0;

        // Создание пула потоков с заданным количеством потоков
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        Future<Double>[] futures = new Future[numThreads]; // Массив для хранения результатов от потоков

        // Распределение работы между потоками
        for (int i = 0; i < numThreads; i++) {
            final int threadIndex = i; // Индекс потока
            futures[i] = executor.submit(() -> {
                double threadResult = 0.0; // Результат текущего потока
                int start = (int) (threadIndex * n / numThreads); // Начало диапазона для текущего потока
                int end = (int) ((threadIndex + 1) * n / numThreads); // Конец диапазона для текущего потока

                // Вычисление интеграла методом трапеций для данного диапазона
                for (int j = start; j < end; j++) {
                    double x1 = a + j * h; // Первая точка
                    double x2 = a + (j + 1) * h; // Вторая точка
                    // Метод трапеций: (f(x1) + f(x2)) * h / 2
                    threadResult += (function.apply(x1) + function.apply(x2)) * h / 2;
                }
                return threadResult; // Возвращаем результат потока
            });
        }

        // Сбор результатов из потоков
        for (int i = 0; i < numThreads; i++) {
            result += futures[i].get(); // Суммируем результаты
        }

        executor.shutdown(); // Завершаем работу пула потоков
        return result; // Возвращаем общий результат интегрирования
    }
}
