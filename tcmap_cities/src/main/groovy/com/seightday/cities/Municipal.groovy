package com.seightday.cities

class Municipal {
	
	String name
	List<String> districts=new ArrayList<String>()
	
	def addDistrict (String d){
		districts.add(d)
	}

}
