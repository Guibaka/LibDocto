package com.devrep.libdocto.repository.rowmapper;

import com.devrep.libdocto.domain.Slot;
import com.devrep.libdocto.domain.enumeration.StateSlot;
import io.r2dbc.spi.Row;
import java.time.LocalDate;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Slot}, with proper type conversions.
 */
@Service
public class SlotRowMapper implements BiFunction<Row, String, Slot> {

    private final ColumnConverter converter;

    public SlotRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Slot} stored in the database.
     */
    @Override
    public Slot apply(Row row, String prefix) {
        Slot entity = new Slot();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setIdAppointment(converter.fromRow(row, prefix + "_id_appointment", Integer.class));
        entity.setAvailability(converter.fromRow(row, prefix + "_availability", StateSlot.class));
        entity.setTimeStart(converter.fromRow(row, prefix + "_time_start", LocalDate.class));
        entity.setTimeEnd(converter.fromRow(row, prefix + "_time_end", LocalDate.class));
        entity.setDoctorId(converter.fromRow(row, prefix + "_doctor_id", Long.class));
        entity.setClientConnectId(converter.fromRow(row, prefix + "_client_connect_id", Long.class));
        entity.setCalendarId(converter.fromRow(row, prefix + "_calendar_id", Long.class));
        return entity;
    }
}
