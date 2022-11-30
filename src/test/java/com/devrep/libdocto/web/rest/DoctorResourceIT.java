package com.devrep.libdocto.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.devrep.libdocto.IntegrationTest;
import com.devrep.libdocto.domain.Doctor;
import com.devrep.libdocto.repository.DoctorRepository;
import com.devrep.libdocto.repository.EntityManager;
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
 * Integration tests for the {@link DoctorResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class DoctorResourceIT {

    private static final Integer DEFAULT_ID_DOCTOR = 1;
    private static final Integer UPDATED_ID_DOCTOR = 2;

    private static final String DEFAULT_FIRST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FIRST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LAST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_LAST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_MAIL = "AAAAAAAAAA";
    private static final String UPDATED_MAIL = "BBBBBBBBBB";

    private static final String DEFAULT_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS = "BBBBBBBBBB";

    private static final String DEFAULT_PHONE = "AAAAAAAAAA";
    private static final String UPDATED_PHONE = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_SCHEDULE_START = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_SCHEDULE_START = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_SCHEDULET_END = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_SCHEDULET_END = LocalDate.now(ZoneId.systemDefault());

    private static final String ENTITY_API_URL = "/api/doctors";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Doctor doctor;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Doctor createEntity(EntityManager em) {
        Doctor doctor = new Doctor()
            .idDoctor(DEFAULT_ID_DOCTOR)
            .firstName(DEFAULT_FIRST_NAME)
            .lastName(DEFAULT_LAST_NAME)
            .mail(DEFAULT_MAIL)
            .address(DEFAULT_ADDRESS)
            .phone(DEFAULT_PHONE)
            .scheduleStart(DEFAULT_SCHEDULE_START)
            .scheduletEnd(DEFAULT_SCHEDULET_END);
        return doctor;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Doctor createUpdatedEntity(EntityManager em) {
        Doctor doctor = new Doctor()
            .idDoctor(UPDATED_ID_DOCTOR)
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .mail(UPDATED_MAIL)
            .address(UPDATED_ADDRESS)
            .phone(UPDATED_PHONE)
            .scheduleStart(UPDATED_SCHEDULE_START)
            .scheduletEnd(UPDATED_SCHEDULET_END);
        return doctor;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Doctor.class).block();
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
        doctor = createEntity(em);
    }

    @Test
    void createDoctor() throws Exception {
        int databaseSizeBeforeCreate = doctorRepository.findAll().collectList().block().size();
        // Create the Doctor
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(doctor))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Doctor in the database
        List<Doctor> doctorList = doctorRepository.findAll().collectList().block();
        assertThat(doctorList).hasSize(databaseSizeBeforeCreate + 1);
        Doctor testDoctor = doctorList.get(doctorList.size() - 1);
        assertThat(testDoctor.getIdDoctor()).isEqualTo(DEFAULT_ID_DOCTOR);
        assertThat(testDoctor.getFirstName()).isEqualTo(DEFAULT_FIRST_NAME);
        assertThat(testDoctor.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
        assertThat(testDoctor.getMail()).isEqualTo(DEFAULT_MAIL);
        assertThat(testDoctor.getAddress()).isEqualTo(DEFAULT_ADDRESS);
        assertThat(testDoctor.getPhone()).isEqualTo(DEFAULT_PHONE);
        assertThat(testDoctor.getScheduleStart()).isEqualTo(DEFAULT_SCHEDULE_START);
        assertThat(testDoctor.getScheduletEnd()).isEqualTo(DEFAULT_SCHEDULET_END);
    }

    @Test
    void createDoctorWithExistingId() throws Exception {
        // Create the Doctor with an existing ID
        doctor.setId(1L);

        int databaseSizeBeforeCreate = doctorRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(doctor))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Doctor in the database
        List<Doctor> doctorList = doctorRepository.findAll().collectList().block();
        assertThat(doctorList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkIdDoctorIsRequired() throws Exception {
        int databaseSizeBeforeTest = doctorRepository.findAll().collectList().block().size();
        // set the field null
        doctor.setIdDoctor(null);

        // Create the Doctor, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(doctor))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Doctor> doctorList = doctorRepository.findAll().collectList().block();
        assertThat(doctorList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllDoctorsAsStream() {
        // Initialize the database
        doctorRepository.save(doctor).block();

        List<Doctor> doctorList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Doctor.class)
            .getResponseBody()
            .filter(doctor::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(doctorList).isNotNull();
        assertThat(doctorList).hasSize(1);
        Doctor testDoctor = doctorList.get(0);
        assertThat(testDoctor.getIdDoctor()).isEqualTo(DEFAULT_ID_DOCTOR);
        assertThat(testDoctor.getFirstName()).isEqualTo(DEFAULT_FIRST_NAME);
        assertThat(testDoctor.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
        assertThat(testDoctor.getMail()).isEqualTo(DEFAULT_MAIL);
        assertThat(testDoctor.getAddress()).isEqualTo(DEFAULT_ADDRESS);
        assertThat(testDoctor.getPhone()).isEqualTo(DEFAULT_PHONE);
        assertThat(testDoctor.getScheduleStart()).isEqualTo(DEFAULT_SCHEDULE_START);
        assertThat(testDoctor.getScheduletEnd()).isEqualTo(DEFAULT_SCHEDULET_END);
    }

    @Test
    void getAllDoctors() {
        // Initialize the database
        doctorRepository.save(doctor).block();

        // Get all the doctorList
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
            .value(hasItem(doctor.getId().intValue()))
            .jsonPath("$.[*].idDoctor")
            .value(hasItem(DEFAULT_ID_DOCTOR))
            .jsonPath("$.[*].firstName")
            .value(hasItem(DEFAULT_FIRST_NAME))
            .jsonPath("$.[*].lastName")
            .value(hasItem(DEFAULT_LAST_NAME))
            .jsonPath("$.[*].mail")
            .value(hasItem(DEFAULT_MAIL))
            .jsonPath("$.[*].address")
            .value(hasItem(DEFAULT_ADDRESS))
            .jsonPath("$.[*].phone")
            .value(hasItem(DEFAULT_PHONE))
            .jsonPath("$.[*].scheduleStart")
            .value(hasItem(DEFAULT_SCHEDULE_START.toString()))
            .jsonPath("$.[*].scheduletEnd")
            .value(hasItem(DEFAULT_SCHEDULET_END.toString()));
    }

    @Test
    void getDoctor() {
        // Initialize the database
        doctorRepository.save(doctor).block();

        // Get the doctor
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, doctor.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(doctor.getId().intValue()))
            .jsonPath("$.idDoctor")
            .value(is(DEFAULT_ID_DOCTOR))
            .jsonPath("$.firstName")
            .value(is(DEFAULT_FIRST_NAME))
            .jsonPath("$.lastName")
            .value(is(DEFAULT_LAST_NAME))
            .jsonPath("$.mail")
            .value(is(DEFAULT_MAIL))
            .jsonPath("$.address")
            .value(is(DEFAULT_ADDRESS))
            .jsonPath("$.phone")
            .value(is(DEFAULT_PHONE))
            .jsonPath("$.scheduleStart")
            .value(is(DEFAULT_SCHEDULE_START.toString()))
            .jsonPath("$.scheduletEnd")
            .value(is(DEFAULT_SCHEDULET_END.toString()));
    }

    @Test
    void getNonExistingDoctor() {
        // Get the doctor
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingDoctor() throws Exception {
        // Initialize the database
        doctorRepository.save(doctor).block();

        int databaseSizeBeforeUpdate = doctorRepository.findAll().collectList().block().size();

        // Update the doctor
        Doctor updatedDoctor = doctorRepository.findById(doctor.getId()).block();
        updatedDoctor
            .idDoctor(UPDATED_ID_DOCTOR)
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .mail(UPDATED_MAIL)
            .address(UPDATED_ADDRESS)
            .phone(UPDATED_PHONE)
            .scheduleStart(UPDATED_SCHEDULE_START)
            .scheduletEnd(UPDATED_SCHEDULET_END);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedDoctor.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedDoctor))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Doctor in the database
        List<Doctor> doctorList = doctorRepository.findAll().collectList().block();
        assertThat(doctorList).hasSize(databaseSizeBeforeUpdate);
        Doctor testDoctor = doctorList.get(doctorList.size() - 1);
        assertThat(testDoctor.getIdDoctor()).isEqualTo(UPDATED_ID_DOCTOR);
        assertThat(testDoctor.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testDoctor.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testDoctor.getMail()).isEqualTo(UPDATED_MAIL);
        assertThat(testDoctor.getAddress()).isEqualTo(UPDATED_ADDRESS);
        assertThat(testDoctor.getPhone()).isEqualTo(UPDATED_PHONE);
        assertThat(testDoctor.getScheduleStart()).isEqualTo(UPDATED_SCHEDULE_START);
        assertThat(testDoctor.getScheduletEnd()).isEqualTo(UPDATED_SCHEDULET_END);
    }

    @Test
    void putNonExistingDoctor() throws Exception {
        int databaseSizeBeforeUpdate = doctorRepository.findAll().collectList().block().size();
        doctor.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, doctor.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(doctor))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Doctor in the database
        List<Doctor> doctorList = doctorRepository.findAll().collectList().block();
        assertThat(doctorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchDoctor() throws Exception {
        int databaseSizeBeforeUpdate = doctorRepository.findAll().collectList().block().size();
        doctor.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(doctor))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Doctor in the database
        List<Doctor> doctorList = doctorRepository.findAll().collectList().block();
        assertThat(doctorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamDoctor() throws Exception {
        int databaseSizeBeforeUpdate = doctorRepository.findAll().collectList().block().size();
        doctor.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(doctor))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Doctor in the database
        List<Doctor> doctorList = doctorRepository.findAll().collectList().block();
        assertThat(doctorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateDoctorWithPatch() throws Exception {
        // Initialize the database
        doctorRepository.save(doctor).block();

        int databaseSizeBeforeUpdate = doctorRepository.findAll().collectList().block().size();

        // Update the doctor using partial update
        Doctor partialUpdatedDoctor = new Doctor();
        partialUpdatedDoctor.setId(doctor.getId());

        partialUpdatedDoctor
            .idDoctor(UPDATED_ID_DOCTOR)
            .firstName(UPDATED_FIRST_NAME)
            .address(UPDATED_ADDRESS)
            .scheduleStart(UPDATED_SCHEDULE_START)
            .scheduletEnd(UPDATED_SCHEDULET_END);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedDoctor.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedDoctor))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Doctor in the database
        List<Doctor> doctorList = doctorRepository.findAll().collectList().block();
        assertThat(doctorList).hasSize(databaseSizeBeforeUpdate);
        Doctor testDoctor = doctorList.get(doctorList.size() - 1);
        assertThat(testDoctor.getIdDoctor()).isEqualTo(UPDATED_ID_DOCTOR);
        assertThat(testDoctor.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testDoctor.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
        assertThat(testDoctor.getMail()).isEqualTo(DEFAULT_MAIL);
        assertThat(testDoctor.getAddress()).isEqualTo(UPDATED_ADDRESS);
        assertThat(testDoctor.getPhone()).isEqualTo(DEFAULT_PHONE);
        assertThat(testDoctor.getScheduleStart()).isEqualTo(UPDATED_SCHEDULE_START);
        assertThat(testDoctor.getScheduletEnd()).isEqualTo(UPDATED_SCHEDULET_END);
    }

    @Test
    void fullUpdateDoctorWithPatch() throws Exception {
        // Initialize the database
        doctorRepository.save(doctor).block();

        int databaseSizeBeforeUpdate = doctorRepository.findAll().collectList().block().size();

        // Update the doctor using partial update
        Doctor partialUpdatedDoctor = new Doctor();
        partialUpdatedDoctor.setId(doctor.getId());

        partialUpdatedDoctor
            .idDoctor(UPDATED_ID_DOCTOR)
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .mail(UPDATED_MAIL)
            .address(UPDATED_ADDRESS)
            .phone(UPDATED_PHONE)
            .scheduleStart(UPDATED_SCHEDULE_START)
            .scheduletEnd(UPDATED_SCHEDULET_END);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedDoctor.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedDoctor))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Doctor in the database
        List<Doctor> doctorList = doctorRepository.findAll().collectList().block();
        assertThat(doctorList).hasSize(databaseSizeBeforeUpdate);
        Doctor testDoctor = doctorList.get(doctorList.size() - 1);
        assertThat(testDoctor.getIdDoctor()).isEqualTo(UPDATED_ID_DOCTOR);
        assertThat(testDoctor.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testDoctor.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testDoctor.getMail()).isEqualTo(UPDATED_MAIL);
        assertThat(testDoctor.getAddress()).isEqualTo(UPDATED_ADDRESS);
        assertThat(testDoctor.getPhone()).isEqualTo(UPDATED_PHONE);
        assertThat(testDoctor.getScheduleStart()).isEqualTo(UPDATED_SCHEDULE_START);
        assertThat(testDoctor.getScheduletEnd()).isEqualTo(UPDATED_SCHEDULET_END);
    }

    @Test
    void patchNonExistingDoctor() throws Exception {
        int databaseSizeBeforeUpdate = doctorRepository.findAll().collectList().block().size();
        doctor.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, doctor.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(doctor))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Doctor in the database
        List<Doctor> doctorList = doctorRepository.findAll().collectList().block();
        assertThat(doctorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchDoctor() throws Exception {
        int databaseSizeBeforeUpdate = doctorRepository.findAll().collectList().block().size();
        doctor.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(doctor))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Doctor in the database
        List<Doctor> doctorList = doctorRepository.findAll().collectList().block();
        assertThat(doctorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamDoctor() throws Exception {
        int databaseSizeBeforeUpdate = doctorRepository.findAll().collectList().block().size();
        doctor.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(doctor))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Doctor in the database
        List<Doctor> doctorList = doctorRepository.findAll().collectList().block();
        assertThat(doctorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteDoctor() {
        // Initialize the database
        doctorRepository.save(doctor).block();

        int databaseSizeBeforeDelete = doctorRepository.findAll().collectList().block().size();

        // Delete the doctor
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, doctor.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Doctor> doctorList = doctorRepository.findAll().collectList().block();
        assertThat(doctorList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
