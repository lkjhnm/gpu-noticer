package cdc.toy.noticer.entity;

import java.util.ArrayList;
import java.util.List;

public class SmsBody {

	private final String type;
	private final String from;
	private String content;
	private List<Message> messages;

	public SmsBody(String content) {
		this.type = "SMS";
		this.from = System.getenv("SMS_HOST");
		this.content = content;
		this.messages = new ArrayList<>();
	}

	public SmsBody(String content, String to) {
		this(content);
		this.messages.add(new Message(to));
	}

	public void to(String phoneNumber) {
		this.messages.add(new Message(phoneNumber));
	}

	public String getType() {
		return type;
	}

	public String getFrom() {
		return from;
	}

	public String getContent() {
		return content;
	}

	public List<Message> getMessages() {
		return messages;
	}

	private class Message {
		String to;

		public Message(String to) {
			this.to = to;
		}

		public String getTo() {
			return to;
		}
	}
}
