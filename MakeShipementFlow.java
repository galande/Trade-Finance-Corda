package java_bootcamp;

import net.corda.core.contracts.StateAndRef;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.InitiatingFlow;
import net.corda.core.flows.StartableByRPC;
import net.corda.core.identity.Party;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.QueryCriteria;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.utilities.ProgressTracker;

import java.util.Iterator;

@InitiatingFlow
@StartableByRPC
public class MakeShipementFlow extends FlowLogic<SignedTransaction> {

    private final String billOfLadingId;
    private final String letterOfCreditId;

    public MakeShipementFlow(String billOfLadingId, String letterOfCreditId) {
        this.billOfLadingId = billOfLadingId;
        this.letterOfCreditId = letterOfCreditId;
    }

    private final ProgressTracker progressTracker = new ProgressTracker();

    @Override
    public ProgressTracker getProgressTracker() {
        return progressTracker;
    }

    @Override
    public SignedTransaction call() throws FlowException {
        Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);
        Party ourIdentity = getOurIdentity();

        QueryCriteria.VaultQueryCriteria criteria = new QueryCriteria.VaultQueryCriteria(Vault.StateStatus.UNCONSUMED);
        Vault.Page<LetterOfCreditState> result = getServiceHub().getVaultService().queryBy(LetterOfCreditState.class,criteria);

        if (result == null || result.getStates().size() == 0){
            throw new FlowException("No Matching LOC record found.");
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
            throw new FlowException("Provided LOC not found.");
        }

        Vault.Page<BillOfLadingState> bolResult = getServiceHub().getVaultService().queryBy(BillOfLadingState.class,criteria);
        if (result == null || result.getStates().size() == 0){
            throw new FlowException("No Matching BOL record found.");
        }

        Iterator<StateAndRef<BillOfLadingState>> bolStates = bolResult.getStates().iterator();

        BillOfLadingState bolState = null;
        StateAndRef<BillOfLadingState> bolStateAndRef = null;

        while (bolStates.hasNext()){
            bolStateAndRef = bolStates.next();
            bolState = bolStateAndRef.getState().getData();

            if (bolState.getBillOfLadingId().equals(billOfLadingId)){
                break;
            }
        }

        if (locStateAndRef == null){
            throw new FlowException("Provided BOL not found.");
        }


        return null;
    }
}
