# SimpleServer README

> **🚨 Note**: This documentation is autogenerated. Always refer to source code or official documentation for accurate details.

## Overview
`SimpleServer` is a lightweight HTTP server designed to handle financial transactions and withdrawal requests for users. Upon instantiation, the server initializes with two users, Bob and Alice, and binds to a specified port number. The server provides endpoints for transferring money, making withdrawal requests, and checking the status of operations.

## Initialization

To start a `SimpleServer` instance, provide a port number:
```java
SimpleServer server = new SimpleServer(8080);
```

## Features

- **In-memory Data Storage**: 
  - Uses an in-memory data access object (`InMemoryDao`) for user data storage.

- **Predefined Users**: 
  - **Bob**: 
    - UUID: `00000000-0000-0000-0000-000000000001`
    - Initial Balance: $1000
  - **Alice**: 
    - UUID: `00000000-0000-0000-0000-000000000002`
    - Initial Balance: $1000

- **Endpoints**: 
  - **/transfer**: 
    - Purpose: Handles transfer of funds between users.
  - **/withdraw**: 
    - Purpose: Manages withdrawal requests from a user's account.
  - **/status**: 
    - Purpose: Provides the status of a given operation.

- **Services**: 
  - **TransactionService**: Manages transaction logic.
  - **ValidationWithdrawalService**: Ensures withdrawal requests are valid.
  - **ProcessingService**: Orchestrates the processing of transactions and withdrawal requests.

- **Shutdown Hook**: 
  - Ensures graceful server termination when the JVM stops.
