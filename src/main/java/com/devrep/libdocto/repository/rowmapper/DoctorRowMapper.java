package com.devrep.libdocto.repository.rowmapper;

import com.devrep.libdocto.domain.Doctor;
import io.r2dbc.spi.Row;
import java.time.LocalDate;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Doctor}, with proper type conversions.
 */
@Service
public class DoctorRowMapper implements BiFunction<Row, String, Doctor> {

    private final ColumnConverter converter;

    public DoctorRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Doctor} stored in the database.
     */
    @Override
    public Doctor apply(Row row, String prefix) {
        Doctor entity = new Doctor();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setIdDoctor(converter.fromRow(row, prefix + "_id_doctor", Integer.class));
        entity.setFirstName(converter.fromRow(row, prefix + "_first_name", String.class));
        entity.setLastName(converter.fromRow(row, prefix + "_last_name", String.class));
        entity.setMail(converter.fromRow(row, prefix + "_mail", String.class));
        entity.setAddress(converter.fromRow(row, prefix + "_address", String.class));
        entity.setPhone(converter.fromRow(row, prefix + "_phone", String.class));
        entity.setScheduleStart(converter.fromRow(row, prefix + "_schedule_start", LocalDate.class));
        entity.setScheduletEnd(converter.fromRow(row, prefix + "_schedulet_end", LocalDate.class));
        entity.setCalendarId(converter.fromRow(row, prefix + "_calendar_id", Long.class));
        return entity;
    }
}
