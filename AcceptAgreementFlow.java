package java_bootcamp;

import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.QueryCriteria;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;

import java.util.Iterator;

@InitiatingFlow
@StartableByRPC
public class AcceptAgreementFlow extends FlowLogic<SignedTransaction> {

    private final String agreementId;

    public AcceptAgreementFlow(String agreementId) {
        this.agreementId = agreementId;
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
        Party exporter = getOurIdentity();

        final String accepted = "ACCEPTED";

        QueryCriteria.VaultQueryCriteria criteria = new QueryCriteria.VaultQueryCriteria(Vault.StateStatus.UNCONSUMED);
        Vault.Page<TradeAgreementState> result = getServiceHub().getVaultService().queryBy(TradeAgreementState.class, criteria);

        if (result == null || result.getStates().size() == 0){
            throw new FlowException("No Matching record found.");
        }

        Iterator<StateAndRef<TradeAgreementState>> agreementStates = result.getStates().iterator();

        TradeAgreementState agreementState = null;
        StateAndRef<TradeAgreementState> agreementStateStateAndRef = null;
        while (agreementStates.hasNext()){
            agreementStateStateAndRef = agreementStates.next();
            agreementState = agreementStateStateAndRef.getState().getData();

            if (agreementState.getAgreementId().equals(agreementId)){
                break;
            }
        }

        if (agreementStateStateAndRef == null){
            throw new FlowException("No matching agreement found.");
        }

        TradeAgreementState tradeAgreementState = new TradeAgreementState(agreementState.getImporter(),agreementState.getExporter(),
                agreementState.getAgreementId(),agreementState.getAmount(),agreementState.getGoods(),
                agreementState.getAgreementDate(),accepted);

        TransactionBuilder transactionBuilder = new TransactionBuilder();
        transactionBuilder.setNotary(notary);

        transactionBuilder.addInputState(agreementStateStateAndRef);

        transactionBuilder.addOutputState(tradeAgreementState,TradeAgreementContract.ID);
        TradeAgreementContract.Commands command = new TradeAgreementContract.Commands.accept();

        transactionBuilder.addCommand(command,agreementState.getExporter().getOwningKey());
        transactionBuilder.verify(getServiceHub());

        SignedTransaction signedTransaction = getServiceHub().signInitialTransaction(transactionBuilder);

        return subFlow(new FinalityFlow(signedTransaction));
    }
}
