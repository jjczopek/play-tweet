import models.Role;
import models.User;
import play.Logger;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.test.Fixtures;

/**
 * Created by IntelliJ IDEA.
 * User: Jerzy
 * Date: 06.06.11
 * Time: 15:28
 * To change this template use File | Settings | File Templates.
 */
@OnApplicationStart
public class Bootstrap extends Job {

    @Override
    public void doJob() throws Exception {
        // check if db is empty
        if (Role.count() == 0) {
            Fixtures.loadModels("initial-data_2.yml");
        }
        Logger.info("Role count is %s", Role.count());
        Logger.info("User count is %s", User.count());

        /*User admin = User.find("byLogin", "admin").first();
        if(admin == null){
            admin = new User("admin", Crypto.encryptAES("!Admin1234"), "", "Administrator");
            List<Role> roles = Role.all().fetch();
            for(Role r : roles){
                admin.userRoles.add(r);
            }

            admin.save();
        }*/


    }
}
