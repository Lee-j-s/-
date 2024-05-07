import android.os.Parcel
import android.os.Parcelable

data class ContentModel(
    val title: String = "",
    val content: String = "",
    val time: String = "",
    val id: String = "",
    val userEmail: String = "", // 사용자 이메일 추가
    val imageUrl: String = "",
    var likes: Int = 0
) : Parcelable {

    // Parcelable 구현을 위한 생성자
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "", // 사용자 이메일 읽기
        parcel.readString() ?: "",
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(content)
        parcel.writeString(time)
        parcel.writeString(id)
        parcel.writeString(userEmail) // 사용자 이메일 쓰기
        parcel.writeString(imageUrl)
        parcel.writeInt(likes)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<ContentModel> {
        override fun createFromParcel(parcel: Parcel): ContentModel {
            return ContentModel(parcel)
        }

        override fun newArray(size: Int): Array<ContentModel?> {
            return arrayOfNulls(size)
        }
    }
}
