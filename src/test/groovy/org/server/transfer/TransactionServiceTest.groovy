package org.server.transfer

import org.server.dao.Dao
import org.server.model.Status
import org.server.model.Transaction
import org.server.model.User
import spock.lang.Specification

class TransactionServiceTest extends Specification {

    Dao dao = Mock(Dao)
    TransactionService transactionService = new TransactionService(dao)

    def "should fail the transaction if source or destination user does not exist"() {
        given:
        Transaction transaction = new Transaction(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), new BigDecimal("100.00"), Status.PENDING)

        when:
        transactionService.accept(transaction)

        then:
        1 * dao.getUser(transaction.getSourceUserID()) >> null
        1 * dao.getUser(transaction.getDestinationUserID())
        0 * dao.processTransaction(_, _, _)
        transaction.getStatus() == Status.FAILED
    }

    def "should fail the transaction if source user balance is insufficient"() {
        given:
        User sourceUser = new User("source", new BigDecimal(50.0))
        User destinationUser = new User("destination", new BigDecimal(150.00))
        Transaction transaction = new Transaction(UUID.randomUUID(), sourceUser.getUserID(), destinationUser.getUserID(), new BigDecimal(100.00), Status.PENDING)

        when:
        transactionService.accept(transaction)

        then:
        1 * dao.getUser(transaction.getSourceUserID()) >> sourceUser
        1 * dao.getUser(transaction.getDestinationUserID()) >> destinationUser
        0 * dao.processTransaction(_, _, _)
        transaction.getStatus() == Status.FAILED
    }

    def "should process the transaction successfully if all conditions are met"() {
        given:
        User sourceUser = new User("source", new BigDecimal(500.0))
        User destinationUser = new User("destination", new BigDecimal(150.00))
        Transaction transaction = new Transaction(UUID.randomUUID(), sourceUser.getUserID(), destinationUser.getUserID(),
                new BigDecimal(100.00), Status.PENDING)

        when:
        transactionService.accept(transaction)

        then:
        1 * dao.getUser(transaction.getSourceUserID()) >> sourceUser
        1 * dao.getUser(transaction.getDestinationUserID()) >> destinationUser
        1 * dao.processTransaction(sourceUser, destinationUser, transaction.getAmount()) >> Status.COMPLETED
        transaction.getStatus() == Status.COMPLETED
    }
}
