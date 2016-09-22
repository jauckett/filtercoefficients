/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.auckett.radio.filter;

import java.util.Formatter;

/**
 *
 * @author john auckett
 */
public class FilterCoefficents {

    static final int ORDERS = 12;
    double butterworth[][] = new double[ORDERS][ORDERS];
    double chebyshev[][] = new double[ORDERS][ORDERS];

    static double atanh(double x) {
        return 0.5 * Math.log(x + 1.0) - 0.5 * Math.log(1 - x);
    }
    

    public void run() {
     // calculate the Butterworth coefficients
        for (int k = 0; k < ORDERS; k++) {
            int n = k + 1;
            for (int j = 0; j < n; j++) {
                int i = j + 1;
                butterworth[k][j] = 2 * Math.sin((2 * i - 1) * Math.PI / (2 * n));
            }
        }
        showTable("Butterworth Normalised Values", butterworth);
        
        // generate tables for different ripple values
        for (double r=0.1; r<3.05; r+=.1) {
            calculateChebyshev(r);
        }
    }
    
    public void calculateChebyshev(double ripple) {
                     
        // Chebyshev       
        double alpha = Math.pow(10.0, ripple / 10) - 1;

        for (int k = 0; k < ORDERS; k++) {
            int n = k + 1;
            double beta = Math.sinh(atanh(1 / Math.sqrt(1 + alpha)) / n);

            // calculate the first coefficient and store it
            double c1 = butterworth[k][0] / beta;
            chebyshev[k][0] = c1;
            // System.out.println("ORDER=" + n + ", alpha=" + alpha + ", beta=" + beta + ", C1=" + c1);
            for (int j = 1; j < n; j++) {
                int i = j + 1;
                double numerator = butterworth[k][j] * butterworth[k][j - 1];
                // System.out.println("NUM=" + numerator);
                double denom = chebyshev[k][j - 1] * (beta * beta + Math.pow(Math.sin((i - 1) * (Math.PI / n)), 2));
                // System.out.println("DEN=" + denom);
                chebyshev[k][j] = numerator / denom;
            }
        }        
        
        StringBuffer sb = new StringBuffer();
        Formatter fm = new Formatter(sb);
        fm.format("Chebyshev Normalised Values %1.2f dB Passband Ripple",ripple);
        showTable(sb.toString(), chebyshev);
    }
    
    public void showTable(String name, double[][] tab) {
        System.out.println(name);
        System.out.println("-------------------------------------------------------------------------------------------------------");
        // display heading
        System.out.print("Order ");
        for (int i=0; i<tab.length; i++) {
            if ((i+1)%2 == 0) {  // it is even
                System.out.format("  %-5s ", "L" +(i+1));
            } else {
                System.out.format("  %-5s ", "C" +(i+1));
            }
            
        }
        System.out.println("");
        System.out.println(".......................................................................................................");
        for (int order = 0; order < tab.length; order++) {
            System.out.format("%5d | ", order + 1);
            for (int i = 0; i < order + 1; i++) {
                System.out.format("%1.5f ", tab[order][i]);
            }
            System.out.println("");
        }        
        System.out.println(".......................................................................................................");
        System.out.println("");
    }

    public static void main(String[] args) {

        FilterCoefficents m = new FilterCoefficents();
        m.run();

    }

}
