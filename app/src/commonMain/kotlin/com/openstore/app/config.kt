@file:Suppress("PropertyName", "ConstPropertyName")

package com.openstore.app

import coil3.toUri
import com.openstore.app.data.node.AppNodes

object AppConfig {
    val Env = EnvConfig.BscTest
    val Social = SocialLinks

    val Nodes = AppNodes(
        defaultApiUrl = Env.ApiUrl,
        defaultBscUrl = Env.NodeUrl,
        defaultGreenfieldUrl = Env.GfUrl,
    )
}

interface EnvConfig {

    val StoreAppAddress: String
    val StoreAddress: String

    val ApiUrl: String
    val NodeUrl: String
    val GfUrl: String
    val ScanUrl: String

    fun explorer(address: String): String {
        return ScanUrl.toUri()
            .newBuilder()
            .path("/address/${address}")
            .build()
            .toString()
    }

    fun assetlinks(website: String): String {
        return website.toUri()
            .newBuilder()
            .path("/.well-known/assetlinks.json")
            .build()
            .toString()
    }

    object BscTest : EnvConfig {
        override val StoreAppAddress: String = "0x0F02715D4EBB9ABCC498F1A6ABF683857A0F3123"
        override val StoreAddress: String = "0x6Edac88EA58168a47ab61836bCbAD0Ac844498A6"

        override val ScanUrl: String = "https://testnet.bscscan.com"
        override val ApiUrl: String = "https://api.openstore.foundation"
        override val NodeUrl: String = "https://bsctest.node.openstore.foundation"
        override val GfUrl: String = "https://gnfdtest.openstore.foundation"
    }

    object Lh : EnvConfig {
        override val StoreAppAddress: String = "0x0F02715D4EBB9ABCC498F1A6ABF683857A0F3123"
        override val StoreAddress: String = "0x0165878A594ca255338adfa4d48449f69242Eb8F"

        override val ScanUrl: String = "https://testnet.bscscan.com"
        override val ApiUrl: String = "https://localhost:8080"
        override val NodeUrl: String = "https://localhost:8545"
        override val GfUrl: String = "https://gnfdtest.openstore.foundation"
    }
}

object SocialLinks {
    const val X = "https://x.com/openstorefndn"
    const val TgNews = "https://t.me/openstore_news"
    const val TgHelp = "https://t.me/openstore_community"
    const val Discord = "https://discord.gg/CPmjuPNt"
}

object Links {
    const val Terms = "https://docs.openstore.foundation/terms-of-service"
    const val Privacy = "https://docs.openstore.foundation/privacy-policy"
}