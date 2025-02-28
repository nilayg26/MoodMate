package com.example.moodmate.ViewModels
import android.content.Context
import android.content.SharedPreferences
import android.os.Parcel
import android.os.Parcelable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
class InternalDataBaseViewModel(private val sharedPreferences: SharedPreferences,context: Context):ViewModel() {
    var listDatabaseStatus by mutableStateOf("")
        private set
    var emergencyContactDatabaseStatus by mutableStateOf("")
        private set
    private val listDatabase = Room.databaseBuilder(
        context = context.applicationContext,
        klass = ToDoListAbstractDatabase::class.java,
        name = "todo_items"
    ).createFromAsset("databaseToDoList.db").build()
    private val emergencyContactDatabase = Room.databaseBuilder(
        context = context.applicationContext,
        klass = EmergencyContactAbstractDatabase::class.java,
        name = "emergency_contacts"
    ).createFromAsset("databaseEmergencyContacts.db").build()
    suspend fun getAllTodoItems(): List<TodoItem> {
        listDatabaseStatus = ToDoStatus.Loading
        val list = listDatabase.todoListDao().getAll()
        return list.apply {
            listDatabaseStatus = ToDoStatus.Success
        }
    }
    suspend fun addTodoItem(todoItem: TodoItem) {
        listDatabaseStatus = ToDoStatus.Loading
        listDatabase.todoListDao().insert(todoItem)
        listDatabaseStatus = ToDoStatus.Success
    }

    suspend fun deleteTodoItem(todoItem: TodoItem) {
        listDatabaseStatus = ToDoStatus.Loading
        listDatabase.todoListDao().deleteList(todoItem.text)
        listDatabaseStatus = ToDoStatus.Success
    }

    suspend fun deleteAllTodoItems() {
        listDatabaseStatus = ToDoStatus.Loading
        listDatabase.todoListDao().deleteAll()
        listDatabaseStatus = ToDoStatus.Success
    }

    suspend fun getAllContacts(): List<EmergencyContact> {
        emergencyContactDatabaseStatus = EmergencyContactStatus.Loading
        val list = emergencyContactDatabase.emergencyContactDao().getAllContacts()
        return list.apply {
            emergencyContactDatabaseStatus = EmergencyContactStatus.Success
        }
    }

    suspend fun addContact(contact: EmergencyContact) {
        emergencyContactDatabaseStatus = EmergencyContactStatus.Loading
        emergencyContactDatabase.emergencyContactDao().insert(contact = contact)
        emergencyContactDatabaseStatus = EmergencyContactStatus.Success
    }

    suspend fun deleteContact(contact: EmergencyContact) {
        emergencyContactDatabaseStatus = EmergencyContactStatus.Loading
        emergencyContactDatabase.emergencyContactDao().delete(contact.phoneNumber)
        emergencyContactDatabaseStatus = EmergencyContactStatus.Success
    }

    suspend fun deleteAllContacts() {
        emergencyContactDatabaseStatus = EmergencyContactStatus.Loading
        emergencyContactDatabase.emergencyContactDao().deleteAll()
        emergencyContactDatabaseStatus = EmergencyContactStatus.Success
    }

    fun getChartData(): Map<Int, List<String>>? {
        val i = sharedPreferences.getInt("count", -1)
        if (i != -1) {
            val map = mutableMapOf<Int, List<String>>()
            for (j in 0..i) {
                val e = sharedPreferences.getString(getKeyForEmoji(j), "") ?: "😊"
                val n = sharedPreferences.getInt(j.toString(), 3)
                map[j] = listOf(n.toString(), e)
            }
            return map
        }
        return null
    }
    fun setChartData(str: String) {
        val emoji = extractEmoji(str)
        val number = extractNumber(str)
        var i = sharedPreferences.getInt("count", -1)
        sharedPreferences.edit()
            .putInt("count", ++i)
            .putInt(i.toString(), number)
            .putString(getKeyForEmoji(i), emoji)
            .apply()

    }
    private fun extractEmoji(text: String): String {
        val emojiRegex = Regex("[\\p{So}\\p{Cs}]")
        return emojiRegex.find(text)?.value ?: "\uD83D\uDE0A"
    }
    private fun extractNumber(text: String): Int {
        val numberRegex = Regex("\\d+")
        val number = numberRegex.find(text)?.value ?: ""
        number.toIntOrNull()?.let {
            if (it <= 5) {
                return it
            }
        }
        return 3
    }
    private fun getKeyForEmoji(i: Int): String {
        val r = i / 26
        var str = ""
        for (t in 1..r) {
            str += "$"
        }
        return str + ((65 + (i % 26)).toChar())
    }
}
object ToDoStatus:State{
    override var Idle="idle"
    override var Error="Error"
    override var Loading="Loading"
     var Success="GOT IT"
}
object EmergencyContactStatus:State{
    override var Idle="idle"
    override var Error="Error"
    override var Loading="Loading"
    var Success="GOT IT"
}
@Entity(tableName = "emergency_contacts")
data class EmergencyContact(
    @PrimaryKey(autoGenerate = true) val id: Int=0,
    val name: String,
    val phoneNumber: String
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeString(phoneNumber)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<EmergencyContact> {
        override fun createFromParcel(parcel: Parcel): EmergencyContact {
            return EmergencyContact(parcel)
        }

        override fun newArray(size: Int): Array<EmergencyContact?> {
            return arrayOfNulls(size)
        }
    }
}
@Dao
interface EmergencyContactDao{
    @Insert
    suspend fun insert(contact: EmergencyContact)
    @Query("SELECT * FROM emergency_contacts")
    suspend fun getAllContacts():List<EmergencyContact>
    @Query("DELETE FROM emergency_contacts WHERE phoneNumber=:phoneNumber")
    suspend fun delete(phoneNumber: String)
    @Query("DELETE FROM emergency_contacts")
    suspend fun deleteAll()
}
@Database(entities = [EmergencyContact::class], version = 1)
abstract class EmergencyContactAbstractDatabase():RoomDatabase(){
    abstract fun emergencyContactDao():EmergencyContactDao
}

@Entity(tableName = "todo_items")
data class TodoItem(
    @PrimaryKey(autoGenerate = true) val id:Int=0,
    val text: String
)
@Dao
interface ToDoListDAO{
    @Insert
    suspend fun insert(todoItem: TodoItem)
    @Query("SELECT * FROM todo_items")
    suspend fun getAll():List<TodoItem>
    @Query("DELETE FROM todo_items WHERE text=:text")
    suspend fun deleteList(text:String)
    @Query("DELETE FROM todo_items")
    suspend fun deleteAll()
}
@Database(entities = [TodoItem::class], version = 2)
abstract class ToDoListAbstractDatabase:RoomDatabase(){
    abstract fun todoListDao():ToDoListDAO
}