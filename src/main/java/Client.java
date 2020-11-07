import java.io.File;
import java.util.*;
import java.util.concurrent.Semaphore;

/**
 * Created by QotboddiniH on 7/31/17.
 */
public class Client {
    public static Semaphore sem = new Semaphore(1);
    public static int numberOfRequests;

    public static void main (String[] args) throws InterruptedException {
        Thread.currentThread().setPriority(Thread.NORM_PRIORITY);
        File dir = new File("data");
        dir.mkdir();
        dir = new File("result");
        dir.mkdir();

        sem.acquire();
        numberOfRequests = 0;

        System.out.println("Wait a while & Let me store some data!\n");
        Receiver receiver = new Receiver();
        receiver.start();
        long storageStartTimeInMil = System.currentTimeMillis();

        int clientRequestedTimeInMin;
        Scanner sc = new Scanner(System.in);
        while ((clientRequestedTimeInMin = sc.nextInt()) != 0) {
            if (System.currentTimeMillis() - (clientRequestedTimeInMin * 60000) < storageStartTimeInMil) {
                System.out.println("I don't have enough data storage for this request!");
                continue;
            }

            numberOfRequests++;

            ReadAndAnalyze readAndAnalyze = new ReadAndAnalyze(clientRequestedTimeInMin * 60);
            readAndAnalyze.start();
            sem.acquire();
            readAndAnalyze.stop();
            System.out.println("Now you can try another request or print 0!");
        }

        System.exit(0);
    }
}
