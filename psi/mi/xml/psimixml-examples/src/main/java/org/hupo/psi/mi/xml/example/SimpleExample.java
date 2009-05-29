package org.hupo.psi.mi.xml.example;

import psidev.psi.mi.xml.model.EntrySet;
import psidev.psi.mi.xml.model.Entry;

/**
 * Example to show how to generate PSI-MI XML files programmatically.
 */
public class SimpleExample {

    public static void main(String[] args) {

        // an EntrySet is the root object
        EntrySet entrySet = new EntrySet(2, 5, 4);

        


        Entry entry = new Entry();
        entrySet.getEntries().add(entry);

        System.out.println("DONE!");

    }

}
