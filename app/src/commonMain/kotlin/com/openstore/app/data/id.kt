package com.openstore.app.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.intl.Locale
import com.openstore.app.common.strings.RString
import com.openstore.app.core.common.lazyUnsafe
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import openstore.core.strings.generated.resources.*
import org.jetbrains.compose.resources.StringResource

// --- AppCategoryId ---
enum class CategoryId(
    val id: Int,
    val objectTypeId: ObjTypeId,
    val displayRes: () -> StringResource,
    val icon: () -> ImageVector,
) {
        // App Categories
        BOOKS(1,  ObjTypeId.APP, { RString.books }, { Icons.Filled.Book }),
        BUSINESS(2,  ObjTypeId.APP, { RString.business }, { Icons.Filled.BusinessCenter }),
        TOOLS(3,  ObjTypeId.APP, { RString.tools }, { Icons.Filled.Build }),
        EDUCATION(4,  ObjTypeId.APP, { RString.education }, { Icons.Filled.School }),
        ENTERTAINMENT(5,  ObjTypeId.APP, { RString.entertainment }, { Icons.Filled.Theaters }),
        FINANCE(6,  ObjTypeId.APP, { RString.finance }, { Icons.Filled.AttachMoney }),
        FOOD_AND_DRINK(7,  ObjTypeId.APP, { RString.food_and_drink }, { Icons.Filled.Restaurant }),
        GRAPHICS_AND_DESIGN(8,  ObjTypeId.APP, { RString.graphics_and_design }, { Icons.Filled.Palette }),
        HEALTH_AND_FITNESS(9,  ObjTypeId.APP, { RString.health_and_fitness }, { Icons.Filled.FitnessCenter }),
        LIFESTYLE(10, ObjTypeId.APP, { RString.lifestyle }, { Icons.Filled.Spa }),
        NEWSPAPERS(11, ObjTypeId.APP, { RString.newspapers }, { Icons.Filled.Newspaper }),
        MEDICAL(12, ObjTypeId.APP, { RString.medical }, { Icons.Filled.MedicalServices }),
        APP_MUSIC(13, ObjTypeId.APP, { RString.app_music }, { Icons.Filled.MusicNote }), // Differentiated nameResId
        NAVIGATION(14, ObjTypeId.APP, { RString.navigation }, { Icons.Filled.Navigation }),
        NEWS(15, ObjTypeId.APP, { RString.news }, { Icons.Filled.Feed }),
        PHOTO_AND_VIDEO(16, ObjTypeId.APP, { RString.photo_and_video }, { Icons.Filled.PhotoCamera }),
        PRODUCTIVITY(17, ObjTypeId.APP, { RString.productivity }, { Icons.Filled.Checklist }),
        SHOPPING(18, ObjTypeId.APP, { RString.shopping }, { Icons.Filled.ShoppingCart }),
        SOCIAL_NETWORKING(19, ObjTypeId.APP, { RString.social_networking }, { Icons.Filled.Group }),
        APP_SPORTS(20, ObjTypeId.APP, { RString.app_sports }, { Icons.Filled.SportsSoccer }), // Differentiated nameResId
        TRAVEL(21, ObjTypeId.APP, { RString.travel }, { Icons.Filled.FlightTakeoff }),
        UTILITIES(22, ObjTypeId.APP, { RString.utilities }, { Icons.Filled.Settings }),
        WEATHER(23, ObjTypeId.APP, { RString.weather }, { Icons.Filled.WbSunny }),

        // Game Categories
        ACTION(101,  ObjTypeId.GAME, { RString.action }, { Icons.Filled.SportsEsports }),
        ADVENTURE(102, ObjTypeId.GAME, { RString.adventure }, { Icons.Filled.Explore }),
        ARCADE(103, ObjTypeId.GAME, { RString.arcade }, { Icons.Filled.VideogameAsset }),
        BOARD(104, ObjTypeId.GAME, { RString.board }, { Icons.Filled.Dashboard }), // Casino or GridOn could also fit
        CARD(105, ObjTypeId.GAME, { RString.card }, { Icons.Filled.Style }), // FilterNone could also fit
        CASINO(106, ObjTypeId.GAME, { RString.casino }, { Icons.Filled.Casino }),
        CASUAL(107, ObjTypeId.GAME, { RString.casual }, { Icons.Filled.Celebration }),
        DICE(108, ObjTypeId.GAME, { RString.dice }, { Icons.Filled.Casino }), // Reusing Casino icon
        EDUCATIONAL(109, ObjTypeId.GAME, { RString.educational }, { Icons.Filled.Lightbulb }), // Differentiate from APP Education
        FAMILY(110, ObjTypeId.GAME, { RString.family }, { Icons.Filled.FamilyRestroom }),
        GAME_MUSIC(111, ObjTypeId.GAME, { RString.game_music }, { Icons.Filled.MusicNote }), // Same icon as APP Music
        PUZZLE(112, ObjTypeId.GAME, { RString.puzzle }, { Icons.Filled.Extension }),
        RACING(113, ObjTypeId.GAME, { RString.racing }, { Icons.Filled.Speed }),
        ROLE_PLAYING(114, ObjTypeId.GAME, { RString.role_playing }, { Icons.Filled.TheaterComedy }),
        SIMULATION(115, ObjTypeId.GAME, { RString.simulation }, { Icons.Filled.PrecisionManufacturing }), // Build could also fit
        GAME_SPORTS(116, ObjTypeId.GAME, { RString.game_sports }, { Icons.Filled.SportsSoccer }), // Same icon as APP Sports
        STRATEGY(117, ObjTypeId.GAME, { RString.strategy }, { Icons.Filled.Castle }),
        TRIVIA(118, ObjTypeId.GAME, { RString.trivia }, { Icons.Filled.Quiz }),
        WORD(119, ObjTypeId.GAME, { RString.word },{ Icons.Filled.Abc });

    val formattedName by lazyUnsafe { name.lowercase() }

    companion object {
        private val map = entries.associateBy(CategoryId::id)
        private val apps = entries.filter { it.objectTypeId == ObjTypeId.APP }
        private val games = entries.filter { it.objectTypeId == ObjTypeId.GAME }
        fun byId(id: Int): CategoryId? = map[id]
        fun requireById(id: Int): CategoryId = map[id]!!
        fun byType(type: ObjTypeId): List<CategoryId> = when (type) {
            ObjTypeId.APP -> apps
            ObjTypeId.GAME -> games
            else -> emptyList()
        }
    }
}

