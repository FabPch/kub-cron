package org.sample.kub;

import io.fabric8.kubernetes.api.model.PodBuilder;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.server.mock.KubernetesServer;
import org.junit.Rule;
import org.junit.Test;
import org.sample.kub.fabric8.KubCronControllerFabric8;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class KubControllerFabric8Test {
    @Rule
    public KubernetesServer server = new KubernetesServer(true, true);

    // Official Test
    @Test
    public void testInCrudMode() {
        KubernetesClient client = server.getClient();
        //CREATE
        client.pods().inNamespace("ns1").create(new PodBuilder().withNewMetadata().withName("pod1").endMetadata().build());

        //READ
        PodList podList = client.pods().inNamespace("ns1").list();
        assertNotNull(podList);
        assertEquals(1, podList.getItems().size());

        //DELETE
        client.pods().inNamespace("ns1").withName("pod1").delete();

        //READ AGAIN
        podList = client.pods().inNamespace("ns1").list();
        assertNotNull(podList);
        assertEquals(0, podList.getItems().size());
    }

    // Test from Custom controller, only display in console
    @Test
    public void customCRUDTest() {
        KubCronController controller = new KubCronControllerFabric8(server.getClient());
        String namespace = "yala";
        String cronName = "cron-test";

        controller.listCronJobsByNamespace(namespace);

        controller.createCronJob(namespace, cronName, "image-test", "*/5 * * * *");
        controller.listCronJobsByNamespace(namespace);

        controller.updateScheduleCronJobByNamespace(namespace, cronName, "*/1 * * * *");
        controller.listCronJobsByNamespace(namespace);

        controller.removeCronJob(namespace, cronName);
        controller.listCronJobsByNamespace(namespace);
    }
}
