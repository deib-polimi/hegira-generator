package it.polimi.hegira.command;

import it.polimi.hegira.generator.RandomUtils;
import it.polimi.hegira.generator.Randomizable;
import it.polimi.hegira.generator.entities.*;
import it.polimi.modaclouds.cpimlibrary.entitymng.CloudEntityManager;
import it.polimi.modaclouds.cpimlibrary.entitymng.migration.SeqNumberProvider;
import it.polimi.modaclouds.cpimlibrary.mffactory.MF;

import javax.persistence.Query;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Generate {

    public static final int MAX_OFFSET = 100;
    private int quantity;
    private String backupFile;

    private enum DependencyType {
        SINGLE, COLLECTION
    }

    public Generate(int quantity) {
        this.quantity = quantity;
        this.backupFile = MF.getFactory().getCloudMetadata().getBackupDir() + "generation";
    }

    public void generateAll() throws IOException {
        persist(Department.class, EmployeeMTO.class, DependencyType.SINGLE);

        persist(Employee.class);

        persist(ProjectMTM.class, EmployeeMTM.class, DependencyType.COLLECTION);

        persist(Phone.class, EmployeeOTO.class, DependencyType.SINGLE);
    }

    private void persist(Class master) throws IOException {
        if (isTableAlreadyDone(master)) {
            System.out.println("Generation for table [" + master.getSimpleName() + "] was already done");
            return;
        }

        CloudEntityManager em = MF.getFactory().getEntityManager();

        System.out.println("Generating [" + quantity + "] entities for master class [" + master.getSimpleName() + "]");
        setSeqNumberOffset(master.getSimpleName(), quantity);
        for (Object o : generate(quantity, master)) {
            em.persist(o);
            System.out.println("\t" + o.toString());
        }

        saveTableDone(master);
    }

    private void persist(Class master, Class slave, DependencyType type) throws IOException {
        CloudEntityManager em = MF.getFactory().getEntityManager();
        Map<Class, List> entities = new HashMap<>();
        boolean masterPreviouslyDone = false;

        if (isTableAlreadyDone(master)) {
            System.out.println("Generation for table [" + master.getSimpleName() + "] was already done");
            masterPreviouslyDone = true;
        } else {
            System.out.println("Generating [" + quantity + "] entities for master class [" + master.getSimpleName() + "]");
            entities.put(master, generate(quantity, master));

            setSeqNumberOffset(master.getSimpleName(), quantity);
            for (Object o : entities.get(master)) {
                em.persist(o);
                System.out.println("\t" + o.toString());
            }

            saveTableDone(master);
        }

        if (slave != null && type != null) {
            if (isTableAlreadyDone(slave)) {
                System.out.println("Generation for table [" + slave.getSimpleName() + "] was already done");
            } else {
                if (masterPreviouslyDone) {
                    System.out.println("Get existing " + MAX_OFFSET + " entities of (already persisted) master class: " + master.getSimpleName());
                    entities.put(master, getSomeExisting(em, master));
                }
                System.out.println("Generating [" + quantity + "] entities for slave class [" + slave.getSimpleName() + "]");
                entities.put(slave, generate(quantity, slave, entities.get(master), type));

                setSeqNumberOffset(slave.getSimpleName(), quantity);
                for (Object o : entities.get(slave)) {
                    em.persist(o);
                    System.out.println("\t" + o.toString());
                }

                saveTableDone(slave);
            }
        }
    }

    private List getSomeExisting(CloudEntityManager em, Class master) {
        Query query = em.createQuery("SELECT e FROM " + master.getSimpleName() + " e ");
        return query.setMaxResults(MAX_OFFSET).getResultList();
    }

    private List generate(int quantity, Class<? extends Randomizable> clazz) {
        List<Object> results = new ArrayList<>();
        for (int i = 0; i < quantity; i++) {
            try {
                results.add(clazz.newInstance().randomize(null));
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return results;
    }

    private List<Object> generate(int number, Class<? extends Randomizable> clazz, List dependenciesSource, DependencyType type) {
        List<Object> results = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            try {
                if (type.equals(DependencyType.SINGLE)) {
                    results.add(clazz.newInstance().randomize(dependenciesSource.get(RandomUtils.randomInt(dependenciesSource.size()))));
                } else {
                    List<Object> depList = new ArrayList<>();
                    for (int j = 0; j < RandomUtils.randomInt(); j++) {
                        depList.add(dependenciesSource.get(RandomUtils.randomInt(dependenciesSource.size())));
                    }
                    results.add(clazz.newInstance().randomize(depList));
                }
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return results;
    }

    private boolean isTableAlreadyDone(Class clazz) throws IOException {
        File file = new File(backupFile);
        if (file.exists() && !file.isDirectory()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.equals(clazz.getSimpleName())) {
                    return true;
                }
            }
            br.close();
        }
        return false;
    }

    private void saveTableDone(Class clazz) throws IOException {
        Writer output = new BufferedWriter(new FileWriter(backupFile, true));
        output.append(clazz.getSimpleName()).append("\n");
        output.close();
    }

    private void setSeqNumberOffset(String tableName, int quantity) {
        int offset;
        if (quantity >= MAX_OFFSET || (quantity * 2) >= MAX_OFFSET) {
            offset = MAX_OFFSET;
        } else {
            offset = quantity * 2;
        }
        SeqNumberProvider.getInstance().setOffset(tableName, offset);
    }
}
