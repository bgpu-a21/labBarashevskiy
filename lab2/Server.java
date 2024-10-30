import java.io.*;  // Импортирование классов для работы с потоками ввода-вывода
import java.net.*; // Импортирование классов для работы с сетевыми сокетами
import java.util.concurrent.*; // Импортирование классов для работы с конкурентными коллекциями и потоками

public class Server {
    private static final int PORT = 12345;
    private static final ConcurrentHashMap<String, BlockingQueue<String>> queues = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Сервер запущен... Ожидание подключений.");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Клиент подключен: " + clientSocket.getInetAddress());
                new ClientHandler(clientSocket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler extends Thread {
        private final Socket clientSocket;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()))) {

                String command = in.readLine();
                String[] parts = command.split(" ", 2);

                if (parts.length != 2) {
                    out.write("Ошибка: неверный формат команды. Используйте формат: <send/receive> <имя_очереди>\n");
                    out.flush();
                    return;
                }

                String mode = parts[0];
                String queueName = parts[1];
                queues.putIfAbsent(queueName, new LinkedBlockingQueue<>());

                if ("send".equalsIgnoreCase(mode)) {
                    out.write("Вы в режиме отправки сообщений в очередь " + queueName + ". Введите сообщение:\n");
                    out.flush();

                    while (true) {
                        String byteArrayString = in.readLine();
                        if (byteArrayString == null) {
                            break;
                        }
                        queues.get(queueName).add(byteArrayString);
                    }
                } else if ("receive".equalsIgnoreCase(mode)) {
                    out.write("Вы в режиме получения сообщений из очереди " + queueName + ". Ожидание сообщений...\n");
                    out.flush();

                    while (true) {
                        String message = queues.get(queueName).take();
                        if (!message.isEmpty()) {
                            
                            System.out.println(clientSocket.isClosed()+" "+clientSocket.isConnected()); 
                            message = message.replaceAll("\\[|\\]", "");
                            String[] stringArray = message.split(",\\s*");
                            int count = stringArray.length;
                            System.out.println("message " + count + "\n" + message);

                            try {
                                out.write(message + "\n");
                                out.flush();
                            } catch (SocketException e) {

                                queues.get(queueName).add(message);

                                }
                        } else {
                            out.write("\n");
                            out.flush();
                        }
                    }
                } else {
                    out.write("Ошибка: неверный режим. Используйте send или receive.\n");
                    out.flush();
                }

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
