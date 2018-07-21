package csye.two.asg;

/**
 *  @author YOUR NAME SHOULD GO HERE
 */


import java.util.ArrayList;


import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;


import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.*;





public class AuctionServer
{
	/**
	 * Singleton: the following code makes the server a Singleton. You should
	 * not edit the code in the following noted section.
	 * 
	 * For test purposes, we made the constructor protected. 
	 */

	/* Singleton: Begin code that you SHOULD NOT CHANGE! */
	protected AuctionServer()
	{
	}

	private static AuctionServer instance = new AuctionServer();

	public static AuctionServer getInstance()
	{
		return instance;
	}

	/* Singleton: End code that you SHOULD NOT CHANGE! */





	/* Statistic variables and server constants: Begin code you should likely leave alone. */


	/**
	 * Server statistic variables and access methods:
	 */
	private AtomicInteger soldItemsCount = new AtomicInteger(0);
	private AtomicInteger revenue = new AtomicInteger(0);
	

	public int soldItemsCount()
	{
		//System.out.println(soldItemsCount);
		return this.soldItemsCount.get();
		//see the total item that salar put on list, but not means must success
	/*	int count = this.soldItemsCount;
		for(int i : itemsAndIDs.keySet()){
			if(itemsAndIDs.get(i).biddingOpen() == false && itemUnbid(i) == false ){
				//System.out.println("The number of Items sold in the autcion " +highestBids.size() +"\n Total number of Items in the auction " + itemsAndIDs.size());
			count++;
			//System.out.println("Items up for bidding are " +itemsUpForBidding.size());
			}
		}
		return count;*/
	}

	public int revenue()
	{
		
		return this.revenue.get();
		
	}



	/**
	 * Server restriction constants:
	 */
	public static final int maxBidCount = 10; // The maximum number of bids at any given time for a buyer.
	public static final int maxSellerItems = 20; // The maximum number of items that a seller can submit at any given time.
	public static final int serverCapacity = 80; // The maximum number of active items at a given time.
	

	/* Statistic variables and server constants: End code you should likely leave alone. */



	/**
	 * Some variables we think will be of potential use as you implement the server...
	 */

	// List of items currently up for bidding (will eventually remove things that have expired).
	//private CopyOnWriteArrayList<Item> itemsUpForBidding = new CopyOnWriteArrayList<Item>();
	//private AtomicReferenceArray<List<Item>> itemsUpForBidding = new AtomicReferenceArray<List<Item>>(null);
	//private List<Item> itemsForBidding = new ArrayList<Item>();
	
	//private AtomicReference<List<Item>> itemsUpForBidding = new AtomicReference<List<Item>>(itemsForBidding);
	
	private AtomicList itemsUpForBidding = new AtomicList();


	// The last value used as a listing ID.  We'll assume the first thing added gets a listing ID of 0.
	//private int lastListingID = -1;
	private AtomicInteger lastListingID = new AtomicInteger(-1);

	// List of item IDs and actual items.  This is a running list with everything ever added to the auction.
	//private ConcurrentHashMap<Integer, Item> itemsAndIDs = new ConcurrentHashMap<Integer, Item>();
	
	private Hashtable<Integer, Item> itemsAndIDs = new Hashtable<Integer, Item>();

	
	
	
	// List of itemIDs and the highest bid for each item.  This is a running list with everything ever added to the auction.
	private Hashtable<Integer, Integer> highestBids = new Hashtable<Integer, Integer>();

	// List of itemIDs and the person who made the highest bid for each item.   This is a running list with everything ever bid upon.
	private Hashtable<Integer, String> highestBidders = new Hashtable<Integer, String>(); 




	// List of sellers and how many items they have currently up for bidding.
	private Hashtable<String, Integer> itemsPerSeller = new Hashtable<String, Integer>();
	// List of sellers and check how many item price< 10
	private Hashtable<String, Integer> PricePerSeller = new Hashtable<String, Integer>();
	// List of sellers, and if all items in its history items list are < $10
	final int TRUE = 1, FALSE = 0;
	private Hashtable<String, Integer> allItemsLower = new Hashtable<String, Integer>();	
	// List of sellers and check how many item not sell
	private Hashtable<String, Integer> unbidItemsCountPerSeller = new Hashtable<String, Integer>();

	// List of buyers and how many items on which they are currently bidding.
	private Hashtable<String, Integer> itemsPerBuyer = new Hashtable<String, Integer>();



