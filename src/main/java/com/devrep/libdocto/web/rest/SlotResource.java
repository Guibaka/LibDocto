package com.devrep.libdocto.web.rest;

import com.devrep.libdocto.domain.Slot;
import com.devrep.libdocto.repository.SlotRepository;
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
 * REST controller for managing {@link com.devrep.libdocto.domain.Slot}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class SlotResource {

    private final Logger log = LoggerFactory.getLogger(SlotResource.class);

    private static final String ENTITY_NAME = "slot";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SlotRepository slotRepository;

    public SlotResource(SlotRepository slotRepository) {
        this.slotRepository = slotRepository;
    }

    /**
     * {@code POST  /slots} : Create a new slot.
     *
     * @param slot the slot to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new slot, or with status {@code 400 (Bad Request)} if the slot has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/slots")
    public Mono<ResponseEntity<Slot>> createSlot(@Valid @RequestBody Slot slot) throws URISyntaxException {
        log.debug("REST request to save Slot : {}", slot);
        if (slot.getId() != null) {
            throw new BadRequestAlertException("A new slot cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return slotRepository
            .save(slot)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/slots/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /slots/:id} : Updates an existing slot.
     *
     * @param id the id of the slot to save.
     * @param slot the slot to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated slot,
     * or with status {@code 400 (Bad Request)} if the slot is not valid,
     * or with status {@code 500 (Internal Server Error)} if the slot couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/slots/{id}")
    public Mono<ResponseEntity<Slot>> updateSlot(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Slot slot
    ) throws URISyntaxException {
        log.debug("REST request to update Slot : {}, {}", id, slot);
        if (slot.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, slot.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return slotRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return slotRepository
                    .save(slot)
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
     * {@code PATCH  /slots/:id} : Partial updates given fields of an existing slot, field will ignore if it is null
     *
     * @param id the id of the slot to save.
     * @param slot the slot to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated slot,
     * or with status {@code 400 (Bad Request)} if the slot is not valid,
     * or with status {@code 404 (Not Found)} if the slot is not found,
     * or with status {@code 500 (Internal Server Error)} if the slot couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/slots/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Slot>> partialUpdateSlot(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Slot slot
    ) throws URISyntaxException {
        log.debug("REST request to partial update Slot partially : {}, {}", id, slot);
        if (slot.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, slot.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return slotRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Slot> result = slotRepository
                    .findById(slot.getId())
                    .map(existingSlot -> {
                        if (slot.getIdAppointment() != null) {
                            existingSlot.setIdAppointment(slot.getIdAppointment());
                        }
                        if (slot.getAvailability() != null) {
                            existingSlot.setAvailability(slot.getAvailability());
                        }
                        if (slot.getTimeStart() != null) {
                            existingSlot.setTimeStart(slot.getTimeStart());
                        }
                        if (slot.getTimeEnd() != null) {
                            existingSlot.setTimeEnd(slot.getTimeEnd());
                        }

                        return existingSlot;
                    })
                    .flatMap(slotRepository::save);

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
     * {@code GET  /slots} : get all the slots.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of slots in body.
     */
    @GetMapping("/slots")
    public Mono<List<Slot>> getAllSlots() {
        log.debug("REST request to get all Slots");
        return slotRepository.findAll().collectList();
    }

    /**
     * {@code GET  /slots} : get all the slots as a stream.
     * @return the {@link Flux} of slots.
     */
    @GetMapping(value = "/slots", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Slot> getAllSlotsAsStream() {
        log.debug("REST request to get all Slots as a stream");
        return slotRepository.findAll();
    }

    /**
     * {@code GET  /slots/:id} : get the "id" slot.
     *
     * @param id the id of the slot to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the slot, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/slots/{id}")
    public Mono<ResponseEntity<Slot>> getSlot(@PathVariable Long id) {
        log.debug("REST request to get Slot : {}", id);
        Mono<Slot> slot = slotRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(slot);
    }

    /**
     * {@code DELETE  /slots/:id} : delete the "id" slot.
     *
     * @param id the id of the slot to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/slots/{id}")
    public Mono<ResponseEntity<Void>> deleteSlot(@PathVariable Long id) {
        log.debug("REST request to delete Slot : {}", id);
        return slotRepository
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
