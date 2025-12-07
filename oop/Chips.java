package oop;

public class Chips extends Product {
    public Chips(String name, double price) {
        super(name, price);
    }

    @Override
    public String getDisplayInfo() {
        return getName() + " - " + getPrice() + " грн";
    }
}