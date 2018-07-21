package csye.two.asg;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class AtomicList <Item>{
	
	private AtomicReference<List<Item>> itemlist = new AtomicReference<List<Item>>();
	private AtomicInteger listsize = new AtomicInteger();
	
	private static class QNode <Item> {
		final Item item;
		final AtomicReference<QNode<Item>> next;

		public QNode(Item item, QNode<Item> next) {
			this.item = item;
			this.next = new AtomicReference<QNode<Item>>(next);
		}
	}

	private final QNode<Item> dummy = new QNode<Item>(null, null);
	private final AtomicReference<QNode<Item>> head
	= new AtomicReference<QNode<Item>>(dummy);
	private final AtomicReference<QNode<Item>> tail
	= new AtomicReference<QNode<Item>>(dummy);

	public boolean put(Item item) {
		QNode<Item> newNode = new QNode<Item>(item, null);
		while (true) {
			QNode<Item> curTail = tail.get();
			QNode<Item> tailNext = curTail.next.get();
			if (curTail == tail.get()) {
				if (tailNext != null) {
					// Queue in intermediate state, advance tail
					tail.compareAndSet(curTail, tailNext);
				} else {
					// In quiescent state, try inserting new node
					if (curTail.next.compareAndSet(null, newNode)) {
						// Insertion succeeded, try advancing tail
						tail.compareAndSet(curTail, newNode);
						return true;
					}
				}
			}
		}
	}
	
	public void insert(Item item){
		
		List<Item> oldl, newl;
		do{
			List<Item> uselist = new ArrayList<Item>();
			oldl = itemlist.get();
			if(itemlist.get() != null){
				for(Item i : oldl){
					uselist.add(i);
				}
			}
			uselist.add(item);
			newl = uselist;
			
			//System.out.println(newl.size());
		}while(!itemlist.compareAndSet(oldl, newl));
		
		
	}
	
	public void remove(Item item){
		List<Item> oldl, newl;
		do{
			List<Item> uselist = new ArrayList<Item>();
			oldl = itemlist.get();
			for(Item i : oldl){
				uselist.add(i);
			}
			uselist.remove(item);
			newl = uselist;
			
			//System.out.println(newl.size());
		}while(!itemlist.compareAndSet(oldl, newl));
	}
	
	public int length(){
		
		int olds,news;
		do{
			
			olds = listsize.get();
			if(itemlist.get() != null)
				news = itemlist.get().size();
			else
				news = 0;
		

		}while(!listsize.compareAndSet(olds, news));
		
		return listsize.get();
		
	}
	
	public List<Item> getlist(){
		
		AtomicReference<List<Item>> nowlist = new AtomicReference<List<Item>>();
		List<Item> oldl, newl;
		do{
			List<Item> uselist = new ArrayList<Item>();
			oldl = itemlist.get();
			//System.out.println(oldl.size());
			for(Item i : oldl){
				uselist.add(i);
			}
			
			nowlist.set(uselist);
			//System.out.println(itemlist.get().size());
			
		}while(nowlist.compareAndSet(oldl, itemlist.get()));
//		System.out.println(nowlist.get().size());
		return nowlist.get();
	}
	
	
}
