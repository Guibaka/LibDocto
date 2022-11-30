package com.devrep.libdocto.repository;

import com.devrep.libdocto.domain.Slot;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Slot entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SlotRepository extends ReactiveCrudRepository<Slot, Long>, SlotRepositoryInternal {
    @Query("SELECT * FROM slot entity WHERE entity.doctor_id = :id")
    Flux<Slot> findByDoctor(Long id);

    @Query("SELECT * FROM slot entity WHERE entity.doctor_id IS NULL")
    Flux<Slot> findAllWhereDoctorIsNull();

    @Query("SELECT * FROM slot entity WHERE entity.client_connect_id = :id")
    Flux<Slot> findByClientConnect(Long id);

    @Query("SELECT * FROM slot entity WHERE entity.client_connect_id IS NULL")
    Flux<Slot> findAllWhereClientConnectIsNull();

    @Query("SELECT * FROM slot entity WHERE entity.calendar_id = :id")
    Flux<Slot> findByCalendar(Long id);

    @Query("SELECT * FROM slot entity WHERE entity.calendar_id IS NULL")
    Flux<Slot> findAllWhereCalendarIsNull();

    @Override
    <S extends Slot> Mono<S> save(S entity);

    @Override
    Flux<Slot> findAll();

    @Override
    Mono<Slot> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface SlotRepositoryInternal {
    <S extends Slot> Mono<S> save(S entity);

    Flux<Slot> findAllBy(Pageable pageable);

    Flux<Slot> findAll();

    Mono<Slot> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Slot> findAllBy(Pageable pageable, Criteria criteria);

}
