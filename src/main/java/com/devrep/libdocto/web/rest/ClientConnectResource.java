package com.devrep.libdocto.web.rest;

import com.devrep.libdocto.domain.ClientConnect;
import com.devrep.libdocto.repository.ClientConnectRepository;
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
 * REST controller for managing {@link com.devrep.libdocto.domain.ClientConnect}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class ClientConnectResource {

    private final Logger log = LoggerFactory.getLogger(ClientConnectResource.class);

    private static final String ENTITY_NAME = "clientConnect";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ClientConnectRepository clientConnectRepository;

    public ClientConnectResource(ClientConnectRepository clientConnectRepository) {
        this.clientConnectRepository = clientConnectRepository;
    }

    /**
     * {@code POST  /client-connects} : Create a new clientConnect.
     *
     * @param clientConnect the clientConnect to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new clientConnect, or with status {@code 400 (Bad Request)} if the clientConnect has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/client-connects")
    public Mono<ResponseEntity<ClientConnect>> createClientConnect(@Valid @RequestBody ClientConnect clientConnect)
        throws URISyntaxException {
        log.debug("REST request to save ClientConnect : {}", clientConnect);
        if (clientConnect.getId() != null) {
            throw new BadRequestAlertException("A new clientConnect cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return clientConnectRepository
            .save(clientConnect)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/client-connects/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /client-connects/:id} : Updates an existing clientConnect.
     *
     * @param id the id of the clientConnect to save.
     * @param clientConnect the clientConnect to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated clientConnect,
     * or with status {@code 400 (Bad Request)} if the clientConnect is not valid,
     * or with status {@code 500 (Internal Server Error)} if the clientConnect couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/client-connects/{id}")
    public Mono<ResponseEntity<ClientConnect>> updateClientConnect(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ClientConnect clientConnect
    ) throws URISyntaxException {
        log.debug("REST request to update ClientConnect : {}, {}", id, clientConnect);
        if (clientConnect.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, clientConnect.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return clientConnectRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return clientConnectRepository
                    .save(clientConnect)
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
     * {@code PATCH  /client-connects/:id} : Partial updates given fields of an existing clientConnect, field will ignore if it is null
     *
     * @param id the id of the clientConnect to save.
     * @param clientConnect the clientConnect to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated clientConnect,
     * or with status {@code 400 (Bad Request)} if the clientConnect is not valid,
     * or with status {@code 404 (Not Found)} if the clientConnect is not found,
     * or with status {@code 500 (Internal Server Error)} if the clientConnect couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/client-connects/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<ClientConnect>> partialUpdateClientConnect(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ClientConnect clientConnect
    ) throws URISyntaxException {
        log.debug("REST request to partial update ClientConnect partially : {}, {}", id, clientConnect);
        if (clientConnect.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, clientConnect.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return clientConnectRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<ClientConnect> result = clientConnectRepository
                    .findById(clientConnect.getId())
                    .map(existingClientConnect -> {
                        if (clientConnect.getIdClientConnect() != null) {
                            existingClientConnect.setIdClientConnect(clientConnect.getIdClientConnect());
                        }
                        if (clientConnect.getFirstName() != null) {
                            existingClientConnect.setFirstName(clientConnect.getFirstName());
                        }
                        if (clientConnect.getLastName() != null) {
                            existingClientConnect.setLastName(clientConnect.getLastName());
                        }
                        if (clientConnect.getMail() != null) {
                            existingClientConnect.setMail(clientConnect.getMail());
                        }
                        if (clientConnect.getPassword() != null) {
                            existingClientConnect.setPassword(clientConnect.getPassword());
                        }

                        return existingClientConnect;
                    })
                    .flatMap(clientConnectRepository::save);

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
     * {@code GET  /client-connects} : get all the clientConnects.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of clientConnects in body.
     */
    @GetMapping("/client-connects")
    public Mono<List<ClientConnect>> getAllClientConnects() {
        log.debug("REST request to get all ClientConnects");
        return clientConnectRepository.findAll().collectList();
    }

    /**
     * {@code GET  /client-connects} : get all the clientConnects as a stream.
     * @return the {@link Flux} of clientConnects.
     */
    @GetMapping(value = "/client-connects", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<ClientConnect> getAllClientConnectsAsStream() {
        log.debug("REST request to get all ClientConnects as a stream");
        return clientConnectRepository.findAll();
    }

    /**
     * {@code GET  /client-connects/:id} : get the "id" clientConnect.
     *
     * @param id the id of the clientConnect to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the clientConnect, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/client-connects/{id}")
    public Mono<ResponseEntity<ClientConnect>> getClientConnect(@PathVariable Long id) {
        log.debug("REST request to get ClientConnect : {}", id);
        Mono<ClientConnect> clientConnect = clientConnectRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(clientConnect);
    }

    /**
     * {@code DELETE  /client-connects/:id} : delete the "id" clientConnect.
     *
     * @param id the id of the clientConnect to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/client-connects/{id}")
    public Mono<ResponseEntity<Void>> deleteClientConnect(@PathVariable Long id) {
        log.debug("REST request to delete ClientConnect : {}", id);
        return clientConnectRepository
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
