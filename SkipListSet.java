import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;
import java.lang.Math;
import java.lang.reflect.Array;
import java.util.ArrayList;
//By Luke Maggio, finished 7/31/2023
//set class
public class SkipListSet<T extends Comparable<T>> implements SortedSet<T>
{
    
    //iterator
    private class SkipListSetIterator<T extends Comparable<T>> implements Iterator<T>
    {
        /*we can call forEachRemaining
        For each remaining elemnet untl all elemnts have been processed
        or the action throws an
        */
  
        //removes the underlying collection the element returned by this iterator
        @SuppressWarnings("unchecked")
        public void remove()
        {
            this.remove();
        }

        //returns true if the iteration has more elements
        public boolean hasNext() 
        {
            if(next() != null) return true;
            return false;
        }

        //returns the next element in the iteration
        public T next() 
        {
            return next(); 
        }  

    }

    //item wrapper
    private class SkipListSetItem<T extends Comparable<T>>
    {
        //this will deal with horizontal
        SkipListSetItem<T> prev = null;
        SkipListSetItem<T> next = null;
        //this will deal with vertical
        SkipListSetItem<T> up = null;
        SkipListSetItem<T> down = null;
        
        boolean isRoot = false;
        T value = null;
        int height = 1; //the total height for this value
        int currentHeight = 1; //the level we are currently o
        static int totalValues = 0;
        static int maxHeight = 2; //min height is 2


        //constructor
        public SkipListSetItem() { }

        public SkipListSetItem(T value)
        {
            this.value = value;
        }

        public T getValue()
        {
            return this.value;
        }

        public int getheight()
        {
            return this.height;
        }

        public void updateheight(int height)
        {
            this.height = height;
        }

        public void updateValue(T value)
        {
            this.value = value;
        }


        //constructor
        public SkipListSetItem(T value, int height)
        {
            this.value = value;
            this.height = height;
        }

        public SkipListSetItem(T value, int height, int currentHeight)
        {
            this.value = value;
            this.height = height;
            this.currentHeight = currentHeight;
        }
    }

    SkipListSetItem<T> root = null;

    //constructor that returns an empty skiplist
    public SkipListSet()
    {
        
    }

    //constructor that returns for non-empty skiplist data set
    public SkipListSet(Collection<?> c)
    {
       
    }