	// Object used for instance synchronization if you need to do it at some point 
	// since as a good practice we don't use synchronized (this) if we are doing internal
	// synchronization.
	//
	 //private Object instanceLock = new Object(); 
	 
	 private Hashtable<String, List<Integer>> bidsByBidder = new Hashtable<String, List<Integer>>();
	 
	 	 
	 //private HashSet<String> blacklist = new HashSet<String>();
	 //private ConcurrentHashSet<String> blacklist = new ConcurrentHashSet<String>();
	 private AtomicSet<String> blacklist = new AtomicSet<String>();
	 //private ConcurrentHashSet<Integer> transactionend = new ConcurrentHashSet<Integer>();
	 private AtomicSet<Integer> transactionend = new AtomicSet<Integer>();



	public int gettotalitem(){
		
		return itemsAndIDs.size();
	}
	public int getlastlistingid(){
		return lastListingID.get();
	}
	public int gethighestBidders(){
		return highestBidders.size();
	}
	private int checkitem = 0;
	public int checkway(){
		return checkitem;
	}



	/*
	 *  The code from this point forward can and should be changed to correctly and safely 
	 *  implement the methods as needed to create a working multi-threaded server for the 
	 *  system.  If you need to add Object instances here to use for locking, place a comment
	 *  with them saying what they represent.  Note that if they just represent one structure
	 *  then you should probably be using that structure's intrinsic lock.
	 */


