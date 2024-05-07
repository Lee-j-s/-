import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.csi.R

data class Item(val no: Int, val name: String, val price: Int, val photo: String)

class ItemAdapter(private val itemList: List<Item>) :
    RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val noTextView: TextView = itemView.findViewById(R.id.noTextView)
        val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        val priceTextView: TextView = itemView.findViewById(R.id.priceTextView)
        val photoImageView: ImageView = itemView.findViewById(R.id.photoImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_layout, parent, false)
        return ItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val currentItem = itemList[position]
        holder.noTextView.text = currentItem.no.toString()
        holder.nameTextView.text = currentItem.name
        holder.priceTextView.text = currentItem.price.toString()

        // Glide를 사용하여 이미지를 로드하고 표시합니다.
        Glide.with(holder.itemView.context)
            .load(currentItem.photo)
            .into(holder.photoImageView)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}
