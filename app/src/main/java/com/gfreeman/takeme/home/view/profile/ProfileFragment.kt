package com.gfreeman.takeme.home.view.profile

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.gfreeman.takeme.R
import com.gfreeman.takeme.home.presenter.profile.ProfilePresenter
import com.gfreeman.takeme.login.view.LoginActivity
import com.gfreeman.takeme.ui.components.ProfileSectionItem
import com.google.android.material.transition.platform.MaterialSharedAxis
import contract.BaseContract
import contract.ProfileContract
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment(), ProfileContract.ProfileView<BaseContract.IBaseView> {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var profileName: TextView
    private lateinit var profileEmail: TextView
    private lateinit var logOutBtn: ProfileSectionItem
    private lateinit var profilePresenter: ProfilePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ false)
        arguments?.let {
            param1 = it.getString(com.gfreeman.takeme.home.view.profile.ARG_PARAM1)
            param2 = it.getString(com.gfreeman.takeme.home.view.profile.ARG_PARAM2)
        }
        initPresenter()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        profileName = view.findViewById(R.id.profile_name)
        profileEmail = view.findViewById(R.id.profile_email)
        logOutBtn = view.findViewById(R.id.logout_profile_section)
        CoroutineScope(Dispatchers.IO).launch {
            loadProfileData()
        }
        logOutBtn.setOnClickListener{
            logOut()
            activity?.finish()
            openLoginScreen()
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileFragment.
         */
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            com.gfreeman.takeme.home.view.profile.ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(com.gfreeman.takeme.home.view.profile.ARG_PARAM1, param1)
                    putString(com.gfreeman.takeme.home.view.profile.ARG_PARAM2, param2)
                }
            }
    }

    private fun loadProfileData() {
        profileName.text = profilePresenter.getUserData()
        profileEmail.text = profilePresenter.getUserData()
    }

    override fun logOut() {
        context?.let { profilePresenter.logOut(it) }
    }

    override fun openLoginScreen() {
        val intent = Intent(context, LoginActivity::class.java)
        startActivity(intent)
    }

    override fun getParentView(): BaseContract.IBaseView? {
        return activity as BaseContract.IBaseView
    }

    private fun initPresenter(){
        profilePresenter = ProfilePresenter()
        profilePresenter.attachView(this)
    }
}