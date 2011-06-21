package models;

import play.db.jpa.Model;

import javax.persistence.Entity;

/**
 * Created by IntelliJ IDEA.
 * User: Jerzy
 * Date: 06.06.11
 * Time: 15:15
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class Role extends Model {

    public String roleName;

    @Override
    public String toString() {
        return "Role{" +
                "roleName='" + roleName + '\'' +
                '}';
    }
}
