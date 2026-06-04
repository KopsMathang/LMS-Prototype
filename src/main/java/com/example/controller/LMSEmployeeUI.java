package com.example.controller;

import com.example.model.LeaveRequest;
import com.example.service.LeaveService;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class LMSEmployeeUI {
    private static LeaveService service = new LeaveService();

    public static void main(String[] args) throws IOException {
        // Render sets the PORT environment variable
        String portEnv = System.getenv("PORT");
        int port = (portEnv != null) ? Integer.parseInt(portEnv) : 8080;

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", new RootHandler());
        server.createContext("/apply", new ApplyHandler());
        server.setExecutor(null);
        server.start();
        System.err.println("Server started on port " + port);
    }

    static class RootHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            StringBuilder response = new StringBuilder();
            response.append("<html><head><title>LMS Prototype</title></head><body>");
            response.append("<h1>Leave Management System</h1>");
            response.append("<h2>Employee Dashboard</h2>");

            double annual = service.getLeaveBalance(1, "ANNUAL");
            double sick = service.getLeaveBalance(1, "SICK");
            response.append("<p><b>Annual leave balance:</b> ").append(annual).append(" days</p>");
            response.append("<p><b>Sick leave balance:</b> ").append(sick).append(" days</p>");

            List<LeaveRequest> requests = service.getRequestsForEmployee(1);
            response.append("<h3>Recent Requests</h3><ul>");
            for (LeaveRequest req : requests) {
                response.append("<li>").append(req.getLeaveType()).append(" from ")
                        .append(req.getStartDate()).append(" to ").append(req.getEndDate())
                        .append(" – ").append(req.getStatus()).append("</li>");
            }
            response.append("</ul>");

            response.append("<h2>Apply for Leave</h2>");
            response.append("<form method='post' action='/apply'>");
            response.append("Leave type: <select name='type'><option>ANNUAL</option><option>SICK</option></select><br>");
            response.append("Start date: <input type='date' name='start'><br>");
            response.append("End date: <input type='date' name='end'><br>");
            response.append("Reason: <input type='text' name='reason'><br>");
            response.append("<input type='submit' value='Submit'>");
            response.append("</form>");
            response.append("</body></html>");

            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.toString().getBytes());
            os.close();
        }
    }

    static class ApplyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }

            String body = new String(exchange.getRequestBody().readAllBytes());
            String type = extractParam(body, "type");
            String startStr = extractParam(body, "start");
            String endStr = extractParam(body, "end");
            String reason = extractParam(body, "reason");

            try {
                LocalDate start = LocalDate.parse(startStr);
                LocalDate end = LocalDate.parse(endStr);
                LeaveRequest req = service.submitLeaveRequest(1, type, start, end, reason);
                String response = "<html><body>Leave request submitted. ID: " + req.getRequestId() +
                        "<br><a href='/'>Go back</a></body></html>";
                exchange.sendResponseHeaders(200, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } catch (Exception e) {
                String error = "<html><body>Error: " + e.getMessage() + "<br><a href='/'>Back</a></body></html>";
                exchange.sendResponseHeaders(400, error.length());
                OutputStream os = exchange.getResponseBody();
                os.write(error.getBytes());
                os.close();
            }
        }

        private String extractParam(String query, String key) {
            for (String pair : query.split("&")) {
                String[] kv = pair.split("=");
                if (kv.length == 2 && kv[0].equals(key)) {
                    return kv[1];
                }
            }
            return "";
        }
    }
}