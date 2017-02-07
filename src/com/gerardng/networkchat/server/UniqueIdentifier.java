package com.gerardng.networkchat.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UniqueIdentifier {

	private static List<Integer> idList = new ArrayList<Integer>();
	private static final int CLIENTS_ALLOWED = 1000;
	private static int index = 0;
	
	static {
		for (int i = 0; i < CLIENTS_ALLOWED; i++) {
			idList.add(i);
		}
		Collections.shuffle(idList);
	}
	
	private UniqueIdentifier() {
	}
	
	public static int getIdentifier() {
		return idList.get(index++);
	}
}
