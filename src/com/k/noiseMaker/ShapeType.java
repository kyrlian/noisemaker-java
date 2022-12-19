package com.k.noiseMaker;

import java.util.Arrays;

enum ShapeType {
	SIN, SQR, SAW, RND, FLAT;
	public static String[] getAsList() {
        String valuesStr = Arrays.toString(ShapeType.values());
        return valuesStr.substring(1, valuesStr.length()-1).replace(" ", "").split(",");
    }
}