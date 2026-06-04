package com.example.controller;



import com.example.model.LeaveRequest;
import com.example.service.LeaveService;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class LMSEmployeeUI {
    private static LeaveService service = new LeaveService();
    private static Scanner scanner = new Scanner(System.in);
    private static int currentEmployeeId = 1;  // simulate logged-in employee (Alice)
    private static int currentManagerId = 10;  // Manager Lee

    public static void main(String[] args) {
        System.out.println("===== Leave Management System Prototype =====");
        while (true) {
            System.out.println("\n1. Employee Menu");
            System.out.println("2. Manager Menu");
            System.out.println("0. Exit");
            System.out.print("Choose: ");
            int choice = scanner.nextInt();
            scanner.nextLine();
            switch (choice) {
                case 1 -> employeeMenu();
                case 2 -> managerMenu();
                case 0 -> {
                    System.out.println("Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid choice");
            }
        }
    }

    private static void employeeMenu() {
        System.out.println("\n--- Employee Dashboard (ID: " + currentEmployeeId + ") ---");
        System.out.println("1. View Leave Balance");
        System.out.println("2. Apply for Leave");
        System.out.println("3. View My Requests");
        System.out.println("4. Back");
        System.out.print("Choose: ");
        int opt = scanner.nextInt();
        scanner.nextLine();

        switch (opt) {
            case 1 -> {
                double annual = service.getLeaveBalance(currentEmployeeId, "ANNUAL");
                double sick = service.getLeaveBalance(currentEmployeeId, "SICK");
                System.out.println("Annual leave balance: " + annual);
                System.out.println("Sick leave balance: " + sick);
            }
            case 2 -> applyForLeave();
            case 3 -> viewMyRequests();
            default -> System.out.println("Back to main");
        }
    }

    private static void applyForLeave() {
        System.out.print("Leave type (ANNUAL/SICK): ");
        String type = scanner.nextLine().toUpperCase();
        System.out.print("Start date (YYYY-MM-DD): ");
        String startStr = scanner.nextLine();
        System.out.print("End date (YYYY-MM-DD): ");
        String endStr = scanner.nextLine();
        System.out.print("Reason: ");
        String reason = scanner.nextLine();

        try {
            LocalDate start = LocalDate.parse(startStr, DateTimeFormatter.ISO_LOCAL_DATE);
            LocalDate end = LocalDate.parse(endStr, DateTimeFormatter.ISO_LOCAL_DATE);
            LeaveRequest req = service.submitLeaveRequest(currentEmployeeId, type, start, end, reason);
            System.out.println("Leave request submitted successfully. Request ID: " + req.getRequestId());
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static void viewMyRequests() {
        List<LeaveRequest> requests = service.getRequestsForEmployee(currentEmployeeId);
        if (requests.isEmpty()) {
            System.out.println("No leave requests found.");
        } else {
            System.out.println("ID\tType\tStart\tEnd\tStatus");
            for (LeaveRequest r : requests) {
                System.out.printf("%d\t%s\t%s\t%s\t%s%n", r.getRequestId(), r.getLeaveType(),
                        r.getStartDate(), r.getEndDate(), r.getStatus());
            }
        }
    }

    private static void managerMenu() {
        System.out.println("\n--- Manager Dashboard (ID: " + currentManagerId + ") ---");
        System.out.println("1. View Pending Requests");
        System.out.println("2. Approve/Reject Request");
        System.out.println("3. Back");
        System.out.print("Choose: ");
        int opt = scanner.nextInt();
        scanner.nextLine();

        if (opt == 1) {
            List<LeaveRequest> pending = service.getPendingRequestsForManager(currentManagerId);
            if (pending.isEmpty()) {
                System.out.println("No pending requests.");
            } else {
                System.out.println("ID\tEmpID\tType\tStart\tEnd\tReason");
                for (LeaveRequest r : pending) {
                    System.out.printf("%d\t%d\t%s\t%s\t%s\t%s%n", r.getRequestId(), r.getEmployeeId(),
                            r.getLeaveType(), r.getStartDate(), r.getEndDate(), r.getReason());
                }
            }
        } else if (opt == 2) {
            System.out.print("Enter Request ID: ");
            int reqId = scanner.nextInt();
            scanner.nextLine();
            System.out.print("Action (APPROVE/REJECT): ");
            String action = scanner.nextLine().toUpperCase();
            System.out.print("Comments (optional): ");
            String comments = scanner.nextLine();
            try {
                if ("APPROVE".equals(action)) {
                    service.approveRequest(reqId, currentManagerId, comments);
                    System.out.println("Request approved.");
                } else if ("REJECT".equals(action)) {
                    service.rejectRequest(reqId, currentManagerId, comments);
                    System.out.println("Request rejected.");
                } else {
                    System.out.println("Invalid action.");
                }
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
    }
}
