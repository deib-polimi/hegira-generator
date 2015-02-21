package it.polimi.hegira.command;

import it.polimi.hegira.generator.RandomUtils;
import it.polimi.hegira.generator.Randomizable;
import it.polimi.hegira.generator.entities.*;
import it.polimi.modaclouds.cpimlibrary.entitymng.CloudEntityManager;
import it.polimi.modaclouds.cpimlibrary.entitymng.migration.SeqNumberProvider;
import it.polimi.modaclouds.cpimlibrary.mffactory.MF;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@AllArgsConstructor
public class Generate {

    private int quantity;

    private enum DependencyType {
        SINGLE, COLLECTION
    }

    public void generateAll() {
        persist(Department.class, EmployeeMTO.class, DependencyType.SINGLE);

        persist(Employee.class);

        persist(ProjectMTM.class, EmployeeMTM.class, DependencyType.COLLECTION);

        persist(Phone.class, EmployeeOTO.class, DependencyType.SINGLE);
    }

    private void persist(Class master) {
        CloudEntityManager em = MF.getFactory().getEntityManager();

        log.info("Generating [" + quantity + "] entities for master class [" + master.getSimpleName() + "]");
        SeqNumberProvider.getInstance().setOffset(master.getSimpleName(), quantity * 2);
        for (Object o : generate(quantity, master)) {
            em.persist(o);
        }
    }

    private void persist(Class master, Class slave, DependencyType type) {
        CloudEntityManager em = MF.getFactory().getEntityManager();
        Map<Class, List> entities = new HashMap<>();

        log.info("Generating [" + quantity + "] entities for master class [" + master.getSimpleName() + "]");
        entities.put(master, generate(quantity, master));

        SeqNumberProvider.getInstance().setOffset(master.getSimpleName(), quantity * 2);
        for (Object o : entities.get(master)) {
            em.persist(o);
        }

        if (slave != null && type != null) {
            log.info("Generating [" + quantity + "] entities for slave class [" + slave.getSimpleName() + "]");
            entities.put(slave, generate(quantity, slave, entities.get(master), type));

            SeqNumberProvider.getInstance().setOffset(slave.getSimpleName(), quantity * 2);
            for (Object o : entities.get(slave)) {
                em.persist(o);
            }
        }
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
}