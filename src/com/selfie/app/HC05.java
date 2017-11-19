package com.selfie.app;

import javax.bluetooth.*;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;

/**
 * Connects to HC-05 bluetooth module
 */
public class HC05 {
    private static OutputStream os;
    private static StreamConnection streamConnection;
    private static InputStream is;
    private static int i = 0;
    private short[] j_bufferR = new short[14];
    private byte[] rFlag = new byte[3];
    private byte[] j_bufferS = new byte[14];
    private static ArrayList<Short> rData = new ArrayList<>();
    private static ArrayList<String> devices = new ArrayList<>();
    private static String hc05Url;
    private static String URL;
    boolean scanFinished = false;
    int syncByte;
    //set your hc05Url

    //Bt Piotrek
    //private static String hc05Url = "btspp://201611226383:1;authenticate=false;encrypt=false;master=false";
    //bt Jacek
    //private static String hc05Url = "btspp://301412260760:1;authenticate=false;encrypt=false;master=false";
    //Bt Team 1
    //private static String hc05Url = "btspp://98D33380730A:1;authenticate=false;encrypt=false;master=false";
    RemoteDevice hc05device;
    char[] bytes = new char[32];

    public static String getURL() {
        return URL;
    }

    public static void setURL(String URL) {
        HC05.URL = URL;
    }

    public static String getHc05Url() {
        return hc05Url;
    }

    public static void close() throws IOException {
        os.close();
        is.close();
        streamConnection.close();
    }

    public ArrayList<String> getDevices() {
        return devices;
    }

    public ArrayList<Short> getData() {
        return rData;
    }

    public void readUrl() throws IOException {
        BufferedReader in = new BufferedReader(new FileReader("Url.txt"));
        URL = in.readLine();
    }

    public void go() {
        System.out.println("Connecting");
        try {
            streamConnection = (StreamConnection) Connector.open(URL);
            os = streamConnection.openOutputStream();
            is = streamConnection.openInputStream();
            System.out.println("Connected to " + URL);
        } catch (Exception e) {
            StartupController.setProblem(true);
            StartupController.setInfo("Can't connect");
        }
    }

    public void send(int a) throws Exception {
        os.write(a);
    }

    public void send(byte[] sData1, byte[] sFlag) throws Exception {
        j_bufferS[0] = (byte) ((sFlag[0]) & 0xFF);
        j_bufferS[1] = (byte) ((sData1[0]) & 0xFF);
        j_bufferS[2] = (byte) ((sData1[0] >> 8 | sData1[1] << 3) & 0xFF);
        j_bufferS[3] = (byte) ((sData1[1] >> 5 | sData1[2] << 6) & 0xFF);
        j_bufferS[4] = (byte) ((sData1[2] >> 2) & 0xFF);
        j_bufferS[5] = (byte) ((sData1[3] << 1 | sData1[2] >> 10) & 0xFF);
        j_bufferS[6] = (byte) ((sData1[4] << 4 | sData1[3] >> 7) & 0xFF);
        j_bufferS[7] = (byte) ((sData1[5] << 7 | sData1[4] >> 4) & 0xFF);
        j_bufferS[8] = (byte) ((sData1[5] >> 1) & 0xFF);
        j_bufferS[9] = (byte) ((sData1[6] << 2 | sData1[5] >> 9) & 0xFF);
        j_bufferS[10] = (byte) ((sData1[7] << 5 | sData1[6] >> 6) & 0xFF);
        j_bufferS[11] = (byte) ((sData1[7] >> 3) & 0xFF);
        j_bufferS[12] = (byte) ((sFlag[1]) & 0xFF);
        j_bufferS[13] = (byte) ((sFlag[2]) & 0xFF);
        os.write(j_bufferS, 0, 14);

    }

