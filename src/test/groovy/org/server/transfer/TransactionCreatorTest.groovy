package org.server.transfer

import org.server.dao.Dao
import org.server.model.Status
import org.server.model.Transaction
import org.server.processor.ProcessingService
import spock.lang.Specification

class TransactionCreatorTest extends Specification {

    Dao dao = Mock(Dao)
    ProcessingService processingService = Mock(ProcessingService)
    TransactionCreator transactionCreator = new TransactionCreator(dao, processingService)

    def "should create a transaction and put it for processing"() {
        given:
        UUID sourceUserID = UUID.randomUUID()
        UUID destinationUserID = UUID.randomUUID()
        BigDecimal amount = new BigDecimal("100.00")

        when:
        UUID transactionId = transactionCreator.createTransaction(sourceUserID, destinationUserID, amount)

        then:
        1 * dao.addTransaction(_) >> { Transaction t ->
            assert t.sourceUserID == sourceUserID
            assert t.destinationUserID == destinationUserID
            assert t.amount == amount
            assert t.status == Status.PENDING
        }

        1 * processingService.putOperationForProcessing(_) >> { Transaction t ->
            assert t.sourceUserID == sourceUserID
            assert t.destinationUserID == destinationUserID
            assert t.amount == amount
            assert t.status == Status.PENDING
        }

        transactionId != null
    }
}

