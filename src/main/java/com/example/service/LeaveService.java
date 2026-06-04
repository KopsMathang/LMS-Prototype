package com.example.service;

import com.example.model.Employee;
import com.example.model.LeaveRequest;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LeaveService {
    private Map<Integer, Employee> employees = new HashMap<>();
    private Map<Integer, LeaveRequest> leaveRequests = new HashMap<>();
    private int nextRequestId = 100;

    public LeaveService() {
        // Mock data – added email addresses
        employees.put(1, new Employee(1, "Alice Johnson", "IT", 12.0, 8.0, "alice@example.com"));
        employees.put(2, new Employee(2, "Bob Smith", "IT", 10.0, 5.0, "bob@example.com"));
        employees.put(3, new Employee(3, "Charlie Brown", "HR", 15.0, 10.0, "charlie@example.com"));
        employees.put(10, new Employee(10, "Manager Lee", "IT", 0, 0, "manager@example.com"));
    }

    public double getLeaveBalance(int employeeId, String leaveType) {
        Employee emp = employees.get(employeeId);
        if (emp == null) return 0;
        if ("ANNUAL".equalsIgnoreCase(leaveType)) return emp.getAnnualLeaveBalance();
        if ("SICK".equalsIgnoreCase(leaveType)) return emp.getSickLeaveBalance();
        return 0;
    }

    // PATCH: Prevent overlapping pending leave requests
    private boolean hasOverlappingPendingRequest(int employeeId, LocalDate start, LocalDate end) {
        for (LeaveRequest req : leaveRequests.values()) {
            if (req.getEmployeeId() == employeeId && "PENDING".equals(req.getStatus())) {
                // Check if date ranges overlap
                if (!(end.isBefore(req.getStartDate()) || start.isAfter(req.getEndDate()))) {
                    return true;
                }
            }
        }
        return false;
    }

    public LeaveRequest submitLeaveRequest(int employeeId, String leaveType, LocalDate startDate,
                                           LocalDate endDate, String reason) throws IllegalArgumentException {
        // PATCH: Check for overlapping pending request
        if (hasOverlappingPendingRequest(employeeId, startDate, endDate)) {
            throw new IllegalArgumentException("You already have a pending leave request that overlaps with these dates.");
        }

        double requiredDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        double balance = getLeaveBalance(employeeId, leaveType);
        if (requiredDays > balance) {
            throw new IllegalArgumentException("Insufficient leave balance. Required: " + requiredDays +
                    ", Available: " + balance);
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must be before end date");
        }
        if ("SICK".equalsIgnoreCase(leaveType) && (reason == null || reason.trim().isEmpty())) {
            throw new IllegalArgumentException("Reason required for sick leave");
        }

        LeaveRequest request = new LeaveRequest(nextRequestId++, employeeId, leaveType, startDate, endDate, reason);
        leaveRequests.put(request.getRequestId(), request);
        return request;
    }

    public List<LeaveRequest> getPendingRequestsForManager(int managerId) {
        Employee manager = employees.get(managerId);
        if (manager == null) return new ArrayList<>();
        String dept = manager.getDepartment();
        List<LeaveRequest> pending = new ArrayList<>();
        for (LeaveRequest req : leaveRequests.values()) {
            if (!"PENDING".equals(req.getStatus())) continue;
            Employee emp = employees.get(req.getEmployeeId());
            if (emp != null && dept.equals(emp.getDepartment())) {
                pending.add(req);
            }
        }
        return pending;
    }

    public void approveRequest(int requestId, int managerId, String comments) throws Exception {
        LeaveRequest req = leaveRequests.get(requestId);
        if (req == null) throw new Exception("Request not found");
        if (!"PENDING".equals(req.getStatus())) throw new Exception("Request already processed (status: " + req.getStatus() + ")");
        Employee manager = employees.get(managerId);
        Employee emp = employees.get(req.getEmployeeId());
        if (manager == null || emp == null || !manager.getDepartment().equals(emp.getDepartment())) {
            throw new Exception("Manager not authorised for this employee");
        }
        // Deduct balance
        double days = ChronoUnit.DAYS.between(req.getStartDate(), req.getEndDate()) + 1;
        if ("ANNUAL".equalsIgnoreCase(req.getLeaveType())) {
            emp.setAnnualLeaveBalance(emp.getAnnualLeaveBalance() - days);
        } else if ("SICK".equalsIgnoreCase(req.getLeaveType())) {
            emp.setSickLeaveBalance(emp.getSickLeaveBalance() - days);
        }
        req.setStatus("APPROVED");
        req.setApprovedBy(managerId);
        req.setComments(comments);

        // NEW FEATURE: Send email notification
        String subject = "Leave Request Approved";
        String body = "Dear " + emp.getName() + ",\n\nYour leave request from " + req.getStartDate() + " to " + req.getEndDate() + " has been APPROVED.\n\nComments: " + comments;
        EmailService.sendNotification(emp.getEmail(), subject, body);
    }

    public void rejectRequest(int requestId, int managerId, String comments) throws Exception {
        LeaveRequest req = leaveRequests.get(requestId);
        if (req == null) throw new Exception("Request not found");
        if (!"PENDING".equals(req.getStatus())) throw new Exception("Request already processed");
        req.setStatus("REJECTED");
        req.setApprovedBy(managerId);
        req.setComments(comments);

        // NEW FEATURE: Send email notification
        Employee emp = employees.get(req.getEmployeeId());
        String subject = "Leave Request Rejected";
        String body = "Dear " + emp.getName() + ",\n\nYour leave request from " + req.getStartDate() + " to " + req.getEndDate() + " has been REJECTED.\n\nComments: " + comments;
        EmailService.sendNotification(emp.getEmail(), subject, body);
    }

    public List<LeaveRequest> getRequestsForEmployee(int employeeId) {
        List<LeaveRequest> list = new ArrayList<>();
        for (LeaveRequest req : leaveRequests.values()) {
            if (req.getEmployeeId() == employeeId) list.add(req);
        }
        return list;
    }
}