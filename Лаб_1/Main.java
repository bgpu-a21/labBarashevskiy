import java.util.concurrent.*;
import java.util.function.Function;

public class Main {
    // Метод для выполнения интегрирования в нескольких потоках
    public static double parallelIntegration(Integral integral, int threads) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(threads); // Создание пула потоков
        double[] results = new double[threads]; // Массив для хранения результатов
        Future<Double>[] futures = new Future[threads]; // Массив для хранения Future объектов

        double a = integral.a; // Получение левой границы
        double b = integral.b; // Получение правой границы
        double h = integral.h; // Получение шага
        int n = (int) ((b - a) / h); // Общее количество интервалов

        // Разделяем диапазон интегрирования на части для каждого потока
        for (int i = 0; i < threads; i++) {
            final int threadIndex = i; // Индекс потока
            futures[i] = executor.submit(() -> {
                double threadA = a + (b - a) * threadIndex / threads; // Начало диапазона для потока
                double threadB = a + (b - a) * (threadIndex + 1) / threads; // Конец диапазона для потока
                // Создаем временный объект Integral для этого потока
                Integral tempIntegral = new Integral(integral.function, threadA, threadB, h);
                return tempIntegral.trapezoidalRule(); // Вычисляем интеграл для этого потока
            });
        }

        // Собираем результаты из всех потоков
        double total = 0.0; // Переменная для итогового результата
        for (int i = 0; i < threads; i++) {
            try {
                total += futures[i].get(); // Получаем результат каждого потока
            } catch (ExecutionException e) {
                e.printStackTrace(); // Обработка ошибок
            }
        }

        executor.shutdown(); // Завершаем работу пула потоков
        return total; // Возвращаем итоговый интеграл
    }

    public static void main(String[]
