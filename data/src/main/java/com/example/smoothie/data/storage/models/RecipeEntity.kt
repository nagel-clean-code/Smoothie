package com.example.smoothie.data.storage.models

import android.os.Parcel
import android.os.Parcelable
import com.example.smoothie.domain.models.IRecipeModel

data class RecipeEntity(
    override var idRecipe: Int = -1,
    override val uniqueId: String? = "",                         //userName_$id
    override val name: String? = "",
    override val recipe: String? = "",
    override val listCategory: List<Int>? = mutableListOf(),
    override var imageUrl: String? = "",
    override var isFavorite: Boolean = false,
    override var inProgress: Boolean = false
): IRecipeModel, Parcelable{


    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        mutableListOf(),
        parcel.readString(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte()
    ) {
    }

    override fun map(): HashMap<String, Any> {
        return hashMapOf(
            "idRecipe" to idRecipe,
            "uniqueId" to uniqueId!!,
            "name" to name!!,
            "recipe" to recipe!!,
            "listCategory1" to listCategory!!,
            "imageUrl" to imageUrl!!,
            "isFavorite" to isFavorite
        )
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(idRecipe)
        parcel.writeString(uniqueId)
        parcel.writeString(name)
        parcel.writeString(recipe)
        parcel.writeString(imageUrl)
        parcel.writeByte(if (isFavorite) 1 else 0)
        parcel.writeByte(if (inProgress) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RecipeEntity> {
        override fun createFromParcel(parcel: Parcel): RecipeEntity {
            return RecipeEntity(parcel)
        }

        override fun newArray(size: Int): Array<RecipeEntity?> {
            return arrayOfNulls(size)
        }
    }


}
