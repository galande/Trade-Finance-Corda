package java_bootcamp;


import net.corda.core.contracts.Command;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.Contract;
import net.corda.core.transactions.LedgerTransaction;
import org.jetbrains.annotations.NotNull;
import static net.corda.core.contracts.ContractsDSL.requireThat;

public class TradeAgreementContract implements Contract {
    public static String ID = "java_bootcamp.TradeAgreementContract";

    @Override
    public void verify(@NotNull LedgerTransaction tx) throws IllegalArgumentException {
        Command command = tx.getCommand(0);
        if (command.getValue() instanceof Commands.create){
            requireThat( require -> {

                require.using( "Agreement must have no Input.",
                        tx.getInputs().isEmpty() );
                require.using( "Only one output state should be created.",
                        tx.getOutputs().size() == 1 );

                require.using("Tx must have 1 command", tx.getCommands().size() == 1);

                TradeAgreementState output = (TradeAgreementState) tx.getOutput(0);
                require.using("Importer and Exporter parties must be different",
                        output.getImporter() != output.getExporter());

                require.using("Amount must be greater than 0.", output.getAmount() > 0);


                require.using("Tx command must be create", command.getValue() instanceof Commands.create);

                require.using("Importer must be required signer",tx.getCommand(0).getSigners().contains(output.getImporter().getOwningKey()) );
                return null;
            } );

            if (command.getValue() instanceof Commands.accept){

                requireThat(require -> {

                    require.using("Tx must have 1 Input.", tx.getInputStates().size() != 1);
                    require.using("Tx must have 1 output",tx.getOutputStates().size() != 1);

                    TradeAgreementState output = (TradeAgreementState) tx.getOutput(0);
                    require.using("Expoter is required signer.", tx.getCommand(0).getSigners().contains(output.getExporter().getOwningKey()));
                    require.using("Status must be Accepted.", output.getStatus() == "ACCEPTED");
                    return null;
                });
            }
        }
    }

    public interface Commands extends CommandData{
        class create implements Commands{}
        class accept implements Commands{}
    }
}
