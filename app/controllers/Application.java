package controllers;

import models.Tweet;
import models.User;
import play.Logger;
import play.mvc.Controller;

import java.util.Collections;
import java.util.List;

public class Application extends Controller {

    public static void index() {

        List<Tweet> newestTweets = Tweet.find("order by dateCreated desc").fetch(10);
        List<Tweet> followedUsersTweets = Collections.emptyList();
        User loggedUser = null;
        if (Security.isConnected()) {
            loggedUser = User.getByLogin(Security.connected());
            Logger.info("Logged user is %s", loggedUser);

            followedUsersTweets = Tweet.getFollowedUsersTweets(loggedUser);

        }
        render(newestTweets, followedUsersTweets, loggedUser);


    }

}