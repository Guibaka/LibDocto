package com.devrep.libdocto.web.rest;

import com.devrep.libdocto.domain.Doctor;
import com.devrep.libdocto.repository.DoctorRepository;
import com.devrep.libdocto.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.devrep.libdocto.domain.Doctor}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class DoctorResource {

    private final Logger log = LoggerFactory.getLogger(DoctorResource.class);

    private static final String ENTITY_NAME = "doctor";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final DoctorRepository doctorRepository;

    public DoctorResource(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    /**
     * {@code POST  /doctors} : Create a new doctor.
     *
     * @param doctor the doctor to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new doctor, or with status {@code 400 (Bad Request)} if the doctor has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/doctors")
    public Mono<ResponseEntity<Doctor>> createDoctor(@Valid @RequestBody Doctor doctor) throws URISyntaxException {
        log.debug("REST request to save Doctor : {}", doctor);
        if (doctor.getId() != null) {
            throw new BadRequestAlertException("A new doctor cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return doctorRepository
            .save(doctor)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/doctors/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /doctors/:id} : Updates an existing doctor.
     *
     * @param id the id of the doctor to save.
     * @param doctor the doctor to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated doctor,
     * or with status {@code 400 (Bad Request)} if the doctor is not valid,
     * or with status {@code 500 (Internal Server Error)} if the doctor couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/doctors/{id}")
    public Mono<ResponseEntity<Doctor>> updateDoctor(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Doctor doctor
    ) throws URISyntaxException {
        log.debug("REST request to update Doctor : {}, {}", id, doctor);
        if (doctor.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, doctor.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return doctorRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return doctorRepository
                    .save(doctor)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /doctors/:id} : Partial updates given fields of an existing doctor, field will ignore if it is null
     *
     * @param id the id of the doctor to save.
     * @param doctor the doctor to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated doctor,
     * or with status {@code 400 (Bad Request)} if the doctor is not valid,
     * or with status {@code 404 (Not Found)} if the doctor is not found,
     * or with status {@code 500 (Internal Server Error)} if the doctor couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/doctors/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Doctor>> partialUpdateDoctor(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Doctor doctor
    ) throws URISyntaxException {
        log.debug("REST request to partial update Doctor partially : {}, {}", id, doctor);
        if (doctor.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, doctor.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return doctorRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Doctor> result = doctorRepository
                    .findById(doctor.getId())
                    .map(existingDoctor -> {
                        if (doctor.getIdDoctor() != null) {
                            existingDoctor.setIdDoctor(doctor.getIdDoctor());
                        }
                        if (doctor.getFirstName() != null) {
                            existingDoctor.setFirstName(doctor.getFirstName());
                        }
                        if (doctor.getLastName() != null) {
                            existingDoctor.setLastName(doctor.getLastName());
                        }
                        if (doctor.getMail() != null) {
                            existingDoctor.setMail(doctor.getMail());
                        }
                        if (doctor.getAddress() != null) {
                            existingDoctor.setAddress(doctor.getAddress());
                        }
                        if (doctor.getPhone() != null) {
                            existingDoctor.setPhone(doctor.getPhone());
                        }
                        if (doctor.getScheduleStart() != null) {
                            existingDoctor.setScheduleStart(doctor.getScheduleStart());
                        }
                        if (doctor.getScheduletEnd() != null) {
                            existingDoctor.setScheduletEnd(doctor.getScheduletEnd());
                        }

                        return existingDoctor;
                    })
                    .flatMap(doctorRepository::save);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, res.getId().toString()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /doctors} : get all the doctors.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of doctors in body.
     */
    @GetMapping("/doctors")
    public Mono<List<Doctor>> getAllDoctors() {
        log.debug("REST request to get all Doctors");
        return doctorRepository.findAll().collectList();
    }

    /**
     * {@code GET  /doctors} : get all the doctors as a stream.
     * @return the {@link Flux} of doctors.
     */
    @GetMapping(value = "/doctors", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Doctor> getAllDoctorsAsStream() {
        log.debug("REST request to get all Doctors as a stream");
        return doctorRepository.findAll();
    }

    /**
     * {@code GET  /doctors/:id} : get the "id" doctor.
     *
     * @param id the id of the doctor to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the doctor, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/doctors/{id}")
    public Mono<ResponseEntity<Doctor>> getDoctor(@PathVariable Long id) {
        log.debug("REST request to get Doctor : {}", id);
        Mono<Doctor> doctor = doctorRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(doctor);
    }

    /**
     * {@code DELETE  /doctors/:id} : delete the "id" doctor.
     *
     * @param id the id of the doctor to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/doctors/{id}")
    public Mono<ResponseEntity<Void>> deleteDoctor(@PathVariable Long id) {
        log.debug("REST request to delete Doctor : {}", id);
        return doctorRepository
            .deleteById(id)
            .then(
                Mono.just(
                    ResponseEntity
                        .noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
                        .build()
                )
            );
    }
}
