package patterns; 

import oop.Product;

public class GiftPackagedProduct extends Product {
    private final Product originalProduct;
    private static final double PACKAGING_COST = 10.0;

    public GiftPackagedProduct(Product originalProduct) {
        super(originalProduct.getName() + " (Подарунок 🎁)", originalProduct.getPrice() + PACKAGING_COST);
        this.originalProduct = originalProduct;
    }

    @Override
    public String getDisplayInfo() {
        return originalProduct.getName() + " + Сюрпріз (+10 грн) - " + getPrice() + " грн";
    }
}