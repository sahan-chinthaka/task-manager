/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

/**
 *
 * @author len
 */
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class TM extends JFrame {
    public TM() {
       
        setTitle("Task Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 200);
        setLocationRelativeTo(null);

        JLabel usernameLabel = new JLabel("Username:");
        JLabel passwordLabel = new JLabel("Password:");

        JTextField usernameField = new JTextField(15);
        JPasswordField passwordField = new JPasswordField(15);

        JButton Login_Button = new JButton("Login");

        Login_Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                if (login(username, password)) {
                    openTaskManager(username);
                } else {
                    JOptionPane.showMessageDialog(TM.this, "Invalid username or password. Please try again.");
                }
            }
        });

        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new GridLayout(4, 2));
        loginPanel.add(usernameLabel);
        loginPanel.add(usernameField);
        loginPanel.add(passwordLabel);
        loginPanel.add(passwordField);
        loginPanel.add(new JLabel());
        loginPanel.add(Login_Button);
        loginPanel.add(new JLabel());

        setLayout(new BorderLayout());
        add(loginPanel, BorderLayout.CENTER);
    }

    private boolean login(String username, String password) 
    {
        // Assert that login parameters are not null
        assert username != null : "Username cannot be null";
        assert password != null : "Password cannot be null";

        return username.equals("admin") && password.equals("admin");
    }

    private void openTaskManager(String username) 
    {
        // Assert that username is valid before opening task manager
        assert username != null && !username.trim().isEmpty() : "Username must not be null or empty";

        TaskManagerFrame TM = new TaskManagerFrame(username);
        TM.setVisible(true);
        dispose();
    }

    private class TaskManagerFrame extends JFrame {
        private ArrayList<Task> tasks;
        private JTable taskTable;
        private DefaultTableModel tableModel;
        private String username;

        public TaskManagerFrame(String username) {
            // Assert constructor parameters
            assert username != null && !username.trim().isEmpty() : "Username must not be null or empty";

            this.username = username;

            setTitle("Task Manager");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setSize(500, 400);
            setLocationRelativeTo(null);

            tasks = new ArrayList<>();

            tableModel = new DefaultTableModel(new Object[]{"Title", "Description", "Priority", "Due Date"}, 0);
            taskTable = new JTable(tableModel);
            
        JButton addButton = new JButton("Add");
        JButton editButton = new JButton("Edit");
        JButton deleteButton = new JButton("Delete");

        addButton.setIcon(resizeIcon(new ImageIcon("add_icon.png"), 20, 20));
            editButton.setIcon(resizeIcon(new ImageIcon("edit_icon.png"), 20, 20));
            deleteButton.setIcon(resizeIcon(new ImageIcon("delete_icon.png"), 20, 20));

    
            addButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    addTask();
                }
            });

            editButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    editTask();
                }
            });

            deleteButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    deleteTask();
                }
            });

            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new FlowLayout());
            buttonPanel.add(addButton);
            buttonPanel.add(editButton);
            buttonPanel.add(deleteButton);

            JScrollPane scrollPane = new JScrollPane(taskTable);

            setLayout(new BorderLayout());
            add(scrollPane, BorderLayout.CENTER);
            add(buttonPanel, BorderLayout.SOUTH);

            loadTasksFromFile();

            // Assert that tasks list is initialized after loading
            assert tasks != null : "Tasks list must be initialized";
        }

        private void loadTasksFromFile() {
            try (BufferedReader reader = new BufferedReader(new FileReader(username + ".txt"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] taskData = line.split(",");

                    // Assert that we have enough data fields for a complete task
                    assert taskData.length >= 4 : "Task data must contain at least 4 fields: title, description, priority, dueDate";

                    String title = taskData[0];
                    String description = taskData[1];
                    String priority = taskData[2];
                    String dueDate = taskData[3];

                    // Assert that critical fields are not empty
                    assert title != null && !title.trim().isEmpty() : "Task title cannot be null or empty";
                    assert priority != null && (priority.equals("high") || priority.equals("medium") || priority.equals("low")) :
                        "Priority must be 'high', 'medium', or 'low'";
                    assert isValidDateFormat(dueDate) : "Due date must be in valid yyyy-MM-dd format";

                    tasks.add(new Task(title, description, priority, dueDate));
                }
            } catch (IOException e) {
            }

            tasks.sort(new Comparator<Task>() {
    @Override
    public int compare(Task task1, Task task2) {
        
        int priority1 = getPriorityValue(task1.priority);
        int priority2 = getPriorityValue(task2.priority);
        return Integer.compare(priority1, priority2);
    }

    private int getPriorityValue(String priority) {
        switch (priority) {
            case "high":
                return 0;
            case "medium":
                return 1;
            case "low":
                return 2;
            default:
                return 3;
        }
    }
});


            for (Task task : tasks) {
                tableModel.addRow(new Object[]{task.title, task.description, task.priority, task.dueDate});
            }
        }

        private void addTask() {
        String title = JOptionPane.showInputDialog(TaskManagerFrame.this, "Enter the task title:");
        String description = JOptionPane.showInputDialog(TaskManagerFrame.this, "Enter the task description:");

        String[] priorityOptions = {"high", "medium", "low"};
        String priority = (String) JOptionPane.showInputDialog(
                TaskManagerFrame.this,
                "Select the task priority:",
                "Priority",
                JOptionPane.PLAIN_MESSAGE,
                null,
                priorityOptions,
                "medium"
        );

        // Assert that user didn't cancel the dialogs
        if (title == null || description == null || priority == null) {
            return; // User cancelled
        }

        // Assert that required fields are not empty
        assert !title.trim().isEmpty() : "Task title cannot be empty";
        assert priority.equals("high") || priority.equals("medium") || priority.equals("low") :
            "Priority must be 'high', 'medium', or 'low'";

        String dueDate;
        while (true) {
            dueDate = JOptionPane.showInputDialog(TaskManagerFrame.this, "Enter the task due date (yyyy-mm-dd):");
            if (dueDate == null) {
                return; // User cancelled
            }
            if (isValidDateFormat(dueDate))
            {
                break;
            } else {
                JOptionPane.showMessageDialog(TaskManagerFrame.this, "Invalid date format. Please enter the date in yyyy-mm-dd format.");
            }
    }

    // Assert final validation before creating task
    assert isValidDateFormat(dueDate) : "Due date validation failed";

    Task task = new Task(title, description, priority, dueDate);
    tasks.add(task);
    tableModel.addRow(new Object[]{task.title, task.description, task.priority, task.dueDate});

    saveTasksToFile();
}

        private boolean isValidDateFormat(String date) {
    // Assert input is not null
    assert date != null : "Date string cannot be null";

    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    dateFormat.setLenient(false);
    try {
        dateFormat.parse(date);
        return true;
    } catch (ParseException e) {
        return false;
    }
}

        private void editTask() {
    int selectedRow = taskTable.getSelectedRow();

    // Assert that a row is selected
    assert selectedRow >= 0 : "No row selected for editing";
    assert selectedRow < tasks.size() : "Selected row index is out of bounds";

    if (selectedRow != -1) {
        Task task = tasks.get(selectedRow);

        // Assert that the task exists
        assert task != null : "Selected task cannot be null";

        String title = JOptionPane.showInputDialog(TaskManagerFrame.this, "Enter the new task title:", task.title);
        String description = JOptionPane.showInputDialog(TaskManagerFrame.this, "Enter the new task description:", task.description);

        String[] priorityOptions = {"high", "medium", "low"};
        String priority = (String) JOptionPane.showInputDialog(
                TaskManagerFrame.this,
                "Select the new task priority:",
                "Priority",
                JOptionPane.PLAIN_MESSAGE,
                null,
                priorityOptions,
                task.priority
        );

        // Check if user cancelled any dialog
        if (title == null || description == null || priority == null) {
            return;
        }

        // Assert valid inputs
        assert !title.trim().isEmpty() : "Task title cannot be empty";
        assert priority.equals("high") || priority.equals("medium") || priority.equals("low") :
            "Priority must be 'high', 'medium', or 'low'";

        String dueDate;
        while (true) {
            dueDate = JOptionPane.showInputDialog(TaskManagerFrame.this, "Enter the new task due date (yyyy-mm-dd):");
            if (dueDate == null) {
                return; // User cancelled
            }
            if (isValidDateFormat(dueDate)) {
                break;
            } else {
                JOptionPane.showMessageDialog(TaskManagerFrame.this, "Invalid date format. Please enter the date in yyyy-mm-dd format.");
            }
        }

        // Assert final validation
        assert isValidDateFormat(dueDate) : "Due date validation failed";

        task.title = title;
        task.description = description;
        task.priority = priority;
        task.dueDate = dueDate;

        tableModel.setValueAt(task.title, selectedRow, 0);
        tableModel.setValueAt(task.description, selectedRow, 1);
        tableModel.setValueAt(task.priority, selectedRow, 2);
        tableModel.setValueAt(task.dueDate, selectedRow, 3);

        saveTasksToFile();
    }
    }
       
        private void deleteTask() {
            int selectedRow = taskTable.getSelectedRow();

            // Assert that a valid row is selected
            assert selectedRow >= 0 : "No row selected for deletion";
            assert selectedRow < tasks.size() : "Selected row index is out of bounds";

            if (selectedRow != -1) {
                tasks.remove(selectedRow);
                tableModel.removeRow(selectedRow);

                // Assert that the task was actually removed
                assert !tasks.contains(tasks.get(Math.min(selectedRow, tasks.size() - 1))) || tasks.isEmpty() :
                    "Task should be removed from list";

                saveTasksToFile();
        }
    }

        private void saveTasksToFile() {
            // Assert that username is valid for file operations
            assert username != null && !username.trim().isEmpty() : "Username must be valid for file operations";

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(username + ".txt"))) {
                for (Task task : tasks) {
                    // Assert that each task has valid data before saving
                    assert task != null : "Task cannot be null when saving";
                    assert task.title != null && task.description != null && task.priority != null && task.dueDate != null :
                        "All task fields must be non-null when saving";

                    writer.write(task.title + "," + task.description + "," + task.priority + "," + task.dueDate);
                    writer.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(TaskManagerFrame.this, "Error occurred while saving tasks to file.");
            }
        }

        private class Task {
        private String title;
        private String description;
        private String priority;
        private String dueDate;

        public Task(String title, String description, String priority, String dueDate) {
            // Assert constructor parameters
            assert title != null && !title.trim().isEmpty() : "Task title cannot be null or empty";
            assert description != null : "Task description cannot be null";
            assert priority != null && (priority.equals("high") || priority.equals("medium") || priority.equals("low")) :
                "Priority must be 'high', 'medium', or 'low'";
            assert dueDate != null && isValidDateFormat(dueDate) : "Due date must be non-null and in valid format";

            this.title = title;
            this.description = description;
            this.priority = priority;
            this.dueDate = dueDate;
        }
    }

        private ImageIcon resizeIcon(ImageIcon icon, int width, int height) {
        // Assert parameters for image resizing
        assert icon != null : "ImageIcon cannot be null";
        assert width > 0 && height > 0 : "Width and height must be positive values";

        Image img = icon.getImage();
        Image resizedImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(resizedImg);
    }
    }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 320, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) 
    {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(TM.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TM.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TM.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TM.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() 
        {
            public void run() 
            {
                new TM().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
