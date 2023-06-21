# Alex Zhang's EStore Implementation

## Getting Started

1. First cd into the project directory
2. Using blow command to start application:
   ```apache
   ./mvnw spring-boot:run
   ```
3. Using below command to run all test cases:
   ```apache
   ./mvnw test
   ```

## Table Design

Per schema.sql under resource folder:


| Table Name | Description                                                                                                                                              |
| ---------- | -------------------------------------------------------------------------------------------------------------------------------------------------------- |
| product    | Store products. If a product is no longer valid, it will be removed from table.<br />The product shall be provided by the invoker and needs to be unique |
| discount   | Store discount related info. One product only allows max to 1 discount record                                                                            |
| account    | Store account info                                                                                                                                       |
| Cart       | Store cart item(product+count) for each user                                                                                                             |

## Description

### Account

1. Users are categorized into two types: admin and retail user
2. Only admin can create/remove products and add/remove discount
3. Ony retail user can add/remove product into his/her own cart
4. At present, 1 admin(uid: 100001) and 1 retail user(uid: 100002) are pre-initialized

### Product

1. The admin must provide an unique product id while adding a new product
2. The product with the same product id could only be inserted once
3. While removing a product, if the product is still in customer's cart, it could be removed
4. After a product is removed, the discount associated with the product shall be removed accordingly
5. Admin can query one product or all products
6. There is a permission check to validate whether the operator is admin or not

### Discount

1. Before adding a discount, the product shall exist
2. One product only allow one discount so that we could avoid discount conflicts
3. Admin is able to specify discount strategy, at present two strategies supported:

| BUY_N_GET_LAST_DISCOUNT  | if the count of product >=`triggerThreshold`,`discountPercentage` would be applied at the last product  |
| ------------------------ | ---------------------------------------------------------------------------------------------------------- |
| BUY_N_GET_TOTAL_DISCOUNT | if the count of product >=`triggerThreshold`,`discountPercentage` would be applied at the total amount   |

4. If removing a discount, will also check the product id to see whether it's valid or not
5. There is a permission check for all write operations of discount. If it's a pure read, permission is ignored

### Cart
1. By calling modify, user can add/update/delete cart item
2. The count inside ModifyCartReq indicated the total count of that product, not the delta
3. If the count is 0, it means that user wants to delete it from his/her cart
4. There would be a lock added whiling modifying the cart because there may be a race condition.
For example, for the same product, user adds/removed simultaneously
5. By calling checkout, user is able to view how much he/she will pay and discount he/she could enjoy.
Other than the total numbers, he/she is also able to view statistic(price after discount,discount,original price) for each product
 