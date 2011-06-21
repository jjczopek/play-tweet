package controllers;

import models.Role;
import models.User;
import play.data.validation.Required;
import play.data.validation.Valid;
import play.data.validation.Validation;
import play.mvc.Before;
import play.mvc.Controller;

import java.util.HashSet;

/**
 * User controller is responsible responsible for user management:
 * <ul>
 * <li>creating user accounts</li>
 * <li>editing user accounts</li>
 * <li>logging users in</li>
 * </ul>
 */

public class Users extends Controller {

    @Before
    static void getUrl() {
        flash.put("url", request.method.equalsIgnoreCase("GET") ? request.url : "/"); // seems a good default
    }

    /**
     * Action rendering new user form allowing new account creation.
     * If user is logged in already, user is redirected to main page
     */
    public static void create() {
        // TODO: if logged in, go to main page
        render();
    }

    /**
     * Action which validates user registration form and saves user into database.
     *
     * @param user user object instance automatically binded by framework
     */
    public static void save(@Valid User user) {

        if (!user.password.equals(user.password2)) {
            Validation.addError("password", "validation.user.password.not.equal");
        }

        if (Validation.hasErrors()) {
            render("Users/create.html", user);
            return;
        }

        Role userRole = Role.find("byRoleName", "ROLE_USER").first();

        if (userRole == null) {
            throw new NullPointerException("User role was not found.");
        }
        user.userRoles = new HashSet<Role>();
        user.userRoles.add(userRole);
        user.password = Security.hashUserPassword(user.password);
        user.save();
        render(user);
    }

    public static void profile(String login) {
        User user = null;
        if (login != null) {
            user = User.getByLogin(login);
        } else {
            if (Security.isConnected()) {
                user = User.getByLogin(Security.connected());
            }
        }
        if (user == null) {
            flash.error("No such user. Please login.");
            redirectToLoginPage();
        }
        render(user);

    }


    public static void follow(@Required String loginToFollow) {
        if (Validation.hasErrors()) {
            flash.error("You must provide user login you want to follow");
            redirectToPrevious();
            return;
        }

        if (Security.isConnected()) {

            User loggedUser = User.find("byLogin", Security.connected()).first();
            User userToFollow = User.find("byLogin", loginToFollow).first();

            if (userToFollow == null) {
                flash.error("User '%s' was not found", loginToFollow);
                redirect("/");
                return;
            } else {
                //userToFollow.followedBy.add(loggedUser);
                //userToFollow.save();
                if (loggedUser.addFollowingUser(userToFollow))
                    flash.success("You are now following %s", userToFollow.fullName);
                else flash.success("You are already following this user");
                redirect("/");
                return;
            }

        } else {
            redirectToLoginPage();
        }


    }

    private static void redirectToPrevious() {
        String urlToRedirect = "/";
        if (flash.contains("url")) {
            urlToRedirect = flash.get("url");
        }
        redirect(urlToRedirect);
    }

    private static void redirectToLoginPage() {
        flash.error("You must log in first");
        redirect("/login");
    }
}