    //I never have to call this, I just must implement this.
    public void reBalance()
    {
        if(SkipListSetItem.totalValues <= 1)
            return;
        SkipListSetItem<T> temp = root;

        //moves us to the first value
        while(temp.currentHeight != 1)
            temp = temp.down;
        temp = temp.next;

        int h = 0;
        SkipListSetItem<T> tempM, tempN;
        
        //until we have gone through all values in the list
        while(temp != null)
        {
            tempM = temp;
            h = coinFlip(); //our new height
            int navHeight = 0;

            if(h < tempM.height) //we need to cut off the top
            {
                navHeight = tempM.height - h;
                //this goes to the first value before where we are cutting off
                for(int i = 1; i < h; i++)
                {
                    tempM.height = h;
                    tempM = tempM.up;
                }
                tempM.height = h;

                //delete the nodes above and relinks around them
                tempN = tempM;
                for(int j = 0; j < navHeight; j++)
                {
                    tempN = tempN.up;
                    tempN.prev.next = tempN.next;
                    if(tempN.next != null) //since our links to the right can null
                        tempN.next.prev = tempN.prev;
                    tempN.prev = null;
                    tempN.next = null;
                }
                tempM.up = null; //cuts off the last connection to those nodes
            }
            else if (h > tempM.height)//we need to grow
            {
                navHeight = temp.height;
                //goes to the maxheight of this value
                for(int i = 1; i < navHeight; i++)
                {
                    tempM.height = h;
                    tempM = tempM.up;
                }
                tempM.height = h;

                
                navHeight = h - tempM.height;

                if(tempM.next != null)
                {

                    //iterates till all node heights are added
                    for(int i = 1; i <= navHeight; i++)
                    {
                        tempM.up = new SkipListSetItem<T>(temp.value, temp.height, tempM.currentHeight+1);
                        tempM.up.down = tempM;
                        //this links nodes that are in front of temp

                        if(tempM.next !=null && tempM.next.up != null) //if the node is directly in front of temp
                        {
                            tempM.next.up.prev = tempM.up;
                            tempM.up.next = tempM.next.up;
                        }
                        else if(tempM.next != null)//node is not directly in front, so we must look for a node
                        {
                            tempN = tempM;
                            while(tempN.next != null && tempM.up.next == null) //until we find a valid node
                            {
                                if(tempN.next.next != null && tempN.next.next.up != null)
                                {
                                    tempN.next.next.up.prev = tempM.up;
                                    tempM.up.next = tempN.next.next.up;
                                }
                                else
                                    tempN = tempN.next;
                            }
                        }

                        //this links nodes that are behind temp
                        if(tempM.prev.up != null) //if the node is directly behind us
                        {
                            tempM.prev.up.next = tempM.up;
                            tempM.up.prev = tempM.prev.up;
                        }
                        else //its not so we must look for a node
                        {
                            tempN = tempM; //temp navigate
                            while(tempM.up.prev == null) //until we find a valid node
                            {
                                if(tempN.prev.prev.up != null)
                                {
                                    tempN.prev.prev.up.next = tempM.up;
                                    tempM.up.prev = tempN.prev.prev.up;
                                }
                                else
                                    tempN = tempN.prev;
                            }
                        }

                        tempM = tempM.up;
                    }

                }
                else //no next value, so the payload is the largest value in the skipList currently
                {

                    //makes new nodes for the height, and then attaches it to the what ever is to the left of it at that height
                    //links nodes that are behind temp
                    for(int i = 1; i <= navHeight; i++)
                    {
                        tempM.up = new SkipListSetItem<T>(temp.value, temp.height, tempM.currentHeight+1);
                        tempM.up.down = tempM;

                        if(tempM.prev.up != null) //if the value right behind us has a node at the same height as our new node
                        {
                            tempM.prev.up.next = tempM.up;
                            tempM.up.prev = tempM.prev.up;
                        }
                        else //does not have a node at the same back, must look further back.
                        {
                            tempN = tempM; //temp navigate
                            while(tempM.up.prev == null)
                            {
                                if(tempN.prev.prev.up != null )
                                {
                                    tempN.prev.prev.up.next = tempM.up;
                                    tempM.up.prev = tempN.prev.prev.up;
                                }
                                else
                                    tempN = tempN.prev;
                            }
                        }
            
                        
                        tempM = tempM.up; //move up
                    }
                }
            }
            temp = temp.next;
        }



      
    }

    //adds the specified element to this set if it not already present
    //returns true if there is any duplicates
    public boolean add(T payload)
    {
        return addTraverse(payload);
    }

    //adds all of the elements in the specified collection to this set
    //if they're not already present, returns true if there is any duplicates
    public boolean addAll(Collection<? extends T> ourList) 
    {
        boolean all = false, allB = false; //for if all were added
        Iterator<? extends T> it = ourList.iterator();
        while(it.hasNext())
        {
            all = add(it.next());
            if(all == true)
                allB = true;
        }

        
        return allB;
    }

    //retains only the elements in this set that are contained in the
    //specified collection
    public boolean retainAll(Collection<?> ourList) 
    {
        Iterator<?> it = ourList.iterator();

        //create an ArrayList to store all of our values from the collection
        ArrayList<Object> list = new ArrayList<>(); 
        int count = 0;
        while(it.hasNext())
        {
            list.add(it.next());
            count++;
        }
        
        SkipListSetItem<T> temp = root;
        SkipListSetItem<T> tempN;
        
        //moves us to the first value, height 1
        while(temp.currentHeight != 1)
            temp = temp.down;
        temp = temp.next;

        boolean track = false;
        while(temp != null)
        {
            for(int i = 0; i < count; i++)
            {
                if(temp == list.get(i))
                    track = true;
            }

            if(track == false) //value was not found so we must delete
            {
                tempN = temp.next;
                deleteTraverse(temp.value);
                temp = tempN;
            }
            else //value was found
            {
                track = false;
                temp = temp.next;
            }
        }
        return false;
    }

    //removes all of the elements from this set
    public void clear() 
    {
        root.next = null;
        root.down = null;
        SkipListSetItem.maxHeight = 2;
        SkipListSetItem.totalValues = 0;
    }

    //returns true if this set contains the specified elemnt
    @SuppressWarnings("unchecked")
    public boolean contains(Object payload)
    {
        return searchTraverse((T)payload);
        //returns false if not found, true if found
    }

