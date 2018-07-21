package csye.two.asg;


/**
 * Class provided for ease of test. This will not be used in the project 
 * evaluation, so feel free to modify it as you like.
 */ 
public class Simulation
{
    public static void main(String[] args)
    {                
        int nrSellers = 10;
        int nrBidders = 10;
 
        
        Thread[] sellerThreads = new Thread[nrSellers];
        Thread[] bidderThreads = new Thread[nrBidders];
        
        Seller[] sellers = new Seller[nrSellers];
        Bidder[] bidders = new Bidder[nrBidders];
        

        AuctionServer buysell = AuctionServer.getInstance();
        
        
        // Start the sellers
        for (int i=0; i<nrSellers; ++i)
        {
            sellers[i] = new Seller(
            		AuctionServer.getInstance(), 
            		"Seller"+i, 
            		100, 50, i
            );
            sellerThreads[i] = new Thread(sellers[i]);
            sellerThreads[i].start();
            
        }
        
        // Start the buyers
        for (int i=0; i<nrBidders; ++i)
        {
        	//System.out.println("確認賣家是否有啟動");
        	bidders[i] = new Bidder(
            		AuctionServer.getInstance(), 
            		"Buyer"+i, 
            		1000, 20, 150, i
            );
            bidderThreads[i] = new Thread(bidders[i]);
            bidderThreads[i].start();
        }
        
        
        // Join on the sellers
        for (int i=0; i<nrSellers; ++i)
        {
            try
            {
                sellerThreads[i].join();
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
        
        
        // Join on the bidders
        for (int i=0; i<nrBidders; ++i)
        {
            try
            {
            	bidderThreads[i].join();
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
        
      
        System.out.println("--------------------------------------------------"); 
        
        // PRINT itemsAndIDs's size
        System.out.println("itemsAndIDs  size: " +  buysell.gettotalitem()); 
        // Print lastID
        System.out.println("Check last id : " + buysell.getlastlistingid());
        System.out.println("--------------------------------------------------");
        if(buysell.soldItemsCount() == buysell.gethighestBidders()){
        	System.out.println("Total submitted items : " + buysell.soldItemsCount());
        }
                   
        System.out.println("Total revenue : " + buysell.revenue());
        System.out.println("--------------------------------------------------");
        
        
        
        
        
        int TotalMoneySpentByBidders = 0;
        for(Bidder b : bidders){
        	
        //Revenue = total cash spent by bidders
        TotalMoneySpentByBidders = TotalMoneySpentByBidders + b.cashSpent();
        
        
        }
        
        System.out.println("Total money spent by Bidders : " + TotalMoneySpentByBidders );
        
        if (TotalMoneySpentByBidders == buysell.revenue()){
        	System.out.println("Revenue is equal to Total Money Spent, Auction Successful");
        }
        
        else{
        	System.out.println("Fault in auction server");
        }
        // TODO: Add code as needed to debug
        
    }
}