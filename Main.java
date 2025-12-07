import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import oop.Product;
import patterns.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        VendingMachine machine = VendingMachine.getInstance();
        machine.reset();

        ProductFactory junkFactory = new JunkFoodFactory();
        ProductFactory healthfood = new HealthyFoodFactory();

        // BUILDER (Крафтовий напій)
        Product customDrink = new ProductBuilder()
                .setName("Крафтовий Лимонад")
                .setPrice(45.0)
                .setParameter(330)
                .asDrink()
                .build();

        // DECORATOR (Подарункові версії)
        Product snickers = junkFactory.createSnack();
        Product vodka = healthfood.createDrink();
        Product giftSnack = new GiftPackagedProduct(snickers);
        Product giftDrink = new GiftPackagedProduct(vodka);

        
        // Використовуємо Builder для створення нових видів снеків
        Product chips = new ProductBuilder()
                .setName("Чіпси Lays (Сіль)")
                .setPrice(35.0)
                .setParameter(120) // 120 грам
                .asSnack() // Це важливо
                .build();

        Product energyDrink = new ProductBuilder()
                .setName("Red Bull")
                .setPrice(65.0)
                .setParameter(250) // 250 мл
                .asDrink()
                .build();

        Product simpleCola = junkFactory.createDrink(); 
        
        Product megaSnickers = new ProductBuilder()
                .setName("Snickers Super")
                .setPrice(40.0)
                .setParameter(112)
                .asSnack()
                .build();

        // --- ЗАВАНТАЖЕННЯ СЛОТІВ ---
        
        Slot slotA1 = new Slot(junkFactory.createSnack(), 10);
        Slot slotA2 = new Slot(customDrink, 5);
        Slot slotB1 = new Slot(giftSnack, 2);
        Slot slotB2 = new Slot(giftDrink, 3);
        Slot slotC1 = new Slot(chips, 15);        
        Slot slotC2 = new Slot(energyDrink, 10);  
        Slot slotD1 = new Slot(simpleCola, 20);   
        Slot slotD2 = new Slot(megaSnickers, 8);  

        machine.loadSlot("A1", slotA1);
        machine.loadSlot("A2", slotA2);
        machine.loadSlot("B1", slotB1);
        machine.loadSlot("B2", slotB2);
        machine.loadSlot("C1", slotC1);
        machine.loadSlot("C2", slotC2);
        machine.loadSlot("D1", slotD1);
        machine.loadSlot("D2", slotD2);

        // --- 3. OBSERVER (Спостерігач) ---
        // Створимо спостерігача, який реагує, якщо лишилось менше 3 шт
        LowStockNotifier notifier = new LowStockNotifier(3);
        
        // Підписуємо слоти на сповіщення
        slotA1.addObserver(notifier);
        slotA2.addObserver(notifier);
        slotB1.addObserver(notifier);
        slotB2.addObserver(notifier);
        slotC1.addObserver(notifier);
        slotC2.addObserver(notifier);
        slotD1.addObserver(notifier);
        slotD2.addObserver(notifier);

        // --- 4. COMMAND (Налаштування команд) ---
        Map<String, Command> commands = new HashMap<>();
        
        commands.put("1", () -> machine.displayAllProducts());
        commands.put("2", new InsertMoneyCommand(machine, scanner)); 
        
        // Тут ми використали рефакторинг 
        commands.put("3", new BuyProductCommand(machine, scanner)); 

        commands.put("4", () -> System.out.println("Решта: " + machine.getChange()));

        // --- ЗАПУСК ---
        boolean running = true;
        System.out.println("=== ВІТАЄМО В АВТОМАТІ ===");
        
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();
            if (choice.equals("5")) {
                running = false;
                System.out.println("Дякуємо!");
            } else if (commands.containsKey(choice)) {
                commands.get(choice).execute();
            } else {
                System.out.println("Невідома команда.");
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n--- MENU ---");
        System.out.println("1. Показати товари");
        System.out.println("2. Внести гроші");
        System.out.println("3. Купити");
        System.out.println("4. Решта");
        System.out.println("5. Вихід");
        System.out.print("> ");
    }
}