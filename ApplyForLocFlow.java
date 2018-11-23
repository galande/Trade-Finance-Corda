package java_bootcamp;

import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.contracts.Command;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;

import java.time.LocalDate;

@StartableByRPC
@InitiatingFlow
public class ApplyForLocFlow extends FlowLogic<SignedTransaction> {

    private final Party exporter;
    private final Party importersBank;
    private final Party exportersBank;
    private final String letterOfCreditId;
    private final LocalDate issueDate;
    private final Double amount;
    private final LocalDate expiryDate;

    public ApplyForLocFlow(Party exporter, Party importersBank, Party exportersBank, String letterOfCreditId, LocalDate issueDate, Double amount, LocalDate expiryDate) {
        this.exporter = exporter;
        this.importersBank = importersBank;
        this.exportersBank = exportersBank;
        this.letterOfCreditId = letterOfCreditId;
        this.issueDate = issueDate;
        this.amount = amount;
        this.expiryDate = expiryDate;
    }

    @Suspendable
    @Override
    public SignedTransaction call() throws FlowException {

        Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);
        Party importer = getOurIdentity();

        final String status = "APPLIED";

        LetterOfCreditState letterOfCreditState = new LetterOfCreditState(importer,exporter,importersBank,exportersBank,
                letterOfCreditId,issueDate,amount,expiryDate,status);

        TransactionBuilder transactionBuilder = new TransactionBuilder();
        transactionBuilder.setNotary(notary);

        LetterOfCreditContract.Commands command = new LetterOfCreditContract.Commands.create();
        transactionBuilder.addOutputState(letterOfCreditState,LetterOfCreditContract.ID)
                .addCommand(command,importer.getOwningKey());

        transactionBuilder.verify(getServiceHub());

        SignedTransaction signedTransaction = getServiceHub().signInitialTransaction(transactionBuilder);

        return subFlow(new FinalityFlow(signedTransaction));
    }
}
