package skiers;

import com.google.gson.Gson;
import rabbitmq.BlockingChannelPool;
import rabbitmq.ChannelPool;
import util.LiftRide;
import util.MessageResponse;
import com.rabbitmq.client.Channel;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.BufferedReader;
import java.io.IOException;

@WebServlet("/skiers/*")
public class SkierServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.getWriter().write(request.getPathInfo());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String[] urlPathList = request.getPathInfo().split("/");
        if (!isSkierPostUrlValid(urlPathList)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            String responseJSON = new Gson().toJson(new MessageResponse("The URL path is invalid"));
            response.getWriter().write(responseJSON);
            return;
        }

        try {
            // push the request to the queue
            int resortID = Integer.parseInt(urlPathList[1]);
            int season = Integer.parseInt(urlPathList[3]);
            int day = Integer.parseInt(urlPathList[5]);
            int skierID = Integer.parseInt(urlPathList[7]);
            SkierServletPostResponse postResponse = new SkierServletPostResponse(resortID, season, day, skierID);
            ChannelPool channelPool = new BlockingChannelPool();
            channelPool.init();
            Channel channel = channelPool.take();
            channel.queueDeclare("post", false, false, false, null);
            channel.basicPublish("", "post", null, new Gson().toJson(postResponse).getBytes());
            channelPool.add(channel);

            // response to the user
            response.setStatus(HttpServletResponse.SC_OK);
            LiftRide liftRide = new Gson().fromJson(getBodyContent(request), LiftRide.class);
            response.getWriter().write(new Gson().toJson(liftRide));
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            String responseJSON = new Gson().toJson(new MessageResponse("The type of the input parameter doesn't match"));
            response.getWriter().write(responseJSON);
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            String responseJSON = new Gson().toJson(new MessageResponse(e.getMessage()));
            response.getWriter().write(responseJSON);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private boolean isSkierPostUrlValid(String[] urlPathList) {
        if (urlPathList.length != 8) {
            return false;
        }

        return urlPathList[2].equals("seasons") && urlPathList[4].equals("days")
            && urlPathList[6].equals("skiers");
    }

    private String getBodyContent(HttpServletRequest req) throws IOException {
        BufferedReader reqBodyBuffer = req.getReader();
        StringBuilder reqBody = new StringBuilder();
        String line;
        while ((line = reqBodyBuffer.readLine()) != null) {
            reqBody.append(line);
        }

        return reqBody.toString();
    }
}
