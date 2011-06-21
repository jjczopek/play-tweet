package rest;

import models.RestSession;
import models.Tweet;
import models.User;
import org.jboss.resteasy.annotations.providers.jaxb.Wrapped;

import javax.ws.rs.*;
import java.util.Collections;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Jerzy
 * Date: 11.06.11
 * Time: 20:48
 * To change this template use File | Settings | File Templates.
 */
@Path("/")
public class TweetRest {

    @POST
    @Path("/tweet")
    public String create(@QueryParam("sessionId") String sessionId, @QueryParam("content") String content) {
        if (RestSession.isSessionValid(sessionId)) {
            RestSession session = RestSession.getBySessionId(sessionId);
            if (content.length() > 255) {
                return "CONTENT_TOO_LONG";
            } else {
                Tweet tweet = new Tweet(content, session.user);
                tweet.save();
                return tweet.id.toString();
            }
        } else {
            return "SESSION_INVALID";
        }
    }

    @GET
    @Path("/tweets/all")
    @Produces("application/xml")
    @Wrapped(element = "tweets")
    public List<Tweet> all(@QueryParam("page") @DefaultValue("1") Integer page) {
        return Tweet.find("order by dateCreated desc").fetch(page, 100);
    }

    @GET
    @Path("/tweets/{userId}")
    @Produces("application/xml")
    @Wrapped(element = "tweets")
    public List<Tweet> userTweets(@PathParam("userId") Long userId) {
        User user = User.findById(userId);
        return (user != null) ? user.tweets : Collections.<Tweet>emptyList();
    }


    @GET
    @Path("/tweets/followed")
    @Produces("application/xml")
    @Wrapped(element = "tweets")
    public List<Tweet> followingUsersTweets(@QueryParam("sessionId") String sessionId) {
        if (RestSession.isSessionValid(sessionId)) {
            User loogedUser = RestSession.getUserFromSession(sessionId);
            return Tweet.getFollowedUsersTweets(loogedUser);
        } else {
            return Collections.emptyList();
        }
    }

}
