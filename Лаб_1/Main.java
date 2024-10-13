import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

public class Main {
    public static void main(String[] args) {
        double a = 0; // Начальная граница интегрирования
        double b = Math.PI; // Конечная граница интегрирования
        double h = Math.pow(10, -6); // Шаг интегрирования

        // Определяем функцию для интегрирования (например, синус)
        Function<Double, Double> function = Math::sin; // Измените функцию при необходимости
        
                // Список для хранения результатов и времени выполнения
        List<Integral> results = new ArrayList<>();

        // Цикл по количеству потоков от 1 до 20
        for (int n = 1; n <= 20; n++) {
            Integral integral = new Integral(a, b, h, function); // Создаем объект Integral
            try {
                long startTime = System.currentTimeMillis(); // Начало измерения времени
                double result = integral.calculate(n); // Вычисляем интеграл
                long endTime = System.currentTimeMillis(); // Конец измерения времени
                double elapsedTime = endTime - startTime; // Вычисляем затраченное время
                
                results.add(new Integral(n, elapsedTime, result));

                // Выводим статистику
                // System.out.printf("%d - %.1f мс. (Результат: %.6f)%n", n, elapsedTime, result);
            } catch (Exception e) {
                e.printStackTrace(); // Обрабатываем возможные исключения
            }
        }
        results.sort(Comparator.comparingDouble(Integral::getElapsedTime));

        // Выводим отсортированные данные
        for (Integral result : results) {
            System.out.printf("%d поток(ов) - %.1f мс. (Результат: %.6f)%n", result.getThreads(), result.getElapsedTime(), result.getResult());
        }
    }
}
