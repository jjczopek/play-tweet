package models;

import play.data.validation.Match;
import play.data.validation.Required;
import play.db.jpa.Model;
import util.validation.Unique;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Jerzy
 * Date: 30.05.11
 * Time: 14:59
 * To change this template use File | Settings | File Templates.
 */
@Entity
@XmlAccessorType(value = XmlAccessType.NONE)
public class User extends Model {

    private static final String PASSWORD_REGEX = "^.*(?=.{8,64})(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[\\W]).*$";

    @Required
    @Unique
    @XmlElement()
    public String login;

    @Required
    @Match(value = PASSWORD_REGEX)
    public String password;

    @Transient
    public String password2;

    @Required
    @XmlElement
    public String fullName;

    public Date lastLogin;

    @ManyToMany
    public List<User> following;

    @ManyToMany(mappedBy = "following", fetch = FetchType.LAZY)
    public List<User> followedBy;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public List<Tweet> tweets;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "UserReadTweets")
    public List<Tweet> readTweets;

    @ManyToMany
    public Set<Role> userRoles;

    /**
     * Adds to current user a new user to follow.
     * New user is added only if it isn't already present in following list.
     *
     * @param userToFollow user to be added to current users following list
     * @return {@code true} if user was added to following list, {@code false} otherwise
     * @throws NullPointerException thrown when user to follow  argument is null ({@code userToFollow ==  null})
     */
    public boolean addFollowingUser(User userToFollow) {
        if (userToFollow == null) throw new NullPointerException("User to follow can not be null.");
        if (!following.contains(userToFollow)) {
            following.add(userToFollow);
            this.save();
            return true;
        } else {
            return false;
        }
    }

    public User(String login, String password, String password2, String fullName) {
        this.login = login;
        this.password = password;
        this.fullName = fullName;

        this.lastLogin = new Date();
        this.following = new ArrayList<User>();
        this.followedBy = new ArrayList<User>();
        this.tweets = new ArrayList<Tweet>();
        this.readTweets = new ArrayList<Tweet>();
        this.userRoles = new HashSet<Role>();
    }

    public static User getByLogin(String login) {
        if (login == null) return null;
        return User.find("byLogin", login).first();
    }

    @Override
    public String toString() {
        return "User{" +
                "login='" + login + '\'' +
                ", fullName='" + fullName + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
