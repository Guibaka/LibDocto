package com.devrep.libdocto.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.devrep.libdocto.IntegrationTest;
import com.devrep.libdocto.domain.Slot;
import com.devrep.libdocto.domain.enumeration.StateSlot;
import com.devrep.libdocto.repository.EntityManager;
import com.devrep.libdocto.repository.SlotRepository;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
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
 * Integration tests for the {@link SlotResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class SlotResourceIT {

    private static final Integer DEFAULT_ID_APPOINTMENT = 1;
    private static final Integer UPDATED_ID_APPOINTMENT = 2;

    private static final StateSlot DEFAULT_AVAILABILITY = StateSlot.AVAILABLE;
    private static final StateSlot UPDATED_AVAILABILITY = StateSlot.BOOKED;

    private static final LocalDate DEFAULT_TIME_START = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_TIME_START = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_TIME_END = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_TIME_END = LocalDate.now(ZoneId.systemDefault());

    private static final String ENTITY_API_URL = "/api/slots";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private SlotRepository slotRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Slot slot;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Slot createEntity(EntityManager em) {
        Slot slot = new Slot()
            .idAppointment(DEFAULT_ID_APPOINTMENT)
            .availability(DEFAULT_AVAILABILITY)
            .timeStart(DEFAULT_TIME_START)
            .timeEnd(DEFAULT_TIME_END);
        return slot;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Slot createUpdatedEntity(EntityManager em) {
        Slot slot = new Slot()
            .idAppointment(UPDATED_ID_APPOINTMENT)
            .availability(UPDATED_AVAILABILITY)
            .timeStart(UPDATED_TIME_START)
            .timeEnd(UPDATED_TIME_END);
        return slot;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Slot.class).block();
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
        slot = createEntity(em);
    }

    @Test
    void createSlot() throws Exception {
        int databaseSizeBeforeCreate = slotRepository.findAll().collectList().block().size();
        // Create the Slot
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(slot))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Slot in the database
        List<Slot> slotList = slotRepository.findAll().collectList().block();
        assertThat(slotList).hasSize(databaseSizeBeforeCreate + 1);
        Slot testSlot = slotList.get(slotList.size() - 1);
        assertThat(testSlot.getIdAppointment()).isEqualTo(DEFAULT_ID_APPOINTMENT);
        assertThat(testSlot.getAvailability()).isEqualTo(DEFAULT_AVAILABILITY);
        assertThat(testSlot.getTimeStart()).isEqualTo(DEFAULT_TIME_START);
        assertThat(testSlot.getTimeEnd()).isEqualTo(DEFAULT_TIME_END);
    }

    @Test
    void createSlotWithExistingId() throws Exception {
        // Create the Slot with an existing ID
        slot.setId(1L);

        int databaseSizeBeforeCreate = slotRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(slot))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Slot in the database
        List<Slot> slotList = slotRepository.findAll().collectList().block();
        assertThat(slotList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkIdAppointmentIsRequired() throws Exception {
        int databaseSizeBeforeTest = slotRepository.findAll().collectList().block().size();
        // set the field null
        slot.setIdAppointment(null);

        // Create the Slot, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(slot))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Slot> slotList = slotRepository.findAll().collectList().block();
        assertThat(slotList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllSlotsAsStream() {
        // Initialize the database
        slotRepository.save(slot).block();

        List<Slot> slotList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Slot.class)
            .getResponseBody()
            .filter(slot::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(slotList).isNotNull();
        assertThat(slotList).hasSize(1);
        Slot testSlot = slotList.get(0);
        assertThat(testSlot.getIdAppointment()).isEqualTo(DEFAULT_ID_APPOINTMENT);
        assertThat(testSlot.getAvailability()).isEqualTo(DEFAULT_AVAILABILITY);
        assertThat(testSlot.getTimeStart()).isEqualTo(DEFAULT_TIME_START);
        assertThat(testSlot.getTimeEnd()).isEqualTo(DEFAULT_TIME_END);
    }

    @Test
    void getAllSlots() {
        // Initialize the database
        slotRepository.save(slot).block();

        // Get all the slotList
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
            .value(hasItem(slot.getId().intValue()))
            .jsonPath("$.[*].idAppointment")
            .value(hasItem(DEFAULT_ID_APPOINTMENT))
            .jsonPath("$.[*].availability")
            .value(hasItem(DEFAULT_AVAILABILITY.toString()))
            .jsonPath("$.[*].timeStart")
            .value(hasItem(DEFAULT_TIME_START.toString()))
            .jsonPath("$.[*].timeEnd")
            .value(hasItem(DEFAULT_TIME_END.toString()));
    }

    @Test
    void getSlot() {
        // Initialize the database
        slotRepository.save(slot).block();

        // Get the slot
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, slot.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(slot.getId().intValue()))
            .jsonPath("$.idAppointment")
            .value(is(DEFAULT_ID_APPOINTMENT))
            .jsonPath("$.availability")
            .value(is(DEFAULT_AVAILABILITY.toString()))
            .jsonPath("$.timeStart")
            .value(is(DEFAULT_TIME_START.toString()))
            .jsonPath("$.timeEnd")
            .value(is(DEFAULT_TIME_END.toString()));
    }

    @Test
    void getNonExistingSlot() {
        // Get the slot
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingSlot() throws Exception {
        // Initialize the database
        slotRepository.save(slot).block();

        int databaseSizeBeforeUpdate = slotRepository.findAll().collectList().block().size();

        // Update the slot
        Slot updatedSlot = slotRepository.findById(slot.getId()).block();
        updatedSlot
            .idAppointment(UPDATED_ID_APPOINTMENT)
            .availability(UPDATED_AVAILABILITY)
            .timeStart(UPDATED_TIME_START)
            .timeEnd(UPDATED_TIME_END);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedSlot.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedSlot))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Slot in the database
        List<Slot> slotList = slotRepository.findAll().collectList().block();
        assertThat(slotList).hasSize(databaseSizeBeforeUpdate);
        Slot testSlot = slotList.get(slotList.size() - 1);
        assertThat(testSlot.getIdAppointment()).isEqualTo(UPDATED_ID_APPOINTMENT);
        assertThat(testSlot.getAvailability()).isEqualTo(UPDATED_AVAILABILITY);
        assertThat(testSlot.getTimeStart()).isEqualTo(UPDATED_TIME_START);
        assertThat(testSlot.getTimeEnd()).isEqualTo(UPDATED_TIME_END);
    }

    @Test
    void putNonExistingSlot() throws Exception {
        int databaseSizeBeforeUpdate = slotRepository.findAll().collectList().block().size();
        slot.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, slot.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(slot))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Slot in the database
        List<Slot> slotList = slotRepository.findAll().collectList().block();
        assertThat(slotList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchSlot() throws Exception {
        int databaseSizeBeforeUpdate = slotRepository.findAll().collectList().block().size();
        slot.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(slot))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Slot in the database
        List<Slot> slotList = slotRepository.findAll().collectList().block();
        assertThat(slotList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamSlot() throws Exception {
        int databaseSizeBeforeUpdate = slotRepository.findAll().collectList().block().size();
        slot.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(slot))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Slot in the database
        List<Slot> slotList = slotRepository.findAll().collectList().block();
        assertThat(slotList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateSlotWithPatch() throws Exception {
        // Initialize the database
        slotRepository.save(slot).block();

        int databaseSizeBeforeUpdate = slotRepository.findAll().collectList().block().size();

        // Update the slot using partial update
        Slot partialUpdatedSlot = new Slot();
        partialUpdatedSlot.setId(slot.getId());

        partialUpdatedSlot.availability(UPDATED_AVAILABILITY).timeStart(UPDATED_TIME_START).timeEnd(UPDATED_TIME_END);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedSlot.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedSlot))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Slot in the database
        List<Slot> slotList = slotRepository.findAll().collectList().block();
        assertThat(slotList).hasSize(databaseSizeBeforeUpdate);
        Slot testSlot = slotList.get(slotList.size() - 1);
        assertThat(testSlot.getIdAppointment()).isEqualTo(DEFAULT_ID_APPOINTMENT);
        assertThat(testSlot.getAvailability()).isEqualTo(UPDATED_AVAILABILITY);
        assertThat(testSlot.getTimeStart()).isEqualTo(UPDATED_TIME_START);
        assertThat(testSlot.getTimeEnd()).isEqualTo(UPDATED_TIME_END);
    }

    @Test
    void fullUpdateSlotWithPatch() throws Exception {
        // Initialize the database
        slotRepository.save(slot).block();

        int databaseSizeBeforeUpdate = slotRepository.findAll().collectList().block().size();

        // Update the slot using partial update
        Slot partialUpdatedSlot = new Slot();
        partialUpdatedSlot.setId(slot.getId());

        partialUpdatedSlot
            .idAppointment(UPDATED_ID_APPOINTMENT)
            .availability(UPDATED_AVAILABILITY)
            .timeStart(UPDATED_TIME_START)
            .timeEnd(UPDATED_TIME_END);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedSlot.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedSlot))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Slot in the database
        List<Slot> slotList = slotRepository.findAll().collectList().block();
        assertThat(slotList).hasSize(databaseSizeBeforeUpdate);
        Slot testSlot = slotList.get(slotList.size() - 1);
        assertThat(testSlot.getIdAppointment()).isEqualTo(UPDATED_ID_APPOINTMENT);
        assertThat(testSlot.getAvailability()).isEqualTo(UPDATED_AVAILABILITY);
        assertThat(testSlot.getTimeStart()).isEqualTo(UPDATED_TIME_START);
        assertThat(testSlot.getTimeEnd()).isEqualTo(UPDATED_TIME_END);
    }

    @Test
    void patchNonExistingSlot() throws Exception {
        int databaseSizeBeforeUpdate = slotRepository.findAll().collectList().block().size();
        slot.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, slot.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(slot))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Slot in the database
        List<Slot> slotList = slotRepository.findAll().collectList().block();
        assertThat(slotList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchSlot() throws Exception {
        int databaseSizeBeforeUpdate = slotRepository.findAll().collectList().block().size();
        slot.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(slot))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Slot in the database
        List<Slot> slotList = slotRepository.findAll().collectList().block();
        assertThat(slotList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamSlot() throws Exception {
        int databaseSizeBeforeUpdate = slotRepository.findAll().collectList().block().size();
        slot.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(slot))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Slot in the database
        List<Slot> slotList = slotRepository.findAll().collectList().block();
        assertThat(slotList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteSlot() {
        // Initialize the database
        slotRepository.save(slot).block();

        int databaseSizeBeforeDelete = slotRepository.findAll().collectList().block().size();

        // Delete the slot
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, slot.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Slot> slotList = slotRepository.findAll().collectList().block();
        assertThat(slotList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
