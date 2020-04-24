package org.sample.kub.official;

import io.kubernetes.client.extended.generic.GenericKubernetesApi;
import io.kubernetes.client.extended.generic.KubernetesApiResponse;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.BatchV1beta1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.*;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.KubeConfig;
import org.apache.commons.lang.NotImplementedException;
import org.sample.kub.KubCronController;

import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

public class KubCronControllerOfficial implements KubCronController {
    private ApiClient client;
    private CoreV1Api api;
    private BatchV1beta1Api batchApi;

    public KubCronControllerOfficial(String kubeConfigPath) throws IOException {
        this.client = ClientBuilder.kubeconfig(KubeConfig.loadKubeConfig(new FileReader(kubeConfigPath))).build();
        Configuration.setDefaultApiClient(client);
        this.api = new CoreV1Api();
        this.batchApi = new BatchV1beta1Api();
    }

    @Override
    public void printListPodsByNamespace(String namespace) {
        V1PodList list = null;
        try {
            list = api.listNamespacedPod(namespace, null, null,null, null, null, null, null, null, null);
        } catch (ApiException e) {
            e.printStackTrace();
        }
        System.out.println("Official List Pod Jobs:");
        for (V1Pod item : list.getItems()) {
            System.out.println(item.getMetadata().getName());
        }
    }

    @Override
    public void listCronJobsByNamespace(String namespace) {
        System.out.println("\nList Cron Jobs:");
        V1beta1CronJobList cronList = null;
        try {
            cronList = batchApi.listNamespacedCronJob(namespace, null, null, null, null, null, null, null, null, null);
//            cronList = batchApi.listCronJobForAllNamespaces(null, null, null, null, null, null, null, null, null);
        } catch (ApiException e) {
            e.printStackTrace();
        }
        for (V1beta1CronJob item : cronList.getItems()) {
            V1Container container = item.getSpec().getJobTemplate().getSpec().getTemplate().getSpec().getContainers().get(0);
            System.out.println("Cron job item:");
            System.out.println(item.toString());
            System.out.println("Container item:");
            System.out.println(container.toString());
        }
    }

    @Override
    public void updateScheduleCronJobByNamespace(String namespace, String CronJobName, String schedule) {
        throw new NotImplementedException("Not implemented Yet");
    }

    @Override
//    "hello-healthcheck"
    public void createPod(String namespace, String name, String imageName) {
        System.out.println("\nCreate Pod:");
        V1Pod pod =
                new V1Pod()
                        .metadata(new V1ObjectMeta().name(name).namespace(namespace))
                        .spec(
                                new V1PodSpec()
                                        .containers(Arrays.asList(new V1Container().name("c").image(imageName).imagePullPolicy("Never"))));

        GenericKubernetesApi<V1Pod, V1PodList> podClient =
                new GenericKubernetesApi<>(V1Pod.class, V1PodList.class, "", "v1", "pods", client);

        KubernetesApiResponse<V1Pod> createResponse = podClient.create(pod);
        if (!createResponse.isSuccess()) {
            throw new RuntimeException(createResponse.getStatus().toString());
        }
    }

    @Override
    public boolean removePod(String namespace, String name) {
        throw new NotImplementedException("Not implemented Yet");
    }

    @Override
    public void createCronJob(String namespace, String name, String imageName, String schedule) {
        System.out.println("\nCreate Cron Job;");
        V1PodSpec podSpec =
                new V1PodSpec()
                        .containers(Arrays.asList(new V1Container()
                                .name("c")
                                .image("hello-writing")
                                .imagePullPolicy("Never")))
                        .restartPolicy("Never");

        V1PodTemplateSpec templateSpec = new V1PodTemplateSpec()
                .metadata(new V1ObjectMeta().name(name).namespace("default"))
                .spec(podSpec);

        V1JobSpec jobSpec = new V1JobSpec();
        jobSpec.setTemplate(templateSpec);
        System.out.println(jobSpec.toString());

        V1beta1JobTemplateSpec jobTemplateSpec = new V1beta1JobTemplateSpec().spec(jobSpec);
        System.out.println(jobTemplateSpec.toString());

        V1beta1CronJobSpec cronJobSpec = new V1beta1CronJobSpec().jobTemplate(jobTemplateSpec).schedule("*/5 * * * *");
        System.out.println(cronJobSpec.toString());

        V1beta1CronJob cronJob =
                new V1beta1CronJob()
                        .kind("CronJob")
                        .metadata(new V1ObjectMeta().name(name).namespace("default"))
                        .spec(cronJobSpec);

        GenericKubernetesApi<V1beta1CronJob, V1beta1CronJobList> cronPodClient =
                new GenericKubernetesApi<>(V1beta1CronJob.class, V1beta1CronJobList.class, "batch", "v1beta1", "cronjobs", client);

        KubernetesApiResponse<V1beta1CronJob> createResponse = cronPodClient.create(cronJob);
        if (!createResponse.isSuccess()) {
            throw new RuntimeException(createResponse.getStatus().toString());
        }
    }

    @Override
    public void removeCronJob(String namespace, String name) {
        throw new NotImplementedException("Not implemented Yet, too heavy with official library compared to fabric8");
    }
}
