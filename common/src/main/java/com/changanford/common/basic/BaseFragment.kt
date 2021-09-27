package com.changanford.common.basic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType

/**********************************************************************************
 * @Copyright (C), 2018-2020.
 * @FileName: com.changanford.common.basic.BaseFragment
 * @Author:　 　
 * @Version : V1.0
 * @Date: 2021/6/21 11:30
 * @Description: Fragment基类，传入layout的ViewBinding,ViewModel
 * *********************************************************************************
 */
abstract class BaseFragment<VB : ViewBinding, VM : ViewModel> : Fragment(), BaseInterface {
    lateinit var binding: VB
    lateinit var viewModel: VM
    var isNavigationViewInit = false
    lateinit var rootView: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val type: ParameterizedType = javaClass.genericSuperclass as ParameterizedType
        try {
            val inflate: Method = (type.actualTypeArguments[0] as Class<*>).getDeclaredMethod(
                "inflate",
                LayoutInflater::class.java,
                ViewGroup::class.java,
                Boolean::class.javaPrimitiveType
            )
            binding = inflate.invoke(null, inflater, container, false) as VB
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        }
        if (!isNavigationViewInit) {
            initViewModel()
            rootView = binding.root
        }
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        view.doOnPreDraw {
            startPostponedEnterTransition()
        }
        if (!isNavigationViewInit) {
            initView()
            initData()
            observe()
            isNavigationViewInit = true
        }
    }

    private fun initViewModel() {
        var vmClass =
            (this.javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[1] as Class<VM>
        viewModel = ViewModelProvider(this).get(vmClass)
    }

    fun navTo(id: Int) {
        findNavController().navigate(id)
    }

  open  fun observe(){}

    fun navFinishActivityTo(id: Int) {
        findNavController().navigate(id)
        requireActivity().finish()
    }

    fun <T:ViewModel> createViewModel(claaz:Class<T>) =
        ViewModelProvider.AndroidViewModelFactory.getInstance(BaseApplication.INSTANT).create(claaz)

}