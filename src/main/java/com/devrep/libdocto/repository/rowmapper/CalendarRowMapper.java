package com.devrep.libdocto.repository.rowmapper;

import com.devrep.libdocto.domain.Calendar;
import io.r2dbc.spi.Row;
import java.time.LocalDate;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Calendar}, with proper type conversions.
 */
@Service
public class CalendarRowMapper implements BiFunction<Row, String, Calendar> {

    private final ColumnConverter converter;

    public CalendarRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Calendar} stored in the database.
     */
    @Override
    public Calendar apply(Row row, String prefix) {
        Calendar entity = new Calendar();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setIdCalender(converter.fromRow(row, prefix + "_id_calender", Integer.class));
        entity.setTimeStart(converter.fromRow(row, prefix + "_time_start", LocalDate.class));
        entity.setTimeEnd(converter.fromRow(row, prefix + "_time_end", LocalDate.class));
        return entity;
    }
}
