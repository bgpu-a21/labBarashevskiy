import java.io.*; // Импортирование классов для работы с потоками ввода-вывода
import java.net.*; // Импортирование классов для работы с сетевыми сокетами
import java.util.Arrays;
import java.util.Scanner; // Импортирование класса Scanner для чтения ввода от пользователя

public class Client { // Объявление публичного класса Client
    private static final String SERVER_ADDRESS = "127.0.0.1"; // Адрес сервера (локальный хост)
    private static final int SERVER_PORT = 12345; // Порт сервера

    public static void main(String[] args) { // Основной метод программы
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT); // Создание сокета для подключения к серверу
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())); // Создание BufferedReader для чтения данных от сервера
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true); // Создание PrintWriter для отправки данных серверу
             Scanner scanner = new Scanner(System.in)) { // Создание Scanner для чтения ввода от пользователя

            System.out.println("Введите режим (send или receive) и имя очереди через пробел:"); // Подсказка для пользователя
            String command = scanner.nextLine(); // Чтение команды от пользователя
            out.println(command); // Отправка команды серверу

            String response = in.readLine(); // Чтение ответа от сервера
            System.out.println("Сервер: " + response); // Вывод ответа сервера

            if (command.startsWith("send")) { // Проверка, если режим отправки
                // Режим отправки сообщений
                while (true) { // Бесконечный цикл для ввода сообщений
                    String message = scanner.nextLine(); // Чтение сообщения от пользователя
                    if ("".equalsIgnoreCase(message)){ // Проверка на пустое сообщение
                        out.println(message); // Отправка пустого сообщения серверу (для завершения)
                        break; // Выход из цикла
                    }
                    byte[] byteArray = message.getBytes("UTF-8");
                    message = Arrays.toString(byteArray);
                    out.println(message); // Отправка сообщения серверу
//                    System.out.println("Сервер: " + in.readLine()); // Чтение и вывод ответа сервера
                }
            } else if (command.startsWith("receive")) { // Проверка, если режим получения
                // Режим получения сообщений
                while (true) { // Бесконечный цикл для получения сообщений
                    String byteArrayString = in.readLine(); // Чтение сообщения от сервера

                    if (byteArrayString == null || byteArrayString == "") break;  // Выход из цикла, если сервер отключен или сообщение пустое

                    // Удаляем квадратные скобки и пробелы
                    byteArrayString = byteArrayString.replace("[", "").replace("]", "").trim();

                    // Разделяем строку по запятой
                    String[] byteStrings = byteArrayString.split(",");

                    // Создаем массив байтов нужного размера
                    byte[] byteArray = new byte[byteStrings.length];

                    // Преобразуем строки в байты
                    for (int i = 0; i < byteStrings.length; i++) {
                        byteArray[i] = Byte.parseByte(byteStrings[i].trim()); // Преобразование строки в байт
                    }

                    String message = new String(byteArray, "UTF-8"); // Преобразование массива байтов обратно в строку

                    System.out.println("message: " + message); // Вывод сообщения от сервера
                }
            }
        } catch (IOException e) { // Обработка исключений ввода-вывода
            e.printStackTrace(); // Вывод информации об ошибке
        }
    }
}