    //returns true if this set contains all of the elements of the specified collection
    public boolean containsAll(Collection<?> ourList) 
    {
        boolean all = false; //if all are in the set
        Iterator<?> it = ourList.iterator();
        while(it.hasNext())
            all = contains(it.next());
        

        return all; //returns true if all were found, false if at least one was not found
    }

    //compares the specified object with set for equality
    @Override
    public boolean equals(Object payload)
    {
        if(payload == this)
            return true;
        else
            return false;
    }

    //returns the hash code value for this set
    @Override
    public int hashCode()
    {
        SkipListSetItem<T> temp = root;
        int hash = 0;
        
        //moves us to the first value
        while(temp.currentHeight != 1)
            temp = temp.down;
        temp = temp.next;

        while(temp != null)
        {
            hash += temp.value.hashCode();
            temp = temp.next;
        }
        return hash;
    }

    //returns true if this set contains no elements
    public boolean isEmpty()
    {
        if(SkipListSetItem.totalValues <= 1)
            return true;
        return false;
    }

    //returns an iterator over the elements in this set
    public Iterator<T> iterator()
    {
        return new SkipListSetIterator<T>();
    }

    //Removes the specified element form this set if this is present, returs true if present
    @SuppressWarnings("unchecked")
    public boolean remove(Object payload)
    {
        return deleteTraverse((T)payload);
        //return false;
    }

    //removes form this set all of its elements that are contained in the specified collection
    public boolean removeAll(Collection<?> ourList)
    {
        boolean all = false; //if all are in the set
        Iterator<?> it = ourList.iterator();
        while(it.hasNext())
        {
            all = remove(it.next());
        }

        return all; //returns true if all were deleted, false if at least 1 was nt deleted
    }

    //returns the number of elements in this set (its cardinality)
    public int size()
    {
        return SkipListSetItem.totalValues; //the head counts as an element
    }

    //Returns an array containing all the elements in this set
    public Object[] toArray()
    {
        Object[] list = new Object[SkipListSetItem.totalValues-1];
        SkipListSetItem<T> temp = root;

        while(temp.currentHeight != 1)
            temp = temp.down;
        
        temp = temp.next;
        
        int index = 0;
        while(temp != null)
        {
            list[index] = temp;
            index++;
            temp = temp.next;
        }
        return list;
    }
    
