package leoliang.taskqueue.repository;

public class Task {

	public enum Status {
		DELETED, DONE, CHECKOUT, BACKLOG
	}

	private long id;
	private String title;
	private Status status;
	private long planned;
	private long modified;
	private int order;

	void setId(long id) {
		this.id = id;
	}

	void setTitle(String title) {
		this.title = title;
	}

	void setStatus(Status status) {
		this.status = status;
	}

	void setPlanned(long planned) {
		this.planned = planned;
	}

	void setModified(long modified) {
		this.modified = modified;
	}

	void setOrder(int order) {
		this.order = order;
	}

	public long getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public Status getStatus() {
		return status;
	}

	public long getPlanned() {
		return planned;
	}

	public long getModified() {
		return modified;
	}

	public int getOrder() {
		return order;
	}

}
