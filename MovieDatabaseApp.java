import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.File;
import javax.swing.border.Border;

public class MovieDatabaseApp {
    JFrame frame = new JFrame();
    private JTextField titleField, genreField, yearField, searchField;
    private DefaultTableModel tableModel;
    private Connection connection;

    JPanel inputPanel = new JPanel(new GridBagLayout()) {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            try {
                Image backgroundImage = ImageIO.read(new File("movie.jpg"));
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Failed to load background image.");
            }
        }
    };

    public MovieDatabaseApp() {
        initializeDatabaseConnection();
        initializeUI();
    }

    private void initializeDatabaseConnection() {
        try {
            String url = "jdbc:mysql://localhost:3306/MovieDB";
            String user = "root";
            String password = "12345678910";
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database connection failed: " + e.getMessage());
            System.exit(1);
        }
    }

    private void initializeUI() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setIconImage(new ImageIcon("movies.png").getImage());
        frame.setSize(800, 500);

        JLabel header = new JLabel("Movie Database", JLabel.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 28));
        header.setOpaque(true);
        header.setBackground(new Color(40, 40, 40));
        header.setForeground(Color.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        frame.add(header, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(50, 50, 50));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        frame.add(mainPanel, BorderLayout.CENTER);

        inputPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.WHITE), "Add a New Movie", 0, 0, new Font("Arial", Font.BOLD, 14), Color.WHITE));
        inputPanel.setBackground(new Color(40, 40, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTH;

        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(createLabel("Title:"), gbc);

        gbc.gridx = 1;
        titleField = createRoundedTextField();
        inputPanel.add(titleField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(createLabel("Genre:"), gbc);

        gbc.gridx = 1;
        genreField = createRoundedTextField();
        inputPanel.add(genreField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        inputPanel.add(createLabel("Year:"), gbc);

        gbc.gridx = 1;
        yearField = createRoundedTextField();
        inputPanel.add(yearField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton addButton = createRoundedButton("Add Movie", new Color(34, 139, 34));
        addButton.addActionListener(e -> addMovie());
        inputPanel.add(addButton, gbc);

        mainPanel.add(inputPanel, BorderLayout.WEST);

        JPanel searchPanel = new JPanel(new BorderLayout(10, 10));
        searchPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.WHITE), "Search Movies", 0, 0, new Font("Arial", Font.BOLD, 14), Color.WHITE));
        searchPanel.setBackground(new Color(40, 40, 40));

        searchField = createRoundedTextField();
        JButton searchButton = createRoundedButton("Search", new Color(30, 144, 255));
        searchButton.addActionListener(e -> searchMovies());

        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);
        mainPanel.add(searchPanel, BorderLayout.SOUTH);

        JTable movieTable = new JTable();
        tableModel = new DefaultTableModel(new String[]{"ID", "Title", "Genre", "Year"}, 0);
        movieTable.setModel(tableModel);
        movieTable.setFillsViewportHeight(true);
        movieTable.setRowHeight(25);
        movieTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        movieTable.getTableHeader().setBackground(new Color(70, 130, 180));
        movieTable.getTableHeader().setForeground(Color.WHITE);
        movieTable.setFont(new Font("Arial", Font.PLAIN, 12));
        movieTable.setBackground(new Color(30, 30, 30));
        movieTable.setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(movieTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        refreshMovieTable();
        frame.setVisible(true);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        return label;
    }
    private JTextField createRoundedTextField() {
        JTextField textField = new JTextField(20) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2d.setColor(Color.GRAY);
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);

                g2d.dispose();

                super.paintComponent(g);
            }

            @Override
            public void setBorder(Border border) {

            }
        };

        textField.setOpaque(false);
        textField.setBackground(Color.WHITE);
        textField.setForeground(Color.BLACK);
        textField.setCaretColor(Color.BLACK);
        textField.setFont(new Font("Arial", Font.PLAIN, 16));
        textField.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        Dimension preferredSize = new Dimension(40, 30);
        textField.setPreferredSize(preferredSize);
        textField.setMinimumSize(preferredSize);
        textField.setMaximumSize(preferredSize);

        return textField;
    }


    private JButton createRoundedButton(String text, Color color) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setColor(color);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2d.dispose();

                super.paintComponent(g);
            }
        };
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        button.setHorizontalAlignment(SwingConstants.CENTER);
        return button;
    }
    //add movies
    private void addMovie() {
        String title = titleField.getText().trim();
        String genre = genreField.getText().trim();
        String yearText = yearField.getText().trim();

        if (title.isEmpty() || genre.isEmpty() || yearText.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int year = Integer.parseInt(yearText);
            String query = "INSERT INTO movies (title, genre, year) VALUES (?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, title);
            statement.setString(2, genre);
            statement.setInt(3, year);
            statement.executeUpdate();

            JOptionPane.showMessageDialog(frame, "Movie added successfully!");
            titleField.setText("");
            genreField.setText("");
            yearField.setText("");

            refreshMovieTable();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Year must be a valid number!", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Failed to add movie: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    //search
    private void searchMovies() {
        String queryText = searchField.getText().trim().toLowerCase();

        if (queryText.isEmpty()) {
            refreshMovieTable();
            return;
        }

        try {
            String query = "SELECT * FROM movies WHERE LOWER(title) LIKE ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, "%" + queryText + "%");
            ResultSet resultSet = statement.executeQuery();

            tableModel.setRowCount(0);
            while (resultSet.next()) {
                tableModel.addRow(new Object[]{
                        resultSet.getInt("id"),
                        resultSet.getString("title"),
                        resultSet.getString("genre"),
                        resultSet.getInt("year")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Search failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    //refresh table after adding movies
    private void refreshMovieTable() {
        try {
            String query = "SELECT * FROM movies";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            tableModel.setRowCount(0);
            while (resultSet.next()) {
                tableModel.addRow(new Object[]{
                        resultSet.getInt("id"),
                        resultSet.getString("title"),
                        resultSet.getString("genre"),
                        resultSet.getInt("year")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Failed to load movies: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MovieDatabaseApp::new);
    }
}
