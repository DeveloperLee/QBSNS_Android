package com.thinksns.adapter;
import com.thinksns.android.Thinksns;
import com.thinksns.android.ThinksnsAbscractActivity;
import com.thinksns.api.ApiMessage;
import com.thinksns.exceptions.ApiException;
import com.thinksns.exceptions.DataInvalidException;
import com.thinksns.exceptions.ListAreEmptyException;
import com.thinksns.exceptions.VerifyErrorException;
import com.thinksns.model.Message;
import com.thinksns.model.ListData;
import com.thinksns.model.SociaxItem; 


public class MessageDetailAdapter extends MessageInboxListAdapter {
	private Message msg;
	public MessageDetailAdapter(ThinksnsAbscractActivity context, Message msg, ListData<SociaxItem> data) {
		super(context,data);
		this.msg=msg;
	}



	@Override
	public ListData<SociaxItem>  refreshHeader(SociaxItem obj) throws VerifyErrorException, ApiException, ListAreEmptyException, DataInvalidException {
		ListData<SociaxItem> message = new ListData<SociaxItem>();
		message = getApiStatuses().outboxHeader((Message)obj, PAGE_COUNT);
		return message;
	}



	@Override
	public ListData<SociaxItem>  refreshFooter(SociaxItem obj) throws VerifyErrorException, ApiException, ListAreEmptyException, DataInvalidException{
		ListData<SociaxItem> message = new ListData<SociaxItem>();
		message = getApiStatuses().outboxFooter((Message)obj, PAGE_COUNT);
		return message;
	}

	@Override
	public ListData<SociaxItem> refreshNew(int count)
			throws VerifyErrorException, ApiException, ListAreEmptyException,
			DataInvalidException {
		ListData<SociaxItem> message = new ListData<SociaxItem>();
		message = getApiStatuses().outbox(this.msg, count);
		return message;
	}
	
	private ApiMessage getApiStatuses(){
		Thinksns app = thread.getApp();
		return  app.getMessages();
	}


}
