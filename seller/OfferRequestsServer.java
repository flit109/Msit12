package seller;


import agents.BookSellerAgent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class OfferRequestsServer extends Behaviour {

    private static final MessageTemplate template = MessageTemplate.MatchPerformative(ACLMessage.CFP);

    @Override
    public void action() {
        ACLMessage message = myAgent.receive(template);
        if (message != null) {
            String bookTitle = message.getContent();
            Integer bookPrice = ((BookSellerAgent) myAgent).getBookPrice(bookTitle);
            ACLMessage reply = constructReply(message, bookPrice);
            myAgent.send(reply);
        } else {
            block();
        }
    }

    private ACLMessage constructReply(ACLMessage original, Integer price) {
        ACLMessage reply = original.createReply();
        if (price != null) {
            reply.setPerformative(ACLMessage.PROPOSE);
            reply.setContent(price.toString());
        } else {
            reply.setPerformative(ACLMessage.REFUSE);
            reply.setContent("Not available");
        }
        return reply;
    }

    @Override
    public boolean done() {
        return false;
    }
}

