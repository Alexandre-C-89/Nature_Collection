package fr.alex.naturecollection.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import fr.alex.naturecollection.MainActivity
import fr.alex.naturecollection.PlantModel
import fr.alex.naturecollection.PlantRepository.Singleton.plantList
import fr.alex.naturecollection.R
import fr.alex.naturecollection.adapter.PlanAdapter
import fr.alex.naturecollection.adapter.PlantItemDecoration


class HomeFragment(
    private val context: MainActivity,
) : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater?.inflate(R.layout.fragment_home, container,  false)


        // récupérer le recyclerview
        val horizontalRecyclerView = view.findViewById<RecyclerView>(R.id.horizontal_recycler_view)
        horizontalRecyclerView.adapter = PlanAdapter(context, plantList.filter{ !it.liked }, R.layout.item_horizontal_plant)

        // Récupérer le second recyclerview
        val verticalRecyclerView = view.findViewById<RecyclerView>(R.id.vertical_recycler_view)
        verticalRecyclerView.adapter = PlanAdapter(context, plantList, R.layout.item_vertical_plant)
        verticalRecyclerView.addItemDecoration(PlantItemDecoration())

        return view
    }
}