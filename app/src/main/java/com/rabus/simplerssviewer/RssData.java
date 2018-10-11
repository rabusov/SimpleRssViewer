package com.rabus.simplerssviewer;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class RssData extends SQLiteOpenHelper {
	public static final String DATABASE_NAME = "feeds.db";
	private static final int DATABASE_VERSION = 2;
	private SQLiteDatabase db = null;
	private Context context = null;
	
	public RssData(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.setContext(context);
		setDb(getWritableDatabase());
		FillFeedsDB(db);
	}
	public RssData(Context context, String name, CursorFactory factory,
			int version) {
		// TODO Auto-generated constructor stub
		super(context, name, factory, version);
		this.setContext(context);
		setDb(getWritableDatabase());
	}

	public RssData(Context context, String name, CursorFactory factory,
			int version, DatabaseErrorHandler errorHandler) {
		// TODO Auto-generated constructor stub
		super(context, name, factory, version, errorHandler);
		this.setContext(context);
		setDb(getWritableDatabase());
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("CREATE TABLE " + SQLConstants.TABLE_NAME
				+ " (" + SQLConstants._ID	+ " INTEGER PRIMARY KEY AUTOINCREMENT"
				+ ", " + SQLConstants.COL_url + " TEXT NOT NULL"
				+ ", " + SQLConstants.COL_name + " TEXT NULL"
				+ ", " + SQLConstants.COL_urlhashcode + " TEXT NULL"
				+ ");"
				);
		FillFeedsDB(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		if (oldVersion != newVersion)
		{
			FillFeedsDB(db);
		}
	}
	public void FillFeedsDB(SQLiteDatabase db)
	{
		// Добавляем ссылки на RSS потоки из ресурсного массива R.array.kommersant_urls
		// с именами потоков из ресурсного массива R.array.kommersant_titles
		String[] links = context.getResources().getStringArray(R.array.kommersant_urls);
		String[] titles = context.getResources().getStringArray(R.array.kommersant_titles);
		ContentValues values = new ContentValues();
		String stmt;
		Cursor Rows;
		for (int i=0; i < links.length; i++)
		{
			stmt = "SELECT COUNT(*) FROM " + SQLConstants.TABLE_NAME + " WHERE " + SQLConstants.COL_url + "='" +links[i]+ "';";
			Rows = db.rawQuery(stmt, null);
			if (Rows.moveToFirst())
			{
				// 0 - строка не найдена, изменяем
				if (Rows.getInt(0) == 0)
				{
					values.clear();
					values.put(SQLConstants.COL_name, titles[i]);
					values.put(SQLConstants.COL_url, links[i]);
					values.put(SQLConstants.COL_urlhashcode, links[i].hashCode());
					db.insertOrThrow(SQLConstants.TABLE_NAME, null, values);
				}
			}
			if (Rows != null) Rows.close();
		}
	}
	public Context getContext() {
		return context;
	}
	public void setContext(Context context) {
		this.context = context;
	}
	public SQLiteDatabase getDb() {
		return db;
	}
	public void setDb(SQLiteDatabase db) {
		this.db = db;
	}
	public String getUrl()
	{
		String rc = null;
		String stmt = "SELECT " + SQLConstants.COL_url + " FROM " + SQLConstants.TABLE_NAME + " WHERE " + SQLConstants._ID + "=1;";
		Cursor Rows = db.rawQuery(stmt, null);
		if (Rows.moveToFirst())
		{
			rc = Rows.getString(0);
		}
		if (Rows != null) Rows.close();

		return rc;
	}
	public String getUrl(int id)
	{
		String rc = null;
		String stmt = "SELECT " + SQLConstants.COL_url + " FROM " + SQLConstants.TABLE_NAME + " WHERE " + SQLConstants._ID + "="+ id +";";
		Cursor Rows = db.rawQuery(stmt, null);
		if (Rows.moveToFirst())
		{
			rc = Rows.getString(0);
		}
		if (Rows != null) Rows.close();

		return rc;
	}
	public void UpdateUrl(String newurl)
	{
		StringBuilder stmt = new StringBuilder("UPDATE ").append(SQLConstants.TABLE_NAME);
		stmt.append(" SET ").append(SQLConstants.COL_url).append("='").append(newurl).append("'");
		stmt.append(",").append(SQLConstants.COL_urlhashcode).append("='").append(newurl.hashCode()).append("'");
		stmt.append(" WHERE ").append(SQLConstants._ID).append("=1;");
		db.execSQL(stmt.toString());
			
	}
	public ArrayList< HashMap<String, String> > getArrayList()
	{
		ArrayList< HashMap<String, String> > myArrList = new ArrayList< HashMap<String, String> >();
		HashMap<String, String> map;

		StringBuilder stmt = new StringBuilder("SELECT ").append(SQLConstants.COL_url);
		stmt.append(", ").append(SQLConstants.COL_name);
		stmt.append(" FROM ").append(SQLConstants.TABLE_NAME).append(" ORDER BY 2;");
		Cursor Rows = db.rawQuery(stmt.toString(), null);
		if (Rows.moveToFirst())
		{
			while (Rows.isAfterLast() == false) {
				map = new HashMap<String, String>();
				map.put("Link", Rows.getString(0));
				map.put("Name", Rows.getString(1));
				myArrList.add(map);
				Rows.moveToNext();
			}
		}
		if (Rows != null) Rows.close();
		return myArrList;
	}
	public void DeleteUrl(String link)
	{
		String where = SQLConstants.COL_urlhashcode + " like '" + link.hashCode() + "'";
		db.delete(SQLConstants.TABLE_NAME, where, null);
		//ListOfLists.h.sendEmptyMessage(Constants.NEEDREFRESHTABLELIST_DEL);
	}
	public int UpdateNameAndUrl(String currentlink, String newname, String newlink)
	{
		String where = SQLConstants.COL_urlhashcode + " like '" + currentlink.hashCode() + "'";
		ContentValues values = new ContentValues();
		int rc = 0;
		if (currentlink.hashCode() == newlink.hashCode())
		{
			values.put(SQLConstants.COL_name, newname);
			db.update(SQLConstants.TABLE_NAME, values, where, null);
			rc = 1;
		} else {
			// проверим, нет ли записи с таким-же hashCode как у newlink
			StringBuilder stmt = new StringBuilder("SELECT COUNT(*) FROM ")
					.append(SQLConstants.TABLE_NAME)
					.append(" WHERE ")
					.append(SQLConstants.COL_urlhashcode)
					.append("=").append(newlink.hashCode());
			Cursor Rows = db.rawQuery(stmt.toString(), null);
			if (Rows.moveToFirst())
			{
				// 0 - строка не найдена, добавляем
				if (Rows.getInt(0) == 0) {

					values.put(SQLConstants.COL_name, newname);
					values.put(SQLConstants.COL_url, newlink);
					values.put(SQLConstants.COL_urlhashcode, newlink.hashCode());
					db.update(SQLConstants.TABLE_NAME, values, where, null);
					rc = 1;
				}
			}
		}
		return rc;
	}

	public int AddNameAndUrl(String newname, String newlink) {
		int rc = 0;
		ContentValues values = new ContentValues();
		// проверим, нет ли записи с таким-же hashCode как у newlink
		StringBuilder stmt = new StringBuilder("SELECT COUNT(*) FROM ")
				.append(SQLConstants.TABLE_NAME)
				.append(" WHERE ")
				.append(SQLConstants.COL_urlhashcode)
				.append("=").append(newlink.hashCode());
		Cursor Rows = db.rawQuery(stmt.toString(), null);
		if (Rows.moveToFirst())
		{
			// 0 - строка не найдена, добавляем
			if (Rows.getInt(0) == 0) {

				values.put(SQLConstants.COL_name, newname);
				values.put(SQLConstants.COL_url, newlink);
				values.put(SQLConstants.COL_urlhashcode, newlink.hashCode());
				db.insertOrThrow(SQLConstants.TABLE_NAME, null, values);
				rc = 1;
			}
		}
		return rc;
	}

}
