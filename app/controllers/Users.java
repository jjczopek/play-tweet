package controllers;

import models.Role;
import models.User;
import play.data.validation.Match;
import play.data.validation.Required;
import play.data.validation.Valid;
import play.data.validation.Validation;
import play.i18n.Messages;
import play.mvc.Before;
import play.mvc.Controller;

import java.util.HashSet;
import java.util.List;

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

    public static void list() {
        List<User> users = User.all().fetch();
        render(users);
    }

    public static void changePassword(@Required(message = "validation.user.old.password.required") String oldPassword,
                                      @Required(message = "validation.user.password.required") @Match(value = User.PASSWORD_REGEX, message = "validation.user.password.match") String newPassword,
                                      @Required(message = "validation.user.password.both.required") String newPassword2) {

        if (request.method.equalsIgnoreCase("POST")) {
            if (!Security.isConnected()) {
                redirectToLoginPage();
            } else {

                User loggedUser = User.getByLogin(Security.connected());

                if (!newPassword.equals(newPassword2)) {
                    Validation.addError("newPassword", "validation.user.password.not.equal");
                }

                if (!loggedUser.password.equals(Security.hashUserPassword(oldPassword))) {
                    Validation.addError("oldPassword", "validation.user.old.password.incorrect");
                }

                if (Validation.hasErrors()) {
                    render();
                } else {
                    loggedUser.password = Security.hashUserPassword(newPassword);
                    loggedUser.save();
                    flash.success("user.password.changed");
                    redirect("/");
                }
            }
        } else {
            Validation.clear(); // we don't need errors when first entering the form
            if (!Security.isConnected()) {
                redirectToLoginPage();
            } else {
                render();
            }
        }

    }

    public static void follow(@Required String loginToFollow) {
        if (Validation.hasErrors()) {
            flash.error("user.follow.login.empty");
            redirectToPrevious();
            return;
        }

        if (Security.isConnected()) {

            User loggedUser = User.find("byLogin", Security.connected()).first();
            User userToFollow = User.find("byLogin", loginToFollow).first();

            if (userToFollow == null) {
                flash.error(Messages.get("user.follow.no.user", loginToFollow));
                redirect("/");
                return;
            } else {
                //userToFollow.followedBy.add(loggedUser);
                //userToFollow.save();

                if (loggedUser.equals(userToFollow)) {
                    flash.error("user.profile.cant.follow.yourself");
                    redirect("/");
                    return;
                }

                if (loggedUser.addFollowingUser(userToFollow))
                    flash.success(Messages.get("user.follow.success", userToFollow.fullName));
                else flash.error(Messages.get("user.follow.already.following", userToFollow.fullName));
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
        flash.error("user.must.login");
        redirect("/login");
    }
}
