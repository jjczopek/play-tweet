package models;

import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Jerzy
 * Date: 30.05.11
 * Time: 15:18
 * To change this template use File | Settings | File Templates.
 */
@Entity
@XmlRootElement(name = "tweet")
@XmlAccessorType(value = XmlAccessType.NONE)
public class Tweet extends Model {

    @Lob
    @XmlElement
    public String content;

    @ManyToOne
    @XmlElement
    public User author;

    @XmlElement
    public Date dateCreated;

    public Tweet(String content, User author) {
        this.content = content;
        this.author = author;

        this.dateCreated = new Date();
    }

    public static List<Tweet> getFollowedUsersTweets(User followingUser, int max) {
        return Tweet.find("SELECT tweet from Tweet tweet " +
                "JOIN tweet.author author " +
                "WHERE author IN " +
                "(SELECT following from User user " +
                "JOIN user.following following where user = :followingUser) " +
                "order by tweet.dateCreated desc").bind("followingUser", followingUser).fetch(max);
    }

    public static List<Tweet> getFollowedUsersTweets(User followingUser) {
        return getFollowedUsersTweets(followingUser, 10);
    }

    @Override
    public String toString() {
        return "Tweet{" +
                "content='" + content + '\'' +
                ", author=" + author +
                ", dateCreated=" + dateCreated +
                '}';
    }
}
