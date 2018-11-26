package java_bootcamp;

import com.google.common.collect.ImmutableList;
import net.corda.core.contracts.ContractState;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.List;

public class LetterOfCreditState implements ContractState {

    private final Party importer;
    private final Party exporter;
    private final Party importersBank;
    private final Party exportersBank;
    private final String letterOfCreditId;
    private final LocalDate issueDate;
    private final Double amount;
    private final LocalDate expiryDate;
    private String status;

    public LetterOfCreditState(Party importer, Party exporter, Party importersBank, Party exportersBank, String letterOfCreditId, LocalDate issueDate, Double amount, LocalDate expiryDate, String status) {
        this.importer = importer;
        this.exporter = exporter;
        this.importersBank = importersBank;
        this.exportersBank = exportersBank;
        this.letterOfCreditId = letterOfCreditId;
        this.issueDate = issueDate;
        this.amount = amount;
        this.expiryDate = expiryDate;
        this.status = status;
    }

    public Party getImporter() {
        return importer;
    }

    public Party getExporter() {
        return exporter;
    }

    public Party getImportersBank() {
        return importersBank;
    }

    public Party getExportersBank() {
        return exportersBank;
    }

    public String getLetterOfCreditId() {
        return letterOfCreditId;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public Double getAmount() {
        return amount;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @NotNull
    @Override
    public List<AbstractParty> getParticipants() {
        return ImmutableList.of(importer,exporter,importersBank,exportersBank);
    }
}
