package com.acg.outtamycircle.utilities;

import android.support.annotation.NonNull;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class MyList<T> implements Iterable<T>{
    private final MyIterator iterator = new MyIterator();
    private Node<T> head = null;

    @NonNull
    @Override
    public Iterator<T> iterator() {
        return iterator;
    }

    public void resetIterator(){
        resetIterator(iterator);
    }

    private void resetIterator(MyIterator iterator) {
        iterator.next = head;
        iterator.cur = null;
    }

    public void add(T key){
        Node<T> node = new Node<>(key);
        if(head != null) {
            head.prev = node;
            node.next = head;
        }
        head = node;
        resetIterator();
    }

    public void remove(T el){
        Node<T> prev = null;
        for(T key : this) {
            if(key.equals(el)) {
                if (prev == null)
                    head = head.next;
                else
                    prev.next = prev.next.next;
                resetIterator();
                return;
            }
            if(prev == null)
                prev = head;
            else
                prev = prev.next;
        }
        throw new NoSuchElementException("Cannot find the element to remove");
    }

    private class MyIterator implements Iterator<T> {
        Node<T> next, cur;

        @Override
        public boolean hasNext() {
            return next != null;
        }

        @Override
        public T next() {
            if(!hasNext())
                throw new NoSuchElementException();
            cur = next;
            T ret = cur.key;
            next = cur.next;
            return ret;
        }

        @Override
        public void remove() {
            if(cur == null)
                throw new IllegalStateException("first get next bitch");
            if(cur == head)
                head = head.next;
            else
                cur.prev.next = cur.next;
        }
    }

    private class Node<T>{
        final T key;
        Node<T> next, prev;

        Node(T key){
            this.key = key;
        }
    }

}
