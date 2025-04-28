package com.example.tutorial2;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class HelloApplication extends Application {

    private Map<String, String> users = new HashMap<>(); // Map for users
    private Map<String, String> userRoles = new HashMap<>(); // Map for user roles
    private String currentRole = "";

    private List<Vehicle> vehicles = new ArrayList<>();
    private List<Customer> customers = new ArrayList<>();
    private List<Booking> bookings = new ArrayList<>();

    @Override
    public void start(Stage stage) {
        showLoginScreen(stage);
    }

    private void showLoginScreen(Stage stage) {
        Label title = new Label("Vehicle Rental Login");
        title.setFont(Font.font("Segoe UI Semibold", 24));
        title.setTextFill(Color.web("#2C3E50"));

        TextField username = new TextField();
        username.setPromptText("Username");
        PasswordField password = new PasswordField();
        password.setPromptText("Password");

        Button login = styledButton("Login");
        Button register = styledButton("Register");

        VBox layout = new VBox(15, title, username, password, login, register);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(30));
        layout.setStyle("-fx-background-color: linear-gradient(to right, #bdc3c7, #2c3e50);");

        login.setOnAction(e -> {
            String u = username.getText();
            String p = password.getText();
            if (users.containsKey(u) && users.get(u).equals(p)) {
                currentRole = userRoles.get(u); // Fetch user role from userRoles map
                showDashboard(stage);
            } else {
                showAlert("Login Failed", "Invalid credentials!");
            }
        });

        register.setOnAction(e -> showRegistrationScreen(stage));

        Scene scene = new Scene(layout, 400, 300);
        stage.setScene(scene);
        stage.setTitle("Login");
        stage.show();
    }

    private void showRegistrationScreen(Stage stage) {
        Label title = new Label("Register");
        title.setFont(Font.font("Segoe UI Semibold", 24));
        title.setTextFill(Color.web("#2C3E50"));

        TextField username = new TextField();
        username.setPromptText("Username");
        PasswordField password = new PasswordField();
        password.setPromptText("Password");

        ComboBox<String> roleComboBox = new ComboBox<>();
        roleComboBox.getItems().addAll("Admin", "Employee");
        roleComboBox.setPromptText("Select Role");

        Button register = styledButton("Register");
        Button back = styledButton("Back");

        VBox layout = new VBox(15, title, username, password, roleComboBox, register, back);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(30));
        layout.setStyle("-fx-background-color: linear-gradient(to right, #bdc3c7, #2c3e50);");

        register.setOnAction(e -> {
            String u = username.getText();
            String p = password.getText();
            String selectedRole = roleComboBox.getValue();

            // Check if all fields are filled
            if (!u.isEmpty() && !p.isEmpty() && selectedRole != null) {
                if (!users.containsKey(u)) { // Ensure no duplicate usernames
                    users.put(u, p); // Store username and password
                    userRoles.put(u, selectedRole); // Store user role
                    showAlert("Registration Successful", "You can now log in with your credentials.");
                    showLoginScreen(stage);  // Return to login screen after successful registration
                } else {
                    showAlert("Registration Failed", "Username already exists.");
                }
            } else {
                showAlert("Registration Failed", "Please fill in all fields.");
            }
        });

        back.setOnAction(e -> showLoginScreen(stage));

        stage.setScene(new Scene(layout, 400, 300));
    }

    private void showDashboard(Stage stage) {
        Label heading = new Label("Dashboard - " + currentRole);
        heading.setFont(Font.font("Segoe UI", 22));
        heading.setTextFill(Color.web("#34495E"));

        Button manageVehicles = styledButton("🚗 Vehicle Management");
        Button manageCustomers = styledButton("👤 Customer Management");
        Button newBooking = styledButton("📝 New Booking");
        Button viewReports = styledButton("📊 Reports");
        Button logout = styledButton("🚪 Logout");

        VBox layout = new VBox(20, heading, manageVehicles, manageCustomers, newBooking, viewReports, logout);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(30));
        layout.setStyle("-fx-background-color: linear-gradient(to top right, #ecf0f1, #bdc3c7);");

        // Restrict Employee actions
        if (currentRole.equals("Employee")) {
            manageVehicles.setDisable(true);
            manageCustomers.setDisable(true);
            viewReports.setDisable(true);
        }

        manageVehicles.setOnAction(e -> manageVehicles(stage));
        manageCustomers.setOnAction(e -> manageCustomers(stage));
        newBooking.setOnAction(e -> createBooking(stage));
        viewReports.setOnAction(e -> viewReports(stage));
        logout.setOnAction(e -> showLoginScreen(stage));

        stage.setScene(new Scene(layout, 500, 400));
    }

    private void manageVehicles(Stage stage) {
        VBox layout = styledPane("Vehicle Management");

        TextField brandField = new TextField(); brandField.setPromptText("Brand & Model");
        ComboBox<String> categoryBox = new ComboBox<>();
        categoryBox.getItems().addAll("Car", "Bike", "Van", "Truck");
        TextField priceField = new TextField(); priceField.setPromptText("Rental Price/Day");

        Button addBtn = styledButton("➕ Add Vehicle");
        TextArea display = styledArea();

        addBtn.setOnAction(e -> {
            try {
                vehicles.add(new Vehicle(brandField.getText(), categoryBox.getValue(), Double.parseDouble(priceField.getText())));
                brandField.clear(); categoryBox.setValue(null); priceField.clear();
                display.setText(getVehicleList());
            } catch (Exception ex) {
                showAlert("Error", "Invalid vehicle details!");
            }
        });

        Button back = styledButton("⬅ Back");
        back.setOnAction(e -> showDashboard(stage));

        layout.getChildren().addAll(brandField, categoryBox, priceField, addBtn, display, back);
        stage.setScene(new Scene(layout, 600, 500));
    }

    private void manageCustomers(Stage stage) {
        VBox layout = styledPane("Customer Management");

        TextField name = new TextField(); name.setPromptText("Full Name");
        TextField contact = new TextField(); contact.setPromptText("Contact");
        TextField license = new TextField(); license.setPromptText("License No");

        Button addBtn = styledButton("➕ Register Customer");
        TextArea display = styledArea();

        addBtn.setOnAction(e -> {
            customers.add(new Customer(name.getText(), contact.getText(), license.getText()));
            name.clear(); contact.clear(); license.clear();
            display.setText(getCustomerList());
        });

        Button back = styledButton("⬅ Back");
        back.setOnAction(e -> showDashboard(stage));

        layout.getChildren().addAll(name, contact, license, addBtn, display, back);
        stage.setScene(new Scene(layout, 600, 500));
    }

    private void createBooking(Stage stage) {
        VBox layout = styledPane("New Booking");

        ComboBox<Vehicle> vehicleBox = new ComboBox<>();
        vehicleBox.getItems().addAll(vehicles.stream().filter(Vehicle::isAvailable).toList());
        vehicleBox.setPromptText("Select Vehicle");

        ComboBox<Customer> customerBox = new ComboBox<>();
        customerBox.getItems().addAll(customers);
        customerBox.setPromptText("Select Customer");

        DatePicker startDate = new DatePicker();
        DatePicker endDate = new DatePicker();
        startDate.setPromptText("Start Date");
        endDate.setPromptText("End Date");

        Button bookBtn = styledButton("✅ Book Now");
        TextArea invoice = styledArea();

        bookBtn.setOnAction(e -> {
            Vehicle v = vehicleBox.getValue();
            Customer c = customerBox.getValue();
            LocalDate start = startDate.getValue();
            LocalDate end = endDate.getValue();

            if (v != null && c != null && start != null && end != null && !end.isBefore(start)) {
                long days = ChronoUnit.DAYS.between(start, end) + 1;
                double cost = days * v.getPrice();
                bookings.add(new Booking(v, c, start, end, cost));
                v.setAvailable(false);
                invoice.setText("Booking Confirmed!\nCustomer: " + c.getName() +
                        "\nVehicle: " + v.getBrandModel() + "\nDuration: " + days + " days\nTotal: $" + cost);
            } else {
                showAlert("Error", "Invalid booking info!");
            }
        });

        Button back = styledButton("⬅ Back");
        back.setOnAction(e -> showDashboard(stage));

        layout.getChildren().addAll(vehicleBox, customerBox, startDate, endDate, bookBtn, invoice, back);
        stage.setScene(new Scene(layout, 600, 550));
    }

    private void viewReports(Stage stage) {
        VBox layout = styledPane("Reports & Stats");

        double revenue = bookings.stream().mapToDouble(Booking::getCost).sum();
        long available = vehicles.stream().filter(Vehicle::isAvailable).count();

        PieChart pie = new PieChart();
        pie.getData().add(new PieChart.Data("Available", available));
        pie.getData().add(new PieChart.Data("Booked", vehicles.size() - available));

        TextArea report = styledArea();
        report.setText("Vehicles: " + vehicles.size() +
                "\nAvailable: " + available +
                "\nCustomers: " + customers.size() +
                "\nBookings: " + bookings.size() +
                "\nRevenue: $" + revenue);

        Button back = styledButton("⬅ Back");
        back.setOnAction(e -> showDashboard(stage));

        layout.getChildren().addAll(pie, report, back);
        stage.setScene(new Scene(layout, 600, 600));
    }

    // 🔧 Styled UI Components
    private Button styledButton(String text) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 14px;");
        btn.setPrefWidth(200);
        btn.setEffect(new DropShadow());
        return btn;
    }

    private VBox styledPane(String titleText) {
        Label title = new Label(titleText);
        title.setFont(Font.font("Segoe UI", 20));
        title.setTextFill(Color.web("#2c3e50"));
        VBox box = new VBox(12, title);
        box.setPadding(new Insets(20));
        box.setAlignment(Pos.TOP_CENTER);
        box.setStyle("-fx-background-color: linear-gradient(to top left, #ffffff, #ecf0f1);");
        return box;
    }

    private TextArea styledArea() {
        TextArea area = new TextArea();
        area.setPrefHeight(200);
        area.setEditable(false);
        area.setStyle("-fx-control-inner-background: #f8f9fa; -fx-font-family: monospace;");
        return area;
    }

    private void showAlert(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setContentText(msg);
        a.showAndWait();
    }

    private String getVehicleList() {
        StringBuilder sb = new StringBuilder();
        for (Vehicle v : vehicles) sb.append(v).append("\n");
        return sb.toString();
    }

    private String getCustomerList() {
        StringBuilder sb = new StringBuilder();
        for (Customer c : customers) sb.append(c).append("\n");
        return sb.toString();
    }

    // 🚗 Models
    class Vehicle {
        static int count = 1;
        int id;
        String brand, category;
        double price;
        boolean available = true;

        Vehicle(String brand, String category, double price) {
            this.id = count++;
            this.brand = brand;
            this.category = category;
            this.price = price;
        }

        public double getPrice() { return price; }
        public boolean isAvailable() { return available; }
        public void setAvailable(boolean available) { this.available = available; }
        public String getBrandModel() { return brand; }

        public String toString() {
            return "#" + id + " | " + brand + " | " + category + " | $" + price + " | " + (available ? "Available" : "Booked");
        }
    }

    class Customer {
        String name, contact, license;

        Customer(String name, String contact, String license) {
            this.name = name;
            this.contact = contact;
            this.license = license;
        }

        public String getName() { return name; }

        public String toString() {
            return name + " | " + contact + " | " + license;
        }
    }

    class Booking {
        Vehicle vehicle;
        Customer customer;
        LocalDate start, end;
        double cost;

        Booking(Vehicle v, Customer c, LocalDate s, LocalDate e, double cost) {
            this.vehicle = v;
            this.customer = c;
            this.start = s;
            this.end = e;
            this.cost = cost;
        }

        public double getCost() { return cost; }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
