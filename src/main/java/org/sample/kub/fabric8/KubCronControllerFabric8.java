package org.sample.kub.fabric8;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodBuilder;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.api.model.batch.CronJob;
import io.fabric8.kubernetes.api.model.batch.CronJobBuilder;
import io.fabric8.kubernetes.api.model.batch.CronJobList;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.sample.kub.KubCronController;

import java.util.Collections;

public class KubCronControllerFabric8 implements KubCronController {

    private KubernetesClient client;

    public KubCronControllerFabric8(KubernetesClient client) {
        this.client = client;
    }

    @Override
    public void printListPodsByNamespace(String namespace) {
        System.out.println("\nFabric8 list:");
        PodList podList = client.pods().inNamespace(namespace).list();
        for (Pod pod : podList.getItems()) {
            System.out.println(pod.getMetadata().getName());
        }
    }

    @Override
    public  void listCronJobsByNamespace(String namespace) {
        System.out.println("\nList Cron Jobs:");
        CronJobList cronJobList = client.batch().cronjobs().inNamespace(namespace).list();
        for (CronJob cronJob : cronJobList.getItems()) {
            System.out.println(cronJob.getMetadata().getName());
            System.out.println("\tSuccessfull history limit: " + cronJob.getSpec().getSuccessfulJobsHistoryLimit());
            System.out.println("\tFailed history limit: " + cronJob.getSpec().getFailedJobsHistoryLimit());
            System.out.println("\tSchedule: " + cronJob.getSpec().getSchedule());
        }
    }

    @Override
    public void updateScheduleCronJobByNamespace(String namespace, String CronJobName, String schedule) {
        System.out.println("Update Cron Job");
        client.batch().cronjobs().inNamespace(namespace).withName(CronJobName)
                .edit()
                .editSpec()
                .withSchedule(schedule)
                .endSpec()
                .done();
    }

    @Override
    public void createPod(String namespace, String name, String imageName) {
        Pod aPod = new PodBuilder().withNewMetadata().withName(name).endMetadata()
                .withNewSpec()
                .addNewContainer()
                .withName(name)
                .withImage(imageName)
                .addNewPort().withContainerPort(80).endPort()
                .endContainer()
                .endSpec()
                .build();
        client.pods().inNamespace(namespace).create(aPod);
    }

    @Override
    public boolean removePod(String namespace, String name) {
        System.out.println("Remove Cron Job");
        return client.pods().inNamespace(namespace).withName(name).delete();
    }

    @Override
    public void createCronJob(String namespace, String name, String imageName, String schedule) {
        System.out.println("Create Cron Job");
        CronJob cronJob1 = new CronJobBuilder()
                .withApiVersion("batch/v1beta1")
                .withNewMetadata()
                .withName(name)
                .withLabels(Collections.singletonMap("foo", "bar"))
                .endMetadata()
                .withNewSpec()
                .withSchedule(schedule)
                .withNewJobTemplate()
                .withNewSpec()
                .withNewTemplate()
                .withNewSpec()
                .addNewContainer()
                .withName("hello-container")
                .withImage(imageName)
                .withNewImagePullPolicy("Never")
                .endContainer()
                .withRestartPolicy("Never")
                .endSpec()
                .endTemplate()
                .endSpec()
                .endJobTemplate()
                .endSpec()
                .build();

        client.batch().cronjobs().inNamespace(namespace).createOrReplace(cronJob1);
    }

    @Override
    public void removeCronJob(String namespace, String name) {
        client.batch().cronjobs().inNamespace(namespace).withName(name).delete();
    }


}
