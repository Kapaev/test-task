package org.server.withdrawal

import org.server.dao.Dao
import org.server.model.Status
import org.server.processor.ProcessingService
import spock.lang.Specification
import spock.lang.Subject

import static org.server.withdrawal.WithdrawalService.*

class WithdrawalCreatorTest extends Specification {

    Dao dao = Mock(Dao)
    ProcessingService processingService = Mock(ProcessingService)

    @Subject
    WithdrawalCreator withdrawalCreator = new WithdrawalCreator(dao, processingService)

    def "should create a withdrawal request and add it for processing"() {
        given:
        UUID sourceUserID = UUID.randomUUID()
        Address withdrawalAddress = new Address("test_address")
        BigDecimal amount = new BigDecimal(100)

        when:
        UUID result = withdrawalCreator.createWithdrawal(sourceUserID, withdrawalAddress, amount)

        then:
        1 * dao.addWithdrawalRequest(_) >> { args ->
            def req = args[0]
            assert req.sourceUserID == sourceUserID
            assert req.amount == amount
            assert req.address == withdrawalAddress
            assert req.status == Status.PENDING
        }
        1 * processingService.putOperationForProcessing(_) >> { args ->
            def req = args[0]
            assert req.sourceUserID == sourceUserID
            assert req.amount == amount
            assert req.address == withdrawalAddress
            assert req.status == Status.PENDING
        }
        assert result instanceof UUID
    }
}
