package models;

import play.Play;
import play.db.jpa.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Jerzy
 * Date: 11.06.11
 * Time: 15:37
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class RestSession extends Model {

    @OneToOne
    public User user;
    @Column(nullable = false, unique = true)
    public String sessionId;
    @Column(nullable = false)
    public Date validThrough;

    public RestSession(User user, String sessionId, Date validThrough) {
        this.user = user;
        this.sessionId = sessionId;
        this.validThrough = validThrough;
    }

    public boolean isSessionValid() {
        return this.validThrough.getTime() > System.currentTimeMillis();
    }

    /**
     * Extends user session with amount of minutes defined in configuration, or 5 minutes by default if session duration
     * is not configured. Session is extended only if it is still valid. If user will try to extend expired session,
     * nothing will happen.
     */
    public void extendsSession() {
        if (this.isSessionValid()) {
            String sessDurStr = Play.configuration.getProperty("application.session.duration", "5");
            long sessDurLng = Long.parseLong(sessDurStr);
            long currentTime = System.currentTimeMillis();
            this.validThrough = new Date(currentTime + (sessDurLng * 1000 * 60));
            this.save();
        }
    }

    public static boolean isSessionValid(String sessionId, boolean extend) {
        if (sessionId == null) return false;
        RestSession session = RestSession.find("bySessionId", sessionId).first();

        boolean valid = session != null && session.isSessionValid();

        if (valid && extend) {
            session.extendsSession();
        }

        return valid;
    }

    public static boolean isSessionValid(String sessionId) {
        return RestSession.isSessionValid(sessionId, true);
    }

    public static RestSession getBySessionId(String sessionId) {
        return RestSession.find("bySessionId", sessionId).first();
    }

    public static RestSession getByUser(User user) {
        return RestSession.find("byUser", user).first();
    }

    /**
     * Returns user associated with given session
     *
     * @param sessionId session identifier from which user is needed
     * @return user associated with session with given id, {@code null} if session with given session id was not found.
     */
    public static User getUserFromSession(String sessionId) {
        if (sessionId == null) throw new NullPointerException("Session can not be null");
        RestSession session = RestSession.getBySessionId(sessionId);
        if (session != null) {
            return session.user;
        } else {
            return null;
        }
    }

    public static List<RestSession> getInvalidSessions() {
        return RestSession.find("from RestSession as session where session.validThrough < :now").bind("now", new Date()).fetch();
    }

    @Override
    public String toString() {
        return "RestSession{" +
                "user=" + user.login +
                ", sessionId='" + sessionId + '\'' +
                ", validThrough=" + validThrough +
                '}';
    }
}
