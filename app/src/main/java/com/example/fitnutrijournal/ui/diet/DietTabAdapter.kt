import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnutrijournal.data.model.Diet
import com.example.fitnutrijournal.databinding.ItemDietBinding

class DietTabAdapter(
    private var diets: List<Diet>,
    private val toggleFavorite: (Diet) -> Unit,
    private val favorites: LiveData<Set<String>>
) : RecyclerView.Adapter<DietTabAdapter.DietViewHolder>() {

    @SuppressLint("NotifyDataSetChanged")
    fun updateDiets(newDiets: List<Diet>) {
        diets = newDiets
        notifyDataSetChanged() // This will notify the RecyclerView to refresh its items
    }

    inner class DietViewHolder(private val binding: ItemDietBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Diet) {
            binding.foodName.text = item.foodName
            binding.foodTotalContent.text = "${item.totalContent} g"
            binding.foodCalories.text = "${item.calories} kcal"
            updateFavoriteButton(binding.favoriteButton, item.foodCode)

            binding.favoriteButton.setOnClickListener {
                toggleFavorite(item)
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
            if (isFavorite) android.R.drawable.btn_star_big_on
            else android.R.drawable.btn_star_big_off
        )
    }

    override fun getItemCount(): Int = diets.size
}
