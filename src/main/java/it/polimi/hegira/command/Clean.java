package it.polimi.hegira.command;

import com.google.appengine.api.datastore.*;
import com.google.appengine.tools.remoteapi.RemoteApiInstaller;
import com.google.appengine.tools.remoteapi.RemoteApiOptions;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class Clean {

    public static void clean() throws IOException {
        RemoteApiOptions options = new RemoteApiOptions()
                .server("localhost", 8080)
                .credentials("username", "");
        RemoteApiInstaller installer = new RemoteApiInstaller();
        installer.install(options);

        Query query = new Query(Entities.KIND_METADATA_KIND);
        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        for (Entity entity : datastoreService.prepare(query).asIterable()) {
            String kind = entity.getKey().getName();
            log.info("deleting all entities of kind [" + kind + "]");
            DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
            query = new Query(kind).setKeysOnly();
            for (Entity e : datastore.prepare(query).asIterable()) {
                datastore.delete(e.getKey());
            }
        }

        installer.uninstall();
    }
}
