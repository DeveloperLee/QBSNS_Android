package com.thinksns.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import com.thinksns.android.R;

public class TSFaceView extends LinearLayout implements
		AdapterView.OnItemClickListener {
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static final ArrayList<Integer> faceDisplayList = new ArrayList();
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static final HashMap<Integer, String> facesKeySrc = new LinkedHashMap();
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static final HashMap<String, Integer> facesKeyString = new LinkedHashMap();
	private Context m_Context;
	private GridView m_GridView;
	private FaceAdapter m_faceAdapter;
    
	
	/**
	 * 将表情资源添加到数组和哈希表中
	 * 哈希表分为以名字为键值和以id为键值两种
	 */
	static {
		/*faceDisplayList.add(Integer.valueOf(R.drawable.badsmile));
		faceDisplayList.add(Integer.valueOf(R.drawable.beer));
		faceDisplayList.add(Integer.valueOf(R.drawable.biggrin));
		faceDisplayList.add(Integer.valueOf(R.drawable.cake));
		faceDisplayList.add(Integer.valueOf(R.drawable.call));
		faceDisplayList.add(Integer.valueOf(R.drawable.cry));
		faceDisplayList.add(Integer.valueOf(R.drawable.fendou));
		faceDisplayList.add(Integer.valueOf(R.drawable.funk));
		faceDisplayList.add(Integer.valueOf(R.drawable.ha));
		faceDisplayList.add(Integer.valueOf(R.drawable.handshake));
		faceDisplayList.add(Integer.valueOf(R.drawable.huffy));
		faceDisplayList.add(Integer.valueOf(R.drawable.hug));
		faceDisplayList.add(Integer.valueOf(R.drawable.jiujie));
		faceDisplayList.add(Integer.valueOf(R.drawable.kiss));
		faceDisplayList.add(Integer.valueOf(R.drawable.ku));
		faceDisplayList.add(Integer.valueOf(R.drawable.lol));
		faceDisplayList.add(Integer.valueOf(R.drawable.loveliness));
		faceDisplayList.add(Integer.valueOf(R.drawable.mad));
		faceDisplayList.add(Integer.valueOf(R.drawable.moon));
		faceDisplayList.add(Integer.valueOf(R.drawable.ok));
		faceDisplayList.add(Integer.valueOf(R.drawable.pig));
		faceDisplayList.add(Integer.valueOf(R.drawable.sad));
		faceDisplayList.add(Integer.valueOf(R.drawable.shy));
		faceDisplayList.add(Integer.valueOf(R.drawable.smile));
		faceDisplayList.add(Integer.valueOf(R.drawable.sweat));
		faceDisplayList.add(Integer.valueOf(R.drawable.tiaoxi));
		faceDisplayList.add(Integer.valueOf(R.drawable.time));
		faceDisplayList.add(Integer.valueOf(R.drawable.titter));
		faceDisplayList.add(Integer.valueOf(R.drawable.tongue));
		faceDisplayList.add(Integer.valueOf(R.drawable.victory));
		faceDisplayList.add(Integer.valueOf(R.drawable.yiwen));
		faceDisplayList.add(Integer.valueOf(R.drawable.yun));*/
		faceDisplayList.add(Integer.valueOf(R.drawable.bizui));
		faceDisplayList.add(Integer.valueOf(R.drawable.ciya));
		faceDisplayList.add(Integer.valueOf(R.drawable.daku));
		faceDisplayList.add(Integer.valueOf(R.drawable.daxiao));
		faceDisplayList.add(Integer.valueOf(R.drawable.fendou));
		faceDisplayList.add(Integer.valueOf(R.drawable.fennu));
		faceDisplayList.add(Integer.valueOf(R.drawable.han));
		faceDisplayList.add(Integer.valueOf(R.drawable.jingya));
		faceDisplayList.add(Integer.valueOf(R.drawable.jiujie));
		faceDisplayList.add(Integer.valueOf(R.drawable.kelian));
		faceDisplayList.add(Integer.valueOf(R.drawable.ku));
		faceDisplayList.add(Integer.valueOf(R.drawable.kun));
		faceDisplayList.add(Integer.valueOf(R.drawable.meiwenti));
		faceDisplayList.add(Integer.valueOf(R.drawable.meme));
		faceDisplayList.add(Integer.valueOf(R.drawable.piaoliang));
		faceDisplayList.add(Integer.valueOf(R.drawable.shuai));
		faceDisplayList.add(Integer.valueOf(R.drawable.shuijiao));
		faceDisplayList.add(Integer.valueOf(R.drawable.tanlan));
		faceDisplayList.add(Integer.valueOf(R.drawable.tiaopi));
		faceDisplayList.add(Integer.valueOf(R.drawable.touxiao));
		faceDisplayList.add(Integer.valueOf(R.drawable.wabikong));
		faceDisplayList.add(Integer.valueOf(R.drawable.weiqu));
		faceDisplayList.add(Integer.valueOf(R.drawable.weixiao));
		faceDisplayList.add(Integer.valueOf(R.drawable.yinxian));
		faceDisplayList.add(Integer.valueOf(R.drawable.yun));


		/*facesKeySrc.put(Integer.valueOf(R.drawable.badsmile), "badsmile");
		facesKeySrc.put(Integer.valueOf(R.drawable.beer), "beer");
		facesKeySrc.put(Integer.valueOf(R.drawable.biggrin), "biggrin");
		facesKeySrc.put(Integer.valueOf(R.drawable.cake), "cake");
		facesKeySrc.put(Integer.valueOf(R.drawable.call), "call");
		facesKeySrc.put(Integer.valueOf(R.drawable.cry), "cry");
		facesKeySrc.put(Integer.valueOf(R.drawable.fendou), "fendou");
		facesKeySrc.put(Integer.valueOf(R.drawable.funk), "funk");
		facesKeySrc.put(Integer.valueOf(R.drawable.ha), "ha");
		facesKeySrc.put(Integer.valueOf(R.drawable.handshake), "handshake");
		facesKeySrc.put(Integer.valueOf(R.drawable.huffy), "huffy");
		facesKeySrc.put(Integer.valueOf(R.drawable.hug), "hug");
		facesKeySrc.put(Integer.valueOf(R.drawable.jiujie), "jiujie");
		facesKeySrc.put(Integer.valueOf(R.drawable.kiss), "kiss");
		facesKeySrc.put(Integer.valueOf(R.drawable.ku), "ku");
		facesKeySrc.put(Integer.valueOf(R.drawable.lol), "lol");
		facesKeySrc.put(Integer.valueOf(R.drawable.loveliness), "loveliness");
		facesKeySrc.put(Integer.valueOf(R.drawable.mad), "mad");
		facesKeySrc.put(Integer.valueOf(R.drawable.moon), "moon");
		facesKeySrc.put(Integer.valueOf(R.drawable.ok), "ok");
		facesKeySrc.put(Integer.valueOf(R.drawable.pig), "pig");
		facesKeySrc.put(Integer.valueOf(R.drawable.sad), "sad");
		facesKeySrc.put(Integer.valueOf(R.drawable.shy), "shy");
		facesKeySrc.put(Integer.valueOf(R.drawable.smile), "smile");
		facesKeySrc.put(Integer.valueOf(R.drawable.sweat), "sweat");
		facesKeySrc.put(Integer.valueOf(R.drawable.tiaoxi), "tiaoxi");
		facesKeySrc.put(Integer.valueOf(R.drawable.time), "time");
		facesKeySrc.put(Integer.valueOf(R.drawable.titter), "titter");
		facesKeySrc.put(Integer.valueOf(R.drawable.tongue), "tongue");
		facesKeySrc.put(Integer.valueOf(R.drawable.victory), "victory");
		facesKeySrc.put(Integer.valueOf(R.drawable.yiwen), "yiwen");
		facesKeySrc.put(Integer.valueOf(R.drawable.yun), "yun");*/
		facesKeySrc.put(Integer.valueOf(R.drawable.bizui), "bizui");
		facesKeySrc.put(Integer.valueOf(R.drawable.ciya), "ciya");
		facesKeySrc.put(Integer.valueOf(R.drawable.daku), "daku");
		facesKeySrc.put(Integer.valueOf(R.drawable.daxiao), "daxiao");
		facesKeySrc.put(Integer.valueOf(R.drawable.fendou), "fendou");
		facesKeySrc.put(Integer.valueOf(R.drawable.fennu), "fennu");
		facesKeySrc.put(Integer.valueOf(R.drawable.han), "han");
		facesKeySrc.put(Integer.valueOf(R.drawable.jingya), "jingya");
		facesKeySrc.put(Integer.valueOf(R.drawable.jiujie), "jiujie");
		facesKeySrc.put(Integer.valueOf(R.drawable.kelian), "kelian");
		facesKeySrc.put(Integer.valueOf(R.drawable.ku), "ku");
		facesKeySrc.put(Integer.valueOf(R.drawable.kun), "kun");
		facesKeySrc.put(Integer.valueOf(R.drawable.meiwenti), "meiwenti");
		facesKeySrc.put(Integer.valueOf(R.drawable.meme), "meme");
		facesKeySrc.put(Integer.valueOf(R.drawable.piaoliang), "piaoliang");
		facesKeySrc.put(Integer.valueOf(R.drawable.shuai), "shuai");
		facesKeySrc.put(Integer.valueOf(R.drawable.shuijiao), "shuijiao");
		facesKeySrc.put(Integer.valueOf(R.drawable.tanlan), "tanlan");
		facesKeySrc.put(Integer.valueOf(R.drawable.tiaopi), "tiaopi");
		facesKeySrc.put(Integer.valueOf(R.drawable.touxiao), "touxiao");
		facesKeySrc.put(Integer.valueOf(R.drawable.wabikong), "wabikong");
		facesKeySrc.put(Integer.valueOf(R.drawable.weiqu), "weiqu");
		facesKeySrc.put(Integer.valueOf(R.drawable.weixiao), "weixiao");
		facesKeySrc.put(Integer.valueOf(R.drawable.yinxian), "yinxian");
		facesKeySrc.put(Integer.valueOf(R.drawable.yun), "yun");
		
		/*facesKeyString.put("badsmile", Integer.valueOf(R.drawable.badsmile));
		facesKeyString.put("beer", Integer.valueOf(R.drawable.beer));
		facesKeyString.put("biggrin", Integer.valueOf(R.drawable.biggrin));
		facesKeyString.put("cake", Integer.valueOf(R.drawable.cake));
		facesKeyString.put("call", Integer.valueOf(R.drawable.call));
		facesKeyString.put("cry", Integer.valueOf(R.drawable.cry));
		facesKeyString.put("fendou", Integer.valueOf(R.drawable.fendou));
		facesKeyString.put("funk", Integer.valueOf(R.drawable.funk));
		facesKeyString.put("ha", Integer.valueOf(R.drawable.ha));
		facesKeyString.put("handshake", Integer.valueOf(R.drawable.handshake));
		facesKeyString.put("huffy", Integer.valueOf(R.drawable.huffy));
		facesKeyString.put("hug", Integer.valueOf(R.drawable.hug));
		facesKeyString.put("jiujie", Integer.valueOf(R.drawable.jiujie));
		facesKeyString.put("kiss", Integer.valueOf(R.drawable.kiss));
		facesKeyString.put("ku", Integer.valueOf(R.drawable.ku));
		facesKeyString.put("lol", Integer.valueOf(R.drawable.lol));
		facesKeyString
				.put("loveliness", Integer.valueOf(R.drawable.loveliness));
		facesKeyString.put("mad", Integer.valueOf(R.drawable.mad));
		facesKeyString.put("moon", Integer.valueOf(R.drawable.moon));
		facesKeyString.put("ok", Integer.valueOf(R.drawable.ok));
		facesKeyString.put("pig", Integer.valueOf(R.drawable.pig));
		facesKeyString.put("sad", Integer.valueOf(R.drawable.sad));
		facesKeyString.put("shy", Integer.valueOf(R.drawable.shy));
		facesKeyString.put("smile", Integer.valueOf(R.drawable.smile));
		facesKeyString.put("sweat", Integer.valueOf(R.drawable.sweat));
		facesKeyString.put("tiaoxi", Integer.valueOf(R.drawable.tiaoxi));
		facesKeyString.put("time", Integer.valueOf(R.drawable.time));
		facesKeyString.put("titter", Integer.valueOf(R.drawable.titter));
		facesKeyString.put("tongue", Integer.valueOf(R.drawable.tongue));
		facesKeyString.put("victory", Integer.valueOf(R.drawable.victory));
		facesKeyString.put("yiwen", Integer.valueOf(R.drawable.yiwen));
		facesKeyString.put("yun", Integer.valueOf(R.drawable.yun));*/
		facesKeyString.put("bizui", Integer.valueOf(R.drawable.bizui));
		facesKeyString.put("ciya", Integer.valueOf(R.drawable.ciya));
		facesKeyString.put("daku", Integer.valueOf(R.drawable.daku));
		facesKeyString.put("daxiao", Integer.valueOf(R.drawable.daxiao));
		facesKeyString.put("fendou", Integer.valueOf(R.drawable.fendou));
		facesKeyString.put("han", Integer.valueOf(R.drawable.han));
		facesKeyString.put("jingya", Integer.valueOf(R.drawable.jingya));
		facesKeyString.put("jiujie", Integer.valueOf(R.drawable.jiujie));
		facesKeyString.put("kelian", Integer.valueOf(R.drawable.kelian));
		facesKeyString.put("ku", Integer.valueOf(R.drawable.ku));
		facesKeyString.put("kun", Integer.valueOf(R.drawable.kun));
		facesKeyString.put("meiwenti", Integer.valueOf(R.drawable.meiwenti));
		facesKeyString.put("meme", Integer.valueOf(R.drawable.meme));
		facesKeyString.put("piaoliang", Integer.valueOf(R.drawable.piaoliang));
		facesKeyString.put("shuai", Integer.valueOf(R.drawable.shuai));
		facesKeyString.put("shuijiao", Integer.valueOf(R.drawable.shuijiao));
		facesKeyString.put("tanlan", Integer.valueOf(R.drawable.tanlan));
		facesKeyString.put("tiaopi", Integer.valueOf(R.drawable.tiaopi));
		facesKeyString.put("touxiao", Integer.valueOf(R.drawable.touxiao));
		facesKeyString.put("wabikong", Integer.valueOf(R.drawable.wabikong));
		facesKeyString.put("weiqu", Integer.valueOf(R.drawable.weiqu));
		facesKeyString.put("weixiao", Integer.valueOf(R.drawable.weixiao));
		facesKeyString.put("yinxian", Integer.valueOf(R.drawable.yinxian));
		facesKeyString.put("yun", Integer.valueOf(R.drawable.yun));

	}

	public TSFaceView(Context paramContext) {
		super(paramContext);
		this.m_Context = paramContext;
		initViews();
	}

	public TSFaceView(Context paramContext, AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
		this.m_Context = paramContext;
		initViews();
	}
     
	/**
	 * 初始化表情面板，采用自定义表格布局方式
	 */
	private void initViews() {
		LayoutInflater.from(getContext()).inflate(R.layout.face_main, this);
		this.m_GridView = ((GridView) findViewById(R.id.gridView));
		GridViewAdapter localGridViewAdapter = new GridViewAdapter(
				this.m_Context, faceDisplayList);
		this.m_GridView.setAdapter(localGridViewAdapter);
		this.m_GridView.setOnItemClickListener(this);
	}

	public FaceAdapter getFaceAdapter() {
		return this.m_faceAdapter;
	}

	@Override
	public void onItemClick(AdapterView<?> paramAdapterView, View paramView,
			int paramInt, long paramLong) {
		if (this.m_faceAdapter != null) {
			int i = faceDisplayList.get(paramInt).intValue();
			String str = facesKeySrc.get(Integer.valueOf(i));
			this.m_faceAdapter.doAction(i, str);
		}
	}

	public void reBuildViews() {
		removeAllViews();
		initViews();
		requestLayout();
		invalidate();
	}

	public void setFaceAdapter(FaceAdapter paramFaceAdapter) {
		this.m_faceAdapter = paramFaceAdapter;
	}

	public static abstract interface FaceAdapter {
		public abstract void doAction(int paramInt, String paramString);
	}
    
	/**
	 * 自定义GridView的适配器，接收整形数组作为参数
	 */
	class GridViewAdapter extends BaseAdapter {
		Context ct;
		List<Integer> list;

		public GridViewAdapter(Context mContext, ArrayList<Integer> arg2) {
			this.ct = mContext;
			this.list = arg2;
		}

		@Override
		public int getCount() {
			return this.list.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return this.list.get(position);
		}

		@Override
		public long getItemId(int paramInt) {
			return paramInt;
		}

		@Override
		public View getView(int paramInt, View paramView,
				ViewGroup paramViewGroup) { 
			ImageView localImageView = null;
			if (paramView == null) {
				localImageView = new ImageView(this.ct);
				localImageView.setBackgroundResource(R.drawable.bg_face);
				int j = TSFaceView.this.getResources().getDimensionPixelSize(
						R.dimen.face_item_view_height);
				localImageView.setPadding(0, j, 0, j);
				paramView = localImageView;
				paramView.setTag(localImageView);
			} else {
				localImageView = (ImageView) paramView.getTag();
			}

			int i = ((Integer) getItem(paramInt)).intValue();

			localImageView.setImageResource(i);
			return paramView;
		}

		/*
		 * int i = ((Integer)getItem(paramInt)).intValue(); ImageView
		 * localImageView = null; if (paramView == null) { localImageView = new
		 * ImageView(this.ct);
		 * localImageView.setBackgroundResource(R.drawable.bg_face_sel); //int j
		 * = FaceView.this.getResources().getDimensionPixelSize(2131099662);
		 * localImageView.setPadding(0, 5, 0, 5);
		 * 
		 * }
		 * 
		 * System.out.println("localImageView" + localImageView);
		 * localImageView.setImageResource(i); return localImageView;
		 * //localImageView = (ImageView)paramView;
		 */

	}
}