// --- TrackId ---
@Serializable // Enable serialization for this enum
enum class TrackId(val id: Int) {
    @SerialName("release")
    RELEASE(1),

    @SerialName("beta")
    BETA(2),

    @SerialName("alpha")
    ALPHA(3);

    companion object {
        private val map = entries.associateBy(TrackId::id)
        private val serialNameMap = entries.associateBy {
            when (it) {
                RELEASE -> "release"
                BETA -> "beta"
                ALPHA -> "alpha"
            }
        }
        fun byId(id: Int): TrackId? = map[id]
        fun fromSerialName(name: String): TrackId? = serialNameMap[name]
    }

    // Override toString to match Rust's display("...")
    override fun toString(): String {
        return when (this) {
            RELEASE -> "release"
            BETA -> "beta"
            ALPHA -> "alpha"
        }
    }
}

// --- PlatformId ---
@Serializable
enum class PlatformId(val id: Int) {
    @SerialName("unspecified")
    PROD_UNSPECIFIED(0),

    @SerialName("android")
    ANDROID(1),

    @SerialName("ios")
    IOS(2),

    @SerialName("windows")
    WINDOWS(3),

    @SerialName("macos")
    MACOS(4),

    @SerialName("linux")
    LINUX(5),

    @SerialName("web")
    WEB(6),

    @SerialName("cli")
    CLI(7),

    @SerialName("all")
    ALL(100);

    // Optional: Helper to find enum by ID
    companion object {
        private val map = entries.associateBy(PlatformId::id)
        private val serialNameMap = entries.associateBy {
            when (it) {
                PROD_UNSPECIFIED -> "unspecified"
                ANDROID -> "android"
                IOS -> "ios"
                WINDOWS -> "windows"
                MACOS -> "macos"
                LINUX -> "linux"
                WEB -> "web"
                CLI -> "cli"
                ALL -> "all"
            }
        }
        fun byId(id: Int): PlatformId? = map[id]
        fun fromSerialName(name: String): PlatformId? = serialNameMap[name]
    }

    // Override toString to match Rust's display("...")
    override fun toString(): String {
        return when (this) {
            PROD_UNSPECIFIED -> "unspecified"
            ANDROID -> "android"
            IOS -> "ios"
            WINDOWS -> "windows"
            MACOS -> "macos"
            LINUX -> "linux"
            WEB -> "web"
            CLI -> "cli"
            ALL -> "all"
        }
    }
}

@Serializable // Enable serialization for this enum
enum class ObjTypeId(val id: Int) {
    // Use @SerialName to match Rust's serde(rename = "...")
    @SerialName("unspecified")
    UNSPECIFIED(0),

    @SerialName("app")
    APP(1),

    @SerialName("game")
    GAME(2),

    @SerialName("site")
    SITE(3);

    // Optional: Helper to find enum by ID
    companion object {
        private val map = entries.associateBy(ObjTypeId::id)
        private val serialNameMap = entries.associateBy {
            // Map constant to its serial name for lookup
            when (it) {
                UNSPECIFIED -> "unspecified"
                APP -> "app"
                GAME -> "game"
                SITE -> "site"
            }
        }
        fun byId(id: Int): ObjTypeId? = map[id]
        fun requireById(id: Int): ObjTypeId = map[id]!!
        fun fromSerialName(name: String): ObjTypeId? = serialNameMap[name]
    }

    // Override toString to match Rust's display("...")
    override fun toString(): String {
        // In this case, the display name is the same as the serial name
        return when (this) {
            UNSPECIFIED -> "unspecified"
            APP -> "app"
            GAME -> "game"
            SITE -> "site"
        }
    }
}
