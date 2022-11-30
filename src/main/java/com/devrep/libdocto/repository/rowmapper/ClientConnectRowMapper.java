package com.devrep.libdocto.repository.rowmapper;

import com.devrep.libdocto.domain.ClientConnect;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link ClientConnect}, with proper type conversions.
 */
@Service
public class ClientConnectRowMapper implements BiFunction<Row, String, ClientConnect> {

    private final ColumnConverter converter;

    public ClientConnectRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link ClientConnect} stored in the database.
     */
    @Override
    public ClientConnect apply(Row row, String prefix) {
        ClientConnect entity = new ClientConnect();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setIdClientConnect(converter.fromRow(row, prefix + "_id_client_connect", Integer.class));
        entity.setFirstName(converter.fromRow(row, prefix + "_first_name", String.class));
        entity.setLastName(converter.fromRow(row, prefix + "_last_name", String.class));
        entity.setMail(converter.fromRow(row, prefix + "_mail", String.class));
        entity.setPassword(converter.fromRow(row, prefix + "_password", String.class));
        return entity;
    }
}
