package csye.two.asg;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class AtomicSet <E>{
	
	private AtomicReference<HashSet<E>> itemlist = new AtomicReference<HashSet<E>>();
	private AtomicBoolean KeyOfSet = new AtomicBoolean();
	
	
	public void put(E e){
		HashSet<E> oldl, newl;
		do{
			HashSet<E> uselist = new HashSet<E>();
			oldl = itemlist.get();
			if(itemlist.get() != null){
				for(E i : oldl){
					uselist.add(i);
				}
			}
			uselist.add(e);
			newl = uselist;
			
			//System.out.println(newl.size());
		}while(!itemlist.compareAndSet(oldl, newl));
		
	}
	
	public void out(E e){
		HashSet<E> oldl, newl;
		do{
			HashSet<E> uselist = new HashSet<E>();
			oldl = itemlist.get();
			for(E i : oldl){
				uselist.add(i);
			}
			uselist.remove(e);
			newl = uselist;
			
			//System.out.println(newl.size());
		}while(!itemlist.compareAndSet(oldl, newl));
	}
	
	public Boolean showKey(E e){
		
		//AtomicBoolean useboolean = new AtomicBoolean(false);
		
		Boolean olds,news;
		do{
			
			olds = KeyOfSet.get();
			if(itemlist.get() != null){
				if(itemlist.get().contains(e)){
					news = true;
				}else
					news = false;
			}else{
				news = false;
			}
			
		

		}while(!KeyOfSet.compareAndSet(olds, news));
		
		
		return KeyOfSet.get();
	}

}
