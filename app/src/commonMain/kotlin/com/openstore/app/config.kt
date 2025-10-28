@file:Suppress("PropertyName", "ConstPropertyName")

package com.openstore.app

import coil3.toUri
import com.openstore.app.data.node.AppNodes

object AppConfig {
    val Env = EnvConfig.Lh
    val Social = SocialLinks

    val Nodes = AppNodes(
        defaultApiUrl = Env.ApiUrl,
        defaultBscUrl = Env.NodeUrl,
        defaultGreenfieldUrl = Env.GfUrl,
    )
}

interface EnvConfig {

    val Caip2: String
    val StoreAppAddress: String
    val StoreAddress: String
    val OracleAddress: String

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
        override val Caip2: String = "eip155:97"
        override val StoreAppAddress: String = "0x0F02715D4EBB9ABCC498F1A6ABF683857A0F3123" // TODO change
        override val StoreAddress: String = "0x4dc802c0E64Eb0C9d9b278F70b6a7d6e21908a46"
        override val OracleAddress: String = "0xCA21F6ab7D9Cf14444028394016066778Cbe1B4B"

        override val ApiUrl: String = "https://api.openstore.foundation"
        override val NodeUrl: String = "https://bsctest.node.openstore.foundation"
        override val GfUrl: String = "https://gnfdtest.openstore.foundation"
        override val ScanUrl: String = "https://testnet.bscscan.com"
    }

    object Lh : EnvConfig {
        override val Caip2: String = "eip155:31337"
        override val StoreAppAddress: String = "0x0F02715D4EBB9ABCC498F1A6ABF683857A0F3123"
        override val StoreAddress: String = "0x0165878A594ca255338adfa4d48449f69242Eb8F"
        override val OracleAddress: String = "0xDc64a140Aa3E981100a9becA4E685f962f0cF6C9"

//        override val ApiUrl: String = "https://localhost:8081"
        override val ApiUrl: String = "http://192.168.0.9:8081"
//        override val NodeUrl: String = "http://localhost:8545"
        override val NodeUrl: String = "http://192.168.0.9:8545"
        override val GfUrl: String = "https://gnfdtest.openstore.foundation"
        override val ScanUrl: String = ""
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