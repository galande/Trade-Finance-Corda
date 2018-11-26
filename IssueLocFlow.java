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
public class IssueLocFlow extends FlowLogic<SignedTransaction> {

    private final String letterOfCreditId;

    public IssueLocFlow(String letterOfCreditId) {
        this.letterOfCreditId = letterOfCreditId;
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
        Party ourIdentity = getOurIdentity();

        QueryCriteria.VaultQueryCriteria criteria = new QueryCriteria.VaultQueryCriteria(Vault.StateStatus.UNCONSUMED);
        Vault.Page<LetterOfCreditState> result = getServiceHub().getVaultService().queryBy(LetterOfCreditState.class, criteria);

        if (result == null || result.getStates().size() == 0){
            throw new FlowException("Matching LOC not found.");
        }

        Iterator<StateAndRef<LetterOfCreditState>> locStates = result.getStates().iterator();
        LetterOfCreditState locState = null;
        StateAndRef<LetterOfCreditState> locStateAndRef = null;

        while (locStates.hasNext()){

            locStateAndRef = locStates.next();
            locState = locStateAndRef.getState().getData();

            if (locState.getLetterOfCreditId().equals(letterOfCreditId)){
                break;
            }
        }

        if (locStateAndRef == null){
            throw new FlowException("Matching LOC not Found.");
        }

        final String issueed = "ISSUED";
        LetterOfCreditState letterOfCreditState = new LetterOfCreditState(locState.getImporter(),locState.getExporter(),ourIdentity,locState.getExportersBank(),
                locState.getLetterOfCreditId(),locState.getIssueDate(),locState.getAmount(),locState.getExpiryDate(),issueed);

        LetterOfCreditContract.Commands command = new LetterOfCreditContract.Commands.accept();
        TransactionBuilder transactionBuilder = new TransactionBuilder();
        transactionBuilder.setNotary(notary);

        transactionBuilder.addInputState(locStateAndRef);
        transactionBuilder.addOutputState(letterOfCreditState, LetterOfCreditContract.ID);
        transactionBuilder.addCommand(command,ourIdentity.getOwningKey());

        transactionBuilder.verify(getServiceHub());

        SignedTransaction signedTransaction = getServiceHub().signInitialTransaction(transactionBuilder);

        return subFlow(new FinalityFlow(signedTransaction));
    }
}
