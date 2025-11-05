
package com.example;

import java.util.*;

/**
 * Single file, only basics: classes, ArrayList, loops, Scanner.
 *
 * What it does:
 *  - Create customers
 *  - Attach existing vehicle IDs to a customer (we don't build Vehicle objects here)
 *  - Book a Work Order (uses customerId + vehicleId)
 *  - Add labour hours to a Work Order
 *  - Mark DONE -> Approve (creates simple Invoice) -> Pay
 *
 * No enums, no HashMap, no exceptions. Just checks + messages.
 */
public class Main {

    // --- Status strings (no enums) ---
    static final String WO_PENDING   = "PENDING";
    static final String WO_INPROG    = "IN_PROGRESS";
    static final String WO_DONE      = "DONE";
    static final String WO_APPROVED  = "APPROVED";

    static final String INV_UNPAID   = "UNPAID";
    static final String INV_PAID     = "PAID";

    // --- Data classes (very small) ---
    static class Customer {
        int id;
        String name;
        String phone;
        ArrayList<Integer> vehicleIds = new ArrayList<>(); // store vehicle IDs only

        Customer(int id, String name, String phone) {
            this.id = id;
            this.name = name;
            this.phone = phone;
        }

        public String toString() {
            return "Customer{id=" + id + ", name='" + name + "', phone='" + phone +
                    "', vehicles=" + vehicleIds + "}";
        }
    }

    static class WorkOrder {
        int id;
        int customerId;
        int vehicleId;
        String status = WO_PENDING;
        double labourHours = 0.0;

        WorkOrder(int id, int customerId, int vehicleId) {
            this.id = id;
            this.customerId = customerId;
            this.vehicleId = vehicleId;
        }

        public String toString() {
            return "WO{id=" + id + ", cust=" + customerId + ", veh=" + vehicleId +
                    ", status=" + status + ", hours=" + labourHours + "}";
        }
    }

    static class Invoice {
        int id;
        int workOrderId;
        double total;
        String status = INV_UNPAID;

        Invoice(int id, int workOrderId, double total) {
            this.id = id;
            this.workOrderId = workOrderId;
            this.total = total;
        }

        public String toString() {
            return "Invoice{id=" + id + ", wo=" + workOrderId + ", total=$" + String.format("%.2f", total) +
                    ", status=" + status + "}";
        }
    }

    // --- "System" that holds everything using ArrayLists ---
    static class AutoServiceSystem {
        static final double HOURLY_RATE = 100.0;

        ArrayList<Customer> customers = new ArrayList<>();
        ArrayList<WorkOrder> workOrders = new ArrayList<>();
        ArrayList<Invoice> invoices = new ArrayList<>();

        int nextCustomerId = 1;
        int nextWorkOrderId = 1;
        int nextInvoiceId = 1;

        // Customers
        int createCustomer(String name, String phone) {
            Customer c = new Customer(nextCustomerId++, name, phone);
            customers.add(c);
            return c.id;
        }

        boolean addVehicleIdToCustomer(int customerId, int vehicleId) {
            int idx = findCustomerIndex(customerId);
            if (idx == -1) return false;
            customers.get(idx).vehicleIds.add(vehicleId);
            return true;
        }

        // Work Orders
        int bookWorkOrder(int customerId, int vehicleId) {
            if (findCustomerIndex(customerId) == -1) return -1;
            // simple check: vehicleId should be attached to that customer
            if (!customerHasVehicle(customerId, vehicleId)) return -2;

            WorkOrder wo = new WorkOrder(nextWorkOrderId++, customerId, vehicleId);
            wo.status = WO_INPROG; // start right away
            workOrders.add(wo);
            return wo.id;
        }

        boolean addLabourHours(int woId, double hours) {
            int idx = findWorkOrderIndex(woId);
            if (idx == -1) return false;
            if (hours < 0) return false;
            workOrders.get(idx).labourHours += hours;
            if (workOrders.get(idx).status.equals(WO_PENDING)) {
                workOrders.get(idx).status = WO_INPROG;
            }
            return true;
        }

        boolean markDone(int woId) {
            int idx = findWorkOrderIndex(woId);
            if (idx == -1) return false;
            workOrders.get(idx).status = WO_DONE;
            return true;
        }

        int approve(int woId) {
            int idx = findWorkOrderIndex(woId);
            if (idx == -1) return -1;
            WorkOrder wo = workOrders.get(idx);
            if (!wo.status.equals(WO_DONE)) return -2;
            wo.status = WO_APPROVED;

            double total = round2(wo.labourHours * HOURLY_RATE);
            Invoice inv = new Invoice(nextInvoiceId++, wo.id, total);
            invoices.add(inv);
            return inv.id;
        }

        boolean payInvoice(int invoiceId, double amount) {
            int idx = findInvoiceIndex(invoiceId);
            if (idx == -1) return false;
            Invoice inv = invoices.get(idx);
            if (amount < inv.total) return false;
            inv.status = INV_PAID;
            return true;
        }

        // Find helpers (linear scans)
        int findCustomerIndex(int id) {
            for (int i = 0; i < customers.size(); i++) {
                if (customers.get(i).id == id) return i;
            }
            return -1;
        }

