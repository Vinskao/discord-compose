package com.mli.discord.core.util;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;

/**
 * 將 LocalDateTime 轉換為資料庫 Timestamp，或將 Timestamp 轉換為 LocalDateTime 的 MyBatis
 * TypeHandler。
 * 
 * @Author D3031104
 * @version 1.0
 */
public class LocalDateTimeHandler extends BaseTypeHandler<LocalDateTime> {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 將 LocalDateTime 參數設置到 PreparedStatement 中。
     * 
     * @param ps        PreparedStatement 對象
     * @param i         索引位置
     * @param parameter LocalDateTime 參數
     * @param jdbcType  JdbcType
     * @throws SQLException 如果設置參數時發生 SQL 錯誤
     */
    @Override
    public void setNonNullParameter(@NonNull PreparedStatement ps, int i, LocalDateTime parameter,
            @NonNull JdbcType jdbcType) throws SQLException {
        logger.debug("Setting non-null parameter in PreparedStatement. Index: {}, Parameter: {}", i, parameter);

        ps.setTimestamp(i, Timestamp.valueOf(parameter.toLocalDate().atStartOfDay()));
    }

    /**
     * 從 ResultSet 中獲取指定欄位的 LocalDateTime 值。
     * 
     * @param rs         ResultSet 對象
     * @param columnName 欄位名稱
     * @return 指定欄位的 LocalDateTime 值，如果為 null 則返回 null
     * @throws SQLException 如果從 ResultSet 中獲取值時發生 SQL 錯誤
     */
    @Override
    public LocalDateTime getNullableResult(@NonNull ResultSet rs, String columnName) throws SQLException {
        logger.debug("Getting nullable result from ResultSet. Column Name: {}", columnName);

        Timestamp timestamp = rs.getTimestamp(columnName);
        return timestamp != null ? timestamp.toLocalDateTime().toLocalDate().atStartOfDay() : null;
    }

    /**
     * 從 ResultSet 中獲取指定索引位置的 LocalDateTime 值。
     * 
     * @param rs          ResultSet 對象
     * @param columnIndex 欄位索引位置
     * @return 指定索引位置的 LocalDateTime 值，如果為 null 則返回 null
     * @throws SQLException 如果從 ResultSet 中獲取值時發生 SQL 錯誤
     */
    @Override
    public LocalDateTime getNullableResult(@NonNull ResultSet rs, int columnIndex) throws SQLException {
        logger.debug("Getting nullable result from ResultSet. Column Index: {}", columnIndex);

        Timestamp timestamp = rs.getTimestamp(columnIndex);
        return timestamp != null ? timestamp.toLocalDateTime().toLocalDate().atStartOfDay() : null;
    }

    /**
     * 從 CallableStatement 中獲取指定索引位置的 LocalDateTime 值。
     * 
     * @param cs          CallableStatement 對象
     * @param columnIndex 欄位索引位置
     * @return 指定索引位置的 LocalDateTime 值，如果為 null 則返回 null
     * @throws SQLException 如果從 CallableStatement 中獲取值時發生 SQL 錯誤
     */
    @Override
    public LocalDateTime getNullableResult(@NonNull CallableStatement cs, int columnIndex) throws SQLException {
        logger.debug("Getting nullable result from CallableStatement. Column Index: {}", columnIndex);

        Timestamp timestamp = cs.getTimestamp(columnIndex);
        return timestamp != null ? timestamp.toLocalDateTime().toLocalDate().atStartOfDay() : null;
    }
}
