package org.sample.kub;

public interface KubCronController {
    void printListPodsByNamespace(String namespace);
    void listCronJobsByNamespace(String namespace);
    void updateScheduleCronJobByNamespace(String namespace, String CronJobName, String schedule);
    void createPod(String namespace, String name, String imageName);
    boolean removePod(String namespace, String name);
    void createCronJob(String namespace, String name, String imageName, String schedule);
    void removeCronJob(String namespace, String name);
}