	/**
	 * Attempt to submit an <code>Item</code> to the auction
	 * @param sellerName Name of the <code>Seller</code>
	 * @param itemName Name of the <code>Item</code>
	 * @param lowestBiddingPrice Opening price
	 * @param biddingDurationMs Bidding duration in milliseconds
	 * @return A positive, unique listing ID if the <code>Item</code> listed successfully, otherwise -1
	 */
	public int submitItem(String sellerName, String itemName, int lowestBiddingPrice, int biddingDurationMs)
	{
		// TODO: IMPLEMENT CODE HERE
		// Some reminders:
		//   Make sure there's room in the auction site.
		//   If the seller is a new one, add them to the list of sellers.
		//   If the seller has too many items up for bidding, don't let them add this one.
		//   Don't forget to increment the number of things the seller has currently listed.
		
		
		AtomicInteger outputItem = new AtomicInteger(-1);
		if (blacklist.showKey(sellerName)){// check if the seller is in blacklist
			return outputItem.get();
			//return -1;
		}
		
		//design the price should 0 < price < 99
		if(lowestBiddingPrice < 0 || lowestBiddingPrice > 99){
			return outputItem.get();
			//return -1;
		}
		
		if (itemsUpForBidding.length() >= serverCapacity) {// over server capacity
			return outputItem.get();
		}
		
		
		if(lowestBiddingPrice < 10){
			if(!PricePerSeller.containsKey(sellerName)){
				itemsPerSeller.put(sellerName, 0); //add new seller to itemsPerSeller list
				PricePerSeller.put(sellerName, 0);
				allItemsLower.put(sellerName, TRUE);//add new seller to list which will store if all historical items < $10
			}else
				PricePerSeller.put(sellerName, PricePerSeller.get(sellerName) + 1);
		}
		if (itemsPerSeller.containsKey(sellerName) && itemsPerSeller.get(sellerName) >= maxSellerItems) {// over items per seller capacity
			return outputItem.get();
		}
		if(PricePerSeller.containsKey(sellerName) && allItemsLower.get(sellerName) == FALSE){
			if(PricePerSeller.get(sellerName) > 3){
				blacklist.put(sellerName);//disqualify this seller, add it into blacklist
				return outputItem.get();
			}
		}
/**		
		// Create and add the item
		//synchronized(instanceLock){
				AtomicReference<Item> item = new AtomicReference<Item>();
				item = new AtomicReference<Item>(new Item(sellerName, itemName, lastListingID.get(), lowestBiddingPrice, biddingDurationMs));
		
				//lastListingID.incrementAndGet();
				itemsAndIDs.put(lastListingID.incrementAndGet(), item.get());
				itemsUpForBidding.add(item.get());
				
				
				highestBids.put(lastListingID.get(), lowestBiddingPrice);
		

				if(itemsPerSeller.containsKey(sellerName)){
					int oldValue = itemsPerSeller.get(sellerName);
					itemsPerSeller.replace(sellerName, oldValue + 1);
				}
				

				if (lowestBiddingPrice < 10) {//new item opening price < $10
					if (allItemsLower.get(sellerName) == FALSE) { // There is at least one item of this seller's history > $10
						PricePerSeller.put(sellerName, PricePerSeller.get(sellerName) + 1);// this seller submit one more item < $10
					}
					
					// submit new item and DO NOTHING
				} else {// new item opening price >= $10
					if(PricePerSeller.containsKey(sellerName)){
					if (allItemsLower.get(sellerName) == TRUE) {
						allItemsLower.put(sellerName, FALSE);//Now in this seller's history there is at least one item > $10
					}
					}
				}
				System.out.println(sellerName + " sell an item" + itemsPerSeller.get(sellerName));
				return item.get().listingID();
		//}
**/
		
			
			//first to check upload number is lower than we design
			if (itemsUpForBidding.length() < serverCapacity
					&& (!itemsPerSeller.containsKey(sellerName) || itemsPerSeller.get(sellerName) < maxSellerItems)
					//check if sell more than 3 item sell lower than 10 dollars;
					//&& (!PricePerSeller.containsKey(sellerName) || PricePerSeller.get(sellerName) < maxlowpriceitem)
					){
				
				//outputItem.incrementAndGet();
				//lastListingID.incrementAndGet();
				//lastListingID++; // begins at -1
				AtomicReference<Item> item = new AtomicReference<Item>(new Item(sellerName, itemName, lastListingID.get(), lowestBiddingPrice, biddingDurationMs));
				//Item item = new Item(sellerName, itemName, lastListingID.get(), lowestBiddingPrice, biddingDurationMs);
				
				itemsAndIDs.put(lastListingID.incrementAndGet(), item.get());
			/**	
				AtomicReference<List<Item>> oldl, newl;
				do{
					oldl = new AtomicReference<List<Item>>(itemsUpForBidding.get());
					
					itemsForBidding.add(item.get());
					newl = new AtomicReference<List<Item>>(itemsForBidding);
					
				}while(!itemsUpForBidding.compareAndSet(oldl.get(), newl.get()));
			**/	
				itemsUpForBidding.insert(item.get());
				//itemsUpForBidding.add(item.get());

				highestBids.put(lastListingID.get(), lowestBiddingPrice);

				if (!itemsPerSeller.containsKey(sellerName)) {
					itemsPerSeller.put(sellerName, 1);
					
				} else {
					itemsPerSeller.put(sellerName, itemsPerSeller.get(sellerName) + 1);
					if (lowestBiddingPrice < 10) {//new item opening price < $10
						if (allItemsLower.get(sellerName) == FALSE) { // There is at least one item of this seller's history > $10
							PricePerSeller.put(sellerName, PricePerSeller.get(sellerName) + 1);// this seller submit one more item < $10
						}
						// else if( all items lower than 10 == TRUE)
						// submit new item and DO NOTHING
					} else {// new item opening price >= $10
						if(PricePerSeller.containsKey(sellerName)){
						if (allItemsLower.get(sellerName) == TRUE) {
							allItemsLower.put(sellerName, FALSE);//Now in this seller's history there is at least one item > $10
						}
						}
					}
				
				}
				System.out.println(sellerName + " sell an item" + itemsPerSeller.get(sellerName));
				
				return lastListingID.get();
			}
		


		return outputItem.get();
		
	}



	/**
	 * Get all <code>Items</code> active in the auction
	 * @return A copy of the <code>List</code> of <code>Items</code>
	 */
	// here change the list to linkedlist and use the push not add
	public List<Item> getItems()
	{
		// TODO: IMPLEMENT CODE HERE
		// Some reminders:
		//    Don't forget that whatever you return is now outside of your control.
		//List<Item> getItems = new CopyOnWriteArrayList<Item>();
		//List<Item> getItem = new ArrayList<Item>();
		AtomicReference<List<Item>> getItems = new AtomicReference<List<Item>>();
		//AtomicInteger count = new AtomicInteger(0);
		
		
			
		
		List<Item> oldl, newl;
		do{
			oldl = itemsUpForBidding.getlist();
			List<Item> uselist = new ArrayList<Item>();
			
//			System.out.println("ere");
			for(Item each : oldl){
				uselist.add(new Item(each.seller(), each.name(), each.listingID(), each.lowestBiddingPrice(),
						each.biddingDurationMs()));
			}
			
			getItems.set(uselist);
			
		}while(getItems.compareAndSet(oldl, itemsUpForBidding.getlist()));

		
		return getItems.get();
		
	}


