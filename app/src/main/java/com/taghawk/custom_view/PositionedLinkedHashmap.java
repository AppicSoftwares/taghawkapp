package com.taghawk.custom_view;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

public class PositionedLinkedHashmap<K, V> extends LinkedHashMap<K, V>
{

    private LinkedList<K> arrayList;

    public int getKeyIndex(K key)
    {
        return arrayList.indexOf(key);
    }

    public K getKeyValue(int position) throws ArrayIndexOutOfBoundsException
    {
        return arrayList.get(position);
    }

    public void updateIndexes()
    {
        if (arrayList!=null)
            arrayList.clear();
        arrayList=new LinkedList<>(this.keySet());
    }

    public void addIndex(K key)
    {
        if (arrayList==null)
            arrayList=new LinkedList<>();
        arrayList.add(key);
    }

    public void addIndexOnPosition(K key,int position)
    {
        if (arrayList==null)
            arrayList=new LinkedList<>();
        arrayList.add(position,key);
    }

    public void removeIndex(K key)
    {
        if (arrayList!=null)
            arrayList.remove(key);
    }
}