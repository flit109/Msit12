package buyer;

import agents.BookBuyerAgent;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class RequestPerformer extends Behaviour {

    public static final String BOOK_TRADE = "book-trade";
    private final String targetBookTitle;
    private AID bestSeller;
    private int bestPrice;
    private int repliesCnt = 0;
    private MessageTemplate mt;
    private int step = 0;

    public RequestPerformer(Agent a) {
        super(a);
        this.targetBookTitle = ((BookBuyerAgent)myAgent).getTargetBookTitle();
    }

    @Override
    public void action() {
        switch (step) {
        case 0:
            // Send the cfp to all sellers
            sendCfps();
            break;
        case 1:
            // Receive all proposals/refusals from seller agents
            collectReplies();
            break;
        case 2:
            // Send the purchase order to the seller that provided the best offer
            acceptBestOffer();
            break;
        case 3:
            // Receive the purchase order reply
            receiveOrderReply();
            break;
        }
    }

    private void sendCfps() {
        ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
        for (AID aid : ((BookBuyerAgent)myAgent).sellerAgents) {
            cfp.addReceiver(aid);
        }
        cfp.setContent(targetBookTitle);
        cfp.setConversationId(BOOK_TRADE);
        cfp.setReplyWith("cfp"+System.currentTimeMillis()); // Unique value
        myAgent.send(cfp);
        // Prepare the template to get proposals
        mt = MessageTemplate.and(MessageTemplate.MatchConversationId("book-trade"),
                MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));
        step = 1;
    }

    private void collectReplies() {
        ACLMessage reply = myAgent.receive(mt);
        if (reply != null) {
            // Reply received
            if (reply.getPerformative() == ACLMessage.PROPOSE) {
                // This is an offer
                int price = Integer.parseInt(reply.getContent());
                if (bestSeller == null || price < bestPrice) {
                    // This is the best offer at present
                    bestPrice = price;
                    bestSeller = reply.getSender();
                }
            }
            repliesCnt++;
            if (repliesCnt >= ((BookBuyerAgent)myAgent).sellerAgents.size()) {
                // We received all replies
                step = 2;
            }
        }
        else {
            block();
        }
    }

    private void acceptBestOffer() {
        ACLMessage order = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
        order.addReceiver(bestSeller);
        order.setContent(targetBookTitle);
        order.setConversationId("book-trade");
        order.setReplyWith("order"+System.currentTimeMillis());
        myAgent.send(order);
        // Prepare the template to get the purchase order reply
        mt = MessageTemplate.and(MessageTemplate.MatchConversationId("book-trade"),
                MessageTemplate.MatchInReplyTo(order.getReplyWith()));
        step = 3;
    }


    private void receiveOrderReply() {
        ACLMessage reply = myAgent.receive(mt);
        if (reply != null) {
            // Purchase order reply received
            if (reply.getPerformative() == ACLMessage.INFORM) {
                // Purchase successful. We can terminate
                System.out.println(targetBookTitle+" successfully purchased from agent "+reply.getSender().getName());
                System.out.println("Price = "+bestPrice);
                myAgent.doDelete();
            }
            else {
                System.out.println("Attempt failed: requested book already sold.");
            }

            step = 4;
        }
        else {
            block();
        }
    }


    @Override
    public boolean done() {
        if (step == 2 && bestSeller == null) {
            System.out.println("Attempt failed: "+targetBookTitle+" not available for sale");
        }
        return ((step == 2 && bestSeller == null) || step == 4);
    }
}

