package shop.storage;

import org.h2.tools.DeleteDbFiles;
import shop.Customer;
import shop.Order;
import shop.Product;
import shop.utils.Printer;
import shop.utils.StoreFilter;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * In order to test H2StoreImpl, please refer to the file H2Store located in
 * the folder ./src/main/java/shop/example
 */
public class H2StoreImpl implements CustomerStore, CustomerStoreQuery {

    private static final String DB_DRIVER = "org.h2.Driver";
    private static final String DB_CONNECTION = "jdbc:h2:./data/shop/H2Store/h2Store";
    private static final String DB_USER = "";
    private static final String DB_PASSWORD = "";

    private static Connection connection;

    private boolean isOpen = false;

    @Override
    public void open() {
        try {
            Class.forName(DB_DRIVER);
            connection = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD);
            createTables();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } finally {
            isOpen = true;
        }
    }

    private static void createTables() throws SQLException {
        String createCustomer =
                "CREATE TABLE CUSTOMERS(id int primary key, name varchar(255))";
        String createOrder =
                "CREATE TABLE ORDERS(oid int primary key, customerId int, shippingAddress varchar(255))";
        String createProduct =
                "CREATE TABLE PRODUCTS(pid int primary key, pname varchar(255))";
        String createOrderItem =
                "CREATE TABLE ORDERITEMS(otid int auto_increment, orderId int, productId int)";

        createTable(createCustomer);
        createTable(createOrder);
        createTable(createProduct);
        createTable(createOrderItem);

        connection.commit();
    }

    @Override
    public void insertCustomer(Customer customer) {
        ensureConnection();

        String insertCustomer =
                "INSERT INTO CUSTOMERS" + "(id, name) values" + "(?,?)";
        try {
            executeInsertQuery(insertCustomer,
                    customer.getCustomerId(), customer.getUserName());

            for (Order order : customer.getOrders()) {
                insertOrder(order, customer.getCustomerId());
            }

            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void insertOrder(Order order, int customerId) throws SQLException {
        String insertOrder =
                "INSERT INTO ORDERS" + "(oid, customerId, shippingAddress) values" + "(?,?,?)";

        executeInsertQuery(insertOrder,
                order.getOrderId(), customerId, order.getShippingAddress());

        for (Product product : order.getItems()) {
            insertProduct(product);
            insertOrderItem(order.getOrderId(), product.getProductId());
        }
    }

    private void insertProduct(Product product) throws SQLException {
        String insertProduct =
                "MERGE INTO PRODUCTS" + "(pid, pname) values" + "(?,?)";

        executeInsertQuery(insertProduct,
                product.getProductId(), product.getName());
    }

    private void insertOrderItem(int orderId, int productId) throws SQLException {
        String insertOrderItem =
                "INSERT INTO ORDERITEMS" + "(orderId, productId) values" + "(?,?)";

        executeInsertQuery(insertOrderItem,
                orderId, productId);
    }

    @Override
    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            isOpen = false;
        }
    }

    @Override
    public void cleanUp() {
        DeleteDbFiles.execute("./data/shop/H2Store", "h2Store", true);
    }

    @Override
    public void queryAllUsers() {
        ensureConnection();

        List<Customer> customerList = new ArrayList<>();

        String selectQuery =
                "SELECT * FROM CUSTOMERS";

        try {
            executeSelectQuery(selectQuery, rs -> {
                while(rs.next()) {
                    int customerId = rs.getInt("id");
                    String userName = rs.getString("name");
                    String address = "";

                    Customer customer = new Customer(customerId, userName, address);

                    List<Order> orderList = queryOrdersByUser(customerId);
                    for (Order order : orderList) {
                        customer.addOrder(order);
                    }

                    customerList.add(customer);
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for (Customer customer : customerList) {
            Printer.printCustomer(0, customer);
        }
    }

    private static List<Order> queryOrdersByUser(int customerId) {
        List<Order> orderList = new ArrayList<>();

        String selectQuery =
                "SELECT * FROM ORDERS WHERE customerId = ?";

        try {
            executeSelectQuery(selectQuery, rs -> {
                while(rs.next()) {
                    int orderId = rs.getInt("oid");
                    String shippingAddress = rs.getString("shippingAddress");
                    List<Product> productList = queryProductsByOrder(orderId);

                    Order order = new Order(orderId, shippingAddress, productList);
                    orderList.add(order);
                }
            }, customerId);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return orderList;
    }

    private static List<Product> queryProductsByOrder(int orderId) {
        List<String> productIdList = new ArrayList<>();
        List<Product> productList = new ArrayList<>();

        String selectOrderItemQuery =
                "SELECT * FROM ORDERITEMS WHERE orderId = ?";

        try {
            executeSelectQuery(selectOrderItemQuery, rs -> {
                while(rs.next()) {
                    int productId = rs.getInt("productId");
                    productIdList.add(Integer.toString(productId));
                }
            }, orderId);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String selectProductQuery =
                "SELECT * FROM PRODUCTS WHERE pid IN ("
                + String.join(",", productIdList)
                + ")";

        try {
            executeSelectQuery(selectProductQuery, rs -> {
                while(rs.next()) {
                    int productId = rs.getInt("pid");
                    String productName = rs.getString("pname");
                    productList.add(new Product(productId, productName));
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return productList;
    }

    @Override
    public void queryTopProduct() {
        ensureConnection();

        String selectQuery =
                "SELECT pid, pname, count FROM PRODUCTS"
                + " JOIN ("
                + " SELECT productId, COUNT(*) as count FROM ORDERITEMS"
                + " GROUP BY productId"
                + " ) AS OCCURRENCES"
                + " ON PRODUCTS.pid = OCCURRENCES.productId";

        Map<Product, Integer> productSalesMap = new HashMap<>();

        try {
            executeSelectQuery(selectQuery, rs -> {
                while(rs.next()) {
                    int productId = rs.getInt("pid");
                    String productName = rs.getString("pname");
                    Product product = new Product(productId, productName);

                    int count = rs.getInt("count");

                    productSalesMap.put(product, count);
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Map.Entry<List<Product>, Integer> topProducts = StoreFilter
                .getTopProducts(productSalesMap);

        Printer.printTopProducts(topProducts.getKey(), topProducts.getValue());
    }

    private static void createTable(String createQuery) throws SQLException {
        PreparedStatement createStatement = connection.prepareStatement(createQuery);
        createStatement.executeUpdate();
        createStatement.close();
    }

    @FunctionalInterface interface SQLConsumer<T> {
        void accept(T t) throws SQLException;
    }

    private static void executeSelectQuery(String selectQuery,
                                           SQLConsumer<? super ResultSet> action,
                                           Object... arguments) throws SQLException {
        PreparedStatement selectStatement = connection.prepareStatement(selectQuery);

        for (int i = 0; i < arguments.length; i++) {
            selectStatement.setObject(i + 1, arguments[i]);
        }

        ResultSet rs = selectStatement.executeQuery();

        action.accept(rs);

        selectStatement.close();
    }

    private static void executeInsertQuery(String insertQuery, Object... arguments) throws SQLException {
        PreparedStatement insertStatement = connection.prepareStatement(insertQuery);

        for (int i = 0; i < arguments.length; i++) {
            insertStatement.setObject(i + 1, arguments[i]);
        }

        insertStatement.executeUpdate();
        insertStatement.close();
    }

    private void ensureConnection() {
        if (!isOpen) {
            throw new IllegalStateException("The connection to the DB is closed");
        }
    }
}
