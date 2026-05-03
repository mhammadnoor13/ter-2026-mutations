import experiment.subjects.WithdrawalLimiter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

    public class WithdrawalLimiterTest {

    @Test
    void testWithdrawKeepsBalanceInAllowedRange() {
        WithdrawalLimiter limiter = new WithdrawalLimiter();

        limiter.deposit(1000.0);
        double balance = limiter.withdraw(900.0, false);

        assertTrue(balance >= -100.0);
        assertTrue(balance <= 1000.0);
    }
}
