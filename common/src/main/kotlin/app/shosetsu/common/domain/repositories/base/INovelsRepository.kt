package app.shosetsu.common.domain.repositories.base
/*
 * This file is part of shosetsu.
 *
 * shosetsu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * shosetsu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with shosetsu.  If not, see <https://www.gnu.org/licenses/>.
 */
import app.shosetsu.common.domain.model.local.BookmarkedNovelEntity
import app.shosetsu.common.domain.model.local.NovelEntity
import app.shosetsu.common.domain.model.local.StrippedBookmarkedNovelEntity
import app.shosetsu.common.domain.model.local.StrippedNovelEntity
import app.shosetsu.common.dto.HResult
import app.shosetsu.lib.IExtension
import app.shosetsu.lib.Novel
import kotlinx.coroutines.flow.Flow


/**
 * shosetsu
 * 25 / 04 / 2020
 *
 * @author github.com/doomsdayrs
 */
interface INovelsRepository {
	/**
	 * Gets all bookmarked as live data
	 *
	 * @return
	 * [HResult.Success] TODO RETURN DESCRIPTION
	 *
	 * [HResult.Error] TODO RETURN DESCRIPTION
	 *
	 * [HResult.Empty] TODO RETURN DESCRIPTION
	 *
	 * [HResult.Loading] TODO RETURN DESCRIPTION
	 */
	fun getLiveBookmarked(): Flow<HResult<List<BookmarkedNovelEntity>>>

	/**
	 * Gets NovelEntities that are bookmarked
	 *
	 * @return
	 * [HResult.Success] TODO RETURN DESCRIPTION
	 *
	 * [HResult.Error] TODO RETURN DESCRIPTION
	 *
	 * [HResult.Empty] TODO RETURN DESCRIPTION
	 *
	 * [HResult.Loading] TODO RETURN DESCRIPTION
	 */
	suspend fun getBookmarkedNovels(): HResult<List<NovelEntity>>

	/**
	 * Searches the bookmarked novels and returns a live data of them
	 *
	 * @return
	 * [HResult.Success] TODO RETURN DESCRIPTION
	 *
	 * [HResult.Error] TODO RETURN DESCRIPTION
	 *
	 * [HResult.Empty] TODO RETURN DESCRIPTION
	 *
	 * [HResult.Loading] TODO RETURN DESCRIPTION
	 */
	suspend fun searchBookmarked(string: String): HResult<List<StrippedBookmarkedNovelEntity>>

	/**
	 * Loads the [NovelEntity] by its [novelID]
	 *
	 * @return
	 * [HResult.Success] TODO RETURN DESCRIPTION
	 *
	 * [HResult.Error] TODO RETURN DESCRIPTION
	 *
	 * [HResult.Empty] TODO RETURN DESCRIPTION
	 *
	 * [HResult.Loading] TODO RETURN DESCRIPTION
	 */
	suspend fun loadNovel(novelID: Int): HResult<NovelEntity>

	/**
	 * Loads live data of the [NovelEntity] by its [novelID]
	 *
	 * @return
	 * [HResult.Success] TODO RETURN DESCRIPTION
	 *
	 * [HResult.Error] TODO RETURN DESCRIPTION
	 *
	 * [HResult.Empty] TODO RETURN DESCRIPTION
	 *
	 * [HResult.Loading] TODO RETURN DESCRIPTION
	 */
	suspend fun loadNovelLive(novelID: Int): Flow<HResult<NovelEntity>>

	/**
	 * Inserts the [novelEntity] and returns a UI version of it
	 *
	 * @return
	 * [HResult.Success] TODO RETURN DESCRIPTION
	 *
	 * [HResult.Error] TODO RETURN DESCRIPTION
	 *
	 * [HResult.Empty] TODO RETURN DESCRIPTION
	 *
	 * [HResult.Loading] TODO RETURN DESCRIPTION
	 */
	suspend fun insertNovelReturnCard(novelEntity: NovelEntity): HResult<StrippedNovelEntity>

	/**
	 * Inserts the [novelEntity]
	 *
	 * @return
	 * [HResult.Success] TODO RETURN DESCRIPTION
	 *
	 * [HResult.Error] TODO RETURN DESCRIPTION
	 *
	 * [HResult.Empty] TODO RETURN DESCRIPTION
	 *
	 * [HResult.Loading] TODO RETURN DESCRIPTION
	 */
	suspend fun insertNovel(novelEntity: NovelEntity): HResult<*>

	/**
	 * Updates the [novelEntity]
	 *
	 * @return
	 * [HResult.Success] TODO RETURN DESCRIPTION
	 *
	 * [HResult.Error] TODO RETURN DESCRIPTION
	 *
	 * [HResult.Empty] TODO RETURN DESCRIPTION
	 *
	 * [HResult.Loading] TODO RETURN DESCRIPTION
	 */
	suspend fun updateNovel(novelEntity: NovelEntity): HResult<*>

	/**
	 * Updates a novel entity with new data
	 *
	 * @return
	 * [HResult.Success] TODO RETURN DESCRIPTION
	 *
	 * [HResult.Error] TODO RETURN DESCRIPTION
	 *
	 * [HResult.Empty] TODO RETURN DESCRIPTION
	 *
	 * [HResult.Loading] TODO RETURN DESCRIPTION
	 */
	suspend fun updateNovelData(novelEntity: NovelEntity, novelInfo: Novel.Info): HResult<*>

	/**
	 * Updates a list of bookmarked novels
	 *
	 * @return
	 * [HResult.Success] TODO RETURN DESCRIPTION
	 *
	 * [HResult.Error] TODO RETURN DESCRIPTION
	 *
	 * [HResult.Empty] TODO RETURN DESCRIPTION
	 *
	 * [HResult.Loading] TODO RETURN DESCRIPTION
	 */
	suspend fun updateBookmarkedNovelData(list: List<BookmarkedNovelEntity>): HResult<*>

	/**
	 * Retrieves NovelInfo from it's source
	 *
	 * @return
	 * [HResult.Success] TODO RETURN DESCRIPTION
	 *
	 * [HResult.Error] TODO RETURN DESCRIPTION
	 *
	 * [HResult.Empty] TODO RETURN DESCRIPTION
	 *
	 * [HResult.Loading] TODO RETURN DESCRIPTION
	 */
	suspend fun retrieveNovelInfo(
		formatter: IExtension,
		novelEntity: NovelEntity,
		loadChapters: Boolean,
	): HResult<Novel.Info>

}