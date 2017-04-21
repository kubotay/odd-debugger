package WorkerThread;

import java.util.Random;

public class Request {
	private  final String name;
    private final int number;
    private final static Random random = new Random();
    public Request(String name, int number){
        System.out.println("Create Request");
        this.name = name;
        this.number = number;
    }
    public  void execute(){
        System.out.println(Thread.currentThread().getName() + " executes " + this);
        try {
            Thread.sleep(random.nextInt(1000));
        }catch (InterruptedException e){
        }
    }
    public String toString(){
        return "[ Request from " + name + " No." + number + " ]";
    }
}
