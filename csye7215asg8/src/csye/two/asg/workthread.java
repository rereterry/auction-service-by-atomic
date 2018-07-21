package csye.two.asg;

public class workthread extends Thread{
	
	private AuctionServer server;

	public workthread(AuctionServer myId) {
        this.server = myId;
    }
	
	public void run() { // override Thread's run()
        
		 for (int i=0; i < 5; i++) {
			 System.out.println("Total sold items : " + server.soldItemsCount());
	            System.out.println("Total revenue : " + server.revenue());
	            try {
	                sleep(100);
	            } catch (InterruptedException e) {}
	        }
		//System.out.println("Here is the starting point of Thread.");
		
    
        
    }

}
