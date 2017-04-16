package sample;

import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import java.io.*;

/**
 * Connects to HC-05 bluetooth module
 */
public class HC05 {
    static OutputStream os;
    static StreamConnection streamConnection;
    static InputStream is;
    static long time3;

    //set your hc05Url
    static String hc05Url = "btspp://301412260760:1;authenticate=false;encrypt=false;master=false";


    public void go() throws IOException {
        streamConnection = (StreamConnection) Connector.open(hc05Url);
        os = streamConnection.openOutputStream();
        is = streamConnection.openInputStream();

        System.out.println("Connected to " + hc05Url);
    }
    public static void send(int a) throws Exception{
        os.write(a);
    }

    public static void close() throws IOException {
        os.close();
        is.close();
        streamConnection.close();
    }
    public static void getValueOfDetector() throws IOException {

        //wielokrotne pobieranie danych
        for(int i=0;;i++) {

            //czas rozpoczecia pobierania jednej wartosci
            long time1= System.currentTimeMillis();

            //zczytywanie danych z czujnika, zamiana na int
            BufferedReader bReader = new BufferedReader(new InputStreamReader(is));
            String read = bReader.readLine();
            int reade = Integer.valueOf(read);
            System.out.println(reade);

            //zapisanie wartosci w tablicy -zestawic w tabeli
            int tablica1[]=new int[100];
            tablica1[i]=reade;

            //czas zakonczenia pobierania jednej wartosci
            long time2=System.currentTimeMillis();

            //roznica czasow - czas jednego pobrania
            time3=time2-time1;
            System.out.println(time3);

            //zapisanie czasu w tablicy -zestawic w tabeli
            long tablica2[]=new long[100];
            tablica2[i]=time3;
            time3+=time3;
        }
    }
}
