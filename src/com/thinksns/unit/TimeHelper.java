package com.thinksns.unit;

import com.thinksns.exceptions.TimeIsOutFriendly;

public class TimeHelper {
	public static String friendlyTime(int time) throws TimeIsOutFriendly{
		long now = System.currentTimeMillis();
		long targetTime = (long)time*1000;
		int diff = (int) Math.ceil((now-targetTime)/1000);
		if(diff<60){
			return "刚刚";
		}else if(diff < 3600){
			return (int)Math.ceil(diff/60) +"分钟";
		}else if(diff <= 21600){
			return (int)Math.ceil(diff/3600) + "小时";
		}else{
			throw new TimeIsOutFriendly();
		}
		
	}
}
