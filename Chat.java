package fop.w11chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class Chat extends MiniJava {

    public static void main(String[] args) {
        Socket socket = null;
        boolean isServer = false;

        while (true) {
            String input = readString(
                    "Enter <port> in order to start the chat server "
                            + "or <host>:<port> in order to connect to a running server. "
                            + "Enter exit for exiting the chat.\n");

            if (input.equals("exit")) {
                System.out.println("Exiting.");
                return;
            }

            try {
                int colonIndex = input.indexOf(':');

                if (colonIndex == -1) {
                    int port = Integer.parseInt(input);
                    ServerSocket serverSocket = new ServerSocket(port);
                    System.out.println("Server is started, expecting connections!");
                    socket = serverSocket.accept();
                    serverSocket.close();
                    isServer = true;
                } else {
                    String host = input.substring(0, colonIndex);
                    int port = Integer.parseInt(input.substring(colonIndex + 1));
                    socket = new Socket(host, port);
                }

                break;
            } catch (UnknownHostException e) {
                System.out.println("Host unknown, try again!");
            } catch (NumberFormatException e) {
                System.out.println("Port invalid, try again!");
            }  catch (IOException e) {
                System.out.println("I/O error, try again!");
            }
        }

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream());

            Thread reading = new Thread(){
                @Override
                public void run(){
                    while (true){
                        String received = null;
                        try {
                            received = in.readLine();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (received.equals("exit")) {
                            System.out.println("exit received");
                            break;
                        }
                        System.out.println(received);
                    }
                }
            };

            Thread writer = new Thread(){
                @Override
                public void run(){
                    while (true){
                        String input = readString("> ");
                        out.println(input);
                        out.flush();
                        if (input.equals("exit")) {
                            break;
                        }
                    }
                }
            };

            reading.start();
            writer.start();

            reading.join();
            writer.join();

            System.out.println("Exiting...");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
