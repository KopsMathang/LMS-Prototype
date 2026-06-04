package com.example.model;

public class Employee {
    private int id;
    private String name;
    private String department;
    private double annualLeaveBalance;
    private double sickLeaveBalance;
    private String email;  // NEW: email address for notifications

    public Employee(int id, String name, String department, double annualBalance, double sickBalance, String email) {
        this.id = id;
        this.name = name;
        this.department = department;
        this.annualLeaveBalance = annualBalance;
        this.sickLeaveBalance = sickBalance;
        this.email = email;  // NEW
    }

    // Getters and setters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getDepartment() { return department; }
    public double getAnnualLeaveBalance() { return annualLeaveBalance; }
    public void setAnnualLeaveBalance(double annualLeaveBalance) { this.annualLeaveBalance = annualLeaveBalance; }
    public double getSickLeaveBalance() { return sickLeaveBalance; }
    public void setSickLeaveBalance(double sickLeaveBalance) { this.sickLeaveBalance = sickLeaveBalance; }
    public String getEmail() { return email; }  // NEW
    public void setEmail(String email) { this.email = email; }  // NEW
}