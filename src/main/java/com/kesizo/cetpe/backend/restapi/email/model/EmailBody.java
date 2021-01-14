package com.kesizo.cetpe.backend.restapi.email.model;

import java.util.Objects;

public class EmailBody {
	private String email;
	private String content;
	private String subject;
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		EmailBody emailBody = (EmailBody) o;
		return Objects.equals(email, emailBody.email) && Objects.equals(content, emailBody.content) && Objects.equals(subject, emailBody.subject);
	}

	@Override
	public int hashCode() {
		return Objects.hash(email, content, subject);
	}

	@Override
	public String toString() {
		return "EmailBody [email=" + email + ", content=" + content + ", subject=" + subject + "]";
	}
	
	
}