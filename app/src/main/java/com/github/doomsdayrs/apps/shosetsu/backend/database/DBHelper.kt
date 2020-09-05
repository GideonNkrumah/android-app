package com.github.doomsdayrs.apps.shosetsu.backend.database

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import app.shosetsu.lib.Novel
import com.github.doomsdayrs.apps.shosetsu.backend.database.DBHelper.Columns.*
import com.github.doomsdayrs.apps.shosetsu.backend.database.DBHelper.Tables.*
import com.github.doomsdayrs.apps.shosetsu.common.enums.ReadingStatus
import com.github.doomsdayrs.apps.shosetsu.common.ext.deserializeString
import com.github.doomsdayrs.apps.shosetsu.common.ext.launchIO
import com.github.doomsdayrs.apps.shosetsu.common.ext.logID
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.ChapterEntity
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.NovelEntity
import com.github.doomsdayrs.apps.shosetsu.providers.database.converters.NovelStatusConverter
import com.github.doomsdayrs.apps.shosetsu.providers.database.dao.ChaptersDao
import com.github.doomsdayrs.apps.shosetsu.providers.database.dao.NovelsDao
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

/*
* This file is part of Shosetsu.
*
* Shosetsu is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* Shosetsu is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with Shosetsu.  If not, see <https://www.gnu.org/licenses/>.
*/

/**
 * Shosetsu
 * 14 / 06 / 2019
 */
