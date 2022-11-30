package com.devrep.libdocto.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class CalendarSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("id_calender", table, columnPrefix + "_id_calender"));
        columns.add(Column.aliased("time_start", table, columnPrefix + "_time_start"));
        columns.add(Column.aliased("time_end", table, columnPrefix + "_time_end"));

        return columns;
    }
}
