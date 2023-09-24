package org.server.withdrawal;

import org.server.dao.Dao;
import org.server.model.Status;
import org.server.model.User;

import java.util.function.Consumer;

public class ValidationWithdrawalService extends WithdrawalServiceStub implements Consumer<WithdrawalRequest>  {

    private final Dao dao;

    public ValidationWithdrawalService(Dao dao) {
        super();
        this.dao = dao;
    }

    @Override
    public void accept(WithdrawalRequest request) {
        User sourceUser = dao.getUser(request.getWithdrawalId().value());

        if (sourceUser == null) {
            request.setStatus(Status.FAILED);
            return;
        }
        if (sourceUser.getBalance().compareTo(request.getAmount()) < 0) {
            request.setStatus(Status.FAILED);
            return;
        }

        dao.subtractBalance(sourceUser, request.getAmount());
        requestWithdrawal(request.getWithdrawalId(), request.getAddress(), request.getAmount());

        //wait to decide on final status
        int tries = 0;
        while (getRequestState(request.getWithdrawalId()) == WithdrawalState.PROCESSING) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (tries++ > 15) {
                break;
            }
        }

        if (getRequestState(request.getWithdrawalId()) == WithdrawalState.FAILED
                || getRequestState(request.getWithdrawalId()) == WithdrawalState.PROCESSING) {
            request.setStatus(Status.FAILED);
            dao.addBalance(sourceUser, request.getAmount());
        } else {
            request.setStatus(Status.COMPLETED);
        }
    }
}
