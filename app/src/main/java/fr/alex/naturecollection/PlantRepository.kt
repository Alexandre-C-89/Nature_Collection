package fr.alex.naturecollection

import android.net.Uri
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import fr.alex.naturecollection.PlantRepository.Singleton.databaseRef
import fr.alex.naturecollection.PlantRepository.Singleton.downloadUri
import fr.alex.naturecollection.PlantRepository.Singleton.plantList
import fr.alex.naturecollection.PlantRepository.Singleton.storageReference
import java.net.URI
import java.util.*

class PlantRepository {

    object Singleton {
        private val BUCKET_URL: String = "gs://naturecollection-3f4f4.appspot.com"

        //se connecter à notre espace de stockage
        val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(BUCKET_URL)

        // se connecter à la référence "plants"
        val databaseRef = FirebaseDatabase.getInstance().getReference("plants")

        // Créer une liste qui va contenir nos plantes
        val plantList = arrayListOf<PlantModel>()

        // contenir le lien de l'image courante
        var downloadUri: Uri? = null
    }

    fun updateData(callback: () -> Unit) {
        // absorber les données depuis la databaseRef -> liste de plantes
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // retirer les anciennes
                plantList.clear()

                // recolter la liste
                for (ds in snapshot.children) {
                    // construire un objet plante
                    val plant = ds.getValue(PlantModel::class.java)

                    // verifier que la plante n'est pas null
                    if(plant != null) {
                        // ajoutez la plante à notre liste
                        plantList.add(plant)
                    }
                }
                // actionner le callback
                callback()
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    // Créer un fonction pour envoyer des fichiers sur le storage
    fun uploadImage(file: Uri, callback: () -> Unit) {
        // Vérifier que ce fichier n'est pas null
        if (file != null) {
            val fileName = UUID.randomUUID().toString() + ".jpg"
            val ref = storageReference.child(fileName)
            val uploadTask = ref.putFile(file)

            // Démarrer la tâche d'envoi
            uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->

                // si il y a eu un poblème lors de l'envoi du fichier
                if(!task.isSuccessful) {
                    task.exception?.let { throw  it}
                }

                return@Continuation ref.downloadUrl
            }).addOnCompleteListener{ task ->
                // Vérifier si tout a bien fonctionné
                if (task.isSuccessful) {
                    // Récupérer l'image
                    downloadUri = task.result
                    callback()
                }
            }
        }
    }

    // mettre à jour un objet plante en BDD
    fun updatePlant(plant: PlantModel) {
        databaseRef.child(plant.id).setValue(plant)
    }

    // insérer une nouvelle plante en BDD
    fun insertPlant(plant: PlantModel) {
        databaseRef.child(plant.id).setValue(plant)
    }

    // supprimer une plante de la base
    fun deletePlant(plant: PlantModel) {
        databaseRef.child(plant.id).removeValue()
    }

}