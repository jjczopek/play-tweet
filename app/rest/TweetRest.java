package rest;

import models.RestSession;
import models.Tweet;
import models.User;

import javax.ws.rs.*;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
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
    //@Wrapped(element = "tweets")
    public TweetList all(@QueryParam("page") @DefaultValue("1") Integer page) {
        //return Tweet.find("order by dateCreated desc").fetch(page, 100);
        List<Tweet> l = Tweet.find("order by dateCreated desc").fetch(page, 100);
        return new TweetList(l);
    }

    @GET
    @Path("/tweets/{userId}")
    @Produces("application/xml")
    //@Wrapped(element = "tweets")
    public TweetList userTweets(@PathParam("userId") Long userId) {
        User user = User.findById(userId);

        List<Tweet> tweetList = (user != null) ? user.tweets : Collections.<Tweet>emptyList();

        return new TweetList(tweetList);
    }


    @GET
    @Path("/tweets/followed")
    @Produces("application/xml")
    //@Wrapped(element = "tweets")
    public TweetList followingUsersTweets(@QueryParam("sessionId") String sessionId) {

        List<Tweet> tweetList = new ArrayList<Tweet>();

        if (RestSession.isSessionValid(sessionId)) {
            User loogedUser = RestSession.getUserFromSession(sessionId);
            tweetList = Tweet.getFollowedUsersTweets(loogedUser);
        }

        return new TweetList(tweetList);
    }

    @XmlRootElement(name = "tweetList")
    private class TweetList {

        private List<Tweet> tweets;

        public TweetList(List<Tweet> tweets) {
            this.tweets = tweets;
        }

        public TweetList() {
        }

        @XmlElement(name = "tweet")
        public List<Tweet> getTweets() {
            return tweets;
        }

        public void setTweets(List<Tweet> tweets) {
            this.tweets = tweets;
        }
    }

}
