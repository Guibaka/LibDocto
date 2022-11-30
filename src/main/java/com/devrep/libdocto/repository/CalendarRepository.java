package com.devrep.libdocto.repository;

import com.devrep.libdocto.domain.Calendar;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Calendar entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CalendarRepository extends ReactiveCrudRepository<Calendar, Long>, CalendarRepositoryInternal {
    @Override
    <S extends Calendar> Mono<S> save(S entity);

    @Override
    Flux<Calendar> findAll();

    @Override
    Mono<Calendar> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface CalendarRepositoryInternal {
    <S extends Calendar> Mono<S> save(S entity);

    Flux<Calendar> findAllBy(Pageable pageable);

    Flux<Calendar> findAll();

    Mono<Calendar> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Calendar> findAllBy(Pageable pageable, Criteria criteria);

}
