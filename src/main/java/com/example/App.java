package com.example;

/**
 * Electricity Billing System
 * Calculates electricity usage and generates bills based on tiered tariff
 * rates.
 *
 * Tariff Slabs (per unit in ₹):
 * 0 - 100 units : ₹1.50 per unit
 * 101 - 300 units : ₹2.50 per unit
 * 301 - 500 units : ₹4.00 per unit
 * 501+ units : ₹6.00 per unit
 *
 * Additional Charges:
 * Fixed meter charge : ₹50.00
 * Tax rate : 5%
 */
public class App {

    // ------- Tariff slab boundaries -------
    public static final int SLAB1_LIMIT = 100;
    public static final int SLAB2_LIMIT = 300;
    public static final int SLAB3_LIMIT = 500;

    // ------- Tariff rates (₹ per unit) -------
    public static final double RATE_SLAB1 = 1.50;
    public static final double RATE_SLAB2 = 2.50;
    public static final double RATE_SLAB3 = 4.00;
    public static final double RATE_SLAB4 = 6.00;

    // ------- Additional charges -------
    public static final double FIXED_METER_CHARGE = 50.00;
    public static final double TAX_RATE = 0.05; // 5%

    /**
     * Calculates electricity usage in units.
     *
     * @param currentReading  the current meter reading (kWh)
     * @param previousReading the previous meter reading (kWh)
     * @return units consumed
     * @throws IllegalArgumentException if readings are negative or current <
     *                                  previous
     */
    public static int calculateUnitsConsumed(int currentReading, int previousReading) {
        if (currentReading < 0 || previousReading < 0) {
            throw new IllegalArgumentException("Meter readings cannot be negative.");
        }
        if (currentReading < previousReading) {
            throw new IllegalArgumentException("Current reading cannot be less than previous reading.");
        }
        return currentReading - previousReading;
    }

    /**
     * Calculates the energy charge based on tiered/slab tariff rates.
     *
     * @param units number of units consumed
     * @return the energy charge amount (₹)
     * @throws IllegalArgumentException if units is negative
     */
    public static double calculateEnergyCharge(int units) {
        if (units < 0) {
            throw new IllegalArgumentException("Units consumed cannot be negative.");
        }

        double charge = 0.0;
        int remaining = units;

        // Slab 1: 0 – 100 units @ ₹1.50
        if (remaining > 0) {
            int slabUnits = Math.min(remaining, SLAB1_LIMIT);
            charge += slabUnits * RATE_SLAB1;
            remaining -= slabUnits;
        }

        // Slab 2: 101 – 300 units @ ₹2.50
        if (remaining > 0) {
            int slabUnits = Math.min(remaining, SLAB2_LIMIT - SLAB1_LIMIT);
            charge += slabUnits * RATE_SLAB2;
            remaining -= slabUnits;
        }

        // Slab 3: 301 – 500 units @ ₹4.00
        if (remaining > 0) {
            int slabUnits = Math.min(remaining, SLAB3_LIMIT - SLAB2_LIMIT);
            charge += slabUnits * RATE_SLAB3;
            remaining -= slabUnits;
        }

        // Slab 4: 501+ units @ ₹6.00
        if (remaining > 0) {
            charge += remaining * RATE_SLAB4;
        }

        return charge;
    }

    /**
     * Calculates the tax amount on the energy charge.
     *
     * @param energyCharge the pre-tax energy charge
     * @return tax amount (₹)
     */
    public static double calculateTax(double energyCharge) {
        return energyCharge * TAX_RATE;
    }

    /**
     * Calculates the total electricity bill including energy charge, tax, and fixed
     * meter charge.
     *
     * @param units number of units consumed
     * @return total bill amount (₹)
     */
    public static double calculateTotalBill(int units) {
        double energyCharge = calculateEnergyCharge(units);
        double tax = calculateTax(energyCharge);
        return energyCharge + tax + FIXED_METER_CHARGE;
    }

    /**
     * Generates a formatted bill summary string.
     *
     * @param customerName    name of the customer
     * @param currentReading  current meter reading
     * @param previousReading previous meter reading
     * @return formatted bill string
     */
    public static String generateBill(String customerName, int currentReading, int previousReading) {
        int units = calculateUnitsConsumed(currentReading, previousReading);
        double energyCharge = calculateEnergyCharge(units);
        double tax = calculateTax(energyCharge);
        double totalBill = calculateTotalBill(units);

        StringBuilder sb = new StringBuilder();
        sb.append("========================================\n");
        sb.append("       ELECTRICITY BILL SUMMARY         \n");
        sb.append("========================================\n");
        sb.append(String.format("Customer Name    : %s%n", customerName));
        sb.append(String.format("Previous Reading : %d kWh%n", previousReading));
        sb.append(String.format("Current Reading  : %d kWh%n", currentReading));
        sb.append(String.format("Units Consumed   : %d units%n", units));
        sb.append("----------------------------------------\n");
        sb.append(String.format("Energy Charge    : ₹%.2f%n", energyCharge));
        sb.append(String.format("Tax (5%%)         : ₹%.2f%n", tax));
        sb.append(String.format("Meter Charge     : ₹%.2f%n", FIXED_METER_CHARGE));
        sb.append("----------------------------------------\n");
        sb.append(String.format("TOTAL AMOUNT DUE : ₹%.2f%n", totalBill));
        sb.append("========================================\n");
        return sb.toString();
    }

    public static void main(String[] args) {
        System.out.println("Electricity Billing System initialized.\n");

        // Example bill generation
        String bill = generateBill("Naveen Kumar", 5350, 4900);
        System.out.println(bill);
    }
}