package ru.mixail_akulov.a20_recyclerview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import ru.mixail_akulov.a20_recyclerview.databinding.ActivityMainBinding
import ru.mixail_akulov.a20_recyclerview.model.User
import ru.mixail_akulov.a20_recyclerview.model.UsersListener
import ru.mixail_akulov.a20_recyclerview.model.UsersService

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: UsersAdapter

    private val usersService: UsersService
        get() = (applicationContext as App).usersService // гетер, чтобы получать доступ к модели UsersService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = UsersAdapter(object : UserActionListener {
            override fun onUserMove(user: User, moveBy: Int) {
                usersService.moveUser(user, moveBy) // перенаправляем на реализацию в UserService
            }

            override fun onUserDelete(user: User) {
                usersService.deleteUser(user) // перенаправляем на реализацию в UserService
            }

            override fun onUserDetails(user: User) {
                Toast.makeText(this@MainActivity, "User: ${user.name}", Toast.LENGTH_SHORT).show()
            }
        })

        val layoutManager = LinearLayoutManager(this) // назначаем вертикальный LinearLayoutManager
        binding.recyclerView.layoutManager = layoutManager   // и назначаем его recyclerView
        binding.recyclerView.adapter = adapter               // и назначаем адаптер, который мы создали

        usersService.addListener(usersListener) // добавляем слушателя в список слушателей UsersService с помощью метода из UsersService
    }

    // удаляем слушатель из списка для исключения утечек память
    override fun onDestroy() {
        super.onDestroy()
        usersService.removeListener(usersListener)
    }

    // слушатель, который будет слушать изменения в классе UsersService, т.е.
    // мы адаптеру присваиваем новый список, который нам приходит в слушателя
    private val usersListener: UsersListener = {
        adapter.users = it
    }
}