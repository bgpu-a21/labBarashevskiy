import java.util.function.Function;

public class Integral {
    private Function<Double, Double> function; // Функция для интегрирования
    private double a; // Левая граница интегрирования
    private double b; // Правая граница интегрирования
    private double h; // Шаг интегрирования

    // Конструктор, принимающий параметры функции и границ
    public Integral(Function<Double, Double> function, double a, double b, double h) {
        this.function = function; // Инициализация функции
        this.a = a; // Установка левой границы
        this.b = b; // Установка правой границы
        this.h = h; // Установка шага
    }

    // Метод для вычисления интеграла методом трапеций
    public double trapezoidalRule() {
        double integral = 0.0; // Переменная для хранения значения интеграла
        int n = (int) ((b - a) / h); // Количество интервалов
        for (int i = 0; i < n; i++) {
            double x0 = a + i * h; // Левая граница текущего интервала
            double x1 = a + (i + 1) * h; // Правая граница текущего интервала
            integral += (function.apply(x0) + function.apply(x1)) * h / 2; // Площадь трапеции
        }
        return integral; // Возвращаем значение интеграла
    }
}
