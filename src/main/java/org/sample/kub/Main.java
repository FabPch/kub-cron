package org.sample.kub;

import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.kubernetes.client.openapi.ApiException;
import org.sample.kub.fabric8.KubCronControllerFabric8;
import org.sample.kub.official.KubCronControllerOfficial;

import java.io.IOException;

public class Main {
    public static void main(String args[]) throws IOException, ApiException {
        String namespace = "default";
        String kubeConfigPath = "C:\\Users\\Fabien\\.kube\\config";
        KubCronController fabric8Controller = new KubCronControllerFabric8(new DefaultKubernetesClient());
        KubCronController officialCrontroller = new KubCronControllerOfficial(kubeConfigPath);

        fabric8Controller.listCronJobsByNamespace(namespace);
        officialCrontroller.listCronJobsByNamespace(namespace);

        String fabric8CronName = "fabric8-cron";
        String officialCronName = "official-cron";
        String imageName = "hello-writing:latest";
        fabric8Controller.createCronJob(namespace, fabric8CronName, imageName, "*/5 * * * *");
        officialCrontroller.createCronJob(namespace, officialCronName, imageName, "*/5 * * * *");

        fabric8Controller.listCronJobsByNamespace(namespace);
//        officialCrontroller.listCronJobsByNamespace(namespace);

        fabric8Controller.updateScheduleCronJobByNamespace(namespace, fabric8CronName, "*/1 * * * *");
        fabric8Controller.listCronJobsByNamespace(namespace);

        fabric8Controller.removeCronJob(namespace, fabric8CronName);
        fabric8Controller.removeCronJob(namespace, officialCronName);

        fabric8Controller.listCronJobsByNamespace(namespace);

        System.exit(0);
    }
}
