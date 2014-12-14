package main;

import agents.TruckAgentBDI;
import jadex.bridge.IInputConnection;
import jadex.bridge.IOutputConnection;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.types.chat.IChatService;
import jadex.commons.future.IFuture;
import jadex.commons.future.ITerminableFuture;
import jadex.commons.future.ITerminableIntermediateFuture;


@Service
public class ChatService implements IChatService{

    @ServiceComponent
    private TruckAgentBDI truck;

    @Override
    public IFuture<String> getNickName() {
        return null;
    }

    @Override
    public IFuture<byte[]> getImage() {
        return null;
    }

    @Override
    public IFuture<String> getStatus() {
        return null;
    }

    @Override
    public IFuture<Void> message(String name, String message, boolean original) {

        if(GarbageCollector.getInstance().getCommunication())
            truck.getMessage(name,message,original);
        return null;
    }

    @Override
    public IFuture<Void> status(String s, String s1, byte[] bytes) {
        return null;
    }

    @Override
    public ITerminableIntermediateFuture<Long> sendFile(String s, String s1, long l, String s2, IInputConnection iInputConnection) {
        return null;
    }

    @Override
    public ITerminableFuture<IOutputConnection> startUpload(String s, String s1, long l, String s2) {
        return null;
    }
}
