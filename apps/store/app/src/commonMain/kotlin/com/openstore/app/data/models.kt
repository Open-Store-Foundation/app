package com.openstore.app.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.openstore.app.screens.format.formatBigNumber
import com.openstore.app.screens.format.formatBytes
import com.openstore.app.ui.text.shrinkAddress
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.JsonClassDiscriminator
import com.openstore.app.core.common.Time
import com.openstore.app.core.common.lazyUnsafe
import io.ktor.http.URLBuilder

@Serializable
data class User(
    val id: Int,
    val address: String
)

@Serializable
data class NewUser(
    val address: String
)

@Serializable
data class Category(
    val id: Int,
    val name: String,
    val categoryId: Int
) {
    val category by lazyUnsafe {
        CategoryId.requireById(id)
    }
}

@Serializable
data class Author(
    val address: String,
    val name: String
)

@Serializable
sealed interface ObjectId {
    @Serializable
    data class Id(val id: Long) : ObjectId

    @Serializable
    data class Address(val address: String) : ObjectId
}

@Entity(
    tableName = "object",
    indices = [
        Index(value = ["address"], unique = true),
        Index(value = ["packageName"], unique = false),
        Index(value = ["createdAt"], unique = false),
    ]
)
@Serializable
data class Asset(
    @PrimaryKey val id: Long,
    val name: String,
    val packageName: String,
    val address: String,

    val website: String?,
    val logo: String?,
    val description: String?,

    val categoryId: Int,
    val platformId: Int,
    val typeId: Int,

    val isOsVerified: Boolean,

    val rating: Float,
    val price: Long,
    val downloads: Long,

    @Transient
    val createdAt: Long = Time.nowMs(),

    // Rich
    val isHidden: Boolean = false,
    val isOracleVerified: Boolean = true,
    val isBuildVerified: Boolean = true,
) {
    val isAttached get() = id >= 0

    val category by lazyUnsafe {
        CategoryId.requireById(categoryId)
    }

    val platform by lazyUnsafe {
        PlatformId.byId(platformId)
    }

    val type by lazyUnsafe {
        ObjTypeId.requireById(typeId)
    }

    val domain by lazyUnsafe {
        website?.let {
            runCatching { URLBuilder(it).host }
                .getOrNull()
        }
    }

    val isDangerous by lazyUnsafe {
        isBuildVerified == false || isOracleVerified == false
    }

    val hasCheckmark by lazyUnsafe {
        isOsVerified
    }

    val formattedAddress by lazyUnsafe {
        address.shrinkAddress()
    }

    val formatedRating by lazyUnsafe {
        "${(rating * 100).toLong() / 100.0}"
    }

    val formatedDownload by lazyUnsafe {
        formatBigNumber(downloads)
    }
}

@Serializable
data class Review(
    val id: Long,
    val assetId: Long,
    val userId: String,
    val rating: Int,
    val text: String?
)

@Serializable
data class NewReview(
    val assetId: Long,
    val userId: String,
    val rating: Int,
    val text: String? 
)

@Serializable
data class NewReport(
    val assetAddress: String,
    val categoryId: Int,
    val subcategoryId: Int?,
    val email: String,
    val description: String?
)

@Serializable
data class Artifact(
    val id: Long, 
    val refId: String,
    val checksum: String,
    val protocolId: Int,
    val size: Long, 
    val versionName: String?, 
    val versionCode: Long
) {
    val formattedSize by lazyUnsafe {
        formatBytes(size)
    }

    fun formatedSize(loaded: Int): String {
        val progress = loaded / 100f
        return formatBytes((size * progress).toLong())
    }
}

@Serializable
data class TrackWithArtifact(
    val track: Track,
    val artifact: Artifact,
)

@Serializable
data class Track(
    val id: Int,
    val name: Int, 
    val objTypeId: Int 
)

@Serializable
data class Achievement(
    val id: Int,
    val name: String,
    val value: String?, 
    val assetId: Long
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("type") // Still uses "type" field in JSON
sealed interface TitleType {
    @Serializable
    @SerialName("popular_in_category")
    data object PopularCategories : TitleType
    @Serializable
    @SerialName("new_releases")
    data object NewReleases : TitleType
    @Serializable
    @SerialName("top_chart")
    data object TopChart : TitleType
    @Serializable
    @SerialName("best_in_categories")
    data object BestInCategories : TitleType
    @Serializable
    @SerialName("best_in_category")
    data class BestInCategory(val categoryId: Int) : TitleType {
        val category by lazyUnsafe { CategoryId.requireById(categoryId) }
    }
}

@Serializable
data class Feed(
    @SerialName("sections")
    val sections: List<Section>,
) {
    @OptIn(ExperimentalSerializationApi::class)
    @Serializable
    @JsonClassDiscriminator("type")
    sealed class Section {

        @Serializable
        @SerialName("banner")
        data class Banner(
            val assets: List<Asset>,
            val covers: List<String>, // == objects.size
        ) : Section()

        @Serializable
        @SerialName("h_list")
        data class HList(
            val assets: List<Asset>,
            val covers: List<String>, // == objects.size
            val title: TitleType? = null,
        ) : Section()

        @Serializable
        @SerialName("v_list")
        data class VList(
            val assets: List<Asset>,
            val title: TitleType? = null,
        ) : Section()

        @Serializable
        @SerialName("highlight")
        data class Highlight(
            val target: Asset,
            val covers: List<String>, // >= 1
        ) : Section()

        @Serializable
        @SerialName("categories")
        data class Categories(
            val categories: List<Int>,
            val title: TitleType? = null,
        ) : Section()

        @Serializable
        @SerialName("header")
        data class Header(
            val title: TitleType,
        ) : Section()
    }
}
