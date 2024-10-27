import java.io.*;
import java.net.*;
import java.util.concurrent.*;

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
                 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                String command = in.readLine();
                String[] parts = command.split(" ", 2);

                if (parts.length != 2) {
                    out.println("Ошибка: неверный формат команды. Используйте формат: <send/receive> <имя_очереди>");
                    return;
                }

                String mode = parts[0];
                String queueName = parts[1];
                queues.putIfAbsent(queueName, new LinkedBlockingQueue<>());

                if ("send".equalsIgnoreCase(mode)) {
                    out.println("Вы в режиме отправки сообщений в очередь " + queueName + ". Введите сообщение:");
                    while (true) {
                        String message = in.readLine();
                        if (message == null) {
                            // notifyReceiveClients(queueName);
                            break;  // Отключение клиента
                        }
                        queues.get(queueName).add(message);
                        out.println("Сообщение добавлено в очередь " + queueName);
                    }
                } else if ("receive".equalsIgnoreCase(mode)) {
                    out.println("Вы в режиме получения сообщений из очереди " + queueName + ". Ожидание сообщений...");
                    while (true) {
                        String message = queues.get(queueName).take(); // Ждет, пока сообщение не будет добавлено
                        if(message != ""){
                            byte[] bytes = message.getBytes("UTF-8");
                            int byteLength = bytes.length;
                            out.println("message " + byteLength + "\n" + message);
                            System.out.println("message " + byteLength + "\n" + message);
                        }else{
                            out.println("Сервер отключен");
                            out.println("");
                        }
                        
                    }
                } else {
                    out.println("Ошибка: неверный режим. Используйте send или receive.");
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
