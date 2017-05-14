package sample;

import javax.bluetooth.*;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import java.io.*;
import java.util.Random;

/**
 * Connects to HC-05 bluetooth module
 */
public class HC05{
    private static OutputStream os;
    private static StreamConnection streamConnection;
    private static InputStream is;
    private static int i=0;
    private int data[] = new int[1000];
    boolean scanFinished = false;
    RemoteDevice hc05device;

    //set your hc05Url

    //Bt Piotrek
    //private static String hc05Url = "btspp://201611226383:1;authenticate=false;encrypt=false;master=false";
    //bt Jacek
    //private static String hc05Url = "btspp://301412260760:1;authenticate=false;encrypt=false;master=false";
    //Bt Team 1
    //private static String hc05Url = "btspp://98D33380730A:1;authenticate=false;encrypt=false;master=false";

    private static String hc05Url;

    public int getData(int i){
        return this.data[i];
    }

    public void go() throws IOException {
        streamConnection = (StreamConnection) Connector.open(hc05Url);
        os = streamConnection.openOutputStream();
        is = streamConnection.openInputStream();

        System.out.println("Connected to " + hc05Url);
    }

    public void send(int a) throws Exception {
        os.write(a);
    }

    public static void close() throws IOException {
        os.close();
        is.close();
        streamConnection.close();
    }

    public void getValueOfDetector() throws IOException {


        //zczytywanie danych z czujnika, zamiana na int
        BufferedReader bReader = new BufferedReader(new InputStreamReader(is));
        String read = bReader.readLine();
        int reade = Integer.valueOf(read);
        System.out.println(reade);

        //zapisanie wartosci w tablicy
        data[i] = reade;

        i++;
    }
    //Test
    public void drawTestData(){
        data[i]= new Random().nextInt(200);
        i++;
    }

    public void search() throws Exception{
        //scan for all devices:
        scanFinished = false;
        LocalDevice.getLocalDevice().getDiscoveryAgent().startInquiry(DiscoveryAgent.GIAC, new DiscoveryListener() {
            @Override
            public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
                try {
                    String name = btDevice.getFriendlyName(false);
                    System.out.format("%s (%s)\n", name, btDevice.getBluetoothAddress());
                    if (name.matches("HC.*")) {
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
            //this is easier to understand (for me) as the thread stuff examples from bluecove
            Thread.sleep(500);
        }

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
    }
}
