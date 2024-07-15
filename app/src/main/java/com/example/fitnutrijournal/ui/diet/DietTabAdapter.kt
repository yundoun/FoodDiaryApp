import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnutrijournal.R
import com.example.fitnutrijournal.data.model.Diet
import com.example.fitnutrijournal.databinding.ItemDietBinding

class DietTabAdapter(
    private var diets: List<Diet>,
    private val toggleFavorite: (Diet) -> Unit,
    private val favorites: LiveData<Set<String>>,
    private val onItemClick: (Diet) -> Unit
) : RecyclerView.Adapter<DietTabAdapter.DietViewHolder>() {

    @SuppressLint("NotifyDataSetChanged")
    fun updateDiets(newDiets: List<Diet>) {
        diets = newDiets
        notifyDataSetChanged() // This will notify the RecyclerView to refresh its items
    }

    inner class DietViewHolder(private val binding: ItemDietBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: Diet) {
            binding.foodName.text = item.foodName
            binding.foodTotalContent.text = "${item.totalContent} g"
            binding.foodCalories.text = "${item.calories} kcal"
            updateFavoriteButton(binding.favoriteButton, item.foodCode)

            binding.favoriteButton.setOnClickListener {
                toggleFavorite(item)
            }

            binding.root.setOnClickListener { // Add this line
                onItemClick(item)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DietViewHolder {
        val binding = ItemDietBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DietViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DietViewHolder, position: Int) {
        val item = diets[position]
        holder.bind(item)

        // 즐겨찾기 상태 관찰
        favorites.observeForever {
            holder.bind(item)
        }


    }

    private fun updateFavoriteButton(button: ImageButton, foodCode: String) {
        val isFavorite = favorites.value?.contains(foodCode) ?: false
        button.setImageResource(
            if (isFavorite) R.drawable.ic_star
            else R.drawable.ic_star_border
        )
    }



    override fun getItemCount(): Int = diets.size
}
