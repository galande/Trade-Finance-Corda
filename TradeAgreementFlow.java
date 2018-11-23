package java_bootcamp;

import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;

import java.time.LocalDate;

@InitiatingFlow
@StartableByRPC
public class TradeAgreementFlow extends FlowLogic<SignedTransaction> {

    private final Party exporter;
    private final String agreementId;
    private final Double amount;
    private final String goods;
    private final LocalDate agreementDate;

    public TradeAgreementFlow(Party exporter, String agreementId, Double amount, String goods, LocalDate agreementDate) {
        this.exporter = exporter;
        this.agreementId = agreementId;
        this.amount = amount;
        this.goods = goods;
        this.agreementDate = agreementDate;
    }

    private final ProgressTracker progressTracker = new ProgressTracker();

    @Override
    public ProgressTracker getProgressTracker() {
        return progressTracker;
    }

    @Suspendable
    @Override
    public SignedTransaction call() throws FlowException {

        Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);
        Party importer = getOurIdentity();

        final String created = "CREATED";
        TradeAgreementState tradeAgreementState = new TradeAgreementState(importer,exporter,agreementId,amount,goods,agreementDate,created);

        TransactionBuilder transactionBuilder = new TransactionBuilder();
        transactionBuilder.setNotary(notary);

        transactionBuilder.addOutputState(tradeAgreementState, TradeAgreementContract.ID);

        TradeAgreementContract.Commands command = new TradeAgreementContract.Commands.create();
        transactionBuilder.addCommand(command,importer.getOwningKey());

        transactionBuilder.verify(getServiceHub());

        SignedTransaction signedTransaction = getServiceHub().signInitialTransaction(transactionBuilder);

        return subFlow(new FinalityFlow(signedTransaction));
    }
}
