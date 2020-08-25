package com.example.messengerapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.messengerapp.Model.ChatList
import com.example.messengerapp.Model.Users
import com.example.messengerapp.R
import com.example.messengerapp.adapter.UserAdapter
import com.example.messengerapp.notifications.Token
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.ktx.Firebase

/**
 * A simple [Fragment] subclass.
 * Use the [ChatFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ChatFragment : Fragment() {
    private var userAdapter: UserAdapter? = null
    private var mUsers: List<Users>? = null
    private var usersChatList: List<ChatList>? = null
    lateinit var recycler_view_chatlist: RecyclerView
    private var firebaseUser: FirebaseUser? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_chat, container, false)

        recycler_view_chatlist = view.findViewById(R.id.recycler_view_chatlist)
        recycler_view_chatlist.setHasFixedSize(true)
        recycler_view_chatlist.layoutManager = LinearLayoutManager(context)

        firebaseUser = FirebaseAuth.getInstance().currentUser

        usersChatList = ArrayList()

        val ref = FirebaseDatabase.getInstance().reference.child("ChatList").child(firebaseUser!!.uid)
        ref!!.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                (usersChatList as ArrayList).clear()

                for (snapshot in p0.children) {
                    val chatList = snapshot.getValue(ChatList::class.java)
                    (usersChatList as ArrayList).add(chatList!!)
                }

                retrieveChatList()
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

        updateToken(FirebaseInstanceId.getInstance().token)

        return view
    }

    private fun updateToken(token: String?) {
        val ref = FirebaseDatabase.getInstance().reference.child("Tokens")
        val token1 = Token(token!!)
        ref.child(firebaseUser!!.uid).setValue(token1)
    }

    private fun retrieveChatList() {
        mUsers = ArrayList()

        val ref = FirebaseDatabase.getInstance().reference.child("Users")
        ref!!.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                (mUsers as ArrayList).clear()

                for (snapshot in p0.children) {
                    val user = snapshot.getValue(Users::class.java)

                    for (eachChatList in usersChatList!!) {
                        if (user!!.getUid().equals(eachChatList.getId())) {
                            (mUsers as ArrayList).add(user!!)
                        }
                    }
                }

                userAdapter = UserAdapter(context!!, (mUsers as ArrayList<Users>), true)
                recycler_view_chatlist.adapter = userAdapter
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
}