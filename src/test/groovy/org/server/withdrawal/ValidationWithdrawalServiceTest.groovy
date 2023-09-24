package org.server.withdrawal

import org.server.dao.Dao
import org.server.model.Status
import org.server.model.User
import spock.lang.Specification

import static java.util.UUID.*
import static org.server.withdrawal.WithdrawalService.*

class ValidationWithdrawalServiceTest extends Specification {

    Dao dao = Mock(Dao)
    TestableValidationWithdrawalService service = new TestableValidationWithdrawalService(dao)


    def "should set status to FAILED if user is not found"() {
        given:
        WithdrawalRequest request = new WithdrawalRequest(randomUUID(), new WithdrawalId(randomUUID()), new BigDecimal(100), new Address("test"), Status.PENDING)
        dao.getUser(_) >> null

        when:
        service.accept(request)

        then:
        request.getStatus() == Status.FAILED
    }

    def "should set status to FAILED if user balance is insufficient"() {
        given:
        User user = new User("John", new BigDecimal(50))
        WithdrawalRequest request = new WithdrawalRequest(user.getUserID(), new WithdrawalId(randomUUID()), new BigDecimal(100), new Address("test"), Status.PENDING)
        dao.getUser(_) >> user

        when:
        service.accept(request)

        then:
        request.getStatus() == Status.FAILED
    }

    def "should wait FAILED final status"() {
        given:
        def user = new User("John", new BigDecimal(150))
        def withdrawalId = new WithdrawalId(randomUUID())
        def request = new WithdrawalRequest(user.getUserID(), withdrawalId, new BigDecimal(100), new Address("test"), Status.PENDING)
        dao.getUser(_) >> user
        service.requestWithdrawal(_, _, _) >> {}
        service.getRequestState(_) >> WithdrawalState.FAILED

        when:
        service.accept(request)

        then:
        1 * dao.subtractBalance(user, request.getAmount())
        1 * dao.addBalance(user, request.getAmount())
    }

    def "should wait COMPLETED final status"() {
        given:
        def user = new User("John", new BigDecimal(150))
        def withdrawalId = new WithdrawalId(randomUUID())
        def request = new WithdrawalRequest(user.getUserID(), withdrawalId, new BigDecimal(100), new Address("test"), Status.PENDING)
        dao.getUser(_) >> user
        service.requestWithdrawal(_, _, _) >> {}
        service.getRequestState(_) >> WithdrawalState.COMPLETED

        when:
        service.accept(request)

        then:
        1 * dao.subtractBalance(user, request.getAmount())
    }

    class TestableValidationWithdrawalService extends ValidationWithdrawalService {

        WithdrawalState withdrawalState = WithdrawalState.PROCESSING

        TestableValidationWithdrawalService(Dao dao) {
            super(dao)
        }

        @Override
        WithdrawalState getRequestState(WithdrawalId id) {
            return withdrawalState
        }
    }
}
