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

import java.time.Instant;
import java.time.LocalDate;
import java.util.Iterator;

@StartableByRPC
@InitiatingFlow
public class CreateBolFlow extends FlowLogic<SignedTransaction> {

    private final Party importer;
    private final Party exporter;
    private final Party importersBank;
    private final Party exportersBank;
    private final Party shipper;
    private final String billOfLadingId;
    private final String descriptionOfGoods;
    private final String portOfLoading;
    private final String portOfDischarge;

    private final String letterOfCreditId;

    public CreateBolFlow(Party importer, Party exporter, Party importersBank, Party exportersBank, Party shipper, String billOfLadingId, String descriptionOfGoods, String portOfLoading, String portOfDischarge, String letterOfCreditId) {
        this.importer = importer;
        this.exporter = exporter;
        this.importersBank = importersBank;
        this.exportersBank = exportersBank;
        this.shipper = shipper;
        this.billOfLadingId = billOfLadingId;
        this.descriptionOfGoods = descriptionOfGoods;
        this.portOfLoading = portOfLoading;
        this.portOfDischarge = portOfDischarge;
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
        Vault.Page<LetterOfCreditState> result = getServiceHub().getVaultService().queryBy(LetterOfCreditState.class,criteria);

        if (result == null || result.getStates().size() == 0){
            throw new FlowException("No Matching record found.");
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

        final String ladedState = "LADED";

        LetterOfCreditState outputLocState = new LetterOfCreditState(locState.getImporter(),locState.getExporter(),
                locState.getImportersBank(),locState.getExportersBank(),locState.getLetterOfCreditId(),
                locState.getIssueDate(),locState.getAmount(),locState.getExpiryDate(),ladedState);

        BillOfLadingState billOfLadingState = new BillOfLadingState(ourIdentity,importer,exporter,importersBank,exportersBank,shipper,Instant.now(),
                billOfLadingId,descriptionOfGoods,portOfLoading,portOfDischarge,LocalDate.now());

        TransactionBuilder transactionBuilder = new TransactionBuilder();
        transactionBuilder.setNotary(notary);

        transactionBuilder.addInputState(locStateAndRef);
        transactionBuilder.addOutputState(outputLocState, LetterOfCreditContract.ID);
        transactionBuilder.addOutputState(billOfLadingState,BillOfLadingContract.ID);

        BillOfLadingContract.Commands commands = new BillOfLadingContract.Commands.create();

        transactionBuilder.addCommand(commands, ourIdentity.getOwningKey());

        transactionBuilder.verify(getServiceHub());

        SignedTransaction signedTransaction = getServiceHub().signInitialTransaction(transactionBuilder);

        return subFlow(new FinalityFlow(signedTransaction));
    }
}
