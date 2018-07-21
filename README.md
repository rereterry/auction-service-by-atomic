# auction-service-by-atomic
In this project, I revisit the Auction Server. I were not to use Synchronized Collections, but I were required to use locks
In this homework, I use Atomic Classes, but I am not to use locks. 
I will need to insure lack of race conditions by using non-blocking algorithms
Here are some restrictions that you should obey in code:
1. The method signatures and return types in AuctionServer must not be changed.
2. The following server statistics variables should not be changed and used as intended in
the assignment:
public static final int maxBidCount = 10; // The maximum number of bids at any given time for a buyer.
public static final int maxSellerItems = 20; // The maximum number of items that a seller can submit at any given time.
public static final int serverCapacity = 80; // The maximum number of active items at a given time.
