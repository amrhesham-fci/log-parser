package com.wallethub.util.log.data.dal;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.wallethub.util.log.data.model.LogParserIPRequest;

@Component
public class LogParserRequestDAO {
	@Autowired
	JdbcTemplate jdbcTemplate;

	public boolean insertBatchIPRequests(List<LogParserIPRequest> requests) {
		boolean inserted = false;
		String query = "INSERT INTO log_parser_request(start_date, IP, duration, threshold) VALUES ( ?, ?, ?, ?)";

		jdbcTemplate.batchUpdate(query, new BatchPreparedStatementSetter() {

			@Override
			public int getBatchSize() {
				// TODO Auto-generated method stub
				return requests.size();
			}

			@Override
			public void setValues(PreparedStatement ps, int iteratorId) throws SQLException {
				LogParserIPRequest request = requests.get(iteratorId);
				ps.setTimestamp(1, new Timestamp(request.getStartDate().getTime()));
				ps.setString(2, request.getIp());
				ps.setString(3, request.getDuration().name());
				ps.setInt(4, request.getThreshold());

			}

		});
		return inserted;
	}
}
