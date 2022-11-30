package com.devrep.libdocto.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.devrep.libdocto.IntegrationTest;
import com.devrep.libdocto.domain.Calendar;
import com.devrep.libdocto.repository.CalendarRepository;
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
 * Integration tests for the {@link CalendarResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class CalendarResourceIT {

    private static final Integer DEFAULT_ID_CALENDER = 1;
    private static final Integer UPDATED_ID_CALENDER = 2;

    private static final LocalDate DEFAULT_TIME_START = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_TIME_START = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_TIME_END = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_TIME_END = LocalDate.now(ZoneId.systemDefault());

    private static final String ENTITY_API_URL = "/api/calendars";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CalendarRepository calendarRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Calendar calendar;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Calendar createEntity(EntityManager em) {
        Calendar calendar = new Calendar().idCalender(DEFAULT_ID_CALENDER).timeStart(DEFAULT_TIME_START).timeEnd(DEFAULT_TIME_END);
        return calendar;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Calendar createUpdatedEntity(EntityManager em) {
        Calendar calendar = new Calendar().idCalender(UPDATED_ID_CALENDER).timeStart(UPDATED_TIME_START).timeEnd(UPDATED_TIME_END);
        return calendar;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Calendar.class).block();
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
        calendar = createEntity(em);
    }

    @Test
    void createCalendar() throws Exception {
        int databaseSizeBeforeCreate = calendarRepository.findAll().collectList().block().size();
        // Create the Calendar
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(calendar))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Calendar in the database
        List<Calendar> calendarList = calendarRepository.findAll().collectList().block();
        assertThat(calendarList).hasSize(databaseSizeBeforeCreate + 1);
        Calendar testCalendar = calendarList.get(calendarList.size() - 1);
        assertThat(testCalendar.getIdCalender()).isEqualTo(DEFAULT_ID_CALENDER);
        assertThat(testCalendar.getTimeStart()).isEqualTo(DEFAULT_TIME_START);
        assertThat(testCalendar.getTimeEnd()).isEqualTo(DEFAULT_TIME_END);
    }

    @Test
    void createCalendarWithExistingId() throws Exception {
        // Create the Calendar with an existing ID
        calendar.setId(1L);

        int databaseSizeBeforeCreate = calendarRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(calendar))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Calendar in the database
        List<Calendar> calendarList = calendarRepository.findAll().collectList().block();
        assertThat(calendarList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkIdCalenderIsRequired() throws Exception {
        int databaseSizeBeforeTest = calendarRepository.findAll().collectList().block().size();
        // set the field null
        calendar.setIdCalender(null);

        // Create the Calendar, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(calendar))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Calendar> calendarList = calendarRepository.findAll().collectList().block();
        assertThat(calendarList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllCalendarsAsStream() {
        // Initialize the database
        calendarRepository.save(calendar).block();

        List<Calendar> calendarList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Calendar.class)
            .getResponseBody()
            .filter(calendar::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(calendarList).isNotNull();
        assertThat(calendarList).hasSize(1);
        Calendar testCalendar = calendarList.get(0);
        assertThat(testCalendar.getIdCalender()).isEqualTo(DEFAULT_ID_CALENDER);
        assertThat(testCalendar.getTimeStart()).isEqualTo(DEFAULT_TIME_START);
        assertThat(testCalendar.getTimeEnd()).isEqualTo(DEFAULT_TIME_END);
    }

    @Test
    void getAllCalendars() {
        // Initialize the database
        calendarRepository.save(calendar).block();

        // Get all the calendarList
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
            .value(hasItem(calendar.getId().intValue()))
            .jsonPath("$.[*].idCalender")
            .value(hasItem(DEFAULT_ID_CALENDER))
            .jsonPath("$.[*].timeStart")
            .value(hasItem(DEFAULT_TIME_START.toString()))
            .jsonPath("$.[*].timeEnd")
            .value(hasItem(DEFAULT_TIME_END.toString()));
    }

    @Test
    void getCalendar() {
        // Initialize the database
        calendarRepository.save(calendar).block();

        // Get the calendar
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, calendar.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(calendar.getId().intValue()))
            .jsonPath("$.idCalender")
            .value(is(DEFAULT_ID_CALENDER))
            .jsonPath("$.timeStart")
            .value(is(DEFAULT_TIME_START.toString()))
            .jsonPath("$.timeEnd")
            .value(is(DEFAULT_TIME_END.toString()));
    }

    @Test
    void getNonExistingCalendar() {
        // Get the calendar
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingCalendar() throws Exception {
        // Initialize the database
        calendarRepository.save(calendar).block();

        int databaseSizeBeforeUpdate = calendarRepository.findAll().collectList().block().size();

        // Update the calendar
        Calendar updatedCalendar = calendarRepository.findById(calendar.getId()).block();
        updatedCalendar.idCalender(UPDATED_ID_CALENDER).timeStart(UPDATED_TIME_START).timeEnd(UPDATED_TIME_END);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedCalendar.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedCalendar))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Calendar in the database
        List<Calendar> calendarList = calendarRepository.findAll().collectList().block();
        assertThat(calendarList).hasSize(databaseSizeBeforeUpdate);
        Calendar testCalendar = calendarList.get(calendarList.size() - 1);
        assertThat(testCalendar.getIdCalender()).isEqualTo(UPDATED_ID_CALENDER);
        assertThat(testCalendar.getTimeStart()).isEqualTo(UPDATED_TIME_START);
        assertThat(testCalendar.getTimeEnd()).isEqualTo(UPDATED_TIME_END);
    }

    @Test
    void putNonExistingCalendar() throws Exception {
        int databaseSizeBeforeUpdate = calendarRepository.findAll().collectList().block().size();
        calendar.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, calendar.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(calendar))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Calendar in the database
        List<Calendar> calendarList = calendarRepository.findAll().collectList().block();
        assertThat(calendarList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchCalendar() throws Exception {
        int databaseSizeBeforeUpdate = calendarRepository.findAll().collectList().block().size();
        calendar.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(calendar))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Calendar in the database
        List<Calendar> calendarList = calendarRepository.findAll().collectList().block();
        assertThat(calendarList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamCalendar() throws Exception {
        int databaseSizeBeforeUpdate = calendarRepository.findAll().collectList().block().size();
        calendar.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(calendar))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Calendar in the database
        List<Calendar> calendarList = calendarRepository.findAll().collectList().block();
        assertThat(calendarList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateCalendarWithPatch() throws Exception {
        // Initialize the database
        calendarRepository.save(calendar).block();

        int databaseSizeBeforeUpdate = calendarRepository.findAll().collectList().block().size();

        // Update the calendar using partial update
        Calendar partialUpdatedCalendar = new Calendar();
        partialUpdatedCalendar.setId(calendar.getId());

        partialUpdatedCalendar.idCalender(UPDATED_ID_CALENDER).timeStart(UPDATED_TIME_START);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCalendar.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCalendar))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Calendar in the database
        List<Calendar> calendarList = calendarRepository.findAll().collectList().block();
        assertThat(calendarList).hasSize(databaseSizeBeforeUpdate);
        Calendar testCalendar = calendarList.get(calendarList.size() - 1);
        assertThat(testCalendar.getIdCalender()).isEqualTo(UPDATED_ID_CALENDER);
        assertThat(testCalendar.getTimeStart()).isEqualTo(UPDATED_TIME_START);
        assertThat(testCalendar.getTimeEnd()).isEqualTo(DEFAULT_TIME_END);
    }

    @Test
    void fullUpdateCalendarWithPatch() throws Exception {
        // Initialize the database
        calendarRepository.save(calendar).block();

        int databaseSizeBeforeUpdate = calendarRepository.findAll().collectList().block().size();

        // Update the calendar using partial update
        Calendar partialUpdatedCalendar = new Calendar();
        partialUpdatedCalendar.setId(calendar.getId());

        partialUpdatedCalendar.idCalender(UPDATED_ID_CALENDER).timeStart(UPDATED_TIME_START).timeEnd(UPDATED_TIME_END);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCalendar.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCalendar))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Calendar in the database
        List<Calendar> calendarList = calendarRepository.findAll().collectList().block();
        assertThat(calendarList).hasSize(databaseSizeBeforeUpdate);
        Calendar testCalendar = calendarList.get(calendarList.size() - 1);
        assertThat(testCalendar.getIdCalender()).isEqualTo(UPDATED_ID_CALENDER);
        assertThat(testCalendar.getTimeStart()).isEqualTo(UPDATED_TIME_START);
        assertThat(testCalendar.getTimeEnd()).isEqualTo(UPDATED_TIME_END);
    }

    @Test
    void patchNonExistingCalendar() throws Exception {
        int databaseSizeBeforeUpdate = calendarRepository.findAll().collectList().block().size();
        calendar.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, calendar.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(calendar))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Calendar in the database
        List<Calendar> calendarList = calendarRepository.findAll().collectList().block();
        assertThat(calendarList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchCalendar() throws Exception {
        int databaseSizeBeforeUpdate = calendarRepository.findAll().collectList().block().size();
        calendar.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(calendar))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Calendar in the database
        List<Calendar> calendarList = calendarRepository.findAll().collectList().block();
        assertThat(calendarList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamCalendar() throws Exception {
        int databaseSizeBeforeUpdate = calendarRepository.findAll().collectList().block().size();
        calendar.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(calendar))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Calendar in the database
        List<Calendar> calendarList = calendarRepository.findAll().collectList().block();
        assertThat(calendarList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteCalendar() {
        // Initialize the database
        calendarRepository.save(calendar).block();

        int databaseSizeBeforeDelete = calendarRepository.findAll().collectList().block().size();

        // Delete the calendar
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, calendar.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Calendar> calendarList = calendarRepository.findAll().collectList().block();
        assertThat(calendarList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
