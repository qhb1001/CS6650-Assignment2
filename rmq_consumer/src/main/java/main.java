import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeoutException;


public class main {
    private static String ipAddress = System.getenv("rabbitmq_ip_address");
    private static String userName = System.getenv("rabbitmq_user_name");
    private static String passwd = System.getenv("rabbitmq_passwd");
    private static String vhost = System.getenv("rabbitmq_vhost");
    public static Map<Integer, List<SkierServletPostRequest>> map;
    private static int NUMBER = Integer.parseInt(System.getenv("thread_number"));

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        map = new ConcurrentHashMap<>();

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(ipAddress);
        factory.setUsername(userName);
        factory.setPassword(passwd);
        factory.setVirtualHost(vhost);

        factory.setPort(5672);
        Connection connection = factory.newConnection();

        for (int i = 0; i < NUMBER; i++) {
            Thread t = new Thread(new PostRequestHandler(connection));
            t.start();
        }

        CountDownLatch countDownLatch = new CountDownLatch(3);
        countDownLatch.await();
    }

}