    //returns an array containing all of the elements in this set; the runtime type of the
    //returned array is that of the specified array
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] temp)
    {
;
        return null;
    }

    //searches when we simply want to find a value
    public boolean searchTraverse(T payload)
    {
        SkipListSetItem<T> temp = root; //used for traversal
        int track = 0;

        if(SkipListSetItem.totalValues <= 1)
            return false;

        //searches for insertion location
        while(temp.currentHeight != 1)
        {
            if(temp.next != null) //if there is any values to the right
                track = temp.next.value.compareTo(payload);
            else //go down if not, bc if the while statement we know we are at least at height 2
            {
                temp = temp.down;
                continue;
            }
                
            if(track == 0) //temp.value is the same, found the value, so no addition is needed
            {
                return true;
            }
            else if(track > 0) //temp.value is bigger, go down
            {
                temp = temp.down; 
            }
            else //temp.value is smaller, go right
            {
                temp = temp.next;
            }
        }

        //if we made to height one but still need to travel to the right
        if(temp.next != null)
        {
            int flag = 0;
            while(flag == 0 && temp.next != null)
            {
                track = temp.next.value.compareTo(payload);
                if(track < 0) //go right, keep looking
                    temp = temp.next;
                else if(track == 0) //duplicate value
                    return true;
                else //no where left to look, payload not in data set
                    return false;
            }
        }


        return false;
    }

    //searches when we want to delete a value, if deleted return true, otherwise return false
    public boolean deleteTraverse(T payload)
    {
        SkipListSetItem<T> temp = root; //used for traversal
        int track = 0, flag = 0;
        if(SkipListSetItem.totalValues <= 1 )
            return false;

        while(temp.currentHeight != 1 && flag == 0)
        {
            if(temp.next != null) //if there is any values to the right
                track = temp.next.value.compareTo(payload);
            else //go down if not, bc if the while statement we know we are at least at height 2
            {
                temp = temp.down;
                continue;
            }
                
            if(track == 0) //temp.value is the same, found the value, so no addition is needed
            {
                temp = temp.next;
                flag = 1;
            }
            else if(track > 0) //temp.value is bigger, go down
            {
                temp = temp.down; 
            }
            else //temp.value is smaller, go right
            {
                temp = temp.next;
            }
        }

        while(temp.currentHeight != 1 && flag == 1) //brings us to the currentHeight = 1 of the node we want to delete
            temp = temp.down;

        //if we made to height one but still need to keep searching
        if(temp.next != null && flag == 0)
        {
            while(flag == 0 && temp.next != null)
            {
                track = temp.next.value.compareTo(payload);
                if(track < 0) //go right, keep looking
                    temp = temp.next;
                else if(track == 0) //duplicate value
                {
                    flag = 1;
                    temp = temp.next;
                }
                else //no where left to look, payload not in data set
                    return false;
            }
        }

        //the actual deletion process, now that we are where we need to be
        for(int i = 1; i < temp.height; i++)
        {
            temp.prev.next = temp.next;
            if(temp.next != null)
                temp.next.prev = temp.prev;
            temp.prev = null;
            temp.next = null;
            temp = temp.up;
        }
        
        temp.prev.next = temp.next;
        if(temp.next != null)
            temp.next.prev = temp.prev;
        temp.prev = null;
        temp.next = null;

        SkipListSetItem.totalValues--;
        //cant shrink below a maxHeight of 2
        if(SkipListSetItem.maxHeight != 2 && Math.log(SkipListSetItem.totalValues)/Math.log(2) == SkipListSetItem.maxHeight-1)        
            shrink();
        
            
        return true;
    }

    //searches when we want to add a value
    public boolean addTraverse(T payload)
    {
        if(root == null) //defines the root if this is our first value entered into the list, the initial max height is 2
        {
            SkipListSetItem.maxHeight = 2;
            SkipListSetItem.totalValues = 0; 
            
            //we build top-down, not buttom-up
            root = new SkipListSetItem<T>(null, 2, 2);
            root.down = new SkipListSetItem<T>(null, 2, 1);
            root.down.up = root;
            root.isRoot = true;
            root.down.isRoot = true;
            SkipListSetItem.totalValues++;
        }
        
        
        ArrayList<SkipListSetItem<T>> leftList = new ArrayList<SkipListSetItem<T>>(); 
        SkipListSetItem<T> temp = root; //used for traversal
        int track = 0, index = SkipListSetItem.maxHeight-2;

        //searches for insertion location
        while(temp.currentHeight != 1)
        {
            if(temp.next != null) //if there is any values to the right
                track = temp.next.value.compareTo(payload);
            else //go down if not, bc if the while statement we know we are at least at height 2
            {
                leftList.add(temp);
                temp = temp.down;
                continue;
            }
                
            if(track == 0) //temp.value is the same, found the value, so no addition is needed
            {
                return true;
            }
            else if(track > 0) //temp.value is bigger, go down
            {
                leftList.add(temp);
                temp = temp.down; 
            }
            else //temp.value is smaller, go right
            {
                temp = temp.next;
            }
        }
        

        //if we made to height one but still need to travel to the right
        if(temp.next != null)
        {
            int flag = 0;
            while(flag == 0 && temp.next != null)
            {
                track = temp.next.value.compareTo(payload);
                if(track < 0) //go right
                    temp = temp.next;
                else if(track == 0) //duplicate value
                    return true;
                else //we are where we need to be, so exit while loop
                    flag = 1;
            }
        }

        SkipListSetItem.totalValues++;
        if(Math.log(SkipListSetItem.totalValues)/Math.log(2) == SkipListSetItem.maxHeight+1)
        {
            grow();
            index = SkipListSetItem.maxHeight-2;
            leftList.add(0, root);
        }
            

        int h = coinFlip(); //how tall our height is

        //a next value exists, thus we must link both from the left and the right
        if(temp.next != null)
        {
            SkipListSetItem<T> tempH = temp.next;
            temp.next = new SkipListSetItem<T>(payload, h, 1);
            temp.next.prev = temp;
            temp.next.next = tempH;
            temp.next.next.prev = temp.next;
            temp = temp.next;
            //iterates till all node heights are added
            for(int i = 2; i <= temp.height; i++)
            {
                temp.up = new SkipListSetItem<T>(payload, temp.height, i);
                temp.up.down = temp;

                //this links nodes that are in front of temp
                if(temp.next != null && temp.next.up != null) //if the node is directly in front of temp
                {
                    temp.next.up.prev = temp.up;
                    temp.up.next = temp.next.up;
                }
                else //node is not directly in front, so we must look for a node
                {
                    SkipListSetItem<T> tempM = temp;
                    while(tempM.next != null && temp.up.next == null) //until we find a valid node
                    {
                        //check further back
                        if(tempM.next.next != null && tempM.next.next.up != null)
                        {
                            tempM.next.next.up.prev = temp.up;
                            temp.up.next = tempM.next.next.up;
                        }
                        else
                            tempM = tempM.next;
                    }
                }

                //this links nodes that are behind temp
                temp.up.prev = leftList.get(index);
                temp.up.prev.next = temp.up;
                temp = temp.up;
                index--;

            }

        }
        else //no next value, so the payload is the largest value in the skipList currently
        {

            temp.next = new SkipListSetItem<T>(payload, h, 1);
            temp.next.prev = temp;
            temp = temp.next;
            //makes new nodes for the height, and then attaches it to the what ever is to the left of it at that height
            //links nodes that are behind temp
            for(int i = 2; i <= temp.height; i++)
            {                
                temp.up = new SkipListSetItem<T>(payload, temp.height, i);
                temp.up.down = temp;
                temp.up.prev = leftList.get(index);
                temp.up.prev.next = temp.up;
                temp = temp.up;
                index--;
            }
        }

        return false;
    }
    
    //randomly generates a height, using the coinflip method
    public int coinFlip()
    {
        //Random RandomGenerator = new Random();
        int max = 10, min = 1, flipCount = 1, range = max - min + 1;
        boolean heads = true;
        while(heads == true & flipCount < SkipListSetItem.maxHeight)
        {
            double rand = Math.random() * range;
            if(rand> 5)
                flipCount++;
            else
                heads = false;
        }

        return flipCount; //will at least be a height of 1
    }
    
    //grows the max height by 1, thus the height of the root by 1
    public void grow()
    {
        SkipListSetItem<T> temp = root;

        //changes the height value throughout the root node
        for(int i = 1; i < temp.height; i++)
        {
            temp.height++;
            temp = temp.down;
        }
        
        //grows the height by 1
        SkipListSetItem.maxHeight++;
        root.up = new SkipListSetItem<T>(null, root.height, root.currentHeight++);
        root.up.down = root;
        root = root.up;
    }

    //shrinks the max height by 1, chopping off the top. Maintains a min height of 2
    public void shrink()
    {
        SkipListSetItem<T> temp = root;
        SkipListSetItem<T> tempN;

        temp = temp.next;
        while(temp != null)
        {
            temp.prev.next = null;
            temp.prev = null;
            tempN = temp;
            for(int i = 1; i < temp.height; i++)
            {
                tempN.down.height--;
                tempN = tempN.down;
            }
            temp.down.up = null;
            temp.down = null;
            temp = temp.next;
        }

        temp = root;
        tempN = temp;
        for(int i = 1; i < temp.height; i++)
        {
            tempN.down.height--;
            tempN = tempN.down;
        }
        root = root.down;
        root.up = null;
        SkipListSetItem.maxHeight--;
    }

    //***Methods from SortedSet<T>***

    //returns null bc this set uses the natural ordering of its elements
    public Comparator<? super T> comparator()
    {
        return null;
    }

    //returns the first (lowest) element currently in the set
    public T first()
    {
        SkipListSetItem<T> temp = root;
        //navigates down, and right 1
        while(temp.currentHeight != 1)
            temp = temp.down;
        return temp.next.value;
    }

    //Returns the last (highest) element currently in this set
    public T last()
    {
        SkipListSetItem<T> temp = root;
        //navigates right and down
        while(temp.currentHeight != 1)
        {
            if(temp.next != null)
                temp = temp.next;
            else
                temp = temp.down;
        }

        //traverse right till at the end, we are at height 1
        if(temp.next != null)
            while(temp.next != null)
                if(temp.next != null)
                    temp = temp.next;
        
        return temp.value;
    }

    //not supported
    public SortedSet<T> headSet(T toElement)
    {
        throw new UnsupportedOperationException("Invalid operation for sorted list.");
    }

    //not supported
    public SortedSet<T> subSet(T fromElement, T toElement)
    {
        throw new UnsupportedOperationException("Invalid operation for sorted list.");
    }

    //not supported
    public SortedSet<T> tailSet(T fromElement)
    {
        throw new UnsupportedOperationException("Invalid operation for sorted list.");
    }

}