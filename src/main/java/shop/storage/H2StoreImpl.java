package shop.storage;

import shop.Customer;

import java.sql.*;

// TODO: Task 2.2 c)
public class H2StoreImpl implements CustomerStore, CustomerStoreQuery {
    @Override
    public void open() {
        // TODO
    }

    private static void createTables() throws SQLException {
        // TODO

        String createCustomer =
                "CREATE TABLE CUSTOMERS(id int primary key, name varchar(255))";
        String createOrder =
                "CREATE TABLE ORDERS(oid int primary key, customerId int, shippingAddress varchar(255))";
        String createProduct =
                "CREATE TABLE PRODUCTS(pid int primary key, pname varchar(255))";
        String createOrderItem =
                "CREATE TABLE ORDERITEMS(otid int auto_increment, orderId int, productId int)";
    }

    @Override
    public void insertCustomer(Customer customer) {
        // TODO
    }

    @Override
    public void close() {
        // TODO
    }

    @Override
    public void cleanUp() {
        // TODO
    }

    @Override
    public void queryAllUsers() {
        // TODO
    }

    @Override
    public void queryTopProduct() {
        // TODO
    }
}
