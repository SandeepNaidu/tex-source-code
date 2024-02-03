package com.project.tex.post.model


import com.google.gson.annotations.SerializedName

data class PostData(
    @SerializedName("address")
    val address: String? = null,
    @SerializedName("artistId")
    val artistId: String? = null,
    @SerializedName("audioUrl")
    var audioUrl: String? = null,
    @SerializedName("caption")
    val caption: String? = null,
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("documentUrl")
    var documentUrl: String? = null,
    @SerializedName("duration")
    val duration: Int? = null,
    @SerializedName("event")
    val event: String? = null,
    @SerializedName("eventDateTime")
    val eventDateTime: String? = null,
    @SerializedName("eventExternalLink")
    val eventExternalLink: String? = null,
    @SerializedName("eventFormat")
    val eventFormat: String? = null,
    @SerializedName("eventImageUrl")
    val eventImageUrl: String? = null,
    @SerializedName("eventType")
    val eventType: String? = null,
    @SerializedName("hashTag")
    val hashTag: String? = null,
    @SerializedName("imageUrl")
    var imageUrl: String? = null,
    @SerializedName("latlong")
    val latlong: String? = null,
    @SerializedName("options")
    val options: Array<String>? = null,
    @SerializedName("postType")
    var postType: String? = null,
    @SerializedName("question")
    val question: String? = null,
    @SerializedName("thumbUrl")
    var thumbUrl: String? = null,
    @SerializedName("videoUrl")
    var videoUrl: String? = null
) : java.io.Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PostData

        if (address != other.address) return false
        if (artistId != other.artistId) return false
        if (audioUrl != other.audioUrl) return false
        if (caption != other.caption) return false
        if (description != other.description) return false
        if (documentUrl != other.documentUrl) return false
        if (duration != other.duration) return false
        if (event != other.event) return false
        if (eventDateTime != other.eventDateTime) return false
        if (eventExternalLink != other.eventExternalLink) return false
        if (eventFormat != other.eventFormat) return false
        if (eventImageUrl != other.eventImageUrl) return false
        if (eventType != other.eventType) return false
        if (hashTag != other.hashTag) return false
        if (imageUrl != other.imageUrl) return false
        if (latlong != other.latlong) return false
        if (options != null) {
            if (other.options == null) return false
            if (!options.contentEquals(other.options)) return false
        } else if (other.options != null) return false
        if (postType != other.postType) return false
        if (question != other.question) return false
        if (thumbUrl != other.thumbUrl) return false
        if (videoUrl != other.videoUrl) return false

        return true
    }

    override fun hashCode(): Int {
        var result = address?.hashCode() ?: 0
        result = 31 * result + (artistId?.hashCode() ?: 0)
        result = 31 * result + (audioUrl?.hashCode() ?: 0)
        result = 31 * result + (caption?.hashCode() ?: 0)
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + (documentUrl?.hashCode() ?: 0)
        result = 31 * result + (duration ?: 0)
        result = 31 * result + (event?.hashCode() ?: 0)
        result = 31 * result + (eventDateTime?.hashCode() ?: 0)
        result = 31 * result + (eventExternalLink?.hashCode() ?: 0)
        result = 31 * result + (eventFormat?.hashCode() ?: 0)
        result = 31 * result + (eventImageUrl?.hashCode() ?: 0)
        result = 31 * result + (eventType?.hashCode() ?: 0)
        result = 31 * result + (hashTag?.hashCode() ?: 0)
        result = 31 * result + (imageUrl?.hashCode() ?: 0)
        result = 31 * result + (latlong?.hashCode() ?: 0)
        result = 31 * result + (options?.contentHashCode() ?: 0)
        result = 31 * result + (postType?.hashCode() ?: 0)
        result = 31 * result + (question?.hashCode() ?: 0)
        result = 31 * result + (thumbUrl?.hashCode() ?: 0)
        result = 31 * result + (videoUrl?.hashCode() ?: 0)
        return result
    }
}