    public void receiveData() throws IOException {
        DataInputStream disReader = new DataInputStream(is);
        if (is.available() > 0) {
            rFlag[0] = disReader.readByte();
            if (rFlag[0] == 0xFF) {
                    syncByte = 0xFE;
                    for (int i = 0; i < 11; i++) {
                        j_bufferR[i] = disReader.readByte();
                    }
                    rData.add((short) ((j_bufferR[0] | j_bufferR[1] << 8) & 0x7FF));
                    rData.add((short) ((j_bufferR[1] >> 3 | j_bufferR[2] << 5) & 0x7FF));
                    rData.add((short) ((j_bufferR[2] >> 6 | j_bufferR[3] << 2 | j_bufferR[4] << 10) & 0x7FF));
                    rData.add((short) ((j_bufferR[4] >> 1 | j_bufferR[5] << 7) & 0x7FF));
                    rData.add((short) ((j_bufferR[5] >> 4 | j_bufferR[6] << 4) & 0x7FF));
                    rData.add((short) ((j_bufferR[6] >> 7 | j_bufferR[7] << 1 | j_bufferR[8] << 9) & 0x7FF));
                    rData.add((short) ((j_bufferR[8] >> 2 | j_bufferR[9] << 6) & 0x7FF));
                    rData.add((short) ((j_bufferR[9] >> 5 | j_bufferR[10] << 3) & 0x7FF));

                    syncByte = 0xFD;
                    rFlag[1] = disReader.readByte();
                    rFlag[2] = disReader.readByte();

                    System.out.println(rData);
            }
            else if (rFlag[0] == 0xFF) {
                    syncByte = 0xFE;
                    for (int i = 0; i < 11; i++) {
                        j_bufferR[i] = disReader.readByte();
                    }
                    rData.add((short) ((j_bufferR[0] | j_bufferR[1] << 8) & 0x7FF));
                    rData.add((short) ((j_bufferR[1] >> 3 | j_bufferR[2] << 5) & 0x7FF));
                    rData.add((short) ((j_bufferR[2] >> 6 | j_bufferR[3] << 2 | j_bufferR[4] << 10) & 0x7FF));
                    rData.add((short) ((j_bufferR[4] >> 1 | j_bufferR[5] << 7) & 0x7FF));
                    rData.add((short) ((j_bufferR[5] >> 4 | j_bufferR[6] << 4) & 0x7FF));
                    rData.add((short) ((j_bufferR[6] >> 7 | j_bufferR[7] << 1 | j_bufferR[8] << 9) & 0x7FF));
                    rData.add((short) ((j_bufferR[8] >> 2 | j_bufferR[9] << 6) & 0x7FF));
                    rData.add((short) ((j_bufferR[9] >> 5 | j_bufferR[10] << 3) & 0x7FF));

                    syncByte = 0xFD;
                    rFlag[1] = disReader.readByte();
                    rFlag[2] = disReader.readByte();

                    System.out.println(rData);
            }
        }
        /*
        if(data.get(0)==100)
            System.out.println("STM READY");
        else if(data.get(0)==200)
            System.out.println("STM STOPPED");
            */
    }

    public void search() throws Exception {
        //scan for all devices:
        System.out.println("searching for devices");
        scanFinished = false;
        LocalDevice.getLocalDevice().getDiscoveryAgent().startInquiry(DiscoveryAgent.GIAC, new DiscoveryListener() {
            @Override
            public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
                try {
                    String name = btDevice.getFriendlyName(false);
                    devices.add(name + "    " + btDevice.getBluetoothAddress());
                    System.out.format("%s (%s)\n", name, btDevice.getBluetoothAddress());
                    if (name.matches("HC-05")) {
                        hc05device = btDevice;
                        System.out.println("got it!");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void inquiryCompleted(int discType) {
                scanFinished = true;
            }

            @Override
            public void serviceSearchCompleted(int transID, int respCode) {
            }

            @Override
            public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
            }
        });
        while (!scanFinished) {
            Thread.sleep(500);
        }
        try {
            //search for services:
            UUID uuid = new UUID(0x1101); //scan for btspp://... services (as HC-05 offers it)
            UUID[] searchUuidSet = new UUID[]{uuid};
            int[] attrIDs = new int[]{
                    0x0100 // service name
            };
            scanFinished = false;
            LocalDevice.getLocalDevice().getDiscoveryAgent().searchServices(attrIDs, searchUuidSet,
                    hc05device, new DiscoveryListener() {
                        @Override
                        public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
                        }

                        @Override
                        public void inquiryCompleted(int discType) {
                        }

                        @Override
                        public void serviceSearchCompleted(int transID, int respCode) {
                            scanFinished = true;
                        }

                        @Override
                        public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
                            for (int i = 0; i < servRecord.length; i++) {
                                hc05Url = servRecord[i].getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
                                if (hc05Url != null) {
                                    break; //take the first one
                                }
                            }
                        }
                    });

            while (!scanFinished) {
                Thread.sleep(500);
            }

            System.out.println(hc05device.getBluetoothAddress());
            System.out.println(hc05Url);
            System.out.println(devices);
        } catch (NullPointerException e) {
            devices.add("No devices found");
        }
    }

    public void saveUrl() throws FileNotFoundException {
        URL = hc05Url;
        PrintWriter printWriter = new PrintWriter("Url.txt");
        printWriter.write(URL);
        printWriter.close();
    }
}
