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
    private static ArrayList<Integer> data = new ArrayList<>();
    private static ArrayList<String> devices = new ArrayList<>();
    private static String hc05Url;
    private static String URL;
    boolean scanFinished = false;
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

    public ArrayList<Integer> getData() {
        return data;
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

    public void send(byte[] a) throws Exception {
        os.write(a, 0, 3);
    }

    public void recieveData() throws IOException {
        data.clear();
        //zczytywanie danych z czujnika, zamiana na int

        DataInputStream disReader = new DataInputStream(is);
        if (is.available() > 0) {

            for (int i = 0; i < 8; i++) {
                int read1 = disReader.readUnsignedShort();
                System.out.println(read1);
                data.add(Integer.valueOf(read1));
            }


        }
        System.out.println(data);


        System.out.println("wartosc");

        //zapisanie wartosci w tablicy
    }

    //Test
    public void drawTestData() {
        data.add(new Random().nextInt(200));
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
