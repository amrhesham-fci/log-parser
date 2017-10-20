package com.wallethub.util.log.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.wallethub.util.log.common.AppConstants.DurationEnum;
import com.wallethub.util.log.data.dal.LogLineDAO;
import com.wallethub.util.log.data.dal.LogParserRequestDAO;
import com.wallethub.util.log.data.dal.ResponseStatusDAO;
import com.wallethub.util.log.data.model.AccessLogLine;
import com.wallethub.util.log.data.model.LogParserIPRequest;
import com.wallethub.util.log.data.model.ParserConsoleParameterContext;

@Component
public class AccessLogParserService {
	@Autowired
	ParserConsoleParameterContext parserConsoleParameterContext;

	@Autowired
	LogLineDAO logLineDAO;

	@Autowired
	ResponseStatusDAO statusDAO;

	@Autowired
	LogParserRequestDAO requestDAO;

	@Value("${parser.batch.size}")
	private int insertUpdateBatchSize;

	public void parseAccessLogFile() {
		BufferedReader fileReader;
		try {
			fileReader = new BufferedReader(
					new FileReader(new File(parserConsoleParameterContext.getAccesslogFilePath())));
			String line = null;
			AccessLogLine accessLogLine = null;
			List<AccessLogLine> listOfLines = new ArrayList<>();
			int counter = 1;

			while ((line = fileReader.readLine()) != null) {
				accessLogLine = parseLine(line);
				if ((counter++ % insertUpdateBatchSize) == 0) {
					logLineDAO.insertBatchLogLines(listOfLines);
					listOfLines.clear();

				}
				listOfLines.add(accessLogLine);
			}
			if (listOfLines.size() > 0) {
				logLineDAO.insertBatchLogLines(listOfLines);
			}
			System.out.println("------ insert access log, number of lines = " + (counter - 1));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private AccessLogLine parseLine(String line) {
		String[] lineInfo = line.split("\\|");
		String startDateStr = lineInfo[0];
		// System.out.println("eee "+startDateStr);
		Date startD = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		try {
			startD = sdf.parse(startDateStr);
		} catch (java.text.ParseException e) {
			e.printStackTrace();
		}
		Integer code = statusDAO.getCodes().get(Integer.parseInt(lineInfo[3]));
		// System.out.println("lineInfo[2] '" + lineInfo[3] + "' code '" +
		// code+"'");
		return new AccessLogLine(startD, lineInfo[1], code, lineInfo[2], lineInfo[4]);
	}

	public void getIPsByThreshold() {
		Calendar startDate = GregorianCalendar.getInstance(TimeZone.getTimeZone("EET"));
		Calendar endDate = GregorianCalendar.getInstance(TimeZone.getTimeZone("EET"));
		startDate.setTime(parserConsoleParameterContext.getStartDate());
		endDate.setTime(startDate.getTime());
		if (parserConsoleParameterContext.getDurationEnum() == DurationEnum.HOURLY) {
			endDate.add(Calendar.HOUR, 1);
		} else {
			endDate.add(Calendar.DAY_OF_MONTH, 1);
		}
		List<LogParserIPRequest> requests = logLineDAO.getIPsByThreshold(endDate.getTime());
		System.out.println("############# RESULT #############");
		requests.stream().forEach(request -> System.out
				.println("IP: " + request.getIp() + ", number of requests: " + request.getNumberOfRequest()));
		requestDAO.insertBatchIPRequests(requests);
		System.out.println("#################################");
	}
}
