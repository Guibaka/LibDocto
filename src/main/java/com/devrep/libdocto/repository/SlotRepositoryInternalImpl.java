package com.devrep.libdocto.repository;

import static org.springframework.data.relational.core.query.Criteria.where;

import com.devrep.libdocto.domain.Slot;
import com.devrep.libdocto.domain.enumeration.StateSlot;
import com.devrep.libdocto.repository.rowmapper.CalendarRowMapper;
import com.devrep.libdocto.repository.rowmapper.ClientConnectRowMapper;
import com.devrep.libdocto.repository.rowmapper.DoctorRowMapper;
import com.devrep.libdocto.repository.rowmapper.SlotRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiFunction;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Comparison;
import org.springframework.data.relational.core.sql.Condition;
import org.springframework.data.relational.core.sql.Conditions;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the Slot entity.
 */
@SuppressWarnings("unused")
class SlotRepositoryInternalImpl extends SimpleR2dbcRepository<Slot, Long> implements SlotRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final DoctorRowMapper doctorMapper;
    private final ClientConnectRowMapper clientconnectMapper;
    private final CalendarRowMapper calendarMapper;
    private final SlotRowMapper slotMapper;

    private static final Table entityTable = Table.aliased("slot", EntityManager.ENTITY_ALIAS);
    private static final Table doctorTable = Table.aliased("doctor", "doctor");
    private static final Table clientConnectTable = Table.aliased("client_connect", "clientConnect");
    private static final Table calendarTable = Table.aliased("calendar", "calendar");

    public SlotRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        DoctorRowMapper doctorMapper,
        ClientConnectRowMapper clientconnectMapper,
        CalendarRowMapper calendarMapper,
        SlotRowMapper slotMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Slot.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.doctorMapper = doctorMapper;
        this.clientconnectMapper = clientconnectMapper;
        this.calendarMapper = calendarMapper;
        this.slotMapper = slotMapper;
    }

    @Override
    public Flux<Slot> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Slot> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = SlotSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(DoctorSqlHelper.getColumns(doctorTable, "doctor"));
        columns.addAll(ClientConnectSqlHelper.getColumns(clientConnectTable, "clientConnect"));
        columns.addAll(CalendarSqlHelper.getColumns(calendarTable, "calendar"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(doctorTable)
            .on(Column.create("doctor_id", entityTable))
            .equals(Column.create("id", doctorTable))
            .leftOuterJoin(clientConnectTable)
            .on(Column.create("client_connect_id", entityTable))
            .equals(Column.create("id", clientConnectTable))
            .leftOuterJoin(calendarTable)
            .on(Column.create("calendar_id", entityTable))
            .equals(Column.create("id", calendarTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Slot.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Slot> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Slot> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private Slot process(Row row, RowMetadata metadata) {
        Slot entity = slotMapper.apply(row, "e");
        entity.setDoctor(doctorMapper.apply(row, "doctor"));
        entity.setClientConnect(clientconnectMapper.apply(row, "clientConnect"));
        entity.setCalendar(calendarMapper.apply(row, "calendar"));
        return entity;
    }

    @Override
    public <S extends Slot> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
