package com.hopding.merp.rpi;

import jssc.*;

/**
 * Represents the current Arduino serial connection. Information can be sent
 * and received to the Arduino using this class.
 * <p>
 * This class implements the singleton design pattern. To obtain an instance,
 * you must call {@link #establishConnection()}.
 * <p>
 * To send information to the Arduino, use the {@link #write(String)} method.
 */
public class ArduinoConnection {
    /** Used to attempt handshake with serial ports, to see if they are the Arduino */
    private static final String HANDSHAKE_QUERY_STRING = "Are you my Arduino?\n";

    /** Class singleton */
    private static ArduinoConnection singleton;

    /** Reference to the current Serial Port */
    private static SerialPort serialPort;

    /** Indicates whether a successful connection to the Arduino has been established */
    private static boolean connEstablished = false;

    /** Private, constructor. Enforces singleton policy/pattern for this class */
    private ArduinoConnection() {
    }

    /**
     * Searches through all serial ports, attempting to connect with and send a
     * message to each. The Arduino is programmed to respond to the particular message
     * being sent with a unique reply, indicating it is the Arduino of interest.
     * <p>
     * If one of the ports sends back the correct response to our message, we
     * attempt to connect with it. If no ports are available, no responses are
     * received, or the connection attempt fails, null will be returned.
     * <p>
     * If connection attempt is successful, an instance of com.hopding.merp.rpi.ArduinoConnection
     * will be returned.
     *
     * @return {@link ArduinoConnection} object if connection was successful.
     *         If connection failed, then returns null.
     */
    public static ArduinoConnection establishConnection() throws InterruptedException {
        // Get all serial port names into String[]
        // We're gonna use this to figure out which
        // port the Arduino is connected to.
        String[] portNames = SerialPortList.getPortNames();

        // If there are no ports, we definitely won't be doing any
        // connecting, so return null.
        if (portNames.length == 0)
            return null;

        for (String portName : portNames) {
            // Try to open/connect with, the serial port
            try {
                serialPort = new SerialPort(portName);
                serialPort.openPort();

                // Arduino resets after the serial port is connected
                // to. So, we need to wait a couple seconds before
                // proceeding, to give the Arduino time to reboot.
                Thread.sleep(2000);
            } catch (SerialPortException ex) {
                // If an exception is thrown here, then we failed to
                // open the port - which also means this wasn't the
                // Arduino port.
                System.out.println("Failed to find Arduino on port: " + portName);
                ex.printStackTrace();
                continue;
            }

            initPort();
            tryHandshake();

            // We just tried to handshake with the port. If the
            // handshake was successful, then connEstablished will
            // have been set to true. That means we managed to find the
            // Arduino and connect with it! So, return an instance of
            // the class singleton.
            if(connEstablished == true) {
                System.out.println("Found Arduino on port: " + portName);
                if(singleton == null)
                    singleton = new ArduinoConnection();
                return singleton;
            }
            else {
                try {
                    System.out.println("Failed to find Arduino on port: " + portName);
                    serialPort.closePort();
                } catch (SerialPortException e) {
                    e.printStackTrace();
                }
            }
        }

        // If we reach this point, then we failed to find
        // the Arduino and handshake with it :( so return
        // null.
        System.out.println("Failed to find the Arduino.");
        return null;
    }

    /**
     * Performs setup work for a successfully opened serial port.
     */
    private static void initPort() {
        try {
            serialPort.setParams(SerialPort.BAUDRATE_9600,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);

            serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN |
                    SerialPort.FLOWCONTROL_RTSCTS_OUT);

            StringBuilder response = new StringBuilder();
            serialPort.addEventListener((SerialPortEvent event) -> {
                if(event.isRXCHAR() && event.getEventValue() > 0) {
                    try {
                        response.append(serialPort.readString(event.getEventValue()));
                        if(response.toString().charAt(response.length() - 1) == '\n')
                            if(response.toString().contains("Yes, I am!")) {
                                connEstablished = true;
                                response.setLength(0);
                            }
                    }
                    catch (SerialPortException ex) {
                        ex.printStackTrace();
                    }
                }
            }, SerialPort.MASK_RXCHAR);
        }
        catch(SerialPortException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Attempts to handshake with a serial port.
     * If handshake is successful, the connEstablished var
     * will be set to true.
     */
    private static void tryHandshake() {
        try {
            System.out.println("Trying to handshake with: " + serialPort.getPortName());
            // Send handshake query
            serialPort.writeString(HANDSHAKE_QUERY_STRING);

            // Wait for a response...
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (SerialPortException e1) {
            e1.printStackTrace();
        }
    }

    /**
     * Attempts to send a message to the Arduino.
     *
     * @param msg String to be sent.
     * @return True if message was sent, false if
     *         msg was null or empty, or failed to
     *         be sent.
     */
    public boolean write(String msg) {
        // If message is empty, then no need to
        // bother with sending it.
        if(msg == null || msg.length() == 0)
            return false;

        // Try sending the message
        try {
            serialPort.writeString(msg);
        } catch (SerialPortException e) {
            e.printStackTrace();
            // If we reach this point, then we failed to send
            // the message, so return false.
            return false;
        }

        // If we got to this point, then we succeeded in sending
        // the message!
        return true;
    }

    /**
     * Disconnects from the connected serial port (if any).
     * Resets singleton instance to null.
     */
    public void disconnect() {
        if(serialPort != null)
            try {
                serialPort.closePort();
            } catch (SerialPortException e) {
                e.printStackTrace();
            }

        serialPort = null;
        singleton = null;
    }
}
