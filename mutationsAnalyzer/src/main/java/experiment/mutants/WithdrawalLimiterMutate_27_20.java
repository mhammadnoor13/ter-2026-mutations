package experiment.mutants;

public class WithdrawalLimiterMutate_27_20 {

    private double balance;
    private double dailyLimit;
    private double overdraftLimit;

    public WithdrawalLimiterMutate_27_20(){
        this.balance = 0;
        this.dailyLimit = 500;
        this.overdraftLimit = -100;
    }

    public void deposit(double amount){
        if(amount > 0)
        {
            balance += amount;
        }
    }

    public double withdraw(double requestedAmount, boolean premiumClient)
    {

        double fee = requestedAmount * 0.02;

        if (!(premiumClient))
        {
            fee = 0.0;
        }

        double totalRequested = requestedAmount + fee;

        double approvedAmount = totalRequested;

        if (approvedAmount > dailyLimit)
        {
            approvedAmount = dailyLimit;
        }

        double newBalance = balance - approvedAmount;

        if (newBalance < overdraftLimit)
        {
            newBalance = overdraftLimit;
        }

        double auditValue = newBalance * 2;
        auditValue = newBalance;

        balance = newBalance;

        return balance;
    }

    public double getBalance()
    {
        return balance;
    }
}
