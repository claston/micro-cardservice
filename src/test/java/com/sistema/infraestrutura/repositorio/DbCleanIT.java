package com.sistema.infraestrutura.repositorio;

import io.agroal.api.AgroalDataSource;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public abstract class DbCleanIT {

    @Inject
    AgroalDataSource ds;

    @BeforeEach
    void cleanDb() throws Exception {
        try (Connection conn = ds.getConnection();
             Statement st = conn.createStatement()) {

            // Desliga FK pra truncar sem dor
            st.execute("SET REFERENTIAL_INTEGRITY FALSE");

            List<String> tables = new ArrayList<>();

            try (ResultSet rs = st.executeQuery(
                    "SELECT TABLE_NAME " +
                            "FROM INFORMATION_SCHEMA.TABLES " +
                            "WHERE TABLE_SCHEMA='PUBLIC' AND TABLE_TYPE='BASE TABLE'")) {
                while (rs.next()) tables.add(rs.getString(1));
            }

            for (String table : tables) {
                st.executeUpdate("TRUNCATE TABLE " + table);
            }

            st.execute("SET REFERENTIAL_INTEGRITY TRUE");

            try (ResultSet rs = st.executeQuery(
                    "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES " +
                            "WHERE TABLE_SCHEMA='PUBLIC' AND TABLE_NAME='TENANTS'")) {
                if (rs.next() && rs.getInt(1) > 0) {
                    st.executeUpdate(
                            "INSERT INTO tenants (id, name, status, created_at) " +
                                    "VALUES ('00000000-0000-0000-0000-000000000000', 'DEFAULT', 'ACTIVE', CURRENT_TIMESTAMP)"
                    );
                }
            }
        }
    }
}
