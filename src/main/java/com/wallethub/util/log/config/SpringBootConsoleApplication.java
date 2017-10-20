package com.wallethub.util.log.config;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import com.wallethub.util.log.common.AppConstants;
import com.wallethub.util.log.common.AppConstants.DurationEnum;
import com.wallethub.util.log.data.dal.LogLineDAO;
import com.wallethub.util.log.data.model.ParserConsoleParameterContext;
import com.wallethub.util.log.service.AccessLogParserService;

@SpringBootApplication
@ComponentScan(basePackages = "com.wallethub.util.log")
public class SpringBootConsoleApplication implements CommandLineRunner {

	@Autowired
	ParserConsoleParameterContext parserConsoleParameterContext;

	@Autowired
	AccessLogParserService accessLogParser;

	public static void main(String[] args) {
		SpringApplication.run(SpringBootConsoleApplication.class, args);
	}

	public void run(String... args) throws Exception {
		final CommandLineParser cmdLineParser = new DefaultParser();
		CommandLine commandLine = null;
		Options options = generateOptions();
		String errorMsg = null;
		try {
			commandLine = cmdLineParser.parse(options, args);
			final String fileOption = commandLine.getOptionValue(AppConstants.COMMANDLINE_PARAM_ACCESSLOG);
			final String startDateOption = commandLine.getOptionValue(AppConstants.COMMANDLINE_PARAM_STARTDATE);
			final String durationOption = commandLine.getOptionValue(AppConstants.COMMANDLINE_PARAM_DURATION);
			final String thresholdOption = commandLine.getOptionValue(AppConstants.COMMANDLINE_PARAM_THRESHOLD);
			errorMsg = validateArguments(fileOption, startDateOption, durationOption, thresholdOption);
			if (!errorMsg.isEmpty()) {
				printOptionsHelp(options, errorMsg);
			}
			accessLogParser.parseAccessLogFile();
			accessLogParser.getIPsByThreshold();
			System.out.println(parserConsoleParameterContext);
		} catch (ParseException parseException) {
			// automatically generate the help statement
			errorMsg = "ERROR: Unable to parse command-line arguments " + Arrays.toString(args) + " due to: "
					+ parseException.getMessage();
			printOptionsHelp(options, errorMsg);

		}
	}

	private String validateArguments(final String fileOption, final String startDateOption, final String durationOption,
			final String thresholdOption) {
		StringBuilder errorMsg = new StringBuilder();
		if (fileOption == null || fileOption.isEmpty()) {
			errorMsg.append("File path is empty or null.\n");
		} else {
			parserConsoleParameterContext.setAccesslogFilePath(fileOption);
		}
		if (startDateOption == null || startDateOption.isEmpty()) {
			errorMsg.append("start date is empty or null.\n");
		} else { // parse date
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd.HH:mm:ss");
			try {
				Date startD = sdf.parse(startDateOption);
				parserConsoleParameterContext.setStartDate(startD);
			} catch (java.text.ParseException e) {
				errorMsg.append("start date format don't match 'yyyy-MM-dd.HH:mm:ss'.\n");
			}
		}
		if (durationOption == null || durationOption.isEmpty()
				|| !(durationOption.equalsIgnoreCase(DurationEnum.HOURLY.toString())
						&& !durationOption.equalsIgnoreCase(DurationEnum.DAILY.toString()))) {
			errorMsg.append("duration is empty, null or not from the following value list (hourly, daily).\n");
		} else { // make sure it's hourly or daily
			if (durationOption.equalsIgnoreCase(DurationEnum.HOURLY.toString())) {
				parserConsoleParameterContext.setDurationEnum(DurationEnum.HOURLY);
			} else {
				parserConsoleParameterContext.setDurationEnum(DurationEnum.DAILY);
			}
		}
		if (thresholdOption == null || thresholdOption.isEmpty()) {
			errorMsg.append("threshold is empty or null.\n");
		} else { // convert to int
			try {
				parserConsoleParameterContext.setThreshold(Integer.parseInt(thresholdOption));
			} catch (NumberFormatException numberFormatException) {
				errorMsg.append("threshold could not be recognized as a number.\n");
				throw numberFormatException;
			}
		}
		return errorMsg.toString();
	}

	private Options generateOptions() {
		final Option fileOption = Option.builder().required().longOpt(AppConstants.COMMANDLINE_PARAM_ACCESSLOG).hasArg()
				.desc("File Path to be processed.").build();
		final Option startDateOption = Option.builder().required().longOpt(AppConstants.COMMANDLINE_PARAM_STARTDATE)
				.hasArg().desc("Start date for search with format 'yyyy-MM-dd HH:mm:ss.SSS'.").build();
		final Option durationOption = Option.builder().required().longOpt(AppConstants.COMMANDLINE_PARAM_DURATION)
				.hasArg().desc("duration (Hourly, Daily).").build();
		final Option thresholdOption = Option.builder().required().longOpt(AppConstants.COMMANDLINE_PARAM_THRESHOLD)
				.hasArg().desc("Threshold4.").build();
		final Options options = new Options();
		options.addOption(fileOption);
		options.addOption(startDateOption);
		options.addOption(durationOption);
		options.addOption(thresholdOption);
		return options;
	}

	private void printOptionsHelp(Options options, String errorMsg) {
		StringBuilder header = new StringBuilder(
				"##################################### HELP #####################################\n ");
		if (errorMsg != null) {
			header.append(errorMsg);
		}
		String footer = "##################################### ";
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("GNU Like Syntax --Param=value", header.toString(), options, footer);
	}
}
