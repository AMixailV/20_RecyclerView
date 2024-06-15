package ru.mixail_akulov.a20_recyclerview

import android.app.Application
import ru.mixail_akulov.a20_recyclerview.model.UsersService

// Делаем UsersService синглтоном. Прописываем его в манифесте классом нашего приложения и получаем
// доступ к нему из любого места
class App : Application() {

    val usersService = UsersService()
}