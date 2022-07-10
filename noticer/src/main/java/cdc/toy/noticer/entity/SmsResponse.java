package cdc.toy.noticer.entity;

public class SmsResponse {

	private String statusCode;
	private String statusName;
	private String requestId;
	private String requestTime;

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public String getStatusName() {
		return statusName;
	}

	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getRequestTime() {
		return requestTime;
	}

	public void setRequestTime(String requestTime) {
		this.requestTime = requestTime;
	}

	@Override
	public String toString() {
		return "SmsResponse{" +
				"statusCode='" + statusCode + '\'' +
				", statusName='" + statusName + '\'' +
				", requestId='" + requestId + '\'' +
				", requestTime='" + requestTime + '\'' +
				'}';
	}
}
