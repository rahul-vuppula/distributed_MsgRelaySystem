
import java.io.*;
import java.net.*;

public class Sender {
    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 19114;

    public static void main(String[] args) {
        try {
            Socket s = new Socket(SERVER_IP, SERVER_PORT);
            OutputStream outputStream = s.getOutputStream();
            PrintWriter out = new PrintWriter(outputStream, true);
            InputStream inputStream = s.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));

            // enter username
            System.out.print("Enter username: ");
            BufferedReader user = new BufferedReader(new InputStreamReader(System.in));
            String username = user.readLine();
            out.println(username);

            // Receiving acknowledgement
            String acknowledgement = in.readLine();
            System.out.println("Acknowledgement from relay server: " + acknowledgement);

            // entering password
            System.out.print("Enter password: ");
            BufferedReader pwd = new BufferedReader(new InputStreamReader(System.in));
            String password = pwd.readLine();
            out.println(password);

            // Authentication from the relay server
            String authResult = in.readLine();
            System.out.println("Authentication result from relay server: " + authResult);

            // entering receiver name and connecting receiver
            System.out.print("Enter receiver name: ");
            BufferedReader recvr = new BufferedReader(new InputStreamReader(System.in));
            String receiverName = recvr.readLine();
            out.println(receiverName);
            String recvResult = in.readLine();
            System.out.println("Receiver connection result from relay server: " + recvResult);

            // Sending and receiving messages from relay server
            String msg;
            while (true) {
                System.out.print("Enter message or 'CLOSE' to quit: ");
                msg = user.readLine();
                if (msg.equals("CLOSE")){
                    out.println("CLOSE");
                    break;
                }
                out.println(msg);
                String reply = in.readLine();
                System.out.println("Reply from relay server: " + reply);
            }

            s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
