package com.example.smoothie.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.smoothie.databinding.PartDefaultLoadStateBinding

typealias TryAgainAction = () -> Unit

class DefaultLoadStateAdapter(
    private val tryAgainAction: TryAgainAction
): LoadStateAdapter<DefaultLoadStateAdapter.Holder>() {


    override fun onBindViewHolder(holder: Holder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): Holder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = PartDefaultLoadStateBinding.inflate(inflater, parent, false)

        return Holder(binding, tryAgainAction, null)
    }

    class Holder(
        private val binding: PartDefaultLoadStateBinding,
        private val tryAgainAction: (TryAgainAction),
        private val swipeRefreshLayout: SwipeRefreshLayout?
        ): RecyclerView.ViewHolder(binding.root){

        init{
            binding.tryAgainButton.setOnClickListener{ tryAgainAction() }
        }

        fun bind(loadState: LoadState) = with(binding){
            messageTextView.isVisible = loadState is LoadState.Error
            tryAgainButton.isVisible = loadState is LoadState.Error
            if(swipeRefreshLayout != null){
                swipeRefreshLayout.isRefreshing = loadState is LoadState.Loading
                progressBar.isVisible = false
            }else{
                progressBar.isVisible = loadState is LoadState.Loading
            }
        }
    }
}