package com.peter.asteroids.main

import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.peter.asteroids.Asteroid
import com.peter.asteroids.AsteroidsTimeToCome
import com.peter.asteroids.R
import com.peter.asteroids.databinding.FragmentMainBinding


@RequiresApi(Build.VERSION_CODES.N)
class MainFragment : Fragment() {

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    }

    private val asteroidAdapter = AsteroidAdapter(AsteroidAdapter.AsteroidListener { asteroid ->
        viewModel.toDetailAsteroids.value = asteroid
    })

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentMainBinding.inflate(inflater)
        binding.lifecycleOwner = this

        binding.viewModel = viewModel
        binding.asteroidRecycler.adapter = asteroidAdapter

        viewModel.toDetailAsteroids.observe(viewLifecycleOwner, Observer { asteroid ->
            if (asteroid != null) {
                this.findNavController().navigate(MainFragmentDirections.actionMainFragmentToDetailFragment(asteroid))
                viewModel.toDetailAsteroids.value = null
            }
        })
        viewModel.pictureOfDay.observe(viewLifecycleOwner, Observer { newPic ->
            if (newPic != null){
                binding.picLoading.visibility = View.GONE
            }
            else{
                binding.picLoading.visibility = View.VISIBLE
            }
        })

        setHasOptionsMenu(true)

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.asteroidList.observe(viewLifecycleOwner, Observer<List<Asteroid>> { asteroid ->
            asteroid.apply {
                asteroidAdapter.submitList(this)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        viewModel.onChangeInMenu(
            when (item.itemId) {
                R.id.show_today -> {
                    AsteroidsTimeToCome.TODAYsAsteroids
                }
                R.id.show_week -> {
                    AsteroidsTimeToCome.WEEKsAsteroids
                }
                else -> {
                    AsteroidsTimeToCome.ALLAsteroids
                }
            }
        )
        return true
    }
}
