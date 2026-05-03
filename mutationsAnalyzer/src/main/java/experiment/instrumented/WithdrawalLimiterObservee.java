package experiment.instrumented;

public class WithdrawalLimiterObservee {

    private double balance;
    private double dailyLimit;
    private double overdraftLimit;

    public WithdrawalLimiterObservee(){
        this.balance = 0;
        this.dailyLimit = 500;
        this.overdraftLimit = -100;
    }

    public void deposit(double amount){
        if(amount > 0)
        {
            balance += amount;
            core.MutationAnalyser.getInstance().setCurrentCode("balance += amount;");
            core.MutationAnalyser.getInstance().addNewVariableObservee(balance, "balance");
        }
    }

    public double withdraw(double requestedAmount, boolean premiumClient)
    {

        double fee = requestedAmount * 0.02;
        core.MutationAnalyser.getInstance().setCurrentCode("double fee = requestedAmount * 0.02;");
        core.MutationAnalyser.getInstance().addNewVariableObservee(fee, "fee");

        if (premiumClient)
        {
            fee = 0.0;
            core.MutationAnalyser.getInstance().setCurrentCode("fee = 0.0;");
            core.MutationAnalyser.getInstance().addNewVariableObservee(fee, "fee");
        }

        double totalRequested = requestedAmount + fee;
        core.MutationAnalyser.getInstance().setCurrentCode("double totalRequested = requestedAmount + fee;");
        core.MutationAnalyser.getInstance().addNewVariableObservee(totalRequested, "totalRequested");

        double approvedAmount = totalRequested;
        core.MutationAnalyser.getInstance().setCurrentCode("double approvedAmount = totalRequested;");
        core.MutationAnalyser.getInstance().addNewVariableObservee(approvedAmount, "approvedAmount");

        if (approvedAmount > dailyLimit)
        {
            approvedAmount = dailyLimit;
            core.MutationAnalyser.getInstance().setCurrentCode("approvedAmount = dailyLimit;");
            core.MutationAnalyser.getInstance().addNewVariableObservee(approvedAmount, "approvedAmount");
        }

        double newBalance = balance - approvedAmount;
        core.MutationAnalyser.getInstance().setCurrentCode("double newBalance = balance - approvedAmount;");
        core.MutationAnalyser.getInstance().addNewVariableObservee(newBalance, "newBalance");

        if (newBalance < overdraftLimit)
        {
            newBalance = overdraftLimit;
            core.MutationAnalyser.getInstance().setCurrentCode("newBalance = overdraftLimit;");
            core.MutationAnalyser.getInstance().addNewVariableObservee(newBalance, "newBalance");
        }

        double auditValue = newBalance * 2;
        core.MutationAnalyser.getInstance().setCurrentCode("double auditValue = newBalance * 2;");
        core.MutationAnalyser.getInstance().addNewVariableObservee(auditValue, "auditValue");
        auditValue = newBalance;
        core.MutationAnalyser.getInstance().setCurrentCode("auditValue = newBalance;");
        core.MutationAnalyser.getInstance().addNewVariableObservee(auditValue, "auditValue");

        balance = newBalance;
        core.MutationAnalyser.getInstance().setCurrentCode("balance = newBalance;");
        core.MutationAnalyser.getInstance().addNewVariableObservee(balance, "balance");

        core.MutationAnalyser.getInstance().setCurrentCode("return balance;");
        core.MutationAnalyser.getInstance().takeSnapshotObservee();
        return balance;
    }

    public double getBalance()
    {
        core.MutationAnalyser.getInstance().setCurrentCode("return balance;");
        core.MutationAnalyser.getInstance().takeSnapshotObservee();
        return balance;
    }
}
