package java_bootcamp;

import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.Contract;
import net.corda.core.transactions.LedgerTransaction;

public class BillOfLadingContract implements Contract {

    public static String ID = "java_bootcamp.BillOfLadingContract";
    @Override
    public void verify(LedgerTransaction tx) throws IllegalArgumentException {

    }

    public interface Commands extends CommandData{
        class create implements Commands{}
    }
}
