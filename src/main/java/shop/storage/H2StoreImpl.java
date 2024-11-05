package shop.storage;

import org.h2.tools.DeleteDbFiles;
import shop.Customer;
import shop.Order;
import shop.Product;
import shop.utils.Printer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// TODO: Task 2.2 c)
public class H2StoreImpl implements CustomerStore, CustomerStoreQuery {

    private static final String DB_DRIVER = "org.h2.Driver";
    private static final String DB_CONNECTION = "jdbc:h2:./data/shop/H2Store/h2Store";
    private static final String DB_USER = "";
    private static final String DB_PASSWORD = "";

    private static Connection connection;

    @Override
    public void open() {
        try {
            Class.forName(DB_DRIVER);
            connection = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD);
            createTables();
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println(e.getMessage());
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

    private void insertOrder(Order order, int customerId) throws SQLException{
        String insertOrder =
                "INSERT INTO ORDERS" + "(oid, customerId, shippingAddress) values" + "(?,?,?)";

        executeInsertQuery(insertOrder,
                order.getOrderId(), customerId, order.getShippingAddress());

        for (Product product : order.getItems()) {
            insertProduct(product);
            insertOrderItem(order.getOrderId(), product.getProductId());
        }
    }

    private void insertProduct(Product product) throws SQLException{
        String insertProduct =
                "MERGE INTO PRODUCTS" + "(pid, pname) values" + "(?,?)";

        executeInsertQuery(insertProduct,
                product.getProductId(), product.getName());
    }

    private void insertOrderItem(int orderId, int productId) throws SQLException{
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
        }
    }

    @Override
    public void cleanUp() {
        DeleteDbFiles.execute("./data/shop/H2Store", "h2Store", true);
    }

    @Override
    public void queryAllUsers() {
        List<Customer> customerList = new ArrayList<>();

        String selectQuery =
                "SELECT * FROM CUSTOMERS";

        try (ResultSet resultSet = executeSelectQuery(selectQuery)){
            while(resultSet.next()) {
                int customerId = resultSet.getInt("id");
                String userName = resultSet.getString("name");
                String address = "";

                Customer customer = new Customer(customerId, userName, address);

                List<Order> orderList = queryOrdersByUser(customerId);
                for (Order order : orderList) {
                    customer.addOrder(order);
                }

                customerList.add(customer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for (Customer customer : customerList) {
            Printer.print(0, customer);
        }
    }

    private List<Order> queryOrdersByUser(int customerId) {
        List<Order> orderList = new ArrayList<>();

        String selectQuery =
                "SELECT * FROM ORDERS WHERE customerId = ?";

        try (ResultSet resultSet = executeSelectQuery(selectQuery, customerId)) {
            while(resultSet.next()) {
                int orderId = resultSet.getInt("oid");
                String shippingAddress = resultSet.getString("shippingAddress");
                List<Product> productList = queryProductsByOrder(orderId);

                Order order = new Order(orderId, shippingAddress, productList);
                orderList.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return orderList;
    }

    private List<Product> queryProductsByOrder(int orderId) {
        List<Integer> productIdList = new ArrayList<>();
        List<Product> productList = new ArrayList<>();

        String selectOrderItemQuery =
                "SELECT * FROM ORDERITEMS WHERE orderId = ?";

        try (ResultSet resultSet = executeSelectQuery(selectOrderItemQuery, orderId)) {
            while(resultSet.next()) {
                int productId = resultSet.getInt("productId");
                productIdList.add(productId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String selectProductQuery =
                "SELECT * FROM PRODUCTS WHERE pid = ?";

        for (Integer productId : productIdList) {
            try (ResultSet resultSet = executeSelectQuery(selectProductQuery, productId)) {
                while(resultSet.next()) {
                    String productName = resultSet.getString("pname");
                    productList.add(new Product(productId, productName));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return productList;
    }

    @Override
    public void queryTopProduct() {
    }

    private static void createTable(String createQuery) throws SQLException {
        PreparedStatement createStatement = connection.prepareStatement(createQuery);
        createStatement.executeUpdate();
        createStatement.close();
    }

    @FunctionalInterface interface SQLConsumer<T> {
        public void accept(T t) throws SQLException;
    }

    private static ResultSet executeSelectQuery(String selectQuery, Object... arguments) throws SQLException {
        PreparedStatement selectStatement = connection.prepareStatement(selectQuery);

        for (int i = 0; i < arguments.length; i++) {
            selectStatement.setObject(i + 1, arguments[i]);
        }

        ResultSet resultSet = selectStatement.executeQuery();

//        selectStatement.close();

        return resultSet;
    }

    private static void executeInsertQuery(String insertQuery, Object... arguments) throws SQLException {
        PreparedStatement insertStatement = connection.prepareStatement(insertQuery);

        for (int i = 0; i < arguments.length; i++) {
            insertStatement.setObject(i + 1, arguments[i]);
        }

        insertStatement.executeUpdate();
        insertStatement.close();
    }
}
