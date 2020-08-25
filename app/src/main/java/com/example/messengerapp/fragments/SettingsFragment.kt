package com.example.messengerapp.fragments

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import com.example.messengerapp.Model.Users
import com.example.messengerapp.R
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_settings.view.*

/**
 * A simple [Fragment] subclass.
 * Use the [SettingsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SettingsFragment : Fragment() {

    var usersReference: DatabaseReference? = null
    var firebaseUser: FirebaseUser? = null
    private val RequestCode = 438
    private var imageUri: Uri? = null
    private var storageRef: StorageReference? = null
    private var coverChecker: String? = ""
    private var socialChekcer: String? = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        firebaseUser = FirebaseAuth.getInstance().currentUser
        usersReference = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)
        storageRef = FirebaseStorage.getInstance().reference.child("User Images")

        usersReference!!.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    val user: Users? = p0.getValue(Users::class.java)

                    if (context != null) {
                        view.username_settings.text = user?.getUsername()
                        Picasso.get().load(user?.getProfile())
                            .placeholder(R.drawable.profile)
                            .into(view.profile_image_settings)
                        Picasso.get().load(user?.getCover())
                            .placeholder(R.drawable.cover)
                            .into(view.cover_image_settings)
                    }

                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

        view.profile_image_settings.setOnClickListener {
            coverChecker = ""
            pickImage()
        }

        view.cover_image_settings.setOnClickListener {
            coverChecker = "cover"
            pickImage()
        }

        view.set_facebook.setOnClickListener {
            socialChekcer = "facebook"
            setSocialLinks()
        }

        view.set_instagram.setOnClickListener {
            socialChekcer = "instagram"
            setSocialLinks()
        }

        view.set_website.setOnClickListener {
            socialChekcer = "website"
            setSocialLinks()
        }

        return view
    }

    private fun setSocialLinks() {
        val builder: AlertDialog.Builder =
            AlertDialog.Builder(context!!, R.style.Theme_AppCompat_DayNight_Dialog_Alert)

        if (socialChekcer == "website") {
            builder.setTitle("Unesite link:")
        } else {
            builder.setTitle("Unesite korisničko ime:")
        }

        val editText = EditText(context)

        if (socialChekcer == "website") {
            editText.hint = "npr. www.google.hr"
        } else {
            editText.hint = "npr. sime123"
        }

        builder.setView(editText)

        builder.setPositiveButton("Spremi", DialogInterface.OnClickListener {
                dialog, which ->
            val str = editText.text.toString()

            if (str == "") {
                Toast.makeText(context, "Niste unijeli ništa ...", Toast.LENGTH_LONG).show()
            } else {
                saveSocialLink(str)
            }
        })

        builder.setNegativeButton("Otkaži", DialogInterface.OnClickListener {
                dialog, which ->
            dialog.cancel()
        })

        builder.show()
    }

    private fun saveSocialLink(str: String) {
        val mapSocial = HashMap<String, Any>()

        when(socialChekcer) {
            "facebook" -> {
                // Update facebook
                mapSocial["facebook"] = "https://m.facebook.com/$str"
            }
            "instagram" -> {
                // Update instagram
                mapSocial["instagram"] = "https://m.instagram.com/$str"
            }
            "website" -> {
                // Update website
                mapSocial["website"] = "https://$str"
            }
        }

        usersReference!!.updateChildren(mapSocial).addOnCompleteListener {
            task ->
            if (task.isSuccessful) {
                Toast.makeText(context, "Promjene su uspješno spremljene!", Toast.LENGTH_LONG).show()
            }
        }
        socialChekcer = ""
    }

    private fun pickImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, RequestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RequestCode && resultCode == Activity.RESULT_OK
            && data!!.data != null) {
            imageUri = data.data
            Toast.makeText(context, "Učitavanje slike ...", Toast.LENGTH_LONG).show()
            uploadImageToDb()
        }
    }

    private fun uploadImageToDb() {
        val progressBar = ProgressDialog(context)
        progressBar.setMessage("Slika se učitava, molimo pričekajte ...")
        progressBar.show()

        if (imageUri != null) {
            val fileRef = storageRef!!.child(System.currentTimeMillis().toString() + ".jpg")

            var uploadTask: StorageTask<*>
            uploadTask = fileRef.putFile(imageUri!!)

            uploadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot,
                Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation fileRef.downloadUrl
            }).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUrl = task.result
                    val url = downloadUrl.toString()

                    if (coverChecker == "cover") {
                        // Set up as cover image
                        val mapCoverImg = HashMap<String, Any>()
                        mapCoverImg["cover"] = url
                        usersReference!!.updateChildren(mapCoverImg)
                        coverChecker = ""

                    } else {
                        // Set up as profile image
                        val mapProfileImg = HashMap<String, Any>()
                        mapProfileImg["profile"] = url
                        usersReference!!.updateChildren(mapProfileImg)
                        coverChecker = ""
                    }

                    progressBar.dismiss()
                }
            }
        }
    }

}