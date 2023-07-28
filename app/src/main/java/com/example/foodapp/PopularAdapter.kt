package com.example.foodapp

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.example.foodapp.Fragments.CartFragment
import com.example.foodapp.databinding.PopularItemViewBinding

class PopularAdapter(private val items: List<String>, private val image : List<Int>, private val price: List<String>, private val context:Context) : RecyclerView.Adapter<PopularAdapter.PopularViewHolder>() {
    private val itemClickListener: MenuAdapter.OnClickListener?= null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopularViewHolder {
        return PopularViewHolder(PopularItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: PopularViewHolder, position: Int) {
        val item = items[position]
        val images = image[position]
        val price = price[position]
        holder.bind(item,images,price,context)
    }

    inner class PopularViewHolder(private val binding: PopularItemViewBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION){
                    itemClickListener?.onItemClick(position)
                }
                val intent = Intent(context, detail_activity::class.java)
                intent.putExtra("name",items.get(position))
                intent.putExtra("image",image.get(position))
                intent.putExtra("price",price.get(position))
                context.startActivity(intent)
            }
        }
        private val imagesView = binding.imageView2
        fun bind(item: String, images: Int, price:String, context: Context) {
            binding.foodNamePopular.text = item
            binding.pricePopular.text = price
            binding.PopularAddToCart.setOnClickListener {
                binding.PopularAddToCart.text = "Added"

                val intent = Intent(context, detail_activity::class.java)
                intent.putExtra("name",item)
                intent.putExtra("image",images)
                intent.putExtra("price",price)

            }
            imagesView.setImageResource(images)
        }
    }
    interface OnClickListener {
        fun onItemClick(position: Int)
    }
}