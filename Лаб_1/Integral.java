import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Integral {
    private final int threads;        // Количество потоков
    private final double elapsedTime; // Время выполнения
    private final double result;      // Результат интегрирования
    private final double a; // Начальная граница интегрирования
    private final double b; // Конечная граница интегрирования
    private final double h; // Шаг интегрирования
    private final java.util.function.Function<Double, Double> function; // Функция для интегрирования
    private java.lang.Thread[] Thread;

    // Конструктор класса Integral (основной, с границами и шагом)
    public Integral(double a, double b, double h, java.util.function.Function<Double, Double> function) {
        this.a = a;
        this.b = b;
        this.h = h;
        this.function = function;
        this.threads = 0;
        this.elapsedTime = 0.0;
        this.result = 0.0;
    }

    // Конструктор для результатов (с количеством потоков, временем и результатом)
    public Integral(int threads, double elapsedTime, double result) {
        this.threads = threads;
        this.elapsedTime = elapsedTime;
        this.result = result;
        this.a = 0.0;
        this.b = 0.0;
        this.h = 0.0;
        this.function = null;
    }

    public int getThreads() {
        return threads;
    }

    public double getElapsedTime() {
        return elapsedTime;
    }

    public double getResult() {
        return result;
    }

    class Znach extends Thread
    {
        double threadResult = 0.0; // Результат текущего потока

        public void run(int threadIndex,int numThreads, double n)
        {
            int start = (int) (threadIndex * n / numThreads); // Начало диапазона для текущего потока
            int end = (int) ((threadIndex + 1) * n / numThreads); // Конец диапазона для текущего потока

            // Вычисление интеграла методом трапеций для данного диапазона
            for (int j = start; j < end; j++) {
                double x1 = a + j * h; // Первая точка
                double x2 = a + (j + 1) * h; // Вторая точка
                threadResult += (function.apply(x1) + function.apply(x2)) * h / 2;
            }
        }
    }

    // Метод для вычисления интеграла
    public double calculate(int numThreads) {
        // Количество трапеций
        double n = (b - a) / h;
        double result = 0.0;

        Znach[] futures = new Znach[numThreads];
        // Распределение работы между потоками
        for (int i = 0; i < numThreads; i++) {
            final int threadIndex = i; // Индекс потока
            futures[i] = new Znach();
            futures[i].run(threadIndex, numThreads, n);
        }

        // Сбор результатов из потоков
        for (int i = 0; i < numThreads; i++) {
            result += futures[i].threadResult; // Суммируем результаты
        }

        return result; // Возвращаем общий результат интегрирования
    }
}
