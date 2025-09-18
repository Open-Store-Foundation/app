package com.openstore.app.data.report

import com.openstore.app.common.strings.RString
import kotlinx.serialization.Serializable
import openstore.core.strings.generated.resources.report_category_cybercrime
import openstore.core.strings.generated.resources.report_category_exploitation
import openstore.core.strings.generated.resources.report_category_financial_crime
import openstore.core.strings.generated.resources.report_category_other
import openstore.core.strings.generated.resources.report_category_privacy
import openstore.core.strings.generated.resources.report_category_trafficking
import openstore.core.strings.generated.resources.report_subcategory_cybercrime_carding
import openstore.core.strings.generated.resources.report_subcategory_cybercrime_hacking
import openstore.core.strings.generated.resources.report_subcategory_cybercrime_malware
import openstore.core.strings.generated.resources.report_subcategory_exploitation_csam
import openstore.core.strings.generated.resources.report_subcategory_exploitation_extremism
import openstore.core.strings.generated.resources.report_subcategory_exploitation_human_trafficking
import openstore.core.strings.generated.resources.report_subcategory_exploitation_violence_for_hire
import openstore.core.strings.generated.resources.report_subcategory_financial_crime_fraud
import openstore.core.strings.generated.resources.report_subcategory_financial_crime_laundering
import openstore.core.strings.generated.resources.report_subcategory_financial_crime_stolen_data
import openstore.core.strings.generated.resources.report_subcategory_privacy_doxing
import openstore.core.strings.generated.resources.report_subcategory_privacy_surveillance
import openstore.core.strings.generated.resources.report_subcategory_trafficking_counterfeit
import openstore.core.strings.generated.resources.report_subcategory_trafficking_narcotics
import openstore.core.strings.generated.resources.report_subcategory_trafficking_stolen_goods
import org.jetbrains.compose.resources.StringResource

@Serializable
enum class ReportCategoryId(val id: Int) {
    TRAFFICKING(1),
    CYBERCRIME(2),
    FINANCIAL_CRIME(3),
    EXPLOITATION(4),
    PRIVACY(5),
    OTHER(6);
}

fun ReportCategoryId.toRString(): StringResource = when (this) {
    ReportCategoryId.TRAFFICKING -> RString.report_category_trafficking
    ReportCategoryId.CYBERCRIME -> RString.report_category_cybercrime
    ReportCategoryId.FINANCIAL_CRIME -> RString.report_category_financial_crime
    ReportCategoryId.EXPLOITATION -> RString.report_category_exploitation
    ReportCategoryId.PRIVACY -> RString.report_category_privacy
    ReportCategoryId.OTHER -> RString.report_category_other
}

@Serializable
enum class ReportSubcategoryId(val id: Int) {
    TRAFFICKING_NARCOTICS(1),
    TRAFFICKING_COUNTERFEIT(2),
    TRAFFICKING_STOLEN_GOODS(3),
    CYBERCRIME_MALWARE(4),
    CYBERCRIME_HACKING(5),
    CYBERCRIME_CARDING(6),
    FINANCIAL_CRIME_LAUNDERING(7),
    FINANCIAL_CRIME_FRAUD(8),
    FINANCIAL_CRIME_STOLEN_DATA(9),
    EXPLOITATION_CSAM(10),
    EXPLOITATION_HUMAN_TRAFFICKING(11),
    EXPLOITATION_VIOLENCE_FOR_HIRE(12),
    EXPLOITATION_TERRORISM(13),
    PRIVACY_DOXING(14),
    PRIVACY_SURVEILLANCE(15);
}

fun ReportSubcategoryId.toRString(): StringResource = when (this) {
    ReportSubcategoryId.TRAFFICKING_NARCOTICS -> RString.report_subcategory_trafficking_narcotics
    ReportSubcategoryId.TRAFFICKING_COUNTERFEIT -> RString.report_subcategory_trafficking_counterfeit
    ReportSubcategoryId.TRAFFICKING_STOLEN_GOODS -> RString.report_subcategory_trafficking_stolen_goods
    ReportSubcategoryId.CYBERCRIME_MALWARE -> RString.report_subcategory_cybercrime_malware
    ReportSubcategoryId.CYBERCRIME_HACKING -> RString.report_subcategory_cybercrime_hacking
    ReportSubcategoryId.CYBERCRIME_CARDING -> RString.report_subcategory_cybercrime_carding
    ReportSubcategoryId.FINANCIAL_CRIME_LAUNDERING -> RString.report_subcategory_financial_crime_laundering
    ReportSubcategoryId.FINANCIAL_CRIME_FRAUD -> RString.report_subcategory_financial_crime_fraud
    ReportSubcategoryId.FINANCIAL_CRIME_STOLEN_DATA -> RString.report_subcategory_financial_crime_stolen_data
    ReportSubcategoryId.EXPLOITATION_CSAM -> RString.report_subcategory_exploitation_csam
    ReportSubcategoryId.EXPLOITATION_HUMAN_TRAFFICKING -> RString.report_subcategory_exploitation_human_trafficking
    ReportSubcategoryId.EXPLOITATION_VIOLENCE_FOR_HIRE -> RString.report_subcategory_exploitation_violence_for_hire
    ReportSubcategoryId.EXPLOITATION_TERRORISM -> RString.report_subcategory_exploitation_extremism
    ReportSubcategoryId.PRIVACY_DOXING -> RString.report_subcategory_privacy_doxing
    ReportSubcategoryId.PRIVACY_SURVEILLANCE -> RString.report_subcategory_privacy_surveillance
}

interface ReportRepo {
    fun getCategories(): List<ReportCategoryId>
    fun getSubcategories(id: ReportCategoryId): List<ReportSubcategoryId>
}

class ReportRepoDefault : ReportRepo {

    override fun getCategories(): List<ReportCategoryId> {
        return ReportCategoryId.entries
    }

    override fun getSubcategories(id: ReportCategoryId): List<ReportSubcategoryId> {
        return when (id) {
            ReportCategoryId.TRAFFICKING -> listOf(
                ReportSubcategoryId.TRAFFICKING_NARCOTICS,
                ReportSubcategoryId.TRAFFICKING_COUNTERFEIT,
                ReportSubcategoryId.TRAFFICKING_STOLEN_GOODS
            )
            ReportCategoryId.CYBERCRIME -> listOf(
                ReportSubcategoryId.CYBERCRIME_MALWARE,
                ReportSubcategoryId.CYBERCRIME_HACKING,
                ReportSubcategoryId.CYBERCRIME_CARDING
            )
            ReportCategoryId.FINANCIAL_CRIME -> listOf(
                ReportSubcategoryId.FINANCIAL_CRIME_LAUNDERING,
                ReportSubcategoryId.FINANCIAL_CRIME_FRAUD,
                ReportSubcategoryId.FINANCIAL_CRIME_STOLEN_DATA
            )
            ReportCategoryId.EXPLOITATION -> listOf(
                ReportSubcategoryId.EXPLOITATION_CSAM,
                ReportSubcategoryId.EXPLOITATION_HUMAN_TRAFFICKING,
                ReportSubcategoryId.EXPLOITATION_VIOLENCE_FOR_HIRE,
                ReportSubcategoryId.EXPLOITATION_TERRORISM,
            )
            ReportCategoryId.PRIVACY -> listOf(
                ReportSubcategoryId.PRIVACY_DOXING,
                ReportSubcategoryId.PRIVACY_SURVEILLANCE
            )
            ReportCategoryId.OTHER -> emptyList()
        }
    }
}