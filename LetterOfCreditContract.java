package java_bootcamp;

import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.Contract;
import net.corda.core.transactions.LedgerTransaction;
import org.jetbrains.annotations.NotNull;

public class LetterOfCreditContract implements Contract {

    public  static String ID = "java_bootcamp.LetterOfCreditContract";

    @Override
    public void verify(@NotNull LedgerTransaction tx) throws IllegalArgumentException {

    }

    public interface Commands extends CommandData {
        class create implements Commands {}
        class accept implements Commands {}
    }
}
