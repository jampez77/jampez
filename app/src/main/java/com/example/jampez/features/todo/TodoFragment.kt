package com.example.jampez.features.todo

import android.content.DialogInterface.BUTTON_NEGATIVE
import android.content.DialogInterface.BUTTON_POSITIVE
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.jampez.MainActivity
import com.example.jampez.R
import com.example.jampez.databinding.FragmentTodoBinding
import com.example.jampez.features.base.BaseFragment
import com.example.jampez.utils.worker.SignOutUserWorker
import com.example.jampez.utils.constants.USERID
import com.example.jampez.utils.extensions.startLoadingAnimation
import com.example.jampez.utils.extensions.stopLoadingAnimation
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class TodoFragment : BaseFragment<FragmentTodoBinding>(FragmentTodoBinding::inflate) {

    private val todoViewModel: TodoViewModel by viewModel()
    private lateinit var navController: NavController
    private var userId: Long = -1
    private val args by navArgs<TodoFragmentArgs>()
    private val workManager: WorkManager by inject()
    private val snackbar: Snackbar by inject { parametersOf(requireActivity()) }

    private val userSignOutRequestBuilder: OneTimeWorkRequest.Builder by inject<OneTimeWorkRequest.Builder> {
        parametersOf(
            OneTimeWorkRequest.Builder(SignOutUserWorker::class.java)
            .setInputData(Data.Builder().putLong(USERID, userId).build())
            .setConstraints(Constraints.Builder().build())
        )
    }

    private val userSignOutRequest by lazy { userSignOutRequestBuilder.build() }

    private val todoAdapter = TodoAdapter()

    private val observers by lazy {

        workManager.getWorkInfoByIdLiveData(userSignOutRequest.id).observe(this@TodoFragment) { workInfo ->

            if (workInfo.state.isFinished) {
                if(workInfo.state == WorkInfo.State.SUCCEEDED) {
                    navController.navigate(R.id.action_todoFragment_to_loginFragment)
                } else {
                    snackbar.setText(getString(R.string.unable_to_sign_out))
                    snackbar.show()
                }
            }
        }

        todoViewModel.allTodosLiveData().observe(viewLifecycleOwner) { todos ->
            if (todos.isNullOrEmpty()) {
                binding.emptyListText.visibility = VISIBLE
                binding.todoItems.visibility = GONE
            } else {
                binding.emptyListText.visibility = GONE
                binding.todoItems.visibility = VISIBLE
                todoViewModel.allTodos = todos
                todoAdapter.submitList(todos)
            }
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            if (todoViewModel.isNetworkConnected()) {
                todoViewModel.fetchTodos(userId)
            } else {
                binding.swipeRefreshLayout.isRefreshing = false
                snackbar.setAnchorView(R.id.swipeRefreshLayout)
                snackbar.setText(getString(R.string.no_network_connection))
                snackbar.show()
            }

        }

        todoViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            snackbar.setText(errorMessage)
            snackbar.show()
        }

        todoViewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            binding.swipeRefreshLayout.isRefreshing = isLoading
            if (isLoading) {
                startLoadingAnimation()
            } else {
                stopLoadingAnimation()
            }
        }

        todoViewModel.signOutButtonState.observe(viewLifecycleOwner) { isPressed ->
            if (isPressed) {
                signOut()
            }
        }
    }

    private fun startLoadingAnimation() {
        binding.loadingList.startLoadingAnimation()
        binding.loadingList.animate().alpha(1f)
        binding.emptyListText.animate().alpha(0f)
    }

    private fun stopLoadingAnimation() {
        binding.emptyListText.animate().scaleX(1f).scaleY(1f)
        binding.emptyListText.animate().alpha(1f)
        binding.loadingList.animate().alpha(0f)
        binding.loadingList.stopLoadingAnimation()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mainActivity = (activity as MainActivity)
        navController = findNavController(mainActivity, R.id.nav_host_fragment)

        args.let {
            userId = it.userId
        }

        binding.apply {
            viewModel = todoViewModel
            adapter = todoAdapter
        }
        observers
        mainActivity.hideLoadingTransition()
        initClickListeners()
    }

    private fun initClickListeners() {
        todoAdapter.setItemClickListener {
            snackbar.setText(it.todo)
            snackbar.show()
        }

        todoAdapter.setOnItemCheckedChangedListener { isChecked, todo ->
            if (isChecked != todo.completed) {
                todo.completed = isChecked
                todoViewModel.updateTodo(todo)
            }
        }
    }

    private fun signOut() {
        val alertDialog = MaterialAlertDialogBuilder(requireContext(), R.style.WrapContentDialogWithUpDownAnimations)
            .create()
        alertDialog.setTitle(getString(R.string.sign_out_confirmation))
        alertDialog.setButton(BUTTON_POSITIVE, getString(R.string.yes)) { dialog, _ ->
            workManager.enqueue(userSignOutRequest)
            dialog.dismiss()
        }
        alertDialog.setButton(BUTTON_NEGATIVE, getString(R.string.no)) { dialog, _ ->
            dialog.dismiss()
        }
        alertDialog.show()
    }

}