package rest;

import models.RestSession;
import models.User;
import play.Logger;
import play.Play;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import java.util.Date;
import java.util.UUID;

/**
 * Created by IntelliJ IDEA.
 * User: Jerzy
 * Date: 11.06.11
 * Time: 16:56
 * To change this template use File | Settings | File Templates.
 */

@Path("/session")
public class Session {

    @POST
    @Path("/login")
    public String login(@QueryParam("login") String login, @QueryParam("passwordHash") String passwordHash) {

        String sessionId = "null";

        // find user by login and check passwords
        User user = User.find("byLoginAndPassword", login, passwordHash).first();
        if (user != null) {
            sessionId = UUID.randomUUID().toString().replaceAll("-", "");
            String sessDurStr = Play.configuration.getProperty("application.session.duration", "5");
            long sessDurLng = Long.parseLong(sessDurStr);
            Date sessionValidThrough = new Date(System.currentTimeMillis() + (sessDurLng * 1000 * 60));

            RestSession restSession = new RestSession(user, sessionId, sessionValidThrough);
            restSession.save();
            Logger.info("Sesscion created: %s", restSession);
        }

        return sessionId;

    }

    @POST
    @Path("/logout/{sessionId}")
    public String logout(@PathParam("sessionId") String sessionId) {
        RestSession session = RestSession.getBySessionId(sessionId);
        if (session != null) {
            session.delete();
            Logger.info("Session deleted %s deleted", session);
        }
        return "";
    }

    @Path("/valid/{sessionId}")
    @POST
    public Boolean isSessionValid(@PathParam("sessionId") String sessionId) {
        return RestSession.isSessionValid(sessionId, false);
    }
}
