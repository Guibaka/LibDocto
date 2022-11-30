package com.devrep.libdocto.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class DoctorSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("id_doctor", table, columnPrefix + "_id_doctor"));
        columns.add(Column.aliased("first_name", table, columnPrefix + "_first_name"));
        columns.add(Column.aliased("last_name", table, columnPrefix + "_last_name"));
        columns.add(Column.aliased("mail", table, columnPrefix + "_mail"));
        columns.add(Column.aliased("address", table, columnPrefix + "_address"));
        columns.add(Column.aliased("phone", table, columnPrefix + "_phone"));
        columns.add(Column.aliased("schedule_start", table, columnPrefix + "_schedule_start"));
        columns.add(Column.aliased("schedulet_end", table, columnPrefix + "_schedulet_end"));

        columns.add(Column.aliased("calendar_id", table, columnPrefix + "_calendar_id"));
        return columns;
    }
}
