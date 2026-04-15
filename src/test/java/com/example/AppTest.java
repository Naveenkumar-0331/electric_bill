package com.example;

import junit.framework.TestCase;

/**
 * JUnit tests for Electricity Billing System tariff calculations.
 * Covers all tariff slabs, boundary conditions, edge cases,
 * tax calculations, total bill computation, and error handling.
 */
public class AppTest extends TestCase {

    // =====================================================================
    // 1. Unit Consumption Calculation Tests
    // =====================================================================

    public void testCalculateUnitsConsumed_NormalUsage() {
        assertEquals(450, App.calculateUnitsConsumed(5350, 4900));
    }

    public void testCalculateUnitsConsumed_ZeroUsage() {
        assertEquals(0, App.calculateUnitsConsumed(1000, 1000));
    }

    public void testCalculateUnitsConsumed_SmallUsage() {
        assertEquals(1, App.calculateUnitsConsumed(101, 100));
    }

    public void testCalculateUnitsConsumed_LargeUsage() {
        assertEquals(10000, App.calculateUnitsConsumed(20000, 10000));
    }

    public void testCalculateUnitsConsumed_NegativeCurrentReading() {
        try {
            App.calculateUnitsConsumed(-100, 50);
            fail("Expected IllegalArgumentException for negative current reading");
        } catch (IllegalArgumentException e) {
            assertEquals("Meter readings cannot be negative.", e.getMessage());
        }
    }

    public void testCalculateUnitsConsumed_NegativePreviousReading() {
        try {
            App.calculateUnitsConsumed(100, -50);
            fail("Expected IllegalArgumentException for negative previous reading");
        } catch (IllegalArgumentException e) {
            assertEquals("Meter readings cannot be negative.", e.getMessage());
        }
    }

    public void testCalculateUnitsConsumed_CurrentLessThanPrevious() {
        try {
            App.calculateUnitsConsumed(100, 200);
            fail("Expected IllegalArgumentException when current < previous");
        } catch (IllegalArgumentException e) {
            assertEquals("Current reading cannot be less than previous reading.", e.getMessage());
        }
    }

    // =====================================================================
    // 2. Tariff / Energy Charge Calculation Tests (Slab-wise)
    // =====================================================================

    // --- Slab 1: 0–100 units @ ₹1.50 ---

    public void testEnergyCharge_ZeroUnits() {
        assertEquals(0.0, App.calculateEnergyCharge(0), 0.01);
    }

    public void testEnergyCharge_Slab1_OneUnit() {
        // 1 × 1.50 = 1.50
        assertEquals(1.50, App.calculateEnergyCharge(1), 0.01);
    }

    public void testEnergyCharge_Slab1_FiftyUnits() {
        // 50 × 1.50 = 75.00
        assertEquals(75.00, App.calculateEnergyCharge(50), 0.01);
    }

    public void testEnergyCharge_Slab1_BoundaryExact() {
        // 100 × 1.50 = 150.00
        assertEquals(150.00, App.calculateEnergyCharge(100), 0.01);
    }

    // --- Slab 2: 101–300 units @ ₹2.50 ---

    public void testEnergyCharge_Slab2_BoundaryStart() {
        // 100 × 1.50 + 1 × 2.50 = 150 + 2.50 = 152.50
        assertEquals(152.50, App.calculateEnergyCharge(101), 0.01);
    }

    public void testEnergyCharge_Slab2_MidRange() {
        // 100 × 1.50 + 100 × 2.50 = 150 + 250 = 400.00
        assertEquals(400.00, App.calculateEnergyCharge(200), 0.01);
    }

    public void testEnergyCharge_Slab2_BoundaryExact() {
        // 100 × 1.50 + 200 × 2.50 = 150 + 500 = 650.00
        assertEquals(650.00, App.calculateEnergyCharge(300), 0.01);
    }

    // --- Slab 3: 301–500 units @ ₹4.00 ---

    public void testEnergyCharge_Slab3_BoundaryStart() {
        // 100 × 1.50 + 200 × 2.50 + 1 × 4.00 = 150 + 500 + 4 = 654.00
        assertEquals(654.00, App.calculateEnergyCharge(301), 0.01);
    }

    public void testEnergyCharge_Slab3_MidRange() {
        // 100 × 1.50 + 200 × 2.50 + 100 × 4.00 = 150 + 500 + 400 = 1050.00
        assertEquals(1050.00, App.calculateEnergyCharge(400), 0.01);
    }

    public void testEnergyCharge_Slab3_450Units() {
        // 100 × 1.50 + 200 × 2.50 + 150 × 4.00 = 150 + 500 + 600 = 1250.00
        assertEquals(1250.00, App.calculateEnergyCharge(450), 0.01);
    }

