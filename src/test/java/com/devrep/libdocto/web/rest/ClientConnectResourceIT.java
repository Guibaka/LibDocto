package com.devrep.libdocto.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.devrep.libdocto.IntegrationTest;
import com.devrep.libdocto.domain.ClientConnect;
import com.devrep.libdocto.repository.ClientConnectRepository;
import com.devrep.libdocto.repository.EntityManager;
import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link ClientConnectResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ClientConnectResourceIT {

    private static final Integer DEFAULT_ID_CLIENT_CONNECT = 1;
    private static final Integer UPDATED_ID_CLIENT_CONNECT = 2;

    private static final String DEFAULT_FIRST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FIRST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LAST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_LAST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_MAIL = "AAAAAAAAAA";
    private static final String UPDATED_MAIL = "BBBBBBBBBB";

    private static final String DEFAULT_PASSWORD = "AAAAAAAAAA";
    private static final String UPDATED_PASSWORD = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/client-connects";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ClientConnectRepository clientConnectRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private ClientConnect clientConnect;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ClientConnect createEntity(EntityManager em) {
        ClientConnect clientConnect = new ClientConnect()
            .idClientConnect(DEFAULT_ID_CLIENT_CONNECT)
            .firstName(DEFAULT_FIRST_NAME)
            .lastName(DEFAULT_LAST_NAME)
            .mail(DEFAULT_MAIL)
            .password(DEFAULT_PASSWORD);
        return clientConnect;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ClientConnect createUpdatedEntity(EntityManager em) {
        ClientConnect clientConnect = new ClientConnect()
            .idClientConnect(UPDATED_ID_CLIENT_CONNECT)
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .mail(UPDATED_MAIL)
            .password(UPDATED_PASSWORD);
        return clientConnect;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(ClientConnect.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @BeforeEach
    public void setupCsrf() {
        webTestClient = webTestClient.mutateWith(csrf());
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        clientConnect = createEntity(em);
    }

    @Test
    void createClientConnect() throws Exception {
        int databaseSizeBeforeCreate = clientConnectRepository.findAll().collectList().block().size();
        // Create the ClientConnect
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(clientConnect))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the ClientConnect in the database
        List<ClientConnect> clientConnectList = clientConnectRepository.findAll().collectList().block();
        assertThat(clientConnectList).hasSize(databaseSizeBeforeCreate + 1);
        ClientConnect testClientConnect = clientConnectList.get(clientConnectList.size() - 1);
        assertThat(testClientConnect.getIdClientConnect()).isEqualTo(DEFAULT_ID_CLIENT_CONNECT);
        assertThat(testClientConnect.getFirstName()).isEqualTo(DEFAULT_FIRST_NAME);
        assertThat(testClientConnect.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
        assertThat(testClientConnect.getMail()).isEqualTo(DEFAULT_MAIL);
        assertThat(testClientConnect.getPassword()).isEqualTo(DEFAULT_PASSWORD);
    }

    @Test
    void createClientConnectWithExistingId() throws Exception {
        // Create the ClientConnect with an existing ID
        clientConnect.setId(1L);

        int databaseSizeBeforeCreate = clientConnectRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(clientConnect))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ClientConnect in the database
        List<ClientConnect> clientConnectList = clientConnectRepository.findAll().collectList().block();
        assertThat(clientConnectList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkIdClientConnectIsRequired() throws Exception {
        int databaseSizeBeforeTest = clientConnectRepository.findAll().collectList().block().size();
        // set the field null
        clientConnect.setIdClientConnect(null);

        // Create the ClientConnect, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(clientConnect))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<ClientConnect> clientConnectList = clientConnectRepository.findAll().collectList().block();
        assertThat(clientConnectList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllClientConnectsAsStream() {
        // Initialize the database
        clientConnectRepository.save(clientConnect).block();

        List<ClientConnect> clientConnectList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(ClientConnect.class)
            .getResponseBody()
            .filter(clientConnect::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(clientConnectList).isNotNull();
        assertThat(clientConnectList).hasSize(1);
        ClientConnect testClientConnect = clientConnectList.get(0);
        assertThat(testClientConnect.getIdClientConnect()).isEqualTo(DEFAULT_ID_CLIENT_CONNECT);
        assertThat(testClientConnect.getFirstName()).isEqualTo(DEFAULT_FIRST_NAME);
        assertThat(testClientConnect.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
        assertThat(testClientConnect.getMail()).isEqualTo(DEFAULT_MAIL);
        assertThat(testClientConnect.getPassword()).isEqualTo(DEFAULT_PASSWORD);
    }

    @Test
    void getAllClientConnects() {
        // Initialize the database
        clientConnectRepository.save(clientConnect).block();

        // Get all the clientConnectList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(clientConnect.getId().intValue()))
            .jsonPath("$.[*].idClientConnect")
            .value(hasItem(DEFAULT_ID_CLIENT_CONNECT))
            .jsonPath("$.[*].firstName")
            .value(hasItem(DEFAULT_FIRST_NAME))
            .jsonPath("$.[*].lastName")
            .value(hasItem(DEFAULT_LAST_NAME))
            .jsonPath("$.[*].mail")
            .value(hasItem(DEFAULT_MAIL))
            .jsonPath("$.[*].password")
            .value(hasItem(DEFAULT_PASSWORD));
    }

    @Test
    void getClientConnect() {
        // Initialize the database
        clientConnectRepository.save(clientConnect).block();

        // Get the clientConnect
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, clientConnect.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(clientConnect.getId().intValue()))
            .jsonPath("$.idClientConnect")
            .value(is(DEFAULT_ID_CLIENT_CONNECT))
            .jsonPath("$.firstName")
            .value(is(DEFAULT_FIRST_NAME))
            .jsonPath("$.lastName")
            .value(is(DEFAULT_LAST_NAME))
            .jsonPath("$.mail")
            .value(is(DEFAULT_MAIL))
            .jsonPath("$.password")
            .value(is(DEFAULT_PASSWORD));
    }

    @Test
    void getNonExistingClientConnect() {
        // Get the clientConnect
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingClientConnect() throws Exception {
        // Initialize the database
        clientConnectRepository.save(clientConnect).block();

        int databaseSizeBeforeUpdate = clientConnectRepository.findAll().collectList().block().size();

        // Update the clientConnect
        ClientConnect updatedClientConnect = clientConnectRepository.findById(clientConnect.getId()).block();
        updatedClientConnect
            .idClientConnect(UPDATED_ID_CLIENT_CONNECT)
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .mail(UPDATED_MAIL)
            .password(UPDATED_PASSWORD);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedClientConnect.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedClientConnect))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ClientConnect in the database
        List<ClientConnect> clientConnectList = clientConnectRepository.findAll().collectList().block();
        assertThat(clientConnectList).hasSize(databaseSizeBeforeUpdate);
        ClientConnect testClientConnect = clientConnectList.get(clientConnectList.size() - 1);
        assertThat(testClientConnect.getIdClientConnect()).isEqualTo(UPDATED_ID_CLIENT_CONNECT);
        assertThat(testClientConnect.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testClientConnect.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testClientConnect.getMail()).isEqualTo(UPDATED_MAIL);
        assertThat(testClientConnect.getPassword()).isEqualTo(UPDATED_PASSWORD);
    }

    @Test
    void putNonExistingClientConnect() throws Exception {
        int databaseSizeBeforeUpdate = clientConnectRepository.findAll().collectList().block().size();
        clientConnect.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, clientConnect.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(clientConnect))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ClientConnect in the database
        List<ClientConnect> clientConnectList = clientConnectRepository.findAll().collectList().block();
        assertThat(clientConnectList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchClientConnect() throws Exception {
        int databaseSizeBeforeUpdate = clientConnectRepository.findAll().collectList().block().size();
        clientConnect.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(clientConnect))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ClientConnect in the database
        List<ClientConnect> clientConnectList = clientConnectRepository.findAll().collectList().block();
        assertThat(clientConnectList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamClientConnect() throws Exception {
        int databaseSizeBeforeUpdate = clientConnectRepository.findAll().collectList().block().size();
        clientConnect.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(clientConnect))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ClientConnect in the database
        List<ClientConnect> clientConnectList = clientConnectRepository.findAll().collectList().block();
        assertThat(clientConnectList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateClientConnectWithPatch() throws Exception {
        // Initialize the database
        clientConnectRepository.save(clientConnect).block();

        int databaseSizeBeforeUpdate = clientConnectRepository.findAll().collectList().block().size();

        // Update the clientConnect using partial update
        ClientConnect partialUpdatedClientConnect = new ClientConnect();
        partialUpdatedClientConnect.setId(clientConnect.getId());

        partialUpdatedClientConnect
            .idClientConnect(UPDATED_ID_CLIENT_CONNECT)
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .password(UPDATED_PASSWORD);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedClientConnect.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedClientConnect))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ClientConnect in the database
        List<ClientConnect> clientConnectList = clientConnectRepository.findAll().collectList().block();
        assertThat(clientConnectList).hasSize(databaseSizeBeforeUpdate);
        ClientConnect testClientConnect = clientConnectList.get(clientConnectList.size() - 1);
        assertThat(testClientConnect.getIdClientConnect()).isEqualTo(UPDATED_ID_CLIENT_CONNECT);
        assertThat(testClientConnect.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testClientConnect.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testClientConnect.getMail()).isEqualTo(DEFAULT_MAIL);
        assertThat(testClientConnect.getPassword()).isEqualTo(UPDATED_PASSWORD);
    }

    @Test
    void fullUpdateClientConnectWithPatch() throws Exception {
        // Initialize the database
        clientConnectRepository.save(clientConnect).block();

        int databaseSizeBeforeUpdate = clientConnectRepository.findAll().collectList().block().size();

        // Update the clientConnect using partial update
        ClientConnect partialUpdatedClientConnect = new ClientConnect();
        partialUpdatedClientConnect.setId(clientConnect.getId());

        partialUpdatedClientConnect
            .idClientConnect(UPDATED_ID_CLIENT_CONNECT)
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .mail(UPDATED_MAIL)
            .password(UPDATED_PASSWORD);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedClientConnect.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedClientConnect))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ClientConnect in the database
        List<ClientConnect> clientConnectList = clientConnectRepository.findAll().collectList().block();
        assertThat(clientConnectList).hasSize(databaseSizeBeforeUpdate);
        ClientConnect testClientConnect = clientConnectList.get(clientConnectList.size() - 1);
        assertThat(testClientConnect.getIdClientConnect()).isEqualTo(UPDATED_ID_CLIENT_CONNECT);
        assertThat(testClientConnect.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testClientConnect.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testClientConnect.getMail()).isEqualTo(UPDATED_MAIL);
        assertThat(testClientConnect.getPassword()).isEqualTo(UPDATED_PASSWORD);
    }

    @Test
    void patchNonExistingClientConnect() throws Exception {
        int databaseSizeBeforeUpdate = clientConnectRepository.findAll().collectList().block().size();
        clientConnect.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, clientConnect.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(clientConnect))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ClientConnect in the database
        List<ClientConnect> clientConnectList = clientConnectRepository.findAll().collectList().block();
        assertThat(clientConnectList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchClientConnect() throws Exception {
        int databaseSizeBeforeUpdate = clientConnectRepository.findAll().collectList().block().size();
        clientConnect.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(clientConnect))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ClientConnect in the database
        List<ClientConnect> clientConnectList = clientConnectRepository.findAll().collectList().block();
        assertThat(clientConnectList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamClientConnect() throws Exception {
        int databaseSizeBeforeUpdate = clientConnectRepository.findAll().collectList().block().size();
        clientConnect.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(clientConnect))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ClientConnect in the database
        List<ClientConnect> clientConnectList = clientConnectRepository.findAll().collectList().block();
        assertThat(clientConnectList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteClientConnect() {
        // Initialize the database
        clientConnectRepository.save(clientConnect).block();

        int databaseSizeBeforeDelete = clientConnectRepository.findAll().collectList().block().size();

        // Delete the clientConnect
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, clientConnect.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<ClientConnect> clientConnectList = clientConnectRepository.findAll().collectList().block();
        assertThat(clientConnectList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
