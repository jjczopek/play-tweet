package controllers;

import models.Tweet;
import models.User;
import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.data.validation.Validation;
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
            flash.error("tweet.login.first");
            redirect("/login");
        }

    }

    public static void save(@Required(message = "") @MaxSize(255) String content) {
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
            flash.error("tweet.login.first");
            redirect("/login");
        }
    }

    public static void show(long tweetId) {
        Tweet tweet = Tweet.findById(tweetId);

        if (tweet == null) {
            flash.error("tweet.not.found");
            redirect("/");
            return;
        }

        User loggedUser = User.getByLogin(Security.connected());

        render(tweet, loggedUser);
    }

    public static void all() {
        User loggedUser = User.getByLogin(Security.connected());
        //ModelPaginator<Tweet> paginator = new ModelPaginator<Tweet>(Tweet.class);
        List<Tweet> tweets = Tweet.find("order by dateCreated desc").fetch();
        ValuePaginator<Tweet> paginator = new ValuePaginator<Tweet>(tweets);
        paginator.setPagesDisplayed(2);
        render(paginator, loggedUser);
    }

    public static void delete(Long tweetId) {
        Tweet t = Tweet.findById(tweetId);
        if (t != null) {
            t.delete();
            flash.success("tweet.deleted");
        } else {
            flash.error("tweet.not.found");
        }
        //Application.index();
        redirect("/");
    }

    public static void userTweets(String login) {
        User loggedUser = User.getByLogin(Security.connected());
        User user = User.find("byLogin", login).first();
        //page = (page == null) ? 1 : page;
        if (user == null) {
            flash.error("user.no.such.user");
            redirect("/");
        } else {
            List<Tweet> userTweets = Tweet.find("author.login = ? order by dateCreated desc", login).fetch();
            ValuePaginator<Tweet> paginator = new ValuePaginator<Tweet>(userTweets);
            paginator.setPagesDisplayed(2);
            String fullName = user.fullName;
            render(paginator, fullName, loggedUser);
        }
    }

}
