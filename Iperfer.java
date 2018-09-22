import java.io.*;
import java.net.*;
import java.util.Date;

public class Iperfer {
    static boolean clientMode;
    static boolean serverMode;
    static String hostName;
    static int port;
    static int time;
    //return code  0 = success
    //return code -1 = missing / additional args
    //return code -2 = server port is out of bounds
    //return code -3 = time is less than 0w
    public static int readCommands(String[] arguments) {
        if (arguments.length < 3) {
            return -1;
        }
        for (int i = 0; i < arguments.length; i++) {
            if (clientMode && arguments.length != 7) {
                return -1;
            }
            else if (serverMode && arguments.length != 3) {
                return -1;
            }

            if (arguments[i].equals("-c")) {
                clientMode = true;
            }
            else if (arguments[i].equals("-s")) {
                serverMode = true;
            }
            else if (arguments[i].equals("-h")) {
                if (++i >= arguments.length) {
                    return -1;
                }
                hostName = arguments[i];
            }
            else if (arguments[i].equals("-p")) {
                if (++i >= arguments.length) {
                    return -1;
                }
                int portNum = Integer.parseInt(arguments[i]);
                if (portNum < 1024 || portNum > 65535) {
                    return -2;
                }
                port = portNum;
            }
            else if (clientMode == true && arguments[i].equals("-t")) {
                if (++i >= arguments.length) {
                    return -1;
                }
                time = Integer.parseInt(arguments[i]);
                if (time < 0) {
                    return -3;
                }
            }
        }
        return 0;
    }
    public static void server(int portNum) {
        try (
                ServerSocket serverSocket = new ServerSocket(portNum);
                Socket clientSocket = serverSocket.accept();
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        ) {
            //unfinished. Check the "writing the server side of a socket tutorial"
            int totalLength = 0;
            String input;
            while ((input = in.readLine()) != null) {
                totalLength += input.length();
            }
            out.println(totalLength);
//            String input; THIS WORKS
//            while ((input = in.readLine()) != null) {
//                out.println(input.length());
//            }
        } catch (IOException e) {
            System.out.println("Caught I/O exception when trying to create a server socket");
        }
    }
    public static void client(int portNum, String portName, int time) {
        try (
                Socket echoSocket = new Socket(hostName, port);
                PrintWriter out = new PrintWriter(echoSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
        ) {
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            char[] arr = new char[125];
            for (int i = 0; i < arr.length; i++) {
                arr[i] = Character.MIN_VALUE;
            }
            for (int i = 0; i < 10; i++) {
                out.println(arr);
            }
            String userInput;
            while ((userInput = in.readLine()) != null) {
                System.out.println("Echo: " + userInput);
            }

//            String userInput;
//            long startTime = System.currentTimeMillis();
//            long elapsedTime = 0L;
//
//            while (elapsedTime < time * 1000) {
//
//                if (elapsedTime % 1000 == 0) {
//                    out.println("elapsed time: " + (elapsedTime));
//                    System.out.println("echo: " + in.readLine());
//                }
//                elapsedTime = (new Date()).getTime() - startTime;
//            }
//            while ((userInput = stdIn.readLine()) != null) {
//                out.println(userInput);
//                System.out.println("echo: " + in.readLine());
//            }
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get i/o for the connection to " + hostName);
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        int success = readCommands(args);
        System.out.println("Success: " + success);
        if (success == -1) {
            System.out.println("Error: missing or additional arguments");
        }
        else if (success == -2) {
            System.out.println("Error: port number must be in the range of 1024 to 65535");
        }
        else if (success == -3) {
            System.out.println("Error: time must be greater than 0");
        }
        if (success < 0) {
            System.exit(0);
        }

        if (clientMode) {
            client(port, hostName, time);
        }
        else if (serverMode) {
            server(port);
        }
    }
}