package com.hopding.merp.rpi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Represents the current Client connection. Information can be received from the client
 * using this Class.
 * <p>
 * This class implements the singleton design pattern. To obtain an instance,
 * you must call {@link #getClientConnection()}.
 * <p>
 * To establish a connection with the client, you must call {@link #waitForClientConnect()}.
 * <p>
 * In order to respond to input as it is received, it is necessary to specify an {@link InputHandler}
 * using the {@link #setInputHandler(InputHandler)} method.
 */
public class ClientConnection {

//////////////////////////////////// Singleton Design Pattern Setup: ///////////////////////////////////////////////////
    /**
     * Singleton instance
     */
    private static ClientConnection singleton;

    /**
     * Private constructor to enforce Singleton Design Pattern.
     */
    private ClientConnection() {}

    /**
     * Returns the singleton instance of {@code ClientConnection}.
     * @return the static instance of {@code ClientConnection}.
     */
    public static ClientConnection getClientConnection() {
        if(singleton == null)
            singleton = new ClientConnection();
        return singleton;
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private InputHandler inputHandler;
    private Thread onInputThread;
    private boolean connected;
    private ServerSocket serverSocket;
    private Socket clientSocket;

    /**
     * Blocks until a client has connected, or throws an IOException if the wait times out.
     * <p>
     * If a client does connect, then a thread is launched whose job it is to listen for input from the client. Whenever
     * a line of input is received, the {@link InputHandler#onInputLine(String)} method of the {@link InputHandler}
     * (specified by {@link #setInputHandler(InputHandler)}) will be called, and given the line of input.
     *
     * @throws IOException thrown if a timeout occurs before a client connects.
     */
    public void waitForClientConnect() throws IOException {
        serverSocket = new ServerSocket(12345);
        clientSocket = serverSocket.accept();
        PrintWriter outStream = new PrintWriter(clientSocket.getOutputStream());
        BufferedReader inStream = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        connected = true;
        onInputThread = new Thread(
                () -> {
                    String input;
                    while (connected) {
                        try {
                            if (inputHandler != null && (input = inStream.readLine()) != null) {
                                System.out.println("Received input: " + input);
                                inputHandler.onInputLine(input);
                            }
                            Thread.sleep(50);
                        } catch(IOException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
        onInputThread.setName("onInputThread");
        onInputThread.start();
    }

    /**
     * Sets the {@link InputHandler} to be used when input is received.
     *
     * @param inputHandler the {@link InputHandler} to be used when input is received.
     */
    public void setInputHandler(InputHandler inputHandler) {
        this.inputHandler = inputHandler;
    }

    /**
     * Disconnects from the client.
     */
    public void disconnect() {
        connected = false;
        try {
            clientSocket.close();
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns a boolean indicating whether or not we are currently connected to a client.
     *
     * @return true if connected, false if not
     */
    public boolean isConnectedToClient() {
        return connected;
    }
}
