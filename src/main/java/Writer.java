import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

/**
 * Created by QotboddiniH on 7/29/17.
 */
public class Writer extends Thread {
    HashMap<Long, Actor> actors = new HashMap<Long, Actor>();
    HashMap<Long, Repo> repos = new HashMap<Long, Repo>();

    public Writer() {
        Thread.currentThread().setPriority(Thread.NORM_PRIORITY);
    }


    public void update(JsonData jsonData) throws InterruptedException {
        Long SCT = System.currentTimeMillis();

        Actor actor = jsonData.actor;
        try(FileWriter fw = new FileWriter("data/ActorMain" + (SCT/60000) + ".txt", true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw))
        {
            out.println(System.currentTimeMillis());
            out.println(actor.id);
            out.println(actor.login);
        } catch (IOException e) {
            //exception handling left as an exercise for the reader
        }

        Repo repo = jsonData.repo;
        try(FileWriter fw = new FileWriter("data/RepoMain" + (SCT/60000) + ".txt", true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw))
        {
            out.println(System.currentTimeMillis());
            out.println(repo.id);
            out.println(repo.name);
        } catch (IOException e) {
            //exception handling left as an exercise for the reader
        }

    }

    @Override
    public void run() {
        while (true) {
            while (Receiver.jsonList.isEmpty()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            while (!Receiver.jsonList.isEmpty()) {
                try {
                    update(Receiver.jsonList.getFirst());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Receiver.jsonList.removeFirst();
            }

        }
    }
}