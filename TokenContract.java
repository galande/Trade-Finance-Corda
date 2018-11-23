package java_bootcamp;

import net.corda.core.contracts.Command;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.Contract;
import net.corda.core.contracts.ContractState;
import net.corda.core.identity.Party;
import net.corda.core.transactions.LedgerTransaction;

import java.security.PublicKey;
import java.util.List;

import static net.corda.core.contracts.ContractsDSL.requireThat;

/* Our contract, governing how our state will evolve over time.
 * See src/main/java/examples/ArtContract.java for an example. */
public class TokenContract implements Contract{
    public static String ID = "java_bootcamp.TokenContract";

    @Override
    public void verify(LedgerTransaction tx) throws IllegalArgumentException {

        if (tx.getInputStates().size() != 0)
            throw new IllegalArgumentException("Tx must have 0 Input.");
        if (tx.getOutputStates().size() != 1)
            throw new IllegalArgumentException("Tx must have 1 Output.");
        if (tx.getCommands().size() != 1)
            throw new IllegalArgumentException("Tx must have 1 command.");

        ContractState output = tx.getOutput(0);
        if (!(output instanceof TokenState))
            throw new IllegalArgumentException("Output must be Instance of Tokenstate.");

        TokenState outputState = (TokenState) tx.getOutput(0);
        if (outputState.getAmount() < 0)
            throw new IllegalArgumentException("Amount must be positive");
        Command command = tx.getCommand(0);
        if (!(command.getValue() instanceof Commands.Issue))
            throw new IllegalArgumentException("Command should be an IssueCommand");

        List<PublicKey> signers = command.getSigners();
        Party issuer = outputState.getIssuer();
        if (!(((List) signers).contains(issuer.getOwningKey())))
            throw new IllegalArgumentException("issuer must be required signer.");
    }

    public interface Commands extends CommandData {
        class Issue implements Commands { }
    }
}