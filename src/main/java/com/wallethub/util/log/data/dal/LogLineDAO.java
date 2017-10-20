package com.wallethub.util.log.data.dal;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.wallethub.util.log.common.AppConstants.DurationEnum;
import com.wallethub.util.log.data.model.AccessLogLine;
import com.wallethub.util.log.data.model.LogParserIPRequest;
import com.wallethub.util.log.data.model.ParserConsoleParameterContext;

@Component
public class LogLineDAO {

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	ParserConsoleParameterContext parserConsoleParameterContext;

	public boolean insertBatchLogLines(List<AccessLogLine> accessLogLines) {
		boolean inserted = false;
		String query = "INSERT INTO log_line(start_date, IP, request, response_status_id, user_agent) VALUES ( ?, ?, ?, ?, ?)";

		int[] affectedRows = jdbcTemplate.batchUpdate(query, new BatchPreparedStatementSetter() {

			@Override
			public int getBatchSize() {
				// TODO Auto-generated method stub
				return accessLogLines.size();
			}

			@Override
			public void setValues(PreparedStatement ps, int iteratorId) throws SQLException {
				AccessLogLine accessLogLine = accessLogLines.get(iteratorId);
				ps.setTimestamp(1, new Timestamp(accessLogLine.getStartDate().getTime()));
				ps.setString(2, accessLogLine.getIP());
				ps.setString(3, accessLogLine.getRequest());
				ps.setInt(4, accessLogLine.getReponseCode());
				ps.setString(5, accessLogLine.getUserAgent());

			}

		});
		return inserted;
	}

	@PostConstruct
	public void truncateTable() {
		System.out.println("------ truncate access log lines before parse ------");
		String query = "DELETE FROM log_line";
		jdbcTemplate.execute(query);
	}

	public List<LogParserIPRequest> getIPsByThreshold(Date endDate) {
		String query = "SELECT  IP, COUNT(*) requests FROM log_line WHERE start_date BETWEEN ? AND ? GROUP BY IP HAVING COUNT(*) > ?";

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(query, new Object[] {
				parserConsoleParameterContext.getStartDate(), endDate, parserConsoleParameterContext.getThreshold() });
		List<LogParserIPRequest> requests = rows.stream().map(row -> {
			LogParserIPRequest pr = new LogParserIPRequest();
			pr.setIp(row.get("IP").toString());
			pr.setNumberOfRequest(Integer.parseInt(row.get("requests").toString()));
			pr.setDuration(parserConsoleParameterContext.getDurationEnum());
			pr.setStartDate(parserConsoleParameterContext.getStartDate());
			pr.setThreshold(parserConsoleParameterContext.getThreshold());
			return pr;
		}).collect(Collectors.toList());
		return requests;

	}

}
