package com.usergeek.stream

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

class ReportsStorage(ctx: Context) :
    SQLiteOpenHelper(ctx, DATABASE_NAME, null, DATABASE_VERSION) {

    data class Report(val sequence: Long, val content: String, val device: String?)

    companion object {
        const val DATABASE_NAME = "reports_storage.db"
        const val DATABASE_VERSION = 1

        object EventEntry : BaseColumns {
            const val TABLE_NAME = "reports"
            const val COLUMN_NAME_SEQUENCE = "sequence"
            const val COLUMN_NAME_CONTENT = "content"
            const val COLUMN_NAME_DEVICE = "device"
        }

        const val SQL_CREATE_ENTRIES =
            "CREATE TABLE IF NOT EXISTS ${EventEntry.TABLE_NAME} (" +
                    "${EventEntry.COLUMN_NAME_SEQUENCE} INTEGER PRIMARY KEY, " +
                    "${EventEntry.COLUMN_NAME_CONTENT} TEXT, " +
                    "${EventEntry.COLUMN_NAME_DEVICE} TEXT NULL)"

        const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${EventEntry.TABLE_NAME}"
    }

    private val file = ctx.getDatabasePath(DATABASE_NAME)

    // ------------------------------------------------------------------------
    // SQLiteOpenHelper interface
    // ------------------------------------------------------------------------

    override fun onCreate(db: SQLiteDatabase) {
        info { "Create database" }

        db.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // first version
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }

    // ------------------------------------------------------------------------
    // API interface
    // ------------------------------------------------------------------------

    /**
     * @param sequence must > 0
     */
    internal fun putReport(sequence: Long, content: String, device: String?) {
        assert(sequence > 0)

        val values = ContentValues().apply {
            put(EventEntry.COLUMN_NAME_SEQUENCE, sequence)
            put(EventEntry.COLUMN_NAME_CONTENT, content)
            put(EventEntry.COLUMN_NAME_DEVICE, device)
        }

        try {
            writableDatabase.use { db ->
                if (db.insert(EventEntry.TABLE_NAME, null, values) == -1L) {
                    error {
                        String.format(
                            "Can not insert report with sequence %s to db",
                            sequence
                        )
                    }
                }
            }
        } catch (th: Throwable) {
            error({
                String.format(
                    "Error while insert report with sequence %s to db",
                    sequence
                )
            }, th)
            eraseSQLite()
        } finally {
            close()
        }
    }

    /**
     *  @return null if not reports
     */
    internal fun getReports(limit: Int): List<Report>? {
        assert(limit > 0)

        val projection = arrayOf(
            EventEntry.COLUMN_NAME_SEQUENCE,
            EventEntry.COLUMN_NAME_CONTENT,
            EventEntry.COLUMN_NAME_DEVICE
        )

        val sortOrder = "${EventEntry.COLUMN_NAME_SEQUENCE} ASC"

        val reports = ArrayList<Report>(limit)

        try {
            readableDatabase.use { db ->
                db.query(
                    EventEntry.TABLE_NAME,
                    projection,
                    null, null,
                    null, null,
                    sortOrder,
                    limit.toString()
                ).use { cursor ->
                    while (cursor.moveToNext()) {
                        reports.add(
                            Report(
                                cursor.getLong(cursor.getColumnIndex(EventEntry.COLUMN_NAME_SEQUENCE)),
                                cursor.getString(cursor.getColumnIndex(EventEntry.COLUMN_NAME_CONTENT)),
                                cursor.getString(cursor.getColumnIndex(EventEntry.COLUMN_NAME_DEVICE))

                            )
                        )
                    }
                }
            }
        } catch (th: Throwable) {
            error({ "Error while get reports from db" }, th)
            eraseSQLite()
        }

        return if (reports.isEmpty()) null else reports
    }

    /**
     * Remove all reports before and include sequence
     */
    internal fun removeEarlyReports(sequence: Long) {
        val selection = "${EventEntry.COLUMN_NAME_SEQUENCE} <= ?"
        val selectionArgs = arrayOf(sequence.toString())

        try {
            writableDatabase.use { db ->
                db.delete(EventEntry.TABLE_NAME, selection, selectionArgs)
            }
        } catch (th: Throwable) {
            error({
                String.format(
                    "Error while delete reports before sequence %s from db",
                    sequence
                )
            }, th)
            eraseSQLite()
        } finally {
            close()
        }
    }

    /**
     * @return 0 if not exists
     */
    internal fun getMaxSequence(): Long {
        val query = "SELECT MAX(${EventEntry.COLUMN_NAME_SEQUENCE}) " +
                "FROM ${EventEntry.TABLE_NAME}"
        try {
            readableDatabase.use { db ->
                db.compileStatement(query).use { statement ->
                    return statement.simpleQueryForLong()
                }
            }
        } catch (th: Throwable) {
            error({ "Error while get max sequence from db" }, th)
            eraseSQLite()

            return 0L
        }
    }

    internal fun getReportsCount(): Long {
        val query = "SELECT COUNT(*) FROM ${EventEntry.TABLE_NAME}"
        try {
            readableDatabase.use { db ->
                db.compileStatement(query).use { statement ->
                    return statement.simpleQueryForLong()
                }
            }
        } catch (th: Throwable) {
            error({ "Error while get total reports from db" }, th)
            eraseSQLite()

            return 0L
        }
    }


    // ------------------------------------------------------------------------
    // Internal
    // ------------------------------------------------------------------------

    private fun eraseSQLite() {
        info { String.format("Erase reports database: %s", file) }

        try {
            close()
            file.delete()
        } catch (th: Throwable) {
            error({ "Error while drop reports database" }, th)
        }
    }
}