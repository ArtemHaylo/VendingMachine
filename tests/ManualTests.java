import exceptions.InsufficientFundsException;
import exceptions.OutOfStockException;
import oop.Drink;
import oop.Product;
import oop.Snack;
import patterns.*;

public class ManualTests {

    public static void main(String[] args) {
        System.out.println("=== ЗАПУСК ТЕСТІВ  ===\n");

        runTest("Тест Singleton", ManualTests::testSingleton);
        runTest("Тест: Успішна покупка", ManualTests::testSuccessfulPurchase);
        runTest("Тест: Недостатньо коштів", ManualTests::testInsufficientFunds);
        runTest("Тест: Товар закінчився", ManualTests::testOutOfStock);
        runTest("Тест: Builder (Будівельник)", ManualTests::testProductBuilder);
        runTest("Тест: Observer (Спостерігач)", ManualTests::testObserver);

        System.out.println("\n=== ВСІ ТЕСТИ ЗАВЕРШЕНО ===");
    }

    // --- САМІ ТЕСТИ ---

    private static void testSingleton() {
        VendingMachine m1 = VendingMachine.getInstance();
        VendingMachine m2 = VendingMachine.getInstance();
        
        check(m1 == m2, "getInstance має повертати один і той самий об'єкт");
    }

    private static void testSuccessfulPurchase() {
        VendingMachine machine = setupMachine();
        PaymentStrategy cash = new CashPayment();
        
        try {
            // Вносимо 50 грн, купуємо товар за 25
            machine.insertMoney(50, cash);
            machine.selectProduct("A1"); // Snickers (25 грн)

            check(machine.getCurrentBalance() == 25.0, "Баланс має стати 25.0");
            check(machine.getSlot("A1").GetQuantity() == 9, "Кількість Snickers має стати 9");
            
        } catch (Exception e) {
            fail("Викинуто неочікуваний виняток: " + e.getMessage());
        }
    }

    private static void testInsufficientFunds() {
        VendingMachine machine = setupMachine();
        PaymentStrategy cash = new CashPayment();

        try {
            machine.insertMoney(10, cash); // Вносимо лише 10
            machine.selectProduct("A1");   // А коштує 25
            fail("Мав вилетіти виняток InsufficientFundsException");
        } catch (InsufficientFundsException e) {
            // Це очікувана поведінка
            check(true, "Отримано очікуваний виняток: " + e.getMessage());
        } catch (Exception e) {
            fail("Неправильний тип винятку: " + e.getClass().getSimpleName());
        }
    }

    private static void testOutOfStock() {
        VendingMachine machine = setupMachine();
        PaymentStrategy cash = new CashPayment();

        try {
            machine.insertMoney(100, cash);
            machine.selectProduct("A2"); // Купуємо єдину Колу
            
            // Намагаємося купити другу
            try {
                machine.selectProduct("A2");
                fail("Мав вилетіти виняток OutOfStockException");
            } catch (OutOfStockException e) {
                check(true, "Отримано очікуваний виняток: " + e.getMessage());
            }

        } catch (Exception e) {
            fail("Помилка в логіці тесту: " + e.getMessage());
        }
    }

    private static void testProductBuilder() {
        Product drink = new ProductBuilder()
                .setName("Тестовий Квас")
                .setPrice(15.0)
                .setParameter(500)
                .asDrink()
                .build();

        check(drink instanceof Drink, "Створений об'єкт має бути Drink");
        check(drink.getName().equals("Тестовий Квас"), "Ім'я має співпадати");
        check(drink.getPrice() == 15.0, "Ціна має бути 15.0");
    }

    private static void testObserver() {
        VendingMachine machine = setupMachine();
        Slot slot = machine.getSlot("A1");

        // Створюємо локальну змінну-прапорець, щоб перевірити виклик
        final boolean[] wasCalled = {false};
        
        slot.addObserver((code, qty) -> {
            wasCalled[0] = true;
            System.out.println("   [Observer спрацював] " + code + " залишилось " + qty);
        });

        try {
            machine.insertMoney(50, new CashPayment());
            machine.selectProduct("A1");
            
            check(wasCalled[0], "Observer мав спрацювати під час видачі товару");
        } catch (Exception e) {
            fail("Помилка під час тесту Observer: " + e.getMessage());
        }
    }

    // --- ДОПОМІЖНІ МЕТОДИ ---

    // Налаштування чистого автомата перед кожним тестом
    private static VendingMachine setupMachine() {
        VendingMachine machine = VendingMachine.getInstance();
        machine.reset();
        
        Product snickers = new Snack("Snickers", 25.0, 50);
        Product cola = new Drink("Cola", 20.0, 500);

        machine.loadSlot("A1", new Slot(snickers, 10));
        machine.loadSlot("A2", new Slot(cola, 1));
        
        return machine;
    }

    // Метод для перевірки умови
    private static void check(boolean condition, String message) {
        if (condition) {
            System.out.println("   ✅ OK: " + message);
        } else {
            System.err.println("   ❌ FAIL: " + message);
            throw new RuntimeException("Test failed"); // Перериваємо поточний тест
        }
    }

    // Метод для провалу тесту
    private static void fail(String message) {
        System.err.println("   ❌ FAIL: " + message);
        throw new RuntimeException("Test failed");
    }

    // Запускач тестів
    private static void runTest(String testName, Runnable testMethod) {
        System.out.println(">> " + testName);
        try {
            testMethod.run();
        } catch (RuntimeException e) {
            // Тест провалився, але продовжуємо інші
            System.out.println("   (Тест провалено)\n");
            return;
        } catch (Exception e) {
            System.out.println("   (Критична помилка: " + e.getMessage() + ")\n");
            return;
        }
        System.out.println("   (Тест пройдено)\n");
    }
}