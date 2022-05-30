package com.example.smoothie.presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.smoothie.R
import com.example.smoothie.data.storage.models.RecipeEntity
import com.example.smoothie.databinding.ItemRecipeBinding
import com.example.smoothie.presentation.images.GlideApp
import com.google.firebase.storage.FirebaseStorage

class RecipeAdapter() : PagingDataAdapter<RecipeEntity, RecipeAdapter.RecipeViewHolder>(ArticleDiffItemCallback), View.OnClickListener {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val inflate = LayoutInflater.from(parent.context)
        val binding = ItemRecipeBinding.inflate(inflate, parent, false)

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
        }
    }

    override fun onClick(p0: View?) {
        val recipe = p0?.tag as RecipeEntity
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
}