	/**
	 * Attempt to submit a bid for an <code>Item</code>
	 * @param bidderName Name of the <code>Bidder</code>
	 * @param listingID Unique ID of the <code>Item</code>
	 * @param biddingAmount Total amount to bid
	 * @return True if successfully bid, false otherwise
	 */
	public boolean submitBid(String bidderName, int listingID, int biddingAmount)
	{
		// TODO: IMPLEMENT CODE HERE
		// Some reminders:
		//   See if the item exists.
		//   See if it can be bid upon.
		//   See if this bidder has too many items in their bidding list.
		//   Get current bidding info.
		//   See if they already hold the highest bid.
		//   See if the new bid isn't better than the existing/opening bid floor.
		//   Decrement the former winning bidder's count
		//   Put your bid in place
		AtomicBoolean outputbid = new AtomicBoolean(false);
		//synchronized (instanceLock) {
			Item item = itemsAndIDs.get(listingID);
			// item exits and list have item and item is still open
			if (item != null && itemsUpForBidding.getlist().contains(item) && item.biddingOpen()
					// buyer bid lower than design
					&& (!itemsPerBuyer.containsKey(bidderName) || itemsPerBuyer.get(bidderName) < maxBidCount)
					// not on black list
					&& !blacklist.showKey(bidderName)
					//check highest price
					&& (!highestBidders.containsKey(listingID) || !highestBidders.get(listingID).equals(bidderName))
					&& (highestBids.containsKey(listingID) && highestBids.get(listingID) < biddingAmount)) {

				String formerBidder = highestBidders.get(listingID);

				if (formerBidder != null && itemsPerBuyer.containsKey(formerBidder)) {
					itemsPerBuyer.put(formerBidder, itemsPerBuyer.get(formerBidder) - 1);
				}

				//這個部分要修
				highestBidders.put(listingID, bidderName);
				highestBids.put(listingID, biddingAmount);

				if (itemsPerBuyer.containsKey(bidderName)) {
					itemsPerBuyer.put(bidderName, itemsPerBuyer.get(bidderName) + 1);
				} else {
					itemsPerBuyer.put(bidderName, 1);
				}

				//change to linkedlist
				LinkedList<Integer> lst;
				if (!bidsByBidder.containsKey(bidderName)) {
					lst = new LinkedList<Integer>();
				} else {
					lst = new LinkedList<Integer>(bidsByBidder.get(bidderName));
				}
				lst.push(listingID);
				bidsByBidder.put(bidderName, lst);
				System.out.println(bidderName + " buy items " + bidsByBidder.get(bidderName));

				outputbid.set(true);
				//return true;
			}

			return outputbid.get();
		//}
	}

