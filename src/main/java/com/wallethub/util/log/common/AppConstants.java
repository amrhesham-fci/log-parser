package com.wallethub.util.log.common;

public class AppConstants {

	public static final String COMMANDLINE_PARAM_ACCESSLOG = "accessLog";
	public static final String COMMANDLINE_PARAM_STARTDATE= "startDate";
	public static final String COMMANDLINE_PARAM_THRESHOLD= "threshold";
	public static final String COMMANDLINE_PARAM_DURATION= "duration";

	public static enum DurationEnum {
		DAILY("daily"), HOURLY("hourly");
		private final String duration;

		private DurationEnum(String duration) {
			this.duration = duration;
		}

		@Override
		public String toString() {
			return duration;
		}
	}
}