@Deprecated("SQL Database removed")
class DBHelper(context: Context) :
		SQLiteOpenHelper(context, DB_NAME, null, 10), KodeinAware {
	override val kodein: Kodein by kodein(context)
	private val novelDAO by kodein.instance<NovelsDao>()
	private val chapterDAO by instance<ChaptersDao>()

	private enum class Columns(private val key: String) {
		URL("url"),
		PARENT_ID("parent_id"),
		ID("id"),

		TITLE("title"),
		IMAGE_URL("image_url"),
		DESCRIPTION("description"),
		GENRES("genres"),
		AUTHORS("authors"),
		STATUS("status"),
		TAGS("tags"),
		ARTISTS("artists"),
		LANGUAGE("language"),
		MAX_CHAPTER_PAGE("max_chapter_page"),

		RELEASE_DATE("release_date"),
		ORDER("order_of"),

		FORMATTER_ID("formatterID"),
		READ_CHAPTER("read"),
		Y_POSITION("y"),
		BOOKMARKED("bookmarked"),
		;

		override fun toString(): String = key
	}

	private enum class Tables(private val key: String) {
		NOVEL_IDENTIFICATION("novel_identification"),
		CHAPTER_IDENTIFICATION("chapter_identification"),
		NOVELS("novels"),
		CHAPTERS("chapters"),

		@Deprecated("ROOM")
		UPDATES("updates"),

		@Deprecated("ROOM")
		DOWNLOADS("downloads"),
		;

		override fun toString(): String {
			return key
		}
	}

	/***/
	override fun onCreate(db: SQLiteDatabase) {}

	/***/
	override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
		if (oldVersion < 9) {
			db.execSQL("DROP TABLE IF EXISTS $CHAPTERS")
			db.execSQL("DROP TABLE IF EXISTS $NOVELS")
			db.execSQL("DROP TABLE IF EXISTS $DOWNLOADS")
			db.execSQL("DROP TABLE IF EXISTS $UPDATES")
		} else if (oldVersion < 10) {
			convert(db)
		}
	}

	private fun convert(db: SQLiteDatabase) {

		val novelIDS = db.rawQuery("SELECT * FROM $NOVEL_IDENTIFICATION", null).let {
			ArrayList<NovelIdentification>().apply {
				while (it.moveToNext()) add(
						NovelIdentification(
								novelID = it.getInt(ID),
								novelURL = it.getString(URL),
								formatterID = it.getInt(FORMATTER_ID)
						)
				)
			}
		}

		val chapterIDS = db.rawQuery("SELECT * FROM $CHAPTER_IDENTIFICATION", null).let {
			ArrayList<ChapterIdentification>().apply {
				while (it.moveToNext()) add(ChapterIdentification(
						chapterID = it.getInt(ID),
						novelID = it.getInt(PARENT_ID),
						chapterURL = it.getString(URL)
				))
			}
		}

		val novels = db.rawQuery("SELECT * FROM $NOVELS", null).let {
			ArrayList<OldNovel>().apply {
				while (it.moveToNext()) add(OldNovel(
						novelID = it.getInt(PARENT_ID),
						bookmarked = it.getInt(BOOKMARKED) == 1,
						title = it.getString(TITLE).deserializeString() ?: "",
						imageURL = it.getString(IMAGE_URL),
						description = it.getString(DESCRIPTION).deserializeString() ?: "",
						language = it.getString(LANGUAGE).deserializeString() ?: "",
						maxChapter = it.getInt(MAX_CHAPTER_PAGE),
						publishingStatus = it.getInt(STATUS).let {
							NovelStatusConverter().toStatus(it)
						},
						authors = it.getString(AUTHORS)
								.deserializeString<String>()?.convertStringToArray()
								?: arrayOf(),
						genres = it.getString(GENRES)
								.deserializeString<String>()?.convertStringToArray()
								?: arrayOf(),
						tags = it.getString(TAGS)
								.deserializeString<String>()?.convertStringToArray()
								?: arrayOf(),
						artists = it.getString(ARTISTS)
								.deserializeString<String>()?.convertStringToArray()
								?: arrayOf()
				))
			}
		}.map { (novelID, bookmarked, title, imageURL, description, genres, authors, tags, publishingStatus, artists, language, maxChapter) ->
			val novelIDF = novelIDS.find { it.novelID == novelID }!!

			NovelEntity(
					id = novelID,
					url = novelIDF.novelURL,
					formatterID = novelIDF.formatterID,
					bookmarked = bookmarked,
					loaded = true,
					title = title,
					imageURL = imageURL,
					description = description,
					language = language,
					genres = genres,
					authors = authors,
					artists = artists,
					tags = tags,
					status = publishingStatus
			)
		}

		val chapters = db.rawQuery("SELECT * FROM $CHAPTERS", null).let {
			ArrayList<OldChapter>().apply {
				while (it.moveToNext()) add(OldChapter(
						chapterID = it.getInt(ID),
						novelID = it.getInt(PARENT_ID),
						title = it.getString(TITLE).deserializeString() ?: "",
						date = it.getString(RELEASE_DATE).deserializeString() ?: "",
						order = it.getDouble(ORDER),
						yPosition = it.getInt(Y_POSITION),
						readChapter = it.getInt(READ_CHAPTER).let {
							ReadingStatus.getStatus(it)
						},
						bookmarked = it.getInt(BOOKMARKED) == 1
				))
			}
		}.map { (chapterID, novelID, title, date, order, yPosition, readChapter, bookmarked) ->
			val chapterIDF = chapterIDS.find { it.chapterID == chapterID }!!
			val novelIDF = novelIDS.find { it.novelID == novelID }!!

			ChapterEntity(
					id = chapterID,
					novelID = novelID,
					url = chapterIDF.chapterURL,
					formatterID = novelIDF.formatterID,
					title = title,
					releaseDate = date,
					order = order,
					readingPosition = yPosition,
					readingStatus = readChapter,
					bookmarked = bookmarked
			)
		}

		launchIO {
			novelDAO.insertAllIgnore(novels)
			chapterDAO.insertAllIgnore(chapters)
			Log.d(logID(), "Finished insert, Deleting tables")

			db.execSQL("DROP TABLE IF EXISTS $CHAPTER_IDENTIFICATION")
			db.execSQL("DROP TABLE IF EXISTS $NOVEL_IDENTIFICATION")
			db.execSQL("DROP TABLE IF EXISTS $CHAPTERS")
			db.execSQL("DROP TABLE IF EXISTS $NOVELS")
			db.execSQL("DROP TABLE IF EXISTS $DOWNLOADS")
			db.execSQL("DROP TABLE IF EXISTS $UPDATES")
		}
	}

	private data class NovelIdentification(
			val novelID: Int,
			val novelURL: String,
			val formatterID: Int,
	)

	private data class ChapterIdentification(
			val chapterID: Int,
			val novelID: Int,
			val chapterURL: String,
	)

	private data class OldChapter(
			val chapterID: Int,
			val novelID: Int,
			val title: String,
			val date: String,
			val order: Double,
			val yPosition: Int,
			val readChapter: ReadingStatus,
			val bookmarked: Boolean,
	)

	private data class OldNovel(
			val novelID: Int,
			val bookmarked: Boolean,
			val title: String,
			val imageURL: String,
			val description: String,
			@Suppress("ArrayInDataClass") val genres: Array<String>,
			@Suppress("ArrayInDataClass") val authors: Array<String>,
			@Suppress("ArrayInDataClass") val tags: Array<String>,
			val publishingStatus: Novel.Status,
			@Suppress("ArrayInDataClass") val artists: Array<String>,
			val language: String,
			val maxChapter: Int,
	)

	/**
	 * Converts a String Array back into an Array of Strings
	 *
	 * @return Array of Strings
	 */
	private fun String.convertStringToArray(): Array<String> {
		val a = this.substring(1, this.length - 1).split(", ".toRegex()).toTypedArray()
		for (x in a.indices) {
			a[x] = a[x].replace(">,<", ",")
		}
		return a
	}

	private fun SQLiteDatabase.query(
			table: Tables,
			columns: Array<String>? = null,
			selection: String? = null,
			selectionArgs: Array<String?>? = null,
			groupBy: String? = null,
			having: String? = null,
			orderBy: String? = null,
			limit: String? = null,
	): Cursor {
		return query(
				table.toString(),
				columns,
				selection,
				selectionArgs,
				groupBy,
				having,
				orderBy,
				limit
		)
	}


	private fun Cursor.getString(column: Columns): String = getString(getColumnIndex(column.toString()))

	private fun Cursor.getInt(column: Columns): Int = getInt(getColumnIndex(column.toString()))

	private fun Cursor.getDouble(column: Columns): Double = getDouble(getColumnIndex(column.toString()))

	private fun Cursor.getLong(column: Columns): Long = getLong(getColumnIndex(column.toString()))

	companion object {
		private const val DB_NAME = "database.db"
	}
}