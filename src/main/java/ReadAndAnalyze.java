import java.io.*;
import java.util.*;

public class ReadAndAnalyze extends Thread{
    private long startTimeInMil;
    private long endTimeInMil;
    HashMap<Long, Actor> actors;
    HashMap<Long, Repo> repos;

    public ReadAndAnalyze(int timePeriodInSec) throws InterruptedException {
        Thread.currentThread().setPriority(Thread.NORM_PRIORITY);

        endTimeInMil = System.currentTimeMillis();
        startTimeInMil = endTimeInMil - (timePeriodInSec*1000);

        actors = new HashMap<Long, Actor>();
        repos = new HashMap<Long, Repo>();
    }


    @Override
    public void run() {
        long buffTime = startTimeInMil / 60000;

        while (buffTime <= (endTimeInMil / 60000) ){


            //Actor Handling
            try (BufferedReader br = new BufferedReader(new FileReader("data/ActorMain" + buffTime + ".txt"))) {
                String line;
                while ((line = br.readLine()) != null) {
                    long savedTime = Long.valueOf(line);

                    Actor actor = new Actor();
                    if (startTimeInMil < savedTime && savedTime < endTimeInMil) {
                        line = br.readLine();
                        actor.id = Long.valueOf(line);
                        actor.login = br.readLine();

                        if (actors.containsKey(actor.id)) {
                            actor.cntr = actors.get(actor.id).cntr;
                            actors.remove(actor.id);
                        }
                        actor.cntr++;
                        actors.put(actor.id, actor);

                    }
                    else {
                        br.readLine();
                        br.readLine();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


            //Repo Handling
            try (BufferedReader br = new BufferedReader(new FileReader("data/RepoMain" + buffTime + ".txt"))) {
                String line;
                while ((line = br.readLine()) != null) {
                    long savedTime = Long.valueOf(line);

                    Repo repo = new Repo();
                    if (startTimeInMil < savedTime && savedTime < endTimeInMil) {
                        line = br.readLine();
                        repo.id = Long.valueOf(line);
                        repo.name = br.readLine();

                        if (repos.containsKey(repo.id)) {
                            repo.cntr = repos.get(repo.id).cntr;
                            repos.remove(repo.id);
                        }
                        repo.cntr++;
                        repos.put(repo.id, repo);

                    }
                    else {
                        br.readLine();
                        br.readLine();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


            ++buffTime;
        }

        writeResult();
    }

    private void writeResult() {

        //Actor Handling
        Actor[] sortedActors = new Actor[actors.size()];
        int cnt = 0;
        for (Map.Entry<Long, Actor> entry : actors.entrySet()) {
            sortedActors[cnt++] = entry.getValue();
        }

        Arrays.sort(sortedActors, (a, b) -> (b.cntr).compareTo(a.cntr));

        try(FileWriter fw = new FileWriter("result/ActorResultNumber " + Client.numberOfRequests + ".txt", true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw))
        {
            cnt = 0;
            for (Actor actor : sortedActors) {
                out.println(String.format("%-3d %s %-50s %s %-10d %s", ++cnt, "user name:", actor.login, "number of actions:", actor.cntr, "\n"));
                if (cnt >= 100)
                    break;
            }
        } catch (IOException e) {
            //exception handling left as an exercise for the reader
        }


        //Repo Handling
        Repo[] sortedRepos = new Repo[repos.size()];
        cnt = 0;
        for (Map.Entry<Long, Repo> entry : repos.entrySet()) {
            sortedRepos[cnt++] = entry.getValue();
        }

        Arrays.sort(sortedRepos, (a, b) -> (b.cntr).compareTo(a.cntr));

        try(FileWriter fw = new FileWriter("result/RepoResultNumber " + Client.numberOfRequests + ".txt", true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw))
        {
            cnt = 0;
            for (Repo repo : sortedRepos) {
                out.println(String.format("%-3d %s %-70s %s %-10d %s", ++cnt, "repository name:", repo.name, "number of actions on it:", repo.cntr, "\n"));
                if (cnt >= 100)
                    break;
            }
        } catch (IOException e) {
            //exception handling left as an exercise for the reader
        }

        Client.sem.release();
    }

}
