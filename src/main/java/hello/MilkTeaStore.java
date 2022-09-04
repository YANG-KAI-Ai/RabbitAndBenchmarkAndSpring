package hello;

public class MilkTeaStore implements Runnable {
    int orderNum;
    MilkTeaStore(int orderNum){
        this.orderNum = orderNum;
    }

    @Override
    public void run() {
//        System.out.println("Thread:Get Milk Tea Order "+orderNum+"!");
    }
}
