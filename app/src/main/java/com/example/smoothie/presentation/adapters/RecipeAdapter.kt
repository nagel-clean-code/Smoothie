package com.example.smoothie.presentation.adapters

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.smoothie.R
import com.example.smoothie.data.storage.models.RecipeEntity
import com.example.smoothie.databinding.ItemRecipeBinding
import com.example.smoothie.presentation.images.GlideApp
import com.google.firebase.storage.FirebaseStorage

class RecipeAdapter(
    private val listener: Listener
) : PagingDataAdapter<RecipeEntity, RecipeAdapter.RecipeViewHolder>(ArticleDiffItemCallback), View.OnClickListener {

    override fun onClick(p0: View?) {
        val recipe = p0?.tag as RecipeEntity
        if (p0.id == R.id.starImageView) {
            listener.onToggleFavoriteFlag(recipe)
        } else if (p0.id == R.id.deleteImageView) {
            listener.onRecipeDelete(recipe)
        }else{
            listener.displayChooseElement(recipe)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val inflate = LayoutInflater.from(parent.context)
        val binding = ItemRecipeBinding.inflate(inflate, parent, false)

        binding.starImageView.setOnClickListener(this)
        binding.deleteImageView.setOnClickListener(this)

        binding.root.setOnClickListener(this)
        return RecipeViewHolder(binding)
    }

    companion object{
        val ref = FirebaseStorage.getInstance("gs://smoothie-40dd3.appspot.com").reference
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = getItem(position) ?: return
        with(holder.binding) {
            holder.itemView.tag = recipe
            nameRecipe.text = recipe.name
            if (recipe.imageUrl.isNotBlank()) {
                GlideApp.with(image.context)
                    .load(ref.child(recipe.imageUrl))
                    .centerCrop()
                    .placeholder(R.drawable.ic_baseline_image_48)
                    .error(R.drawable.ic_baseline_broken_image_48)
                    .into(image)
            } else {
                image.setImageResource(R.drawable.ic_baseline_image_48)
            }
            userProgressBar.alpha = if (recipe.inProgress) 1f else 0f
            starImageView.isInvisible = recipe.inProgress
            deleteImageView.isInvisible = recipe.inProgress

            setIsFavorite(starImageView, recipe.isFavorite)

            starImageView.tag = recipe
            deleteImageView.tag = recipe
        }
    }


    private fun setIsFavorite(starImageView: ImageView, isFavorite: Boolean) {
        val context = starImageView.context
        if (isFavorite) {
            starImageView.setImageResource(R.drawable.ic_star)
            starImageView.imageTintList = ColorStateList.valueOf(
                ContextCompat.getColor(context, R.color.active)
            )
        } else {
            starImageView.setImageResource(R.drawable.ic_star_outline)
            starImageView.imageTintList = ColorStateList.valueOf(
                ContextCompat.getColor(context, R.color.inactive)
            )
        }
    }

    private object ArticleDiffItemCallback : DiffUtil.ItemCallback<RecipeEntity>() {

        override fun areItemsTheSame(oldItem: RecipeEntity, newItem: RecipeEntity): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: RecipeEntity, newItem: RecipeEntity): Boolean {
            return oldItem.imageUrl == newItem.imageUrl &&
                    oldItem.idRecipe == newItem.idRecipe &&
                    oldItem.name == newItem.name
        }
    }

    class RecipeViewHolder(
        val binding: ItemRecipeBinding
    ) : RecyclerView.ViewHolder(binding.root)

    interface Listener {
        /**
         * Called when the user taps the "Delete" button in a list item
         */
        fun onRecipeDelete(recipeEntity: RecipeEntity)

        /**
         * Called when the user taps the "Star" button in a list item.
         */
        fun onToggleFavoriteFlag(recipeEntity: RecipeEntity)

        fun displayChooseElement(recipeEntity: RecipeEntity)
    }
}