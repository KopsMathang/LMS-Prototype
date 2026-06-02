package com.example.model;



import java.time.LocalDate;

public class LeaveRequest {
    private int requestId;
    private int employeeId;
    private String leaveType;   // "ANNUAL", "SICK"
    private LocalDate startDate;
    private LocalDate endDate;
    private String reason;
    private String status;      // "PENDING", "APPROVED", "REJECTED"
    private int approvedBy;     // managerId
    private String comments;

    public LeaveRequest(int requestId, int employeeId, String leaveType, LocalDate startDate,
                        LocalDate endDate, String reason) {
        this.requestId = requestId;
        this.employeeId = employeeId;
        this.leaveType = leaveType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reason = reason;
        this.status = "PENDING";
    }

    // Getters and setters
    public int getRequestId() { return requestId; }
    public int getEmployeeId() { return employeeId; }
    public String getLeaveType() { return leaveType; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public String getReason() { return reason; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public void setApprovedBy(int approvedBy) { this.approvedBy = approvedBy; }
    public void setComments(String comments) { this.comments = comments; }
    public int getApprovedBy() { return approvedBy; }
    public String getComments() { return comments; }
}