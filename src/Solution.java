import java.util.Random;

public class Solution {
    private double a,b,c,x;

    public Solution(double a, double b, double c, double x) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.x = x;
    }

    public double getA() {
        return a;
    }

    public void setA(double a) {
        this.a = a;
    }

    public double getB() {
        return b;
    }

    public void setB(double b) {
        this.b = b;
    }

    public double getC() {
        return c;
    }

    public void setC(double c) {
        this.c = c;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double calculateError(){
        return Math.abs(a * x * x + b * x + c);
    }


}