	/**
	 * Check the status of a <code>Bidder</code>'s bid on an <code>Item</code>
	 * @param bidderName Name of <code>Bidder</code>
	 * @param listingID Unique ID of the <code>Item</code>
	 * @return 1 (success) if bid is over and this <code>Bidder</code> has won<br>
	 * 2 (open) if this <code>Item</code> is still up for auction<br>
	 * 3 (failed) If this <code>Bidder</code> did not win or the <code>Item</code> does not exist
	 */
	public int checkBidStatus(String bidderName, int listingID)
	{
		// TODO: IMPLEMENT CODE HERE
		// Some reminders:
		//   If the bidding is closed, clean up for that item.
		//     Remove item from the list of things up for bidding.
		//     Decrease the count of items being bid on by the winning bidder if there was any...
		//     Update the number of open bids for this seller
		AtomicInteger situation = new AtomicInteger(0);
		if (!itemsAndIDs.containsKey(listingID)) {//item not exists
			//return 3;
			situation.set(3);
			return situation.get();
		}

		AtomicReference<Item> item = new AtomicReference<Item>(itemsAndIDs.get(listingID));
		//Item item = null;
		//item = itemsAndIDs.get(listingID);

		if (item.get().biddingOpen()) {//if item is still active for bidding
			situation.set(2);
			return situation.get();
			//return 2;
		} else {
			if (!highestBidders.containsKey(listingID)) {//there isn't any bids exists of this item
				//increment count of unbid but expired items of this seller
				
				unbidItemsCountPerSeller.put(item.get().seller(), unbidItemsCountPerSeller.getOrDefault(listingID, 0) + 1);
				//if the seller already has 4 or more unbid but expired items, add it into blacklist
				if (unbidItemsCountPerSeller.get(item.get().seller()) >= 4) {
					blacklist.put(item.get().seller());
				}
				//remove expired but without bids, so not sold item
			/**	
				AtomicReference<List<Item>> oldl, newl;
				do{
					oldl = new AtomicReference<List<Item>>(itemsUpForBidding.get());
					itemsForBidding.remove(item.get());
					newl = new AtomicReference<List<Item>>(itemsForBidding);
					
				}while(!itemsUpForBidding.compareAndSet(oldl.get(), newl.get()));
			**/
				itemsUpForBidding.remove(item.get());
				//itemsUpForBidding.remove(item);
				System.out.println(listingID + " is expired without bid.");
				situation.set(3);
				return situation.get();
				//return 3;
			}
		}
			
		//else{ item is closed, and there are several bids of this item
		
		String currentBidder = highestBidders.get(listingID);
		if (!bidderName.equals(currentBidder)) {//if bidderName is not the highest bidder of this item, return 
			situation.set(3);
			return situation.get();
			//return 3;
		}

		String sellerName = item.get().seller();
		if(itemsPerSeller.containsKey(sellerName)){
			int oldValue = itemsPerSeller.get(sellerName);
			itemsPerSeller.replace(sellerName, oldValue - 1);//decrement active items count of the seller
		}
		
		// Remove closed item from list
	/**
		AtomicReference<List<Item>> oldlv, newlv;
		do{
			oldlv = new AtomicReference<List<Item>>(itemsUpForBidding.get());
			itemsForBidding.remove(item.get());
			newlv = new AtomicReference<List<Item>>(itemsForBidding);
			
			
		}while(!itemsUpForBidding.compareAndSet(oldlv.get(), newlv.get()));
	**/
		itemsUpForBidding.remove(item.get());
		//itemsUpForBidding.remove(item);
		//revenue is here
		if (!transactionend.showKey(listingID)) {
			//revenue += highestBids.get(listingID);
			revenue.addAndGet(highestBids.get(listingID));
			transactionend.put(listingID);
		}
		//revenue += highestBids.get(listingID);//increment revenue with highest bid price
		if(itemsAndIDs.get(listingID).biddingOpen() == false && itemUnbid(listingID) == false ){
			//soldItemsCount++;//increment sold items count
			soldItemsCount.incrementAndGet();
		}
		
		
		//decrement bidding items count of bidderName
		int oldValueBuyer = itemsPerBuyer.get(bidderName);
		itemsPerBuyer.replace(bidderName, oldValueBuyer - 1);
		System.out.println(bidderName + " won item " + listingID +" with price " + highestBids.get(listingID));
		situation.set(1);
		return situation.get();
		//return 1;
	}

	/**
	 * Check the current bid for an <code>Item</code>
	 * @param listingID Unique ID of the <code>Item</code>
	 * @return The highest bid so far or the opening price if no bid has been made,
	 * -1 if no <code>Item</code> exists
	 */
	public int itemPrice(int listingID)
	{
		// TODO: IMPLEMENT CODE HERE
		AtomicInteger situation = new AtomicInteger(-1);
		//synchronized (instanceLock) {
			Item item = itemsAndIDs.get(listingID);
			if (item != null) {
				if(highestBids.get(listingID) != null){
					situation.set(highestBids.get(listingID));
				}
				//return highestBids.get(listingID);
			} else { // item doesn't exist or bidding closed
				//return -1;
			}
			return situation.get();
		//}
	}

	/**
	 * Check whether an <code>Item</code> has been bid upon yet
	 * @param listingID Unique ID of the <code>Item</code>
	 * @return True if there is no bid or the <code>Item</code> does not exist, false otherwise
	 */
	public Boolean itemUnbid(int listingID)
	{
		// TODO: IMPLEMENT CODE HERE
		AtomicBoolean itemunbid = new AtomicBoolean(false);
		
			if(!(highestBids.containsKey(listingID))){
				itemunbid.set(true);
				//return itemunbid.get();
			}
			
			return itemunbid.get();
		
		
	}
	
	


}
 