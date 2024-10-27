import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    private static final String SERVER_ADDRESS = "127.0.0.1";
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             Scanner scanner = new Scanner(System.in)) {

            System.out.println("Введите режим (send или receive) и имя очереди через пробел:");
            String command = scanner.nextLine();
            out.println(command);

            String response = in.readLine();
            System.out.println("Сервер: " + response);

            if (command.startsWith("send")) {
                // Режим отправки сообщений
                while (true) {
                    String message = scanner.nextLine();
                    if ("".equalsIgnoreCase(message)){
                        out.println(message);
                        break;
                    }
                    out.println(message);
                    System.out.println("Сервер: " + in.readLine());
                }
            } else if (command.startsWith("receive")) {
                // Режим получения сообщений
                while (true) {
                    String message = in.readLine();
                    if (message == null || message == "") break;  // Сервер отключен
                    
                    System.out.println("Сервер: " + message);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
