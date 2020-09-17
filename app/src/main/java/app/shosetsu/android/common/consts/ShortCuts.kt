package app.shosetsu.android.common.consts

import android.content.Context
import android.content.Intent
import androidx.core.content.pm.ShortcutInfoCompat.Builder
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat.createWithResource
import app.shosetsu.android.ui.splash.SplashScreen
import com.github.doomsdayrs.apps.shosetsu.R

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

/**
 * shosetsu
 * 06 / 09 / 2020
 */
object ShortCuts {
	const val ACTION_OPEN_LIBRARY = "openLibrary"
	const val ACTION_OPEN_CATALOGUE = "openCatalogue"
	const val ACTION_OPEN_UPDATES = "openUpdates"
	const val ACTION_OPEN_SEARCH = "openSearch"

	fun createShortcuts(context: Context) {
		ShortcutManagerCompat.addDynamicShortcuts(context, listOf(
				Builder(context, "Library")
						.setIcon(createWithResource(context, R.drawable.ic_collections_bookmark))
						.setLongLabel(context.getString(R.string.my_library))
						.setShortLabel(context.getString(R.string.my_library))
						.setIntent(Intent(context, SplashScreen::class.java).apply {
							action = ACTION_OPEN_LIBRARY
						})
						.build(),
				Builder(context, "Browse")
						.setIcon(createWithResource(context, R.drawable.ic_view_module))
						.setLongLabel(context.getString(R.string.browse))
						.setShortLabel(context.getString(R.string.browse))
						.setIntent(Intent(context, SplashScreen::class.java).apply {
							action = ACTION_OPEN_CATALOGUE
						})
						.build(),
				Builder(context, "Updates")
						.setIcon(createWithResource(context, R.drawable.ic_update_24dp))
						.setLongLabel(context.getString(R.string.updates))
						.setShortLabel(context.getString(R.string.updates))
						.setIntent(Intent(context, SplashScreen::class.java).apply {
							action = ACTION_OPEN_UPDATES
						})
						.build(),
				Builder(context, "Search")
						.setIcon(createWithResource(context, R.drawable.ic_search_24dp))
						.setLongLabel(context.getString(R.string.search))
						.setShortLabel(context.getString(R.string.search))
						.setIntent(Intent(context, SplashScreen::class.java).apply {
							action = ACTION_OPEN_SEARCH
						})
						.build()
		))
	}
}