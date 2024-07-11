import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnutrijournal.R
import com.example.fitnutrijournal.data.model.Diet

// RecyclerView.Adapter
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

    inner class DietViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.foodName)
        val favoriteButton: ImageButton = view.findViewById(R.id.favorite_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DietViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_diet, parent, false)
        return DietViewHolder(view)
    }

    override fun onBindViewHolder(holder: DietViewHolder, position: Int) {
        val item = diets[position]
        Log.d("DietTabAdapter", "Binding item at position $position: $item")
        holder.textView.text = item.foodName
        updateFavoriteButton(holder.favoriteButton, item.foodCode)

        holder.favoriteButton.setOnClickListener {
            toggleFavorite(item)
        }

        favorites.observeForever {
            updateFavoriteButton(holder.favoriteButton, item.foodCode)
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
