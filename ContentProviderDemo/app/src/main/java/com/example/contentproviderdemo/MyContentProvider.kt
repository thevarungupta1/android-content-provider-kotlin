package com.example.contentproviderdemo

import android.content.*
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteQueryBuilder
import android.net.Uri
import java.lang.IllegalArgumentException

class MyContentProvider : ContentProvider() {

    companion object{

        // defining authority so that other application can access it
        const val PROVIDER_NAME = "com.example.contentproviderdemo"

        // defining content URI
        const val URI = "content://$PROVIDER_NAME/users"

        // parsing the content URI
        val CONTENT_URI = Uri.parse(URI)
        const val id = "id"
        const val name = "name"
        const val uriCode = 1
        var uriMatcher: UriMatcher? = null
        private val values: HashMap<String, String>? = null

        // declaring name of the database
        const val DATABASE_NAME = "userdb"
        // declaring the version of the database
        const val DATABASE_VERSION = 1
        // declaring table name
        const val TABLE_NAME = "users"
        // sql query to create the table
        const val CREATE_TABLE = "create table $TABLE_NAME (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL)";

        init {
            // to match the content URI
            // every time user access under content provider
            uriMatcher = UriMatcher(UriMatcher.NO_MATCH)

            // to access whole table
            uriMatcher!!.addURI(PROVIDER_NAME, "users", uriCode)

            // to access particular row of the table
            uriMatcher!!.addURI(PROVIDER_NAME, "users/*", uriCode)
        }

    }

    // create the database
    override fun onCreate(): Boolean {
        val context = context
        val dbHelper = DatabaseHelper(context!!)
        db = dbHelper.writableDatabase
        return db!=null
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        var sortOrder = sortOrder
        val qb = SQLiteQueryBuilder()
        qb.tables = TABLE_NAME
        when(uriMatcher!!.match(uri)){
            uriCode -> qb.projectionMap = values
            else -> throw IllegalArgumentException("Unknown URI $uri")
        }
        if(sortOrder ==null || sortOrder === ""){
            sortOrder = id
        }
        val c = qb.query(
            db, projection, selection, selectionArgs, null, null, sortOrder
        )
        c.setNotificationUri(context!!.contentResolver, uri)
        return c
    }

    override fun getType(uri: Uri): String? {
        return when(uriMatcher!!.match(uri)){
            uriCode -> ""
                else -> throw IllegalArgumentException("Unsupported URI: $uri")
        }
    }

    // adding data to the database
    override fun insert(uri: Uri, values: ContentValues?): Uri? {
       val rowId =  db!!.insert(TABLE_NAME, "", values)
        if(rowId > 0){
            return ContentUris.withAppendedId(CONTENT_URI, rowId)
        }
        throw SQLiteException("failed to add a record into $uri")
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        var count = 0
        count = when(uriMatcher!!.match(uri)){
            uriCode -> db!!.delete(TABLE_NAME, selection, selectionArgs)
            else -> throw IllegalArgumentException("Unknown URI $uri")
        }
        context!!.contentResolver.notifyChange(uri, null)
        return count
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        var count = 0
        count = when(uriMatcher!!.match(uri)){
            uriCode -> db!!.update(TABLE_NAME, values, selection, selectionArgs)
            else -> throw IllegalArgumentException("Unknown URI $uri")
        }
        context!!.contentResolver.notifyChange(uri, null)
        return count
    }

    // creating object of database to perform query
    private var db: SQLiteDatabase? = null

    // create a database
    private class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
        override fun onCreate(db: SQLiteDatabase?) {
            db?.execSQL(CREATE_TABLE)
        }

        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
            db?.execSQL("DROP TABLE IF  EXISTS $TABLE_NAME")
            onCreate(db)
        }

    }

}