        int findWorkOrderIndex(int id) {
            for (int i = 0; i < workOrders.size(); i++) {
                if (workOrders.get(i).id == id) return i;
            }
            return -1;
        }

        int findInvoiceIndex(int id) {
            for (int i = 0; i < invoices.size(); i++) {
                if (invoices.get(i).id == id) return i;
            }
            return -1;
        }

        boolean customerHasVehicle(int customerId, int vehicleId) {
            int idx = findCustomerIndex(customerId);
            if (idx == -1) return false;
            return customers.get(idx).vehicleIds.contains(vehicleId);
        }

        static double round2(double v) {
            return Math.round(v * 100.0) / 100.0;
        }
    }

    // --- Console UI ---
    static final Scanner in = new Scanner(System.in);
    static final AutoServiceSystem sys = new AutoServiceSystem();

    public static void main(String[] args) {
        System.out.println("=== Auto Service System (Super Simplified) ===");
        boolean run = true;
        while (run) {
            menu();
            int choice = readInt("Choose: ");
            if (choice == 0) { run = false; continue; }

            switch (choice) {
                case 1 -> createCustomer();
                case 2 -> attachVehicleId();
                case 3 -> bookWO();
                case 4 -> addHours();
                case 5 -> doneWO();
                case 6 -> approveWO();
                case 7 -> payInv();
                case 8 -> listCustomers();
                case 9 -> listWOs();
                case 10 -> listInvoices();
                default -> System.out.println("Invalid.");
            }
        }
        System.out.println("Bye.");
    }

    // --- Menu actions ---
    static void createCustomer() {
        String name = readLine("Name: ");
        String phone = readLine("Phone: ");
        int id = sys.createCustomer(name, phone);
        System.out.println("Created customer ID " + id);
    }

    static void attachVehicleId() {
        int cid = readInt("Customer ID: ");
        int vid = readInt("Existing Vehicle ID: ");
        boolean ok = sys.addVehicleIdToCustomer(cid, vid);
        System.out.println(ok ? "Vehicle attached." : "Could not attach (check IDs).");
    }

    static void bookWO() {
        int cid = readInt("Customer ID: ");
        int vid = readInt("Vehicle ID (must belong to customer): ");
        int woId = sys.bookWorkOrder(cid, vid);
        if (woId == -1) System.out.println("No such customer.");
        else if (woId == -2) System.out.println("Vehicle not linked to that customer.");
        else System.out.println("WO created: " + woId);
    }

    static void addHours() {
        int woId = readInt("WO ID: ");
        double h = readDouble("Hours to add: ");
        boolean ok = sys.addLabourHours(woId, h);
        System.out.println(ok ? "Hours added." : "Could not add hours.");
    }

    static void doneWO() {
        int woId = readInt("WO ID: ");
        boolean ok = sys.markDone(woId);
        System.out.println(ok ? "Marked DONE." : "Bad WO ID.");
    }

    static void approveWO() {
        int woId = readInt("WO ID: ");
        int invId = sys.approve(woId);
        if (invId == -1) System.out.println("Bad WO ID.");
        else if (invId == -2) System.out.println("WO must be DONE first.");
        else System.out.println("Approved. Invoice ID " + invId);
    }

    static void payInv() {
        int invId = readInt("Invoice ID: ");
        double amt = readDouble("Payment amount: $");
        boolean ok = sys.payInvoice(invId, amt);
        System.out.println(ok ? "Paid. Thank you." : "Payment failed (check ID/amount).");
    }

    static void listCustomers() {
        System.out.println("-- Customers --");
        for (int i = 0; i < sys.customers.size(); i++) {
            System.out.println(sys.customers.get(i));
        }
    }

    static void listWOs() {
        System.out.println("-- Work Orders --");
        for (int i = 0; i < sys.workOrders.size(); i++) {
            System.out.println(sys.workOrders.get(i));
        }
    }

    static void listInvoices() {
        System.out.println("-- Invoices --");
        for (int i = 0; i < sys.invoices.size(); i++) {
            System.out.println(sys.invoices.get(i));
        }
    }

    // --- UI helpers ---
    static void menu() {
        System.out.println("\nMenu:");
        System.out.println("1) Create Customer");
        System.out.println("2) Attach Vehicle ID to Customer");
        System.out.println("3) Book Work Order");
        System.out.println("4) Add Labour Hours");
        System.out.println("5) Mark WO as DONE");
        System.out.println("6) Approve WO -> Invoice");
        System.out.println("7) Pay Invoice");
        System.out.println("8) List Customers");
        System.out.println("9) List Work Orders");
        System.out.println("10) List Invoices");
        System.out.println("0) Exit");
    }

    static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = in.nextLine().trim();
            try { return Integer.parseInt(s); }
            catch (NumberFormatException e) { System.out.println("Enter a whole number."); }
        }
    }

    static double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = in.nextLine().trim();
            try { return Double.parseDouble(s); }
            catch (NumberFormatException e) { System.out.println("Enter a number (e.g., 1.5)."); }
        }
    }

    static String readLine(String prompt) {
        System.out.print(prompt);
        return in.nextLine();
    }
}
