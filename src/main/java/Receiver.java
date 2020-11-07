/**
 * Created by QotboddiniH on 7/26/17.
 */
import com.satori.rtm.*;
import com.satori.rtm.model.*;

import java.util.Deque;
import java.util.LinkedList;

public class Receiver extends Thread {
    static final String endpoint = "wss://open-data.api.satori.com";
    static final String appkey = "21320cC01D7bC9364399e01eC8dDFa9C";
    static final String channel = "github-events";

    static Deque<JsonData> jsonList = new LinkedList<JsonData>();


    public Receiver () {
        Thread.currentThread().setPriority(Thread.NORM_PRIORITY);
    }

    @Override
    public void run () {

        final RtmClient client = new RtmClientBuilder(endpoint, appkey).setListener(new RtmClientAdapter() {
            @Override
            public void onEnterConnected(RtmClient client) {
                System.out.println("Connected to Satori RTM!");
            }
        }).build();

        Writer writer = new Writer();
        writer.start();

        SubscriptionAdapter listener = new SubscriptionAdapter() {
            @Override
            public void onSubscriptionData(SubscriptionData data) {
                for (AnyJson json : data.getMessages()) {
                    jsonList.addLast(json.convertToType(JsonData.class));
                }
            }
        };

        client.createSubscription(channel, SubscriptionMode.SIMPLE, listener);

        client.start();
    }
}