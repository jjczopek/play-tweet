package controllers;

import models.Tweet;
import models.User;
import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.data.validation.Validation;
import play.modules.paginate.ModelPaginator;
import play.modules.paginate.ValuePaginator;
import play.mvc.Controller;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Jerzy
 * Date: 06.06.11
 * Time: 19:22
 * To change this template use File | Settings | File Templates.
 */
//@With(Secure.class)
public class Tweets extends Controller {

    public static void post() {
        if (Secure.checkPermission(null, "post")) {
            render("Tweets/tweet.html");
        } else {
            flash.error("You must log in to tweet");
            redirect("/login");
        }

    }

    public static void save(@Required @MaxSize(255) String content) {
        if (Secure.checkPermission(null, "save")) {
            if (Validation.hasErrors()) {
                render("Tweets/tweet.html", content);
            } else {
                User author = User.find("byLogin", Security.connected()).first();
                if (author != null) {
                    Tweet tweet = new Tweet(content, author);
                    tweet.save();
                    show(tweet.id);
                } else {
                    throw new NullPointerException("No such author.");
                }
            }
        } else {
            flash.error("You must log in to tweet");
            redirect("/login");
        }
    }

    public static void show(long tweetId) {
        Tweet tweet = Tweet.findById(tweetId);
        if (tweet == null) {
            flash.error("No such post");
            redirect("/");
            return;
        }

        render(tweet);
    }

    public static void all() {
        ModelPaginator<Tweet> paginator = new ModelPaginator<Tweet>(Tweet.class);
        paginator.setPagesDisplayed(2);
        render(paginator);
    }

    public static void userTweets(String login) {
        User user = User.find("byLogin", login).first();
        //page = (page == null) ? 1 : page;
        if (user == null) {
            flash.error("No such user");
            redirect("/");
        } else {
            List<Tweet> userTweets = Tweet.find("author.login = ? order by dateCreated desc", login).fetch();
            ValuePaginator<Tweet> paginator = new ValuePaginator<Tweet>(userTweets);
            paginator.setPagesDisplayed(2);
            String fullName = user.fullName;
            render(paginator, fullName);
        }
    }

}
