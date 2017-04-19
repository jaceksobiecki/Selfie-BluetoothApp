package sample;

import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import java.io.*;

/**
 * Connects to HC-05 bluetooth module
 */
public class HC05 implements Runnable {
    private static OutputStream os;
    private static StreamConnection streamConnection;
    private static InputStream is;
    private static long time3;
    private static int i=0;
    private static int tablica1[] = new int[100];
    private static long tablica2[] = new long[100];

    //set your hc05Url
    private static String hc05Url = "btspp://201611226383:1;authenticate=false;encrypt=false;master=false";
    //bt Jacek
    //"btspp://301412260760:1;authenticate=false;encrypt=false;master=false";
    //bt Piotrek
    //"btspp://201611226383:1;authenticate=false;encrypt=false;master=false";

    public void go() throws IOException {
        streamConnection = (StreamConnection) Connector.open(hc05Url);
        os = streamConnection.openOutputStream();
        is = streamConnection.openInputStream();

        System.out.println("Connected to " + hc05Url);
    }

    public void send(int a) throws Exception {
        os.write(a);
    }

    public void close() throws IOException {
        os.close();
        is.close();
        streamConnection.close();
    }

    public void getValueOfDetector() throws IOException {


        //czas rozpoczecia pobierania jednej wartosci
        long time1 = System.currentTimeMillis();

        //zczytywanie danych z czujnika, zamiana na int
        BufferedReader bReader = new BufferedReader(new InputStreamReader(is));
        String read = bReader.readLine();
        int reade = Integer.valueOf(read);
        System.out.println(reade);

        //zapisanie wartosci w tablicy -zestawic w tabeli
        tablica1[i] = reade;

        //czas zakonczenia pobierania jednej wartosci
        long time2 = System.currentTimeMillis();

        //roznica czasow - czas jednego pobrania
        time3 = time2 - time1;
        System.out.println(time3);

        //zapisanie czasu w tablicy -zestawic w tabeli
        tablica2[i] = time3;
        time3 += time3;

        i++;
    }

    @Override
    public void run() {
        while (true) {
            try {
                getValueOfDetector();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
