package com.example.service;

import com.example.model.LeaveRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class LeaveServiceTest {
    private LeaveService service;

    @BeforeEach
    void setUp() {
        service = new LeaveService();
    }

    @Test
    void testGetLeaveBalance_Annual() {
        double balance = service.getLeaveBalance(1, "ANNUAL");
        assertEquals(12.0, balance);
    }

    @Test
    void testGetLeaveBalance_Sick() {
        double balance = service.getLeaveBalance(1, "SICK");
        assertEquals(8.0, balance);
    }

    @Test
    void testGetLeaveBalance_InvalidEmployee() {
        double balance = service.getLeaveBalance(99, "ANNUAL");
        assertEquals(0.0, balance);
    }

    @Test
    void testSubmitLeaveRequest_Valid() {
        LeaveRequest req = service.submitLeaveRequest(1, "ANNUAL",
                LocalDate.of(2026, 6, 10),
                LocalDate.of(2026, 6, 12),
                "Vacation");
        assertNotNull(req);
        assertEquals("PENDING", req.getStatus());
        assertEquals(100, req.getRequestId());
    }

    @Test
    void testSubmitLeaveRequest_InsufficientBalance() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                service.submitLeaveRequest(1, "ANNUAL",
                        LocalDate.of(2026, 6, 10),
                        LocalDate.of(2026, 7, 10),
                        "Long leave")
        );
        assertTrue(exception.getMessage().contains("Insufficient leave balance"));
    }

    @Test
    void testSubmitLeaveRequest_StartAfterEnd() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                service.submitLeaveRequest(1, "ANNUAL",
                        LocalDate.of(2026, 6, 12),
                        LocalDate.of(2026, 6, 10),
                        "Invalid dates")
        );
        assertTrue(exception.getMessage().contains("Start date must be before end date"));
    }

    @Test
    void testSubmitLeaveRequest_SickNoReason() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                service.submitLeaveRequest(1, "SICK",
                        LocalDate.of(2026, 6, 10),
                        LocalDate.of(2026, 6, 12),
                        null)
        );
        assertTrue(exception.getMessage().contains("Reason required for sick leave"));
    }

    @Test
    void testApproveRequest_Valid() throws Exception {
        LeaveRequest req = service.submitLeaveRequest(1, "ANNUAL",
                LocalDate.of(2026, 6, 10),
                LocalDate.of(2026, 6, 12),
                "Vacation");
        int reqId = req.getRequestId();

        service.approveRequest(reqId, 10, "Approved");

        assertEquals("APPROVED", service.getRequestsForEmployee(1).get(0).getStatus());
        double newBalance = service.getLeaveBalance(1, "ANNUAL");
        assertEquals(9.0, newBalance);
    }

    @Test
    void testApproveRequest_UnauthorisedManager() throws Exception {
        LeaveRequest req = service.submitLeaveRequest(3, "ANNUAL",
                LocalDate.of(2026, 6, 10),
                LocalDate.of(2026, 6, 12),
                "HR leave");
        Exception exception = assertThrows(Exception.class, () ->
                service.approveRequest(req.getRequestId(), 10, "Wrong manager")
        );
        assertTrue(exception.getMessage().contains("not authorised"));
    }

    @Test
    void testRejectRequest_Valid() throws Exception {
        LeaveRequest req = service.submitLeaveRequest(1, "ANNUAL",
                LocalDate.of(2026, 6, 10),
                LocalDate.of(2026, 6, 12),
                "Vacation");
        service.rejectRequest(req.getRequestId(), 10, "Not approved");
        assertEquals("REJECTED", service.getRequestsForEmployee(1).get(0).getStatus());
        double balance = service.getLeaveBalance(1, "ANNUAL");
        assertEquals(12.0, balance);
    }
}