package com.changanford.evos

import android.view.Menu
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.changanford.common.basic.BaseActivity
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey.BUS_HIDE_BOTTOM_TAB
import com.changanford.common.util.room.Db
import com.changanford.common.utilext.setupWithNavController
import com.changanford.evos.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {

    private lateinit var appBarConfiguration: AppBarConfiguration


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }


    override fun onSupportNavigateUp(): Boolean {
        //旧
//        val navController = findNavController(R.id.nav_host_fragment_content_main)
//        return navController.navigateUp(appBarConfiguration)
//                || super.onSupportNavigateUp()
        //新
        return currentNavController?.value?.navigateUp() ?: false

    }


    override fun initView() {
        setSupportActionBar(binding.toolbar)
        //旧
//        val navController = findNavController(R.id.nav_host_fragment_content_main)
//        appBarConfiguration = AppBarConfiguration(navController.graph)
//        setupActionBarWithNavController(navController, appBarConfiguration)
//        binding.homeBottomNavi.setupWithNavController(navController)
//        binding.homeBottomNavi.setOnNavigationItemReselectedListener {
//            "do nothing".toast()
//        }
        //新
        setupBottomNavigationBar()



        LiveDataBus.get().with(BUS_HIDE_BOTTOM_TAB).observe(this, {
            if (it as Boolean) {
                val badge = binding.homeBottomNavi.getOrCreateBadge(R.id.homeFragment)
                badge.isVisible = true
                badge.number += 1
                val badge2 = binding.homeBottomNavi.getOrCreateBadge(R.id.circleFragment)
                badge2.isVisible = true
            }
        })

        lifecycleScope.launchWhenStarted {
//            Db.myDb.getData("name")?.storeValue?.toast()
        }

        //权限
        binding.fab.setOnClickListener {
            //旧
//            binding.homeBottomNavi.selectedItemId = R.id.carFragment
            //新
            binding.homeBottomNavi.selectedItemId = R.id.nav3
        }

    }


    override fun initData() {
        viewModel.getUserData()
        viewModel.user.observe(this, Observer {
            lifecycleScope.launch {
                Db.myDb.saveData("name", it[0].name)
            }
        })
    }

    private lateinit var currentNavController: LiveData<NavController>

    private fun setupBottomNavigationBar() {
        val navGraphIds = listOf(
            R.navigation.nav1,
            R.navigation.nav2,
            R.navigation.nav3,
            R.navigation.nav4,
            R.navigation.nav5
        )
        val controller = binding.homeBottomNavi.setupWithNavController(
            navGraphIds = navGraphIds,
            fragmentManager = supportFragmentManager,
            containerId = R.id.nav_host_fragment_content_main,
            intent = intent
        )
        controller.observe(this, { navController ->
            val appBarConfiguration = AppBarConfiguration(navGraphIds.toSet())
            NavigationUI.setupWithNavController(binding.toolbar, navController, appBarConfiguration)
            setSupportActionBar(binding.toolbar)
        })
        currentNavController = controller
        binding.homeBottomNavi.itemIconTintList = null
    }

}