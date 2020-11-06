package buyer;


import agents.BookBuyerAgent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

import java.util.Arrays;
import java.util.stream.Collectors;

public class SellerUpdater extends TickerBehaviour {

    public SellerUpdater(BookBuyerAgent a, long period) {
        super(a, period);
    }

    @Override
    protected void onTick() {
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("book-selling");
        template.addServices(sd);
        try {
            DFAgentDescription[] result = DFService.search(myAgent, template);
            ((BookBuyerAgent)myAgent).sellerAgents =
                    Arrays.stream(result).map(DFAgentDescription::getName).collect(Collectors.toList());
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
        }

        myAgent.addBehaviour(new RequestPerformer(myAgent));
    }
}

