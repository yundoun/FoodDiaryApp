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
    private val items: List<Diet>,
    private val onFavoriteClicked: (Diet) -> Unit,
    private val favorites: LiveData<Set<String>>
) : RecyclerView.Adapter<DietTabAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.text_view)
        val favoriteButton: ImageButton = view.findViewById(R.id.favorite_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_diet, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.textView.text = item.foodName
        updateFavoriteButton(holder.favoriteButton, item.foodCode)

        holder.favoriteButton.setOnClickListener {
            onFavoriteClicked(item)
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

    override fun getItemCount(): Int = items.size
}
