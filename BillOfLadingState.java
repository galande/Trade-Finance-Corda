package java_bootcamp;

import com.google.common.collect.ImmutableList;
import net.corda.core.contracts.ContractState;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public class BillOfLadingState implements ContractState {

    private final Party currentOwner;
    private final Party importer;
    private final Party exporter;
    private final Party importersBank;
    private final Party exportersBank;
    private final Party shipper;
    private final Instant updateTimestamp;
    private final String billOfLadingId;
    private final String descriptionOfGoods;
    private final String portOfLoading;
    private final String portOfDischarge;
    private final LocalDate dateOfShipment;

    public BillOfLadingState(Party currentOwner, Party importer, Party exporter, Party importersBank, Party exportersBank, Party shipper, Instant updateTimestamp, String billOfLadingId, String descriptionOfGoods, String portOfLoading, String portOfDischarge, LocalDate dateOfShipment) {
        this.currentOwner = currentOwner;
        this.importer = importer;
        this.exporter = exporter;
        this.importersBank = importersBank;
        this.exportersBank = exportersBank;
        this.shipper = shipper;
        this.updateTimestamp = updateTimestamp;
        this.billOfLadingId = billOfLadingId;
        this.descriptionOfGoods = descriptionOfGoods;
        this.portOfLoading = portOfLoading;
        this.portOfDischarge = portOfDischarge;
        this.dateOfShipment = dateOfShipment;
    }

    public Party getCurrentOwner() {
        return currentOwner;
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

    public Party getShipper() {
        return shipper;
    }

    public Instant getUpdateTimestamp() {
        return updateTimestamp;
    }

    public String getBillOfLadingId() {
        return billOfLadingId;
    }

    public String getDescriptionOfGoods() {
        return descriptionOfGoods;
    }

    public String getPortOfLoading() {
        return portOfLoading;
    }

    public String getPortOfDischarge() {
        return portOfDischarge;
    }

    public LocalDate getDateOfShipment() {
        return dateOfShipment;
    }

    @NotNull
    @Override
    public List<AbstractParty> getParticipants() {
        return ImmutableList.of(importer, exporter, importersBank, exportersBank);
    }
}
