
import java.io.*;
import java.net.*;

public class Receiver {
    private static final int SERVER_PORT = 54673;

    public static void main(String[] args) {
        try {
            try (ServerSocket ss = new ServerSocket(SERVER_PORT)) {
                System.out.println("----Receiver is running and waiting for connections---");

                while (true) {
                    Socket socket = ss.accept();
                    System.out.println("Connection established with Relay Server: " + socket.getInetAddress());

                    InputStream inputStream = socket.getInputStream();
                    BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
                    OutputStream outputStream = socket.getOutputStream();
                    PrintWriter out = new PrintWriter(outputStream, true);

                    String msg;
                    while ((msg = in.readLine()) != null) {
                        if (msg.equals("CLOSE")) {
                            // Close connection
                            socket.close();
                            break;
                        }

                        // computation and printing the result
                        String longestCommonSubstring = getLongestCommonSubstring(msg);
                        int substringLength = longestCommonSubstring.length();
                        String result = "Longest Common Substring: " + longestCommonSubstring + " (Length: " + substringLength + ")";
                        out.println(result);
                    }

                    socket.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getLongestCommonSubstring(String message) {
        String[] words = message.split(" ");
        int maxCount = 0;
        String mostCommonWord = "";
        for (String word : words) {
            int count = 0;
            for (String otherWord : words) {
                if (word.equals(otherWord)) {
                    count++;
                }
            }
            if (count > maxCount) {
                maxCount = count;
                mostCommonWord = word;
            }
        }
        return mostCommonWord;
    }
}