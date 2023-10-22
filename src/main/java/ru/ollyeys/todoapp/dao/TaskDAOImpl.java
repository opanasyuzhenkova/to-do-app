package ru.ollyeys.todoapp.dao;

import jakarta.servlet.http.HttpSession;
import ru.ollyeys.todoapp.model.Login;
import ru.ollyeys.todoapp.model.Task;
import ru.ollyeys.todoapp.utils.JDBCUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TaskDAOImpl implements TaskDAO {

    private static final String INSERT_TASK_SQL = "insert into todos" +
            " (title, username, description, target_date, is_done) values " + " (?, ?, ?, ?, ?); ";

    private static final String SELECT_TASK_BY_ID = "select id, title, username, description, " +
            "target_date, is_done from todos where id =?";
    private static final String SELECT_ALL_TASKS = "select * from todos where username = ?;";
    private static final String DELETE_TASK_BY_ID = "delete from todos where id = ?;";
    private static final String UPDATE_TASK = "update todos set title = ?, username = ?, description = ?, " +
            "target_date = ?, is_done = ? where id = ?;";

    public TaskDAOImpl() {
    }

    @Override
    public void insertTask(Task task) throws SQLException {
        System.out.println(INSERT_TASK_SQL);
        try (Connection connection = JDBCUtils.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_TASK_SQL)) {
            preparedStatement.setString(1, task.getTitle());
            preparedStatement.setString(2, task.getUsername());
            preparedStatement.setString(3, task.getDescription());
            preparedStatement.setDate(4, JDBCUtils.getSQLDate(task.getTargetDate()));
            preparedStatement.setBoolean(5, task.getStatus());
            System.out.println(preparedStatement);
            preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            JDBCUtils.printSQLException(exception);
        }
    }

    @Override
    public Task selectTask(long taskId) {
        Task task = null;

        try (Connection connection = JDBCUtils.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_TASK_BY_ID);) {
            preparedStatement.setLong(1, taskId);
            System.out.println(preparedStatement);
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                long id = rs.getLong("id");
                String title = rs.getString("title");
                String username = rs.getString("username");
                String description = rs.getString("description");
                LocalDate targetDate = rs.getDate("target_date").toLocalDate();
                boolean isDone = rs.getBoolean("is_done");
                task = new Task(id, title, username, description, targetDate, isDone);
            }
        } catch (SQLException exception) {
            JDBCUtils.printSQLException(exception);
        }
        return task;
    }

    @Override
    public List<Task> selectAllTasks(String username) {


        List<Task> tasks = new ArrayList<>();


        try (Connection connection = JDBCUtils.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_TASKS);) {
            preparedStatement.setString(1, username);
            System.out.println(preparedStatement);

            ResultSet rs = preparedStatement.executeQuery();


            // Step 4: Process the ResultSet object.
            while (rs.next()) {
                long id = rs.getLong("id");
                String title = rs.getString("title");
//                String username = rs.getString("username");
                String description = rs.getString("description");
                LocalDate targetDate = rs.getDate("target_date").toLocalDate();
                boolean isDone = rs.getBoolean("is_done");
                tasks.add(new Task(id, title, username, description, targetDate, isDone));
            }
        } catch (SQLException exception) {
            JDBCUtils.printSQLException(exception);
        }
        System.out.println(tasks);
        return tasks;
    }

    @Override
    public boolean deleteTask(int id) throws SQLException {
        boolean taskDeleted;
        try (Connection connection = JDBCUtils.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_TASK_BY_ID);) {
            preparedStatement.setInt(1, id);
            taskDeleted = preparedStatement.executeUpdate() > 0;
        }
        return taskDeleted;
    }

    @Override
    public boolean updateTask(Task task) throws SQLException {
        boolean taskUpdated;
        try (Connection connection = JDBCUtils.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_TASK);) {
            preparedStatement.setString(1, task.getTitle());
            preparedStatement.setString(2, task.getUsername());
            preparedStatement.setString(3, task.getDescription());
            preparedStatement.setDate(4, JDBCUtils.getSQLDate(task.getTargetDate()));
            preparedStatement.setBoolean(5, task.getStatus());
            preparedStatement.setLong(6, task.getId());
            taskUpdated = preparedStatement.executeUpdate() > 0;
        }
        return taskUpdated;
    }
}
