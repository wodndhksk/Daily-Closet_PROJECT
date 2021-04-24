package com.megait.soir.domain;

import lombok.ToString;

@ToString
public class ReviewDomain {
	private long no;
	private String title;
	private String contents;
	
	public long getNo() {
		return no;
	}
	public void setBno(long no) {
		this.no = no;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContents() {
		return contents;
	}
	public void setContents(String contents) {
		this.contents = contents;
	}
}
