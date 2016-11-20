package com.hopding.merp.rpi;

import java.io.*;

public class Main {
    public static void main(String[] args) throws InterruptedException, IOException {
        System.out.println("Trying to find Arduino...");
        ArduinoConnection ac = ArduinoConnection.establishConnection();
        if(ac == null) { // Arduino wasn't found, so must end program
            System.out.println("Exiting program.");
            System.exit(1);
        }

        ClientConnection cc = ClientConnection.getClientConnection();
        try {
            System.out.println("Waiting for client to connect...");
            cc.waitForClientConnect();
            System.out.println("Successfully connected with a client!");
        } catch(IOException e) { // Failed to connect with a client, so must end program
            System.out.println("Failed to connect with a client. Exiting Program.");
            System.exit(1);
        }

        // Arduino was found and connected with, and a client has successfully connected. So now we
        // will specify the logic to be executed whenever a line of input is received from the client.
        cc.setInputHandler(
            (String inputLine) -> {
                if(inputLine.equals("forward"))
                    ac.write("[180,180]");
                else if(inputLine.equals("reverse"))
                    ac.write("[0,0]");
                else if(inputLine.equals("right"))
                    ac.write("[180,0]");
                else if(inputLine.equals("left"))
                    ac.write("[0,180]");
                else if(inputLine.equals("stop"))
                    ac.write("[90,90]");
                else if(inputLine.matches("\\[\\d?\\d?\\d,\\d?\\d?\\d\\]")) {
                    String[] pwmString = inputLine.replace("[", "").replace("]", "").split(",");
                    int leftVal = Integer.parseInt(pwmString[0]);
                    int rightVal = Integer.parseInt(pwmString[1]);
                    if(leftVal <= 180 && rightVal <= 180 && leftVal >=0 && rightVal >= 0)
                        ac.write(inputLine);
                    else
                        System.err.printf("!!Input Error!!: One or both values within the input \"%s\" " +
                                " are not within the range 0 to 180 - unable to respond.\n", inputLine);
                }
                else if(inputLine.equals("disconnect")) {
                    ac.disconnect();
                    cc.disconnect();
                    System.out.println("Exiting program.");
                    System.exit(0);
                }
                else
                    System.err.printf("!!Input Error!!: Unable to recognize input line: %s\n", inputLine);
            }
        );

    }
}
