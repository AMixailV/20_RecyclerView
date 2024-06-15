package ru.mixail_akulov.a20_recyclerview

import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.mixail_akulov.a20_recyclerview.databinding.ItemUsersBinding
import ru.mixail_akulov.a20_recyclerview.model.User

// Если бы действие в списке было бы одно, то можно было определить один typealias, а т.к. у нас
// три действия,то использовать интерфейс
interface UserActionListener {

    fun onUserMove(user: User, moveBy: Int)

    fun onUserDelete(user: User)

    fun onUserDetails(user: User)

}

class UsersAdapter(
    private val actionListener: UserActionListener
) : RecyclerView.Adapter<UsersAdapter.UsersViewHolder>(), View.OnClickListener {

    var users: List<User> = emptyList()
        set(newValue) {
            field = newValue  // при размещении нового элемента из списка
            notifyDataSetChanged()  // список перерисовывается
        }

    override fun onClick(v: View) {    // здесь View никогда не равно null, это вьюшка, на которую нажал пользователь
        val user = v.tag as User // в тег при нажатии был положен сам User в методе onBindViewHolder,
                                       // пробуем здесь его вытащить и будем хранить в теге нашего вью
        when (v.id) {                  // поэтому можно из него получить на что именно нажали
            R.id.moreImageViewButton -> {
                showPopupMenu(v)       // в нем размещаем наши три действия, при нажатии на кнопку
            }
            else -> {                              // или при нажатии на элемент списка
                actionListener.onUserDetails(user) // реализован onUserDetails() в MainActivity,
                                                   // передаем ему пользователя, на которого нажали
            }
        }
    }

    override fun getItemCount(): Int = users.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemUsersBinding.inflate(inflater, parent, false)

        binding.root.setOnClickListener(this) // инициализация слушателей при нажатии на элемент списка
        binding.moreImageViewButton.setOnClickListener(this) // и нажатия на кнопку

        return UsersViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {
        val user = users[position]

        with(holder.binding) {
            holder.itemView.tag = user     // помещаем в тэги юзера при каких-то нажатиях,
            moreImageViewButton.tag = user // чтобы можно было его вытащить в обработке нажатий onClick()

            userNameTextView.text = user.name
            userCompanyTextView.text = user.company
            if (user.photo.isNotBlank()) {
                Glide.with(photoImageView.context)
                    .load(user.photo)
                    .circleCrop()  // делает аватарку круглой
                    .placeholder(R.drawable.ic_user_avatar)  // задает Android id для ресурса из drawable
                    .error(R.drawable.ic_user_avatar)  // задает что грузить в случае ошибки
                    .into(photoImageView)
            } else {
                Glide.with(photoImageView.context).clear(photoImageView)
                photoImageView.setImageResource(R.drawable.ic_user_avatar)
                // вы также можете использовать следующий код вместо этих двух строк ^
                // Glide.with(photoImageView.context)
                //        .load(R.drawable.ic_user_avatar)
                //        .into(photoImageView)
            }
        }
    }

    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(view.context, view)
        val context = view.context
        val user = view.tag as User                           // вытаскиваем пользователя из тэга
        val position = users.indexOfFirst { it.id == user.id } // получаем индекс элемента

        // Определяем popupMenu. Добавляем действия
        popupMenu.menu.add(0, ID_MOVE_UP, Menu.NONE, context.getString(R.string.move_up)).apply {
            isEnabled = position > 0                      // действие доступно,только если индекс > 0
        }
        popupMenu.menu.add(0, ID_MOVE_DOWN, Menu.NONE, context.getString(R.string.move_down)).apply {
            isEnabled = position < users.size - 1         // действие доступно,только если индекс < 0
        }
        popupMenu.menu.add(0, ID_REMOVE, Menu.NONE, context.getString(R.string.remove))

        // обработка событий в popupMenu. Сюда приходит пункт меню, на которыйпользователь нажал.
        // Отправляем пользователя в соответствующий метод в MainActivity
        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) { // вытаскиваем id пункта
                ID_MOVE_UP -> {
                    actionListener.onUserMove(user, -1)
                }
                ID_MOVE_DOWN -> {
                    actionListener.onUserMove(user, 1)
                }
                ID_REMOVE -> {
                    actionListener.onUserDelete(user)
                }
            }
            return@setOnMenuItemClickListener true
        }

        popupMenu.show()
    }

    class UsersViewHolder(
        val binding: ItemUsersBinding
    ) : RecyclerView.ViewHolder(binding.root)

    companion object { // идентификаторы действий
        private const val ID_MOVE_UP = 1
        private const val ID_MOVE_DOWN = 2
        private const val ID_REMOVE = 3
    }
}