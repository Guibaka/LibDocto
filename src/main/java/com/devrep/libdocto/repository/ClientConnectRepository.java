package com.devrep.libdocto.repository;

import com.devrep.libdocto.domain.ClientConnect;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the ClientConnect entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ClientConnectRepository extends ReactiveCrudRepository<ClientConnect, Long>, ClientConnectRepositoryInternal {
    @Override
    <S extends ClientConnect> Mono<S> save(S entity);

    @Override
    Flux<ClientConnect> findAll();

    @Override
    Mono<ClientConnect> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface ClientConnectRepositoryInternal {
    <S extends ClientConnect> Mono<S> save(S entity);

    Flux<ClientConnect> findAllBy(Pageable pageable);

    Flux<ClientConnect> findAll();

    Mono<ClientConnect> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<ClientConnect> findAllBy(Pageable pageable, Criteria criteria);

}