    public void testEnergyCharge_Slab3_BoundaryExact() {
        // 100 × 1.50 + 200 × 2.50 + 200 × 4.00 = 150 + 500 + 800 = 1450.00
        assertEquals(1450.00, App.calculateEnergyCharge(500), 0.01);
    }

    // --- Slab 4: 501+ units @ ₹6.00 ---

    public void testEnergyCharge_Slab4_BoundaryStart() {
        // 100 × 1.50 + 200 × 2.50 + 200 × 4.00 + 1 × 6.00 = 1450 + 6 = 1456.00
        assertEquals(1456.00, App.calculateEnergyCharge(501), 0.01);
    }

    public void testEnergyCharge_Slab4_LargeUsage() {
        // 100 × 1.50 + 200 × 2.50 + 200 × 4.00 + 500 × 6.00
        // = 150 + 500 + 800 + 3000 = 4450.00
        assertEquals(4450.00, App.calculateEnergyCharge(1000), 0.01);
    }

    public void testEnergyCharge_NegativeUnits() {
        try {
            App.calculateEnergyCharge(-10);
            fail("Expected IllegalArgumentException for negative units");
        } catch (IllegalArgumentException e) {
            assertEquals("Units consumed cannot be negative.", e.getMessage());
        }
    }

    // =====================================================================
    // 3. Tax Calculation Tests
    // =====================================================================

    public void testTax_ZeroCharge() {
        assertEquals(0.0, App.calculateTax(0.0), 0.01);
    }

    public void testTax_Slab1() {
        // Energy charge for 100 units = 150.00, tax = 150 × 0.05 = 7.50
        double energyCharge = App.calculateEnergyCharge(100);
        assertEquals(7.50, App.calculateTax(energyCharge), 0.01);
    }

    public void testTax_Slab3() {
        // Energy charge for 450 units = 1250.00, tax = 1250 × 0.05 = 62.50
        double energyCharge = App.calculateEnergyCharge(450);
        assertEquals(62.50, App.calculateTax(energyCharge), 0.01);
    }

    public void testTax_LargeCharge() {
        // Energy charge for 1000 units = 4450.00, tax = 4450 × 0.05 = 222.50
        double energyCharge = App.calculateEnergyCharge(1000);
        assertEquals(222.50, App.calculateTax(energyCharge), 0.01);
    }

    // =====================================================================
    // 4. Total Bill Calculation Tests (Energy + Tax + Meter Charge)
    // =====================================================================

    public void testTotalBill_ZeroUnits() {
        // Energy = 0, Tax = 0, Meter = 50 → Total = 50.00
        assertEquals(50.00, App.calculateTotalBill(0), 0.01);
    }

    public void testTotalBill_Slab1Only() {
        // Energy = 150.00, Tax = 7.50, Meter = 50 → Total = 207.50
        assertEquals(207.50, App.calculateTotalBill(100), 0.01);
    }

    public void testTotalBill_Slab2Boundary() {
        // Energy = 650.00, Tax = 32.50, Meter = 50 → Total = 732.50
        assertEquals(732.50, App.calculateTotalBill(300), 0.01);
    }

    public void testTotalBill_Slab3_450Units() {
        // Energy = 1250.00, Tax = 62.50, Meter = 50 → Total = 1362.50
        assertEquals(1362.50, App.calculateTotalBill(450), 0.01);
    }

    public void testTotalBill_Slab4_1000Units() {
        // Energy = 4450.00, Tax = 222.50, Meter = 50 → Total = 4722.50
        assertEquals(4722.50, App.calculateTotalBill(1000), 0.01);
    }

    // =====================================================================
    // 5. Bill Generation Tests
    // =====================================================================

    public void testGenerateBill_ContainsCustomerName() {
        String bill = App.generateBill("Naveen Kumar", 5350, 4900);
        assertTrue(bill.contains("Naveen Kumar"));
    }

    public void testGenerateBill_ContainsUnitsConsumed() {
        String bill = App.generateBill("Test User", 1200, 1000);
        assertTrue(bill.contains("200 units"));
    }

    public void testGenerateBill_ContainsTotalAmount() {
        String bill = App.generateBill("Test User", 1100, 1000);
        // 100 units → Energy = 150, Tax = 7.50, Meter = 50 → Total = 207.50
        assertTrue(bill.contains("207.50"));
    }

    public void testGenerateBill_ContainsMeterReadings() {
        String bill = App.generateBill("Test User", 5000, 4500);
        assertTrue(bill.contains("5000"));
        assertTrue(bill.contains("4500"));
    }
}
