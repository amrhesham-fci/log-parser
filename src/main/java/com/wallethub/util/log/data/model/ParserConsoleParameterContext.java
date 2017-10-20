package com.wallethub.util.log.data.model;

import java.util.Date;

import org.springframework.stereotype.Component;

import com.wallethub.util.log.common.AppConstants.DurationEnum;

@Component
public class ParserConsoleParameterContext {
	private String accesslogFilePath;
	private Date startDate;
	private DurationEnum durationEnum;
	private int threshold;

	public DurationEnum getDurationEnum() {
		return durationEnum;
	}

	public String getAccesslogFilePath() {
		return accesslogFilePath;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setAccesslogFilePath(String accesslogFilePath) {
		this.accesslogFilePath = accesslogFilePath;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public void setDurationEnum(DurationEnum durationEnum) {
		this.durationEnum = durationEnum;
	}

	public int getThreshold() {
		return threshold;
	}

	public void setThreshold(int threshold) {
		this.threshold = threshold;
	}

	@Override
	public String toString() {
		return "ParserConsoleParameterContext [accesslogFilePath=" + accesslogFilePath + ", startDate=" + startDate
				+ ", durationEnum=" + durationEnum + ", threshold=" + threshold + "]";
	}

}
