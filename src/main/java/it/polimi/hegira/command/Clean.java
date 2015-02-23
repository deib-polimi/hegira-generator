package it.polimi.hegira.command;

import com.google.appengine.api.datastore.*;
import com.google.appengine.tools.remoteapi.RemoteApiInstaller;
import com.google.appengine.tools.remoteapi.RemoteApiOptions;
import it.polimi.modaclouds.cpimlibrary.mffactory.MF;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class Clean {

    private static final String SERVER = "localhost";
    private static final int PORT = 8080;
    private static final String USERNAME = "username";
    private static final String PASSWORD = "pwd";

    public static void cleanDatastore() throws IOException {
        RemoteApiOptions options = new RemoteApiOptions().server(SERVER, PORT).credentials(USERNAME, PASSWORD);
        RemoteApiInstaller installer = new RemoteApiInstaller();
        installer.install(options);

        Query query = new Query(Entities.KIND_METADATA_KIND);
        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        for (Entity entity : datastoreService.prepare(query).asIterable()) {
            String kind = entity.getKey().getName();
            if (kind.startsWith("__")) {
                System.out.println("skipping kind [" + kind + "]");
                continue;
            }
            System.out.println("deleting all entities of kind [" + kind + "]");
            DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
            query = new Query(kind).setKeysOnly();
            for (Entity e : datastore.prepare(query).asIterable()) {
                datastore.delete(e.getKey());
            }
        }

        installer.uninstall();
    }

    public static void cleanBackups() throws IOException {
        FileUtils.cleanDirectory(new File(MF.getFactory().getCloudMetadata().getBackupDir()));
    }
}
