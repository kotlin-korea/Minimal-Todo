package data

import java.util.*


/**
 * Created by jeonghyeonji on 2017. 7. 22..
 */

data class ToDoItem(
        val mToDoText: String,
        val mHasReminder: Boolean,
        val mToDoColor: Int,
        val mToDoDate: Date,
        val mTodoIdentifier: UUID,
        val TODOTEXT: String = "todotext",
        val TODOREMINDER: String = "todoreminder",
        val TODOCOLOR: String = "todocolor",
        val TODODATE: String = "tododate",
        val TODOIDENTIFIER: String = "todoidentifier"
)
