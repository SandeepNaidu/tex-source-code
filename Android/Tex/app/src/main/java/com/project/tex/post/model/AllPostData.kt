package com.project.tex.post.model

import androidx.annotation.Keep
import androidx.core.text.isDigitsOnly
import com.google.gson.annotations.SerializedName
import com.project.tex.main.model.OptionItem
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

@Keep
data class AllPostData(
    @SerializedName("body")
    val body: Body? = null,
    @SerializedName("responseCode")
    val responseCode: Int?
) : java.io.Serializable {
    @Keep
    data class Body(
        @SerializedName("posts")
        val posts: DPost?
    ) : java.io.Serializable {
        @Keep
        data class PostD(
            @SerializedName("data")
            val data: Posts?
        )

        @Keep
        data class Posts(
            @SerializedName("documents")
            val document: List<Document?>?,
            @SerializedName("events")
            val event: List<Event?>?,
            @SerializedName("images")
            val image: List<Image?>?,
            @SerializedName("musics")
            val music: List<Music?>?,
            @SerializedName("polls")
            val poll: List<Poll?>?,
            @SerializedName("videos")
            val video: List<Video?>?
        ) : java.io.Serializable {
            @Keep
            data class Document(
                @SerializedName("address")
                var address: String?,
                @SerializedName("firstName")
                val artistFirstName: String?,
                @SerializedName("lastName")
                val artistLastName: String?,
                @SerializedName("caption")
                val caption: String?,
                @SerializedName("createAt")
                var createAt: String?,
                @SerializedName("documentUrl")
                val documentUrl: String?,
                @SerializedName("hashTag")
                val hashTag: String?,
                @SerializedName("id")
                val id: Int?,
                @SerializedName("artistId")
                val artistId: Int?,
                @SerializedName("latlong")
                val latlong: String?,
                @SerializedName("thumbUrl")
                val thumbUrl: String?,
                @SerializedName("artistProfileImage")
                val artistProfileImage: String?,
                @SerializedName("like_count")
                var likeCount: Int?,
                @SerializedName("share_count")
                var shareCount: Int?,
                @SerializedName("save_count")
                var saveCount: Int?,
                @SerializedName("is_liked")
                var isLiked: Int?,
                @SerializedName("is_saved")
                var isSaved: Int?,
                @SerializedName("is_shared")
                var isShared: Int?
            ) : java.io.Serializable, DateComparable(createAt, likeCount)

            @Keep
            data class Event(
                @SerializedName("address")
                var address: String?,
                @SerializedName("firstName")
                val artistFirstName: String?,
                @SerializedName("lastName")
                val artistLastName: String?,
                @SerializedName("createAt")
                var createAt: String?,
                @SerializedName("description")
                val description: String?,
                @SerializedName("event")
                val event: String?,
                @SerializedName("eventExternalLink")
                val eventExternalLink: String?,
                @SerializedName("eventFormat")
                val eventFormat: String?,
                @SerializedName("eventImageUrl")
                val eventImageUrl: String?,
                @SerializedName("eventType")
                val eventType: String?,
                @SerializedName("hashTag")
                val hashTag: String?,
                @SerializedName("id")
                val id: Int?,
                @SerializedName("artistId")
                val artistId: Int?,
                @SerializedName("artistProfileImage")
                val artistProfileImage: String?,
                @SerializedName("latlong")
                val latlong: String?,
                @SerializedName("like_count")
                var likeCount: Int?,
                @SerializedName("share_count")
                var shareCount: Int?,
                @SerializedName("save_count")
                var saveCount: Int?,
                @SerializedName("is_liked")
                var isLiked: Int?,
                @SerializedName("is_saved")
                var isSaved: Int?,
                @SerializedName("is_shared")
                var isShared: Int?
            ) : java.io.Serializable, DateComparable(createAt, likeCount)

            //{
            //                        "id": 14,
            //                        "firstName": "Super",
            //                        "lastName": "Admin",
            //                        "imageUrl": "imageUrl content ",
            //                        "thumbUrl": "thumbUrl ",
            //                        "caption": "caption ",
            //                        "hashTag": "1,4",
            //                        "latlong": "latlong",
            //                        "address": "address",
            //                        "createAt": "2023-01-30T02:00:17.000Z",
            //                        "like_count": 0,
            //                        "shareCount": 1,
            //                        "save_count": 0,
            //                        "isLiked": 0,
            //                        "isSaved": 0,
            //                        "isShared": 1
            //                    }
            @Keep
            data class Image(
                @SerializedName("address")
                var address: String?,
                @SerializedName("firstName")
                val artistFirstName: String?,
                @SerializedName("lastName")
                val artistLastName: String?,
                @SerializedName("caption")
                val caption: String?,
                @SerializedName("createAt")
                var createAt: String?,
                @SerializedName("hashTag")
                val hashTag: String?,
                @SerializedName("id")
                val id: Int?,
                @SerializedName("artistId")
                val artistId: Int?,
                @SerializedName("imageUrl")
                val imageUrl: String?,
                @SerializedName("latlong")
                val latlong: String?,
                @SerializedName("thumbUrl")
                val thumbUrl: String?,
                @SerializedName("artistProfileImage")
                val artistProfileImage: String?,
                @SerializedName("like_count")
                var likeCount: Int?,
                @SerializedName("share_count")
                var shareCount: Int?,
                @SerializedName("save_count")
                var saveCount: Int?,
                @SerializedName("is_liked")
                var isLiked: Int?,
                @SerializedName("is_saved")
                var isSaved: Int?,
                @SerializedName("is_shared")
                var isShared: Int?
            ) : java.io.Serializable, DateComparable(createAt, likeCount)

            @Keep
            data class Music(
                @SerializedName("address")
                var address: String?,
                @SerializedName("firstName")
                val artistFirstName: String?,
                @SerializedName("lastName")
                val artistLastName: String?,
                @SerializedName("audioUrl")
                val audioUrl: String?,
                @SerializedName("caption")
                val caption: String?,
                @SerializedName("createAt")
                var createAt: String?,
                @SerializedName("hashTag")
                val hashTag: String?,
                @SerializedName("id")
                val id: Int?,
                @SerializedName("artistId")
                val artistId: Int?,
                @SerializedName("latlong")
                val latlong: String?,
                @SerializedName("thumbUrl")
                val thumbUrl: String?,
                @SerializedName("artistProfileImage")
                val artistProfileImage: String?,
                @SerializedName("like_count")
                var likeCount: Int?,
                @SerializedName("share_count")
                var shareCount: Int?,
                @SerializedName("save_count")
                var saveCount: Int?,
                @SerializedName("is_liked")
                var isLiked: Int?,
                @SerializedName("is_saved")
                var isSaved: Int?,
                @SerializedName("is_shared")
                var isShared: Int?
            ) : java.io.Serializable, DateComparable(createAt, likeCount)

            @Keep
            data class Poll(
                @SerializedName("address")
                var address: String?,
                @SerializedName("firstName")
                val artistFirstName: String?,
                @SerializedName("lastName")
                val artistLastName: String?,
                @SerializedName("createAt")
                var createAt: String?,
                @SerializedName("duration")
                val duration: Int?,
                @SerializedName("hashTag")
                val hashTag: String?,
                @SerializedName("id")
                val id: Int?,
                @SerializedName("artistId")
                val artistId: Int?,
                @SerializedName("latlong")
                val latlong: String?,
                @SerializedName("artistProfileImage")
                val artistProfileImage: String?,
                @SerializedName("option")
                val options: Array<OptionItem?>?,
                @SerializedName("question")
                val question: String?,
                @SerializedName("like_count")
                var likeCount: Int?,
                @SerializedName("share_count")
                var shareCount: Int?,
                @SerializedName("save_count")
                var saveCount: Int?,
                @SerializedName("is_voted")
                var isVoted: Int?,
                @SerializedName("is_liked")
                var isLiked: Int?,
                @SerializedName("is_saved")
                var isSaved: Int?,
                @SerializedName("is_shared")
                var isShared: Int?
            ) : java.io.Serializable, DateComparable(createAt, likeCount) {
                override fun equals(other: Any?): Boolean {
                    if (this === other) return true
                    if (javaClass != other?.javaClass) return false

                    other as Poll

                    if (address != other.address) return false
                    if (artistFirstName != other.artistFirstName) return false
                    if (artistLastName != other.artistLastName) return false
                    if (createAt != other.createAt) return false
                    if (duration != other.duration) return false
                    if (hashTag != other.hashTag) return false
                    if (id != other.id) return false
                    if (latlong != other.latlong) return false
                    if (!options.contentEquals(other.options)) return false
                    if (question != other.question) return false
                    if (likeCount != other.likeCount) return false
                    if (shareCount != other.shareCount) return false
                    if (saveCount != other.saveCount) return false
                    if (isVoted != other.isVoted) return false
                    if (isLiked != other.isLiked) return false
                    if (isSaved != other.isSaved) return false
                    if (isShared != other.isShared) return false

                    return true
                }

                override fun hashCode(): Int {
                    var result = address?.hashCode() ?: 0
                    result = 31 * result + (artistFirstName?.hashCode() ?: 0)
                    result = 31 * result + (artistLastName?.hashCode() ?: 0)
                    result = 31 * result + (createAt?.hashCode() ?: 0)
                    result = 31 * result + (duration ?: 0)
                    result = 31 * result + (hashTag?.hashCode() ?: 0)
                    result = 31 * result + (id ?: 0)
                    result = 31 * result + (latlong?.hashCode() ?: 0)
                    result = 31 * result + options.contentHashCode()
                    result = 31 * result + (question?.hashCode() ?: 0)
                    result = 31 * result + (likeCount ?: 0)
                    result = 31 * result + (shareCount ?: 0)
                    result = 31 * result + (saveCount ?: 0)
                    result = 31 * result + (isVoted ?: 0)
                    result = 31 * result + (isLiked ?: 0)
                    result = 31 * result + (isSaved ?: 0)
                    result = 31 * result + (isShared ?: 0)
                    return result
                }
            }

            @Keep
            data class Video(
                @SerializedName("address")
                var address: String?,
                @SerializedName("firstName")
                val artistFirstName: String?,
                @SerializedName("lastName")
                val artistLastName: String?,
                @SerializedName("caption")
                val caption: String?,
                @SerializedName("createAt")
                var createAt: String?,
                @SerializedName("hashTag")
                val hashTag: String?,
                @SerializedName("id")
                val id: Int?,
                @SerializedName("artistId")
                val artistId: Int?,
                @SerializedName("latlong")
                val latlong: String?,
                @SerializedName("thumbUrl")
                val thumbUrl: String?,
                @SerializedName("videoUrl")
                val videoUrl: String?,
                @SerializedName("artistProfileImage")
                val artistProfileImage: String?,
                @SerializedName("like_count")
                var likeCount: Int?,
                @SerializedName("share_count")
                var shareCount: Int?,
                @SerializedName("save_count")
                var saveCount: Int?,
                @SerializedName("is_liked")
                var isLiked: Int?,
                @SerializedName("is_saved")
                var isSaved: Int?,
                @SerializedName("is_shared")
                var isShared: Int?
            ) : java.io.Serializable, DateComparable(createAt, likeCount)


            open class DateComparable : Comparable<String?> {
                public var date: String?
                public var likeCounts: Int?

                constructor(createAt: String?, likeCount: Int?) {
                    this.date = createAt
                    this.likeCounts = likeCount
                }

                fun compareTo(other: Int?): Int {
                    return likeCounts?.compareTo(other ?: 0) ?: 0
                }

                override fun compareTo(other: String?): Int {
                    val date1: LocalDate
                    val date2: LocalDate
                    //2023-01-30T02:00:17.000Z
//                    val formatter: DateTimeFormatter =
//                        DateTimeFormatter.ofPattern(
//                            "yyyy-MM-dd'T'HH:mm:ss.SSSX",
//                            Locale.ENGLISH
//                        )
//                    val date1: LocalDate =
//                        this.date?.let { LocalDate.parse(it, formatter) } ?: LocalDate.now()
//                    val date2: LocalDate =
//                        other?.let { LocalDate.parse(it, formatter) } ?: LocalDate.now()

                    // -------------if use millis uncomment below line -----------
                    if (date?.isDigitsOnly() == true) {
                        val long = date?.toLongOrNull() ?: System.currentTimeMillis()
                        date1 =
                            this.date?.let {
                                Instant.ofEpochMilli(long).atZone(ZoneId.systemDefault())
                                    .toLocalDate()
                            } ?: LocalDate.now()
                        date2 =
                            other?.let {
                                Instant.ofEpochMilli(
                                    other.toLongOrNull() ?: System.currentTimeMillis()
                                ).atZone(ZoneId.systemDefault()).toLocalDate()
                            } ?: LocalDate.now()
                    } else {
                        val formatter: DateTimeFormatter =
                            DateTimeFormatter.ofPattern(
                                "yyyy-MM-dd'T'HH:mm:ss.SSSX",
                                Locale.ENGLISH
                            )
                        date1 =
                            this.date?.let { LocalDate.parse(it, formatter) } ?: LocalDate.now()
                        date2 =
                            other?.let { LocalDate.parse(it, formatter) } ?: LocalDate.now()
                    }
                    return date1.compareTo(date2)
                }
            }
        }
    }

    data class DPost(
        @SerializedName("body")
        val body: Body.PostD?,
        @SerializedName("resultcode")
        val resultcode: Int?
    )
}