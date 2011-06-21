package controllers;


import models.Role;
import models.User;
import org.apache.commons.codec.binary.Base64;
import play.libs.Crypto;

import java.math.BigInteger;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Jerzy
 * Date: 05.06.11
 * Time: 23:48
 * To change this template use File | Settings | File Templates.
 */
public class Security extends Secure.Security {

    static boolean authentify(String username, String password) {
        User user = User.find("byLogin", username).first();
        if (username == null || password == null)
            throw new NullPointerException("Username and password must be provided");
        if (user != null) {
            /*
            passwordHash method returns base64 string. No idea what for. Very smart.
            I just wonder why documentation says nothing about this
             */
            if (user.password.equals(hashUserPassword(password))) {
                user.lastLogin = new Date();
                user.save();
                return true;
            } else {
                return false;
            }

        } else {
            return false;
        }
    }

    static Set<String> roles() {
        Set<String> roles = new HashSet<String>();

        String username = Security.connected();
        User user = User.find("byLogin", username).first();

        if (user != null) {
            for (Role r : user.userRoles) {
                roles.add(r.roleName);
            }
        }

        return roles;
    }

    public static String hashUserPassword(String password) {
        BigInteger decodedHash = Base64.decodeInteger(Crypto.passwordHash(password).getBytes());
        return String.format("%1$032X", decodedHash).toLowerCase();
    }


}
