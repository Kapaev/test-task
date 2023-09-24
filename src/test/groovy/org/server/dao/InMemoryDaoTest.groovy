package org.server.dao

import org.server.model.Status
import org.server.model.Transaction
import org.server.model.User
import spock.lang.Specification

class InMemoryDaoTest extends Specification {

    InMemoryDao dao = new InMemoryDao()

    def "should add and retrieve a user"() {
        given:
        UUID userId = UUID.randomUUID()
        User user = new User("Name", new BigDecimal(1000.0))
        user.setUserID(userId) 

        when:
        dao.addUser(user)

        then:
        dao.getUser(userId) == user
    }

    def "should add and retrieve a transaction"() {
        given:
        def transactionId = UUID.randomUUID()
        Transaction transaction = new Transaction(transactionId, UUID.randomUUID(), UUID.randomUUID(), new BigDecimal(100.0), Status.PENDING )

        when:
        dao.addTransaction(transaction)

        then:
        dao.getTransaction(transactionId) == transaction
    }

    def "should retrieve transaction status"() {
        given:
        def transactionId = UUID.randomUUID()
        Transaction transaction = new Transaction(transactionId, UUID.randomUUID(), UUID.randomUUID(), new BigDecimal(100.0), Status.PENDING )
        transaction.setStatus(Status.FAILED)
        dao.addTransaction(transaction)

        when:
        Status status = dao.getTransactionStatus(transactionId)

        then:
        status == Status.FAILED
    }

    def "should process a transaction between users"() {
        given:
        User sourceUser = new User("user1", new BigDecimal(100.0))
        User destinationUser = new User("user2", new BigDecimal(100.0))

        BigDecimal amount = new BigDecimal(50.0)

        when:
        Status status = dao.processTransaction(sourceUser, destinationUser, amount)

        then:
        status == Status.COMPLETED
        sourceUser.getBalance() == new BigDecimal(50)
        destinationUser.getBalance() == new BigDecimal(150)
    }

    def "should subtract amount from source user"() {
        given:
        User sourceUser = new User("user1", new BigDecimal(100.0))
        BigDecimal amount = new BigDecimal(50.0)

        when:
        dao.subtractBalance(sourceUser, amount)

        then:
        sourceUser.getBalance() == new BigDecimal(50)
    }

    def "should add amount to destination user"() {
        given:
        User destinationUser = new User("user2", new BigDecimal(100.0))
        BigDecimal amount = new BigDecimal(50.0)

        when:
        dao.addBalance(destinationUser, amount)

        then:
        destinationUser.getBalance() == new BigDecimal(150)
    }


}
