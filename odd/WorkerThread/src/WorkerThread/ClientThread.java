package WorkerThread;

import java.util.Random;

public class ClientThread extends Thread{
	private  final Channel channel;
    private static final Random random = new Random();
    public ClientThread(String name, Channel channel){
        super(name);
        System.out.println("Create ClientThread");
        this.channel = channel;
    }
    public  void run(){
        try {
            for(int i = 0; i < 30; i++){
                Request request = new Request(getName(), i);
                channel.putRequest(request);
                Thread.sleep(random.nextInt(1000));
            }
        }catch (InterruptedException e){
        }
    }
}
