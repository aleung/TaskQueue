package leoliang.taskqueue.repository;

import java.util.List;

public interface TaskRepository {

	void addTask(Task task);

	void updateTask(Task task);

	List<Task> queryHistoryTasks();

	List<Task> queryCheckoutTasks();

	List<Task> queryBacklogTasks();
}
