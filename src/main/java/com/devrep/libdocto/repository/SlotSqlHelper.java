package com.devrep.libdocto.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class SlotSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("id_appointment", table, columnPrefix + "_id_appointment"));
        columns.add(Column.aliased("availability", table, columnPrefix + "_availability"));
        columns.add(Column.aliased("time_start", table, columnPrefix + "_time_start"));
        columns.add(Column.aliased("time_end", table, columnPrefix + "_time_end"));

        columns.add(Column.aliased("doctor_id", table, columnPrefix + "_doctor_id"));
        columns.add(Column.aliased("client_connect_id", table, columnPrefix + "_client_connect_id"));
        columns.add(Column.aliased("calendar_id", table, columnPrefix + "_calendar_id"));
        return columns;
    }
}
