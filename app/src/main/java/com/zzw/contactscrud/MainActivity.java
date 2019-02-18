package com.zzw.contactscrud;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/**
 * https://www.cnblogs.com/liaojie970/p/5744580.html
 */

public class MainActivity extends AppCompatActivity {
	String name = "测试";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
	}

	private void initView() {
		findViewById(R.id.insert).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				insert();
			}
		});
		findViewById(R.id.insert_img).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				insertImg();
			}
		});
		findViewById(R.id.insert_all).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				insertAll();
			}
		});
		findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				delete();

			}
		});
		findViewById(R.id.update).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				update();
			}
		});
		findViewById(R.id.query).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				query();
			}
		});
	}


	private void query() {
		//uri = content://com.android.contacts/contacts
		Uri uri = Uri.parse("content://com.android.contacts/contacts"); //访问raw_contacts表
		ContentResolver resolver = getContentResolver();
		//获得_id属性
		Cursor cursor = resolver.query(uri, new String[]{ContactsContract.Data._ID}, null, null, null);
		while (cursor.moveToNext()) {
			StringBuilder buf = new StringBuilder();
			//获得id并且在data中寻找数据
			int id = cursor.getInt(0);
			buf.append("id=" + id);
			uri = Uri.parse("content://com.android.contacts/contacts/" + id + "/data");
			//data1存储各个记录的总数据，mimetype存放记录的类型，如电话、email等
			Cursor cursor2 = resolver.query(uri, new String[]{ContactsContract.Data.DATA1, ContactsContract.Data.MIMETYPE}, null, null, null);
			while (cursor2.moveToNext()) {
				String data = cursor2.getString(cursor2.getColumnIndex("data1"));
				if (cursor2.getString(cursor2.getColumnIndex("mimetype")).equals("vnd.android.cursor.item/name")) {       //如果是名字
					buf.append(",name=" + data);
				} else if (cursor2.getString(cursor2.getColumnIndex("mimetype")).equals("vnd.android.cursor.item/phone_v2")) {  //如果是电话
					buf.append(",phone=" + data);
				} else if (cursor2.getString(cursor2.getColumnIndex("mimetype")).equals("vnd.android.cursor.item/email_v2")) {  //如果是email
					buf.append(",email=" + data);
				} else if (cursor2.getString(cursor2.getColumnIndex("mimetype")).equals("vnd.android.cursor.item/postal-address_v2")) { //如果是地址
					buf.append(",address=" + data);
				} else if (cursor2.getString(cursor2.getColumnIndex("mimetype")).equals("vnd.android.cursor.item/organization")) {  //如果是组织
					buf.append(",organization=" + data);
				}
			}
			String str = buf.toString();
			Log.i("Contacts", str);
		}
	}

	private void update() {
		//根据姓名求id
		ContentResolver resolver = getContentResolver();
		Cursor cursor = resolver.query(Uri.parse("content://com.android.contacts/raw_contacts"),
				new String[]{ContactsContract.Data._ID}, "display_name=?",
				new String[]{name}, null);
		int id = -1;
		if (cursor.moveToFirst()) {
			id = cursor.getInt(0);
			Log.i("mainactivity", "insert: " + id);
		}
		String phone = "66666666";
		Uri uri = Uri.parse("content://com.android.contacts/data");//对data表的所有数据操作
		ContentValues values = new ContentValues();
		values.put("data1", phone);
		resolver.update(uri, values, "mimetype=? and raw_contact_id=?", new String[]{"vnd.android.cursor.item/phone_v2",id+""});
	}

	private void delete() {
		//根据姓名求id
		Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
		ContentResolver resolver = getContentResolver();
		Cursor cursor = resolver.query(uri, new String[]{ContactsContract.Data._ID}, "display_name=?", new String[]{name}, null);
		if (cursor.moveToFirst()) {
			int id = cursor.getInt(0);
			//根据id删除data中的相应数据
			resolver.delete(uri, "display_name=?", new String[]{name});
			uri = Uri.parse("content://com.android.contacts/data");
			resolver.delete(uri, "raw_contact_id=?", new String[]{id + ""});
		}
	}

	private void insert() {


		Uri uri = ContactsContract.Data.CONTENT_URI;
		ContentResolver resolver = getContentResolver();
		Cursor cursorUser = resolver.query(uri, new String[]{ContactsContract.CommonDataKinds.Phone._ID,
				ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID},
				null, null, null);
		int ceshiId = -1;
		while (cursorUser.moveToNext()) {
			int id = cursorUser.getInt(0); // 按上面数组的声明顺序获取
			String name = cursorUser.getString(1);
			int rawContactsId = cursorUser.getInt(2);
			Log.i("mainactivity", "insert: " + rawContactsId);
			if (this.name.equals(name)) {
				ceshiId = cursorUser.getInt(2);
				Log.i("mainactivity", "insert: 测试  " + rawContactsId);
				break;
			}
		}
		Uri dataUri = Uri.parse("content://com.android.contacts/data");
		ContentValues phonevalues = new ContentValues();
		phonevalues.put("raw_contact_id", ceshiId);
		phonevalues.put("data1", "1502311");
		phonevalues.put("mimetype", "vnd.android.cursor.item/phone_v2");
		resolver.insert(dataUri, phonevalues);
	}

	private void insertImg() {
		ContentValues values = new ContentValues();
		//根据姓名求id
		Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
		ContentResolver resolver = getContentResolver();
		Cursor cursor = resolver.query(uri, new String[]{ContactsContract.Data._ID}, "display_name=?", new String[]{name}, null);
		int id = -1;
		if (cursor.moveToFirst()) {
			id = cursor.getInt(0);
			Log.i("mainactivity", "insert: " + id);
		}
		// 向data表插入头像数据
		Bitmap sourceBitmap = BitmapFactory.decodeResource(getResources(),
				R.mipmap.aaa);
		final ByteArrayOutputStream os = new ByteArrayOutputStream();
		// 将Bitmap压缩成PNG编码，质量为100%存储
		sourceBitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
		byte[] avatar = os.toByteArray();
		values.put(ContactsContract.Data.RAW_CONTACT_ID, id);
		values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE);
		values.put(ContactsContract.CommonDataKinds.Photo.PHOTO, avatar);
		getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
	}

	private void insertAll() {
		new Thread() {
			@Override
			public void run() {
				super.run();

				String callPhoneNumber1 = "0216078";
				String callPhoneNumber2 = "0216078";
				String callPhoneNumber3 = "0216078";
				String callPhoneNumber4 = "0216078";
				String callPhoneNumber5 = "0571281";
				String callPhoneNumber6 = "0571281";
				Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
				ContentResolver resolver = getContentResolver();
				ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();
				ContentProviderOperation op1 = ContentProviderOperation.newInsert(uri)
						.withValue("account_name", null)
						.build();
				operations.add(op1);

				uri = Uri.parse("content://com.android.contacts/data");
				//添加姓名
				ContentProviderOperation op2 = ContentProviderOperation.newInsert(uri)
						.withValueBackReference("raw_contact_id", 0)
						.withValue("mimetype", "vnd.android.cursor.item/name")
						.withValue("data2", name)
						.build();
				operations.add(op2);
				//添加电话号码
				ContentProviderOperation op3 = ContentProviderOperation.newInsert(uri)
						.withValueBackReference("raw_contact_id", 0)
						.withValue("mimetype", "vnd.android.cursor.item/phone_v2")
						.withValue("data1", callPhoneNumber1)
						.withValue("data2", "2")
						.build();
				operations.add(op3);
				//添加电话号码
				ContentProviderOperation op4 = ContentProviderOperation.newInsert(uri)
						.withValueBackReference("raw_contact_id", 0)
						.withValue("mimetype", "vnd.android.cursor.item/phone_v2")
						.withValue("data1", callPhoneNumber2)
						.withValue("data2", "2")
						.build();
				operations.add(op4);
				//添加电话号码
				ContentProviderOperation op5 = ContentProviderOperation.newInsert(uri)
						.withValueBackReference("raw_contact_id", 0)
						.withValue("mimetype", "vnd.android.cursor.item/phone_v2")
						.withValue("data1", callPhoneNumber3)
						.withValue("data2", "2")
						.build();
				operations.add(op5);
				//添加电话号码
				ContentProviderOperation op6 = ContentProviderOperation.newInsert(uri)
						.withValueBackReference("raw_contact_id", 0)
						.withValue("mimetype", "vnd.android.cursor.item/phone_v2")
						.withValue("data1", callPhoneNumber4)
						.withValue("data2", "2")
						.build();
				operations.add(op6);
				//添加电话号码
				ContentProviderOperation op7 = ContentProviderOperation.newInsert(uri)
						.withValueBackReference("raw_contact_id", 0)
						.withValue("mimetype", "vnd.android.cursor.item/phone_v2")
						.withValue("data1", callPhoneNumber5)
						.withValue("data2", "2")
						.build();
				operations.add(op7);
				ContentProviderOperation op8 = ContentProviderOperation.newInsert(uri)
						.withValueBackReference("raw_contact_id", 0)
						.withValue("mimetype", "vnd.android.cursor.item/phone_v2")
						.withValue("data1", callPhoneNumber6)
						.withValue("data2", "2")
						.build();
				operations.add(op8);


				try {
					resolver.applyBatch("com.android.contacts", operations);
				} catch (RemoteException e) {
					e.printStackTrace();
				} catch (OperationApplicationException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}
}
