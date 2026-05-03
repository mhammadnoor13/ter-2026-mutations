package experiment.execution;

public class ClasseSimpleExecution implements Execution{

    @Override
    public void run(Object instance) {
        try {
            instance.getClass()
                    .getMethod("ajouterValeur", int.class)
                    .invoke(instance, 5);

            instance.getClass()
                    .getMethod("ajouterValeur", int.class)
                    .invoke(instance, 15);

            instance.getClass()
                    .getMethod("calculerScore")
                    .invoke(instance);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
