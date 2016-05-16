package com.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;

import com.ds.shape.DsImage;

 
public class Main {
	
	private static boolean KMP_NEXT_VAL(String s, String t){
		int[] next = new int[t.length()+1];
		next[0] = -1;
		next[1] = 0;
		for(int i=1; i<t.length(); ++i){
			int j = next[i];
			while(j!=-1 && t.charAt(i)!=t.charAt(j)) j = next[j];
			next[i+1] = j+1;
			if(i+1 < t.length()) {
				if(t.charAt(i+1) == t.charAt(j+1))
					next[i+1] = next[j+1];
			}
		}
		
		for(int i=0, j=0; i < s.length(); ){
			if(j==-1 || s.charAt(i) == t.charAt(j)){
				++i;
				++j;
				if(j == t.length()) return true;
			} else {
				j = next[j];
			}
		}
		return false;
	}
	
	public static void main(String[] args){
		 Scanner scan = new Scanner(System.in);
		 String s = scan.nextLine();
		 String t = scan.nextLine();
		 System.out.println(KMP_NEXT_VAL(s, t));
	}
}
