package com.wallethub.util.log.data.dal;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.wallethub.util.log.data.model.HttpStatusCode;

@Component
public class ResponseStatusDAO {
	@Autowired
	JdbcTemplate jdbcTemplate;

	private Map<Integer, Integer> codes;

	@PostConstruct
	public void findAll() {
		String query = "SELECT * FROM http_status_code";
		codes = jdbcTemplate.query(query, new BeanPropertyRowMapper<HttpStatusCode>(HttpStatusCode.class)).stream()
				.collect(Collectors.toMap(HttpStatusCode::getCode, HttpStatusCode::getId));

//		codes.keySet().stream().forEach(code -> System.out.println(code));
	}

	public Map<Integer, Integer> getCodes() {
		return codes;
	}

	public void setCodes(Map<Integer, Integer> codes) {
		this.codes = codes;
	}

}
