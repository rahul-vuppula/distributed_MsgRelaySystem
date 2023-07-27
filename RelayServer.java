
import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class RelayServer {
    private static final int SERVER_PORT = 19114;

    private static final Map<String, String> userCredentials = new HashMap<>();
    private static final Map<String, String> receiverNames = new HashMap<>();

    static {
        //Accesing userList and creating user credentials
        String fileName = "userList.txt";
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] s=line.split(" ");
                userCredentials.put(s[0],s[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Accesing receiverList
        String fileNames = "receiverList.txt";
        try (BufferedReader br = new BufferedReader(new FileReader(fileNames))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] str=line.split(" ");
                String s=str[1]+":"+str[2];
                receiverNames.put(str[0], s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            try (ServerSocket ss = new ServerSocket(SERVER_PORT)) {
                System.out.println("----Relay Server is running and waiting for connections----");

                while (true) {
                    Socket socket = ss.accept();
                    System.out.println("Connection established with Sender: " + socket.getInetAddress());

                    // Creating new thread to handle the connection
                    RelayThread relayThread = new RelayThread(socket);
                    relayThread.start();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class RelayThread extends Thread {
        private Socket socket;

        public RelayThread(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                OutputStream outputStream = socket.getOutputStream();
                PrintWriter out = new PrintWriter(outputStream, true);
                InputStream inputStream = socket.getInputStream();
                BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));

                // Receive username and password from sender
                String username = in.readLine();
                System.out.println("Received username from Sender: " + username);
                out.println("Username received");
                String password = in.readLine();
                System.out.println("Received password from Sender: " + password);

                // Authentication
                if (userCredentials.containsKey(username) && userCredentials.get(username).equals(password)) {
                    out.println("Authentication successful");
                } else {
                    out.println("Authentication failed");
                    socket.close();
                    //return;
                }

                // Receive receiver name from sender
                String receiverName = in.readLine();
                System.out.println("Received receiver name from Sender: " + receiverName);

                // Validating receiver name
                if (receiverNames.containsKey(receiverName)) {
                    out.println("Receiver connection established");
                } else {
                    out.println("Receiver connection failed");
                    socket.close();
                    //return;
                }

                // Connect to receiver
                String receiverAddress = receiverNames.get(receiverName);
                String[] receiverParts = receiverAddress.split(":");
                String receiverIP = receiverParts[0];
                int receiverPort = Integer.parseInt(receiverParts[1]);
                Socket receiverSocket = new Socket(receiverIP, receiverPort);

                // Relay messages between sender and receiver
                String message;
                BufferedReader receiverIn = new BufferedReader(new InputStreamReader(receiverSocket.getInputStream()));
                PrintWriter receiverOut = new PrintWriter(receiverSocket.getOutputStream(), true);
                while ((message = in.readLine()) != null) {
                    if (message.equals("CLOSE")) {
                        // Close connection
                        receiverOut.println("CLOSE");
                        receiverSocket.close();
                        socket.close();
                        break;
                    }

                    // Send message to receiver
                    receiverOut.println(message);

                    // Receive reply from receiver
                    String reply = receiverIn.readLine();
                    out.println("Reply from receiver: " + reply);
                }

                receiverSocket.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
