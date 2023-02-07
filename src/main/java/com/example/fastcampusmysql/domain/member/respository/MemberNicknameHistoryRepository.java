package com.example.fastcampusmysql.domain.member.respository;

import com.example.fastcampusmysql.domain.member.entity.MemberNicknameHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class MemberNicknameHistoryRepository {

    private final NamedParameterJdbcTemplate namedJdbcTemplate;

    private static final RowMapper<MemberNicknameHistory> rowMapper = (ResultSet rs, int rowNum) -> MemberNicknameHistory.builder()
            .id(rs.getLong("id"))
            .memberId(rs.getLong("memberId"))
            .nickname(rs.getString("nickname"))
            .createdAt(rs.getObject("createdAt", LocalDateTime.class))
            .build();

    private final String TABLE = "memberNicknameHistory";


    public List<MemberNicknameHistory> findAllByMemberId(Long memberId) {
        String sql = String.format("SELECT * FROM %s WHERE memberId = :memberId", TABLE);
        SqlParameterSource params = new MapSqlParameterSource().addValue("memberId", memberId);
        return namedJdbcTemplate.query(sql, params, rowMapper);
    }

    public MemberNicknameHistory save(MemberNicknameHistory history) {
        /**
         * member id를 보고 갱신 또는 삽입을 정함
         * 반환값은 id를 담아서 반환한다.
         */
        if (history.getId() == null) {
            return insert(history);
        }
        throw new UnsupportedOperationException("MemberNicknameHistory는 갱신을 지원하지 않습니다.");
    }

    private MemberNicknameHistory insert(MemberNicknameHistory history) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(namedJdbcTemplate.getJdbcTemplate())
                .withTableName(TABLE)
                .usingGeneratedKeyColumns("id");

        SqlParameterSource params = new BeanPropertySqlParameterSource(history);
        var id = simpleJdbcInsert.executeAndReturnKey(params).longValue();

        return MemberNicknameHistory
                .builder()
                .id(id)
                .memberId(history.getMemberId())
                .nickname(history.getNickname())
                .createdAt(history.getCreatedAt())
                .build();
    }

}
