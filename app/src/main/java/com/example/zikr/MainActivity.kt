    package com.example.zikr

    import android.os.Bundle
    import android.preference.PreferenceManager
    import android.util.Log
    import android.view.LayoutInflater
    import android.view.MotionEvent
    import android.widget.EditText
    import android.widget.Toast
    import androidx.appcompat.app.AlertDialog
    import androidx.appcompat.app.AppCompatActivity
    import androidx.core.content.ContextCompat
    import androidx.recyclerview.widget.ItemTouchHelper
    import androidx.recyclerview.widget.LinearLayoutManager
    import androidx.recyclerview.widget.RecyclerView
    import androidx.room.Dao
    import androidx.room.Delete
    import androidx.room.Insert
    import androidx.room.Query
    import com.example.zikr.DB.AppDatabase
    import com.example.zikr.DB.DoneItem
    import com.example.zikr.DB.Person
    import com.example.zikr.databinding.ActivityMainBinding
    import com.google.android.material.snackbar.BaseTransientBottomBar
    import com.google.android.material.snackbar.Snackbar
    import kotlinx.coroutines.CoroutineScope
    import kotlinx.coroutines.Dispatchers
    import kotlinx.coroutines.delay
    import kotlinx.coroutines.launch
    import kotlinx.coroutines.withContext

    class MainActivity : AppCompatActivity() {

        private lateinit var binding: ActivityMainBinding

        private lateinit var itemRv: RecyclerView
        private lateinit var itemList: ArrayList<String>
        private lateinit var itemAdapter: ItemRecyclarAapter
        private lateinit var doneRv: RecyclerView
        private lateinit var doneList: ArrayList<String>
        private lateinit var doneAdapter: ItemRecyclarAapter
        val itemLayoutManager = LinearLayoutManager(this)
        val doneLayoutManager = LinearLayoutManager(this)
        private lateinit var personDao: PersonDao
        private var undoClicked = false
        private lateinit var doneItemDao: DoneItemDao

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            val isDarkTheme = readThemePreference()

            // Set the theme based on the user preference
            if (isDarkTheme) {
                setTheme(R.style.Theme_Zikr_night)
            } else {
                setTheme(R.style.Theme_Zikr)
            }
            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)
            // Initialize Room Database
            val appDatabase = AppDatabase.getDatabase(this)
            personDao = appDatabase.personDao()
            doneItemDao = appDatabase.doneItemDao()


            itemRv = binding.itemRv
            doneRv = binding.archiveRv

            itemList = ArrayList()
            doneList = ArrayList()
            CoroutineScope(Dispatchers.Main).launch {
                loadPersons()
                loadDones()
            }
            binding.fab.setOnClickListener {
                showAddTextDialog()
            }
            try {

                binding.restart.setOnClickListener {
                        moveAllItemsFromDone()


                }
            }catch (e:Exception){
                Toast.makeText(this@MainActivity, e.message.toString(), Toast.LENGTH_SHORT).show()
            }

         items()
        }

        private fun items(){
            itemAdapter = ItemRecyclarAapter(itemList,this)
            itemRv.layoutManager = itemLayoutManager
            itemRv.adapter = itemAdapter


            doneAdapter = ItemRecyclarAapter(doneList,this)
            doneRv.layoutManager = doneLayoutManager
            doneRv.adapter = doneAdapter
            swipeToGesture(itemRv)

        }
        private fun moveAllItemsFromDone() {
            // Get all items from the "Done" list
            try {
                val doneItems: List<String> = doneList.toList()

                // Clear the "Done" list and update the adapter
                doneList.clear()
                doneAdapter.notifyDataSetChanged()


                // Move items to the "Items" list
                itemList.addAll(doneItems)
                itemList.reverse()
                itemAdapter.notifyDataSetChanged()

                // Move items from the "Done" database to the "Items" database
                moveAllItemsFromDoneDatabase()

                // Notify the user or perform any other necessary actions
                Toast.makeText(this@MainActivity ,getString(R.string.moved_all_items_from_done_to_items), /* duration = */
                    Toast.LENGTH_SHORT).show()


            }catch (e:Exception){
                Toast.makeText(this@MainActivity, e.message.toString(), Toast.LENGTH_SHORT).show()
            }

        }

        private fun moveAllItemsFromDoneDatabase() {
            try {
                    // Delete all items from the "Done" database
                CoroutineScope(Dispatchers.IO).launch {
                    // Retrieve all items from the "Done" database
                    var doneItems = doneItemDao.getAllDoneItems()

                    doneItemDao.deleteAll()
                    try {
                        doneItems.forEach { doneItem ->
                            personDao.insert(Person(name = doneItem.name.toString()))

                        }

                    }catch (e:Exception){
                        Toast.makeText(this@MainActivity, "wron with db.", Toast.LENGTH_SHORT).show()
                    }

                }

                // Insert items into the "Items" database


            }catch (e: Exception){
                Toast.makeText(this, e.message.toString(), Toast.LENGTH_SHORT).show()
            }
            // Use a CoroutineScope to run database operations asynchronously
        }


        // Inside your MainActivity or any other activity
        private fun showAddTextDialog() {
            val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_text, null)

            val editText = dialogView.findViewById<EditText>(R.id.editText)

            val alertDialogBuilder =
                AlertDialog.Builder(this)
                .setView(dialogView)
                .setTitle("إضافة عضو")
                .setPositiveButton("إضافة") { dialog, which ->
                    // Handle the positive button click
                    val newText = editText.text.toString()
                    // Process the new text as needed (e.g., add it to your list)
                    if (newText.isNotEmpty()) {
                        // Add your logic here, for example, add to your list
                        insertPerson(Person(name = newText))
                        CoroutineScope(Dispatchers.Main).launch {
                            loadPersons()
                            loadDones()
                        }

                    }
                }
                .setNegativeButton("إلغاء") { dialog, which ->
                    // Handle the negative button click (optional)
                    // Do nothing
                    dialog.dismiss()


                }
                    .setIcon(R.drawable.baseline_account_box_24)

            // Create and show the AlertDialog

            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()


        }


        private fun loadPersons() {
            // Use a CoroutineScope to run database operations asynchronously
            CoroutineScope(Dispatchers.IO).launch {
                // Retrieve all persons from the database
                val persons = personDao.getAllPersons()
                // Update the UI on the main thread
                withContext(Dispatchers.Main) {
                    // Clear the existing list
                    itemList.clear()
                    // Add persons to the list
                    itemList.addAll(persons.map { it.name })
                    itemList.reverse()
                    // Notify the adapter about the data change
                    itemAdapter.notifyDataSetChanged()
                }
            }
        }
        private fun loadDones() {
            // Use a CoroutineScope to run database operations asynchronously
            CoroutineScope(Dispatchers.IO).launch {
                // Retrieve all persons from the database
                val dones = doneItemDao.getAllDoneItems()
                // Update the UI on the main thread
                withContext(Dispatchers.Main) {
                    // Clear the existing list
                    doneList.clear()
                    // Add persons to the list
                    doneList.addAll(dones.map { it.name })
                    doneList.reverse()
                    // Notify the adapter about the data change

                    doneAdapter.notifyDataSetChanged()
                }
            }
        }

        private fun insertPerson(person: Person) {
            // Use a CoroutineScope to run database operations asynchronously
            CoroutineScope(Dispatchers.IO).launch {
                personDao.insert(person)
            }
        }
        private fun insertDone(done: DoneItem) {
            // Use a CoroutineScope to run database operations asynchronously
            CoroutineScope(Dispatchers.IO).launch {
                doneItemDao.insert(done)
            }
        }

        private fun swipeToGesture(itemRv : RecyclerView?){
            val swipeGeature = object : SwipeGesture(context = this){
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.adapterPosition
                    var actionBtnTapped = false
                    try {
                        when(direction){

                            ItemTouchHelper.LEFT -> {
                                val deleteItem = itemList[position]
                                deletePersonByNameWithSnack(deleteItem)
                                itemList.removeAt(position)
                                itemAdapter.notifyItemRemoved(position)

                            }
                            ItemTouchHelper.RIGHT -> {
                                val doneItem : String= itemList[position]
                                itemList.removeAt(position)
                                itemAdapter.notifyItemRemoved(position)
                                doneList.add(doneItem)
                                doneAdapter.notifyItemInserted(
                                    doneList.size-1)
                                //add the item name to the done list
                                insertDone(DoneItem(name = doneItem.toString()))
                                deletePersonFromDatabase(doneItem.toString())
                                CoroutineScope(Dispatchers.Main).launch {
                                    loadPersons()
                                    loadDones()
                                }

                                val snackbar = Snackbar.make(binding.root,"done $doneItem",
                                    Snackbar.LENGTH_LONG).addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {

                                        super.onDismissed(transientBottomBar, event)
                                    }

                                    override fun onShown(transientBottomBar: Snackbar?) {
                                        super.onShown(transientBottomBar)
                                        transientBottomBar?.setAction("Undo"){
                                            itemList.add(position,doneItem)
                                            itemAdapter.notifyItemInserted(position)
                                            doneList.removeAt(doneList.size-1)
                                            doneAdapter.notifyItemRemoved(doneList.size)
                                            actionBtnTapped = true
                                            /////////////////
                                            deleteDoneFromDatabase(doneItem.toString())
                                            insertPerson(Person(name = doneItem.toString()))
                                            CoroutineScope(Dispatchers.Main).launch {
                                                loadPersons()
                                                loadDones()
                                            }
                                        }
                                    }

                                }).apply {
                                    animationMode = Snackbar.ANIMATION_MODE_SLIDE
                                }
                                snackbar.setActionTextColor(ContextCompat.getColor(this@MainActivity,
                                    R.color.orangeRed))
                                snackbar.show()

                            }


                        }
                    }
                    catch (e: Exception){
                        val snackbar = Snackbar.make(binding.root, "your Error is :${e.message.toString()}", Snackbar.LENGTH_LONG)

                            .setActionTextColor(ContextCompat.getColor(this@MainActivity, R.color.orangeRed))
                            .setAnimationMode(Snackbar.ANIMATION_MODE_FADE)
                        snackbar.show()
                        Toast.makeText(this@MainActivity,e.message, Toast.LENGTH_SHORT).show()
                        Log.e("ahmednagah",e.message.toString())
                    }





                }

            }
            val touchHelper = ItemTouchHelper(swipeGeature)
            touchHelper.attachToRecyclerView(itemRv)


        }

        private fun deletePersonByNameWithSnack(personName: String) {
            // Find the person by name
            val personToDelete = itemList.find { it == personName }

            personToDelete?.let {
                // Display Snackbar with an undo action
                val snackbar = Snackbar.make(binding.root, "Deleted $personName", Snackbar.LENGTH_LONG)
                    .setAction("Undo") {
                        // If Undo is clicked, add the person back to the list
                        itemList.add(personToDelete)

                        undoClicked = true

                        itemAdapter.notifyDataSetChanged()
                    }
                    .setActionTextColor(ContextCompat.getColor(this, R.color.orangeRed))
                    .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)


                // Show the Snackbar
                snackbar.show()

                // Use a CoroutineScope to delay the actual deletion from the database
                CoroutineScope(Dispatchers.Main).launch {
                    delay(2000) // Wait for 5 seconds (adjust as needed)
                    // Remove the person from the list after the delay

                    if (!undoClicked){
                        itemList.remove(personToDelete)
                        itemAdapter.notifyDataSetChanged()

                        // Use another CoroutineScope to perform the deletion from the database
                        CoroutineScope(Dispatchers.IO).launch {
                            deletePersonFromDatabase(it)

                            // Delete the person from the database
                        }
                    }else{
                        undoClicked = false
                    }


                }
            }
        }

        private fun deletePersonFromDatabase(person: String) {
            // Use a CoroutineScope to run database operations asynchronously
            CoroutineScope(Dispatchers.IO).launch {
                // Get the person from the database (assuming personDao is available)
                val personEntity = personDao.getPersonByName(person)
                // Delete the person
                personEntity?.let {
                    personDao.delete(it)
                }
            }
        }


        private fun deleteDoneFromDatabase(done: String) {
            // Use a CoroutineScope to run database operations asynchronously
            CoroutineScope(Dispatchers.IO).launch {
                // Get the person from the database (assuming personDao is available)
                val doneEntity = doneItemDao.getDoneItemByName(done)
                // Delete the person
                doneEntity?.let {
                    doneItemDao.delete(it)
                }

            }
        }
        
        // Function to read theme preference from SharedPreferences
        private fun readThemePreference(): Boolean {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
            return sharedPreferences.getBoolean("dark_theme_preference", false)
        }
    }

    // PersonDao.kt
    @Dao
    interface PersonDao {
        @Insert
         fun insert(person: Person)
        @Query("SELECT * FROM persons")
         fun getAllPersons(): List<Person>
        @Delete
        fun delete(person: Person)
        @Query("SELECT * FROM persons WHERE name = :name")
        fun getPersonByName(name: String): Person?

    }

    // DoneItemDao.kt
    @Dao
    interface DoneItemDao {
        @Insert
         fun insert(doneItem: DoneItem)

        @Query("SELECT * FROM done_items")
         fun getAllDoneItems(): List<DoneItem>

        @Delete
         fun delete(doneItem: DoneItem)

        @Query("SELECT * FROM done_items WHERE name = :name")
         fun getDoneItemByName(name: String): DoneItem?

        @Query("DELETE FROM done_items")
        fun deleteAll()

    }


