package agents;



import buyer.SellerUpdater;
import jade.core.AID;
import jade.core.Agent;

import java.util.List;

public class BookBuyerAgent extends Agent {

    public List<AID> sellerAgents;
    private String targetBookTitle;

    @Override
    protected void setup() {
        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            targetBookTitle = (String) args[0];
            System.out.println("Target book is " + targetBookTitle);
            addBehaviour(new SellerUpdater(this, 60000));
        } else {
            System.out.println("No target book title specified");
            doDelete();
        }
    }

    @Override
    protected void takeDown() {
        // Printout a dismissal message
        System.out.println("Buyer-agent "+getAID().getName()+" terminating.");
    }

    public String getTargetBookTitle() {
        return targetBookTitle;
    }
}
