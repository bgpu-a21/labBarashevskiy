import java.io.*;  // Импортирование классов для работы с потоками ввода-вывода
import java.net.*; // Импортирование классов для работы с сетевыми сокетами
import java.util.concurrent.*; // Импортирование классов для работы с конкурентными коллекциями и потоками

public class Server { // Объявление публичного класса Server
    private static final int PORT = 12345; // Определение порта, на котором будет работать сервер
    private static final ConcurrentHashMap<String, BlockingQueue<String>> queues = new ConcurrentHashMap<>(); // Создание потокобезопасной карты для хранения очередей сообщений

    public static void main(String[] args) { // Основной метод программы
        try (ServerSocket serverSocket = new ServerSocket(PORT)) { // Создание серверного сокета для ожидания подключений на указанном порту
            System.out.println("Сервер запущен... Ожидание подключений."); // Сообщение о запуске сервера

            while (true) { // Бесконечный цикл для обработки входящих соединений
                Socket clientSocket = serverSocket.accept(); // Ожидание подключения клиента
                System.out.println("Клиент подключен: " + clientSocket.getInetAddress()); // Вывод информации о подключившемся клиенте
                new ClientHandler(clientSocket).start(); // Создание нового потока для обработки клиента
            }
        } catch (IOException e) { // Обработка исключений ввода-вывода
            e.printStackTrace(); // Вывод информации об ошибке
        }
    }

    private static class ClientHandler extends Thread { // Вложенный класс для обработки клиента в отдельном потоке
        private final Socket clientSocket; // Сокет для клиента

        public ClientHandler(Socket socket) { // Конструктор класса
            this.clientSocket = socket; // Инициализация сокета клиента
        }

        @Override
        public void run() { // Метод, выполняемый в потоке BufferedWriter
            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); // Создание BufferedReader для чтения данных от клиента
                 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) { // Создание PrintWriter для отправки данных клиенту

                String command = in.readLine(); // Чтение команды от клиента
                String[] parts = command.split(" ", 2); // Разделение команды на части

                if (parts.length != 2) { // Проверка корректности количества частей команды
                    out.println("Ошибка: неверный формат команды. Используйте формат: <send/receive> <имя_очереди>"); // Сообщение об ошибке
                    return; // Выход из метода
                }

                String mode = parts[0]; // Получение режима (send или receive)
                String queueName = parts[1]; // Получение имени очереди
                queues.putIfAbsent(queueName, new LinkedBlockingQueue<>()); // Создание очереди, если она еще не существует

                if ("send".equalsIgnoreCase(mode)) { // Проверка, если режим отправки
                    out.println("Вы в режиме отправки сообщений в очередь " + queueName + ". Введите сообщение:"); // Сообщение о переходе в режим отправки
                    while (true) { // Бесконечный цикл для отправки сообщений
                        String byteArrayString  = in.readLine(); // Чтение сообщения от клиента

                        if (byteArrayString == null) { // Проверка на отключение клиента
                            break; // Выход из цикла
                        }
                        queues.get(queueName).add(byteArrayString); // Добавление сообщения в очередь
//                        out.println("Сообщение добавлено в очередь " + queueName); // Подтверждение добавления сообщения
                    }
                } else if ("receive".equalsIgnoreCase(mode)) { // Проверка, если режим получения
                    out.println("Вы в режиме получения сообщений из очереди " + queueName + ". Ожидание сообщений..."); // Сообщение о переходе в режим получения
                    while (true) { // Бесконечный цикл для получения сообщений
                        String message = queues.get(queueName).take(); // Ожидание сообщения из очереди
//                        System.out.println("message: " + message);
                        if (message != "") { // Проверка, если сообщение не пустое

                            out.println(message); // Отправка сообщения и его длины клиенту
                            // Удаление квадратных скобок
                            message = message.replaceAll("\\[|\\]", "");

                            // Разделение строки на массив строк
                            String[] stringArray = message.split(",\\s*"); // Учитываем возможные пробелы

                            // Подсчет количества элементов
                            int count = stringArray.length; // Количество элементов в массиве
                            System.out.println("message " + count + "\n" + message); // Вывод сообщения и его длины в консоль
                        } else { // Если сообщение пустое
                            out.println("");
                        }
                    }
                } else { // Если режим не распознан
                    out.println("Ошибка: неверный режим. Используйте send или receive."); // Сообщение об ошибке
                }

            } catch (IOException | InterruptedException e) { // Обработка исключений ввода-вывода и прерывания
                e.printStackTrace(); // Вывод информации об ошибке
            } finally { // Блок finally для закрытия ресурсов
                try {
                    clientSocket.close(); // Закрытие сокета клиента
                } catch (IOException e) { // Обработка исключений при закрытии
                    e.printStackTrace(); // Вывод информации об ошибке
                }
            }
        }
    }
}
