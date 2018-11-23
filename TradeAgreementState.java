package java_bootcamp;

import com.google.common.collect.ImmutableList;
import net.corda.core.contracts.ContractState;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.List;

public class TradeAgreementState implements ContractState {

    private final Party importer;
    private final Party exporter;
    private final String agreementId;
    private final Double amount;
    private final String goods;
    private final LocalDate agreementDate;
    private final String status;

    public TradeAgreementState(Party importer, Party exporter, String agreementId, Double amount, String goods, LocalDate agreementDate, String status) {
        this.importer = importer;
        this.exporter = exporter;
        this.agreementId = agreementId;
        this.amount = amount;
        this.goods = goods;
        this.agreementDate = agreementDate;
        this.status = status;
    }

    public Party getImporter() {
        return importer;
    }

    public Party getExporter() {
        return exporter;
    }


    public String getAgreementId() {
        return agreementId;
    }

    public Double getAmount() {
        return amount;
    }


    public String getGoods() {
        return goods;
    }

    public LocalDate getAgreementDate() {
        return agreementDate;
    }


    public String getStatus() {
        return status;
    }


    @NotNull
    @Override
    public List<AbstractParty> getParticipants() {
        return ImmutableList.of(importer, exporter);
    }
}

//enum AgreementStatus{
//    CREATED,
//    ACCEPTED,
//    FINALISED,
//    TERMINATED
//}
