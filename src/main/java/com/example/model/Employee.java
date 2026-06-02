package com.example.model;

public class Employee {
    private int id;
    private String name;
    private String department;
    private double annualLeaveBalance;
    private double sickLeaveBalance;

    public Employee(int id, String name, String department, double annualBalance, double sickBalance) {
        this.id = id;
        this.name = name;
        this.department = department;
        this.annualLeaveBalance = annualBalance;
        this.sickLeaveBalance = sickBalance;
    }

    // Getters and setters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getDepartment() { return department; }
    public double getAnnualLeaveBalance() { return annualLeaveBalance; }
    public void setAnnualLeaveBalance(double annualLeaveBalance) { this.annualLeaveBalance = annualLeaveBalance; }
    public double getSickLeaveBalance() { return sickLeaveBalance; }
    public void setSickLeaveBalance(double sickLeaveBalance) { this.sickLeaveBalance = sickLeaveBalance; }
}
