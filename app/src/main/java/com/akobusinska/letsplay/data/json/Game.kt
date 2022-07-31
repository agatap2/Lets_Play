package com.akobusinska.letsplay.data.json


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Game(
    @SerializedName("id")
    @Expose
    val id: String?,

    @SerializedName("handle")
    @Expose
    val handle: String?,

    @SerializedName("url")
    @Expose
    val url: String?,

    @SerializedName("edit_url")
    @Expose
    val editUrl: String?,

    @SerializedName("name")
    @Expose
    val name: String?,

    @SerializedName("price")
    @Expose
    val price: String?,

    @SerializedName("price_ca")
    @Expose
    val priceCa: String?,

    @SerializedName("price_uk")
    @Expose
    val priceUk: String?,

    @SerializedName("price_au")
    @Expose
    val priceAu: String?,

    @SerializedName("msrp")
    @Expose
    val msrp: Double?,

    @SerializedName("msrps")
    @Expose
    val msrps: List<Msrp>?,

    @SerializedName("discount")
    @Expose
    val discount: String?,

    @SerializedName("year_published")
    @Expose
    val yearPublished: Int?,

    @SerializedName("min_players")
    @Expose
    val minPlayers: Int?,

    @SerializedName("max_players")
    @Expose
    val maxPlayers: Int?,

    @SerializedName("min_playtime")
    @Expose
    val minPlaytime: Int?,

    @SerializedName("max_playtime")
    @Expose
    val maxPlaytime: Int?,

    @SerializedName("min_age")
    @Expose
    val minAge: Int?,

    @SerializedName("description")
    @Expose
    val description: String?,

    @SerializedName("commentary")
    @Expose
    val commentary: String?,

    @SerializedName("faq")
    @Expose
    val faq: String?,

    @SerializedName("thumb_url")
    @Expose
    val thumbUrl: String?,

    @SerializedName("image_url")
    @Expose
    val imageUrl: String?,

    @SerializedName("matches_specs")
    @Expose
    val matchesSpecs: Any?,

    @SerializedName("specs")
    @Expose
    val specs: List<Any>?,

    @SerializedName("mechanics")
    @Expose
    val mechanics: List<Mechanic>?,

    @SerializedName("categories")
    @Expose
    val categories: List<Category>?,

    @SerializedName("publishers")
    @Expose
    val publishers: List<Publisher>?,

    @SerializedName("designers")
    @Expose
    val designers: List<Designer>?,

    @SerializedName("primary_publisher")
    @Expose
    val primaryPublisher: PrimaryPublisher?,

    @SerializedName("primary_designer")
    @Expose
    val primaryDesigner: PrimaryDesigner?,

    @SerializedName("developers")
    @Expose
    val developers: List<Any>?,

    @SerializedName("related_to")
    @Expose
    val relatedTo: List<Any>?,

    @SerializedName("related_as")
    @Expose
    val relatedAs: List<String>?,

    @SerializedName("artists")
    @Expose
    val artists: List<String>?,

    @SerializedName("names")
    @Expose
    val names: List<Any>?,

    @SerializedName("rules_url")
    @Expose
    val rulesUrl: String?,

    @SerializedName("official_url")
    @Expose
    val officialUrl: String?,

    @SerializedName("sell_sheet_url")
    @Expose
    val sellSheetUrl: Any?,

    @SerializedName("store_images_url")
    @Expose
    val storeImagesUrl: Any?,

    @SerializedName("comment_count")
    @Expose
    val commentCount: Int?,

    @SerializedName("num_user_ratings")
    @Expose
    val numUserRatings: Int?,

    @SerializedName("average_user_rating")
    @Expose
    val averageUserRating: Double?,

    @SerializedName("historical_low_prices")
    @Expose
    val historicalLowPrices: List<HistoricalLowPrice>?,

    @SerializedName("active")
    @Expose
    val active: Boolean?,

    @SerializedName("num_user_complexity_votes")
    @Expose
    val numUserComplexityVotes: Int?,

    @SerializedName("average_learning_complexity")
    @Expose
    val averageLearningComplexity: Double?,

    @SerializedName("average_strategy_complexity")
    @Expose
    val averageStrategyComplexity: Double?,

    @SerializedName("visits")
    @Expose
    val visits: Int?,

    @SerializedName("lists")
    @Expose
    val lists: Int?,

    @SerializedName("mentions")
    @Expose
    val mentions: Int?,

    @SerializedName("links")
    @Expose
    val links: Int?,

    @SerializedName("plays")
    @Expose
    val plays: Int?,

    @SerializedName("rank")
    @Expose
    val rank: Int?,

    @SerializedName("type")
    @Expose
    val type: String?,

    @SerializedName("sku")
    @Expose
    val sku: String?,

    @SerializedName("upc")
    @Expose
    val upc: String?,

    @SerializedName("isbn")
    @Expose
    val isbn: String?,

    @SerializedName("video_links")
    @Expose
    val videoLinks: List<Any>?,

    @SerializedName("availability_status")
    @Expose
    val availabilityStatus: String?,

    @SerializedName("num_distributors")
    @Expose
    val numDistributors: Int?,

    @SerializedName("trending_rank")
    @Expose
    val trendingRank: Int?,

    @SerializedName("listing_clicks")
    @Expose
    val listingClicks: Int?,

    @SerializedName("is_historical_low")
    @Expose
    val isHistoricalLow: Boolean?,

    @SerializedName("skus")
    @Expose
    val skus: List<String>?,

    @SerializedName("sku_objects")
    @Expose
    val skuObjects: List<SkuObject>?,

    @SerializedName("players")
    @Expose
    val players: String?,

    @SerializedName("playtime")
    @Expose
    val playtime: String?,

    @SerializedName("msrp_text")
    @Expose
    val msrpText: String?,

    @SerializedName("price_text")
    @Expose
    val priceText: String?,

    @SerializedName("tags")
    @Expose
    val tags: List<String>?,

    @SerializedName("images")
    @Expose
    val images: Images?,

    @SerializedName("description_preview")
    @Expose
    val descriptionPreview: String?
)