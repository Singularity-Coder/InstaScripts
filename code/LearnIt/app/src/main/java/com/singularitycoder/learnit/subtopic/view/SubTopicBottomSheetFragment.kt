package com.singularitycoder.learnit.subtopic.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.singularitycoder.learnit.R
import com.singularitycoder.learnit.databinding.FragmentSubTopicBottomSheetBinding
import com.singularitycoder.learnit.helpers.AndroidVersions
import com.singularitycoder.learnit.helpers.collectLatestLifecycleFlow
import com.singularitycoder.learnit.helpers.constants.FragmentResultBundleKey
import com.singularitycoder.learnit.helpers.constants.FragmentResultKey
import com.singularitycoder.learnit.helpers.constants.FragmentsTag
import com.singularitycoder.learnit.helpers.constants.globalLayoutAnimation
import com.singularitycoder.learnit.helpers.constants.globalSlideToBottomAnimation
import com.singularitycoder.learnit.helpers.enableSoftInput
import com.singularitycoder.learnit.helpers.hideKeyboard
import com.singularitycoder.learnit.helpers.layoutAnimationController
import com.singularitycoder.learnit.helpers.onImeClick
import com.singularitycoder.learnit.helpers.onSafeClick
import com.singularitycoder.learnit.helpers.runLayoutAnimation
import com.singularitycoder.learnit.helpers.setTransparentBackground
import com.singularitycoder.learnit.helpers.showKeyboard
import com.singularitycoder.learnit.helpers.showPopupMenuWithIcons
import com.singularitycoder.learnit.helpers.showScreen
import com.singularitycoder.learnit.subject.model.Subject
import com.singularitycoder.learnit.subject.view.MainActivity
import com.singularitycoder.learnit.subtopic.model.SubTopic
import com.singularitycoder.learnit.subtopic.viewmodel.SubTopicViewModel
import com.singularitycoder.learnit.topic.model.Topic
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class SubTopicBottomSheetFragment : BottomSheetDialogFragment() {

    companion object {
        private const val ARG_TOPIC = "ARG_TOPIC"
        private const val ARG_SUBJECT = "ARG_SUBJECT"

        @JvmStatic
        fun newInstance(
            topic: Topic?,
            subject: Subject?
        ) = SubTopicBottomSheetFragment().apply {
            arguments = Bundle().apply {
                putParcelable(ARG_TOPIC, topic)
                putParcelable(ARG_SUBJECT, subject)
            }
        }
    }

    private lateinit var binding: FragmentSubTopicBottomSheetBinding

    private var subTopicList = listOf<SubTopic?>()

    private val subTopicViewModel by viewModels<SubTopicViewModel>()

    private val subTopicsAdapter: SubTopicsAdapter by lazy { SubTopicsAdapter() }

    private var isNewInstance: Boolean = true

    private var topic: Topic? = null
    private var subject: Subject? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (AndroidVersions.isTiramisu()) {
            topic = arguments?.getParcelable(ARG_TOPIC, Topic::class.java)
            subject = arguments?.getParcelable(ARG_SUBJECT, Subject::class.java)
        } else {
            topic = arguments?.getParcelable(ARG_TOPIC)
            subject = arguments?.getParcelable(ARG_SUBJECT)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSubTopicBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.setupUI()
        binding.setupUserActionListeners()
        observeForData()
    }

    private fun FragmentSubTopicBottomSheetBinding.setupUI() {
        enableSoftInput()
        setTransparentBackground()
        tvHeader.text = "Recall ${topic?.title}"
        rvSubTopics.apply {
            layoutAnimation = rvSubTopics.context.layoutAnimationController(globalLayoutAnimation)
            layoutManager = LinearLayoutManager(context)
            adapter = subTopicsAdapter
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun FragmentSubTopicBottomSheetBinding.setupUserActionListeners() {
        subTopicsAdapter.setOnItemClickListener { subTopic, position ->
        }

        subTopicsAdapter.setOnItemLongClickListener { subTopic, view, position ->
        }

        subTopicsAdapter.setOnApproveUpdateClickListener { subTopic, position ->
            subTopicViewModel.updateSubTopic(subTopic)
        }

        ivMore.onSafeClick {
            val optionsList = listOf(
                Pair("Reset", R.drawable.round_settings_backup_restore_24),
                Pair("Edit", R.drawable.outline_edit_24)
            )
            requireContext().showPopupMenuWithIcons(
                view = it.first,
                menuList = optionsList
            ) { it: MenuItem? ->
                when (it?.title?.toString()?.trim()) {
                    optionsList[0].first -> {
                        subTopicViewModel.updateAllSubTopics(
                            subTopicList.map { it?.copy(isCorrectRecall = false) }.filterNotNull()
                        )
                    }

                    optionsList[1].first -> {
                        this@SubTopicBottomSheetFragment.dismiss()
                        (requireActivity() as MainActivity).showScreen(
                            fragment = AddSubTopicFragment.newInstance(topic, subject),
                            tag = FragmentsTag.ADD_SUB_TOPIC,
                            isAdd = true,
                            enterAnim = R.anim.slide_to_top,
                            exitAnim = R.anim.slide_to_bottom,
                            popEnterAnim = R.anim.slide_to_top,
                            popExitAnim = R.anim.slide_to_bottom,
                        )
                    }
                }
            }
        }

        ivSearch.onSafeClick {
            clSearch.layoutAnimation = clSearch.context.layoutAnimationController(globalSlideToBottomAnimation)
            etSearch.setText("")
            clSearch.isVisible = clSearch.isVisible.not()
            if (clSearch.isVisible) {
                etSearch.showKeyboard()
            } else {
                etSearch.hideKeyboard()
            }
        }

        ibClearSearch.onSafeClick {
            etSearch.setText("")
        }

        etSearch.doAfterTextChanged { query: Editable? ->
            ibClearSearch.isVisible = query.isNullOrBlank().not()
            if (query.isNullOrBlank()) {
                subTopicsAdapter.subTopicList = subTopicList
                subTopicsAdapter.notifyDataSetChanged()
                return@doAfterTextChanged
            }
            subTopicsAdapter.subTopicList = subTopicList.filter {
                it?.title?.contains(other = query, ignoreCase = true) == true
            }
            subTopicsAdapter.notifyDataSetChanged()
        }

        etSearch.onImeClick {
            etSearch.hideKeyboard()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeForData() {
        (activity as? MainActivity)?.collectLatestLifecycleFlow(
            flow = subTopicViewModel.getAllTopicByTopicIdItemsFlow(topic?.id)
        ) { list: List<SubTopic> ->
            subTopicList = list
            subTopicsAdapter.subTopicList = subTopicList
            subTopicsAdapter.notifyDataSetChanged()
            if (isNewInstance) {
                binding.rvSubTopics.runLayoutAnimation(globalLayoutAnimation)
                isNewInstance = false
            }
            binding.tvCount.text =
                "${list.size} Sub-Topics   |   ${list.filter { it.isCorrectRecall }.size} Recalled   |   ${topic?.revisionCount} Revisions"
            if (subTopicList.all { it?.isCorrectRecall == true }) {
                subTopicViewModel.updateAllSubTopics(
                    subTopicList.map { it?.copy(isCorrectRecall = false) }.filterNotNull()
                )
                withContext(Dispatchers.Main) {
                    try {
                        parentFragmentManager.setFragmentResult(
                            /* requestKey = */ FragmentResultKey.SHOW_KONFETTI,
                            /* result = */ bundleOf(FragmentResultBundleKey.TOPIC to topic)
                        )
                    } catch (_: Exception) {
                    }
                    dismiss()
                }
            }
        }
    }
}