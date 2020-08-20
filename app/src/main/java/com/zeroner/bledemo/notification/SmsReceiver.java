package com.zeroner.bledemo.notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;

/**
 * 信息 / 彩信的监听广播
 * 
 * @author
 * @created
 */
public class SmsReceiver extends BroadcastReceiver {
	public static final boolean D = true;
	// Tag
	private String TAG = this.getClass().getSimpleName();

	private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
	private static final String SMS_RECEIVED_NEW = "android.provider.Telephony.SMS_DELIVER";
	@Override
	public void onReceive(Context context, Intent intent) {
		if (D)
			Log.e(TAG, "+++ ON RECEIVE +++");
		// sms
		if (intent.getAction().equals(SMS_RECEIVED) || intent.getAction().equals(SMS_RECEIVED_NEW)) {
			Sms sms = getSms(context, intent);
			if (sms == null) {
				return;
			}

			String number = sms.getContact().getDisplayName();
			String smsBody=sms.getBody();
			if (number.length() != 0 && number.startsWith("+86")) {
				number = number.substring(3, number.length());
			}
			NotificationBiz.addMsg(0x02,"SMS|"+number + ":" + smsBody);
		}
	}


	public static Sms getSms(Context context, Intent intent) {
		// SMS sender
		StringBuilder number = new StringBuilder("");
		// sms message
		StringBuilder body = new StringBuilder("");
		Bundle bundle = intent.getExtras();
		// Bundle object is empty interrupt operation
		if (bundle == null)
			return null;
		// Get sms content and sender
		Object[] _pdus = (Object[]) bundle.get("pdus");
		if(_pdus==null||_pdus.length==0){
			return null;
		}
		SmsMessage[] message = new SmsMessage[_pdus.length];

		for (int i = 0; i < _pdus.length; i++) {
			message[i] = SmsMessage.createFromPdu((byte[]) _pdus[i]);
		}
		// getting information
		for (SmsMessage currentMessage : message) {
			// Get the number
			if (!number.toString().equals(currentMessage.getDisplayOriginatingAddress())) {
				number.append(currentMessage.getDisplayOriginatingAddress());
			}
			body.append(currentMessage.getDisplayMessageBody());
		}

		return new Sms(number.toString(), body.toString(), SmsReceiver.getContact(context, number.toString()));
	}

	public static class Sms {
		public Sms() {
		}

		public Sms(String number, String body, Contact contact) {
			this.number = number;
			this.body = body;
			this.contact = contact;
		}

		private String number;
		private String body;
		private Contact contact;

		public String getNumber() {
			return number;
		}

		public void setNumber(String number) {
			this.number = number;
		}

		public String getBody() {
			return body;
		}

		public void setBody(String body) {
			this.body = body;
		}

		public Contact getContact() {
			return contact;
		}

		public void setContact(Contact contact) {
			this.contact = contact;
		}

	}

	public static Contact getContact(Context context, String phoneNumber) {
		Contact contact = new Contact(phoneNumber);
		if (TextUtils.isEmpty(phoneNumber)) {
			contact.setDisplayName("Unknown Number");
		}
		Cursor cursor = null;
		try {
			// Find number, and name
			// Find the display name, did not find the display number
			Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
			cursor = context.getContentResolver().query(uri,
					new String[] { ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup.TYPE, ContactsContract.PhoneLookup.LABEL }, null,
					null, ContactsContract.PhoneLookup.DISPLAY_NAME + " LIMIT 1");
			while (cursor.moveToNext()) {
				contact.setDisplayName(cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME)));
				break;
			}
		} catch (Exception e) {
			contact.setDisplayName(phoneNumber);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return contact;
	}

	public static class Contact {
		private String number;
		private String displayName;

		public Contact(String phoneNumber) {
			this.number = phoneNumber;
			this.displayName = phoneNumber;
		}

		public String getNumber() {
			return number;
		}

		public void setNumber(String number) {
			this.number = number;
		}

		public String getDisplayName() {
			return displayName;
		}

		public void setDisplayName(String displayName) {
			this.displayName = displayName;
		}
	}
}