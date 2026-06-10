import enteties.Product;
import enums.Commands;
import enums.Groups;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import packet.Message;
import packet.Package;
import packet_processing.Processor;
import utils.DataBase;
import utils.Queues;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestThreads {
    //  якщо 333 потоків одночасно додають гречку то кількість гречки рівно 333
//    @Test
//    public void concurrentAddTest() throws Exception {
//        Product buckwheat = DataBase.groups.get(Groups.GROATS.name).get("Buckwheat");
//        int operations = 333;
//        ExecutorService pool = Executors.newFixedThreadPool(4);
//        for (int i = 0; i < operations; i++) {
//            pool.submit(() ->
//                    exe(
//                            Commands.ADD_PRODUCT_AMOUNT.cType,
//                            "product: Buckwheat" +
//                                    "\n" +
//                                    "amount: 1"
//                    )
//            );
//        }
//        pool.shutdown();
//        pool.awaitTermination(6, TimeUnit.SECONDS);
//        assertEquals(333,
//                buckwheat.getQuantity());
//    }
//
//    //невід'ємна к-сть
//    @Test
//    public void concurrentRemoveTest() throws Exception {
//        Product buckwheat = DataBase.groups.get(Groups.GROATS.name).get("Buckwheat");
//        buckwheat.setQuantity(100);
//        ExecutorService pool =
//                Executors.newFixedThreadPool(4);
//        for (int i = 0; i < 1000; i++) {
//            pool.submit(() ->
//                    exe(
//                            Commands.DEL_PRODUCT_AMOUNT.cType,
//                            "product: Buckwheat" +
//                                    "\n" +
//                                    "amount: 1"
//                    )
//            );
//        }
//        pool.shutdown();
//        pool.awaitTermination(7, TimeUnit.SECONDS);
//
//        assertTrue(buckwheat.getQuantity() >= 0);
//
//        assertEquals(0,
//                buckwheat.getQuantity());
//    }
//
//    //одночасне списання/додавання правильно обробляється
//    @Test
//    public void mixedOperationsTest() throws Exception {
//        Product buckwheat = DataBase.groups.get(Groups.GROATS.name).get("Buckwheat");
//        buckwheat.setQuantity(100);
//        int addOperations = 500;
//        int removeOperations = 300;
//        ExecutorService pool =
//                Executors.newFixedThreadPool(4);
//        for (int i = 0; i < addOperations; i++) {
//            pool.submit(() ->
//                    exe(
//                            Commands.ADD_PRODUCT_AMOUNT.cType,
//                            "product: Buckwheat\namount: 1"
//                    )
//            );
//        }
//        for (int i = 0; i < removeOperations; i++) {
//            pool.submit(() ->
//                    exe(
//                            Commands.DEL_PRODUCT_AMOUNT.cType,
//                            "product: Buckwheat" +
//                                    "\n" +
//                                    "amount: 1"
//                    )
//            );
//        }
//        pool.shutdown();
//        pool.awaitTermination(6, TimeUnit.SECONDS);
//        // 100 + 500 - 300 = 300
//        assertEquals(
//                300,
//                buckwheat.getQuantity()
//        );
//    }
//
//
//    private void exe(int type, String body) {
//        try {
//            Message msg = new Message(type, 1, body);
//            Package pg = new Package(msg);
//            Queues.queueOfPackages.put(pg);
//            new Processor().process();
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    @Before
//    public void prep() {
//        DataBase.groups.clear();
//        Queues.queueOfPackages.clear();
//        ConcurrentHashMap<String, Product> food =
//                new ConcurrentHashMap<>();
//        Product buckwheat = new Product("Buckwheat");
//        buckwheat.setQuantity(0);
//        food.put("Buckwheat", buckwheat);
//        DataBase.groups.put(Groups.GROATS.name, food);
//    }
//
//    @After
//    public void reset() {
//        DataBase.groups.clear();
//        Queues.queueOfPackages.clear();
//    }
}