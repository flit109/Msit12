package agents;

import seller.OfferRequestsServer;
import seller.PurchaseOrdersServer;
import gui.BookSellerGui;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BookSellerAgent extends Agent {
    private Map<String, Integer> catalogue;
    private BookSellerGui myGui;

    @Override
    protected void setup() {
        catalogue = new ConcurrentHashMap<>();
        myGui = new BookSellerGui(this);
        myGui.showGui();

        registerInDf();

        addBehaviour(new OfferRequestsServer());
        addBehaviour(new PurchaseOrdersServer());
    }

    private void registerInDf() {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(this.getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("book-selling");
        sd.setName("JADE-book-trading");
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
    }

    @Override
    protected void takeDown() {
        try {
            DFService.deregister(this);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
        myGui.dispose();
    }

    public void addToCatalogue(String title, Integer price) {
        catalogue.put(title, price);
    }

    public Integer getBookPrice(String title) {
        return catalogue.get(title);
    }

    public Integer removeFromCatalogue(String title) {
        return catalogue.remove(title);
    }
}
